// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst ansi nonlb space 

package com.iflytek.msc;


// Referenced classes of package com.iflytek.msc:
//			MSCSessionInfo

public class MSC2 {

	public MSC2() {
	}

	public static native int DebugLog(boolean flag);

	public static native int QTTSInit(byte abyte0[]);

	public static final native char[] QTTSSessionBegin(byte abyte0[], MSCSessionInfo2 mscsessioninfo);

	public static native int QTTSTextPut(char ac[], byte abyte0[]);

	public static native byte[] QTTSAudioGet(char ac[], MSCSessionInfo2 mscsessioninfo);

	public static native int QTTSGetParam(char ac[], byte abyte0[], MSCSessionInfo2 mscsessioninfo);

	public static native char[] QTTSAudioInfo(char ac[]);

	public static native int QTTSSessionEnd(char ac[], byte abyte0[]);

	public static native int QTTSFini();

	public static native int QISRInit(byte abyte0[]);

	public static final native char[] QISRSessionBegin(byte abyte0[], byte abyte1[], MSCSessionInfo2 mscsessioninfo);

	public static native int QISRGetParam(char ac[], byte abyte0[], MSCSessionInfo2 mscsessioninfo);

	public static native int QISRAudioWrite(char ac[], byte abyte0[], int i, int j, MSCSessionInfo2 mscsessioninfo);

	public static final native byte[] QISRGetResult(char ac[], MSCSessionInfo2 mscsessioninfo);

	public static native int QISRSessionEnd(char ac[], byte abyte0[]);

	public static native int QISRFini();

	public static native int QISVFini();

	public static native int QMSPLogin(byte abyte0[], byte abyte1[], byte abyte2[]);

	public static native byte[] QMSPUploadData(byte abyte0[], byte abyte1[], int i, byte abyte2[], MSCSessionInfo2 mscsessioninfo);

	public static native byte[] QMSPDownloadData(byte abyte0[], MSCSessionInfo2 mscsessioninfo);

	public static native byte[] QMSPSearch(byte abyte0[], byte abyte1[], MSCSessionInfo2 mscsessioninfo);

	public static native int QMSPLogOut();

	static  {
		System.loadLibrary("msc");
	}
}
