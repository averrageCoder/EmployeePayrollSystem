package com.bridgelabz.employeepayroll;

public class EmployeePayrollExceptions extends Exception {
	enum ExceptionType {
		SQL_ERROR
	}
	
	ExceptionType type;
	
	public EmployeePayrollExceptions(ExceptionType type, String message) {
		super(message);
		this.type = type;
	}
}
