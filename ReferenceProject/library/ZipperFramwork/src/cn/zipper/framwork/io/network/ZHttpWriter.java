package cn.zipper.framwork.io.network;

import java.io.OutputStream;

import cn.zipper.framwork.io.base.ZOutputStreamWriter;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public final class ZHttpWriter extends ZOutputStreamWriter {
	
	private OutputStream outputStream;

	public ZHttpWriter(OutputStream outputStream, OnPercentChangedListener listener) {
		super(listener);
		this.outputStream = outputStream;
	}

	@Override
	protected void createOutputStream() {
	}

	@Override
	protected void write(byte[] bytes, int offset, int length) throws Exception {
		outputStream.write(bytes, offset, length);
	}

	@Override
	public void close() {
		try {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
				outputStream = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
