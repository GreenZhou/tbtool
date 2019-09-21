package com.augurit.awater.poi;

public class HxzbToWgsUtils {
	 public double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
	 public double PI = 3.1415926535897932384626;
	 public double a = 6378245.0;
	 public double ee = 0.00669342162296594323;
	//火星坐标系转wgs84
	    public double[] gcj02towgs84(double gcj_lon, double gcj_lat)
	    {
	        if (out_of_china(gcj_lon, gcj_lat))
	        {
	            //不在国内，不进行纠偏
	            double[] back = { gcj_lon, gcj_lat };
	            return back;
	        }
	        else
	        {
	        	double dlon = transformlon(gcj_lon - 105.0, gcj_lat - 35.0);
	        	double dlat = transformlat(gcj_lon - 105.0, gcj_lat - 35.0);
	        	double radlat = gcj_lat / 180.0 * PI;
	        	double magic = Math.sin(radlat);
	            magic = 1 - ee * magic * magic;
	            double sqrtmagic = Math.sqrt(magic);
	            dlon = (dlon * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
	            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
	            double mglon = gcj_lon + dlon;
	            double mglat = gcj_lat + dlat;
	            double wgs_lon = gcj_lon * 2 - mglon;
	            double wgs_lat = gcj_lat * 2 - mglat;
	            double[] wgs = { wgs_lon, wgs_lat };//wgs84坐标系值
	            return wgs;
	        }
	    }
	    
	    private double transformlon(double lon, double lat)
	    {
	    	double ret = 300.0 + lon + 2.0 * lat + 0.1 * lon * lon + 0.1 * lon * lat + 0.1 * Math.sqrt(Math.abs(lon));
	        ret += (20.0 * Math.sin(6.0 * lon * PI) + 20.0 * Math.sin(2.0 * lon * PI)) * 2.0 / 3.0;
	        ret += (20.0 * Math.sin(lon * PI) + 40.0 * Math.sin(lon / 3.0 * PI)) * 2.0 / 3.0;
	        ret += (150.0 * Math.sin(lon / 12.0 * PI) + 300.0 * Math.sin(lon / 30.0 * PI)) * 2.0 / 3.0;
	        return ret;
	    }

	    private double transformlat(double lon, double lat)
	    {
	    	double ret = -100.0 + 2.0 * lon + 3.0 * lat + 0.2 * lat * lat + 0.1 * lon * lat + 0.2 * Math.sqrt(Math.abs(lon));
	        ret += (20.0 * Math.sin(6.0 * lon * PI) + 20.0 * Math.sin(2.0 * lon * PI)) * 2.0 / 3.0;
	        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
	        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
	        return ret;
	    }

	    //判断是否在国内，不在国内则不做偏移
	    private Boolean out_of_china(double lon, double lat)
	    {
	        return (lon < 72.004 || lon > 137.8347) || ((lat < 0.8293 || lat > 55.8271) || false);
	    }

	    
	    public static void main(String[] args) {
	    	System.out.println(new HxzbToWgsUtils().gcj02towgs84(Double.parseDouble("113.200999"),
	    			Double.parseDouble("23.431240"))[0]);
	    	
		}
}
