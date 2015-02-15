/*
 * 
 *      zhangwei@cmmobi.com
 *      
 *      2013-03-28
 * */

package com.iflytek.msc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 *  @author zhangwei 
 */
public class QISR {
	public final static int QISR_STATUS_UNINIT = 0;
	public final static int QISR_STATUS_INIT = 1;
	public final static int QISR_STATUS_SESSION = 2;
	public final static int QISR_STATUS_WRITE = 3;

	private String appID;
	private String TAG = "QISR";
	private String inputFile; // pcm
	private String outputText;
	private MSCSessionInfo2 sInfo;

	private char[] sessinID;

	private Handler handler;

	private int status;

	private QISR() {
		sInfo = new MSCSessionInfo2();
		status = QISR_STATUS_UNINIT;
		appID = null;
		outputText = "";
	}

	public QISR(Handler h, String ID) {
		this();
		appID = ID;
		handler = h;
	}

	public void Init() {
		if (appID == null) {
			Log.e(TAG, "Init - fail - appID is null");
			return;
		}

		int ret = MSC2.QISRInit(("appid=" + appID + ",vad_enable=0").getBytes());

		if (ret == 0) {
			status = QISR_STATUS_INIT;
			Log.d(TAG, "QISRInit - OK");
		} else {
			Log.e(TAG, "QISRInit - fail");
		}
	}

	public void Fini() {
		int ret = MSC2.QISRFini();
		if (ret == 0) {
			// exec ok
			status = QISR_STATUS_UNINIT;
			Log.d(TAG, "QISRFini - OK");
		} else {
			Log.e(TAG, "QISRFini - fail");
		}
	}

	public int getStatus() {
		return status;
	}

	public String Process(String inputFile) {
		this.inputFile = inputFile;
		process();

		return outputText;

	}

	public String Process() {
		//this.inputFile = TestMscActivity.RECORD_FILE_DIR;
		process();

		return outputText;

	}

	public String getResult() {
		return outputText;
	}

	// //////////////////private method ////////////////////

	// session should lock
	private void QISRSessionBegin() {
		// for 16k audio
		String arg = "ssm=1,sub=iat,auf=audio/L16;rate=44100,aue=raw,rst=plain,rse=utf8";
		//String arg = "ssm=1,sub=iat,auf=audio/L16;rate=16000,aue=raw,ent=sms16k,rst=plain,rse=utf8";
		//String arg = "ssm=1,sub=iat,auf=audio/L16;rate=11025,aue=raw,ent=sms16k,rst=plain,rse=utf8";

		// for 8k audio
		// String arg =
		// "ssm=1,sub=iat,auf=audio/L16;rate=8000,aue=speex,ent=sms8k,rst=plain,rse=utf8";

		sessinID = MSC2.QISRSessionBegin(null, arg.getBytes(), sInfo);

		if (sInfo.errorcode == 0) {
			status = QISR_STATUS_SESSION;
			Log.d(TAG, "QISRSessionBegin - OK");
		} else {
			Log.e(TAG, "QISRSessionBegin - fail");
		}
	}

	/*
	 * 5KB 大小的16KPCM 持续的时间是160 毫秒,单声道，16bit WAV格式文件所占容量（KB) = （取样频率 X 量化位数 X 声道）
	 * X 时间 / 8 (字节= 8bit) 5*1024=16000*16*1*t/8 t=0.160 (160ms)
	 */
	private int QISRAudioWrite(byte[] data, int len, int seq) {
		int ret;
		if (seq == 1) {// head
			Log.e(TAG, "QISRAudioWrite head - sessinID:" + sessinID + " len:"
					+ len);
			ret = MSC2.QISRAudioWrite(sessinID, data, len, 2, sInfo);
		} else if (seq == 2) {// more
			Log.e(TAG, "QISRAudioWrite more - sessinID:" + sessinID + " len:"
					+ len);
			ret = MSC2.QISRAudioWrite(sessinID, data, len, 2, sInfo);
		} else {// final
			Log.e(TAG, "QISRAudioWrite final - sessinID:" + sessinID);
			ret = MSC2.QISRAudioWrite(sessinID, "\0".getBytes(), 1, 4, sInfo);
		}

		return ret;
	}

	private String QISRGetResult() {
		byte[] result = MSC2.QISRGetResult(sessinID, sInfo);
		String r = null;
		if (result != null) {

			r = new String(result);

			Log.e(TAG, "QISRGetResult - sInfo.rsltstatus:" + sInfo.rsltstatus
					+ "result:" + r);
			// Log.e(TAG, System.getProperty("file.encoding"));
			Message msg = new Message();
			msg.what = 0;
			msg.obj = r;
			handler.sendMessage(msg);
			return r;
		} else {
			return null;
		}

	}

	private void QISRSessionEnd() {
		int ret = MSC2.QISRSessionEnd(sessinID, null);
		if (ret == 0) {
			// exec ok
			status = QISR_STATUS_WRITE;
			Log.d(TAG, "QISRSessionEnd - OK");
		} else {
			Log.e(TAG, "QISRSessionEnd - fail");
		}
	}

	private void process() {
		QISRSessionBegin();
		if (status != QISR_STATUS_SESSION) {
			return;
		}

		InputStream in = null;
		File file = new File(inputFile);

		int audioSource = MediaRecorder.AudioSource.MIC;
		int sampleRateInHz = 16000; //11025 ,16000
		int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
				channelConfig, audioFormat);
		AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
				channelConfig, audioFormat, bufferSizeInBytes);
		
		//audioRecord.startRecording();
		short[] buffer = new short[bufferSizeInBytes/4];

		long dleta_s = 0;
		long dleta_e = 0;

		long write_done = 0;

		try {
			in = new FileInputStream(file);
			int len = 1;
			byte[] buff = new byte[Constants.MAX_AUDIO_SIZE];
			// int status = 2, ep_status = 0, rec_status = 0, rslt_status = 0;
			sInfo.epstatues = 0;
			sInfo.rsltstatus = 0;
			sInfo.sesstatus = 2;

			boolean flag_w = true;
			boolean flag_r = true;
			int offset = 0;
			while (flag_w || flag_r) {
				dleta_s = System.currentTimeMillis();
				if (flag_w) {
					len = in.read(buff);// -1 eof
					//len = audioRecord.read(buffer, 0, bufferSizeInBytes/4);

					if (len > 0) {
						// read src ok

						Log.e(TAG, "read len:" + len + " offset:" + offset);
						int seq = 0;
						if (offset == 0) {
							seq = 1; // head
						} else { // more
							seq = 2;
						}
						int ret = QISRAudioWrite(buff, len, seq);

						offset += len;

						if (ret != 0) {
							Log.e(TAG, "QISRAudioWrite error:" + ret);
							// break;
						} else if (sInfo.epstatues == Constants.ISR_EP_AFTER_SPEECH) {
							/* 检测到音频后端点，停止发送音频? */
							Log.e(TAG,
									"QISRAudioWrite error: end point of speech has been detected!");
							// break;
						}

					} else if (len > -2) {
						// -1 or 0 : EOF,send fin
						int ret = QISRAudioWrite(buff, len, len);
						if (ret != 0) {
							Log.e(TAG, "QISRAudioWrite error:" + ret);
							// break;
						}

						flag_w = false;
						write_done = System.currentTimeMillis();
					} else {
						Log.e(TAG, "src read error:" + len);
						flag_w = false;
						write_done = System.currentTimeMillis();
					}
				}

				// check and Retrieve result text
				if (sInfo.rsltstatus == 0) {
					/* 识别成功，此时用户可以调用QISRGetResult来获取（部分）结果。 */
					outputText = outputText + QISRGetResult();
					flag_r = true;// let it loop next time
				}

				if (!flag_w) {// write complete, check result always
					do {
						String s = QISRGetResult();

						if (s != null) {
							outputText = outputText + s;
						}

						Log.e(TAG, "sInfo.rsltstatus:" + sInfo.rsltstatus
								+ " outputText:" + outputText);
						if (sInfo.rsltstatus == 0) {
							/* 识别成功，此时用户可以调用QISRGetResult来获取（部分）结果。 */
							outputText = outputText + QISRGetResult();
							flag_r = true;// let it loop next time
						} else if (sInfo.rsltstatus == 1) {
							// 识别结束，没有识别结果
							outputText = "识别结束，没有识别结果";
							flag_r = false;
						} else if (sInfo.rsltstatus == 2
								|| sInfo.rsltstatus == 4) {
							// 2: 正在识别中
							// 4: 发现有效音频
							Thread.sleep(100);
							flag_r = false;
						} else {
							flag_r = false;
						}
					} while (flag_r);
				}

				dleta_e = System.currentTimeMillis();
				// sleep time to avoid the local msc buffer overhead
				if (flag_w) {
					// len=16000*16/8*t
					long wait_time = len / 32 - (dleta_e - dleta_s);
					if (wait_time > 0) {
						Thread.sleep(wait_time);
					}
				}

				// check the deadline,no write and read time out
				if (flag_w == false && (dleta_e - write_done) > 5000) { // 5
																		// seconds
					flag_r = false;
				}

			}
			in.close();
			
			audioRecord.stop();
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		QISRSessionEnd();
		if (status != QISR_STATUS_INIT) {
			return;
		}

	}
}
