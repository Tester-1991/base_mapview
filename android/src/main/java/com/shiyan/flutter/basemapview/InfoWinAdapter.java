package com.shiyan.flutter.basemapview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

/**
 * Created by Teprinciple on 2016/8/23. * 地图上自定义的infowindow的适配器
 */
public class InfoWinAdapter implements AMap.InfoWindowAdapter {

    private Context mContext = BaseMapviewPlugin.root;

    private LinearLayout rootView;

    private String title;

    private String sn;

    private String flightNum;

    private TextView tvTitle;

    private TextView tvToDetail;

    private Marker marker;

    public InfoListener infoListener;

    @Override
    public View getInfoWindow(Marker marker) {

        Log.e("地图测试","getInfoWindow");

        this.marker = marker;

        initData(marker);

        View view = initView();

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {

        return null;
    }

    private void initData(Marker marker) {

        if (marker.getTitle().contains("--")) {

            title = marker.getTitle().split("--")[0];

            flightNum = marker.getTitle().split("--")[1];

            sn = marker.getTitle().split("--")[2];

        } else {

            title = marker.getTitle();

            sn = null;

        }
    }


    private View initView() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_infowindow, null);

        rootView = view.findViewById(R.id.rootview);

        tvTitle = view.findViewById(R.id.tv_title);

        tvToDetail = view.findViewById(R.id.tv_to_detail);

        tvTitle.setText(title);

        tvToDetail.setVisibility(marker.getTitle().contains("--") ? View.VISIBLE : View.GONE);

        rootView.setOnClickListener(v -> {

            marker.hideInfoWindow();

            if (infoListener != null) {

                infoListener.onClick(flightNum, sn);

            }

        });

        return view;
    }

    public void setInfoListener(InfoListener infoListener) {

        this.infoListener = infoListener;

    }

    public interface InfoListener {

        void onClick(String flightNumber, String boxSn);

    }

}