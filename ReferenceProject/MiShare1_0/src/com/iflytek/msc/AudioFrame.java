package com.iflytek.msc;

public class AudioFrame {
	public AudioFrame(byte[] buffer, int len2, int i) {
		// TODO Auto-generated constructor stub
		data = buffer;
		len = len2;
		type = i;
	}
	
	byte[] data;
	int len;
	int type; //1 normal 4 FIN

}
