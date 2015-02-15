package cn.zipper.framwork.canvas;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import cxx.oxy.engine.core.Shell;
import cxx.oxy.engine.game.GameLogic;
import cxx.oxy.engine.graphics.BitmapLoader;
import cxx.oxy.engine.utils.ActivityUtils;

public class OxyEngineActivityBase extends Activity {
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.setActivityToFullScreen(this);//ȫ��;
		ActivityUtils.setActivityToNoTitle(this);//�ޱ���;
        Log.e("", "*************** Activity : onCreate() ***************");
    }
    
    @Override
   	protected void onStart() {
    	super.onStart();
    	start();
    	Log.e("", "*************** Activity : onStart() ***************");
    };
    
    @Override
   	protected void onDestroy() {
    	super.onDestroy();
    	Log.e("", "*************** Activity : onDestroy() ***************");
    };
    
   	@Override
   	protected void onPause() {
   		super.onPause();
   		Log.e("", "*************** Activity : onPause() ***************");
   	};
   	
   	@Override
   	protected void onRestart() {
   		super.onRestart();
   		Log.e("", "*************** Activity : onRestart() ***************");
   	};
   	
   	@Override
   	protected void onResume() {
   		super.onResume();
   		Log.e("", "*************** Activity : onResume() ***************");
   	};
   	
   	@Override
   	protected void onStop() {
   		super.onStop();
   		Log.e("", "*************** Activity : onStop() ***************");
   	};
   	
   	
    public void start(){
    	BitmapLoader.init(this);
    	Shell.createNewInstance(this, 250, 0xFF000000);
		if(!GameLogic.hasInstance()){
			GameLogic.createNewInstance();
		}
		Shell.getInstance().setCurrent(GameLogic.getInstance());
		setContentView(Shell.getInstance());
    	
    	GLSurfaceView glSurfaceView = new GLSurfaceView(this);
    	glSurfaceView.setRenderer(new CubeRenderer(false));
    	setContentView(glSurfaceView);
    }
    
    
}