package cn.zipper.framwork.io.network;

import java.io.InputStream;

import cn.zipper.framwork.io.base.ZInputStreamReader;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public final class ZHttpReader extends ZInputStreamReader {
	
	private InputStream inputStream;

	public ZHttpReader(InputStream inputStream, OnPercentChangedListener listener) {
		super(listener);
		this.inputStream = inputStream;
	}

	@Override
	protected void createInputStream() {
	}
	
	@Override
	public long getAvailable() {
		long available = 0;
		try {
			available = inputStream.available();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return available;
	}

	@Override
	protected int read(byte[] bytes) throws Exception {
		return inputStream.read(bytes);
	}

	@Override
	public void close() {
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void skip(long byteCount) throws Exception {
		if (inputStream != null) {
			inputStream.skip(byteCount);
		}
	}

}
