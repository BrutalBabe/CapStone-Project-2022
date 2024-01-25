
package practice;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DAO {
	public static int insertCase(String caseID, String caseName, String caseType, String creationDate){
		int status = 0;
		try{
			Connection con = DB.getConnection();
			//TODO ensure the on conflict update does not overwrite data
			PreparedStatement ps = con.prepareStatement("INSERT INTO CASE_FILE (CASE_ID, CASE_NAME, CASE_TYPE, CASE_CREATION_DATE)\n"
															+ "VALUES(?, ?, ?, ?)\n"
															+ "    ON CONFLICT (CASE_ID) DO UPDATE SET \n"
															+ "    CASE_CREATION_DATE = EXCLUDED.CASE_CREATION_DATE;");
					
			ps.setString(1, caseID ); 
			ps.setString(2, caseName );
			ps.setString(3, caseType );
			ps.setString(4, creationDate);
			status = ps.executeUpdate();
			status += 1;
			con.close();
			ps.close();
		}catch(Exception e){System.out.println(e);}
		return status;
	}
	
	public static int insertDeposition(String caseID, String depoName, byte[] byteFile ){
		int status = 0;
		try{
			Connection con = DB.getConnection();
			//TODO ensure the on conflict update does not overwrite data
			PreparedStatement ps = con.prepareStatement("INSERT INTO DEPOSITION(CASE_ID, DEPO_NAME, FILE_BLOB)"
															+ "VALUES(?, ?, ?)\n"
															+ " ON CONFLICT (DEPO_NAME) DO UPDATE SET"
															+ " FILE_BLOB = EXCLUDED.FILE_BLOB;");
			ps.setString(1, caseID );
			ps.setString(2, depoName );
			ps.setBytes(3, byteFile );
			status = ps.executeUpdate();
			status += 1;
			con.close();
			ps.close();
		}catch(Exception e){System.out.println(e);}
		return status;
	}
	
		public static int insertExhibit(String caseID, String exhibitName, byte[] byteFile ){
			int status = 0;
			try{
				Connection con = DB.getConnection();
				PreparedStatement ps = con.prepareStatement("INSERT INTO EXHIBIT(CASE_ID, EXHIBIT_NAME, FILE_BLOB)"
															+ "VALUES(?, ?, ?)\n"
															+ " ON CONFLICT (EXHIBIT_NAME) DO UPDATE SET"
															+ " FILE_BLOB = EXCLUDED.FILE_BLOB;");
				ps.setString(1, caseID );
				ps.setString(2, exhibitName );
				ps.setBytes(3, byteFile );
				status = ps.executeUpdate();
				status += 1;
				con.close();
				ps.close();
			}catch(Exception e){System.out.println(e);}

			return status;
		}
	
	public static int delete(String caseID){
		int status = 0;
		try{
			Connection con = DB.getConnection();
			PreparedStatement exhibit = con.prepareStatement("DELETE FROM EXHIBIT WHERE CASE_ID = " + caseID + ";");
			PreparedStatement deposition = con.prepareStatement("DELETE FROM DEPOSITION WHERE CASE_ID = " + caseID + ";");
			PreparedStatement caseFile = con.prepareStatement("DELETE FROM CASE_FILE WHERE CASE_ID = " + caseID + ";");

			status = deposition.executeUpdate();
			status = exhibit.executeUpdate();
			status = caseFile.executeUpdate();
			status += 1;
			con.close();
		}catch(Exception e){System.out.println(e);}
		return status;
	}
	
	
		//Select All EXHIBIT and DEPOSITION from the CASE_FILE Table
		public static ResultSetTableModel search(String query){
			ResultSetTableModel model = null;
			try{
				Connection con = DB.getConnection();
				PreparedStatement ps = con.prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				model = new ResultSetTableModel();
				model.setResultSet(rs);
				con.close();
				ps.close();
				int count= model.getRowCount();
				if (count != 0) {
					return model;
				}else {
					//TODO if the result set is null then don't display any tables
					JOptionPane.showMessageDialog(null, "Cannot find record, please check ID and try again");
					
				}

			}catch(Exception e){System.out.println(e);}
			return null;
		}
		
		
		
		
	public static void exportCase(String caseID, String pathname ) {
	    // update sql
	    String selectSQL = "SELECT CASE_ID, EXHIBIT_NAME AS NAME, FILE_BLOB FROM EXHIBIT WHERE CASE_ID = " + caseID +"\n"
				+ "	UNION \n"
				+ "SELECT CASE_ID, DEPO_NAME AS NAME, FILE_BLOB FROM DEPOSITION WHERE CASE_ID = " + caseID;
		Connection con = null;
	    ResultSet rs = null;
	    OutputStream out = null;
	    PreparedStatement pstmt = null;
	
	    try {
			con = DB.getConnection();
	        pstmt = con.prepareStatement(selectSQL);
	        rs = pstmt.executeQuery();
	        ResultSet resultSet = pstmt.executeQuery();
	        
	        while (resultSet.next()) {
	            Path path = Paths.get(pathname);
	    	    byte[] buffer = java.nio.file.Files.readAllBytes(path);

	        	String name = resultSet.getString(2);
	        	InputStream is = resultSet.getBinaryStream(3);  //contains byte data of the file
	        	File file = new File(name);
	        	out = new FileOutputStream(file);
	        	out.write(buffer);
	        	out.close();
	        }//while
	    } catch (SQLException | IOException e) {
	        System.out.println(e.getMessage());
	    } finally {
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	            if (pstmt != null) {
	                pstmt.close();
	            }
	
	            if (con != null) {
	                con.close();
	            }
	            if (out != null) {
	                out.close();
	            }
	
	        } catch (SQLException | IOException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	}//exportCase
	
	public static void copyStream(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while ((read = in.read(buffer)) != -1) {
	        out.write(buffer, 0, read);
	    }
	}//copyStream
}