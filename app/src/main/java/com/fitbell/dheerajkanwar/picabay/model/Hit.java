package com.fitbell.dheerajkanwar.picabay.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hit {

@SerializedName("largeImageURL")
@Expose
private String largeImageURL;

@SerializedName("id")
@Expose
private int id;

@SerializedName("previewURL")
@Expose
private String previewURL;

public String getLargeImageURL() {
return largeImageURL;
}

public void setLargeImageURL(String largeImageURL) {
this.largeImageURL = largeImageURL;
}

public int getId() {
return id;
}

public void setId(int id) {
this.id = id;
}

public String getPreviewURL() {
return previewURL;
}

public void setPreviewURL(String previewURL) {
this.previewURL = previewURL;
}

}