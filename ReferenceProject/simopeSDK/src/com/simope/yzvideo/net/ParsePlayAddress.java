//package com.simope.yzvideo.net;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import android.content.Context;
//import android.os.Handler;
//import android.util.Log;
//
//
//import com.simope.yzvideo.entity.Video;
//
//public class ParsePlayAddress {	
//	private int http_code = 0;
//	private static int HTTP_IO_ERROR=55;
////	private static int HTTP_TIME_OUT=56;
//
//	public ParsePlayAddress(Context context, Handler handler) {
//	
//
//	}
//
//	public Video parsePlayaddress(Video video) throws ParseAddressError {
//	
//			String contentString = null;
//			for (int i = 0; i <= 1; i++) {
//				contentString = getHtml(video.getPlayAddress(), "utf-8");
//				Log.v("UI", video.getTitle() + ":解析出来的播放地址里的内容：" + contentString
//						+ ",i=" + i);
//				if (contentString != null) {
//					break;
//				}
//				if(i==1&&http_code==0){
//					video.setState(1);
//					return video;
//				}
//			}
//			if (contentString == null) {
//				switch (http_code) {
//				case 404:
//					video.setState(3);
//					break;
//				case 200:
//					video.setState(4);
//					break;
//				case 55:
//					video.setState(0);
//					break;		
//				default:
//					video.setState(http_code);
//					break;
//				}
//			} else {
//				if (!contentString.startsWith("#YZ-Y-LIST")
//						&& contentString.length() > 20) {
//					video.setState(2);
//				} else {
//					if (contentString.length() <= 20 && http_code == 200) {
//						video.setState(4);
//					} else {
//						String[] arr;			
//						if(!contentString.contains("{")||!contentString.contains("}")){
//							video.setState(5);
//							throw new ParseAddressError();
//						}else{
//							int start = contentString.indexOf("{");
//							int end = contentString.indexOf("}");
//							String address = contentString.substring(start + 1,
//									end);
//							if(address.trim().equals("")){
//								video.setState(5);
//								throw new ParseAddressError();
//							}else{
//								arr = address.split(",");
//								if(arr == null || arr.length < 1){
//									video.setPlayAddress(null);
//									video.setState(5);
//									throw new ParseAddressError();
//								}else{
//									for(String addr:arr){
//										if(addr.trim().equals("")){
//											video.setState(5);
//											throw new ParseAddressError();
//										}
//									}								
//									video.setMultiAddress(arr);									
//								}														
//							}											
//						}													
//					}
//				}
//			}		
//		http_code = 0;
//		return video;
//	}
//
//	public String getHtml(String urlpath, String encoding) {
//		Log.v("UI", "没有播放地址，解析播放地址：" + urlpath);
//			try {
//				URL url = new URL(urlpath);
//				// 实例化一个HTTP连接对象conn
//				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//				// 定义请求方式为GET，其中GET的格式需要注意
//				conn.setRequestMethod("GET");
//				conn.setConnectTimeout(30000);
//				conn.setReadTimeout(30000);
//				conn.addRequestProperty("userAgent", "android");
//
//				switch (conn.getResponseCode()) {
//				case 200:
//					http_code = 200;
//					InputStream inStream = conn.getInputStream();
//					byte[] data = readStream(inStream);
//					return new String(data, encoding);				
//				case 404:
//					http_code = 404;
//					break;	
//				default:
//					http_code =conn.getResponseCode();
//					break;
//				}
//			} catch (IOException e) {
//				http_code=HTTP_IO_ERROR;
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		
//		return null;
//	}
//
//	public byte[] readStream(InputStream inStream) throws Exception {
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		int len = -1;
//		// 将输入流不断的读，并放到缓冲区中去。直到读完
//		while ((len = inStream.read(buffer)) != -1) {
//			// 将缓冲区的数据不断的写到内存中去。
//			outStream.write(buffer, 0, len);
//		}
//		outStream.close();
//		inStream.close();
//		return outStream.toByteArray();
//	}
//}
