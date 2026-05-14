package com.jef.justenoughfakepixel.repo;

import com.jef.justenoughfakepixel.repo.data.RepoData;

public class CapeAPI {

    public static String getAPIUrl(){
        RepoData url = RepoHandler.get(JefRepo.KEY_REPO, RepoData.class,new RepoData());
        return url.capeApi;
    }

    public static String getAPIUrl(String endpoint){
        RepoData url = RepoHandler.get(JefRepo.KEY_REPO, RepoData.class,new RepoData());
        return url.capeApi + (url.capeApi.endsWith("/") || endpoint.startsWith("/") ? "" : "/") + endpoint;
    }

}
