package cn.steve.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

/**
 * Created by yantinggeng on 2017/1/5.
 */

public class BaiduLocationUtil {

    private Context context;
    private LocationClient mLocClient; // 定位工具
    private LocationSuccessListener locationSuccessListener;

    public BaiduLocationUtil(Context context, LocationSuccessListener locationSuccessListener) {
        this.context = context;
        this.locationSuccessListener = locationSuccessListener;
        init();
    }

    // 定位的配置
    private LocationClientOption getLocationClientOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        //option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        return option;
    }

    private void init() {
        mLocClient = new LocationClient(context.getApplicationContext());
        mLocClient.registerLocationListener(new LocationListener());
        mLocClient.setLocOption(getLocationClientOption());
        mLocClient.start();
    }

    public void requestLocation() {
        mLocClient.requestLocation();
    }


    public interface LocationSuccessListener {

        void onReceiveLocation(BDLocation location);
    }


    private class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            String locationString = getLocationString(location);
            if (locationSuccessListener != null) {
                locationSuccessListener.onReceiveLocation(location);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }


        private String getLocationString(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\n error code : ");
            sb.append(location.getLocType());
            sb.append("\n latitude : ");
            sb.append(location.getLatitude());
            sb.append("\n lontitude : ");
            sb.append(location.getLongitude());
            sb.append("\n radius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\n speed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\n satellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\n height : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\n direction : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\n addr : ");
                sb.append(location.getAddrStr());
                sb.append("\n describe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\n addr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\n operationers : ");
                sb.append(location.getOperators());
                sb.append("\n describe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\n describe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\n describe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\n describe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\n describe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\n locationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\n poilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\n poi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            return sb.toString();
        }
    }


}
