package effect;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * 
 * @author xpg-2007
 *
 */

public interface EffectType {
	//特效id
    public static int KEF_TYPE_NOTHING = 0;
    public static int KEF_TYPE_GRAY = 1;//              //灰度特效
    public static int KEF_TYPE_ADDRGB = 2;//            //增强颜色值特效
    public static int KEF_TYPE_BW = 3;//                //黑白特效
    public static int KEF_TYPE_LUM_CT = 4;//            //亮度对比度特效
    public static int KEF_TYPE_LUM_PALETTE = 5;//            //调色板特效
        
    public static int KEF_TYPE_TWIRL = 6;//             //扭曲特效
    public static int KEF_TYPE_PINCH = 7;//             //凹特效  
    public static int KEF_TYPE_POUCH = 8;//             //凸特效  
    public static int KEF_TYPE_FLASH = 9;//             //放射特效
        
    public static int KEF_TYPE_FLIP = 10;//              //上下颠倒特效
    public static int KEF_TYPE_FOLD_LR = 11;//           //左右折叠
    public static int KEF_TYPE_FOLD_TB = 12;//           //上下折叠
    public static int KEF_TYPE_MOSAIC = 13;//            //马赛克特效
    public static int KEF_TYPE_GRAYFILM = 14;//          //黑白底片效果
        
    public static int KEF_TYPE_RELIEVO = 15;//           //浮雕特效
    public static int KEF_TYPE_SKETCH = 16;//            //素描特效
    public static int KEF_TYPE_PAINTING = 17;//          //油画效果
        
    public static int KEF_TYPE_LIGHTRING = 18;//         //光斑效果
        
    public static int KEF_TYPE_PHOTOFRAME = 19;//        //相框
    public static int KEF_TYPE_ANIMATION = 20;//         //动画
    
    public static int KEF_TYPE_BRIGHTEN = 21;//          //变亮叠加效果
    
    public static int KEF_TYPE_AUDIOSYNTHESIS = 22;//    //音频合成
        
    public static int KEF_TYPE_ZOOM = 23;//              //数字变焦  [暂时算一个特效]
    public static int KEF_TYPE_CWR_90 = 24;//             //顺时针转90+翻转  【暂时先占一个位置吧】
    
    
  //旋转角度
    public static int MY_PI_ZERO = 0;          	//旋转0度
    public static int MY_PI_1P2  = 1;           //旋转90度
    public static int MY_PI_     = 3;           //旋转180度
    public static int MY_PI_3P2  = 4;           //旋转270度
    
    //视频采集分辨率大小
    //public static int VIDEO_WIDTH = 480;
    //public static int VIDEO_HEIGHT = 320;
    public static int VIDEO_WIDTH = 640;
    public static int VIDEO_HEIGHT = 480;

    public static final int VIDEO_FORMATRATE = 15;
    
    //音频采集相关参数
    // 音频获取源 
   public static final int  KAUDIO_SOURCE = MediaRecorder.AudioSource.MIC; 
   // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025 
   public static final int SAMPLERATE_IN_HZ = 44100; 
   // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道 
   public static final int  CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO; 
   // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。 
   public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; 
}
