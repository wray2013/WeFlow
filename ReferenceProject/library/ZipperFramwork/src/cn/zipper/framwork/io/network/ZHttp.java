package cn.zipper.framwork.io.network;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.text.TextUtils;

public final class ZHttp {
	
	private static final int TIMEOUT = 60000;
	
	private URI uri;
	private DefaultHttpClient client;
	private HashMap<String, String> headers;

	public ZHttp() {

	}
	
	public HttpResponse get(String url) {
		return get(url, null);
	}

	public HttpResponse get(String url, String encoding, NameValuePair... nameValuePairs) {
		HttpRequestBase request = null;
		HttpResponse response = null;
		
		try {
			uri = new URI(url);
			request = createHttpGetRequest(url, encoding, nameValuePairs);
			client = createClient();
			response = client.execute(request);
		} catch (Exception e) {
			e.printStackTrace();
			if (request != null) {
				request.abort();
			}
		}
		return response;
	}
	
	public HttpResponse post(String url, HttpEntity entity) {
		return post(url, null, entity);
	}
	
	public HttpResponse post(String url, String encoding, NameValuePair... nameValuePairs) {
		return post(url, encoding, null, nameValuePairs);
	}
	
	public HttpResponse post(String url, String encoding, HttpEntity entity, NameValuePair... nameValuePairs) {
		HttpRequestBase request = null;
		HttpResponse response = null;
		
		try {
			uri = new URI(url);
			request = createHttpPostRequest(url, encoding, entity, nameValuePairs);
			client = createClient();
			response = client.execute(request);
		} catch (Exception e) {
			e.printStackTrace();
			if (request != null) {
				request.abort();
			}
		}
		return response;
	}
	
	private HttpGet createHttpGetRequest(String url, String encoding, NameValuePair... nameValuePairs) {
		String query = encodeGetParameters(stripNulls(nameValuePairs), encoding);
		if (!TextUtils.isEmpty(query)) {
			url += "?" + query;
		}
		HttpGet httpGet = new HttpGet(url);
		setHeaders(httpGet);
		return httpGet;
	}

	private HttpPost createHttpPostRequest(String url, String encoding, HttpEntity entity, NameValuePair... nameValuePairs) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		setHeaders(httpPost);

//		MultipartEntity entity = new MultipartEntity();
//		entity.addPart("param1",new StringBody("中国", Charset.forName("UTF-8")));
//		entity.addPart("param2",new StringBody("value2", Charset.forName("UTF-8")));
//		entity.addPart("param3",new FileBody(new File("C:\\1.txt")));
		if (entity != null) {
			httpPost.setEntity(entity);
		} else {
			httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(nameValuePairs), encoding));
		}
		return httpPost;
	}
	
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	private void setHeaders(HttpRequestBase request) {
		if (headers != null) {
			Set<String> set = headers.keySet();
			for(String string : set) {
				request.addHeader(string, headers.get(string));
			}
		}
	}
	
	private DefaultHttpClient createClient() {
		HttpParams httpParams = new BasicHttpParams();
		
        HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        ZProxy proxy = ZProxy.getZProxy();
        if (proxy != null) {
        	ConnRouteParams.setDefaultProxy(httpParams, proxy.toHttpHostObject());
        }
        
        int port = 80;
        if (uri.getPort() > -1) {
        	port = uri.getPort();
        }
        SchemeRegistry supportedSchemes = new SchemeRegistry();
        supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), port));
        
        SingleClientConnManager singleClientConnManager = new SingleClientConnManager(httpParams, supportedSchemes);
        return new DefaultHttpClient(singleClientConnManager, httpParams);
	}
	
	private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
    	List<NameValuePair> params = null;
    	if (nameValuePairs != null) {
    		params = new ArrayList<NameValuePair>();
            for (int i = 0; i < nameValuePairs.length; i++) {
                NameValuePair param = nameValuePairs[i];
                if (param.getValue() != null) {
                    params.add(param);
                }
            }
    	}
        return params;
    }
	
	private String encodeGetParameters(List<? extends NameValuePair> parameters, String encoding) {
		String string = null;
		if (parameters != null && encoding != null) {
			string = URLEncodedUtils.format(parameters, encoding);
			string = string.replace("+", "%20");
		}
		return string;
	}
	
	public void shutdown() {
		if (client != null) {
			client.getConnectionManager().shutdown();
		}
	}

}
