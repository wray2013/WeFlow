package com.cmmobi.looklook.common.service.aidl;  
import com.cmmobi.looklook.common.service.aidl.Main2ServiceObj;
import com.cmmobi.looklook.common.service.aidl.MainCallBack;
interface MainUpdate {   
    void registerUpdateCall(MainCallBack cb);   
    void invokCallBack(in Main2ServiceObj obj); 
}  
