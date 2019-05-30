package com.fitbell.dheerajkanwar.picabay.view;

/* A splash screen to show app branding */

import android.content.Intent;
import android.os.Bundle;


import com.fitbell.dheerajkanwar.picabay.R;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SplashActivity extends AppCompatActivity {

    //Splash screen timeout in milliseconds
    private static final int SPLASH_TIME_OUT = 3000 ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity_layout);

        Observable.timer(SPLASH_TIME_OUT, TimeUnit.MILLISECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        openNewsActivity();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void openNewsActivity() {
        finish();
        Intent intent = new Intent(SplashActivity.this, PictureSearchListActivity.class);
        startActivity(intent);
    }

}
