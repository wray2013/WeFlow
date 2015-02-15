package cn.zipper.framwork.utils;



public final class ZPercent {
	
	private OnPercentChangedListener listener;
	private float maxValue;
	private float currentValue;
	private float percent;
	private Object object;
	
	public ZPercent(OnPercentChangedListener listener) {
		this.listener = listener;
	}
	
	public void setMaxValue(float max) {
		this.maxValue = max;
		notifyListener();
	}
	
	public float getMaxValue() {
		return maxValue;
	}
	
	public float getCurrentValue() {
		return currentValue;
	}
	
	public void setCurrentValue(float current, Object object) {
		this.currentValue = current;
		this.object = object;
		notifyListener();
	}
	
	public void setCurrentValueStep(float current, Object object) {
		this.currentValue += current;
		this.object = object;
		notifyListener();
	}
	
	public float getPercent() {
		if (maxValue > 0) {
			percent = currentValue/maxValue * 100;
		}
		return percent;
	}
	
	public int getPercentInt() {
		return (int) getPercent();
	}
	
	/**
	 * 是否已经达到100%;
	 * @return
	 */
	public boolean isOneHundredPercent() {
		return getPercent() == 100.0f;
	}
	
	public Object getObject() {
		return object;
	}
	
	private void notifyListener() {
		if (listener != null) {
			listener.onPercentChanged(this);
		}
	}
	
	public static interface OnPercentChangedListener {
		public void onPercentChanged(ZPercent percent);
	}

}
