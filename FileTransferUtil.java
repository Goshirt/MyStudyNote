package com.wtt.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.wtt.framework.config.ConfigManager;
import com.wtt.framework.config.IConfig;
import com.wtt.framework.constant.FwkConstant;
import com.wtt.framework.distribution.UploadFTPUtil;
import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;
import java.io.InputStream;

/**
 * 
 *  @author <a href="kimsiu@wharftt.com">kim siu</a>
 * 	<P>
 *	A helper class to provide a high-level way to upload and download files via:
 *		<ul>
 *			<li>http protocol through JSF context</li>
 *			<li>ftp protocol with <a href="http://www.enterprisedt.com/products/edtftpjssl/doc/api/index.html">com.enterprisedt.net.ftp API</a></li>
 *	     </ul>
 *  </P>
 *  <p>
 *  More details about org.apache.myfaces.custom.fileupload.UploadedFile : <a href="http://myfaces.apache.org/tomahawk-project/tomahawk/apidocs/index.html">http://myfaces.apache.org/tomahawk-project/tomahawk/apidocs/index.html</a>
 *  </p>
 */
public final class FileTransferUtil {

	private FileTransferUtil(){
		
	}
	
	private final static ILog iLog = AppLogFactory.getLog(FileTransferUtil.class, FwkConstant.MODULE_UTIL);
	
	/**
	 * Config Para Keys for ftp connection.
	 */
	public static final String CONFIG_PARA_USER_KEY = "user";
	public static final String CONFIG_PARA_PWD_KEY = "pwd";
	public static final String CONFIG_PARA_HOST_KEY = "host";
	
	/**
	 *  file download method with "dialog to save or open file." in client side through <b>JSF CONTEXT</b>.<br/>
	 *  <b>CURRENTLY ONLY works with Command-BUTTONs</b> 
	 */
	public static void onFileDownloadViaHTTPJSF(String filePathStorage, String fileName) throws IOException {
		
		HttpServletResponse response = (HttpServletResponse)FacesContextUtil.getExternalContext().getResponse();
		onFileDownloadViaHTTP(filePathStorage, fileName, response);
		FacesContextUtil.getFacesContext().responseComplete();
		
	}
	
	/**
	 *  File download from FTP SERVER method with "dialog to save or open file." in client side through <b>JSF CONTEXT</b>.<br/>
	 *  <b>CURRENTLY ONLY works with Command-BUTTONs</b> 
	 *  @param configKey : key of the config defined in config.properties, the ftp connection informations are defined in DB 
	 *  @param remoteFile : the file we want to open stored in ftp server : example: "folder1/folder2/folder3/myfile.txt"
	 */
	public static void onDownloadFileFromFTPServerViaHTTPJSF(String configKey, String remoteFile) throws IOException, FTPException {
		
		iLog.info("onDownloadFileFromFTPServerViaHTTPJSF", "remoteFile:" + remoteFile);
		HttpServletResponse httpServletResponse = (HttpServletResponse)FacesContextUtil.getExternalContext().getResponse();
		onDownloadFileFromFTPServerViaHTTP(configKey, remoteFile ,httpServletResponse);
		FacesContextUtil.getFacesContext().responseComplete();
		
	}

	/**
	 * File Download from FTP SERVER method  with "dialog to save or open file." in client side .<br/>
	 * No context dependance there...
	 * @param configKey : key of the config defined in config.properties, the ftp connection informations are defined in DB 
	 * @param remoteFile : the file we want to open stored in ftp server : example: "folder1/folder2/folder3/myfile.txt"
	 * @param httpServletResponse : {@link HttpServletResponse} Object 
	 */
	public static void onDownloadFileFromFTPServerViaHTTP(String configKey, String remoteFile , HttpServletResponse httpServletResponse ) throws IOException, FTPException {
		
		byte[] bytes = getFileFromFTPServer(configKey, remoteFile);
		String fileName = FileUtil.trimFilePath(remoteFile);
		setDownLoadContentInfo(fileName, bytes.length, httpServletResponse);
		writeOutputStream(bytes, httpServletResponse);
		
	}
	
	/**
	 *   
	 *  File download method with "dialog to save or open file." in client side.<br/>
	 * @param filePathStorage : path where the file can be 
	 * @param fileName : file name 
	 * @param httpServletResponse : HttpServletResponse  
	 */
	public static void onFileDownloadViaHTTP(String filePathStorage, String fileName ,HttpServletResponse httpServletResponse) throws IOException {
		
		BufferedInputStream bin =null;
		BufferedOutputStream bout=null;
		
		try {
			File file = new File(filePathStorage, fileName);
			setDownLoadContentInfo(fileName,(int)file.length(),httpServletResponse);
				
			int len;
			byte[] buf = new byte[4096];
			
			//create FileInputStream object
			FileInputStream fin = new FileInputStream(file);
			
			//create object of BufferedInputStream
			bin  = new BufferedInputStream(fin);
			bout = new BufferedOutputStream(httpServletResponse.getOutputStream());
			
	     	while (-1!=(len=bin.read(buf))) {
	       		bout.write(buf, 0, len);
			}
			bout.flush();
			bout.close();
			bin.close();
		}
		finally {
			FileUtil.close(bin);
			FileUtil.close(bout);
		}
		
	}	
	
	/**
	 * 
	 * File upload method through <b>JSF CONTEXT</b>.<br/>
	 * 
	 * @param pathStorage : the path storage which contains the uploaded files.
	 * @param uploadedFile
	 * @param prefixe : add prefixe to the file {filename}.extension when save the file (i.e  prefixe{filename}.extension)
	 * @return the name of the file of the uploaded file.
	 * @throws Exception
	 * 
	 */	
	public static File uploadFileViaHTTPJSF(String filePathStorage , UploadedFile uploadedFile , String prefixe) throws IOException {
		
        String uploadedFileName = FileUtil.trimFilePath(uploadedFile.getName());
        
		StringBuffer finalFileNameBuffer  = new StringBuffer();
		if (prefixe != null) {
			finalFileNameBuffer.append(prefixe);
		}
		
        String finalFileName =  finalFileNameBuffer.append(uploadedFileName).toString();
        File uniqueFile = FileUtil.uniqueFile(new File(filePathStorage), finalFileName);
        FileUtil.write(uniqueFile,uploadedFile.getInputStream());
        
        return uniqueFile;
        
	}
	
	/**
	 * 
	 * Get file from server with connection informations defined in SYS_CONFIG TABLE
	 * @param configKey : key of the config defined in config.properties
	 * @param filePathStorage
	 * @param fileName
	 * @return file in byte array
	 */
	public static byte[] getFileFromFTPServer(String configKey, String remoteFile) throws IOException, FTPException {
		
    	IConfig ftpConfig = ConfigManager.getInstance().getConfig(configKey);
    	String host = ftpConfig.getString(CONFIG_PARA_HOST_KEY);
    	String user = ftpConfig.getString(CONFIG_PARA_USER_KEY);
    	String pwd  = ftpConfig.getString(CONFIG_PARA_PWD_KEY);
    	
    	return getFileFromFTPServer(host, user, pwd , remoteFile);
	    	
	}
	
	/**
	 * Get file from server with connection informations
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteFile
	 * @return file in byte array
	 */
	public static byte[] getFileFromFTPServer(String host, String user , String password , String remoteFile) throws IOException, FTPException {

    	FTPClient ftp = new FTPClient(); 
    	byte[] fileBytes = null;
    	/*
    	FTPMessageCollector listener = new FTPMessageCollector();
        ftp.setMessageListener(listener);
	    */
	    try {
			ftp.setRemoteHost(host);
			ftp.connect(); 
			ftp.login(user, password); 
			ftp.setConnectMode(FTPConnectMode.PASV); 
			ftp.setType(FTPTransferType.BINARY);
			fileBytes = ftp.get(remoteFile);
		}
		finally {
			ftp.quit();
			ftp = null;
		}
		
		return fileBytes;
		
	}

	/**
	 * 
	 * File Upload method to FTP SERVER via JSF form: 
	 *   <ol>
	 *   	<li>upload the file to tomcat via HTTP through JSF CONTEXT</li>
	 *      <li>put the file to ftp server , the destination is defined by the 2nd parameter "remoteFile"</li>
	 *   </ol>
	 * 
	 * @param configKey : key of the config defined in config.properties, the ftp connection informations are defined in DB 
	 * @param remoteFile : information of the target file which will be stored in ftp server , example: "folder1/folder2/folder3/myfile.txt"
	 * @param uploadedFile : faces' upload file object 
	 * @throws IOException
	 */
	public static void uploadFileToFTPServerViaHTTPJSF(String configKey, String remoteFile , UploadedFile uploadedFile) throws IOException, FTPException {
		
		putFileToFTPServer(configKey, remoteFile, uploadedFile.getBytes());
		
	}
	
	/**
	 * 
	 * Put File onto FTP server with connection informations defined in SYS_CONFIG TABLE
	 * @param configKey : key of the config defined in config.properties
	 * @param remoteFile : information of the target file which will be stored in ftp server , example: "folder1/folder2/folder3/myfile.txt"
	 * @param fileBytes : byte array of the file.
	 * @return file in byte array
	 */
	public static void putFileToFTPServer(String configKey, String remoteFile, byte[] fileBytes) throws IOException, FTPException {
		
		IConfig ftpConfig = ConfigManager.getInstance().getConfig(configKey);	
		String host = ftpConfig.getString(CONFIG_PARA_HOST_KEY);
		String user = ftpConfig.getString(CONFIG_PARA_USER_KEY);
		String pwd  = ftpConfig.getString(CONFIG_PARA_PWD_KEY);
		putFileToFTPServer(host, user, pwd , remoteFile, fileBytes );
	    	
	}
	
	/**
	 * Put File onto FTP server with connection informations
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteFile : target remote file 
	 * @param fileBytes file in byte array
	 * 	 
	 * <p>
	 * The difference compared to {@link UploadFTPUtil#uploadByFile(String, String, String, String, String)} is the client
	 * can create a directory in the upload phrase.
	 * </p>
	 */
	public static void putFileToFTPServer(String host, String user , String password , String remoteFile , byte[] fileBytes) throws IOException, FTPException {
		
		String methodName = " putFileToFTPServer..";
    	FTPClient ftp = new FTPClient(); 
    	/*
    	FTPMessageCollector listener = new FTPMessageCollector();
        ftp.setMessageListener(listener);
	    */
	    try {
	    	  
			ftp.setRemoteHost(host);
			ftp.connect(); 
			ftp.login(user, password); 
			ftp.setConnectMode(FTPConnectMode.PASV); 
			ftp.setType(FTPTransferType.BINARY);
			iLog.info(methodName,"remoteFile:" + remoteFile);
			String[] files = remoteFile.split("/");
			int folderSize = files.length -1;
			for(int i=0; i <folderSize; i++){ // check existance of the folders..
				
				/*
				 * 
				 *  ANOTHER WAY TO DO IS TO CHECK THE PRESENCE WITH THE REPERTORIES LISTINGS 
				 *  BUT WHEN THE NUMBERS OF FOLDERS ARE NOT NEGLIGEABLE..NOT SURE ABOUT THE PERFORMANCE..SO
				 *  
				 */
				
				try {
					iLog.info(methodName,"Trying to change directory to: " + files[i]);
					ftp.chdir(files[i]);
				}
				catch (FTPException exception) {
					iLog.info(methodName,"Trying to create directory: " + files[i]);
					ftp.mkdir(files[i]);
					
					iLog.info(methodName,"After created directory, Trying to change directory to: " + files[i]);
					ftp.chdir(files[i]);
				}
			}
			
			ftp.put( fileBytes, files[folderSize]);			

		}
		finally {
			ftp.quit();
			ftp = null;
		}
		
	}
	
	
	/**
	 * Check File is exist on FTP server
	 * @param configKey
	 * @param remoteFile
	 * @return
	 */
	public static boolean isExistFileFromFTPServer(String configKey, String remoteFile) throws IOException, FTPException {
    	IConfig ftpConfig = ConfigManager.getInstance().getConfig(configKey);
    	String host = ftpConfig.getString(CONFIG_PARA_HOST_KEY);
    	String user = ftpConfig.getString(CONFIG_PARA_USER_KEY);
    	String pwd  = ftpConfig.getString(CONFIG_PARA_PWD_KEY);
    	return isExistFileFromFTPServer(host, user, pwd , remoteFile);
	}
	
	/**
	 * Check File is exist on FTP server
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteFile: target remote file 
	 * @return
	 */
	public static boolean isExistFileFromFTPServer(String host, String user , String password , String remoteFile ) throws IOException, FTPException {
		String methodName = "isExistFileFromFTPServer";
    	FTPClient ftp = new FTPClient(); 
    	boolean result = false;
	    try {
			ftp.setRemoteHost(host);
			ftp.connect(); 
			ftp.login(user, password); 
			ftp.setConnectMode(FTPConnectMode.PASV); 
			ftp.setType(FTPTransferType.BINARY);
			iLog.info(methodName,"check file is existance:" + remoteFile);
			result = ftp.exists(remoteFile);
		} finally {
			ftp.quit();
			ftp = null;
		}
		
		return result;
	}
	

	
	/**
	 * Delete File on FTP server
	 * FileTransferUtil.deleteFileFromFTPServer("10.27.1.28", "shine", "shine", "tt\\qq\\5.jpg");
	 * @param host
	 * @param user
	 * @param password
	 * @param remoteFile : target remote file 
	 * 	 
	 */
	public static void deleteFileFromFTPServer(String host, String user , String password , String remoteFile ) throws IOException, FTPException {
		
		String methodName = " deleteFileFromFTPServer..";
    	FTPClient ftp = new FTPClient(); 

	    try {
			ftp.setRemoteHost(host);
			ftp.connect(); 
			ftp.login(user, password); 
			ftp.setConnectMode(FTPConnectMode.PASV); 
			ftp.setType(FTPTransferType.BINARY);
			iLog.info(methodName,"remoteFile:" + remoteFile);
			ftp.delete(remoteFile);
		} finally {
			ftp.quit();
			ftp = null;
		}
		
	}

	/**
	 * 
	 * @param bytes
	 * @param httpServletResponse
	 */
	public static void writeOutputStream( byte[] bytes , HttpServletResponse httpServletResponse) throws IOException {
		
		BufferedInputStream bin =null;
		BufferedOutputStream bout=null;
		
		try {

			int len;
			//create FileInputStream object
			ByteArrayInputStream fin = new ByteArrayInputStream(bytes);
			
			//create object of BufferedInputStream
			bin  = new BufferedInputStream(fin);
			bout = new BufferedOutputStream(httpServletResponse.getOutputStream());
			
	     	while (-1!=(len=bin.read(bytes))) {
	       		bout.write(bytes , 0, len);
			}
			bout.flush();
			bout.close();
			bin.close();

		} finally {
			FileUtil.close(bin);
			FileUtil.close(bout);
		}
	}
	
    /**
     * Write inputstream to file with httpServletResponse. It's highly recommended to feed the inputstream as
     * BufferedInputStream or ByteArrayInputStream as those are been automatically buffered.
     * @param inputStream The file where the given byte inputstream have to be written to.
     * @param httpServletResponse The byte inputstream which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
	public static void writeOutputStream(InputStream inputStream, HttpServletResponse httpServletResponse) throws IOException {
		
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;		
		
		try {
			byte[] bytes = new byte[1024];
			int len;			
			
			//create object of BufferedInputStream
			bin  = new BufferedInputStream(inputStream);
			bout = new BufferedOutputStream(httpServletResponse.getOutputStream());
			while (-1!=(len=bin.read(bytes))) {
	       		bout.write(bytes , 0, len);
			}
			bout.flush();
			bout.close();
			bin.close();

		} finally {
			FileUtil.close(bin);
			FileUtil.close(bout);
		}
	}

	private static void setDownLoadContentInfo(String fileName, int fileLength, HttpServletResponse httpServletResponse) {
		
		httpServletResponse.setContentType("attachment/octet-stream");
		httpServletResponse.setHeader("Content-disposition", "attachment; filename=\"" + fileName +"\"");
		httpServletResponse.setContentLength(fileLength);
		
	}
	
    /**
     * Setup the filename for client, contentType, header and encoding of httpServletResponse
     * @param fileName The filename is returned for the popup dialog of client side. 
     * @param encoding What encoding do you want to use for the returned file.
     * @param httpServletResponse just pass the httpSerletResponse.
     * @param mimeType what mime type do you want for the output.
     * @param containHeader determine the header contains header or not.
     * @throws IOException If writing file fails.
     */
	public static void setDownLoadContentInfo(String fileName, String encoding, HttpServletResponse httpServletResponse, String mimeType, boolean containHeader) {		
		httpServletResponse.setContentType(mimeType);			
		httpServletResponse.setCharacterEncoding(encoding);		
		if (containHeader)
			httpServletResponse.setHeader("Content-disposition", "attachment; filename=\"" + fileName +"\"");
	}
	
	/**
	 * File Download from HTTP SERVER method <br/>
	 * No context dependance there...
	 * @param fileName
	 * @param bytes"
	 * @param httpServletResponse : {@link HttpServletResponse} Object 
	 */
	public static void onDownloadFileFromHTTP(String fileName, byte[] bytes) throws IOException, FTPException {
		HttpServletResponse httpServletResponse = (HttpServletResponse)FacesContextUtil.getExternalContext().getResponse();
		setDownLoadContentInfo(fileName, bytes.length, httpServletResponse);
		writeOutputStream(bytes, httpServletResponse);
		FacesContextUtil.getFacesContext().responseComplete();
	}
	
}
