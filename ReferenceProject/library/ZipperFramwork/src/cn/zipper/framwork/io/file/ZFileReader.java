package cn.zipper.framwork.io.file;

import java.io.File;
import java.io.FileInputStream;

import cn.zipper.framwork.io.base.ZInputStreamReader;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public final class ZFileReader extends ZInputStreamReader {
	
	private File file;
	private FileInputStream fileInputStream;
	
	
	public ZFileReader(File file, OnPercentChangedListener listener) {
		super(listener);
		this.file = file;
		createInputStream();
	}
	
	public ZFileReader(String path, OnPercentChangedListener listener) {
		super(listener);
		this.file = new File(path);
		createInputStream();
	}
	
	public ZFileReader(FileInputStream fileInputStream, OnPercentChangedListener listener) {
		super(listener);
		this.fileInputStream = fileInputStream;
		createInputStream();
	}

	@Override
	protected void createInputStream() {
		try {
			if (fileInputStream == null) {
				fileInputStream = new FileInputStream(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public long getAvailable() {
		long available = 0;
		try {
			available = fileInputStream.available();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return available;
	}

	@Override
	protected int read(byte[] bytes) throws Exception {
		return fileInputStream.read(bytes);
	}

	@Override
	public void close() {
		try {
			if (fileInputStream != null) {
				fileInputStream.close();
				fileInputStream = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void skip(long byteCount) throws Exception {
		try {
			if (fileInputStream != null) {
				fileInputStream.skip(byteCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
