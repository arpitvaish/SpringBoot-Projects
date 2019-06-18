package com.application.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "EMPLOYEE")
@XmlRootElement
public class Employee implements Serializable {

	private static final long serialVersionUID = -4680573348497201209L;
	@Id
	@GeneratedValue
	@Column(name = "Id", nullable = false)
	private Long employeeId;

	@Column(name = "name")
	private String employeeName;
	@Column(name = "title")
	private String jobTitle;
	@Column(name = "address")
	private String address;

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
