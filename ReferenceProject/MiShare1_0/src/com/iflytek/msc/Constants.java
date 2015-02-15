package com.iflytek.msc;
/**
 *  @author zhangwei 
 */
public class Constants {
	public final static String appID = "5150f897";
	
	public final static int MAX_AUDIO_SIZE = 8192;//;5242880;

	/* epstatues: End-point detected */
	public final static int SR_EP_LOOKING_FOR_SPEECH = 0;// 还没有检测到音频的前端点。
	public final static int ISR_EP_IN_SPEECH = 1;// 已经检测到了音频前端点，正在进行正常的音频处理。
	public final static int ISR_EP_AFTER_SPEECH = 3;// 检测到音频的后端点，后继的音频会被MSC忽略。
	public final static int ISR_EP_TIMEOUT = 4;// 超时。
	public final static int ISR_EP_ERROR = 5;// 出现错误。
	public final static int ISR_EP_MAX_SPEECH = 6;// 音频过大。

	/* recogStatus=?rsltstatus */
	public final static int ISR_REC_STATUS_SUCCESS = 0;// 识别成功，此时用户可以调用QISRGetResult来获取（部分）结果。
	public final static int ISR_REC_STATUS_NO_MATCH = 1;// 识别结束，没有识别结果
	public final static int ISR_REC_STATUS_INCOMPLETE = 2;// 正在识别中
	public final static int ISR_REC_STATUS_NON_SPEECH_DETECTED = 3;// 保留
	public final static int ISR_REC_STATUS_SPEECH_DETECTED = 4;// 发现有效音频
	public final static int ISR_REC_STATUS_SPEECH_COMPLETE = 5;// 识别结束
	public final static int ISR_REC_STATUS_MAX_CPU_TIME = 6;// 保留
	public final static int ISR_REC_STATUS_MAX_SPEECH = 7;// 保留
	public final static int ISR_REC_STATUS_STOPPED = 8;// 保留
	public final static int ISR_REC_STATUS_REJECTED = 9;// 保留
	public final static int ISR_REC_STATUS_NO_SPEECH_FOUND = 10;// 没有发现音频
}