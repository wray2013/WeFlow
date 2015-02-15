package com.cmmobi.looklook.common.push;

public class PushCommand {

	public int sync;
	public String productID;
	public String micshareid;
	public int timeOut;

	public PushCommand(int sync, String productID, String micshareid,
			int timeOut) {
		// TODO Auto-generated constructor stub
		this.sync = sync;
		this.productID = productID;
		this.micshareid = micshareid;
		this.timeOut = timeOut;
	}

}
