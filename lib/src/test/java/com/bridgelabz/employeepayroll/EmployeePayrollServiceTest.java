package com.bridgelabz.employeepayroll;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class EmployeePayrollServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData( 1, "Jeff Bezos", 100000.0),
				new EmployeePayrollData( 2, "Bill Gates",  200000.0),
				new EmployeePayrollData( 3, "Mark Zuckerberg", 300000.0)
		};
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_I0);
		employeePayrollService.printData(EmployeePayrollService.IOService.FILE_I0);
		long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_I0);
		assertEquals( 3, entries);
	}
	
	@Test
	public void givenFileOnReadingFromFileShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_I0);
		assertEquals(3, employeePayrollData.size());
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
		assertEquals(3, employeePayrollData.size());
	}
	
	@Test
	public void givenEmployeeSalary_whenUpdatedShouldMatch() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
		employeePayrollService.updateEmployeeSalary("Terissa",3000000);
		boolean result = employeePayrollService.checkEmployeePayrollWithDB("Terissa");
		assertTrue(result);
	}
	
	@Test
	public void givenEmployeeSalary_whenUpdatedUsingPrepareStatementShouldMatch() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
		employeePayrollService.updateEmployeeSalaryUsingPrepareStatement("Terissa",3000000);
		boolean result = employeePayrollService.checkEmployeePayrollWithDB("Terissa");
		assertTrue(result);
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedDataUsingStartDateShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollDataUsingStartDate("2019-01-01");
		assertEquals(2, employeePayrollData.size());
	}
}
