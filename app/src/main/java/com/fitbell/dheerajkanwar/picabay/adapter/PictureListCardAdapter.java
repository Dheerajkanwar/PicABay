package com.fitbell.dheerajkanwar.picabay.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.fitbell.dheerajkanwar.picabay.databinding.PictureListCardBinding;
import com.fitbell.dheerajkanwar.picabay.model.Hit;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class PictureListCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Hit> arrayList ;
    private LayoutInflater inflater ;

    public PictureListCardAdapter(ArrayList<Hit> arrayList) {
        this.arrayList = arrayList ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(inflater == null){
            inflater = LayoutInflater.from(parent.getContext()) ;
        }

        PictureListCardBinding pictureListCardBinding = PictureListCardBinding.inflate(inflater, parent, false);
        return new NewsItemViewHolder(pictureListCardBinding) ;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        NewsItemViewHolder holder1 = (NewsItemViewHolder) holder ;
        Hit datum = arrayList.get(position) ;
        holder1.bind(datum);

    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }



    class NewsItemViewHolder extends RecyclerView.ViewHolder {

        private PictureListCardBinding pictureListCardBinding ;

        NewsItemViewHolder(PictureListCardBinding pictureListCardBinding) {
            super(pictureListCardBinding.getRoot());
            this.pictureListCardBinding = pictureListCardBinding ;
        }

        void bind(Hit hit) {
            pictureListCardBinding.setHit(hit);
        }

    }

    public void addDatatoAdapter() {
        notifyDataSetChanged();
    }


}
