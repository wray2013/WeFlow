package cn.zipper.framwork.utils;

public final class ZBooleanBox {
	
	public static final int STATE_NULL = 0;
	public static final int STATE_TRUE = 1;
	public static final int STATE_FALSE = 2;
	
	private int state;
	
	
	
	public void setStateToNull() {
		state = STATE_NULL;
	}
	
	public void setStateToTrue() {
		state = STATE_TRUE;
	}
	
	public void setStateToFalse() {
		state = STATE_FALSE;
	}
	
	public int getStateVaule() {
		return state;
	}
	
	public boolean isNull() {
		return state == STATE_NULL;
	}
	
	public boolean isTrue() {
		return state == STATE_TRUE;
	}
	
	public boolean isFalse() {
		return state == STATE_FALSE;
	}
}
