package com.cmmobi.looklook.common.utils;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontFitTextView extends TextView {

	public FontFitTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FontFitTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void refitText(String text, int textWidth) {
		if (textWidth > 0) {
			float availableWidth = textWidth - this.getPaddingLeft()
					- this.getPaddingRight();

			TextPaint tp = getPaint();
			Rect rect = new Rect();
			tp.getTextBounds(text, 0, text.length(), rect);
			float size = rect.width();

			if (size > availableWidth)
				setTextScaleX(availableWidth / size);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		refitText(this.getText().toString(), parentWidth);
		this.setMeasuredDimension(parentWidth, parentHeight);
	}

	@Override
	protected void onTextChanged(final CharSequence text, final int start,
			final int before, final int after) {
		refitText(text.toString(), this.getWidth());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != oldw) {
			refitText(this.getText().toString(), w);
		}
	}

}
