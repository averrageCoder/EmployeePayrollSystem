package com.bridgelabz.employeepayroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
	
	private Connection getConnection() throws SQLException {
		String jdbcUrl = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false&characterEncoding=utf8";
		String userName = "root";
		String password ="";
		Connection connection;
		System.out.println("Connecting to database: "+jdbcUrl);
		connection = DriverManager.getConnection(jdbcUrl, userName, password);
		System.out.println("Connection is successful!!!"+connection);
		return connection;
	}

	public List<EmployeePayrollData> readEmployeePayrollData() {
		String sql="SELECT e.id,e.name,e.start_date, p.net_pay from employee e, payroll p where e.id=p.employee_id;";
		List<EmployeePayrollData> employeePayrollData = new ArrayList<EmployeePayrollData>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				Double salary = resultSet.getDouble("net_pay");
				LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
				employeePayrollData.add(new EmployeePayrollData(id, name, salary, startDate));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return employeePayrollData;
	}
}
