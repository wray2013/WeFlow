package net.zkbc.framework.fep.mdef.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cfg_valuetype")
public class ValueType {

	private String dbType;

	private String javaType;

	private String objcType;

	@Id
	@Column(length = 20)
	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	@Column(length = 20)
	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	@Column(length = 20)
	public String getObjcType() {
		return objcType;
	}

	public void setObjcType(String objcType) {
		this.objcType = objcType;
	}
}
