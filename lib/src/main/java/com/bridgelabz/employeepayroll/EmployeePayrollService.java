package com.bridgelabz.employeepayroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
	
public class EmployeePayrollService {
	
	public enum IOService {CONSOLEIO, FILE_I0, DB_I0, REST_I0}
	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;


	public EmployeePayrollService(List<EmployeePayrollData> list) {
		this();
		this.employeePayrollList = list;
	}

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
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

	public void printData(IOService ioservice) {
		if(ioservice.equals(IOService.FILE_I0)) {
			new EmployeePayrollFileIOService().printData();
		}
	}

	public long countEntries(IOService ioservice) {
		if(ioservice.equals(IOService.FILE_I0)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioservice) {
		if(ioservice.equals(IOService.FILE_I0)) {
			this.employeePayrollList=new EmployeePayrollFileIOService().readEmployeePayrollData();
			System.out.println("PARSED DATA FROM FILE: ");
			this.employeePayrollList.forEach(employee -> System.out.println(employee));
		}
		else if(ioservice.equals(IOService.DB_I0)) {
			this.employeePayrollList=employeePayrollDBService.readEmployeePayrollData();
			System.out.println("PARSED DATA FROM DB: ");
			this.employeePayrollList.forEach(employee -> System.out.println(employee));
		}
		return this.employeePayrollList;
	}

	public void updateEmployeeSalary(String name, double updatedSalary) {
		int result = employeePayrollDBService.updateEmployee(name,updatedSalary);
		if (result==0) return;
		this.employeePayrollList=employeePayrollDBService.readEmployeePayrollData();
	}
	
	public void updateEmployeeSalaryUsingPrepareStatement(String name, double updatedSalary) {
		int result =employeePayrollDBService.updateEmployeeUsingPreparedStatement(name,updatedSalary);
		if (result==0) return;
		this.employeePayrollList=employeePayrollDBService.readEmployeePayrollData();
	}

	public boolean checkEmployeePayrollWithDB(String name) {
		// TODO Auto-generated method stub
		List<EmployeePayrollData> employeePayrollData = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollData.get(0).equals(getEmployeeData(name));	
	}

	private EmployeePayrollData getEmployeeData(String name) {
		EmployeePayrollData employeePayrollData;
		employeePayrollData = this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
				.findFirst()
				.orElse(null);
		return employeePayrollData;
	}
	
	public List<EmployeePayrollData> readEmployeePayrollDataUsingStartDate(String startDate) {
		this.employeePayrollList=employeePayrollDBService.getEmployeeDetailsBasedOnStartDate(startDate);
		System.out.println("PARSED DATA FROM DB: ");
		this.employeePayrollList.forEach(employee -> System.out.println(employee));
		return this.employeePayrollList;
	}
}
