package com.bridgelabz.employeepayroll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bridgelabz.employeepayroll.EmployeePayrollExceptions.ExceptionType;

public class EmployeePayrollDBService {
	
	private PreparedStatement employeePayrollDataStatement;
	private PreparedStatement employeePayrollUpdateStatement;
	private static EmployeePayrollDBService employeePayrollDBService;
	
	public EmployeePayrollDBService() {
		
	}
	
	public static EmployeePayrollDBService getInstance() {
		if(employeePayrollDBService == null) 
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}
	
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

	public List<EmployeePayrollData> readEmployeePayrollData() throws EmployeePayrollExceptions {
		String sql="SELECT e.id,e.name,e.start_date, p.net_pay from employee e, payroll p where e.id=p.employee_id;";
		return this.getEmployeeDataBasedOnSQL(sql);
	}

	public int updateEmployee(String name, double updatedSalary) throws EmployeePayrollExceptions {
		String sql=String.format("update payroll p join employee e on e.id=p.employee_id set net_pay = %.2f where e.NAME='%s';",updatedSalary,name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
	}
	
	public int updateEmployeeUsingPreparedStatement(String name, double updatedSalary) throws EmployeePayrollExceptions {
		String sql="update payroll p join employee e on e.id=p.employee_id set net_pay =? where e.NAME=?;";
		List<EmployeePayrollData> employeePayrollData = null;
		if(this.employeePayrollUpdateStatement == null)
			this.prepareStatementForUpdateData(sql);
		try {
			employeePayrollDataStatement.setDouble(1, updatedSalary);
			employeePayrollDataStatement.setString(2, name);
			return employeePayrollDataStatement.executeUpdate();
		} catch (SQLException e) {
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
	}
	
	public List<EmployeePayrollData> getEmployeeDetailsBasedOnStartDate(String startDate) throws EmployeePayrollExceptions {
		String sql = String.format("select e.id,e.name,e.start_date,p.net_pay from employee e, payroll p where e.id=p.employee_id and start_date between CAST('%s' as date) and date(now());",startDate);
		return this.getEmployeeDataBasedOnSQL(sql);
	}
	
	

	private List<EmployeePayrollData> getEmployeeDataBasedOnSQL(String sql) throws EmployeePayrollExceptions {
		List<EmployeePayrollData> employeePayrollData = new ArrayList<EmployeePayrollData>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollData = this.getEmployeePayrollData(resultSet);
		}
		catch(SQLException e){
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return employeePayrollData;
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws EmployeePayrollExceptions {
		List<EmployeePayrollData> employeePayrollData = null;
		//if(this.employeePayrollDataStatement == null)
		this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollData = this.getEmployeePayrollData(resultSet);
		}
		catch (SQLException e) {
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return employeePayrollData;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws EmployeePayrollExceptions {
		List<EmployeePayrollData> employeePayrollData = new ArrayList<>();
		try {
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				Double salary = resultSet.getDouble("net_pay");
				LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
				employeePayrollData.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		}
		catch (SQLException e) {
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return employeePayrollData;
	}

	private void prepareStatementForEmployeeData() throws EmployeePayrollExceptions {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT e.id,e.name,e.start_date, p.net_pay from employee e, payroll p where e.id=p.employee_id and e.name=?;";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		
	}
	
	private void prepareStatementForUpdateData(String sql) throws EmployeePayrollExceptions {
		try {
			Connection connection = this.getConnection();
			employeePayrollDataStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		
	}

	public double getSalarySumBasedOnGender(String gender) throws EmployeePayrollExceptions {
		String sql = String.format("select sum(net_pay) from employee e, payroll p where e.id=p.employee_id and gender='%s' group by gender;",gender);
		double salarySum = 0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				salarySum = resultSet.getDouble("sum(net_pay)");
			}
		}
		catch(SQLException e){
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return salarySum;
	}

	public double getSalaryAvgBasedOnGender(String gender) throws EmployeePayrollExceptions {
		String sql = String.format("select avg(net_pay) from employee e, payroll p where e.id=p.employee_id and gender='%s' group by gender;",gender);
		double salaryAvg = 0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				salaryAvg = resultSet.getDouble("avg(net_pay)");
			}
		}
		catch(SQLException e){
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return salaryAvg;
	}

	public double getSalaryMaxBasedOnGender(String gender) throws EmployeePayrollExceptions {
		String sql = String.format("select max(net_pay) as max from employee e, payroll p where e.id=p.employee_id and gender='%s' group by gender;",gender);
		double salaryMax = 0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				salaryMax = resultSet.getDouble("max");
			}
		}
		catch(SQLException e){
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return salaryMax;
	}

}
