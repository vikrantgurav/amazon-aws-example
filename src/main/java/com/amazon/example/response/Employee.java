package com.amazon.example.response;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class Employee implements Serializable{

	String empId;
	String name;
	String gender;
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", name=" + name + ", gender=" + gender + "]";
	}
	
	
}
