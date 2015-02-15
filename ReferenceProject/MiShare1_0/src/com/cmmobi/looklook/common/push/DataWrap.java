package com.cmmobi.looklook.common.push;

public class DataWrap {
	public DataWrap(byte[] src, int len) {
		// TODO Auto-generated constructor stub
		this.buf = null;
		this.len = 0;
		
		if(len>0 && src!=null){
			buf = new byte[len];
			System.arraycopy(src, 0, buf, 0, len);
			this.len = len;
		}

	}
	
	public byte[] buf;
	public int len;

}
