package effect;

import android.graphics.Bitmap;
import android.util.Log;

public class Effects {
	static{
		System.loadLibrary("effect_Effects");
	}
	
	private final String tag = "Effects";
	
	public long mList = 0;		//the point of effect.
	public void itest()
	{
		Log.i(tag, "EffectType:" + EffectType.KEF_TYPE_NOTHING + "-" + EffectType.KEF_TYPE_CWR_90);
	}
	public native void test();
	
	public Effects()
	{
		native_CreateEffectList();
	}
	
	
	public Effect NewEffect(int ntype)
	{	
		return new Effect(ntype);
	}
	
	/**
	 * AddEffectsInFrontWithTag 在tag之前插入特效。
	 * @param list 特效列表，用CreateEffectList创建。
	 * @param effect 要插入的特效。用CreateEffect创建。
	 * @param tag 目标位置tag。
	 */
	public int AddEffectsInFrontWithTag(Effect effect, int tag)
	{
		native_AddEffectsInFrontWithTag(effect.mEffect, tag);
		effect.mEffect = 0;
		return 0;
	}
	/**
	 * AddEffectsInBackWithTag 在已知特效之后添加特效。
	 * @param list
	 * @param effect
	 * @param tag
	 */
	public int AddEffectsInBackWithTag(Effect effect, int tag)
	{
		native_AddEffectsInBackWithTag(effect.mEffect, tag);
		effect.mEffect = 0;
		return 0;
	}
	/**
	 * RemoveEffectsUseTag 在已知的特效链list上删除tag为tag的特效。
	 * @param list 特效链。
	 * @param tag 删除的特效tag。
	 */
	public int RemoveEffectsUseTag(int tag)
	{
		native_RemoveEffectsUseTag(tag);
		return 0;
	}
	
	
	
	/**
	 * ReplaceEffectsWithTag 用effect替换掉原来tag的特效
	 * @param list 特效列表。
	 * @param effect 想要替换到list上的特效对象。
	 * @param tag 目标特效tag。
	 */
	public int ReplaceEffectsWithTag(Effect effect, int tag)
	{
		native_ReplaceEffectsWithTag(effect.mEffect, tag);
		effect.mEffect = 0;
		return 0;
	}
	
	public void Release()
	{
		native_DestroyEffectList();
	}
	
	public class Effect{
		public long mEffect;
		private final String tag = "Effect";
		public Effect(int ntype)
		{
			if(native_CreateEffect(ntype) == 0)
			{
				Log.e(tag, "create effect is error.");
			}else
			{
				Log.i(tag, "mEffect=0x" + Long.toHexString(mEffect));
			}
		}
		
		public void Release()
		{
			native_DestroyEffect();
		}
		//创建以及填充单个特效相关函数
		/**
		 * CreateEffect 用该函数来创建特效对象。
		 * @param ntype	特效type。在EffectType类里定义了各种特效的值。
		 * @return	返回一个特效对象  用DestroyEffect销毁。
		 */
		public native long native_CreateEffect(int ntype); 
		/**
		 * DestroyEffect 销毁用CreateEffect创建的特效对象。
		 * @param effect 用CreateEffect创建的特效对象。
		 */
		public native void native_DestroyEffect();
		
		//设置特效变量。effect为特效对象  用CreateEffect创建
		public native void native_SetEffectTag(int tag);  //tag
		public native void native_SetEffectColor(int R, int G, int B); //rgb
		public native void native_SetEffectBWv(int nBw);  //黑白
		public native void native_SetEffectLumAndCon(int nLum, double nCon); //亮度对比度
		public native void native_SetEffectFrame(Bitmap frame);  //相框
		public native void native_SetEffectFactor(double nfactor);	//数字焦距。
		public native void native_SetEffectMosaic(int nMosaic);   //马赛克粒度。
		public native void native_SetProcessRect(int left, int top, int width, int height);  //马斯克/相框等的位置。
		public native void native_SetTableRect(int left, int top, int width, int height);   //凹凸变形特效时设置		
	}
	
	
	//特效列表管理相关函数
	/**
	 * CreateEffectList
	 * @return 返回一个特效列表  该特效列表必须用DestroyEffectList函数销毁。
	 */
	public native long native_CreateEffectList();
	/**
	 * DestroyEffectList
	 * @param list 用CreateEffectList创建的特效列表.
	 */
	public native void native_DestroyEffectList();

	/**
	 * AddEffectsInFrontWithTag 在tag之前插入特效。
	 * @param list 特效列表，用CreateEffectList创建。
	 * @param effect 要插入的特效。用CreateEffect创建。
	 * @param tag 目标位置tag。
	 */
	public native int native_AddEffectsInFrontWithTag(long effect, int tag);
	/**
	 * AddEffectsInBackWithTag 在已知特效之后添加特效。
	 * @param list
	 * @param effect
	 * @param tag
	 */
	public native int native_AddEffectsInBackWithTag(long effect, int tag);
	/**
	 * RemoveEffectsUseTag 在已知的特效链list上删除tag为tag的特效。
	 * @param list 特效链。
	 * @param tag 删除的特效tag。
	 */
	public native int native_RemoveEffectsUseTag(int tag);
	
	/**
	 * ReplaceEffectsWithTag 用effect替换掉原来tag的特效
	 * @param list 特效列表。
	 * @param effect 想要替换到list上的特效对象。
	 * @param tag 目标特效tag。
	 */
	public native int native_ReplaceEffectsWithTag(long effect, int tag);
	
	/**
	 * ProcessBitmap 建立好特效列表后用该函数来用特效列表处理视频流。
	 * @param nv21 输入视频数据。
	 * @param list 用CreateEffectList创建的特效列表.
	 * @param width 视频宽度。
	 * @param height 视频高度。
	 * @param effected 输出画面对象。
	 */
	public native void native_ProcessBitmap(byte[] nv21,int width,int height,Bitmap effected);
	/**
	 * ProcessBitmap 建立好特效列表后用该函数来用特效列表处理视频流。
	 * @param nv21 输入视频数据。
	 * @param list 用CreateEffectList创建的特效列表.
	 * @param width 视频宽度。
	 * @param height 视频高度。
	 * @param rgba 输出的图像rgba8888数据。
	 */
	public native void native_ProcessVideo(byte[] nv21,int width,int height, byte[] rgba);
	
	/**
	 * ProcessVideoRGBA 建立好特效列表后用该函数来用特效列表处理视频流。
	 * @param inRgba 输入视频数据rgba8888。
	 * @param list 用CreateEffectList创建的特效列表.
	 * @param width 视频宽度。
	 * @param height 视频高度。
	 * @param rgba 输出的图像rgba8888数据。
	 */
	public native void native_ProcessVideoRGBA(byte[] inRgba,int width,int height, byte[] rgba);
	
	/**
	 * ProcessAudio 对音频数据进行处理。
	 * @param npcm 音频原始数据
	 * @param size 数据大小。
	 */
	public native void native_ProcessAudio(byte[] npcm, int size);
	
	
	
	//下边函数主要是文件生成方面的。
	
	/**
	 * StartRecoder 开始录制文件。该函数来创建录制对象。
	 * @param list	特效列表对象
	 * @param outfile_name	文件名
	 * @param output	输出文件路径
	 * @param vwidth	视频宽度
	 * @param vheight	视频高度
	 * @param vbitrate	视频码率
	 * @param vframeRate	视频帧率
	 * @param vangle	视频角度
	 * @param abitrate	音频码率
	 * @param achannels	音频声道
	 * @param asampleRate	音频采样率
	 */
	public native int native_StartRecoder(
			String outfile_name,
			String outpath,
            int vwidth,
            int vheight,
            int vbitrate,
            int vframeRate,
            int vangle,
            int abitrate,
            int achannels,
            int asampleRate,
            int bsmall);
	
	/**
	 * StopRecoder 停止写文件  将释放文件写对象
	 * @param list	特效列表对象
	 */
	public native void native_StopRecoder();
	
	public void Notify_BigFile_Done(String filename)
	{
		Log.i(tag, filename + "is done.");
	}
	
	
	//test modle.
	public native byte[] TestByteArray(byte[] tA);
	
	
	//特效实现方法
	public native void TwirlBitmap(byte[] nv21,int width,int height,Bitmap frame,Bitmap effected);
	public native void FrameBitmap(byte[] nv21,int width,int height,Bitmap frame,Bitmap effected);
}
