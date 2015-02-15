package cn.zipper.framwork.io.file;

import java.io.File;
import java.io.FileOutputStream;

import cn.zipper.framwork.io.base.ZOutputStreamWriter;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public final class ZFileWriter extends ZOutputStreamWriter {
	
	private File file;
	private boolean isAppend;
	private FileOutputStream fileOutputStream;
	
	
	public ZFileWriter(File file, boolean isAppend, OnPercentChangedListener listener) {
		super(listener);
		this.file = file;
		this.isAppend = isAppend;
		createOutputStream();
	}
	
	public ZFileWriter(String path, boolean isAppend, OnPercentChangedListener listener) {
		super(listener);
		this.file = new File(path);
		this.isAppend = isAppend;
		createOutputStream();
	}
	
	public ZFileWriter(FileOutputStream fileOutputStream, boolean isAppend, OnPercentChangedListener listener) {
		super(listener);
		this.fileOutputStream = fileOutputStream;
		this.isAppend = isAppend;
		createOutputStream();
	}

	@Override
	protected void createOutputStream() {
		try {
			if (fileOutputStream == null) {
				fileOutputStream = new FileOutputStream(file, isAppend);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void write(byte[] bytes, int offset, int length) throws Exception {
		fileOutputStream.write(bytes, offset, length);
	}

	@Override
	public void close() {
		try {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
				fileOutputStream = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
