package br.unifesp.ict.seg.smis.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityInfo {
	
	private String projectName;
	private String className;
	private String methodName;
	private String params;
	private String projectType;
	private Integer entityId;
	
	private String returnType;
	
	public void fillClassAndMethod(String fqn) {
		int pos = fqn.lastIndexOf(".");
		className = fqn.substring(0, pos);
		methodName = fqn.substring(pos + 1);
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public List<String> getParamList() {
		String aux = params.replace("(", "").replace(")", "");
		List<String> paramList =  new ArrayList<>(Arrays.asList(aux.split(",")));
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

	public String getParams() {
		return params;
	}
	
	public void setParams(String params) {
		this.params = params;
	}

	public String getProjectType() {
		return projectType;
	}
	
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	
	public Integer getEntityId() {
		return entityId;
	}
	
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
}
