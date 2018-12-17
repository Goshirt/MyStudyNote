package com.wtt.framework.util.encoding;

import java.io.UnsupportedEncodingException;

import com.wtt.framework.constant.FwkConstant;
import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;

import net.sf.chineseutils.ChineseUtils;

/**
 * The class for converting files' encoding and transform Chinese between simplified and traditional.
 * 
 * @author Sam
 * 
 */
public class EncodingConverter {
	
	private static ILog iLog = AppLogFactory.getLog(EncodingConverter.class, FwkConstant.MODULE_UTIL);
	
	/** the mode of converting from Simplified Chinese to Traditional Chinese */
	public static final String SC_TO_TC = "sc2tc";
	/** the mode of converting from Traditional Chinese to Simplified Chinese */
	public static final String TC_TO_SC = "tc2sc";
	/** the mode of no converting between Simplified Chinese and Traditional Chinese */
	public static final String KEEP_INTACT = "keep-intact";
	
	/**
	 * The convert method is called to convert files' encoding and transform Chinese between simplified and traditional.
	 * 
	 * @param source
	 * the source file's bytes
	 * @param sourceEncoding
	 * the source file's encoding
	 * @param destinationEncoding
	 * the encoding of the destination file after converting
	 * @param sctcConversion
	 * how to perform conversion between simplified Chinese and traditional Chinese 
	 * @return 
	 * the destination file's bytes after converting encoding and transform Chinese between simplified and traditional
	 */
	public static byte[] convert(byte[] source, String sourceEncoding, String destinationEncoding, String sctcConversion) {
		final String method = "convert";
		
		iLog.debug(method, new StringBuilder("Convert file's encoding from ")
										.append(sourceEncoding)
										.append(" to ")
										.append(destinationEncoding)
										.toString());
		// Change source file from source encoding to destination encoding, then get intermediate file
		byte[] intermediate = convertEncoding(source, sourceEncoding, destinationEncoding); 
		iLog.debug(method, new StringBuilder("Convert file encoded in ")
										.append(destinationEncoding)
										.append(" between Simplified Chinese and Traditional Chinese")
										.toString());
		// Perform Perform SimplifiedChinese-TraditionalChinese Conversion on intermediate file in way of sctcConversion, then get destination file
		byte[] destination = convertSimplifiedChineseAndTraditionalChinese(intermediate, destinationEncoding, sctcConversion);
		iLog.debug(method, "Finished converting and return result");
		return destination;
	}
	
	private static byte[] convertSimplifiedChineseAndTraditionalChinese(byte[] source, String sourceEncoding, String sctcConversion) {
		if (SC_TO_TC.equals(sctcConversion)) { // convert Simplified Chinese To Traditional Chinese
			return convertSimplifiedChineseToTraditionalChinese(source, sourceEncoding);
		} else if (TC_TO_SC.equals(sctcConversion)) { // convert Traditional Chinese To Simplified Chinese
			return convertTraditionalChineseToSimplifiedChinese(source, sourceEncoding);
		} else { // Otherwise, No SimplifiedChinese-TraditionalChinese Conversion
			return source;
		}
	}
	
	private static byte[] convertEncoding(byte[] source, String sourceEncoding, String destinationEncoding) {
		final String method = "convertEncoding";
		
		if (sourceEncoding.equals(destinationEncoding)) {
			return source;
		} else {
			byte[] destination = null;
			try {
				 iLog.debug(method, new StringBuilder("Convert file encoding from ")
				 								.append(sourceEncoding)
				 								.append(" to " )
				 								.append(destinationEncoding)
				 								.append(" Start !")
				 								.toString());
				 destination = new String(source, sourceEncoding).getBytes(destinationEncoding);
				 iLog.debug(method, new StringBuilder("Convert file encoding from ")
												.append(sourceEncoding)
												.append(" to " )
												.append(destinationEncoding)
												.append(" End !")
												.toString());
			} catch (UnsupportedEncodingException e) {
				iLog.error(method, e.getMessage(), "UnsupportedEncoding", e);
				e.printStackTrace();
			}
			return destination;
		}
		
	}
	
	private static byte[] convertSimplifiedChineseToTraditionalChinese(byte[] source, String sourceEncoding) {
		final String method = "convertEncoding";
		
		byte[] destination = null;
		try {
			iLog.debug(method, new StringBuilder("Convert file encoded in ")
											.append(sourceEncoding)
											.append(" from Simplified Chinese to Traditional Chinese Start !")
											.toString());
			destination =  ChineseUtils.simpToTrad(new String(source, sourceEncoding)).getBytes(sourceEncoding);
			iLog.debug(method, new StringBuilder("Convert file encoded in ")
											.append(sourceEncoding)
											.append(" from Simplified Chinese to Traditional Chinese End !")
											.toString());
		} catch (UnsupportedEncodingException e) {
			iLog.error(method, e.getMessage(), "UnsupportedEncoding", e);
			e.printStackTrace();
		}
		return destination;
	}
	
	private static byte[] convertTraditionalChineseToSimplifiedChinese(byte[] source, String sourceEncoding) {
		final String method = "convertTraditionalChineseToSimplifiedChinese";
		
		byte[] destination = null;
		try {
			iLog.debug(method, new StringBuilder("Convert file encoded in ")
											.append(sourceEncoding)
											.append(" from Traditional Chinese to Simplified Chinese Start !")
											.toString());
			destination =  ChineseUtils.tradToSimp(new String(source, sourceEncoding)).getBytes(sourceEncoding);
			iLog.debug(method, new StringBuilder("Convert file encoded in ")
											.append(sourceEncoding)
											.append(" from Traditional Chinese to Simplified Chinese End !")
											.toString());
		} catch (UnsupportedEncodingException e) {
			iLog.error(method, e.getMessage(), "UnsupportedEncoding", e);
			e.printStackTrace();
		}
		return destination;
	}
	
	public static void main(String[] args) {
		byte[] sc = "你好吗，最近一切都挺好的吧".getBytes();
		byte[] be = EncodingConverter.convertSimplifiedChineseToTraditionalChinese(sc,"utf-8");
		System.out.println(new String(be));
	}
	
}
