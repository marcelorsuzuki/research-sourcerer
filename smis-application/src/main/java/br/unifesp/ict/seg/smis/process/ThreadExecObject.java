package br.unifesp.ict.seg.smis.process;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThreadExecObject implements Runnable {
	
	private Class<?> myClass;
	private Object[] values;
	private Method method;
	
	private volatile Object obj = null;
	
	
	public ThreadExecObject(Class<?> myClass, Object[] values, Method method) {
		this.myClass = myClass;
		this.values = values;
		this.method = method;
	}
	
	public void run() {
		try {
    		Object instance = myClass.newInstance();
			obj =  method.invoke(instance, values);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			obj = "Exception: (Thread) " + e.getCause();
		}
    }
	
	public Object getObj() {
		return obj;
	}
	

}
