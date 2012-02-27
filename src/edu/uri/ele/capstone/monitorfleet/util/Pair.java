package edu.uri.ele.capstone.monitorfleet.util;

public class Pair<T, U> {
	private final T _t;
	private final U _u;
	
	public Pair(T t, U u){
		_t = t;
		_u = u;
	}
	
	public T getT(){
		return _t;
	}
	
	public U getU(){
		return _u;
	}
}
