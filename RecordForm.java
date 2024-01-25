
package practice;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class RecordForm extends JFrame  {
	static RecordForm frame;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private String caseID;
	private String caseName;
	private String caseType;
	private String[] category = {"SELECT TYPE", "ASBESTOS", "CONSTRUCTION", "MEDICAL MALPRACTICE", "PERSONAL INJURY", "WORKER COMPENSATION"};
	JComboBox<String> categoryBox = new JComboBox<>(category);
	private java.util.Date date;
	private String creationDate;
	//File attribute variables
	private String fileName;
	private boolean isDeposition;
	private boolean isExhibit;  
	private byte fileBytes[];

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					frame = new RecordForm();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	//Create Frame 
	public RecordForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
	    
		//Labels the display panel
		JLabel lblAddFile = new JLabel("Add File");		
		//Label the text field
		JLabel lblCaseNo = new JLabel("Case No:");
		JLabel lblName = new JLabel("Case Name:");
		JLabel lblCategory = new JLabel("Category:");

		//Set the text fields
		textField = new JTextField();	//Court Case ID, 20 char
		textField.setColumns(20); 
		textField_1 = new JTextField(); //File Name, 20 char
		textField_1.setColumns(20);
		categoryBox = new JComboBox<>(category); //Case Category
		
	    JButton btnSelectFile = new JButton("Choose File");
	    btnSelectFile.addActionListener(new ActionListener() {  
	         public void actionPerformed(ActionEvent e) {
	        	//get data from field and put them into variables
	        	caseID = textField.getText();
		 		caseName = textField_1.getText();
		 		caseType = categoryBox.getSelectedItem().toString();
		 		caseType = String.valueOf(caseType);
		 		if(caseType == "SELECT TYPE") {
					JOptionPane.showMessageDialog(RecordForm.this, "Please select a category.");
		 		}else {  //insert the files 
		 			caseCreation();
		 		}  
	         }//actionPerformed "Choose File"
	      });//eventListener
	      
	    //button to call back to the main landing page
		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginSuccess.main(new String[]{});
				frame.dispose();
			}
		});
		btnBack.setFont(new Font("Verdana", Font.PLAIN, 13));

		//sets the layout of the page, got from visual text editor (not easily modifiable)
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(150)
							.addComponent(lblAddFile))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblName, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCaseNo)
								.addComponent(lblCategory, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
							.addGap(47)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(categoryBox, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
								.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE))))
								
					.addContainerGap(125, Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(161)
					.addComponent(btnSelectFile, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(162, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(359, Short.MAX_VALUE)
					.addComponent(btnBack)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblAddFile)
					.addGap(18)

					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCaseNo)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)

					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)

					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCategory)
						.addComponent(categoryBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)					

					.addComponent(btnSelectFile, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBack)
					.addContainerGap(53, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		
	}//RecordForm

	/**
     * Read the file and returns the byte array
     * @param must have a file to insert
     * @return the byte array of the file
     */
	public static byte[] convertFileToByte(File file)
	    {
		ByteArrayOutputStream bos = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
            fis.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        
        return bos != null ? bos.toByteArray() : null;
	
		/*
			//file object instance
	        FileInputStream fl = new FileInputStream(file);
	        // Now creating byte array of same length as file
	        byte[] arr = new byte[(int)file.length()];
	        //read into byte file
	        fl.read(arr);
	        //cose the file and return the byte array
	        fl.close();
	        return arr;
	     */
	    }
	
	private static Blob convertToBlob(List fileList) throws SerialException, SQLException {
		ByteArrayOutputStream output = 
	            new ByteArrayOutputStream();
	    ObjectOutputStream obj;
	    try {
	       obj = new ObjectOutputStream(output);
	       obj.writeObject(fileList);
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
	
	    byte[] bytes = output.toByteArray();
	    Blob fileBlob = new SerialBlob(bytes);
	    return fileBlob;
	}
	
	private void compareExtentions(File file) {
		//searches for extension of file and set flag to true for .pdf or .txt
        //to determine if the file is a deposition of an exhibit
        String extension = "";
        int i = fileName.lastIndexOf('.');
        
        if (i > 0) {
             extension = fileName.substring(i + 1);
           }
        
      //this file uploaded is a .txt so call an insert into depo table
        if ( extension.equals("txt")) {
	           isExhibit = false;
	           isDeposition = true;
          	}//TXT if
              
	          //this file uploaded is a .txt so call an insert into depo table
        else if ( extension.equals("pdf")) {
		        isDeposition = false;
	            isExhibit = true;
         	}//PDF if     
	}//compareExtention
	
	public void caseCreation() {
		//Capture creation data at the time of case upload
       	date = Calendar.getInstance().getTime();  
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
        creationDate = dateFormat.format(date);  
        
		//File Chooser to select files to add as data into the cases
		JFileChooser  filechooser = new JFileChooser("C:\\");
		filechooser.setMultiSelectionEnabled(true);	
		
   		int returnVal = filechooser.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
           //gathers file data or array of file data as blob/byte[]	
           //get file data, convert the data for file into byte array, put the byte[] into a list
           //Create an array list consisting of one or many byte arrays, cast as BLOB when passing parameter to insert call
           File[] fileArray = filechooser.getSelectedFiles();
           List<byte[]> fileList = new ArrayList<byte[]>();

           //convert file or file array to a byte array
           try {
        	   //read the length of the array of files selected for the case
        	   for(int i = 0; i < fileArray.length; i++) {
        		   //at each iteration set the file attributes
	               fileName = fileArray[i].getName();
	               compareExtentions(fileArray[i]); 
	               
	               //convert the file at in the array to a byte array  and add the byte array to a list
	               //creates an arrayList that holds the file information as one or more byte[]
        		   fileBytes = convertFileToByte(fileArray[i]);
        		   fileList.add(fileBytes);

        		   if (isDeposition == true && isExhibit == false) {
        			   int j = DAO.insertDeposition(caseID, fileName, fileList.get(i));

        		   			if(j > 0){
        		   				frame.dispose();
        		   			}else{
        		   				JOptionPane.showMessageDialog(RecordForm.this,"Sorry, unable to save deposition.");
        		   			}
        		   }//isDeposition if
        		   
        		   else if (isExhibit == true && isDeposition == false) {
        			   int k = DAO.insertExhibit(caseID, fileName, fileList.get(i));
        		   			if(k > 0){
        		   				frame.dispose();
        		   			}else{
        		   				JOptionPane.showMessageDialog(RecordForm.this,"Sorry, unable to save exhibit.");
        		   			}
        		   }
        	   }//for loop
			}catch(Exception e1){System.out.println(e1);}
           
           int c = DAO.insertCase(caseID, caseName, caseType.toString(), creationDate);
   			if(c > 0){
   				JOptionPane.showMessageDialog(RecordForm.this,"Case Added Succesfully!");
   				LoginSuccess.main(new String[]{});
   				frame.dispose();
   			}else{
   				JOptionPane.showMessageDialog(RecordForm.this,"Sorry, unable to save case.");
   			}
	      } else {
	       	System.out.println("Selection Process Cancelled");
	      }
	}//caseCreation

}