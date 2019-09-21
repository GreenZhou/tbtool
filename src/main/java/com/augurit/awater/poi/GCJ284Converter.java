package com.augurit.awater.poi;

import java.math.BigDecimal;

public class GCJ284Converter {
	public static final double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
	public static final double PI = 3.1415926535897932384626;
	public static final double a = 6378245.0;
	public static final double ee = 0.00669342162296594323;

	public static double[] wgs84togcj02(double lng, double lat) {
		double[] res = {lng, lat};
		if (out_of_china(lng, lat)) {
			return res;
		} else {
			double dlat = transformlat(lng - 105.0,lat - 35.0);
			double dlng = transformlng(lng - 105.0, lat - 35.0);
			double radlat = lat / 180.0 * PI;
			double magic = Math.sin(radlat);
			magic = 1 - ee * magic * magic;
			double sqrtmagic = Math.sqrt(magic);
			dlat = (dlat * 180.0) / ((a * (1 - ee)) /
					(magic * sqrtmagic) * PI);
			dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
			res[1] = lat + dlat;
			res[0] = lng + dlng;
			return res;
		}
	}
	public static double[] gcj02towgs84(double lng, double lat) {
		double[] res = {lng, lat};
		if (!out_of_china(lng, lat)) {
			double dlat = transformlat(lng - 105.0, lat - 35.0);
			double dlng = transformlng(lng - 105.0, lat - 35.0);
			double radlat = lat / 180.0 * PI;
			double magic = Math.sin(radlat);
			magic = 1 - ee * magic * magic;
			double sqrtmagic = Math.sqrt(magic);
			dlat = (dlat * 180.0) / ((a * (1 - ee)) /
					(magic * sqrtmagic) * PI);
			dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
			double mglat = lat + dlat;
			double mglng = lng + dlng;
			res[0] = lng * 2 - mglng;
			res[1] = lat * 2 - mglat;
		}
		return res;
	}

	public static boolean out_of_china(double lng, double lat) {
		// 纬度 3.86~53.55,经度 73.66~135.05
		return !(lng > 73.66 && lng < 135.05 && lat > 3.86 && lat < 53.55);
	}

	public static double transformlat(double lng, double lat) {
		double ret = -100.0 + 2.0 * lng + 3.0 * lat +
				0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0
				* Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
		ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
		return ret;
	}

	public static double transformlng(double lng, double lat) {
		double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs
				(lng));
		ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0
				* Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
		ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
		ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
		return ret;
	}

	public static void main(String[] args){
		double[][] coors_list = {
				{121.299123,31.188365},
				{121.293008,31.191706},
				{121.301376,31.193652},
				{121.298737,31.184602},
				{121.296183,31.186163},
		};

		for(double[] coors : coors_list) {
			// double[] coors = {121.513225, 31.217408};
			double[] gcj02towgs84 = gcj02towgs84(coors[0], coors[1]);
			// System.out.println(String.valueOf(gcj02towgs84[0]) + '\t' + String.valueOf(gcj02towgs84[1]));
			Mercator merc = new Mercator();
			double[] merc_res = merc.merc(gcj02towgs84[0], gcj02towgs84[1]);
			String[] merc_str = {String.format(new BigDecimal(merc_res[0]).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()),
					new BigDecimal(merc_res[1]).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString()};
			System.out.println(String.valueOf(coors
					[0])+'\t'+String.valueOf(coors[1])+'\t'
					+String.valueOf(merc_str[0]) + '\t' + String.valueOf(merc_str[1]));
		}
	}
}