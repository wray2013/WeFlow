package net.zkbc.framework.fep.commons.controller;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.junit.Test;
import org.springframework.util.FileCopyUtils;

public class FileUploadControllerTest {

	private static final int TIMEOUT = 30 * 1000;
	private static final String CHARSET = "UTF-8";

	@Test
	public void testFileupload() {
		try {
			String uuid = connect("http://localhost:9080/fileupload/jpg",
					FileCopyUtils.copyToByteArray(new File(
							"D:\\docs\\pictures\\z1015\\100_0020.JPG")));
			assertNotNull(uuid);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Test
	public void testFormupload() {
		String uuid = uploadFile(new File(
				"D:\\docs\\pictures\\z1015\\100_0020.JPG"),
				"http://localhost:9080/formupload");
		assertNotNull(uuid);
	}

	/**
	 * @author mingfanglin
	 */
	private String connect(String urlString, byte[] bitmapByte) {
		// Common.log("url = " + urlString);
		String result = null;
		URL url = null;
		HttpURLConnection conn = null;

		OutputStream os = null;
		try {
			url = new URL(urlString);
			if (conn == null) {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setConnectTimeout(TIMEOUT);// 设置超时
			conn.setReadTimeout(TIMEOUT); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", "application/octet-stream");

			byte[] buf = new byte[128];
			int ch = -1;
			int count = 0;
			// InputStream fileIs = Common.read(context, fileName);
			InputStream bitmapIS = new ByteArrayInputStream(bitmapByte);
			os = conn.getOutputStream();
			long length = bitmapIS.available();
			while ((ch = bitmapIS.read(buf)) != -1) {
				os.write(buf, 0, ch);
				count += ch;
				if (length - count > 0) {
					// 如果知道响应的长度，调用publishProgress（）更新进度
					// publishProgress((int) ((count / (float) length) * 100));
				}
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = conn.getInputStream();
			byte[] temp = new byte[256];
			int len = 0;
			while ((len = is.read(temp)) != -1) {
				baos.write(temp, 0, len);
			}

			byte[] dataResult = baos.toByteArray();
			// Common.log("data Length = " + dataResult.length);
			result = new String(dataResult, CHARSET);
			bitmapIS.close();
			bitmapIS = null;
			is.close();
			is = null;

			if (os != null) {
				os.close();
				os = null;
			}
			conn = null;
			url = null;
		} catch (Exception e) {
			new RuntimeException(e.getMessage(), e);
		}
		// Common.log("result = " + result);
		return result;
	}

	/**
	 * @author hekangchong
	 */
	private String uploadFile(File file, String RequestURL) {
		String res = "0";
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT);
			conn.setConnectTimeout(TIMEOUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);

			if (file != null) {
				/**
				 * 当文件不为空时执行上传
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名
				 */

				sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int resInt = conn.getResponseCode();
				// Log.e(TAG, "response code:" + resInt);
				res = Integer.toString(resInt);
				System.out.println("================" + res);
				if (resInt == 200) {
					// Log.e(TAG, "request success");
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
					// Log.e(TAG, "result : " + result);
				} else {
					// Log.e(TAG, "request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
