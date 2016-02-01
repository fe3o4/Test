package CRMVWG_dataimport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class DataImport {
	
	private static Connection conn;
	private static PreparedStatement statInsert;
	private static PreparedStatement statAttr;
	private static ResultSet rsLead;

	private static void dbConnection() throws ClassNotFoundException, SQLException{
		Class.forName("oracle.jdbc.driver.OracleDriver");
		conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", "SSO_DCRM", "abc123");
		
		String sql = "Insert into SSO_DCRM.CUSTOMERS_NONE_SSO(CNS_ID,APPID,CUSTOMER_NAME,CONTACT_EMAIL,CONTACT_MOBILE,CUSTOMER_BIRTHDAY,ADDRESS_PROVINCE,"
				+ "ADDRESS_CITY,HOBBY,CUSTOMER_EDUCATION,CUSTOMER_OCCUPATION,PURCHASE_BRAND,PURCHASE_MODEL,PURCHASE_DATE,PURCHASE_BUDGET,OWNCAR_MANUFACTURER,"
				+ "OWNCAR_MODEL,OWNCAR_AGE,INSERT_TIMESTAMP,UPDATE_TIMESTAMP) VALUES (SSO_DCRM.SEQ_CUSTOMERS_NONE_SSO.NEXTVAL,"
				+ "'a9d6c6cd604510a9a30b8243a915b8ed',?,?,?,?,?,?,?,?,?,'VW',?,?,?,?,?,?,?,?)";
		statInsert = conn.prepareStatement(sql);
		
		sql = "select (select AL_KEY from ATTRIBUTE_LIST where AL_KEY_CN = ? and AL_NAME = 'HOBBY') AS Hobby," + 
				"(select AL_KEY from ATTRIBUTE_LIST where AL_KEY_CN = ? and AL_NAME = 'EDUCATION') AS Education," +
				"(select AL_KEY from ATTRIBUTE_LIST where AL_KEY_CN = ? and AL_NAME = 'OCCUPATION') AS Occupation, " + 
				"(select AL_KEY from ATTRIBUTE_LIST where AL_KEY_CN = ? and AL_NAME = 'PROVINCE') AS P from dual ";
		statAttr = conn.prepareStatement(sql);
	}
	
	private static void excelWork() throws IOException, SQLException{
		String province, city, hobby, education, occupation;
		
		
		InputStream input = new FileInputStream("xprofile-Test data 1126.xlsx");
		XSSFWorkbook wb = null;
		wb = new XSSFWorkbook(input);
		XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0); // 获得第一个表单
		Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
		DecimalFormat df = new DecimalFormat("0"); 
		Row row = rows.next(); // 获得行数据
		row = rows.next();
		row = rows.next();
		row = rows.next();
		int i = 0;
		ResultSet rs;
		while (rows!=null) {
			
			i++;
			
			province = row.getCell(5).getStringCellValue();
			hobby = row.getCell(7).getStringCellValue();
			education = row.getCell(8).getStringCellValue();
			occupation = row.getCell(9).getStringCellValue();
			statAttr.setString(1, hobby);
			statAttr.setString(2, education);
			statAttr.setString(3, occupation);
			statAttr.setString(4, province);
			rs = statAttr.executeQuery();
			if(rs.next()){
				hobby = rs.getString(1);
				education = rs.getString(2);
				occupation = rs.getString(3);
				province = rs.getString(4);
				System.out.println(hobby + "\t" + education + "\t" + occupation + "\t" + province);
			}
			System.out.println();
			
			
			statInsert.setString(1, row.getCell(0).getStringCellValue());
			statInsert.setString(2, row.getCell(1).getStringCellValue());
			statInsert.setString(3, df.format(row.getCell(2).getNumericCellValue()));
			statInsert.setString(4, df.format(row.getCell(3).getNumericCellValue()) + "-" + df.format(row.getCell(4).getNumericCellValue()));
			statInsert.setString(5, province);
			statInsert.setString(6, row.getCell(6).getStringCellValue());
			statInsert.setString(7, hobby);
			statInsert.setString(8, education);
			statInsert.setString(9, occupation);
			statInsert.setString(10, row.getCell(11).getStringCellValue());
			statInsert.setString(11, getPurchaseDate(row.getCell(12).getStringCellValue()));
			statInsert.setString(12, getPurchaseBudget(row.getCell(13).getStringCellValue()));
			statInsert.setString(13, getOwncarBrand(row.getCell(14).getStringCellValue()));
			statInsert.setString(14, row.getCell(15).getStringCellValue());
			statInsert.setString(15, row.getCell(16).getStringCellValue());
			statInsert.setTimestamp(16, new Timestamp(new Date().getTime()));
			statInsert.setTimestamp(17, new Timestamp(new Date().getTime()));
			
			try{
				statInsert.executeUpdate();
			}catch(Exception e){
				e.printStackTrace();
			}
			if(rows.hasNext())
				row = rows.next();
			else
				break;
		}
		System.out.println(i);
	}
	
	/**
	 * Convert purchase date
	 * ORIGINAL VALUES: LESS_3_MONTHS|3_6_MONTHS|6_12_MONTHS|1_2_YEARS|MORE_2_YEARS
	 */
	private static String getPurchaseDate(String pd){

		if(pd.contains("3个月以内"))
			return "LESS_3_MONTHS";
		else if(pd.contains("3-6个月"))
			return "3_6_MONTHS";
		else if(pd.contains("6-12个月"))
			return "6_12_MONTHS";
		else if(pd.contains("1-2年"))
			return "1_2_YEARS";
		else if(pd.contains("2年以后"))
			return "MORE_2_YEARS";
		else
			return "";
	}
	
	/**
	 * Convert purchase budget spend maximum
	 * ORIGINAL VALUES: <10WAN|10-20WAN|20-30WAN|30-50WAN|50-200WAN|>200WAN
	 */
	private static String getPurchaseBudget(String pb){

		if(pb.contains("10万以下"))
			return "<10WAN";
		else if(pb.contains("10-20万"))
			return "10-20WAN";
		else if(pb.contains("20-30万"))
			return "20-30WAN";
		else if(pb.contains("30-50万"))
			return "30-50WAN";
		else if(pb.contains("50-200万"))
			return "50-200WAN";
		else if(pb.contains("200万以上"))
			return ">200WAN";
		else
			return "";
	}
	
	private static String getOwncarBrand(String ob){

		if(ob.contains("上海大众"))
			return "SHANGHAI VOLKSWAGEN";
		else if(ob.contains("一汽大众"))
			return "FAW - VOLKSWAGEN";
		else if(ob.contains("进口大众"))
			return "VW";
		else
			return "OTHER";
	}
	
	
	public static void main(String[] args){
		try {
			dbConnection();
			excelWork();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
