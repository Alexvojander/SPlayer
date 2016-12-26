package com.dmplayer.ui;


import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmplayer.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import butterknife.ButterKnife;

/**
 * Created by Alexvojander on 28.11.2016.
 */

public class GoogleMapWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater=null;
    Bitmap img_bmp=null;
    public void setImg_res(Bitmap img_bmp){
        this.img_bmp=img_bmp;
    }

    public GoogleMapWindowAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }
    @Override
    public View getInfoWindow(Marker marker) {

        return (null);

    }

    @Override
    public View getInfoContents(Marker marker) {

        View popup=inflater.inflate(R.layout.google_map_window_adapter, null);
        ImageView im=ButterKnife.findById(popup, R.id.icon) ;


        if(img_bmp==null){
            im.setImageResource(R.drawable.avatar_default);
        }else{
            im.setImageBitmap(img_bmp);
        }

        TextView tv=ButterKnife.findById(popup, R.id.title);

        tv.setText(marker.getTitle());
        tv=ButterKnife.findById(popup, R.id.snippet);
        tv.setText(marker.getSnippet());

        return(popup);
    }
}
