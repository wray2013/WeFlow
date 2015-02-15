package cn.zipper.framwork.utils;

import java.util.Observable;
import java.util.Observer;

public final class ZObserverManager extends Observable {
	
	public ZObserverManager() {
	}
	
	public void addObserver(Observer observer) {
		this.addObserver(observer);
	}
	
	public void deleteObserver(Observer observer) {
		this.deleteObserver(observer);
	}
	
	public void deleteAllObserver() {
		this.deleteObservers();
	}
	
	public void notifyObservers(Object data) {
		this.setChanged();
		this.notifyObservers(data);
	}
	
	public void notifyObservers() {
		this.setChanged();
		this.notifyObservers();
	}
	
	
	
}