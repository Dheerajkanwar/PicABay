package com.fitbell.dheerajkanwar.picabay.source.api;

import com.fitbell.dheerajkanwar.picabay.model.PictureListMain;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RestApiInterface {

    @GET
    Observable<PictureListMain> getLatestNews(@Url String url);

}


