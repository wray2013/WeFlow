package com.cmmobi.looklook.info.location;

import java.util.List;

import com.baidu.mapapi.search.MKAddrInfo;

/**
 * 
 * @author wangjm
 *
 */
public interface OnPoiSearchListener {
    public void onPoiSearch(List<POIAddressInfo> poiList);
    public void onAddrSearch(MKAddrInfo res);
}
