package cn.steve.map;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/12/21.
 */

public class BaiduMapActivity extends AppCompatActivity {

    private TextView textView;
    private Handler handler = new Handler();
    private BaiduLocationUtil locationUtil;
    private BaiduMapUtil mapUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidumap);
        textView = (TextView) findViewById(R.id.textView);
        MapView mMapView = (MapView) findViewById(R.id.bmapView);
        mapUtil = new BaiduMapUtil(this, mMapView);
        locationUtil = new BaiduLocationUtil(this, new BaiduLocationUtil.LocationSuccessListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                float accuracy = location.getRadius();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                float direction = location.getDirection();
                int satelliteNumber = location.getSatelliteNumber();
                float speed = location.getSpeed();
                mapUtil.setCurrentLocation(accuracy, direction, longitude, latitude, satelliteNumber, speed);
            }
        });
        findViewById(R.id.refreshLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationUtil.requestLocation();
            }
        });

        findViewById(R.id.zoomButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapUtil.setZoom(18);
            }
        });
        mapUtil.setOnMapStatusChangeListener(new BaiduMapUtil.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeFinish(final MapStatus mapStatus) {
                // TODO: 2017/1/6 根据中心点，获取数据
                LatLng target = mapStatus.target; //地图操作的中心点。
                Point targetScreen = mapStatus.targetScreen; //地图操作中心点在屏幕中的坐标
                float zoom = mapStatus.zoom; //地图缩放级别 3~21

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapUtil.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapUtil.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapUtil.onPause();
    }
}

