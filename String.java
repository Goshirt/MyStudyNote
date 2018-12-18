package com.wtt.framework.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import waffle.util.Base64;

import com.wtt.framework.constant.FwkConstant;
import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;
import com.wtt.framework.option.util.OptionTreeUtil;
import com.wtt.framework.util.charset.ChineseUtil;

/**
 * String utility.
 * 
 * @author framework
 */
public class StringUtil {

	private static final ILog iLog = AppLogFactory.getLog(StringUtil.class,
			AppLogFactory.MODULE_FRAMEWORK);

	// BNSMK20070321, trim string length
	public static final int DEFAULT_STRING_LENGTH = 20;
	public static final String ICMS_CHINESE_ENCODING = "Cp937";
	public static final String ICMS_SERVER_ENCODING = "Cp037";
	public static final String EBCDIC_SPACE = new String();

	/**
	 * Convert primitive integer to string.
	 * 
	 * @param tmp
	 * @return
	 */
	public static String int2string(int tmp) {
		return Integer.toString(tmp);
	}

	/**
	 * Convert primitive double to string.
	 * 
	 * @param tmp
	 * @return
	 */
	public static String double2string(double tmp) {
		return Double.toString(tmp);
	}

	/**
	 * Convert string to primitive integer.
	 * 
	 * @param tmp
	 * @return
	 * @throws ParseException
	 */
	public static int string2int(String tmp) throws ParseException {
		if (tmp == null || tmp.equals("")) {
			return 0;
		} else {
			try {
				return Integer.parseInt(tmp, 10);
			} catch (NumberFormatException ex) {
				throw ex;
			}
		}
	}

	/**
	 * 
	 * 
	 * @param tmp
	 * @param sep
	 * @return
	 */
	public static String stringWithDoubleQuote(String tmp, char sep) {
		if (tmp == null)
			return "";
		else {
			tmp = tmp.replace('\n', ' ');
			tmp = tmp.replace('\r', ' ');
			if (tmp.indexOf(sep) == -1)
				return tmp;
			else if (tmp.indexOf('\"') == -1)
				return '"' + tmp + '"';
			else
				return '"' + tmp.replaceAll("\"", "\"\"") + '"';
		}
	}

	/**
	 * Determine whether a string is either empty or null.
	 * 
	 * @param parmStr
	 * @return
	 */
	public static boolean isEmptyOrNull(String parmStr) {
		final String THIS_METHOD = "isEmptyOrNull";
		boolean ret = false;
		try {
			if ((parmStr == null) || ("".equals(parmStr.trim()))) {
				ret = true;
			}
		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
		}
		return ret;
	}

	/**
	 * Return toString() of given object. If given object is null, return empty
	 * string "".
	 * 
	 * @param paramObj
	 * @return
	 */
	public static String null2String(Object paramObj) {
		if (paramObj == null) {
			return "";
		} else {
			return paramObj.toString();
		}
	}

	/**
	 * Left pad given integer with zero to given length.
	 * 
	 * @param integer
	 * @param size
	 * @return
	 */
	public static String leftPadIntegerWithZero(int integer, int size) {
		return leftPad(String.valueOf(integer), "0", size);
	}

	/**
	 * Left pad given long with zero to given length.
	 * 
	 * @param number
	 * @param size
	 * @return
	 */
	public static String leftPadLongWithZero(long number, int size) {
		return leftPad(String.valueOf(number), "0", size);
	}

	/**
	 * Right pad given string with space to given length.
	 * 
	 * @param stringToPad
	 * @param size
	 * @return
	 */
	public static String rightPadWithSpace(String stringToPad, int size) {
		return rightPad(stringToPad, " ", size);
	}

	/**
	 * 
	 * @param sb
	 * @param size
	 * @return
	 */
	public static StringBuffer rightPadBufferWithSpace(StringBuffer sb, int size) {
		if (sb != null) {
			while (sb.length() < size) {
				sb.append(" ");
			}
		}
		return sb;
	}

	// ------------------------------------------------------------------------------

	/**
	 * method to left pad a string with a given string to a given size. This
	 * method will repeat the padder string as many times as is necessary until
	 * the exact specified size is reached. If the specified size is less than
	 * the size of the original string then the original string is returned
	 * unchanged.
	 * <ul>
	 * <li>Example1 - original string "cat", padder string "white", size 8 gives
	 * "whitecat".</li>
	 * <li>Example2 - original string "cat", padder string "white", size 15
	 * gives "whitewhitewhcat".</li>
	 * <li>Example3 - original string "cat", padder string "white", size 2 gives
	 * "cat".</li>
	 * </ul>
	 * 
	 * @return String the newly padded string
	 * @param stringToPad
	 *            The original string
	 * @param padder
	 *            The string to pad onto the original string
	 * @param size
	 *            The required size of the new string
	 */
	public static String leftPad(String stringToPad, String padder, int size) {
		if (padder.length() == 0) {
			return stringToPad;
		}
		StringBuffer strb = new StringBuffer(size);
		StringCharacterIterator sci = new StringCharacterIterator(padder);

		while (strb.length() < (size - stringToPad.length())) {
			for (char ch = sci.first(); ch != CharacterIterator.DONE; ch = sci
					.next()) {
				if (strb.length() < size - stringToPad.length()) {
					strb.insert(strb.length(), String.valueOf(ch));
				}
			}
		}
		return strb.append(stringToPad).toString();
	}

	/**
	 * method to right pad a string with a given string to a given size. This
	 * method will repeat the padder string as many times as is necessary until
	 * the exact specified size is reached. If the specified size is less than
	 * the size of the original string then the original string is returned
	 * unchanged.
	 * <ul>
	 * <li>Example1 - original string "cat", padder string "white", size 8 gives
	 * "catwhite".</li>
	 * <li>Example2 - original string "cat", padder string "white", size 15
	 * gives "catwhitewhitewh".</li>
	 * <li>Example3 - original string "cat", padder string "white", size 2 gives
	 * "cat".</li>
	 * </ul>
	 * 
	 * @return String the newly padded string
	 * @param stringToPad
	 *            The original string
	 * @param padder
	 *            The string to pad onto the original string
	 * @param size
	 *            The required size of the new string
	 */
	public static String rightPad(String stringToPad, String padder, int size) {
		if (padder.length() == 0) {
			return stringToPad;
		}
		StringBuffer strb = null;
		if (stringToPad != null) {
			strb = new StringBuffer(stringToPad);
		} else {
			strb = new StringBuffer();
		}
		StringCharacterIterator sci = new StringCharacterIterator(padder);

		while (strb.length() < size) {
			for (char ch = sci.first(); ch != CharacterIterator.DONE; ch = sci
					.next()) {
				if (strb.length() < size) {
					strb.append(String.valueOf(ch));
				}
			}
		}
		return strb.toString();
	}

	/**
	 * Substring length with default length
	 * 
	 * @author BNSMK
	 * @since 20070321
	 * @param tmp
	 * @return
	 */
	public static String subStringLen(String tmp) {
		return subStringLen(tmp, DEFAULT_STRING_LENGTH);
	}

	/**
	 * Substring length with given length
	 * 
	 * @author BNSMK
	 * @since 20070321
	 * @param tmp
	 * @return
	 */
	public static String subStringLen(String tmp, int size) {
		if (tmp.length() > size) {
			tmp = tmp.substring(0, size);
			tmp = new StringBuffer().append(tmp).append("...").toString();
		}
		return tmp;
	}

	/**
	 * COMPLETELY FALSE LOGIC, DO NOT USE. To be removed in future.
	 * 
	 * @deprecated
	 * @param input
	 * @param wildStart
	 * @return Null if null string or empty string.
	 */
	@Deprecated
	public static String replaceSqlWildCard(String input, boolean wildStart) {
		if (input == null || input.trim().length() == 0) {
			return null;
		}
		String temp = input.replaceAll("[\\s]+", "%");
		if (wildStart) {
			return new StringBuffer().append('%').append(temp).append('%')
					.toString();
		} else {
			return new StringBuffer().append(temp).append('%').toString();
		}
	}

	/**
	 * FALSE LOGIC, DO NOT USE. To be removed in future.
	 * 
	 * @deprecated
	 * @param input
	 * @return
	 */
	@Deprecated
	public static String replaceSqlWildCard(String input) {
		return replaceSqlWildCard(input, false);
	}

	/**
	 * Convert empty string or string with only space to java null.
	 * 
	 * @param string
	 * @return
	 */
	public static String emptyStringToNull(String string) {
		if (string == null) {
			return null;
		}
		string = string.trim();
		return string.length() == 0 ? null : string;
	}

	/**
	 * Convert String to boolean.
	 * 
	 * @param value
	 * @return true if 'Y', false if 'N'.
	 */
	public static boolean str2bool(String value) {
		return str2bool(value, FwkConstant.ENABLED_STRING);
	}

	/**
	 * Convert String to boolean using given true string to compare.
	 * 
	 * @param value
	 * @param trueString
	 * @return
	 */
	public static boolean str2bool(String value, String trueString) {
		return value == null ? false : value.trim()
				.equalsIgnoreCase(trueString);
	}

	/**
	 * Encrypt given message with given encrypt sequence and key.
	 * 
	 * @param message
	 * @param encryptSeq
	 * @param encryptKey
	 * @return
	 */
	public static String translate(String message, String encryptSeq,
			String encryptKey) {
		StringBuffer result = new StringBuffer();
		char tmp;
		int i = 0;
		int j = 0;

		for (i = 0; i < message.length(); i++) {
			tmp = message.charAt(i);

			for (j = 0; j < encryptSeq.length(); j++) {
				if (tmp == encryptSeq.charAt(j)) {
					result.append(encryptKey.charAt(j));
					break;
				}
			}
			// do not match on cipher, then replace the message character
			if (j >= encryptSeq.length()) {
				result.append(tmp);
			}
		}

		return result.toString();
	}

	/**
	 * Convert base64-encoded string to hex string.
	 * 
	 * @param encodeString
	 * @return
	 */
	public static String base64encodedStringToHexString(String encodeString) {
		return byteArrayToHexString(Base64.decode(encodeString));
	}

	/**
	 * Convert byte array to hex string.
	 * 
	 * @param in
	 * @return
	 */
	public static String byteArrayToHexString(byte in[]) {
		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
							"A", "B", "C", "D", "E", "F" };
		StringBuffer out = new StringBuffer(in.length * 2);
		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4);
			// shift the bits down
			ch = (byte) (ch & 0x0F);
			// must do this is high order bit is on!
			out.append(pseudo[(int) ch]); // convert the nibble to a String Character
			ch = (byte) (in[i] & 0x0F); // Strip off low nibble
			out.append(pseudo[(int) ch]); // convert the nibble to a String Character
			i++;
		}
		return new String(out);
	}

	/**
	 * Produce a string of given occurrence of given char.
	 * 
	 * <pre>
	 * INPUT: 'a', 10
	 * OUTPUT: aaaaaaaaaa
	 * </pre>
	 * 
	 * @param character
	 * @param count
	 * @return
	 */
	public static String str(char character, int count) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < count; i++)
			result.append(character);
		return result.toString();
	}

	/**
	 * Produce a string with '...' suffix if the length of given string is
	 * longer than the specified display length.
	 * 
	 * <pre>
	 * INPUT: &quot;abcde&quot;, 3
	 * OUTPUT: &quot;abc...&quot;
	 * </pre>
	 * 
	 * @param value
	 * @param displayLength
	 * @return
	 */
	public static String shortenForDisplay(String value, int displayLength) {
		if (value == null || value.trim().length() < displayLength) {
			return value;
		}
		StringBuffer buffer = new StringBuffer(value.trim());
		buffer.delete(displayLength, buffer.length());
		return buffer.append("...").toString();
	}

	/**
	 * Trim a string if not null.
	 * 
	 * @param value
	 * @return An empty string if value is null, or a trimmed string otherwise.
	 */
	public static String trimIfNecessary(String value) {
		return value == null ? "" : value.trim();
	}

	/**
	 * Translate given i18n label key to its value.
	 * 
	 * @param bundle
	 * @param input
	 * @return
	 */
	public static String translate2i18nLabel(ResourceBundle bundle, String input) {
		return StringUtil.translate2i18nLabel(bundle, input, (Object[]) null);
	}

	/**
	 * Translate given i18n label key with parameters (for those {0}, {1} in
	 * i18n properties) to its value.
	 * 
	 * @param bundle
	 * @param input
	 * @param parameter the parameter value to be replaced in the translated value; i.e. the first value in parameter will be replaced the [0]
	 *        and so on...
	 * @return
	 */
	public static String translate2i18nLabel(ResourceBundle bundle, String input, Object... parameter) {
		MessageFormat messageFormat = null;

		String i18nLabel = null;
		try {
			i18nLabel = bundle.getString(input);
			messageFormat = new MessageFormat(i18nLabel);

			if (parameter != null && parameter.length > 0) {
				i18nLabel = messageFormat.format(parameter);
			}
		} catch (MissingResourceException ex) {
			i18nLabel = input;
		}

		return i18nLabel;
	}
	
	/**
	 * Translate given i18n label key to its values according to the ResourceBundle array given.
	 * 
	 * @param bundles an array of ResourceBundle
	 * @param input input the resource bundle key
	 * @return the translated values in String array. An empty string array will be returned if the bundle array is
	 *         null / empty OR input is null / empty.
	 */
	public static String[] translate2i18nLabel(ResourceBundle[] bundles, String input) {
		return translate2i18nLabel(bundles, input, (Object[]) null);
	}
	
	/**
	 * Translate given i18n label key with parameters (for those {0}, {1} in
	 * i18n properties) to its values according to the ResourceBundle array given.
	 * The ordering of the result String array will be the same as the given ResourceBundle array.
	 * 
	 * Developers can utilize the result String array according to their requirements, i.e. concat them,
	 * use them separately or discard them.
	 * 
	 * @param bundles an array of ResourceBundle
	 * @param input the resource bundle key
	 * @param parameter the parameter value to be replaced in the translated value; i.e. the first value in parameter will be replaced the [0]
	 *        and so on...
	 * @return the translated values in String array. An empty string array will be returned if the bundle array is
	 *         null / empty OR input is null / empty.
	 */
	public static String[] translate2i18nLabel(ResourceBundle[] bundles, String input, Object... parameter) {
		if ((bundles != null && bundles.length > 0) && (!StringUtil.isEmptyOrNull(input))) {
			String[] translatedValues = new String[bundles.length];
			
			for (int i = 0; i < bundles.length; i++) {
				translatedValues[i] = translate2i18nLabel(bundles[i], input, parameter);
			}
			return translatedValues;
		} else {
			return new String[0];
		}
	}

	/**
	 * Please use translate2i18nLabelFromOptionTreeKey(ResourceBundle bundle,
	 * String listId, String itemKey, String parentKey) instead.
	 * 
	 * @deprecated
	 * @param bundle
	 * @param listId
	 * @param itemKey
	 * @return
	 * @see StringUtil#translate2i18nLabelFromOptionTreeKey(ResourceBundle,
	 *      String, String, String, String)
	 */
	@Deprecated
	public static String translate2i18nLabelFromOptionTreeKey(
			ResourceBundle bundle, String listId, String itemKey) {
		String label = OptionTreeUtil.getTreeLabel(listId, itemKey);
		if (label == null) {
			return "";
		}
		return translate2i18nLabel(bundle, label);
	}

	/**
	 * Directly translate option tree item to i18n value.
	 * 
	 * @param bundle
	 * @param listId
	 * @param itemKey
	 * @param parentKey
	 * @return
	 */
	public static String translate2i18nLabelFromOptionTreeKey(
			ResourceBundle bundle, String listId, String itemKey,
			String parentKey) {
		String label = OptionTreeUtil.getTreeLabel(listId, itemKey, parentKey);
		if (label == null) {
			return "";
		}
		return translate2i18nLabel(bundle, label);
	}

	/**
	 * Encodes input string value into proper format for chinese storage in ICMS
	 * with built-in Simplified to Traditional Chinese translation.
	 * 
	 * @param value
	 *            - CSBS string value
	 * @return - encoded for storage in Cp937 (mixed double byte traditional
	 *         chinese) and transport via Cp037 (ICMS server default encoding)
	 */
	public static String csbsToIcmsChineseText(String value) {
		if (value == null) {
			return "";
		}
		String result = "";
		try {
			result = new String(ChineseUtil.getInstance()
					.simplifiedToTraditional(value).getBytes(
							ICMS_CHINESE_ENCODING), ICMS_SERVER_ENCODING);
		} catch (UnsupportedEncodingException ignored) {
		}
		return result;
	}

	/**
	 * Encodes input string value into proper format for chinese storage in ICMS
	 * with built-in Simplified to Traditional Chinese translation. The output
	 * string will be trimmed or padded with space to conform with
	 * <code>length</code> count.
	 * 
	 * @param value
	 *            - CSBS string value
	 * @param length
	 *            - column length in bytes of the AS/400 data field.
	 * @return - encoded for storage in Cp937 (mixed double byte traditional
	 *         chinese) and transport via Cp037 (ICMS server default encoding)
	 */
	public static String csbsToIcmsChineseText(String value, int length) {
		if (value == null) {
			return rightPadWithSpace("", length);
		}
		String result = "";
		try {
			String current = ChineseUtil.getInstance().simplifiedToTraditional(
					value);
			byte[] bytes = current.getBytes(ICMS_CHINESE_ENCODING);
			while (bytes.length > length) {
				current = current.substring(0, current.length() - 1);
				bytes = current.getBytes(ICMS_CHINESE_ENCODING);
			}
			result = new String(current.getBytes(ICMS_CHINESE_ENCODING),
					ICMS_SERVER_ENCODING);
		} catch (UnsupportedEncodingException ignored) {
		}
		return rightPadWithSpace(result, length);
	}

	/**
	 * Convert CSBS string to ICMS text.
	 * 
	 * @param values
	 * @return
	 */
	public static String[] csbsToIcmsText(String[] values) {
		if (values == null) {
			return null;
		}
		String[] converted = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			converted[i] = csbsToIcmsChineseText(values[i]);
		}
		return converted;
	}

	/**
	 * Convert ICMS to CSBS Chinese text.
	 * 
	 * @param value
	 * @return
	 */
	public static String icmsToCsbsChineseText(String value) {
		return icmsToCsbsChineseText(value, true);
	}

	/**
	 * Convert ICMS to CSBS Chinese text
	 * 
	 * @param value
	 * @param trimmed
	 * @return
	 */
	public static String icmsToCsbsChineseText(String value, boolean trimmed) {
		if (value == null) {
			return "";
		}
		String result = "";
		try {
			result = new String(value.getBytes(ICMS_SERVER_ENCODING),
					ICMS_CHINESE_ENCODING);
		} catch (UnsupportedEncodingException ignored) {
		}
		return trimmed ? result.trim() : result;
	}

	/**
	 * Auxiliary function for concatFieldValues() and
	 * concatFieldValuesNoTruncate().
	 * 
	 * <pre>
	 * the.fully.qualitied.class.name.Example(field1=value1, field2=value2, field3=value3, )
	 * </pre>
	 * 
	 * @since 2009-Sep-01
	 * @author Danny Wong Chun W003372
	 * @param object
	 *            the object to be TO-STRING-ed.
	 * @return a new StringBuilder. This method never return null. Even the
	 *         argument object is null, an empty StringBuilder will be returned.
	 */
	private static StringBuilder concatFieldValuesAux(Object object) {
		final String METHOD_NAME = "concatFieldValuesAux";
		StringBuilder resultConcatSb = new StringBuilder();

		if (object != null) {
			List<Field> fieldList = new ArrayList<Field>();
			Class<?> classA = object.getClass();
			resultConcatSb.append(classA.getName()).append('(');
			Class<?> fieldValueType = null;
			StringBuilder fieldName = null;

			Method getterA = null;
			Object fieldValueA = null;

			do {
				// get private, protected, public fields
				fieldList.addAll(Arrays.asList(classA.getDeclaredFields()));
				classA = classA.getSuperclass();
			} while (classA != null);

			int fieldListSize = fieldList.size();
			for (int i = 0; i < fieldListSize; i++) {
				fieldValueType = fieldList.get(i).getType();
				fieldName = new StringBuilder(fieldList.get(i).getName());

				// To skip the getServialVersionUID, since this is use to
				// identify the verions of the object for serialization.
				if (fieldName.toString().equals("serialVersionUID")) {
					continue;
				}
				
				if (fieldName.toString().equals("instance")) {
					continue;
				}

				// concat the 'fieldName=' part
				resultConcatSb.append(fieldName).append('=');

				// upper case the field name to construct the getter name
				// isAaaaBbb or getAaaaBbb
				fieldName.setCharAt(0, Character.toUpperCase(fieldName
						.charAt(0)));

				try {

					if (fieldValueType.isPrimitive()
							&& fieldValueType.getName().equals("boolean")
							|| fieldValueType.equals(Class
									.forName("java.lang.Boolean"))) {
						fieldName.insert(0, "is");
					} else {
						fieldName.insert(0, "get");
					}

					// can get public methods ONLY
					getterA = object.getClass().getMethod(fieldName.toString(),
							(Class[]) null);

					// invoke the public getter by holy reflection
					fieldValueA = getterA.invoke(object, (Object[]) null);

					if (fieldValueType.equals(String.class.getName())) {
						fieldValueA = (fieldValueA == null) ? "null"
								: fieldValueA;
					}

					// concat (append) the value to the StringBuilder.
					// After this line, should looks like 'fieldName=fieldValue,
					// '
					resultConcatSb.append(fieldValueA).append(", ");

				} catch (ClassNotFoundException cnfe) {
					iLog.error(METHOD_NAME, cnfe.getMessage(), cnfe);
				} catch (NoSuchMethodException nsme) {
					iLog.error(METHOD_NAME, nsme.getMessage(), nsme);
				} catch (InvocationTargetException ite) {
					iLog.error(METHOD_NAME, ite.getMessage(), ite);
				} catch (IllegalAccessException iae) {
					iLog.error(METHOD_NAME, iae.getMessage(), iae);
				}
			}

			resultConcatSb.append(')');
		}

		return resultConcatSb;
	}

	/**
	 * Concat the fields (with public getter <b>ONLY</b>) in the given object to
	 * string in following format.
	 * 
	 * <pre>
	 * the.fully.qualitied.class.name.Example(field1=value1, field2=value2, field3=value3, )
	 * </pre>
	 * 
	 * This method will truncate the result string by given length and return
	 * it.
	 * 
	 * @since 2009-Sep-01
	 * @author Danny Wong Chun W003372
	 * @param object
	 *            the object to be TO-STRING-ed.
	 * @param trunLength
	 *            the max length for the result string
	 * @return the result string, never null.
	 * @see StringUtil#concatFieldValuesAux(Object)
	 */
	public static String concatFieldValues(Object object, int trunLength) {
		StringBuilder sb = concatFieldValuesAux(object);
		return sb.length() > trunLength ? sb.substring(0, trunLength) : sb
				.toString();
	}

	/**
	 * Concat the fields (with public getter <b>ONLY</b>) in the given object to
	 * string in following format.
	 * 
	 * <pre>
	 * the.fully.qualitied.class.name.Example(field1=value1, field2=value2, field3=value3, )
	 * </pre>
	 * 
	 * This method will return the whole string without any truncate.
	 * 
	 * @since 2009-Sep-01
	 * @author Danny Wong Chun W003372
	 * @param object
	 *            the object to be TO-STRING-ed.
	 * @return the result string, never null.
	 * @see StringUtil#concatFieldValues(Object, int)
	 * @see StringUtil#concatFieldValuesAux(Object)
	 */
	public static String concatFieldValuesNoTruncate(Object object) {
		return concatFieldValuesAux(object).toString();
	}


	/**
	 * <pre>
	 * eg1 : insertString(&quot;the length of string value&quot;, &quot;_&quot;, 7) return &quot;the len_gth of _string _value&quot;
	 * </pre>
	 * 
	 * <pre>
	 * eg2 : insertString(&quot;value&quot;, &quot;_&quot;, 7) return &quot;value&quot;
	 * </pre>
	 * 
	 * <pre>
	 * eg3 : insertString(&quot;the length of &quot;, &quot;_&quot;, 7) return &quot;the len_gth of &quot;
	 * </pre>
	 * 
	 * @since 2009-11-04
	 * @author Raymon Li Wei Wen D000375
	 * @param val
	 * @param eachLength
	 * @param insertStr
	 * @return
	 */
	public static String insertString(String val, int eachLength,
			String insertStr) {
		if (val != null) {
			int times = (val.length() % eachLength > 0) ? (val.length()
					/ eachLength + 1) : (val.length() / eachLength);
			if (times > 1) {
				String regex = "";
				String replacement = "";
				for (int i = 0; i < times; i++) {
					if (i > 0) {
						regex = regex + "(.{" + eachLength + "})";
					}
					replacement = replacement + insertStr + "$" + (i + 1);
				}
				return val.replaceAll(regex + "(.+)", replacement
						.substring(insertStr.length()));
			}
		}
		return val;
	}
	
	/**
	 * Compare 2 strings (case-sensitive) ignoring null or empty string which means
	 * null == empty string and vice versa.
	 * 
	 * @param string1 string to compare
	 * @param string2 another string to be compared
	 * @return true if 2 strings are equals. Otherwise, false.
	 */
	public static boolean equalsIgnoreNullEmpty(final String string1, final String string2) {
		if (string1 == null && string2 == null) return true;
		String fieldValueA = (string1 == null) ? "" : string1;
		String fieldValueB = (string2 == null) ? "" : string2;
		
		if (fieldValueA.equals(fieldValueB)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Split a String with specific delimiter to a List which is without duplication and trimmed 
	 * <pre>
	 * [For Examaple]
	 * input: Peter, is, a, clever, guy
	 * output: List {Peter,is,a,clever,guy}
	 * </pre>
	 * 
	 * @param stringList
	 * @param delim
	 * @return java.util.List of String. Null if any exception
	 */
	public static List<String> string2List (String stringList, String delim ){
		List<String> result = new ArrayList<String>();
		try {
			StringTokenizer st = new StringTokenizer(stringList, delim);
			while (st.hasMoreTokens()){
				String temp = st.nextToken().trim();
				if (!result.contains(temp)){
					result.add(temp);
				}
			}			
		} catch(Exception e){
			iLog.error("string2List", e.getMessage(), e);
			result = null;
		}
		return result;
	}
	/**
	 * Split a String with specific delimiter to a List which is without duplication and trimmed 
	 * Exception found in Batch uploading CDIR files with Same file size. De-Dup feature will lost a size entry.
	 * @param stringList
	 * @param delim
	 * @return java.util.List of String. Null if any exception
	 */
	public static List<String> string2ListRaw (String stringList, String delim ){
		List<String> result = new ArrayList<String>();
		try {
			StringTokenizer st = new StringTokenizer(stringList, delim);
			while (st.hasMoreTokens()){
				String temp = st.nextToken().trim();				
				result.add(temp);				
			}			
		} catch(Exception e){
			iLog.error("string2List", e.getMessage(), e);
			result = null;
		}
		return result;
	}	
	/**
	 * Filter out mismatch characters.
	 *
	 * @since 2013-Oct-16
	 * @author Howard Chui 0001314
	 * @param target
	 * @param pattern
	 * @param maximium 
	 *            
	 * @return the result string, never null.
	 * @see StringUtil#concatFieldValues(Object, int)
	 */
	public static String filterOutMisMatch(String target, String pattern,  Integer max){
		String result="";
		String c="";
		max= ( max <=0 ? 10 : max);  // Default 10		
		for (int i = 0; i < target.length(); i++) {
			c = target.substring(i,i+1);
			if(c.matches(pattern)){
				result = result + target.substring(i,i+1);;
			}
			if(result.length() >= max){
				return result;
			}
		}
		return result;
	}	
	/**
	 * Escape Unix/Linux shell arguments to string literals, ie. using single quotes to quote each argument.
	 * This method is guranteed NOT to modify any content from the user input.
	 * 
	 * As stated in <a href="http://www.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html">open standard</a>
	 * section 2.2.2, there must not be any single quote within a pair of single quotes.
	 * 
	 * To prevent any command execution in parameter using <code>$(echo hello)</code> and <code>`echo hello`</echo>
	 * as well as other file name globbing, we will quote each arguments with a pair of single quotes to make
	 * it as a literal string. If the given argument already quoted its content with double quotes, we will
	 * convert them into single quotes too. If there is any escaped double quotes, we will unescape them after converting
	 * double-quotes into single-quotes.
	 * 
	 * Please see our unit test for more information.
	 *  
	 * @param arguments
	 * @return a {@link List} of string; each element represents an single-quoted argument in the caller input.
	 * @throws IllegalArgumentException if there is any single quote in user provided arguments.
	 */
	public static List<String> escapeShellArgumentsToLiterals(final String arguments) throws IllegalArgumentException {

		if (StringUtil.isEmptyOrNull(arguments)) {
			return Collections.emptyList();
		}
		
		if (arguments.indexOf('\'') != -1) {
			throw new IllegalArgumentException("No single quote from user input allowed.");
		}
		
		boolean insideQuote = false;
		
		StringBuffer s = new StringBuffer(arguments);
		StringBuffer doubleQuotesToSingleQuotes = new StringBuffer();
		
		for (int i = 0; i < s.length(); i++) {
			char c = arguments.charAt(i);
			if (!insideQuote) {
				if (c == '"') {
					doubleQuotesToSingleQuotes.append('\'');
					insideQuote = true;
					continue;
				} else {
					doubleQuotesToSingleQuotes.append(c);
				}
			} else {
				if (c == '"') {
					if (arguments.charAt(i-1) != '\\') {
						insideQuote = false;
						doubleQuotesToSingleQuotes.append('\'');
						continue;
					} else {
						doubleQuotesToSingleQuotes.deleteCharAt(doubleQuotesToSingleQuotes.length()-1);
						doubleQuotesToSingleQuotes.append(c);
					}
				} else {
					if (c == ' ' || c == '\t') {
						doubleQuotesToSingleQuotes.append(":SPACE_INSIDE_QUOTE:");
					} else {
						doubleQuotesToSingleQuotes.append(c);
					}
				}
			}
		}
		
		// data structure for storing the final escaped and single-quoted arguments
		List<String> finalResult = new ArrayList<String>();
		
		if (doubleQuotesToSingleQuotes.indexOf("'") == -1) {
			// if no quotes in user input; we just split the caller input by space.
			String [] splitsBySpace = doubleQuotesToSingleQuotes.toString().split(" ");
			for (String split : splitsBySpace) {
				finalResult.add("'" + split.replaceAll(":SPACE_INSIDE_QUOTE:", " ") + "'");
			}
		} else {
			List<Integer> quoteIndexes = new ArrayList<Integer>();
			int previousIndex = -1;
			for (int i = 0; i < doubleQuotesToSingleQuotes.length(); i++) {
				int quoteIndex = doubleQuotesToSingleQuotes.indexOf("'",i);
				if (quoteIndex != previousIndex && quoteIndex != -1) {
					quoteIndexes.add(quoteIndex);
				}
				previousIndex = quoteIndex;
			}
			
			for (int i = 0; i < quoteIndexes.size(); i++) {
				if (i == 0) {
					String firstSegment = doubleQuotesToSingleQuotes.substring(0, quoteIndexes.get(0)).trim();
					if (!StringUtil.isEmptyOrNull(firstSegment)) {
						String [] splitsBySpace = firstSegment.split(" ");
						for (String split : splitsBySpace) {
							finalResult.add("'" + split.replaceAll(":SPACE_INSIDE_QUOTE:", " ") + "'");
						}
					}
					String segment = doubleQuotesToSingleQuotes.substring(quoteIndexes.get(0) + 1, quoteIndexes.get(i+1)).trim();
					if (!StringUtil.isEmptyOrNull(segment)) {
						String [] splitsBySpace = segment.split(" ");
						for (String split : splitsBySpace) {
							finalResult.add("'" + split.replaceAll(":SPACE_INSIDE_QUOTE:", " ") + "'");
						}
					}
				} else if (i == quoteIndexes.size() - 1) {
					String segment = doubleQuotesToSingleQuotes.substring(quoteIndexes.get(i) + 1).trim();
					if (!StringUtil.isEmptyOrNull(segment)) {
						String [] splitsBySpace = segment.split(" ");
						for (String split : splitsBySpace) {
							finalResult.add("'" + split.replaceAll(":SPACE_INSIDE_QUOTE:", " ") + "'");
						}
					}
				} else {
					String segment = doubleQuotesToSingleQuotes.substring(quoteIndexes.get(i) + 1, quoteIndexes.get(i+1)).trim();
					if (!StringUtil.isEmptyOrNull(segment)) {
						String [] splitsBySpace = segment.split(" ");
						for (String split : splitsBySpace) {
							finalResult.add("'" + split.replaceAll(":SPACE_INSIDE_QUOTE:", " ") + "'");
						}
					}
				}
			}
		}
		return finalResult;
	}
	
	public static boolean isNumeric(String number){
	    try{
	        Double.parseDouble(number);
	    } catch(NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	
	public static String str2txt(String tmp) {
		if (tmp == null || tmp.equals("")) {
			return "";
		} else {
			StringBuffer buf = new StringBuffer();
			char[] charTxt = tmp.toCharArray();
			for (int i = 0; i < tmp.length(); i++) {
				if (charTxt[i] == '\\') {
					buf.append("\\\\");
				} else if (charTxt[i] == '\"') {
					buf.append("&quot;");
				} else if (charTxt[i] == '&') {
					buf.append("&amp;");
				} else if (charTxt[i] == '>') {
					buf.append("&gt;");
				} else if (charTxt[i] == '<') {
					buf.append("&lt;");
				} else {
					buf.append(charTxt[i]);
				}
			}
			return buf.toString();
		}
	}
  
  /**
	 * Convert a string with bracket-quoted values to list of string.
	 * 
	 * <pre>
	 * e.g. openBracket / closeBracket = []
	 * input: [ABC] [DEF][GHI]  [JKL]
	 * output: List {ABC, DEF, GHI, JKL}
	 * </pre>
	 * 
	 * @param paramStr
	 * @param openBracket
	 * @param closeBracket
	 * @return java.util.List of String. Null if any exception
	 */
	public static List<String> string2List(String paramStr, String openBracket, String closeBracket) {
		List<String> resultList = new ArrayList<String>();
		try {
			StringTokenizer st1 = new StringTokenizer(paramStr, openBracket);
			while (st1.hasMoreTokens()) {
				String tmp = st1.nextToken();
				int offset = tmp.indexOf(closeBracket);
				if (offset != -1) {
				  tmp = tmp.substring(0, tmp.indexOf(closeBracket));
				  resultList.add(tmp);
				}
			}
		} catch (Exception e) {
			iLog.error("string2List", e.getMessage(), e);
			resultList = null;
		}
		return resultList;
	}

}
