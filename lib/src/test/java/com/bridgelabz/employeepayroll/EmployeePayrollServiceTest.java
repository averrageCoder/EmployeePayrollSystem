package com.bridgelabz.employeepayroll;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

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

}
