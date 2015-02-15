package com.cmmobi.sns.exceptions;
/**
 * 所有分享平台api访问返回值错误包装类
 * @author xudongsheng
 *
 */
public class WeiboError extends RuntimeException {

	private static final long serialVersionUID = -7614264285563089016L;
	
	private int mErrorCode;
	private int mResponseCode;
	private String mResponse;

	public WeiboError(String errorMessage) {
		super(errorMessage);
	}

	public WeiboError(int errorCode, String errorMessage, int responseCode,
			String response) {
		super(errorMessage);
		mErrorCode = errorCode;
		mResponseCode = responseCode;
		mResponse = response;
	}
	
	public WeiboError(int errorCode, String errorMessage,
			String response) {
		super(errorMessage);
		mErrorCode = errorCode;
		mResponse = response;
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public int getResponseCode() {
		return mResponseCode;
	}

	public String getResponse() {
		return mResponse;
	}

	@Override
	public String toString() {
		return "errorCode:" + mErrorCode + "\nerrorMessage:"
				+ this.getMessage() + "\nresponse code:" + mResponseCode + "\nresponse:"
				+ mResponse;
	}
}
