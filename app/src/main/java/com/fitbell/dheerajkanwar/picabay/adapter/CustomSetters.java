package com.fitbell.dheerajkanwar.picabay.adapter;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import androidx.databinding.BindingAdapter;

public class CustomSetters {

    @BindingAdapter("imgSrc")
    public static void setImgSrc(ImageView view, String url) {
        if(url != null && !url.isEmpty()) Picasso.get().load(url).into(view);
    }

}
