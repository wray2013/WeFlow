package com.cmmobi.looklook.v31.httpproxybak;

import java.net.Socket;

import android.os.Environment;

/**
 * Config
 * @author Ray
 *
 */
public class Config{

	final static public String LOCAL_IP_ADDRESS = "127.0.0.1";
	final static public int HTTP_PORT = 80;
	final static public String HTTP_BODY_END = "\r\n\r\n";
	final static public String HTTP_RESPONSE_BEGIN = "HTTP/";
	final static public String HTTP_REQUEST_BEGIN = "GET ";
	final static public String HTTP_REQUEST_LINE1_END = " HTTP/";
	final static public String CACHE_FILE_PATH = Environment
			.getExternalStorageDirectory() + "/ProxyBuffer/files";

	public static final String HTTPPROXY_RESULT_MSG = "HTTPPROXY.RESULT.MSG";
	public static final int HANDLER_HTTPPROXY_CLEAN    = 0x19861214;
	public static final int HANDLER_HTTPPROXY_PREPARED = 0x19861029;
	public static final int HANDLER_HTTPPROXY_DONE     = 0x19561130;
	
	static public class ProxyRequest {
		/**Http Request 内容*/
		public String _body;
		/**Range的位置*/
		public long _rangePosition;
	}
	
	static public class ProxyRequestWrapper {
		public ProxyRequest pr;
		public Socket s;
		public boolean isSentHeader;
		public ProxyRequestWrapper(ProxyRequest request , Socket socket, boolean isSent) {
			pr = request;
			s = socket;
			isSentHeader = isSent;
		}
	}
	
	static public class ProxyResponse {
		public byte[] _body;
		public byte[] _other;
		public long _currentPosition;
		public long _duration;
	}
	
	static public class ProxyResponseWrapper {
		public ProxyResponse pr;
		public long _offset;
	}
}