package com.jef.justenoughfakepixel.repo.data;

import com.google.gson.annotations.SerializedName;

public class PlayerSizeData {
    @SerializedName("name")
    public String name;
    @SerializedName("scale")
    public float scale = 1f;
    @SerializedName("scaleX")
    public Float scaleX = null;
    @SerializedName("scaleY")
    public Float scaleY = null;
    @SerializedName("scaleZ")
    public Float scaleZ = null;

    public float x() {
        return scaleX != null ? scaleX : scale;
    }

    public float y() {
        return scaleY != null ? scaleY : scale;
    }

    public float z() {
        return scaleZ != null ? scaleZ : scale;
    }
}