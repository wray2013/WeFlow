package com.cmmobi.looklook.common.service.aidl; 
import com.cmmobi.looklook.common.service.aidl.Service2MainObj;
import com.cmmobi.looklook.common.service.aidl.Service2Main2Obj;

interface MainCallBack {   
    void UpdateCallBack(in Service2MainObj obj);
    void Update2CallBack(in Service2Main2Obj obj);
} 