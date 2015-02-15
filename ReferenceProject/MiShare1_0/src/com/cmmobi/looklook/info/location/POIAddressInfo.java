package com.cmmobi.looklook.info.location;

public class POIAddressInfo {
	public String position;// 具体地址名称
	public String longitude;// 经度
	public String latitude;// 维度
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "position = " + position + " longitude = " + longitude + " latitude = " + latitude;
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (position == null || o == null) {
			return false;
		}
		if (o instanceof String) {
			return position.equals(o);
		} else if (o instanceof POIAddressInfo) {
			return position.equals(((POIAddressInfo) o).position);
		}
		
		return false;
	}
}
