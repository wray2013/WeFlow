package com.cmmobi.looklook.animation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class AnimationHelper{
	ViewGroup mContainer;
	View view_first;
	View view_second;
	
	public AnimationHelper(ViewGroup container, View v_first, View v_second){
		mContainer = container;
		view_first = v_first;
		view_second = v_second; 
		
	}
	
	
    /**
    * Setup a new 3D rotation on the container view.
    *
    * @param position the item that was clicked to show a picture, or - 1 to show the list
    * @param start the start angle at which the rotation must begin
    * @param end the end angle of the rotation
    */
   public  void applyRotation(int position, float start, float end) {
       // Find the center of the container
       final float centerX = mContainer.getWidth() / 2.0f;
       final float centerY = mContainer.getHeight() / 2.0f;

       // Create a new 3D rotation with the supplied parameter
       // The animation listener is used to trigger the next animation
       final Rotate3dAnimation rotation =
               new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true );
       rotation.setDuration(500);
       rotation.setFillAfter( true );
       rotation.setInterpolator( new AccelerateInterpolator());
       rotation.setAnimationListener( new DisplayNextView(position));

       mContainer.startAnimation(rotation);
   }
   
   /**
   * This class listens for the end of the first half of the animation.
   * It then posts a new action that effectively swaps the views when the container
   * is rotated 90 degrees and thus invisible.
   */
  private final class DisplayNextView implements Animation.AnimationListener {
      private final int mPosition;

      private DisplayNextView(int position) {
          mPosition = position;
      }

      public void onAnimationStart(Animation animation) {
      }

      public void onAnimationEnd(Animation animation) {
          mContainer.post(new SwapViews( mPosition));
      }

      public void onAnimationRepeat(Animation animation) {
      }
  }
  
  /**
  * This class is responsible for swapping the views and start the second
  * half of the animation.
  */
 private final class SwapViews implements Runnable {
     private final int mPosition;

     public SwapViews( int position) {
         mPosition = position;
     }

     public void run() {
         final float centerX = mContainer.getWidth() / 2.0f;
         final float centerY = mContainer.getHeight() / 2.0f;
         Rotate3dAnimation rotation;
        
         if (mPosition > -1) {
        	 view_first.setVisibility(View.GONE);
        	 view_second.setVisibility(View.VISIBLE);
        	 view_second.requestFocus();

             rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false );
         } else {
        	 view_second.setVisibility(View.GONE);
             view_first.setVisibility(View.VISIBLE);
             view_first.requestFocus();

             rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false );
         }

         rotation.setDuration(500);
         rotation.setFillAfter( true );
         rotation.setInterpolator( new DecelerateInterpolator());

         mContainer.startAnimation(rotation);
     }
 }



}