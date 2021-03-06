package com.bridgelabz.employeepayroll;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			List<EmployeePayrollData> employeePayrollData;
			employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_I0);
			assertEquals(3, employeePayrollData.size());
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		try {
			employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
			assertEquals(3, employeePayrollData.size());
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void givenEmployeeSalary_whenUpdatedShouldMatch() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		try {
			List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
			employeePayrollService.updateEmployeeSalary("Terissa",3000000);
			boolean result = employeePayrollService.checkEmployeePayrollWithDB("Terissa");
			assertTrue(result);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void givenEmployeeSalary_whenUpdatedUsingPrepareStatementShouldMatch() {
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
			employeePayrollService.updateEmployeeSalaryUsingPrepareStatement("Terissa",3000000);
			boolean result;
			result = employeePayrollService.checkEmployeePayrollWithDB("Terissa");
			assertTrue(result);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedDataUsingStartDateShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		try {
			employeePayrollData = employeePayrollService.readEmployeePayrollDataUsingStartDate("2019-01-01");
			assertEquals(2, employeePayrollData.size());
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedSumForFemaleShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		double sumOfMaleSalaries;
		try {
			sumOfMaleSalaries = employeePayrollService.findSalarySumUsingGender("F");
			assertEquals(3000000.0, sumOfMaleSalaries);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedSumForMaleShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		double sumOfMaleSalaries;
		try {
			sumOfMaleSalaries = employeePayrollService.findSalarySumUsingGender("M");
			assertEquals(5100000.0, sumOfMaleSalaries);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedAvgForFemaleShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		double avgOfFemaleSalaries;
		try {
			avgOfFemaleSalaries = employeePayrollService.findSalaryAvgUsingGender("F");
			assertEquals(3000000, avgOfFemaleSalaries);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedAvgForMaleShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		double avgOfMaleSalaries;
		try {
			avgOfMaleSalaries = employeePayrollService.findSalaryAvgUsingGender("M");
			assertEquals(1700000, avgOfMaleSalaries);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedMaxForFemaleShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		double maxOfFemaleSalaries;
		try {
			maxOfFemaleSalaries = employeePayrollService.findSalaryMaxUsingGender("F");
			assertEquals(3000000, maxOfFemaleSalaries);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenEmployeePayrollInDB_whenRetreivedMaxForMaleShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		double maxOfMaleSalaries;
		try {
			maxOfMaleSalaries = employeePayrollService.findSalaryMaxUsingGender("M");
			assertEquals(4000000, maxOfMaleSalaries);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenNewEmployee_WhenAdded_ShouldBeInSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		try {
			employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
			employeePayrollService.addEmployeeToPayroll("Mark",1000000, LocalDate.now(), "M", "7894561230", "Bangalore");
			boolean result = employeePayrollService.checkEmployeePayrollWithDB("Mark");
			assertTrue(result);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenNewEmployee_WhenAddedForER_ShouldBeInSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		try {
			employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
			List<String> Department = new ArrayList<String>();
			Department.add("Finance");
			Department.add("Security");
			employeePayrollService.addEmployeeToPayrollWithER("Mark",1000000, LocalDate.now(), "M", "7894561230", "Bangalore",Department);
			boolean result = employeePayrollService.checkEmployeePayrollWithDBWithER("Mark");
			assertTrue(result);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void givenNewEmployee_WhenRemovedForER_ShouldBeInSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData;
		try {
			employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_I0);
			List<String> Department = new ArrayList<String>();
			Department.add("Finance");
			Department.add("Security");
			employeePayrollService.removeEmployeeToPayrollWithER("Mark");
			boolean result = employeePayrollService.checkEmployeePayrollWithDBWithER("Mark");
			assertTrue(!result);
		} catch (EmployeePayrollExceptions e) {
			e.printStackTrace();
		}
	}
}
