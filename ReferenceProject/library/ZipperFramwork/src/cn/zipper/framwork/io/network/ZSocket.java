package cn.zipper.framwork.io.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import cn.zipper.framwork.core.ZLog;

public final class ZSocket {
	
	public static final int STATE_CONNECTED = 0;
	public static final int STATE_GET_DATA = 1;
	public static final int STATE_RETRY = 2;
	public static final int STATE_ERROR = 3;
	public static final int STATE_CLOSED = 4;
	
	private ArrayList<String> data;
	private ZSocketListener zsl;
	private Socket socket;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private Reader reader;
	private Writer writer;
	private String url;
	private int port;
	private boolean keepAlive;
	
	public ZSocket(ZSocketListener zsl, String url, int port, boolean keepAlive) {
		this.zsl = zsl;
		this.url = url;
		this.port = port;
		this.keepAlive = keepAlive;
		
		this.data = new ArrayList<String>();
	}
	
	public void connect() {
		try{
			socket = new Socket(url, port);
			socket.setKeepAlive(keepAlive);
			
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			
			notifyListener(STATE_CONNECTED, null);
			
			reader = new Reader();
			reader.work();
			
			writer = new Writer();
			writer.work();
		} catch (Exception e) {
			e.printStackTrace();
			notifyListener(STATE_ERROR, null);
			clean();
		}
		
	}
	
	public void send(String string) {
		data.add(string);
	}
	
	public void close() {
		if (reader != null) {
			reader.running = false;
		}
		if (writer != null) {
			writer.running = false;
		}
	}
	
	private void clean() {
		try {
			if (socket != null 
					&& dataInputStream == null 
					&& dataOutputStream == null) {
				socket.close();
				socket = null;
				notifyListener(STATE_CLOSED, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void notifyListener(int state, String data) {
		if (zsl != null) {
			zsl.OnStateChanged(state, data);
		}
	}
	
	private class Reader {
		
		private boolean running = true;
		
		private void work() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						while (running) {
							String string = dataInputStream.readUTF();
							if (string != null) {
								notifyListener(STATE_GET_DATA, string);
								continue;
							} else {
								Thread.yield();
							}
						}
						
					} catch (Exception e) {
						ZLog.alert();
						ZLog.e();
						e.printStackTrace();
						notifyListener(STATE_ERROR, null);
						close();
					} finally {
						clean();
						ZSocket.this.clean();
					}
				}
			}).start();
		}
		
		private void clean() {
			try {
				if (dataInputStream != null) {
					dataInputStream.close();
					dataInputStream = null;
				}
			} catch (Exception e) {
				ZLog.alert();
				ZLog.e();
			}
		}
	}
	
	private class Writer {
		
		private boolean running = true;
		
		private void work() {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						while (running) {
							if (data.size() > 0) {
								String string = data.remove(0);
								dataOutputStream.writeUTF(string);
								dataOutputStream.flush();
								continue;
							} else {
								Thread.yield();
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						ZLog.alert();
						ZLog.e();
						notifyListener(STATE_ERROR, null);
						close();
					} finally {
						clean();
						ZSocket.this.clean();
					}
				}
			}).start();
		}
		
		private void clean() {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
					dataOutputStream = null;
				}
			} catch (Exception e) {
				ZLog.alert();
				ZLog.e();
			}
		}
	}
	
	public static interface ZSocketListener {
		
		public abstract void OnStateChanged(int state, String data);
		
	}
	
}
