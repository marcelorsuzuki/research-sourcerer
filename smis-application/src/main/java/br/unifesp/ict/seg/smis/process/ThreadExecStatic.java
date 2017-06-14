package br.unifesp.ict.seg.smis.process;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThreadExecute implements Runnable {
	
	private Class<?> myClass;
	private Object[] values;
	private Method method;
	
	private volatile Object obj = null;
	
	
	public ThreadExecute(Class<?> myClass, Object[] values, Method method) {
		this.myClass = myClass;
		this.values = values;
		this.method = method;
	}
	
	public void run() {
    	try {
			obj =  method.invoke(myClass, values);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			obj = e;
		}
    }
	
	public Object getObj() {
		return obj;
	}
	

}
