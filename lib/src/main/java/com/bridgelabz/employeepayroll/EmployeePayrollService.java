package com.bridgelabz.employeepayroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
	
public class EmployeePayrollService {
	
	public enum IOService {CONSOLEIO, FILE_I0, DB_I0, REST_I0}
	private List<EmployeePayrollData> employeePayrollList;


	public EmployeePayrollService(List<EmployeePayrollData> list) {
		this.employeePayrollList = list;
	}

	public static void main(String[] args) {	
		ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList) ;
		Scanner consoleInputReader = new Scanner(System.in) ;
		employeePayrollService.readEmployeePayrollData(consoleInputReader) ;
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLEIO);
	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {	
		System.out.println("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.println("Enter Employee Salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));	
	}
	
	void writeEmployeePayrollData(IOService ioservice) {	
		if(ioservice.equals(IOService.CONSOLEIO))
			System.out.println("\nWriting Employee Payroll to Console\n" + employeePayrollList);
		else if(ioservice.equals(IOService.FILE_I0)) {
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
		}
	}
}
