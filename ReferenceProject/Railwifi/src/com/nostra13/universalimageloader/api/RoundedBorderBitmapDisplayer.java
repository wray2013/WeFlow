/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.api;

import android.graphics.*;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Can display bitmap with rounded corners. This implementation works only with ImageViews wrapped
 * in ImageViewAware.
 * <br />
 * This implementation is inspired by
 * <a href="http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/">
 * Romain Guy's article</a>. It rounds images using custom drawable drawing. Original bitmap isn't changed.
 * <br />
 * <br />
 * If this implementation doesn't meet your needs then consider
 * <a href="https://github.com/vinc3m1/RoundedImageView">RoundedImageView</a> or
 * <a href="https://github.com/Pkmmte/CircularImageView">CircularImageView</a> projects for usage.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public class RoundedBorderBitmapDisplayer implements BitmapDisplayer {

	protected int cornerRadius;
	protected static int borderPixels;
	protected int margin;
	protected static int stokeColor;

	public RoundedBorderBitmapDisplayer(int cornerRadiusPixels, int borderColor, int borderPixels) {
		this(cornerRadiusPixels, 0, borderColor, borderPixels);
	}

	public RoundedBorderBitmapDisplayer(int cornerRadiusPixels, int marginPixels, int _stokeColor, int _borderPixels) {
		this.cornerRadius = cornerRadiusPixels;
		borderPixels = _borderPixels;
		this.margin = marginPixels;
		stokeColor = _stokeColor;
		if(cornerRadius<cornerRadiusPixels){
			this.cornerRadius = cornerRadiusPixels+1;
		}
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}

		imageAware.setImageDrawable(new RoundedDrawable(bitmap, cornerRadius, margin));
	}

	public static class RoundedDrawable extends Drawable {

		protected final float cornerRadius;
		protected final int margin;

		protected final RectF mRect = new RectF(),
				mBitmapRect;
		protected final RectF qRect = new RectF();
		protected final BitmapShader bitmapShader;
		protected Paint paint;
		private Paint qPaint;

		public RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) {
			this.cornerRadius = cornerRadius;
			this.margin = margin;

			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapRect = new RectF (margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
			
			paint = new Paint();
//			paint.setColor(stokeColor);
			paint.setStrokeWidth(0);
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
			
			qPaint = new Paint();    //创建一个画笔对象
			qPaint.setAntiAlias(true);    //消除锯齿
			qPaint.setStyle(Style.STROKE);    //设置画笔风格为描边
			qPaint.setStrokeWidth(borderPixels);    //设置描边的宽度为4
//			qPaint.setStrokeJoin(Join.ROUND);
//			qPaint.setStrokeCap(Cap.ROUND);
//			qPaint.setStrokeMiter(90);
			qPaint.setColor(stokeColor);    //设置画笔的颜色为绿色
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(margin+borderPixels, margin+borderPixels, bounds.width() - margin-borderPixels, bounds.height() - margin-borderPixels);
			qRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);
			
			// Resize the original bitmap to fit the new bound
			Matrix shaderMatrix = new Matrix();
			shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
			bitmapShader.setLocalMatrix(shaderMatrix);
			
		}

		@Override
		public void draw(Canvas canvas) {
//			canvas.drawRect(mRect, paint);
			canvas.drawRoundRect(mRect, cornerRadius-borderPixels, cornerRadius-borderPixels, paint);
			canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, qPaint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}
}