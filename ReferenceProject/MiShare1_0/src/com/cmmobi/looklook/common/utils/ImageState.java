package com.cmmobi.looklook.common.utils;

public class ImageState {
	private float left;
	private float right;
	private float top;
	private float bottom;
	private float scale = 1.0f;
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public float getLeft() {
		return left;
	}
	public void setLeft(float left) {
		this.left = left;
	}
	public float getRight() {
		return right;
	}
	public void setRight(float right) {
		this.right = right;
	}
	public float getTop() {
		return top;
	}
	public void setTop(float top) {
		this.top = top;
	}
	public float getBottom() {
		return bottom;
	}
	public void setBottom(float bottom) {
		this.bottom = bottom;
	}
	
	public float getWidth() {
		return right - left;
	}
	
	public float getHeight() {
		return bottom - top;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "left = " + left + " right = " + right + " top = " + top + " bottom = " + bottom;
	}
}
