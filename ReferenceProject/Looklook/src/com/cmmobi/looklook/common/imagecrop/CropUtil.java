package com.cmmobi.looklook.common.imagecrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.Environment;

public class CropUtil {
	/**
	 * ѹ��ͼƬ������ͼƬ�ֽ����
	 * @param b		����ѹ����ͼƬ
	 * @param len	��ָ����ѹ���������Ȼ�߶�
	 * @param size	:ָ����ѹ������������
	 * @return
	 */
	public static byte[] compressPhotoByte(Bitmap b, int len, int maxSize){
		int w = b.getWidth();
        int h = b.getHeight();
        float s;
        if(w<len && h<len){
        	s = 1;
        }
        if(w>h){
        	s = (float)len/w; 
        }else{
        	s = (float)len/h;
        }
    	Matrix matrix = new Matrix();  
        matrix.postScale(s, s);  
        //ѹ��ͼƬ
        Bitmap newB = Bitmap.createBitmap(b , 0, 0, w, h, matrix, false); 
        //��ѹ�����ͼƬת��Ϊ�ֽ����飬����ֽ������С����200K������ѹ��
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int qt = 70;
        newB.compress(CompressFormat.JPEG, qt, bos);
        int size = bos.size();
        while(qt!=0 && size>maxSize){
        	if(qt<0)
        		qt = 0;
        	bos.reset();
        	newB.compress(CompressFormat.JPEG, qt, bos);
        	size = bos.size();
    		qt -= 10;
        }
        System.out.println("ѹ�����ͼƬ��С��"+bos.size());
        return bos.toByteArray();
	}
	
	/**
	 * �ر�IO��
	 * @param in
	 * @param out
	 */
	public static void closeIO(InputStream in, OutputStream out){
		try {
			if(in!=null)
				in.close();
			if(out!=null)
				out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����ͼƬ�����ش洢��
	 * @param in		��������
	 * @param nameKey	���ļ����
	 */
	public static File makeTempFile(Bitmap photo, String nameKey){
		//�ж��Ƿ��д洢��
		String status=Environment.getExternalStorageState();
		if(!status.equals(Environment.MEDIA_MOUNTED))
			throw new RuntimeException("û�д洢��");
		//�ȱ���ѹ��ͼƬ�����ϳ���һ��ѹ����600pxһ�£��������������200K
		byte[] tempData = CropUtil.compressPhotoByte(photo, 600, 200*1024);
		//��ѹ�����ͼƬ���浽�洢����Ŀ¼�£�Ȩ�ޣ�
		File bFile = new File(Environment.getExternalStorageDirectory(), nameKey);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(bFile);
			fos.write(tempData);
			fos.flush();
			if(bFile.exists() && bFile.length()>0)
				return bFile;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			CropUtil.closeIO(null, fos);
		}
		return null;
	}
}
