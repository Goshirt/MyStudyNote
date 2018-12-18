package com.wtt.framework.distribution;

import static com.wtt.framework.distribution.DtbConstant.CFG_AS_OF_DATE;
import static com.wtt.framework.distribution.DtbConstant.CFG_DESTINATION_DIR;
import static com.wtt.framework.distribution.DtbConstant.CFG_FTP_IP;
import static com.wtt.framework.distribution.DtbConstant.CFG_FTP_PORT;
import static com.wtt.framework.distribution.DtbConstant.CFG_UPLOAD_IS_TRACE;
import static com.wtt.framework.distribution.DtbConstant.CFG_UPLOAD_PWD;
import static com.wtt.framework.distribution.DtbConstant.CFG_UPLOAD_USER;
import static com.wtt.framework.distribution.DtbConstant.DOC_CODE_SEPARATOR;
import static com.wtt.framework.distribution.DtbConstant.SUFFIX_NEW;
import static com.wtt.framework.distribution.DtbConstant.SUFFIX_READY;
import static com.wtt.framework.distribution.DtbConstant.SUFFIX_ZIP;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.FileTransferOutputStream;

/**
 * @author Vincent Mak
 * @author Danny Wong Chun W003372
 * @author Rainy Liu D000225
 */
public class UploadFTPUtil {

//	private final byte[] toBeDel = "To be deleted".getBytes();
	private final byte[] file_size = "file_size=".getBytes();
	// <START> D000225 Rainy Liu, Zhao Ming 2009-Nov-09
	private final byte[] file_name = "file_name=".getBytes();
	// < END > D000225 Rainy Liu, Zhao Ming 2009-Nov-09
	// <START> D000375 Raymon Li, Wei Wen 2011-01-13 16:06:99
	private final byte[] file_add_uid = "file_add_uid=".getBytes();
	// < END > D000375 Raymon Li, Wei Wen 2011-01-13 16:06:99
	private static boolean isTrace;
	private char ftpSrvSeparator = '/';
	private SimpleDateFormat sdf;

	private FileTransferClient ftClient;

	/*
	 * default constructor should not be called.
	 */
	protected UploadFTPUtil() {
	}

	/**
	 * Constructor
	 * 
	 * @param ftpIPAddress
	 *            ftp server IPv4 address
	 * @param ftpPortNum
	 *            ftp server port
	 * @param ftpUser
	 *            ftp connecting user
	 * @param ftpPassword
	 *            ftp connecting password
	 * @param asOfDateFormat
	 *            document as of date format default as yyMMdd
	 * @throws Exception
	 *             exception if fail to initialize ftp client
	 */
	public UploadFTPUtil(String ftpIPAddress, int ftpPortNum, String ftpUser, String ftpPassword, String asOfDateFormat) throws Exception {

		writeOut("UploadFTPUtil constructor called");

		// exception when connected
		ftClient = new FileTransferClient();
		ftClient.setRemoteHost(ftpIPAddress);
		ftClient.setRemotePort(ftpPortNum);
		ftClient.setUserName(ftpUser);
		ftClient.setPassword(ftpPassword);
		ftClient.setContentType(FTPTransferType.BINARY);

		// DEV 1.4 -> 1.5
		// Added as of date format in constructor
		// the SimpleDateFormat is removed in dtbConstant
		sdf = new SimpleDateFormat(asOfDateFormat);

	}

	/**
	 * Upload data to FTP via an absolute file name.
	 * 
	 * @param system
	 *            system of the document code belongs to in String
	 * @param docCode
	 *            document code in String
	 * @param destDir
	 *            destination directory in String
	 * @param filePath
	 *            the absolute path name of the file to be uploaded
	 * @param asOfDate
	 *            the as of date value of the file to be uploaded
	 * @throws Exception
	 *             exception if any ftp operation fails
	 * 
	 */
	public void uploadByFile(String system, String destDir, String docCode, String filePath, String asOfDate, Integer uid) throws Exception {

		try {
			File inFile = new File(filePath);
			if (!inFile.exists() || !inFile.isFile() || !inFile.canRead()) {
				throw new IOException("File [" + filePath + "] either does not exist or not readable!");
			}
			
			File tempZipFile = null;
			long fileSize = 0; // file size
			String fileName = "";
//			 all file change to zip file 
//			if(isZipFile(filePath)){
//				tempZipFile = new File(filePath);
//				fileSize = tempZipFile.length();
//				fileName = tempZipFile.getName();		
//				writeOut("The upload file type is zip, size:" + fileSize + " , name:" + fileName);	
//			}else{		
				tempZipFile =File.createTempFile("upload", SUFFIX_ZIP);

				// DEV 1.4 -> 1.5
				// put the zipping code into a try catch block
				// to ensure both FileInputStream and ZipOutputStream
				// are ALWAYS closed. Core logic is not modified.
				ZipOutputStream zos = null;
				FileInputStream fis = null;
			
				try {
					zos = new ZipOutputStream(new FileOutputStream(tempZipFile));
					zos.setLevel(Deflater.DEFAULT_COMPRESSION);
					fis = new FileInputStream(filePath);
					fileName = new File(filePath).getName();
					zos.putNextEntry(new ZipEntry(new File(filePath).getName()));			
					BufferedInputStream bis = null;
					bis = new BufferedInputStream(fis);		
					int bufferSize = 1024;
					int count;
					byte data[] = new byte[bufferSize];
					while ((count = bis.read(data, 0, bufferSize)) != -1) {
						zos.write(data, 0, count);
						fileSize = fileSize + count;
					}
				
					zos.closeEntry();
				} catch (IOException ioe) {
					throw ioe;
				} finally {
					if (fis != null) {
						fis.close();
						fis = null;
					}
					if (zos != null) {
						zos.close();
						zos = null;
					}
				}
			writeOut("zipped data to tmp file : " + tempZipFile.getName());
		 //}
			
			ftClient.connect();
			writeOut("uploadByFile() ftp client connected to " + ftClient.getRemoteHost());

			String home = ftClient.getRemoteDirectory();
			if (home.indexOf('/') == -1) {
				ftpSrvSeparator = '\\';
			}

			// DEV 1.4 -> 1.5
			// Comment out by Danny Wong Chun W003372
			//
			// REASON
			// =======================================================================================
			// This uploading util should not create a directory in the upload
			// phrase.
			// Client should make sure they have CREATED the corresponding the
			// document code
			// in the database and the directory is created before they upload.
			// 
			// Logically, document code is maintained (Create, Update, Delete?)
			// by Document Code
			// Maintenance Module by some authorized personels. Upon the code
			// creation, the directory
			// should be also created on their behalf INSTEAD OF this client.
			// 
			// CHANGE SUMMARY
			// =======================================================================================
			// The document code directory creation is moved to the Document
			// Code Maintenance Module.

			StringBuffer uploadPathBuf = new StringBuffer(16);
			// Example upload path - CSBS/IN_QUEUE
			uploadPathBuf.append(system).append(ftpSrvSeparator).append(destDir);
			ftClient.changeDirectory(uploadPathBuf.toString()); // exception
			// when not a
			// folder or
			// fail to
			// change
			// directory
			writeOut("Change directory to " + uploadPathBuf.toString());

			String remoteFileName = latestFileName(docCode, asOfDate);

			// exception when file has problem
			ftClient.uploadFile(tempZipFile.getCanonicalPath(), remoteFileName + SUFFIX_NEW);
			writeOut("uploaded zip file to " + remoteFileName + SUFFIX_NEW);

			// mark the .rdy upon entire upload completes
			markFinished(remoteFileName, fileSize, fileName, uid);
			
            if(!isZipFile(filePath)){
            	tempZipFile.delete();
            	writeOut("temp zip file deleted");
            }

		} catch (Exception e) {
			throw e;
		} finally {
			if (ftClient.isConnected())
				ftClient.disconnect();
		}
	}

	/**
	 * Upload data to FTP via byte stream.
	 * 
	 * @param system
	 *            system of the document code belongs to in String
	 * @param destDir
	 *            destination directory in String
	 * @param docCode
	 *            document code in String
	 * @param fileByte
	 *            the byte array of the file
	 * @param asFileName
	 *            the remote file name
	 * @param asOfDate
	 *            the as of date value of the file to be uploaded
	 * @throws Exception
	 * 
	 * @author D000225 Rainy Liu, Zhao Ming 2009-Nov-09 Add param preserved
	 */
	public void uploadByByte(String system, String destDir, String docCode, byte[] fileByte, String asFileName, String asOfDate, Integer uid)
			throws Exception {

		try {
			File tempZipFile = File.createTempFile("upload", SUFFIX_ZIP);

			// DEV 1.4 -> 1.5
			// put the zipping code into a try catch block
			// to ensure both ZipOutputStream
			// are ALWAYS closed. Core logic is not modified.
			ZipOutputStream zos = null;
			long fileSize = fileByte.length;
			try {
				zos = new ZipOutputStream(new FileOutputStream(tempZipFile));
				zos.setLevel(Deflater.DEFAULT_COMPRESSION);
				zos.putNextEntry(new ZipEntry(asFileName));
				zos.write(fileByte);
				zos.closeEntry();
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				if (zos != null) {
					zos.close();
					zos = null;
				}
			}
			writeOut("zipped data to tmp file : " + tempZipFile.getName());
			
			ftClient.connect();
			writeOut("uploadByByte() ftp client connected to " + ftClient.getRemoteHost());

			String home = ftClient.getRemoteDirectory();
			if (home.indexOf('/') == -1) {
				ftpSrvSeparator = '\\';
			}

			// DEV 1.4 -> 1.5
			// Comment out by Danny Wong Chun W003372
			//
			// REASON
			// =======================================================================================
			// This uploading util should not create a directory in the upload
			// phrase.
			// Client should make sure they have CREATED the corresponding the
			// document code
			// in the database and the directory is created before they upload.
			// 
			// Logically, document code is maintained (Create, Update, Delete?)
			// by Document Code
			// Maintenance Module by some authorized personels. Upon the code
			// creation, the directory
			// should be also created on their behalf INSTEAD OF this client.
			// 
			// CHANGE SUMMARY
			// =======================================================================================
			// The document code directory creation is moved to the Document
			// Code Maintenance Module.
			//

			StringBuffer uploadPathBuf = new StringBuffer(16);
			// Example upload path - CSBS/IN_QUEUE/
			uploadPathBuf.append(system).append(ftpSrvSeparator).append(destDir);
			ftClient.changeDirectory(uploadPathBuf.toString()); // exception
			// when not a
			// folder or
			// fail to
			// change
			// directory
			writeOut("Change directory to " + uploadPathBuf.toString());

			String remoteFileName = latestFileName(docCode, asOfDate);

			ftClient.uploadFile(tempZipFile.getCanonicalPath(), remoteFileName + SUFFIX_NEW); // exception
			// when
			// file
			// has
			// problem
			writeOut("uploaded zip file " + remoteFileName + SUFFIX_NEW);

			// mark the .rdy upon entire upload completes
			// String file_name = asFileName.substring(0,
			// asFileName.lastIndexOf("."));
			markFinished(remoteFileName, fileSize, asFileName, uid);
			tempZipFile.delete();
			writeOut("temp zip file deleted");

		} catch (Exception e) {
			throw e;
		} finally {
			if (ftClient.isConnected())
				ftClient.disconnect();
		}
	}
	/*
	 * Return default as of date in String format. The date format is defined in
	 * property file
	 */
	private String defaultAsOfDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1); // current date - 1
		return sdf.format(calendar.getTime());
	}

	/*
	 * Return latest file name and write .del file for old files
	 */
	private String latestFileName(String docCode, String asOfDate) throws Exception {
		if (asOfDate == null || asOfDate.equals("")) {
			asOfDate = defaultAsOfDate();
		} else {
			sdf.parse(asOfDate); // throw exception when date format invalid
		}

		// DOCCODE12_____081017_123123233223
		return docCode + DOC_CODE_SEPARATOR + asOfDate + "_" + Calendar.getInstance().getTimeInMillis();
	}

	/*
	 * mark finished by piping some text to the .rdy file #20090121 _Rainy#->add
	 * fileSize in function
	 */
	private void markFinished(String remoteFileName, long fileSize, String fileName, Integer uid) throws Exception {
		FileTransferOutputStream ftos = ftClient.uploadStream(remoteFileName + SUFFIX_READY);
		String tmpFileSize = String.valueOf(fileSize).trim();
		try {
			// ftos.write(fileReady);
			ftos.write(file_size);
			ftos.write(tmpFileSize.getBytes());
			ftos.write(";".getBytes());
			ftos.write(file_name);
			ftos.write(fileName.getBytes());
			ftos.write(";".getBytes());
			ftos.write(file_add_uid);
			ftos.write(uid.toString().getBytes());
		} catch (Exception e) {
			throw e;
		} finally {
			ftos.close();
			ftos = null;
			writeOut("marked finished");
		}
	}

	/*
	 * Desc : print a trace message to standard out
	 * 
	 * Input : paraStr - the string to print
	 */
	private void writeOut(String paraStr) throws Exception {
		if (isTrace)
			System.out.println((new Date()).toString() + " --> " + paraStr);
	}
	
	public boolean isZipFile(String filePath) throws Exception{
		boolean isZip = false;	
		String sufix = filePath.substring(filePath.lastIndexOf(".") + 1);
		if (sufix!=null && !sufix.equals("")){
			if (("."+sufix).toLowerCase().contains(SUFFIX_ZIP)){
				isZip = true ;
			}
		}
		return isZip;
	}
	
	/*
	 * Check the file type whether is zip
	 * @param filePath
	 *            the absolute path name of the file to be checked
	 * @throws Exception
	 */
/*	public boolean isZipFile2(String filePath) throws Exception{
		boolean isZip = false;	
		FileInputStream is = null;
		try {
			is = new FileInputStream(filePath);
			byte[] b = new byte[4];
			is.read(b, 0, b.length);  
			StringBuilder stringBuilder = new StringBuilder();     
			if (b == null || b.length <= 0) {     
				isZip = false;
			}else{
				for (int i = 0; i < b.length; i++) {   
					int v = b[i] & 0xFF;     
					String hv = Integer.toHexString(v);     
					if (hv.length() < 2) {     
						stringBuilder.append(0);     
					}     
					stringBuilder.append(hv);     
				}     
				String type = stringBuilder.toString().toUpperCase(); 
				if(type.contains("504B0304")){ 
					isZip = true;
				}else{
					isZip =  false;
				} 
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(is != null){
			   is.close();
			   is = null;	
			}
		}    
        return isZip;
	}
	*/

	/*
	 * main driver program
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Not enough parameter!");
			System.exit(1);
		}

		// We considered there are 2 ways of implementations for getting the
		// system name
		// 1.) Given to the given docCode and retrieve the corresponding system
		// accordingly
		// - to minimize the user input and hence the errors
		// 2.) Get the system name from command line argument
		// -
		// 
		// Our final decision is to get the system name from command line
		// arguement because
		// 1.) We do not want the upload agent to be bonded with the database
		// and with the
		// fat wttFrameworkOffline.jar
		// 2.) There is severe overhead as there is database communication in
		// each agent
		// innovation upon upload.
		// 3.) The 'system' should be coded in script and can be verified

		String config = args[0];
		String system = args[1];
		String docCode = args[2];
		String filePath = args[3];
		String asOfDate = args.length > 4 ? args[4] : "";
		String addUidStr = args.length > 5? args[5]: "";
		int addUid = 2;
		try {addUid = Integer.valueOf(addUidStr);} catch (Exception e) {}

		try {
			System.out.println(new Date().toString() + "--> UploadFTPUtil.main() started...");
			System.out.println(new Date().toString() + "------> config___= [" + config + "]");
			System.out.println(new Date().toString() + "------> system___= [" + system + "]");
			System.out.println(new Date().toString() + "------> docCode__= [" + docCode + "]");
			System.out.println(new Date().toString() + "------> filePath_= [" + filePath + "]");
			System.out.println(new Date().toString() + "------> asOfDate_= [" + asOfDate + "]");
			System.out.println(new Date().toString() + "------> addUidStr= [" + addUidStr + "]");

			Properties prop = new Properties();
			prop.load(new FileInputStream(config));

			String ip = prop.getProperty(CFG_FTP_IP);
			int port = new Integer(prop.getProperty(CFG_FTP_PORT, "21")).intValue();
			String user = prop.getProperty(CFG_UPLOAD_USER);
			String password = prop.getProperty(CFG_UPLOAD_PWD);
			String strIsTrace = prop.getProperty(CFG_UPLOAD_IS_TRACE, "false");
			if (strIsTrace != null) {
				isTrace = "true".equalsIgnoreCase(strIsTrace);
			} else {
				isTrace = false;
			}
			String asOfDateFormat = prop.getProperty(CFG_AS_OF_DATE);
			String destinationDir = prop.getProperty(CFG_DESTINATION_DIR, "IN_QUEUE");


			System.out.println(new Date().toString() + "------------------");
			System.out.println(new Date().toString() + "------> IP__= [" + ip + "]");
			System.out.println(new Date().toString() + "------> Port= [" + String.valueOf(port) + "]");
			System.out.println(new Date().toString() + "------> User= [" + user + "]");
			System.out.println(new Date().toString() + "------> addUid= [" + String.valueOf(addUid) + "]");
			System.out.println(new Date().toString() + "------> asOfDateFormat= [" + asOfDateFormat + "]");
			System.out.println(new Date().toString() + "------> destinationDir= [" + destinationDir + "]");
			System.out.println(new Date().toString() + "------------------");


			UploadFTPUtil util = new UploadFTPUtil(ip, port, user, password, asOfDateFormat);

			util.uploadByFile(system, destinationDir, docCode, filePath, asOfDate, addUid);
			
//			String asFileName = new File(filePath).getName();
//			File file = new File(filePath);
//			InputStream is = new FileInputStream(file); 
//			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();  
//			int ch;  
//			while ((ch = is.read()) != -1) {  
//				bytestream.write(ch);  
//			   }  
//			   byte imgdata[] = bytestream.toByteArray(); 
//			util.uploadByByte(system, destinationDir, docCode, imgdata, asFileName, asOfDate, addUid);

		} catch (Exception e) {
			System.err.println(new Date().toString() + "--> UploadFTPUtil.main() exits with ERROR");
			e.printStackTrace();
			System.exit(1);
		} finally {
			System.out.println(new Date().toString() + "--> UploadFTPUtil.main() finished!");
			System.exit(0);
		}
	}

}
