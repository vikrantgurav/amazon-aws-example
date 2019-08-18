package com.amazon.example.request;

import javax.validation.constraints.NotEmpty;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeRequest {
	
	@NotEmpty
	@JsonProperty("empId")
	String empId;
	
	@Nullable
	@JsonProperty("name")
	String name;
	
	@Nullable
	@JsonProperty("gender")
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
		return "EmployeeRequest [empId=" + empId + ", name=" + name + ", gender=" + gender + "]";
	}

	
}
