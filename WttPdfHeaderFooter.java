/**
 * 2009 All rights Reserved by Wharf T&T Java Development Team
 * 
 * Project		: framework
 * Package		: com.wtt.framework.util.pdf
 * File			: WttPdfHeaderFooter.java
 * Creation TS	: Oct 28, 2009 4:29:14 PM
 * 
 * ==============================================
 */
package com.wtt.framework.util.pdf;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.wtt.framework.util.StringUtil;

/**
 * 
 * WTT PDF Header and Footer template
 *
 * @author Danny Wong Chun W003372
 */
public class WttPdfHeaderFooter implements PdfPageEvent {

	//protected Phrase header;
	protected List<Phrase> header;
	protected PdfPTable footer;
	protected PdfGState gstate;
	protected PdfTemplate total;
	protected BaseFont baseFont;
	
	private boolean enableHeader;
	private boolean enablePageNum;
	private boolean enablePagingNavigation;
	private boolean enableWatermark;
	private String watermarkText;
	private int columnHeaderNoOfLines;
	private float headerTextFontSize;
	
	/**
	 * Initialize the features by parameters.
	 * 
	 * @param enableHeader
	 * @param headerText
	 * @param headerTextFontSize
	 * @param enablePageNum
	 * @param enablePagingNavigation
	 * @param enableWatermark
	 * @param watermarkText
	 */
	private void initialize(boolean enableHeader,
							String headerText,
							float headerTextFontSize,
							boolean enablePageNum,
							boolean enablePagingNavigation,
							boolean enableWatermark,
							String watermarkText,
							int columnHeaderNoOfLines) {
		if (enableHeader) {
			try {
				Font f = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.NOT_EMBEDDED), headerTextFontSize);
				String[] aa = headerText.split("\\n");				
				header = new ArrayList<Phrase>();
				for (String a : aa) header.add(new Phrase(a, f));
				
			} catch (Exception e) {
				throw new ExceptionConverter(e);
			}
		}
		if (enablePagingNavigation) {
			footer = new PdfPTable(4);
			footer.setTotalWidth(300);
			footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			footer.addCell(new Phrase(new Chunk("First Page").setAction(new PdfAction(PdfAction.FIRSTPAGE))));
			footer.addCell(new Phrase(new Chunk("Prev Page").setAction(new PdfAction(PdfAction.PREVPAGE))));
			footer.addCell(new Phrase(new Chunk("Next Page").setAction(new PdfAction(PdfAction.NEXTPAGE))));
			footer.addCell(new Phrase(new Chunk("Last Page").setAction(new PdfAction(PdfAction.LASTPAGE))));
		}
	}

	/**
	 * constructor all features to false (off).
	 */
	public WttPdfHeaderFooter() {
		this.enableHeader = false;
		this.enablePageNum = false;
		this.enablePagingNavigation = false;
		this.enableWatermark = false;
		this.watermarkText = null;
		initialize(this.enableHeader, null, 0, this.enablePageNum, this.enablePagingNavigation, this.enableWatermark, this.watermarkText, this.columnHeaderNoOfLines);
	}
	
	/**
	 * constructor with customized features.
	 * 
	 * @param headerText
	 * @param headerTextFontSize,
	 * @param enablePageNum
	 * @param enablePagingNavigation
	 * @param watermarkText
	 */
	public WttPdfHeaderFooter(String headerText,
							  float headerTextFontSize,
							  boolean enablePageNum,
							  boolean enablePagingNavigation,
							  String watermarkText,
							  int columnHeaderNoOfLines) {
		this.enableHeader = StringUtil.isEmptyOrNull(headerText)? false : true;
		this.enablePageNum = enablePageNum;
		this.enablePagingNavigation = enablePagingNavigation;
		this.enableWatermark = StringUtil.isEmptyOrNull(watermarkText)? false : true;;
		this.watermarkText = watermarkText;
		this.columnHeaderNoOfLines = columnHeaderNoOfLines;
		this.headerTextFontSize = headerTextFontSize;
		initialize(enableHeader, headerText, headerTextFontSize, enablePageNum, enablePagingNavigation, enableWatermark, watermarkText, columnHeaderNoOfLines);
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onEndPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
	 */
	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		if (this.enableHeader) {
			if (document.getPageNumber() > 0) {
				int i = 10;
				for (Phrase p : header) {
					ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, p,
											document.leftMargin(), document.top() + i, 0);
					i = i - 10;
				}
			}
		}
		if (this.enablePagingNavigation) {
			footer.writeSelectedRows(0, -1, 
								(document.right() - document.left() - 300) / 2 + document.leftMargin(), document.bottom() - 10, cb);
		}
		if (this.enablePageNum) {
			cb = writer.getDirectContent();
			cb.saveState();
			String text = " Page " + writer.getPageNumber() + " of ";
			float textBase = document.bottom() - 20;
			float textSize = baseFont.getWidthPoint(text, 12);
			cb.beginText();
			cb.setFontAndSize(baseFont, 12);
			float adjust = baseFont.getWidthPoint("0", 12);
			cb.setTextMatrix(document.right() - textSize - adjust, textBase);
			cb.showText(text);
			cb.endText();
			cb.addTemplate(total, document.right() - adjust, textBase);
			cb.restoreState();
		}
		if (this.enableWatermark) {
			PdfContentByte contentunder = writer.getDirectContentUnder();
			contentunder.saveState();
			contentunder.setGState(gstate);
			contentunder.setColorFill(Color.gray);
			contentunder.beginText();
			try {
				BaseFont monoFont = BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
				
				contentunder.setFontAndSize(monoFont, 72);
				contentunder.showTextAligned(Element.ALIGN_CENTER,
											this.watermarkText,
											document.getPageSize().getWidth() / 2,
											document.getPageSize().getHeight() / 2, 45);
				contentunder.endText();
			} catch (Exception e) {
				throw new ExceptionConverter(e);
			}
			contentunder.restoreState();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onChapter(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document, float, com.lowagie.text.Paragraph)
	 */
	public void onChapter(PdfWriter writer, Document document, float arg2,
			Paragraph arg3) {
		// do nth
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onChapterEnd(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document, float)
	 */
	public void onChapterEnd(PdfWriter writer, Document document, float arg2) {
		// do nth
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onCloseDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
	 */
	public void onCloseDocument(PdfWriter writer, Document document) {
		total.beginText() ;
		total.setFontAndSize(baseFont, 12) ;
		total.setTextMatrix(0, 0) ;
		total.showText(String.valueOf( writer.getPageNumber() - 1)) ;
		total.endText() ;
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onGenericTag(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document, com.lowagie.text.Rectangle, java.lang.String)
	 */
	public void onGenericTag(PdfWriter writer, Document document, Rectangle arg2,
			String arg3) {
		// do nth
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
		total = writer.getDirectContent().createTemplate(100, 100);
		total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
		try {
			baseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
		gstate = new PdfGState() ;
		gstate.setFillOpacity(0.3f) ;
		gstate.setStrokeOpacity(0.3f) ; 
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onParagraph(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document, float)
	 */
	public void onParagraph(PdfWriter writer, Document document, float arg2) {
		// do nth

	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onParagraphEnd(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document, float)
	 */
	public void onParagraphEnd(PdfWriter writer, Document document, float arg2) {
		// do nth

	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onSection(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document, float, int, com.lowagie.text.Paragraph)
	 */
	public void onSection(PdfWriter writer, Document document, float arg2,
			int arg3, Paragraph arg4) {
		// do nth

	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onSectionEnd(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document, float)
	 */
	public void onSectionEnd(PdfWriter writer, Document document, float arg2) {
		// do nth
	}

	/*
	 * (non-Javadoc)
	 * @see com.lowagie.text.pdf.PdfPageEvent#onStartPage(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
	 */
	public void onStartPage(PdfWriter writer, Document document) {
		BaseFont bf;
		try {
			bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
			Font font = new Font(bf, this.headerTextFontSize);
			for (int i = 0; i < this.columnHeaderNoOfLines - 2; i++) {
				Paragraph p = new Paragraph(" ", font);
				document.add(p);
			}
		} catch (DocumentException e) {
			new ExceptionConverter(e);
		} catch (IOException e) {
			new ExceptionConverter(e);
		}
	}
}
