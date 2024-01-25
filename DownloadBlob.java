package practice;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DownloadBlob {

	public static void downloadBlob(String caseID, String pathname) {
		Connection conn = null; // Initialize connection / get connection
		PreparedStatement pst = null;
		try {
			conn = DB.getConnection();
			String query = "SELECT CASE_ID, EXHIBIT_NAME AS NAME, FILE_BLOB FROM EXHIBIT WHERE CASE_ID = " + caseID +"\n"
					+ "	UNION \n"
					+ "SELECT CASE_ID, DEPO_NAME AS NAME, FILE_BLOB FROM DEPOSITION WHERE CASE_ID = " + caseID;

			pst = conn.prepareStatement(query);

			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				InputStream is = rs.getBinaryStream(3);
				String str = convert(is);
				String fileName = rs.getString(2);
				File file = new File(pathname, fileName);
				System.out.println("Filename: " + fileName);
				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(str.getBytes());
				outputStream.close();

				System.out.println("File Generated: " + fileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			try {
				pst.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static String convert(InputStream is) {
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result;
		String str = null;
		try {
			result = bis.read();

			while (result != -1) {
				buf.write((byte) result);
				result = bis.read();
			}
			str = buf.toString("UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

}