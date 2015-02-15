// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst ansi nonlb space 

package com.iflytek.msc;


public class MSCSessionInfo2 {
	/*	MSP_ERROR_NOT_INIT 未初始化
		MSP_ERROR_INVALID_HANDLE 无效的会话ID
		MSP_ERROR_INVALID_PARA 无效的参数
		MSP_ERROR_INVALID_PARA_VALUE 无效的参数值
		MSP_ERROR_NO_LICENSE 会话模式中此前开始一路会话失败。*/
	public int errorcode;
	
	/*	
	    epstatues: End-point detected
	    SR_EP_LOOKING_FOR_SPEECH = 0 还没有检测到音频的前端点。
		ISR_EP_IN_SPEECH = 1 已经检测到了音频前端点，正在进行正常的音频处理。
		ISR_EP_AFTER_SPEECH = 3 检测到音频的后端点，后继的音频会被MSC忽略。
		ISR_EP_TIMEOUT = 4 超时。
		ISR_EP_ERROR = 5 出现错误。
		ISR_EP_MAX_SPEECH = 6 音频过大。
	*/
	public int epstatues;
	
	/*
	 * recogStatus=?rsltstatus
	 *  ISR_REC_STATUS_SUCCESS = 0  识别成功，此时用户可以调用QISRGetResult来获取（部分）结果。
        ISR_REC_STATUS_NO_MATCH = 1 识别结束，没有识别结果
        ISR_REC_STATUS_INCOMPLETE = 2 正在识别中
        ISR_REC_STATUS_NON_SPEECH_DETECTED = 3 保留
        ISR_REC_STATUS_SPEECH_DETECTED = 4 发现有效音频
        ISR_REC_STATUS_SPEECH_COMPLETE = 5 识别结束
        ISR_REC_STATUS_MAX_CPU_TIME = 6 保留
        ISR_REC_STATUS_MAX_SPEECH = 7, 保留
        ISR_REC_STATUS_STOPPED = 8 保留
        ISR_REC_STATUS_REJECTED = 9 保留
        ISR_REC_STATUS_NO_SPEECH_FOUND = 10 没有发现音频
	 
	 **/
	public int rsltstatus;
	public int sesstatus;
	public byte buffer[];
	public int buflen;

	public MSCSessionInfo2() {
		buffer = null;
		buflen = -1;
		buffer = null;
		sesstatus = -1;
		rsltstatus = 2;
	}
}
