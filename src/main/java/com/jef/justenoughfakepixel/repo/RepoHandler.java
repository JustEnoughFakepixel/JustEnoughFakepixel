// Credit: NotEnoughFakepixel (https://github.com/davidbelesp/NotEnoughFakepixel)

package com.jef.justenoughfakepixel.repo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RepoHandler {

    public static final Gson GSON = new GsonBuilder().create();
    private static final int TIMEOUT_MS = 5000;
    private static final String USER_AGENT = "JEF/1.0 (Minecraft 1.8.9)";
    private static final ExecutorService IO = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(32), r -> {
        Thread t = new Thread(r, "JefRepo-IO");
        t.setDaemon(true);
        return t;
    }, new ThreadPoolExecutor.DiscardOldestPolicy());

    private static final ConcurrentMap<String, SourceState> SOURCES = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, ConcurrentMap<Type, ParsedCache>> PARSED = new ConcurrentHashMap<>();

    private RepoHandler() {
    }

    private static ParsedCache cacheFor(String key, Type type) {
        return PARSED.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).computeIfAbsent(type, t -> new ParsedCache());
    }

    public static void register(String key, String url) {
        SOURCES.put(key, new SourceState(url));
    }

    public static void warmupAll() {
        for (String key : SOURCES.keySet()) {
            System.out.println("[JEF] Fetching: " + SOURCES.get(key).url);
            fetchAsync(key);
        }
    }

    public static void refresh(String key) {
        SourceState s = SOURCES.get(key);
        if (s != null) {
            s.lastFetch = 0;
            fetchAsync(key);
        }
    }

    public static String getJson(String key) {
        SourceState s = SOURCES.get(key);
        return s == null ? null : s.json.get();
    }

    public static <T> T get(String key, Class<T> type, T fallback) {
        return get(key, (Type) type, fallback);
    }

    public static <T> T get(String key, Type type, T fallback) {
        final String json = getJson(key);
        if (json == null) return fallback;

        final ParsedCache pc = cacheFor(key, type);

        if (!Objects.equals(json, pc.lastJsonRef)) {
            try {
                T parsed = GSON.fromJson(json, type);
                if (parsed != null) {
                    pc.parsed = parsed;
                    pc.lastJsonRef = json;
                }
            } catch (Exception e) {
                System.err.println("[JEF] JSON parse error (" + key + "): " + e.getMessage());
            }
        }

        @SuppressWarnings("unchecked") T result = (T) pc.parsed;
        return result != null ? result : fallback;
    }

    public static void shutdown() {
        IO.shutdownNow();
    }

    private static void fetchAsync(String key) {
        SourceState s = SOURCES.get(key);
        if (s == null || !s.loading.compareAndSet(false, true)) return;
        IO.execute(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(s.url).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(TIMEOUT_MS);
                conn.setReadTimeout(TIMEOUT_MS);
                conn.setRequestProperty("User-Agent", USER_AGENT);
                if (s.etag != null) conn.setRequestProperty("If-None-Match", s.etag);

                int code = conn.getResponseCode();
                System.out.println("[JEF] " + key + " -> HTTP " + code);
                if (code == HttpURLConnection.HTTP_NOT_MODIFIED) return;
                if (code >= 200 && code < 300) {
                    String etag = conn.getHeaderField("ETag");
                    if (etag != null) s.etag = etag;
                    s.json.set(readAll(conn));
                    s.lastFetch = System.currentTimeMillis();
                }
            } catch (Exception e) {
                System.err.println("[JEF] Repo fetch failed (" + key + "): " + e.getMessage());
            } finally {
                s.loading.set(false);
            }
        });
    }

    private static String readAll(HttpURLConnection conn) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }

    private static final class SourceState {
        final String url;
        final AtomicReference<String> json = new AtomicReference<>();
        final AtomicBoolean loading = new AtomicBoolean();
        volatile String etag;
        volatile long lastFetch;

        SourceState(String url) {
            this.url = url;
        }
    }

    // Parsed object cache keyed by (repoKey, Type)
    private static final class ParsedCache {
        volatile Object parsed;
        volatile String lastJsonRef;
    }
}