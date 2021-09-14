package com.rmw.photolibrary.model;

public class ImageModel {

    private String img_ref;
    private String refKey;

    public void setImgRef(String img_ref) {
        this.img_ref = img_ref;
    }

    public String getImgRef() {
        return this.img_ref;
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }
}
