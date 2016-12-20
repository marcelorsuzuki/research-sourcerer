package br.unifesp.ict.seg.smis.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityInfo {
	
	private String projectName;
	private String className;
	private String methodName;
	private List<String> paramList;
	private String returnType;
	
	public void fillClassAndMethod(String fqn) {
		int pos = fqn.lastIndexOf(".");
		className = fqn.substring(0, pos);
		methodName = fqn.substring(pos + 1);
	}
	
	public void splitParams(String params){
		params = params.replace("(", "").replace(")", "");
		paramList =  new ArrayList<>(Arrays.asList(params.split(",")));
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public List<String> getParamList() {
		return paramList;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectName() {
		return projectName;
	}
	
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getReturnType() {
		return returnType;
	}

}
