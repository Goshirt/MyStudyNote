package com.wtt.framework.util.pdf;

import static com.wtt.framework.distribution.DtbConstant.MODULE_DISTRIBUTION;

import java.io.File;
import java.net.ConnectException;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.wtt.framework.config.ConfigManager;
import com.wtt.framework.config.IConfig;
import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;
/**
 * 
 * @author D002843
 * @author Chuck Li
 *	use open office to changge office file to PDF
 */
public class DocumentCoverter {
	private final ILog iLog = AppLogFactory.getLog(DocumentCoverter.class, MODULE_DISTRIBUTION);
	private File pdfFile;
	private File docFile;
	private String officeHost;
	private int officePort ;
	private final String PDF_CFG_KEY = "sys.pdfutil";
	private final String PDF_PARA_HOST_KEY = "office_host";
	private final String PDF_PARA_PORT_KEY = "office_port";
	
	/**
	 * 
	 * @param fileString input office file 
	 * @param outFile output pdf file 
	 */
	public DocumentCoverter(File sourceFile ,File outFile) {
		IConfig config = ConfigManager.getInstance().getConfig(PDF_CFG_KEY);
		officeHost = config.getString(PDF_PARA_HOST_KEY);
		officePort = config.getInt(PDF_PARA_PORT_KEY);
		ini(sourceFile,outFile);
	}

	/**
	 * 
	 * @param fileString
	 */
	private void ini(File sourceFile,File outFile) {
		final String THIS_METHOD = "ini";
		iLog.info(THIS_METHOD, "init the DocumentCoverter file path");
		docFile = sourceFile;
		pdfFile = outFile;
	}
	
	/**
	 * to PDF
	 * 
	 * @param file
	 */
	private void doc2pdf() throws Exception {
		final String THIS_METHOD = "doc2pdf";
		if (docFile.exists()) {
			//OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
			OpenOfficeConnection connection = new SocketOpenOfficeConnection(officeHost,officePort);
			try {
				connection.connect();
				StreamOpenOfficeDocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);
				converter.convert(docFile, pdfFile);
				connection.disconnect();
				iLog.info(THIS_METHOD, docFile.getAbsolutePath() + " to PDF Success ");
			} catch (ConnectException e) {
				iLog.error(THIS_METHOD, e.getMessage(), e);
				throw e;
			} catch (OpenOfficeException e) {
				iLog.error(THIS_METHOD, e.getMessage(), e);
				throw e;
			} catch (Exception e) {
				throw e ;
			}
		} else {
			iLog.error(THIS_METHOD, "Can not found the source file");
		}
	}

	public void conver() throws Exception {
		doc2pdf();
	}
	
}
