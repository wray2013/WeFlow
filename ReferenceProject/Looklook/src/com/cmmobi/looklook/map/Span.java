package com.cmmobi.looklook.map;

public class Span {

	public Span(int latspan, int lonspan, int clat, int clon){
		latSpanE6 = latspan;
		longSpanE6 = lonspan;
		clatE6 = clat;
		clonE6 = clon;
	}
	
	public int latSpanE6;
	public int longSpanE6;
	public int clatE6;
	public int clonE6;
}
