package cn.steve.map;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.DrawableRes;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import cn.steve.study.R;

/**
 * Created by yantinggeng on 2017/1/5.
 */

public class BaiduMapUtil {

    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;

    private Context context;
    private BaiduMap mBaiduMap; // 地图控制器
    private MapView mMapView = null; // 地图 view
    private float zoom, maxZoom = 21, minZoom = 3; // 最大最小的比例尺

    private BitmapDescriptor mCurrentMarker;

    public BaiduMapUtil(Context context, MapView mMapView) {
        this.context = context;
        this.mMapView = mMapView;
        initMap();
    }

    private void initMap() {
        this.mBaiduMap = this.mMapView.getMap();
        this.mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        // 开启定位图层
        this.mBaiduMap.setMyLocationEnabled(true);
        // 定位图层显示方式, COMPASS :罗盘态，显示定位方向圈，保持定位图标在地图中心 ;FOLLOWING: 跟随态，保持定位图标在地图中心 ;NORMAL: 普通态, 更新定位数据时不对地图做任何操作
        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker, accuracyCircleFillColor, accuracyCircleStrokeColor);
        this.mBaiduMap.setMyLocationConfigeration(config);
    }

    /**
     * 设置比例尺范围
     *
     * @param maxZoom 最大
     * @param minZoom 最小
     */

    public void setMaxAndMinZoomLevel(float maxZoom, float minZoom) {
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        this.mBaiduMap.setMaxAndMinZoomLevel(maxZoom, minZoom);
    }

    public void setZoom(float zoom) {
        if (zoom < minZoom | zoom > maxZoom) {
            return;
        }
        MapStatus currentMapStatus = this.mBaiduMap.getMapStatus();
        float currentZoom = currentMapStatus.zoom;
        if (currentZoom != zoom) {
            MapStatus.Builder builder = new MapStatus.Builder();
            MapStatus mapStatus = builder.zoom(zoom).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            this.mBaiduMap.animateMapStatus(mapStatusUpdate);
        }
    }

    public void setOnMapStatusChangeListener(final OnMapStatusChangeListener mapStatusChangeListener) {
        this.mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (mapStatusChangeListener != null) {
                    mapStatusChangeListener.onMapStatusChangeFinish(mapStatus);
                }
            }
        });
    }


    public void setOnMarkerClickListener(final OnMarkerClickListener onMarkerClickListener) {
        this.mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (onMarkerClickListener != null) {
                    return onMarkerClickListener.onMarkerClick(marker);
                }
                return false;
            }
        });
    }

    public void setCurrentLocation(float accuracy, float direction, double lng, double lat, int satellitesNum, float speed) {
        MyLocationData locData = new MyLocationData.Builder()
            .accuracy(accuracy)
            .direction(direction)
            .latitude(lat)
            .longitude(lng)
            .satellitesNum(satellitesNum)
            .speed(speed)
            .build();
        this.mBaiduMap.setMyLocationData(locData);
    }

    // 标记
    public void addOverlay(final MapStatus mapStatus) {
        LatLng target = mapStatus.target; //地图操作的中心点。
        Point targetScreen = mapStatus.targetScreen; //地图操作中心点在屏幕中的坐标
        float zoom = mapStatus.zoom; //地图缩放级别 3~21
        MarkerOptions ooA = new MarkerOptions().position(target).icon(mCurrentMarker).zIndex(9).draggable(true);
        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
        this.mBaiduMap.addOverlay(ooA);
    }

    //
    private void addInfoWindow(final Marker marker, @DrawableRes int resID, String msg) {
        Button button = new Button(context);
        button.setBackgroundResource(resID);
        button.setText(msg);
        button.setBackgroundColor(0x0000f);
        button.setWidth(300);

        InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                LatLng ll = marker.getPosition();
                LatLng llNew = new LatLng(ll.latitude + 0.005, ll.longitude + 0.005);
                marker.setPosition(llNew);
                // 隐藏当前 InfoWindow
                mBaiduMap.hideInfoWindow();
            }
        };
        LatLng ll = marker.getPosition();
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(button);
        InfoWindow mInfoWindow = new InfoWindow(descriptor, ll, -47, listener);
        this.mBaiduMap.showInfoWindow(mInfoWindow);
    }


    public void clearOverlay() {
        this.mBaiduMap.clear();
    }

    public void onDestroy() {
        this.mMapView.onDestroy();
    }

    public void onPause() {
        this.mMapView.onPause();
    }

    public void onResume() {
        this.mMapView.onResume();
    }


    public interface OnMapStatusChangeListener {

        public void onMapStatusChangeFinish(final MapStatus mapStatus);
    }

    public interface OnMarkerClickListener {

        public boolean onMarkerClick(Marker marker);
    }


}


