/**
 * 2009 All rights Reserved by Wharf T&T Java Development Team
 * 
 * Project		: framework
 * Package		: com.wtt.framework.util.pdf
 * File			: PdfUtil.java
 * Creation TS	: Oct 28, 2009 4:29:14 PM
 * 
 * ==============================================
 */

package com.wtt.framework.util.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.wtt.framework.constant.FwkConstant;
import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;
import com.wtt.framework.util.FileUtil;
import com.wtt.framework.util.StringUtil;

/**
 * PDF Utility converts txt, csv and html to PDF.
 * 
 * @author Danny Wong Chun W003372
 */
public class PdfUtil {
	
	private static ILog iLog = AppLogFactory.getLog(PdfUtil.class, FwkConstant.MODULE_UTIL);

	private static final PdfUtil instance = new PdfUtil();

	/**
	 * Supported page size in our utility
	 * A4 portrait and landscape
	 * A3 portrait and landscape
	 *
	 * @author Danny Wong Chun W003372
	 */
	public static enum SUPPORTED_PAGE_SIZE {
		A4_PORTRAIT, A4_LANDSCAPE, A3_PORTRAIT, A3_LANDSCAPE
	}

	/**
	 * Defualt PDF page size, A4 landscape.
	 */
	private static final Rectangle DEFAULT_PAGE_SIZE = PageSize.A4.rotate();
	
	/**
	 * Default PDF author in 'PDF Document Properties'
	 */
	private static final String DEFAULT_AUTHOR = "WTT";
	
	/**
	 * Default PDF subject in 'PDF Document Properties'
	 */
	private static final String DEFAULT_SUBJECT = "WTT";
	
	/**
	 * Default PDF title in 'PDF Document Properties'
	 */
	private static final String DEFAULT_TITLE = "WTT";
	
	/**
	 * Default PDF producer in 'PDF Document Properties'
	 */
	private static final String DEFAULT_PRODUCER = "WTT";
	
	// The PDF margin is calculated as followings
	// a margin of 36 pt (0.5 in),
	// a margin of 72 pt (1 in),
	// a margin of 108 pt (1.5 in),
	// a margin of 180 pt (2.5 in).
	/**
	 * default left margin
	 */
	private static final float DEFAULT_LEFT_MARGIN = 36;
	
	/**
	 * Default right margin
	 */
	private static final float DEFAULT_RIGHT_MARGIN = 36;
	
	/**
	 * Default top margin
	 */
	private static final float DEFAULT_TOP_MARGIN = 36;
	
	/**
	 * Default bottom margin
	 */
	private static final float DEFAULT_BOTTOM_MARGIN = 36;
	
	/**
	 * Default encoding
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * Default number of column header row
	 */
	private static final int DEFAULT_HEADER_ROW = 0;
	
	/**
	 * Default font size
	 */
	private static final int DEFAULT_FONT_SIZE = 8;
	
	/**
	 * Default CSV field delimitor
	 */
	private static final String DEFAULT_DELIMITOR = ",";

	/**
	 * constructor
	 */
	private PdfUtil() {
		// do nothing!
	}

	/**
	 * Get PdfUtil singleton instance.
	 * @return
	 */
	public static PdfUtil getInstance() {
		return instance;
	}
	
	/**
	 * Map given page size string to our supported page size. Non-matched items
	 * all go to default page size.
	 * 
	 * @param pageSize
	 * @return SUPPORTED_PAGE_SIZE
	 */
	public static SUPPORTED_PAGE_SIZE mapPageSize(String pageSize) {
		final String THIS_METHOD = "mapPageSize";
		if ("A4".equalsIgnoreCase(pageSize)) {
			return SUPPORTED_PAGE_SIZE.A4_PORTRAIT;
		} else if ("A4R".equalsIgnoreCase(pageSize)) {
			return SUPPORTED_PAGE_SIZE.A4_LANDSCAPE;
		} else if ("A3".equalsIgnoreCase(pageSize)) {
			return SUPPORTED_PAGE_SIZE.A3_PORTRAIT;
		} else if ("A3R".equalsIgnoreCase(pageSize)) {
			return SUPPORTED_PAGE_SIZE.A3_LANDSCAPE;
		} else {
			iLog.warn(THIS_METHOD, "Invalid page size [" + pageSize + "] detected. Using default.");
			return SUPPORTED_PAGE_SIZE.A4_LANDSCAPE;
		}
	}

	/**
	 * Map our supported page size to iText constant. Default or non-matched items
	 * all go to default page size.
	 *  
	 * @param pageSize SUPPORTED_PAGE_SIZE
	 * @return
	 * @see PdfUtil#DEFAULT_PAGE_SIZE
	 */
	public static Rectangle mapPageSize(SUPPORTED_PAGE_SIZE pageSize) {
		switch (pageSize) {
		case A4_PORTRAIT:
			return PageSize.A4;
		case A4_LANDSCAPE:
			return PageSize.A4.rotate();
		case A3_PORTRAIT:
			return PageSize.A3;
		case A3_LANDSCAPE:
			return PageSize.A3.rotate();
		default:
			return DEFAULT_PAGE_SIZE;
		}
	}

	/**
	 * Convert text to PDF file. Column header will be repeated in each page if parameter
	 * 'columnHeaderNoOfLines' > 0.
	 * 
	 * <p>Please be aware the Latin characters in Chinese font is proportional (not mono)</p>
	 * 
	 * @param pageSize 					page size of output PDF.
	 * @param author 					author of the PDF in the PDF 'Document Properties'
	 * @param producer					producer of the PDF in the PDF 'Document Properties'
	 * @param subject					subject of the PDF in the PDF 'Document Properties'
	 * @param title 					title of the PDF in the PDF 'Document Properties'
	 * @param leftMargin 				left margin of the PDF
	 * @param rightMargin 				right margin of the PDF
	 * @param topMargin 				top margin of the PDF
	 * @param bottomMargin 				bottom margin of the PDF
	 * @param headerFontSize			header font size of the PDF, in pt.
	 * @param fontSize					font size of the PDF, in pt.
	 * @param txtFile 					java File object pointing to the source file.
	 * @param fileEncoding 				TODO file encoding of the soruce file.
	 * @param withChinese 				indicator the input source file contains Chinese characters.
	 * @param outputAbsolutePath 		absolute path for the output PDF file.
	 * @param columnHeaderNoOfLines		number of lines in the source file are column header. Can be 0..n.
	 * @param enablePageNum 		 	boolean to specify whether to enable the page numbers.
	 * @param enablePageNavigation		boolean to specify whether to enable to paging navigation,
	 * @param watermarkText				the watermark text to be printed on each page of the PDF. Null of empty means no watermark.
	 * @param manualPageBreakIndicator	manual page break indicator. When program encounters a line contains this indicator, then will explicit make a page break.
	 * @throws Exception				in case of any exception...
	 */
	public static void convertTxt2PdfOffline(SUPPORTED_PAGE_SIZE pageSize, String author, String producer, String subject, String title,
			float leftMargin, float rightMargin, float topMargin, float bottomMargin, float headerFontSize, float fontSize, File txtFile, String fileEncoding,
			boolean withChinese, String outputAbsolutePath, int columnHeaderNoOfLines, boolean enablePageNum, boolean enablePageNavigation,
			String watermarkText, String manualPageBreakIndicator) throws Exception {
		
		final String THIS_METHOD = "convertTxt2PdfOffline";

		author = StringUtil.isEmptyOrNull(author) ? DEFAULT_AUTHOR : author;
		producer = StringUtil.isEmptyOrNull(producer) ? DEFAULT_PRODUCER : producer;
		subject = StringUtil.isEmptyOrNull(subject) ? DEFAULT_SUBJECT : subject;
		title = StringUtil.isEmptyOrNull(title) ? DEFAULT_TITLE : title;
		leftMargin = leftMargin < 0 ? DEFAULT_LEFT_MARGIN : leftMargin;
		rightMargin = rightMargin < 0 ? DEFAULT_RIGHT_MARGIN : rightMargin;
		topMargin = topMargin < 0 ? DEFAULT_TOP_MARGIN : topMargin;
		bottomMargin = bottomMargin < 0 ? DEFAULT_BOTTOM_MARGIN : bottomMargin;
		
		Document document = new Document(mapPageSize(pageSize), leftMargin, rightMargin, topMargin, bottomMargin);
		document.addAuthor(author);
		document.addCreationDate();
		document.addSubject(subject);
		document.addTitle(title);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputAbsolutePath));
			convertTxt2PdfAux(writer, document, headerFontSize, fontSize, txtFile, fileEncoding, withChinese, 
							  columnHeaderNoOfLines, enablePageNum, enablePageNavigation, watermarkText, manualPageBreakIndicator);
			

		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	/**
	 * Convert text to PDF file. Column header will be repeated in each page if parameter
	 * 'columnHeaderNoOfLines' > 0.
	 * 
	 * <p>Please be aware the Latin characters in Chinese font is proportional (not mono)</p>
	 * 
	 * @param pageSize 					page size of output PDF.
	 * @param author 					author of the PDF in the PDF 'Document Properties'
	 * @param producer					producer of the PDF in the PDF 'Document Properties'
	 * @param subject					subject of the PDF in the PDF 'Document Properties'
	 * @param title 					title of the PDF in the PDF 'Document Properties'
	 * @param leftMargin 				left margin of the PDF
	 * @param rightMargin 				right margin of the PDF
	 * @param topMargin 				top margin of the PDF
	 * @param bottomMargin 				bottom margin of the PDF
	 * @param headerFontSize			header font size of the PDF, in pt.
	 * @param fontSize					font size of the PDF, in pt.
	 * @param txtFile 					java File object pointing to the source file.
	 * @param fileEncoding 				TODO file encoding of the soruce file.
	 * @param withChinese 				indicator the input source file contains Chinese characters.
	 * @param response					HTTP servlet response.
	 * @param outputfileName			File name for the output PDF.
	 * @param columnHeaderNoOfLines		number of lines in the source file are column header. Can be 0..n.
	 * @param enablePageNum 		 	boolean to specify whether to enable the page numbers.
	 * @param enablePageNavigation		boolean to specify whether to enable to paging navigation,
	 * @param watermarkText				the watermark text to be printed on each page of the PDF. Null of empty means no watermark.
	 * @param manualPageBreakIndicator	manual page break indicator. When program encounters a line contains this indicator, then will explicit make a page break.
	 * @throws Exception				in case of any exception...
	 */
	public static void convertTxt2PdfOnline(SUPPORTED_PAGE_SIZE pageSize, String author, String producer, String subject, String title,
			float leftMargin, float rightMargin, float topMargin, float bottomMargin, float headerFontSize, float fontSize, File txtFile, String fileEncoding,
			boolean withChinese, HttpServletResponse response, String outputFileName, int columnHeaderNoOfLines, boolean enablePageNum, boolean enablePageNavigation,
			String watermarkText, String manualPageBreakIndicator) throws Exception {
		
		final String THIS_METHOD = "convertTxt2PdfOffline";

		author = StringUtil.isEmptyOrNull(author) ? DEFAULT_AUTHOR : author;
		producer = StringUtil.isEmptyOrNull(producer) ? DEFAULT_PRODUCER : producer;
		subject = StringUtil.isEmptyOrNull(subject) ? DEFAULT_SUBJECT : subject;
		title = StringUtil.isEmptyOrNull(title) ? DEFAULT_TITLE : title;
		leftMargin = leftMargin < 0 ? DEFAULT_LEFT_MARGIN : leftMargin;
		rightMargin = rightMargin < 0 ? DEFAULT_RIGHT_MARGIN : rightMargin;
		topMargin = topMargin < 0 ? DEFAULT_TOP_MARGIN : topMargin;
		bottomMargin = bottomMargin < 0 ? DEFAULT_BOTTOM_MARGIN : bottomMargin;
		
		Document document = new Document(mapPageSize(pageSize), leftMargin, rightMargin, topMargin, bottomMargin);
		document.addAuthor(author);
		document.addCreationDate();
		document.addSubject(subject);
		document.addTitle(title);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + outputFileName + "\"");
			
			convertTxt2PdfAux(writer, document, headerFontSize, fontSize, txtFile, fileEncoding, withChinese, 
							  columnHeaderNoOfLines, enablePageNum, enablePageNavigation, watermarkText, manualPageBreakIndicator);
			

		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	/**
	 * Internal AUX method for TXT-to-PDF operations.
	 * 
	 * @param writer
	 * @param document
	 * @param fontSize
	 * @param csvFile
	 * @param fileEncoding						TODO
	 * @param withChinese
	 * @param columnHeaderNoOfLines
	 * @param enablePageNum
	 * @param enablePageNavigation
	 * @param watermarkText
	 * @param manualPageBreakIndicator
	 * @throws Exception
	 */
	private static void convertTxt2PdfAux(PdfWriter writer, Document document, float headerFontSize, float fontSize, File txtFile, String fileEncoding,
			boolean withChinese, int columnHeaderNoOfLines, boolean enablePageNum, boolean enablePageNavigation,
			String watermarkText, String manualPageBreakIndicator) throws Exception {
		
		final String THIS_METHOD = "convertTxt2PdfAux";
		try {

			BufferedReader reader = (BufferedReader) FileUtil.readReader(txtFile);

			// read first n lines and save it in headerTextBuffer whereas n is
			// 0..n
			StringBuilder headerTextBuffer = new StringBuilder();
			for (int i = 0; i < columnHeaderNoOfLines; i++) {
				headerTextBuffer.append(reader.readLine());
				if (i != columnHeaderNoOfLines -1) headerTextBuffer.append('\n');
			}

			WttPdfHeaderFooter ex = new WttPdfHeaderFooter(headerTextBuffer.toString(), headerFontSize, enablePageNum, enablePageNavigation, watermarkText, columnHeaderNoOfLines);
			writer.setPageEvent(ex);
			document.open();

			String line;
			BaseFont bf = null;
			if (withChinese) {
				// http://itextdocs.lowagie.com/tutorial/fonts/getting/index.php
				// Traditional-Chinese: MHei-Medium, MSung-Light and
				// MSungStd-Light with the encodings UniCNS-UCS2-H and
				// UniCNS-UCS2-V
				bf = BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
			} else {
				bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			}
			Font font = new Font(bf, fontSize);

			boolean isManualBreak = false;
			if (!StringUtil.isEmptyOrNull(manualPageBreakIndicator)) isManualBreak = true; 
			while ((line = reader.readLine()) != null) {
				if (isManualBreak && line.indexOf(manualPageBreakIndicator) != -1) {
					document.newPage();
				} else {
					Paragraph p = new Paragraph(line, font);
					document.add(p);
				}
			}
			reader.close();

		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
			throw new Exception(e);
		}

		document.close();
	}
	
	/**
	 * Internal AUX method for CVS-to-PDF operations.
	 * 
	 * @param writer
	 * @param document
	 * @param fontSize
	 * @param csvFile
	 * @param fileEncoding
	 * @param withChinese
	 * @param columnHeaderNoOfLines
	 * @param enablePageNum
	 * @param enablePageNavigation
	 * @param watermarkText
	 * @param delimitor
	 * @param manualPageBreakIndicator
	 * @throws Exception
	 */
	private static void convertCsv2PdfAux(PdfWriter writer, Document document, float fontSize, File csvFile, String fileEncoding,
			boolean withChinese, int columnHeaderNoOfLines, boolean enablePageNum, boolean enablePageNavigation,
			String watermarkText, String delimitor, String manualPageBreakIndicator) throws Exception {
		
		final String THIS_METHOD = "convertCsv2PdfAux";

		try {
			WttPdfHeaderFooter ex = new WttPdfHeaderFooter(null, 0, enablePageNum, enablePageNavigation, watermarkText, columnHeaderNoOfLines);
			writer.setPageEvent(ex);
			document.open();

			BaseFont bf = null;
			if (withChinese) {
				// http://itextdocs.lowagie.com/tutorial/fonts/getting/index.php
				// Traditional-Chinese: MHei-Medium, MSung-Light and
				// MSungStd-Light with the encodings UniCNS-UCS2-H and
				// UniCNS-UCS2-V
				bf = BaseFont.createFont("MHei-Medium", "UniCNS-UCS2-H", BaseFont.NOT_EMBEDDED);
			} else {
				bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			}
			Font font = new Font(bf, fontSize);

			BufferedReader reader = (BufferedReader) FileUtil.readReader(csvFile);
			String line = reader.readLine();
			int noOfColumn = 1;
			boolean lineNull = StringUtil.isEmptyOrNull(line);
			if (lineNull) {
				throw new Exception("No line can be read from file.");
			} else {
				String[] splits = line.split(delimitor);
				if (splits != null) {
					noOfColumn = splits.length;
				}
			}
			
			PdfPTable table = new PdfPTable(noOfColumn);
			table.setHeaderRows(columnHeaderNoOfLines);
			table.setWidthPercentage(100);
			// FAQ
			// Is it possible to have the column width change dynamically based on the content 
			// of the cells? PDF isn't HTML, and a PdfPTable is completely different 
			// from an HTML table rendered in a browser; iText can't calculate column widths based on the content of the columns. The result would 
			// depend on too many design decisions and wouldn't always correspond 
			// with what a developer expects. It's better to have the developer define 
			// the widths.
			table.getDefaultCell().setNoWrap(false);
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			
			
			String[] splits = line.split(delimitor);
			for (int i = 0; i < splits.length; i++) {
				table.addCell(new Phrase(new Chunk(splits[i], font)));
			}
			table.completeRow();
			
			boolean isManualBreak = false;
			if (!StringUtil.isEmptyOrNull(manualPageBreakIndicator)) isManualBreak = true; 
			while ((line = reader.readLine()) != null) {
				if (isManualBreak && line.indexOf(manualPageBreakIndicator) != -1) {
					document.newPage();
				} else {
					splits = line.split(delimitor);
					for (int i = 0; i < splits.length; i++) {
						table.addCell(new Phrase(new Chunk(splits[i], font)));
					}
					table.completeRow();
				}
			}
			document.add(table);
			reader.close();

		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
			throw new Exception(e);
		} finally {
			
		}

		document.close();
	}

	/**
	 * Convert CSV to PDF file. Column header will be repeated in each page if parameter
	 * 'columnHeaderNoOfLines' > 0.
	 * 
	 * <b>FAQ on column width!</b>
	 * <p>
	 * Is it possible to have the column width change dynamically based on the content
	 * of the cells? 
	 * PDF isn't HTML, and a PdfPTable is completely different 
	 * from an HTML table rendered in a browser; iText can't calculate column widths based 
	 * on the content of the columns. The result would depend on too many design decisions 
	 * and wouldn't always correspond with what a developer expects. 
	 * It's better to have the developer define the widths.
	 * </p>
	 * 
	 * @param pageSize 					page size of output PDF.
	 * @param author 					author of the PDF in the PDF 'Document Properties'
	 * @param producer					producer of the PDF in the PDF 'Document Properties'
	 * @param subject					subject of the PDF in the PDF 'Document Properties'
	 * @param title 					title of the PDF in the PDF 'Document Properties'
	 * @param leftMargin 				left margin of the PDF
	 * @param rightMargin 				right margin of the PDF
	 * @param topMargin 				top margin of the PDF
	 * @param bottomMargin 				bottom margin of the PDF
	 * @param fontSize					font size of the PDF, in pt.
	 * @param csvFile 					java File object pointing to the source file.
	 * @param fileEncoding 				TODO file encoding of the soruce file.
	 * @param withChinese 				indicator the input source file contains Chinese characters.
	 * @param outputAbsolutePath 		absolute path for the output PDF file.
	 * @param columnHeaderNoOfLines		number of lines in the source file are column header. Can be 0..n.
	 * @param enablePageNum 		 	boolean to specify whether to enable the page numbers.
	 * @param enablePageNavigation		boolean to specify whether to enable to paging navigation,
	 * @param watermarkText				the watermark text to be printed on each page of the PDF. Null of empty means no watermark.
	 * @param delimitor					the delimitor string in the source CSV file.
	 * @param manualPageBreakIndicator	manual page break indicator. When program encounters a line contains this indicator, then will explicit make a page break.
	 * @throws Exception				in case of any exception...
	 */
	public static void convertCsv2PdfOffline(SUPPORTED_PAGE_SIZE pageSize, String author, String producer, String subject, String title,
			float leftMargin, float rightMargin, float topMargin, float bottomMargin, float fontSize, File csvFile, String fileEncoding,
			boolean withChinese, String outputAbsolutePath, int columnHeaderNoOfLines, boolean enablePageNum, boolean enablePageNavigation,
			String watermarkText, String delimitor, String manualPageBreakIndicator) throws Exception {
		
		final String THIS_METHOD = "convertCsv2PdfOffline";
		
		author = StringUtil.isEmptyOrNull(author) ? DEFAULT_AUTHOR : author;
		producer = StringUtil.isEmptyOrNull(producer) ? DEFAULT_PRODUCER : producer;
		subject = StringUtil.isEmptyOrNull(subject) ? DEFAULT_SUBJECT : subject;
		title = StringUtil.isEmptyOrNull(title) ? DEFAULT_TITLE : title;
		delimitor = StringUtil.isEmptyOrNull(delimitor) ? DEFAULT_DELIMITOR : delimitor;
		leftMargin = leftMargin < 0 ? DEFAULT_LEFT_MARGIN : leftMargin;
		rightMargin = rightMargin < 0 ? DEFAULT_RIGHT_MARGIN : rightMargin;
		topMargin = topMargin < 0 ? DEFAULT_TOP_MARGIN : topMargin;
		bottomMargin = bottomMargin < 0 ? DEFAULT_BOTTOM_MARGIN : bottomMargin;

		Document document = new Document(mapPageSize(pageSize), leftMargin, rightMargin, topMargin, bottomMargin);
		document.addAuthor(author);
		document.addCreationDate();
		document.addSubject(subject);
		document.addTitle(title);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputAbsolutePath));
			
			convertCsv2PdfAux(writer, document, fontSize, csvFile, fileEncoding, withChinese, 
							  columnHeaderNoOfLines, enablePageNum, enablePageNavigation, 
							  watermarkText, delimitor, manualPageBreakIndicator);

		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
			throw new Exception(e);
		}

	}
	
	/**
	 * Convert CSV to PDF file. Column header will be repeated in each page if parameter
	 * 'columnHeaderNoOfLines' > 0.
	 * 
	 * <b>FAQ on column width!</b>
	 * <p>
	 * Is it possible to have the column width change dynamically based on the content
	 * of the cells? 
	 * PDF isn't HTML, and a PdfPTable is completely different 
	 * from an HTML table rendered in a browser; iText can't calculate column widths based 
	 * on the content of the columns. The result would depend on too many design decisions 
	 * and wouldn't always correspond with what a developer expects. 
	 * It's better to have the developer define the widths.
	 * </p>
	 * 
	 * @param pageSize 					page size of output PDF.
	 * @param author 					author of the PDF in the PDF 'Document Properties'
	 * @param producer					producer of the PDF in the PDF 'Document Properties'
	 * @param subject					subject of the PDF in the PDF 'Document Properties'
	 * @param title 					title of the PDF in the PDF 'Document Properties'
	 * @param leftMargin 				left margin of the PDF
	 * @param rightMargin 				right margin of the PDF
	 * @param topMargin 				top margin of the PDF
	 * @param bottomMargin 				bottom margin of the PDF
	 * @param fontSize					font size of the PDF, in pt.
	 * @param csvFile 					java File object pointing to the source file.
	 * @param fileEncoding 				TODO file encoding of the soruce file.
	 * @param withChinese 				indicator the input source file contains Chinese characters.
	 * @param response			 		HTTP servlet response
	 * @param outputFileName			file name for the output PDF.
	 * @param columnHeaderNoOfLines		number of lines in the source file are column header. Can be 0..n.
	 * @param enablePageNum 		 	boolean to specify whether to enable the page numbers.
	 * @param enablePageNavigation		boolean to specify whether to enable to paging navigation,
	 * @param watermarkText				the watermark text to be printed on each page of the PDF. Null of empty means no watermark.
	 * @param delimitor					the delimitor string in the source CSV file.
	 * @param manualPageBreakIndicator	manual page break indicator. When program encounters a line contains this indicator, then will explicit make a page break.
	 * @throws Exception				in case of any exception...
	 */
	public static void convertCsv2PdfOnline(SUPPORTED_PAGE_SIZE pageSize, String author, String producer, String subject, String title,
			float leftMargin, float rightMargin, float topMargin, float bottomMargin, float fontSize, File csvFile, String fileEncoding,
			boolean withChinese, HttpServletResponse response, String outputFileName, 
			int columnHeaderNoOfLines, boolean enablePageNum, boolean enablePageNavigation,
			String watermarkText, String delimitor, String manualPageBreakIndicator) throws Exception {
		
		final String THIS_METHOD = "convertCsv2PdfOffline";
		
		author = StringUtil.isEmptyOrNull(author) ? DEFAULT_AUTHOR : author;
		producer = StringUtil.isEmptyOrNull(producer) ? DEFAULT_PRODUCER : producer;
		subject = StringUtil.isEmptyOrNull(subject) ? DEFAULT_SUBJECT : subject;
		title = StringUtil.isEmptyOrNull(title) ? DEFAULT_TITLE : title;
		delimitor = StringUtil.isEmptyOrNull(delimitor) ? DEFAULT_DELIMITOR : delimitor;
		leftMargin = leftMargin < 0 ? DEFAULT_LEFT_MARGIN : leftMargin;
		rightMargin = rightMargin < 0 ? DEFAULT_RIGHT_MARGIN : rightMargin;
		topMargin = topMargin < 0 ? DEFAULT_TOP_MARGIN : topMargin;
		bottomMargin = bottomMargin < 0 ? DEFAULT_BOTTOM_MARGIN : bottomMargin;

		Document document = new Document(mapPageSize(pageSize), leftMargin, rightMargin, topMargin, bottomMargin);
		document.addAuthor(author);
		document.addCreationDate();
		document.addSubject(subject);
		document.addTitle(title);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + outputFileName + "\"");
			
			convertCsv2PdfAux(writer, document, fontSize, csvFile, fileEncoding, withChinese, 
							  columnHeaderNoOfLines, enablePageNum, enablePageNavigation, 
							  watermarkText, delimitor, manualPageBreakIndicator);
			
		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	/**
	 * Convert HTML (with CSS styling) to PDF. Currently, only support printing to A4 portrait.
	 * 
	 * No more constraint to 2.0.8!
	 * 
	 * @param htmlFile 				java File object pointing to the source file.
	 * @param outputAbsolutePath 	absolute path for the output PDF file.
	 * @throws Exception			in case of any exception...
	 */
	public static void convertHtmlToPdfOffline(File htmlFile, String outputAbsolutePath) throws Exception {
        OutputStream os = new FileOutputStream(outputAbsolutePath);
        
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(htmlFile);
        renderer.layout();
        renderer.createPDF(os);
        
        os.close();
	}
	
	/**
	 * Convert HTML (with CSS styling) to PDF. Currently, only support printing to A4 portrait.
	 * 
	 * No more constraint to 2.0.8!
	 * 
	 * @param htmlFile				java File object pointing to the source file.
	 * @param response				HTTP servlet response.
	 * @param outputFileName		file name for output PDF file.
	 * @throws Exception			in case of any exception...
	 */
	public static void convertHtmlToPdfOnline(File htmlFile, HttpServletResponse response, String outputFileName) throws Exception {
        OutputStream os = response.getOutputStream();
        response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + outputFileName + "\"");
        
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(htmlFile);
        renderer.layout();
        renderer.createPDF(os);
        
        os.close();
	}
	
	/**
	 * Parsing the CLI arguments to a map object.
	 * 
	 * @param args
	 * @return
	 */
	private static HashMap<String, String> parseArgument(String args[]) throws Exception {
		
		HashMap<String, String> hMap = new HashMap<String, String>();

		for (int i = 0; i < args.length; i++) {
			if (HELP.equals(args[i])) {
				printUsage();
				System.exit(1);
			} else if (TYPE.equalsIgnoreCase(args[i])) {
				hMap.put(TYPE, args[i + 1]);
			} else if (IN_FILE.equalsIgnoreCase(args[i])) {
				hMap.put(IN_FILE, args[i + 1]);
			} else if (ENCODING.equalsIgnoreCase(args[i])) {
				hMap.put(ENCODING, args[i + 1]);
			} else if (OUT_FILE.equalsIgnoreCase(args[i])) {
				hMap.put(OUT_FILE, args[i + 1]);
			} else if (CHINESE.equalsIgnoreCase(args[i])) {
				hMap.put(CHINESE, Boolean.toString(true));
			} else if (PAGE_SIZE.equalsIgnoreCase(args[i])) {
				hMap.put(PAGE_SIZE, args[i + 1]);
			} else if (AUTHOR.equalsIgnoreCase(args[i])) {
				hMap.put(AUTHOR, args[i + 1]);
			} else if (SUBJECT.equalsIgnoreCase(args[i])) {
				hMap.put(SUBJECT, args[i + 1]);
			} else if (PRODUCER.equalsIgnoreCase(args[i])) {
				hMap.put(PRODUCER, args[i + 1]);
			} else if (TITLE.equalsIgnoreCase(args[i])) {
				hMap.put(TITLE, args[i + 1]);
			} else if (LEFT_MARGIN.equalsIgnoreCase(args[i])) {
				hMap.put(LEFT_MARGIN, args[i + 1]);
				try {
					Float.parseFloat(args[i + 1]);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("Error parsing left margin [" + args[i + 1] + "].");
				}
			} else if (RIGHT_MARGIN.equalsIgnoreCase(args[i])) {
				hMap.put(RIGHT_MARGIN, args[i + 1]);
				try {
					Float.parseFloat(args[i + 1]);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("Error parsing right margin [" + args[i + 1] + "].");
				}
			} else if (TOP_MARGIN.equalsIgnoreCase(args[i])) {
                hMap.put(TOP_MARGIN, args[i + 1]);
                try {
					Float.parseFloat(args[i + 1]);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("Error parsing top margin [" + args[i + 1] + "].");
				}
            } else if (BOTTOM_MARGIN.equalsIgnoreCase(args[i])) {
                hMap.put(BOTTOM_MARGIN, args[i + 1]);
                try {
					Float.parseFloat(args[i + 1]);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("Error parsing bottom margin [" + args[i + 1] + "].");
				}
            } else if (HEADER_ROW.equalsIgnoreCase(args[i])) {
                hMap.put(HEADER_ROW, args[i + 1]);
                try {
					Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("Error parsing number of column header row [" + args[i + 1] + "].");
				}
            } else if (HEADER_FONT_SIZE.equalsIgnoreCase(args[i])) {
                hMap.put(HEADER_FONT_SIZE, args[i + 1]);
                try {
					Float.parseFloat(args[i + 1]);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("Error parsing header font size [" + args[i + 1] + "].");
				}
            }  else if (FONT_SIZE.equalsIgnoreCase(args[i])) {
                hMap.put(FONT_SIZE, args[i + 1]);
                try {
					Float.parseFloat(args[i + 1]);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("Error parsing font size [" + args[i + 1] + "].");
				}
            } else if (WATERMARK.equalsIgnoreCase(args[i])) {
                hMap.put(WATERMARK, args[i + 1]);
            } else if (PAGING.equalsIgnoreCase(args[i])) {
                hMap.put(PAGING, Boolean.toString(true));
            } else if (NAVI_BAR.equalsIgnoreCase(args[i])) {
                hMap.put(NAVI_BAR, Boolean.toString(true));
            } else if (DELIMITOR.equalsIgnoreCase(args[i])) {
                hMap.put(DELIMITOR, args[i + 1]);
            } else if (MANUAL_PAGE_BREAK_INDICATOR.equalsIgnoreCase(args[i])) {
            	hMap.put(MANUAL_PAGE_BREAK_INDICATOR, args[i + 1]);
            }
		}
		
		if (hMap.get(TYPE) == null || hMap.get(IN_FILE) == null) {
			throw new IllegalArgumentException("Mandatory argument " + TYPE + ", " + IN_FILE + " are required.");
		}
		
		File inFile = new File(hMap.get(IN_FILE));
		if (!inFile.exists() || !inFile.isFile() || !inFile.canRead()) {
			throw new IOException("Input file [" + hMap.get(IN_FILE) + "]does not exists.");
		}
		
		if (hMap.get(OUT_FILE) == null) {
			// set the output file same name with input file (under in file directory) with new file extension as .pdf
			String outFilePath = inFile.getAbsolutePath().substring(0, inFile.getAbsolutePath().lastIndexOf(File.separatorChar)) + 
								 File.separatorChar + 
								 inFile.getName();
			if (outFilePath.lastIndexOf('.') != -1) {
				outFilePath = outFilePath.substring(0, outFilePath.lastIndexOf('.'));
			}
			outFilePath = outFilePath + ".pdf";
			
			hMap.put(OUT_FILE, outFilePath);
		}

		return hMap;
	}

	/**
	 * Print program usage text to standard out.
	 */
	private static void printUsage() {
		System.out.print("The PDF margin is calculated as followings\n"
				+ "a margin of 36 pt (0.5 in),\n"
				+ "a margin of 72 pt (1 in),\n"
				+ "a margin of 108 pt (1.5 in),\n"
				+ "a margin of 180 pt (2.5 in).\n\n"
				+ "Each optional value will take default if not given. Below are the default values:\n"
				+ "\tPage size: A4R (landscape)\n"
				+ "\tMargins: 36pt (0.5 in)\n"
				+ "\tNo of column header row: 0\n"
				+ "\tEncoding: UTF-8\n"
				+ "\tAuthor: WTT\n"
				+ "\tProducer: WTT\n"
				+ "\tSubject: WTT\n"
				+ "\tTitle: WTT\n"
				+ "\tHeader Font size: 8\n"
				+ "\tFont size: 8\n"
				+ "\tWatermark: none (disabled)\n"
				+ "\tPage numbering: true\n"
				+ "\tPaging navigation bar: false\n"
				+ "\tDelimitor: , (behave unexpectedly if there is other commas in content!)\n"
				+ "\tManual page break indicator: none (disabled)"
				+ "\n\n"
				+ "TXT/CSV Usage: java com.wtt.framework.util.pdf.PdfUtil\n"
				+ "\t-type txt|csv -infile /absoluate/path/to/infile [-encoding infile_encoding]\n"
				+ "\t-outfile /absolute/path/to/outfile\n"
				+ "\t[-chinese]\n"
				+ "\t[-pagesize A3|A3R|A4|A4R]  [-author XX]\n"
				+ "\t[-producer XX] [-subject XX] [-title XX]\n"
				+ "\t[-lmargin 36 -rmargin 36 -tmargin 36 - bmargin 36]\n"
				+ "\t[-headerrow 1] [-headerfontsize 8] [-fontsize 8]\n"
				+ "\t[-watermark XX] [-paging] [-navibar]\n"
				+ "\t[-delimitor XX]\n"
				+ "\t[-manualpagebreakstr XX]\n"
                + "\t[-help]\n\n"
                + "HTML Usage: java com.wtt.framework.util.pdf.PdfUtil\n"
				+ "\t-type html -infile /absoluate/path/to/infile\n"
				+ "\t-outfile /absolute/path/to/outfile\n"
                + "\t[-help]\n\n"
				+ "\nReturn: 0=success, 1=fail\n\n");;
	}

	/**
	 * Main Driver
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		try {
			Map<String, String> arguMap = parseArgument(args);

			if ("TXT".equalsIgnoreCase(arguMap.get(TYPE))) {
				// do TXT transform
				PdfUtil.convertTxt2PdfOffline(mapPageSize(arguMap.get(PAGE_SIZE)),
						arguMap.get(AUTHOR), 
						arguMap.get(PRODUCER), 
						arguMap.get(SUBJECT), 
						arguMap.get(TITLE), 
						Float.parseFloat(arguMap.get(LEFT_MARGIN) == null ? Float.toString(DEFAULT_LEFT_MARGIN) : arguMap.get(LEFT_MARGIN)), 
						Float.parseFloat(arguMap.get(RIGHT_MARGIN) == null ? Float.toString(DEFAULT_RIGHT_MARGIN) : arguMap.get(RIGHT_MARGIN)), 
						Float.parseFloat(arguMap.get(TOP_MARGIN) == null ? Float.toString(DEFAULT_TOP_MARGIN) : arguMap.get(TOP_MARGIN)), 
						Float.parseFloat(arguMap.get(BOTTOM_MARGIN) == null ? Float.toString(DEFAULT_BOTTOM_MARGIN) : arguMap.get(BOTTOM_MARGIN)),
						Float.parseFloat(arguMap.get(HEADER_FONT_SIZE) == null ? Float.toString(DEFAULT_FONT_SIZE) : arguMap.get(HEADER_FONT_SIZE)),
						Float.parseFloat(arguMap.get(FONT_SIZE) == null ? Float.toString(DEFAULT_FONT_SIZE) : arguMap.get(FONT_SIZE)), 
						new File(arguMap.get(IN_FILE)), 
						arguMap.get(ENCODING)== null ? DEFAULT_ENCODING : arguMap.get(ENCODING), 
						Boolean.parseBoolean(arguMap.get(CHINESE) == null ? "false" : arguMap.get(CHINESE)), 
						arguMap.get(OUT_FILE), 
						Integer.parseInt(arguMap.get(HEADER_ROW) == null ? Integer.toString(DEFAULT_HEADER_ROW) : arguMap.get(HEADER_ROW)), 
						Boolean.parseBoolean(arguMap.get(PAGING) == null ? "false" : arguMap.get(PAGING)), 
						Boolean.parseBoolean(arguMap.get(NAVI_BAR) == null ? "false" : arguMap.get(NAVI_BAR)), 
						arguMap.get(WATERMARK),
						arguMap.get(MANUAL_PAGE_BREAK_INDICATOR));
				
			} else if ("CSV".equalsIgnoreCase(arguMap.get(TYPE))) {
				// do CSV transform
				PdfUtil.convertCsv2PdfOffline(mapPageSize(arguMap.get(PAGE_SIZE)),
							arguMap.get(AUTHOR), 
							arguMap.get(PRODUCER), 
							arguMap.get(SUBJECT), 
							arguMap.get(TITLE), 
							Float.parseFloat(arguMap.get(LEFT_MARGIN) == null ? Float.toString(DEFAULT_LEFT_MARGIN) : arguMap.get(LEFT_MARGIN)), 
							Float.parseFloat(arguMap.get(RIGHT_MARGIN) == null ? Float.toString(DEFAULT_RIGHT_MARGIN) : arguMap.get(RIGHT_MARGIN)), 
							Float.parseFloat(arguMap.get(TOP_MARGIN) == null ? Float.toString(DEFAULT_TOP_MARGIN) : arguMap.get(TOP_MARGIN)), 
							Float.parseFloat(arguMap.get(BOTTOM_MARGIN) == null ? Float.toString(DEFAULT_BOTTOM_MARGIN) : arguMap.get(BOTTOM_MARGIN)), 
							Float.parseFloat(arguMap.get(FONT_SIZE) == null ? Float.toString(DEFAULT_FONT_SIZE) : arguMap.get(FONT_SIZE)), 
							new File(arguMap.get(IN_FILE)), 
							arguMap.get(ENCODING)== null ? DEFAULT_ENCODING : arguMap.get(ENCODING), 
							Boolean.parseBoolean(arguMap.get(CHINESE) == null ? "false" : arguMap.get(CHINESE)), 
							arguMap.get(OUT_FILE), 
							Integer.parseInt(arguMap.get(HEADER_ROW) == null ? Integer.toString(DEFAULT_HEADER_ROW) : arguMap.get(HEADER_ROW)), 
							Boolean.parseBoolean(arguMap.get(PAGING) == null ? "false" : arguMap.get(PAGING)), 
							Boolean.parseBoolean(arguMap.get(NAVI_BAR) == null ? "false" : arguMap.get(NAVI_BAR)), 
							arguMap.get(WATERMARK), 
							arguMap.get(DELIMITOR),
							arguMap.get(MANUAL_PAGE_BREAK_INDICATOR));
				
			} else if ("HTML".equalsIgnoreCase(arguMap.get(TYPE))) {
				// do HTML transform
				PdfUtil.convertHtmlToPdfOffline(new File(arguMap.get(IN_FILE)), arguMap.get(OUT_FILE));
			} else {
				// not supported type, throw exception!
				throw new IllegalArgumentException("Type [" + arguMap.get(TYPE) + "] is not supported!");				
			}
			
			System.exit(0);

		} catch (Exception e) {
			System.err.println(e.getMessage());
			iLog.error("main", e.getMessage(), e);
			System.exit(1);
		}
		
	}
	
	private static final String HELP = "-help";
	private static final String TYPE = "-type";
	private static final String IN_FILE = "-infile";
	private static final String ENCODING = "-encoding";
	private static final String OUT_FILE = "-outfile";
	private static final String CHINESE = "-chinese";
	private static final String PAGE_SIZE = "-pagesize";
	private static final String AUTHOR = "-author";
	private static final String PRODUCER = "-producer";
	private static final String SUBJECT = "-subject";
	private static final String TITLE = "-title";
	private static final String LEFT_MARGIN = "-lmargin";
	private static final String RIGHT_MARGIN = "-rmargin";
	private static final String TOP_MARGIN = "-tmargin";
	private static final String BOTTOM_MARGIN = "-bmargin";
	private static final String HEADER_ROW = "-headerrow";
	private static final String HEADER_FONT_SIZE = "-headerfontsize";
	private static final String FONT_SIZE = "-fontsize";
	private static final String WATERMARK = "-watermark";
	private static final String PAGING = "-paging";
	private static final String NAVI_BAR = "-navibar";
	private static final String DELIMITOR = "-delimitor";
	private static final String MANUAL_PAGE_BREAK_INDICATOR = "-manualpagebreakstr";
	
}
