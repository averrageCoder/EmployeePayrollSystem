package com.bridgelabz.employeepayroll;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

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
		String sql="SELECT e.id,e.name,e.start_date, p.basic_pay from employee e, payroll p where e.id=p.employee_id;";
		return this.getEmployeeDataBasedOnSQL(sql);
	}

	public int updateEmployee(String name, double updatedSalary) throws EmployeePayrollExceptions {
		String sql=String.format("update payroll p join employee e on e.id=p.employee_id set baic_pay = %.2f where e.NAME='%s';",updatedSalary,name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
	}
	
	public int updateEmployeeUsingPreparedStatement(String name, double updatedSalary) throws EmployeePayrollExceptions {
		String sql="update payroll p join employee e on e.id=p.employee_id set baic_pay =? where e.NAME=?;";
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
	}public int removeEmployeeUsingName(String name) throws EmployeePayrollExceptions {
		String sql="delete from employee e where e.NAME=?;";
		List<EmployeePayrollData> employeePayrollData = null;
		if(this.employeePayrollUpdateStatement == null)
			this.prepareStatementForUpdateData(sql);
		try {
			employeePayrollDataStatement.setString(1, name);
			return employeePayrollDataStatement.executeUpdate();
		} catch (SQLException e) {
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
	}
	
	
	
	public List<EmployeePayrollData> getEmployeeDetailsBasedOnStartDate(String startDate) throws EmployeePayrollExceptions {
		String sql = String.format("select e.id,e.name,e.start_date,p.baic_pay from employee e, payroll p where e.id=p.employee_id and start_date between CAST('%s' as date) and date(now());",startDate);
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
	
	public List<EmployeePayrollData> getEmployeePayrollDataWithER(String name) throws EmployeePayrollExceptions {
		List<EmployeePayrollData> employeePayrollData = null;
		String sql="SELECT e.id,e.name,e.start_date, p.basic_pay from employee e, payroll p where e.id=p.employee_id and e.name=?;";
		try (Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql), resultSet2;
			while(resultSet.next()) {
				int id = resultSet.getInt("id");
				String emp_name = resultSet.getString("name");
				Double salary = resultSet.getDouble("basic_pay");
				LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
				sql = String.format("SELECT d.name from department d, employee e, employee_department ed where ed.employee_id=e.id and e.id=%s;",id);
				resultSet2 = statement.executeQuery(sql);
				List<String> departments = new ArrayList<String>();
				while(resultSet.next()) {
					departments.add(resultSet2.getString("name"));
				}
				employeePayrollData.add(new EmployeePayrollData(id, name, salary, startDate, departments));
			}
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
				Double salary = resultSet.getDouble("basic_pay");
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
			String sql = "SELECT e.id,e.name,e.start_date, p.basic_pay from employee e, payroll p where e.id=p.employee_id and e.name=?;";
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
		String sql = String.format("select sum(baic_pay) from employee e, payroll p where e.id=p.employee_id and gender='%s' group by gender;",gender);
		double salarySum = 0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				salarySum = resultSet.getDouble("sum(basic_pay)");
			}
		}
		catch(SQLException e){
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return salarySum;
	}

	public double getSalaryAvgBasedOnGender(String gender) throws EmployeePayrollExceptions {
		String sql = String.format("select avg(baic_pay) from employee e, payroll p where e.id=p.employee_id and gender='%s' group by gender;",gender);
		double salaryAvg = 0;
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				salaryAvg = resultSet.getDouble("avg(baic_pay)");
			}
		}
		catch(SQLException e){
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return salaryAvg;
	}

	public double getSalaryMaxBasedOnGender(String gender) throws EmployeePayrollExceptions {
		String sql = String.format("select max(baic_pay) as max from employee e, payroll p where e.id=p.employee_id and gender='%s' group by gender;",gender);
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

	public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate start_date, String gender, String phone, String address) throws EmployeePayrollExceptions {
		int employeeID = -1;
		EmployeePayrollData employeeData = null;
		String sql = String.format("INSERT INTO `employee`\n"
				+ "(`name`,`gender`,`phone_number`,`address`,`start_date`)\n"
				+ "VALUES('%s','%s','%s','%s','%s');",name,gender,phone,address, Date.valueOf(start_date));
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected==1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) employeeID = resultSet.getInt(1);
				double deductions = salary * 0.2;
				double taxable_pay = salary -deductions;
				double tax = salary*0.1;
				double baic_pay = salary-tax;
				String sql2 = String.format("INSERT INTO `payroll` (`employee_id`,`basic_pay`,`deductions`,`taxable_pay`,`tax`,`baic_pay`)\n"
						+ "VALUES(%s,%s,%s,%s,%s,%s);",employeeID, salary, deductions, taxable_pay, tax, baic_pay);
				rowAffected = statement.executeUpdate(sql2, statement.RETURN_GENERATED_KEYS);
			}
			employeeData = new EmployeePayrollData(employeeID, name, salary, start_date);
			connection.commit();
		}
		catch(SQLException e){
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
			}
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return employeeData;
	}

	public EmployeePayrollData addEmployeeToPayrollWithER(String name, double salary, LocalDate start_date,
			String gender, String phone, String address, List<String> department) throws EmployeePayrollExceptions {
		int employeeID = -1;
		EmployeePayrollData employeeData = null;
		String sql = String.format("INSERT INTO `employee`\n"
				+ "(`name`,`gender`,`phone_number`,`address`,`start_date`)\n"
				+ "VALUES('%s','%s','%s','%s','%s');",name,gender,phone,address, Date.valueOf(start_date));
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if(rowAffected==1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) employeeID = resultSet.getInt(1);
				double deductions = salary * 0.2;
				double taxable_pay = salary -deductions;
				double tax = salary*0.1;
				double net_pay = salary-tax;
				
				String sql2 = String.format("INSERT INTO `payroll` (`employee_id`,`basic_pay`,`deductions`,`taxable_pay`,`tax`,`net_pay`)\n"
						+ "VALUES(%s,%s,%s,%s,%s,%s);",employeeID, salary, deductions, taxable_pay, tax, net_pay);
				rowAffected = statement.executeUpdate(sql2, statement.RETURN_GENERATED_KEYS);
				if(rowAffected==1) {
					
					String sql3 = "";
					List<Integer> department_ids = new ArrayList<>();
					int departmentID = -1;
					for(String dept : department) {
						sql3 = String.format("INSERT INTO `department` (`name`)\n"
								+ "VALUES ('%s');", dept);
						statement = connection.createStatement();
						rowAffected = statement.executeUpdate(sql3, statement.RETURN_GENERATED_KEYS);
						if(rowAffected==1) {
							resultSet = statement.getGeneratedKeys();
							if(resultSet.next()) departmentID = resultSet.getInt(1);
							department_ids.add(departmentID);
						}
					}
					
					String sql4 = "";
					for(int deptId : department_ids) {
						sql4 = String.format("INSERT INTO `employee_department`(`employee_id`,`department_id`)\n"
								+ "VALUES(%s, %s)",employeeID, deptId);
						rowAffected = statement.executeUpdate(sql4, statement.RETURN_GENERATED_KEYS);
					}
				}
			}
			employeeData = new EmployeePayrollData(employeeID, name, salary, start_date, department);
			connection.commit();
		}
		catch(SQLException e){
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.printStackTrace();
				throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
			}
			throw new EmployeePayrollExceptions(ExceptionType.SQL_ERROR, "SQL ERROR!");
		}
		return employeeData;
	}

}
