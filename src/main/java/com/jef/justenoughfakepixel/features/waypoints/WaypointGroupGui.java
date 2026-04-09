package com.jef.justenoughfakepixel.features.waypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jef.justenoughfakepixel.core.config.gui.GuiElement;
import com.jef.justenoughfakepixel.core.config.utils.TextRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Type;
import java.util.*;

public class WaypointGroupGui extends GuiElement {

    private static final int MAX_W = 410;
    private static final int MAX_H = 330;
    private static final int MARGIN = 8;
    private static final int PAD = 8;
    private static final int TITLE_H = 22;
    private static final int ROW_H = 22;
    private static final int ROW_PAD = 3;
    private static final int WP_H = 17;
    private static final int WP_PAD = 1;
    private static final int SF_H = 14;
    private static final int BTN_W = 40;
    private static final int BTN_H = 13;
    private static final int SBTN_W = 14;
    private static final int SBTN_H = 11;
    private static final int INDENT = 18;
    private final Set<String> expandedGroups = new HashSet<>();
    private int scrollOffset = 0;
    private GuiTextField searchField = null;
    private GuiTextField importField = null;
    private GuiTextField createField = null;
    private boolean importOpen = false;
    private boolean createOpen = false;
    private int pw, ph, px, py;

    static String exportSoopy(WaypointGroup g) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WaypointPoint wp : g.waypoints) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("x", wp.x);
            m.put("y", wp.y);
            m.put("z", wp.z);
            m.put("r", 0);
            m.put("g", 1);
            m.put("b", 0);
            Map<String, Object> opts = new LinkedHashMap<>();
            opts.put("name", wp.name != null ? wp.name : "");
            m.put("options", opts);
            list.add(m);
        }
        return new GsonBuilder().create().toJson(list);
    }

    private static List<WaypointPoint> parseSoopy(String json) {
        try {
            if (json.startsWith("[")) {
                Type type = new TypeToken<List<Map<String, Object>>>() {
                }.getType();
                List<Map<String, Object>> raw = new Gson().fromJson(json, type);
                List<WaypointPoint> wps = new ArrayList<>();
                for (int i = 0; i < raw.size(); i++) {
                    Map<String, Object> m = raw.get(i);
                    double x = toD(m.get("x")), y = toD(m.get("y")), z = toD(m.get("z"));
                    String name = String.valueOf(i + 1);
                    if (m.containsKey("options")) {
                        @SuppressWarnings("unchecked") Map<String, Object> opts = (Map<String, Object>) m.get("options");
                        if (opts != null && opts.containsKey("name")) name = String.valueOf(opts.get("name"));
                    }
                    wps.add(new WaypointPoint(x, y, z, name));
                }
                wps.sort((a, b) -> {
                    try {
                        return Integer.compare(Integer.parseInt(a.name), Integer.parseInt(b.name));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                });
                return wps;
            }
            if (json.matches("(?s).*\\d.*")) {
                List<WaypointPoint> wps = new ArrayList<>();
                String[] rows = json.split("[\\r\\n]+");
                for (int i = 0; i < rows.length; i++) {
                    String[] parts = rows[i].trim().split("\\s+");
                    if (parts.length >= 3)
                        wps.add(new WaypointPoint(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), String.valueOf(i + 1)));
                }
                return wps.isEmpty() ? null : wps;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static double toD(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        return Double.parseDouble(String.valueOf(o));
    }

    private void updatePanel(ScaledResolution sr) {
        pw = Math.min(MAX_W, sr.getScaledWidth() - MARGIN * 2);
        ph = Math.min(MAX_H, sr.getScaledHeight() - MARGIN * 2);
        px = (sr.getScaledWidth() - pw) / 2;
        py = (sr.getScaledHeight() - ph) / 2;
    }

    private int computeTotalH(List<WaypointGroup> groups) {
        int h = 0;
        for (WaypointGroup g : groups) {
            h += ROW_H + ROW_PAD;
            if (expandedGroups.contains(g.name)) h += (g.waypoints.size() + 1) * (WP_H + WP_PAD) + ROW_PAD;
        }
        return h;
    }

    private List<RowItem> buildRows(int listTopY, List<WaypointGroup> groups) {
        List<RowItem> rows = new ArrayList<>();
        int rowY = listTopY - scrollOffset;
        for (WaypointGroup g : groups) {
            GroupRow gr = new GroupRow();
            gr.g = g;
            gr.y = rowY;
            gr.expanded = expandedGroups.contains(g.name);
            rows.add(gr);
            rowY += ROW_H + ROW_PAD;
            if (gr.expanded) {
                for (int i = 0; i < g.waypoints.size(); i++) {
                    WpRow wr = new WpRow();
                    wr.g = g;
                    wr.idx = i;
                    wr.y = rowY;
                    rows.add(wr);
                    rowY += WP_H + WP_PAD;
                }
                AddRow ar = new AddRow();
                ar.g = g;
                ar.y = rowY;
                rows.add(ar);
                rowY += WP_H + ROW_PAD;
            }
        }
        return rows;
    }

    // How much vertical space the header elements use (matches render curY tracking)
    private int headerHeight() {
        int h = PAD + TITLE_H + ROW_PAD + ROW_H + ROW_PAD + SF_H + ROW_PAD;
        if (importOpen) h += SF_H + ROW_PAD;
        if (createOpen) h += SF_H + ROW_PAD;
        h += 4; // separator gap
        return h;
    }

    @Override
    public void render() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;
        updatePanel(sr);

        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0xaa080810);
        drawPanel(px, py, pw, ph);

        int curY = py + PAD;

        Gui.drawRect(px + 1, curY, px + pw - 1, curY + TITLE_H, 0xff1a1a2a);
        Gui.drawRect(px + 1, curY + TITLE_H - 1, px + pw - 1, curY + TITLE_H, 0xff3a3a90);
        fr.drawStringWithShadow(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "Waypoint Groups", px + PAD + 2, curY + 7, -1);
        String escHint = EnumChatFormatting.DARK_GRAY + "ESC to close";
        fr.drawStringWithShadow(escHint, px + pw - fr.getStringWidth(escHint) - PAD, curY + 7, -1);
        curY += TITLE_H + ROW_PAD;

        WaypointState state = WaypointState.getInstance();
        WaypointStorage storage = WaypointStorage.getInstance();

        Gui.drawRect(px + 5, curY, px + pw - 5, curY + ROW_H - 1, 0xff191926);
        if (state.hasGroup()) {
            String dot = EnumChatFormatting.GREEN + "● ";
            String label = EnumChatFormatting.WHITE + state.loadedGroup.name + EnumChatFormatting.GRAY + " (" + state.size() + ")" + "  " + EnumChatFormatting.AQUA + (state.currentIndex + 1) + EnumChatFormatting.GRAY + "/" + state.size();
            fr.drawStringWithShadow(dot + label, px + PAD + 2, curY + 6, -1);
            int bx = px + pw - BTN_W - PAD;
            drawBtn(bx, curY + 4, BTN_W, BTN_H, EnumChatFormatting.RED + "Unload", fr, isHovered(bx, curY + 4, BTN_W, BTN_H));
        } else {
            fr.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "● " + EnumChatFormatting.GRAY + "No group loaded", px + PAD + 2, curY + 6, -1);
        }
        curY += ROW_H + ROW_PAD;

        int sfX = px + PAD;
        int sfW = pw - PAD * 2 - BTN_W * 2 - 10;
        ensureSearchField(sfX, curY, sfW, SF_H, fr);
        searchField.xPosition = sfX;
        searchField.yPosition = curY;
        searchField.width = sfW;
        searchField.height = SF_H;
        drawInputBg(sfX - 2, curY - 1, sfW + 4, SF_H + 2);
        searchField.drawTextBox();
        if (searchField.getText().isEmpty() && !searchField.isFocused())
            fr.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "search...", sfX + 2, curY + 3, -1);

        int impTogX = px + pw - BTN_W * 2 - 8;
        int newTogX = px + pw - BTN_W - PAD;
        drawBtn(impTogX, curY, BTN_W, SF_H, importOpen ? EnumChatFormatting.YELLOW + "Cancel" : EnumChatFormatting.AQUA + "Import", fr, isHovered(impTogX, curY, BTN_W, SF_H));
        drawBtn(newTogX, curY, BTN_W, SF_H, createOpen ? EnumChatFormatting.YELLOW + "Cancel" : EnumChatFormatting.GREEN + "New", fr, isHovered(newTogX, curY, BTN_W, SF_H));
        curY += SF_H + ROW_PAD;

        if (importOpen) {
            int ifX = px + PAD, ifW = pw - PAD * 2 - BTN_W - 6;
            importField = ensureGenericField(importField, 2, ifX, curY, ifW, SF_H, fr);
            importField.xPosition = ifX;
            importField.yPosition = curY;
            importField.width = ifW;
            importField.height = SF_H;
            drawInputBg(ifX - 2, curY - 1, ifW + 4, SF_H + 2);
            importField.drawTextBox();
            if (importField.getText().isEmpty())
                fr.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "group name for import...", ifX + 2, curY + 3, -1);
            int goBtnX = px + pw - BTN_W - PAD;
            drawBtn(goBtnX, curY, BTN_W, SF_H, EnumChatFormatting.GREEN + "Go", fr, isHovered(goBtnX, curY, BTN_W, SF_H));
            curY += SF_H + ROW_PAD;
        }

        if (createOpen) {
            int cfX = px + PAD, cfW = pw - PAD * 2 - BTN_W - 6;
            createField = ensureGenericField(createField, 3, cfX, curY, cfW, SF_H, fr);
            createField.xPosition = cfX;
            createField.yPosition = curY;
            createField.width = cfW;
            createField.height = SF_H;
            drawInputBg(cfX - 2, curY - 1, cfW + 4, SF_H + 2);
            createField.drawTextBox();
            if (createField.getText().isEmpty())
                fr.drawStringWithShadow(EnumChatFormatting.DARK_GRAY + "new group name...", cfX + 2, curY + 3, -1);
            int createBtnX = px + pw - BTN_W - PAD;
            drawBtn(createBtnX, curY, BTN_W, SF_H, EnumChatFormatting.GREEN + "Create", fr, isHovered(createBtnX, curY, BTN_W, SF_H));
            curY += SF_H + ROW_PAD;
        }

        Gui.drawRect(px + 6, curY, px + pw - 6, curY + 1, 0xff2a2a40);
        curY += 4;

        int listTopY = curY;
        int listBottomY = py + ph - PAD;
        int visibleH = Math.max(0, listBottomY - listTopY);

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        List<WaypointGroup> groups = filteredGroups(storage, query);
        int totalH = computeTotalH(groups);
        int maxScroll = Math.max(0, totalH - visibleH);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        List<RowItem> rows = buildRows(listTopY, groups);

        int scale = sr.getScaleFactor();
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(px * scale, mc.displayHeight - listBottomY * scale, pw * scale, visibleH * scale);

        for (RowItem item : rows) {
            if (item.y + item.h() <= listTopY || item.y >= listBottomY) continue;
            if (item instanceof GroupRow) renderGroupRow(px, pw, (GroupRow) item, state, fr);
            else if (item instanceof WpRow) renderWpRow(px, pw, (WpRow) item, fr);
            else if (item instanceof AddRow) renderAddRow(px, pw, (AddRow) item, fr);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();

        if (groups.isEmpty()) {
            String msg = query.isEmpty() ? EnumChatFormatting.GRAY + "No groups  –  click " + EnumChatFormatting.GREEN + "New" + EnumChatFormatting.GRAY + " or /w create <n>" : EnumChatFormatting.GRAY + "No groups match \"" + query + "\"";
            fr.drawStringWithShadow(msg, px + PAD + 8, listTopY + 18, -1);
        }

        if (maxScroll > 0) {
            int sbX = px + pw - 6;
            int barH = Math.max(16, (int) (visibleH * (float) visibleH / totalH));
            int barY = listTopY + (int) ((visibleH - barH) * ((float) scrollOffset / maxScroll));
            Gui.drawRect(sbX, listTopY, sbX + 4, listBottomY, 0xff141420);
            Gui.drawRect(sbX, barY, sbX + 4, barY + barH, 0xff4a4aaa);
            Gui.drawRect(sbX, barY, sbX + 4, barY + 1, 0xff7070cc);
        }
    }

    private void renderGroupRow(int panelX, int panelW, GroupRow gr, WaypointState state, FontRenderer fr) {
        boolean isLoaded = state.loadedGroup != null && state.loadedGroup.name.equalsIgnoreCase(gr.g.name);
        Gui.drawRect(panelX + 5, gr.y, panelX + panelW - 5, gr.y + ROW_H - 1, isLoaded ? 0xff152215 : 0xff1c1c2a);
        if (isLoaded) Gui.drawRect(panelX + 5, gr.y, panelX + 7, gr.y + ROW_H - 1, 0xff44aa44);

        String arrow = gr.expanded ? EnumChatFormatting.YELLOW + "v" : EnumChatFormatting.DARK_GRAY + ">";
        fr.drawStringWithShadow(arrow, panelX + PAD + 2, gr.y + 7, -1);

        String nameStr = (isLoaded ? EnumChatFormatting.GREEN : EnumChatFormatting.YELLOW) + gr.g.name + EnumChatFormatting.GRAY + " (" + gr.g.waypoints.size() + ")";
        int maxW = panelW - PAD * 2 - INDENT - BTN_W * 3 - 18;
        TextRenderUtils.drawStringScaledMaxWidth(nameStr, fr, panelX + PAD + INDENT, gr.y + 7, false, maxW, -1);

        int delX = panelX + panelW - BTN_W - PAD;
        int expX = delX - BTN_W - 4;
        int loadX = expX - BTN_W - 4;
        drawBtn(loadX, gr.y + 4, BTN_W, BTN_H, isLoaded ? EnumChatFormatting.AQUA + "Reload" : EnumChatFormatting.GREEN + "Load", fr, isHovered(loadX, gr.y + 4, BTN_W, BTN_H));
        drawBtn(expX, gr.y + 4, BTN_W, BTN_H, EnumChatFormatting.YELLOW + "Export", fr, isHovered(expX, gr.y + 4, BTN_W, BTN_H));
        drawBtn(delX, gr.y + 4, BTN_W, BTN_H, EnumChatFormatting.RED + "Delete", fr, isHovered(delX, gr.y + 4, BTN_W, BTN_H));
    }

    private void renderWpRow(int panelX, int panelW, WpRow wr, FontRenderer fr) {
        WaypointPoint wp = wr.g.waypoints.get(wr.idx);
        Gui.drawRect(panelX + 5 + INDENT, wr.y, panelX + panelW - 5, wr.y + WP_H, wr.idx % 2 == 0 ? 0xff141422 : 0xff111120);
        Gui.drawRect(panelX + 5 + INDENT, wr.y, panelX + 6 + INDENT, wr.y + WP_H, 0xff282860);

        String idxStr = EnumChatFormatting.DARK_GRAY.toString() + (wr.idx + 1) + ".";
        fr.drawStringWithShadow(idxStr, panelX + PAD + INDENT + 2, wr.y + 4, -1);
        int indexW = fr.getStringWidth("99.") + 4;

        int delX = panelX + panelW - PAD - SBTN_W - 4;
        int downX = delX - SBTN_W - 2;
        int upX = downX - SBTN_W - 2;
        drawSmallBtn(delX, wr.y + 3, SBTN_W, SBTN_H, EnumChatFormatting.RED + "x", fr, isHovered(delX, wr.y + 3, SBTN_W, SBTN_H));
        drawSmallBtn(downX, wr.y + 3, SBTN_W, SBTN_H, EnumChatFormatting.YELLOW + "v", fr, isHovered(downX, wr.y + 3, SBTN_W, SBTN_H));
        drawSmallBtn(upX, wr.y + 3, SBTN_W, SBTN_H, EnumChatFormatting.GREEN + "^", fr, isHovered(upX, wr.y + 3, SBTN_W, SBTN_H));

        String coords = EnumChatFormatting.DARK_GRAY.toString() + (int) wp.x + ", " + (int) wp.y + ", " + (int) wp.z;
        int coordsW = fr.getStringWidth(coords);
        int coordsX = upX - coordsW - 6;
        fr.drawStringWithShadow(coords, coordsX, wr.y + 4, -1);

        String name = EnumChatFormatting.WHITE + (wp.name != null ? wp.name : "");
        int nameAreaW = coordsX - (panelX + PAD + INDENT + indexW) - 4;
        if (nameAreaW > 0)
            TextRenderUtils.drawStringScaledMaxWidth(name, fr, panelX + PAD + INDENT + indexW, wr.y + 4, false, nameAreaW, -1);
    }

    private void renderAddRow(int panelX, int panelW, AddRow ar, FontRenderer fr) {
        int rx = panelX + 5 + INDENT, rw = panelW - 10 - INDENT;
        boolean hov = isHovered(rx, ar.y, rw, WP_H);
        Gui.drawRect(rx, ar.y, rx + rw, ar.y + WP_H, hov ? 0xff1a2e1a : 0xff10141c);
        Gui.drawRect(rx, ar.y, rx + rw, ar.y + 1, hov ? 0xff3a6a3a : 0xff1e1e30);
        fr.drawStringWithShadow(EnumChatFormatting.GREEN + "+ " + EnumChatFormatting.GRAY + "Add waypoint at current position", rx + PAD, ar.y + 4, -1);
    }

    @Override
    public boolean mouseInput(int mouseX, int mouseY) {
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            scrollOffset = Math.max(0, scrollOffset - (dWheel > 0 ? 20 : -20));
            return false;
        }
        if (!Mouse.getEventButtonState() || Mouse.getEventButton() != 0) return false;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        updatePanel(sr);

        WaypointState state = WaypointState.getInstance();
        WaypointStorage storage = WaypointStorage.getInstance();

        int curY = py + PAD + TITLE_H + ROW_PAD;

        if (state.hasGroup()) {
            int bx = px + pw - BTN_W - PAD;
            if (inBounds(mouseX, mouseY, bx, curY + 4, BTN_W, BTN_H)) {
                state.unload();
                return true;
            }
        }
        curY += ROW_H + ROW_PAD;

        if (searchField != null) searchField.mouseClicked(mouseX, mouseY, 0);

        int impTogX = px + pw - BTN_W * 2 - 8;
        int newTogX = px + pw - BTN_W - PAD;
        if (inBounds(mouseX, mouseY, impTogX, curY, BTN_W, SF_H)) {
            importOpen = !importOpen;
            if (!importOpen) importField = null;
            return true;
        }
        if (inBounds(mouseX, mouseY, newTogX, curY, BTN_W, SF_H)) {
            createOpen = !createOpen;
            if (!createOpen) createField = null;
            return true;
        }
        curY += SF_H + ROW_PAD;

        if (importOpen) {
            if (importField != null) importField.mouseClicked(mouseX, mouseY, 0);
            if (inBounds(mouseX, mouseY, px + pw - BTN_W - PAD, curY, BTN_W, SF_H)) {
                doImport(storage);
                return true;
            }
            curY += SF_H + ROW_PAD;
        }
        if (createOpen) {
            if (createField != null) createField.mouseClicked(mouseX, mouseY, 0);
            if (inBounds(mouseX, mouseY, px + pw - BTN_W - PAD, curY, BTN_W, SF_H)) {
                doCreate(storage);
                return true;
            }
            curY += SF_H + ROW_PAD;
        }

        curY += 4;
        int listTopY = curY;
        int listBottomY = py + ph - PAD;

        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        List<WaypointGroup> groups = filteredGroups(storage, query);
        List<RowItem> rows = buildRows(listTopY, groups);

        for (RowItem item : rows) {
            if (item.y + item.h() <= listTopY || item.y >= listBottomY) continue;

            if (item instanceof GroupRow) {
                GroupRow gr = (GroupRow) item;

                // expand toggle (left arrow area)
                if (inBounds(mouseX, mouseY, px + PAD, gr.y, INDENT + 8, ROW_H)) {
                    if (gr.expanded) expandedGroups.remove(gr.g.name);
                    else expandedGroups.add(gr.g.name);
                    return true;
                }

                int delX = px + pw - BTN_W - PAD;
                int expX = delX - BTN_W - 4;
                int loadX = expX - BTN_W - 4;
                if (inBounds(mouseX, mouseY, loadX, gr.y + 4, BTN_W, BTN_H)) {
                    state.load(gr.g);
                    return true;
                }
                if (inBounds(mouseX, mouseY, expX, gr.y + 4, BTN_W, BTN_H)) {
                    GuiScreen.setClipboardString(exportSoopy(gr.g));
                    return true;
                }
                if (inBounds(mouseX, mouseY, delX, gr.y + 4, BTN_W, BTN_H)) {
                    if (state.loadedGroup != null && state.loadedGroup.name.equalsIgnoreCase(gr.g.name)) state.unload();
                    storage.removeGroup(gr.g.name);
                    storage.saveIfDirty();
                    expandedGroups.remove(gr.g.name);
                    return true;
                }

            } else if (item instanceof WpRow) {
                WpRow wr = (WpRow) item;
                int delX = px + pw - PAD - SBTN_W - 4;
                int downX = delX - SBTN_W - 2;
                int upX = downX - SBTN_W - 2;

                if (inBounds(mouseX, mouseY, delX, wr.y + 3, SBTN_W, SBTN_H)) {
                    wr.g.waypoints.remove(wr.idx);
                    storage.markDirty();
                    storage.saveIfDirty();
                    return true;
                }
                if (inBounds(mouseX, mouseY, downX, wr.y + 3, SBTN_W, SBTN_H)) {
                    if (wr.idx < wr.g.waypoints.size() - 1) {
                        WaypointPoint t = wr.g.waypoints.get(wr.idx);
                        wr.g.waypoints.set(wr.idx, wr.g.waypoints.get(wr.idx + 1));
                        wr.g.waypoints.set(wr.idx + 1, t);
                        storage.markDirty();
                        storage.saveIfDirty();
                    }
                    return true;
                }
                if (inBounds(mouseX, mouseY, upX, wr.y + 3, SBTN_W, SBTN_H)) {
                    if (wr.idx > 0) {
                        WaypointPoint t = wr.g.waypoints.get(wr.idx);
                        wr.g.waypoints.set(wr.idx, wr.g.waypoints.get(wr.idx - 1));
                        wr.g.waypoints.set(wr.idx - 1, t);
                        storage.markDirty();
                        storage.saveIfDirty();
                    }
                    return true;
                }

            } else if (item instanceof AddRow) {
                AddRow ar = (AddRow) item;
                int rx = px + 5 + INDENT, rw = pw - 10 - INDENT;
                if (inBounds(mouseX, mouseY, rx, ar.y, rw, WP_H)) {
                    double bx = Math.floor(mc.thePlayer.posX);
                    double by = Math.floor(mc.thePlayer.posY) - 1;
                    double bz = Math.floor(mc.thePlayer.posZ);
                    ar.g.waypoints.add(new WaypointPoint(bx, by, bz, String.valueOf(ar.g.waypoints.size() + 1)));
                    storage.markDirty();
                    storage.saveIfDirty();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyboardInput() {
        if (!Keyboard.getEventKeyState()) return false;
        int key = Keyboard.getEventKey();
        char c = Keyboard.getEventCharacter();

        boolean sf = searchField != null && searchField.isFocused();
        boolean imf = importField != null && importField.isFocused();
        boolean crf = createField != null && createField.isFocused();

        if (!sf && !imf && !crf) {
            if (key == Keyboard.KEY_DOWN) {
                scrollOffset += 12;
                return true;
            }
            if (key == Keyboard.KEY_UP) {
                scrollOffset = Math.max(0, scrollOffset - 12);
                return true;
            }
        }

        if (imf && key == Keyboard.KEY_RETURN) {
            doImport(WaypointStorage.getInstance());
            return true;
        }
        if (crf && key == Keyboard.KEY_RETURN) {
            doCreate(WaypointStorage.getInstance());
            return true;
        }
        if (sf) {
            searchField.textboxKeyTyped(c, key);
            return true;
        }
        if (imf) {
            importField.textboxKeyTyped(c, key);
            return true;
        }
        if (crf) {
            createField.textboxKeyTyped(c, key);
            return true;
        }

        return false;
    }

    private void doImport(WaypointStorage storage) {
        if (importField == null) return;
        String name = importField.getText().trim().toLowerCase();
        if (name.isEmpty()) return;
        String clip = GuiScreen.getClipboardString();
        if (clip == null || clip.trim().isEmpty()) return;
        List<WaypointPoint> wps = parseSoopy(clip.trim());
        if (wps == null || wps.isEmpty()) return;
        WaypointGroup g = storage.getGroup(name);
        if (g == null) g = new WaypointGroup(name);
        g.waypoints = wps;
        storage.putGroup(g);
        storage.saveIfDirty();
        importOpen = false;
        importField = null;
    }

    private void doCreate(WaypointStorage storage) {
        if (createField == null) return;
        String name = createField.getText().trim().toLowerCase();
        if (name.isEmpty() || storage.getGroup(name) != null) return;
        storage.putGroup(new WaypointGroup(name));
        storage.saveIfDirty();
        createOpen = false;
        createField = null;
    }

    private void drawPanel(int x, int y, int w, int h) {
        Gui.drawRect(x, y, x + w, y + h, 0xff0e0e18);
        Gui.drawRect(x, y, x + w, y + 1, 0xff3a3a80);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0xff202030);
        Gui.drawRect(x, y, x + 1, y + h, 0xff3a3a80);
        Gui.drawRect(x + w - 1, y, x + w, y + h, 0xff202030);
    }

    private void drawBtn(int x, int y, int w, int h, String label, FontRenderer fr, boolean hov) {
        Gui.drawRect(x, y, x + w, y + h, hov ? 0xff32324a : 0xff1e1e2c);
        Gui.drawRect(x, y, x + w, y + 1, hov ? 0xff5a5aaa : 0xff38384a);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0xff090910);
        TextRenderUtils.drawStringCenteredScaledMaxWidth(label, fr, x + w / 2f, y + h / 2f + 1, false, w - 4, -1);
    }

    private void drawSmallBtn(int x, int y, int w, int h, String label, FontRenderer fr, boolean hov) {
        Gui.drawRect(x, y, x + w, y + h, hov ? 0xff303048 : 0xff1a1a28);
        Gui.drawRect(x, y, x + w, y + 1, 0xff343448);
        TextRenderUtils.drawStringCenteredScaledMaxWidth(label, fr, x + w / 2f, y + h / 2f + 1, false, w - 2, -1);
    }

    private void drawInputBg(int x, int y, int w, int h) {
        Gui.drawRect(x, y, x + w, y + h, 0xff0a0a14);
        Gui.drawRect(x, y, x + w, y + 1, 0xff282838);
        Gui.drawRect(x, y + h - 1, x + w, y + h, 0xff282838);
    }

    private void ensureSearchField(int x, int y, int w, int h, FontRenderer fr) {
        if (searchField == null) {
            searchField = new GuiTextField(0, fr, x, y, w, h);
            searchField.setMaxStringLength(64);
            searchField.setEnableBackgroundDrawing(false);
            searchField.setCanLoseFocus(true);
            searchField.setFocused(false);
        }
    }

    private GuiTextField ensureGenericField(GuiTextField existing, int id, int x, int y, int w, int h, FontRenderer fr) {
        if (existing == null) {
            existing = new GuiTextField(id, fr, x, y, w, h);
            existing.setMaxStringLength(64);
            existing.setEnableBackgroundDrawing(false);
            existing.setCanLoseFocus(true);
            existing.setFocused(true);
        }
        return existing;
    }

    private List<WaypointGroup> filteredGroups(WaypointStorage storage, String query) {
        List<WaypointGroup> result = new ArrayList<>();
        for (WaypointGroup g : storage.getGroups().values())
            if (query.isEmpty() || g.name.toLowerCase().contains(query)) result.add(g);
        return result;
    }

    private boolean inBounds(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private boolean isHovered(int x, int y, int w, int h) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int mx = Mouse.getX() * sr.getScaledWidth() / mc.displayWidth;
        int my = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / mc.displayHeight - 1;
        return inBounds(mx, my, x, y, w, h);
    }

    private abstract static class RowItem {
        int y;

        abstract int h();
    }

    private static class GroupRow extends RowItem {
        WaypointGroup g;
        boolean expanded;

        int h() {
            return ROW_H;
        }
    }

    private static class WpRow extends RowItem {
        WaypointGroup g;
        int idx;

        int h() {
            return WP_H;
        }
    }

    private static class AddRow extends RowItem {
        WaypointGroup g;

        int h() {
            return WP_H;
        }
    }
}