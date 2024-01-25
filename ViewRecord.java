
package practice;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ViewRecord extends JFrame {
	private static ViewRecord frame;
	private static JPanel contentPane;
	private static JTextField textField;
	private static String caseID;
	private static String query;
	private String caseType;
	private String[] category = {"SELECT", "ASBESTOS", "CONSTRUCTION", "MEDICAL MALPRACTICE", "PERSONAL INJURY", "WORKER COMPENSATION"};
	JComboBox<String> categoryBox = new JComboBox<>(category);
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ViewRecord();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public ViewRecord() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblWelcomePage = new JLabel("Find Files");
		lblWelcomePage.setFont(new Font("Verdana", Font.PLAIN, 22));
		lblWelcomePage.setForeground(Color.BLACK);
	
		JLabel lblEnterId = new JLabel("Enter Court No: ");
		textField = new JTextField();			 //Case ID
		textField.setColumns(10);
		categoryBox = new JComboBox<>(category); //Case Category

		//Add Files
		JButton btnSearchCase = new JButton("Find Case File");
		btnSearchCase.setFont(new Font("Verdana", Font.PLAIN, 15));
		btnSearchCase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				caseType = categoryBox.getSelectedItem().toString();
		 		caseType = String.valueOf(caseType);
				caseID = textField.getText();
				
				if(caseType == null || caseType.trim().equals("") || caseID == null || caseID.trim().equals("")){
					JOptionPane.showMessageDialog(ViewRecord.this,"ID cannot be blank");
					}else{
						query = "SELECT CASE_ID, EXHIBIT_NAME AS NAME, FILE_BLOB FROM EXHIBIT WHERE CASE_ID = " + caseID +"\n"
								+ "	UNION \n"
								+ "SELECT CASE_ID, DEPO_NAME AS NAME, FILE_BLOB FROM DEPOSITION WHERE CASE_ID = " + caseID;
						ResultSetTableModel model = DAO.search(query);
						createAndShowGUI(model);
					}
			}
		});
		btnSearchCase.setFont(new Font("Verdana", Font.PLAIN, 15));
	
		//View Files, by category, takes the user input of the category and pulls all case files with that type
		JButton btnSearchCategory = new JButton("Search Categories");
		btnSearchCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				caseID = textField.getText().toUpperCase();
				
				//If input text is empty/
				if(caseID == null || caseID.trim().equals("")){
					JOptionPane.showMessageDialog(ViewRecord.this,"Category cannot be blank.");
					}else{ //if the string is not empty then 
						//see if the id has any numeric values
						if ( caseID.matches(".*\\d+.*")) {	
							JOptionPane.showMessageDialog(ViewRecord.this,"Please enter a valid category");
						}else {
							caseID = "'" + caseID + "'";
							query = "SELECT * FROM CASE_FILE\n"
									+ "WHERE CASE_TYPE = " + caseID + "\n"
									+ "ORDER BY CASE_NAME ASC;" ;
							ResultSetTableModel model = DAO.search(query);
							createAndShowGUI(model);
							if (frame != null) {frame.dispose();
							}
						}
			
					}
				
			}
		});
		btnSearchCategory.setFont(new Font("Verdana", Font.PLAIN, 15));
		
		JButton btnExport = new JButton("Export Case");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				caseID = textField.getText();
				
				if(caseID == null || caseID.trim().equals("")){
					JOptionPane.showMessageDialog(ViewRecord.this,"ID cannot be blank");
				}else{
					JFileChooser chooser = new JFileChooser("C:\\Miller-Reporting");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = chooser.showOpenDialog(frame);
					
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						String pathname = chooser.getSelectedFile().getAbsolutePath();
							DownloadBlob.downloadBlob(caseID, pathname);
							System.out.println("You chose the directory: " + pathname);

						}
				
				}//outer if/else
			}//actionPerformed
		});
		btnExport.setFont(new Font("Verdana", Font.PLAIN, 15));
		
		JButton btnDelete = new JButton("Delete Case");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				caseID = textField.getText();
				
				if(caseID == null || caseID.trim().equals("")){
					JOptionPane.showMessageDialog(ViewRecord.this,"ID cannot be blank");
				}else{
					String message = "Are you sure you want to erase case: " + caseID + " and all related files?";
					int reply = JOptionPane.showConfirmDialog(ViewRecord.this, message, "Confirm Deletion" , JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {
						int i = DAO.delete(caseID);
						if(i > 0){
							JOptionPane.showMessageDialog(ViewRecord.this, "Record deleted successfully!");
						}else{
							JOptionPane.showMessageDialog(ViewRecord.this, "Please enter the correct ID");
						}
					} else {
					    JOptionPane.showMessageDialog(ViewRecord.this, "Cannot find record, please check ID and try again");
					}//inner if/else
				}//outer if/else
			}//actionPerformed
		});
		btnDelete.setFont(new Font("Verdana", Font.PLAIN, 15));
		
		
		
		//Takes user back to the main landing page
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LoginSuccess.main(new String[]{});
				frame.dispose();
			}
		});
		btnBack.setFont(new Font("Verdana", Font.PLAIN, 15));
		
		/**********************************************************Frame Layout*************************************************************************/
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(150, Short.MAX_VALUE)
					.addComponent(lblWelcomePage, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
					.addGap(123))
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(39)
						.addComponent(lblEnterId)
						.addGap(57)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(107, Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(134)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnExport, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)

						.addComponent(btnSearchCategory, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSearchCase, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(109, Short.MAX_VALUE))
		);
		
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblWelcomePage, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
					.addGap(11)
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblEnterId))
					.addGap(33)
					.addComponent(btnSearchCase, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnSearchCategory, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnExport, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnBack, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(21, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	 private static void createAndShowGUI(ResultSetTableModel model) {
	        //Create and set up the window.
	        JFrame frame = new JFrame("File Viewer");
	 
	        JTable table = new JTable(model);
			table.setFillsViewportHeight(true);
			JScrollPane scrollPane = new JScrollPane(table);
			frame.add(scrollPane);
			frame.setVisible(true);
	        	 
	        //Display the window.
	        frame.pack();
	        frame.setVisible(true);
	    }

}