package com.fitbell.dheerajkanwar.picabay.repository;

import com.fitbell.dheerajkanwar.picabay.model.PictureListMain;
import com.fitbell.dheerajkanwar.picabay.source.api.RestApiClient;
import com.fitbell.dheerajkanwar.picabay.source.api.RestApiInterface;

import io.reactivex.Observable;

public class PictureListRepository {

    private static PictureListRepository pictureListRepository = null ;

    public static PictureListRepository getInstance() {
        if(pictureListRepository == null) {
            pictureListRepository = new PictureListRepository();
        }
        return pictureListRepository ;
    }

    public Observable<PictureListMain> getNewsFromApi(int page) {

        return RestApiClient.getClient().create(RestApiInterface.class).getLatestNews("?key=12625202-08b7e653a9225deda9f677b94&q=yellow+flowers&image_type=photo");

    }


}
