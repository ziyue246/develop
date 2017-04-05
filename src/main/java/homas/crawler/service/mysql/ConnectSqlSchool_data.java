package homas.crawler.service.mysql;


import homas.crawler.bean.SchoolData;
import homas.crawler.system.SystemCommon;

import java.sql.*;
import java.util.Date;

public class ConnectSqlSchool_data {
	public static String table = "school_data";
	private static String url = "jdbc:mysql://121.42.208.82:3306/homas";
	private static String user = "homas";// 用户名,系统默认的账户名
	private static String password = "homas2017";// 你安装时选设置的密码

	public static int countMd5(String md5Code) {
		
		ResultSet result = null;// 创建一个结果集对象
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String sqlMd5 = "select count(*) as md5Count from " + table + " where MD5 = '" + md5Code + "'";
			result = DriverManager.getConnection(url, user, password).
					createStatement().executeQuery(sqlMd5);
			while (result.next()) {
				return result.getInt("md5Count");
			}
			result.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return 0;
	}

	public static void insertMysql(SchoolData schoolData) {
		String sql = "insert into " + table + " (name,district,locationCode,search_keyword,md5,insert_time,optCode," +
				"primaryAddressLine,stateCode,zip,accessibilityCode,accessibilityDescription," +
				"profile,boroughNumber,boroughName,phoneNumber,size,grades," +
				"distance,rank,isChildhoodCenter,displayName,dayLength," +
				"managedBy,seats,prekType,primaryBuildingCode)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		System.out.println(sql);
		if (schoolData == null)
			return;
		try {
			Class.forName("com.mysql.jdbc.Driver");// 加载Mysql驱动程序
			Connection con = DriverManager.getConnection(url, user, password);// 获取连接
			PreparedStatement preparedStmt = con.prepareStatement(sql);

			preparedStmt.setString(1, schoolData.getName());
			preparedStmt.setString(2, schoolData.getDisplayName());
			preparedStmt.setString(3, schoolData.getLocationCode());
			preparedStmt.setString(4, schoolData.getSearchKeyword());
			preparedStmt.setString(5, schoolData.getMd5());

			preparedStmt.setTimestamp(6, new Timestamp((new Date()).getTime()));

			preparedStmt.setString(7, schoolData.getOptCode());
			preparedStmt.setString(8, schoolData.getPrimaryAddressLine());
			preparedStmt.setString(9, schoolData.getStateCode());
			preparedStmt.setString(10, schoolData.getZip());
			preparedStmt.setString(11, schoolData.getAccessibilityCode());
			preparedStmt.setString(12, schoolData.getAccessibilityDescription());
			preparedStmt.setString(13, schoolData.getProfile());
			preparedStmt.setString(14, schoolData.getBoroughNumber());
			preparedStmt.setString(15, schoolData.getBoroughName());
			preparedStmt.setString(16, schoolData.getPhoneNumber());
			preparedStmt.setString(17, schoolData.getSize());
			preparedStmt.setString(18, schoolData.getGrades());
			preparedStmt.setString(19, schoolData.getDistance());
			preparedStmt.setString(20, schoolData.getRank());
			preparedStmt.setString(21, schoolData.getIsChildhoodCenter());
			preparedStmt.setString(22, schoolData.getDisplayName());
			preparedStmt.setString(23, schoolData.getDayLength());
			preparedStmt.setString(24, schoolData.getManagedBy());
			preparedStmt.setString(25, schoolData.getSeats());
			preparedStmt.setString(26, schoolData.getPrekType());
			preparedStmt.setString(27, schoolData.getPrimaryBuildingCode());
			System.out.println(preparedStmt.toString());
			preparedStmt.execute();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void insertMessage(SchoolData schoolData) {
		if(countMd5(schoolData.getMd5())==0){
			insertMysql(schoolData);
		}else{
			SystemCommon.printLog("the "+schoolData.getTitle()+" was inserted!!");
		}
	}
}
