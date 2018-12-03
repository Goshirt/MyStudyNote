package com.wtt.framework.util.pdf;


import java.io.File;
import java.util.Date;

import com.wtt.framework.config.ConfigManager;
import com.wtt.framework.config.IConfig;
import com.wtt.framework.distribution.DtbConstant;
import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;
/**
 * change txt and office file to PDF for preview
 * 
 * @author D002843
 *
 */
public class PreviewPDFFileUtil {
	private static final String TXTSUFFIX=".txt";
	private static final String PDFSUFFIX = ".pdf";
	private static final ILog iLog = AppLogFactory.getLog(PreviewPDFFileUtil.class, "PDF_UTIL");
	/**
	 * the txt file use PdfUtil.convertTxt2PdfOffline
	 * the office file use the openoffice server.
	 * @param sourceFile input the file support the txt, doc, docx ,ppt ,pptx , xls ,xlsx
	 * @param outPath the pdf out put path 
	 * @return the pdf file 
	 * @throws Exception
	 */
	public static File getPrevieeFile (File sourceFile,String outPath) throws Exception{
		String THIS_METHOD = "getPrevieeFile";
		String fileSuffix = "";
		String fileName = "";
		if (sourceFile.getName().indexOf(".") != -1) {
			fileSuffix = sourceFile.getName().substring(sourceFile.getName().lastIndexOf("."));
			fileName = sourceFile.getName().substring(0,sourceFile.getName().lastIndexOf("."));
		}
		File tempFile = new File(outPath + fileName + new Date().getTime() + PDFSUFFIX);
		iLog.info(THIS_METHOD, "file:" + sourceFile.getName() + " start changhe to PDF");
		IConfig config = ConfigManager.getInstance().getConfig(DtbConstant.CFG_KEY_DTB);
		String fomart = config.getString(DtbConstant.CFG_PREVIEW_FOMART);
		if(fileSuffix.toLowerCase().equals(TXTSUFFIX)){
			PdfUtil.convertTxt2PdfOffline(PdfUtil.SUPPORTED_PAGE_SIZE.A4_PORTRAIT,
					"WTT", 
					"", 
					"", 
					"", 
					Float.parseFloat("10"), 
					Float.parseFloat("10"), 
					Float.parseFloat("10"), 
					Float.parseFloat("10"),
					Float.parseFloat("10"),
					Float.parseFloat("10"), 
					tempFile, 
					"UTF-8", 
					Boolean.parseBoolean("false"), 
					tempFile.getAbsolutePath(), 
					Integer.parseInt("0"), 
					Boolean.parseBoolean("false"), 
					Boolean.parseBoolean("false"), 
					"",
					"true");
			
		}else if (fileSuffix.toLowerCase().equals(PDFSUFFIX)) {
			sourceFile.renameTo(tempFile);
		}else if ((fomart.toUpperCase()).contains(fileSuffix.toUpperCase())) {
			DocumentCoverter coverter = new DocumentCoverter(sourceFile,tempFile);
			coverter.conver();
		}else  {
			throw new RuntimeException("the file : " + fileSuffix + "  type can not to preview");
		}
		
		if (tempFile.exists()){
			iLog.info(THIS_METHOD, "file:" + sourceFile.getName() + "changge to PDF success");
			return tempFile;
		}else {
			throw new RuntimeException(  "file " + sourceFile.getName() + " can not turn to PDF for preview");
		}
		
	}
}
