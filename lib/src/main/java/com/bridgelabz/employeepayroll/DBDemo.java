package com.bridgelabz.employeepayroll;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DBDemo {

	public static void main(String[] args) {
		String jdbcUrl = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false&characterEncoding=utf8";
		String userName = "root";
		String password ="";
		
		Connection conn;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver Loaded");
		}
		catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find driver", e);
		}
		
		listDrivers();
		
		try {
			System.out.println("Connecting to database: "+jdbcUrl);
			conn = DriverManager.getConnection(jdbcUrl, userName, password);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while(driverList.hasMoreElements()) {
			Driver driverClass = (Driver) driverList.nextElement();
			System.out.println(" "+driverClass.getClass().getName());
		}
		
	}

}
