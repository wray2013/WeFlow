package cn.zipper.framwork.io.network;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.text.TextUtils;

import cn.zipper.framwork.core.ZLog;

public final class ZHttpResponse {
	
	private int responseCode;
	private Map<String, List<String>> headers;
	private InputStream inputStream;
	
	public ZHttpResponse() {
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getHeader(String key) {
		String value = null;
		if (headers.containsKey(key)) {
			List<String> list = headers.get(key);
			for (String string : list) {
				if (!TextUtils.isEmpty(string)) {
					value = string;
					break;
				}
			}
		}
		return value;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public void printHeaders() {
		ZLog.e("response headers:");
		Set<String> set = headers.keySet();
		for (String string : set) {
			List<String> list = headers.get(string);
			String header = string;
			if (list.size() == 1) {
				header += " = " + list.get(0);
				ZLog.e(header);
			} else {
				header += " : ";
				ZLog.e(header);
				for(String string2 : list) {
					ZLog.e("     value: " + string2);
				}
			}
		}
	}

}
