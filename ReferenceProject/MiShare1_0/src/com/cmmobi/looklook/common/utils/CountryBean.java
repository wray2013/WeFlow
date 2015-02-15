package com.cmmobi.looklook.common.utils;

public class CountryBean{
	public String sortKey;
	public String countryName;
	public String countryNo;
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null || countryNo == null || countryName == null) {
			return false;
		}
		return countryName.equals(((CountryBean)o).countryName) && countryNo.equals(((CountryBean)o).countryNo);
	}
}
