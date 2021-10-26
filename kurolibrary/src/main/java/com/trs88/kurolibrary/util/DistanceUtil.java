package com.trs88.kurolibrary.util;

import com.trs88.kurolibrary.log.KuroLog;

/**
 * 定位距离的工具类
 */
public class DistanceUtil {
    private final static double EARTH_RADIUS =6378.137;


    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
    /**
     * 计算两个经纬度之间的距离，单位米
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        KuroLog.v("getDistance 两点间距离: "+s);
//        Toast.makeText(App.getInstance(),"curLatitude  :"+lat2+" ,curLongitude: "+lng2+ " ,getDistance 两点间距离: "+s ,Toast.LENGTH_SHORT).show();
        return s;
    }


    /**
     * 判断是否在范围内
     * @param fenceLatitude
     * @param fenceLongitude
     * @param curLatitude
     * @param curLongitude
     * @param radius
     * @return
     */
    public static boolean inRadius(double fenceLatitude, double fenceLongitude, double curLatitude, double curLongitude, int radius){
        if (getDistance(fenceLatitude,fenceLongitude,curLatitude,curLongitude)>=radius){
            return  false;
        }else {
            return true;
        }
    }
}
