package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class QuickBarView extends View {

	private static final char[] letters = new char[] { 'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#' };

	private SectionIndexer sectionIndexter;
	private ListView listView;

	private float mDensity;
	private float mIndexbarWidth;
	private float mIndexbarMargin;
	boolean showBkg = false;
	int choose = -1;

	private Paint indexbarContainerPaint = new Paint();
	private Paint paint = new Paint();
	private RectF rectF;

	public QuickBarView(Context context) {
		super(context);
		mDensity = context.getResources().getDisplayMetrics().density;
		mIndexbarWidth = 20 * mDensity;
		indexbarContainerPaint.setAntiAlias(true);
		indexbarContainerPaint.setColor(Color.parseColor("#8f9296"));

	}

	public QuickBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDensity = context.getResources().getDisplayMetrics().density;
		mIndexbarWidth = 20 * mDensity;
		indexbarContainerPaint.setAntiAlias(true);
		indexbarContainerPaint.setColor(Color.parseColor("#8f9296"));
	}

	public QuickBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mDensity = context.getResources().getDisplayMetrics().density;
		mIndexbarWidth = 20 * mDensity;
		indexbarContainerPaint.setAntiAlias(true);
		indexbarContainerPaint.setColor(Color.parseColor("#8f9296"));
	}

	public void setListView(ListView listView) {
		this.listView = listView;
		sectionIndexter = (SectionIndexer) listView.getAdapter();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		final float y = (int) event.getY();
		int currentY = (int) (y / getHeight() * letters.length);

		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			showBkg = true;
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) listView.getAdapter();
			}

			if (currentY > 0 && currentY < letters.length) {
				int position = sectionIndexter
						.getPositionForSection(letters[currentY]);
				listView.setSelection(position);
				invalidate();
			}
		} else {
			showBkg = false;
			invalidate();
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		int height = getHeight();
		int width = getWidth();
		rectF = new RectF(width - mIndexbarMargin - mIndexbarWidth,
				mIndexbarMargin, width - mIndexbarMargin, height
						- mIndexbarMargin);
		if (showBkg) {
			canvas.drawRoundRect(rectF, 5 * mDensity, 5 * mDensity,
					indexbarContainerPaint);
			// canvas.drawColor(Color.parseColor("#8f9296"));
		}
		int singleHeight = height / letters.length;

		float sectionHeight = (rectF.height() - 2 * mIndexbarMargin)
				/ letters.length;
		float paddingTop = (sectionHeight - (paint.descent() - paint.ascent())) / 2;

		for (int i = 0; i < letters.length; i++) {
			paint.setColor(Color.parseColor("#6a7373"));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(24);
			paint.setAntiAlias(true);
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			// float xPos = width / 2 - paint.measureText(b[i]) / 2;
			// float yPos = singleHeight * i + singleHeight;
			// canvas.drawText(b[i], xPos, yPos, paint);

			float paddingLeft = (mIndexbarWidth - paint.measureText(String
					.valueOf(letters[i]))) / 2;
			canvas.drawText(String.valueOf(letters[i]), rectF.left
					+ paddingLeft, rectF.top + mIndexbarMargin + sectionHeight
					* i + paddingTop - paint.ascent(), paint);

			paint.reset();
		}

	}
}
