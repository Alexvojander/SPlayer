package com.dmplayer.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dmplayer.R;

import com.dmplayer.manager.MediaController;
import com.dmplayer.ui.GoogleMapWindowAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**

 */
public class FragmentMap extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    GoogleMapWindowAdapter myAdapter;
    String city="";
    String date="";
    double lat;
    double lng;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_map, null);
        //setupInitialViews(rootview);

        myAdapter=new GoogleMapWindowAdapter(inflater);
        myAdapter.setImg_res(MediaController.getInstance().getPlayingSongDetail().getCover(getActivity().getApplicationContext()));

        String strtext = getArguments().getString("city");
        date= getArguments().getString("date");

        try{
            lat=Double.parseDouble(getArguments().getString("location_lat"));
            lng=Double.parseDouble(getArguments().getString("location_lng"));
        }
        catch (NullPointerException e){
            lat=0;
            lng=0;
        }

        city=strtext;
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment =  MapFragment.newInstance();
        fm.beginTransaction().replace(R.id.fragment_map, mapFragment).commit();
        mapFragment.getMapAsync(this);
        return rootview;
    }
    @Override
    public void onClick(View view) {

        Toast.makeText(getActivity(), city,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(myAdapter);

       final LatLng baseCity = new LatLng(lat,lng);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(baseCity)
                .title("Begin-area: "+city)
                .snippet("Year: " +date));
       // melbourne.showInfoWindow();
        myAdapter.getInfoWindow(marker);

       // mMap.setOnMarkerClickListener(markerListener);

       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(baseCity,5));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(baseCity,5));

        //melbourne.showInfoWindow();


    }
//    GoogleMap.OnMarkerClickListener markerListener=new GoogleMap.OnMarkerClickListener() {
//        @Override
//        public boolean onMarkerClick(Marker marker) {
//            if(marker.isInfoWindowShown()) {
//
//                marker.hideInfoWindow();
//
//
//            }else {
//                marker.showInfoWindow();
//            }
//
//
//            return true;
//        }
//    };

}
