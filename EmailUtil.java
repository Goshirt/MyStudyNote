/**
 * 2009 All rights Reserved by Wharf T&T Java Development Team
 * 
 * Project		: framework
 * Package		: com.wtt.framework.util
 * File			: EmailUtil.java
 * Creation TS	: Nov 2, 2009 3:10:32 PM
 * 
 * ==============================================
 */

package com.wtt.framework.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import com.wtt.framework.constant.FwkConstant;
import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;

/**
 * Email reading utility  
 *
 * @author Danny Wong Chun W003372
 */
public final class EmailUtil {
	
	private static ILog iLog = AppLogFactory.getLog(EmailUtil.class, FwkConstant.MODULE_UTIL);
	
	/**
	 * Singleton instance for EmailUtil
	 */
	private static EmailUtil instance = new EmailUtil();
	
	/**
	 * @return the instance
	 */
	public static EmailUtil getInstance() {
		return instance;
	}
	
	/**
	 * Private constructor to ensure singleton
	 */
	private EmailUtil() {
		// do nothing
	}

	/**
	 * Retrieve the sender personal as per RFC 2047.
	 * 
	 * @param message the email message to parse
	 * @return the sender personal in String. Empty string if none.
	 * @throws MessagingException
	 */
	public static String getSenderName(Message message) throws MessagingException {
		InternetAddress address[] = (InternetAddress[]) message.getFrom();
		// Quote from InternetAddress#setPersonal().
		// If the name contains non US-ASCII characters, then the name will be encoded using 
		// the platform's default charset.
		
		// Get the personal name. If the name is encoded as per RFC 2047, 
		// it is decoded and converted into Unicode. If the decoding or conversion fails, 
		// the raw data is returned as is.
		String personal = address[0].getPersonal();
		return personal == null ? personal = "" : personal;
	}
	
	/**
	 * Retrieve the sender email address.
	 * 
	 * @param message the email message to parse
	 * @return sender email address in String. Empty string if none.
	 * @throws MessagingException
	 */
	public static String getSenderAddress(Message message) throws MessagingException {
		InternetAddress address[] = (InternetAddress[]) message.getFrom();
		String from = address[0].getAddress();
		return from == null ? from = "" : from;
	}

	/**
	 * Retrieve an array of TO/CC/BCC recipient of an email in form of InternetAddress object.
	 * 
	 * @param message the email message to parse
	 * @param reciType TO/CC/BCC recipient type
	 * @return an array of recipients InternetAddress object. 
	 * @throws MessagingException
	 * @see {@link javax.mail.Message.RecipientType}
	 */
	public static InternetAddress[] getRecipients(Message message, RecipientType reciType) throws MessagingException {
		return (InternetAddress[]) message.getRecipients(reciType); 
	}
	
	/**
	 * Retrieve a list of TO/CC/BCC recipient of an email in form of string object.
	 * 
	 * @param message the email message to parse
	 * @param reciType
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static List<String> getRecipientsAddress(Message message, RecipientType reciType) throws MessagingException, UnsupportedEncodingException {
		InternetAddress[] addresses = null;
		addresses = (InternetAddress[]) message.getRecipients(reciType);
		if (addresses != null) {
			List<String> emails = new ArrayList<String>();
			String email = null;
			for (InternetAddress addr : addresses) {
				email = addr.getAddress();
				if (email != null) {
					// Decode "unstructured" headers, that is, headers that are defined as 
					// '*text' as per RFC 822.
					email = MimeUtility.decodeText(email);
					emails.add(email);
				}
			}
			return emails;
		}
		return Collections.emptyList();
	}
	
	/**
	 * Retrieve the email subject.
	 * 
	 * @param message the email message to parse
	 * @return the email subject in string
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static String getSubject(Message message) throws MessagingException, UnsupportedEncodingException {
		String subject = "";
		subject = MimeUtility.decodeText(message.getSubject());
		return subject == null ? subject = "" : subject;
	}

	/**
	 * Retrieve the sent date of the email subject (might not be accurate, the value depends on the mail server settings)
	 * 
	 * @param message the email message to parse
	 * @return the sent date in string. Date format using {@link DateTimeUtil#DEFAULT_DATETIME_FORMAT}
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public static String getSentDate(Message message) throws MessagingException, UnsupportedEncodingException {
		Date sentdate = message.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat(DateTimeUtil.DEFAULT_DATETIME_FORMAT);
		return format.format(sentdate);
	}

	/**
	 * Retrieve the mail content in string (ignoring the attachment).
	 * 
	 * @param part the email part
	 * @return the mail content (body text) in string.
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static String getMailContent(Part part) throws MessagingException, IOException {
		StringBuilder bodyText = new StringBuilder(128);
		getMailContentAux(part, bodyText);
		return bodyText.toString();
	}

	/**
	 * Recursive method for getMailContent() as body part is a recursive structure. 
	 * 
	 * @param part the email part
	 * @param bodyText the mail content (body text) in string.
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static void getMailContentAux(Part part, StringBuilder bodyText) throws MessagingException, IOException {
		final String THIS_METHOD = "getMailContent";
		
		String contentType = part.getContentType();
		int nameindex = contentType.indexOf("name");
		boolean conname = false;
		if (nameindex != -1)
			conname = true;

		if (part.isMimeType("text/plain") && !conname) {
			bodyText.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodyText.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContentAux(multipart.getBodyPart(i),bodyText);
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContentAux((Part) part.getContent(),bodyText);
		} else {
			iLog.warn(THIS_METHOD, "unknown MIME type [" + contentType + "]");
		}
	}

	/**
	 * Retrieve the unique message ID of an email in string.
	 * 
	 * @param mimeMessage the email message to parse
	 * @return the unique message ID in string.
	 * @throws MessagingException
	 */
	public static String getMessageId(MimeMessage mimeMessage) throws MessagingException {
		return mimeMessage.getMessageID();
	}
	
	/**
	 * Determine whether an email has been read.
	 * 
	 * @param message the email message to parse
	 * @return true if the email is read. Otherwise, false.
	 * @throws MessagingException
	 */
	public static boolean isRead(Message message) throws MessagingException {
		boolean isNew = false;
		Flags flags = message.getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isNew = true;
				break;
			}
		}
		return isNew;
	}

	/**
	 * To set a message as 'READ' or 'UNREAD'.
	 * 
	 * @param message the email message to parse
	 * @param seenYet boolean to indicate whether the email has been read. True as READ already, false as unread.
	 * @throws MessagingException
	 */
	public static void setReadStatus(Message message, boolean seenYet) throws MessagingException {
		message.setFlag(Flags.Flag.SEEN, seenYet);
	}
	
	public static void delete(Message message) throws MessagingException {
		message.setFlag(Flags.Flag.DELETED, true);
	}
	
	/**
	 * Save email attachments into local filesystem and referenced as a list of java.io.File objects.
	 * 
	 * @param part the email part.
	 * @return a list of java.io.File referencing the saved attachments.
	 */
	public static List<File> saveAttachment(Part part, String destinationDirectoryAbsolutePath) {
		final String THIS_METHOD = "saveAttachment";
		List<File> fileList = new ArrayList<File>();
		String fileName = "";
		try {
			if (part.isMimeType("multipart/*")) {
				Multipart mp = (Multipart) part.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					BodyPart mpart = mp.getBodyPart(i);
					if (mpart.getFileName() != null) {
						fileName = MimeUtility.decodeText(mpart.getFileName());
						File f = new File(destinationDirectoryAbsolutePath + File.separator + fileName);
						FileUtil.write(f, mpart.getInputStream(), false);
						fileList.add(f);
					} else if (mpart.isMimeType("multipart/*")) {
						saveAttachment(mpart, destinationDirectoryAbsolutePath);
					} 
				}
			} else if (part.isMimeType("message/rfc822")) {
				saveAttachment((Part) part.getContent(), destinationDirectoryAbsolutePath);
			}
		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
		}
		return fileList;
	}
	
}
