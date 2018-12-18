package com.wtt.framework.adaptor.email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.internet.MimeUtility;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import com.wtt.framework.logging.AppLogFactory;
import com.wtt.framework.logging.ILog;
import com.wtt.framework.noti.constant.NotiConstant;
import com.wtt.framework.util.BracketUtil;
import com.wtt.framework.util.StringUtil;

public class EmailSender {
	
	private static final ILog iLog = AppLogFactory.getLog(EmailSender.class, NotiConstant.MODULE_NOTI_EMAIL);

	public static final String SMTP = "-smtp";
	public static final String TO = "-to";
	public static final String CC = "-cc";
	public static final String BCC = "-bcc";
	public static final String REPLY = "-reply";
	public static final String FROM = "-from";
	public static final String SUBJECT = "-subject";
	public static final String BODY = "-body";
	public static final String BODYFILE = "-bodyfile";
	public static final String ATTACH = "-attach";
	public static final String HELP = "-help";
	public static final String TYPE = "-type";
	public static final String CHARSET = "-charset";
	public static final String TRACE = "-trace";
	public static final String SEVERITY = "-severity";

	public static final String TYPE_HTML = "HTML";
	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_RICH = "RICH";
	
	public static final String CHARSET_BIG5 = "BIG5";
	public static final String CHARSET_UTF8 = "UTF8";
	public static final String CHARSET_GB = "GB";

	public static final String TRACE_TRUE = "TRUE";
	public static final String TRACE_FALSE = "FALSE";
	
	public static final String SEVERITY_INFO = "I";
	public static final String SEVERITY_WARN = "W";
	public static final String SEVERITY_ERROR = "E";

	private String smtpIP = "";
	private String toAddr = "";
	private String ccAddr = "";
	private String bccAddr = "";
	private String replyAddr = "";
	private String fromAddr = "";
	private String subject = "";
	private String body = "";
	private String bodyfile = "";
	private String attach = "";
	private String type = "";
	private String charset = "";
	private String severity = "";

	/**
	 * Main Driver.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		final String THIS_METHOD = "main";
		HashMap<String, String> arguMap = new HashMap<String, String>();
		try {
			arguMap = parseArgument(args);
			EmailSender sender = new EmailSender(arguMap);
			sender.send();
			System.exit(0);

		} catch (Exception e) {
			iLog.error(THIS_METHOD, e.getMessage(), e);
			System.exit(1);
		}

	}

	public static HashMap<String, String> parseArgument(String args[]) {
		HashMap<String, String> hMap = new HashMap<String, String>();

		for (int i = 0; i < args.length; i++) {
			if (EmailSender.HELP.equals(args[i])) {
				EmailSender.printUsage();
				System.exit(1);
			} else if (EmailSender.SMTP.equals(args[i])) {
				hMap.put(EmailSender.SMTP, args[i + 1]);
			} else if (EmailSender.TO.equals(args[i])) {
				hMap.put(EmailSender.TO, args[i + 1]);
			} else if (EmailSender.CC.equals(args[i])) {
				hMap.put(EmailSender.CC, args[i + 1]);
			} else if (EmailSender.BCC.equals(args[i])) {
				hMap.put(EmailSender.BCC, args[i + 1]);
			} else if (EmailSender.REPLY.equals(args[i])) {
				hMap.put(EmailSender.REPLY, args[i + 1]);
			} else if (EmailSender.FROM.equals(args[i])) {
				hMap.put(EmailSender.FROM, args[i + 1]);
			} else if (EmailSender.SUBJECT.equals(args[i])) {
				hMap.put(EmailSender.SUBJECT, args[i + 1]);
			} else if (EmailSender.BODY.equals(args[i])) {
				hMap.put(EmailSender.BODY, args[i + 1]);
			} else if (EmailSender.BODYFILE.equals(args[i])) {
				hMap.put(EmailSender.BODYFILE, args[i + 1]);
			} else if (EmailSender.ATTACH.equals(args[i])) {
				hMap.put(EmailSender.ATTACH, args[i + 1]);
			} else if (EmailSender.TYPE.equals(args[i])) {
				hMap.put(EmailSender.TYPE, args[i + 1]);
			} else if (EmailSender.CHARSET.equals(args[i])) {
				hMap.put(EmailSender.CHARSET, args[i + 1]);
			} else if (EmailSender.TRACE.equals(args[i])) {
				hMap.put(EmailSender.TRACE, args[i + 1]);
			} else if (EmailSender.SEVERITY.equals(args[i])) {
				hMap.put(EmailSender.SEVERITY, args[i + 1]);
			}
		}

		return hMap;
	}

	private static void printUsage() {
		System.out.print("\n  Usage: java com.wtt.framework.adaptor.email.EmailSender\n"
				+ "\t[-smtp XX]  [-to <John Li, Tai Man>johnli@abc.com;may.li@pqr.com]\n" + "\t[-cc XX;XX] [-bcc XX;XX] [-reply XX]\n"
				+ "\t[-from <John Chan, Tai Man>johnchan@abcde.com]\n" + "\t[-subj XX]  [-body XX | -bodyfile XX]\n"
				+ "\t[-attach XX;XX] [-type HTML | TEXT]\n" + "\t[-charset BIG5 | UTF8]\n"
				+ "\t[-severity I | W | E]\n"
				+ "\t[-help]\n"
				+ "\n  Return: 0=success, 1=fail\n\n");
	}

	public EmailSender(Map<String, String> arguMap) {
		this.smtpIP = arguMap.get(EmailSender.SMTP) == null ? "" : arguMap.get(EmailSender.SMTP);
		this.toAddr = arguMap.get(EmailSender.TO) == null ? "" : arguMap.get(EmailSender.TO);
		this.ccAddr = arguMap.get(EmailSender.CC) == null ? "" : arguMap.get(EmailSender.CC);
		this.bccAddr = arguMap.get(EmailSender.BCC) == null ? "" : arguMap.get(EmailSender.BCC);
		this.replyAddr = arguMap.get(EmailSender.REPLY) == null ? "" : arguMap.get(EmailSender.REPLY);
		this.fromAddr = arguMap.get(EmailSender.FROM) == null ? "" : arguMap.get(EmailSender.FROM);
		this.subject = arguMap.get(EmailSender.SUBJECT) == null ? "" : arguMap.get(EmailSender.SUBJECT);
		this.body = arguMap.get(EmailSender.BODY) == null ? "" : arguMap.get(EmailSender.BODY);
		this.bodyfile = arguMap.get(EmailSender.BODYFILE) == null ? "" : arguMap.get(EmailSender.BODYFILE);
		this.attach = arguMap.get(EmailSender.ATTACH) == null ? "" : arguMap.get(EmailSender.ATTACH);
		this.type = arguMap.get(EmailSender.TYPE) == null ? "" : arguMap.get(EmailSender.TYPE);
		this.charset = arguMap.get(EmailSender.CHARSET) == null ? "" : arguMap.get(EmailSender.CHARSET);
		this.severity = arguMap.get(EmailSender.SEVERITY) == null ? "" : arguMap.get(EmailSender.SEVERITY);
	}
	
	/**
	 * Set the common attributes of an email.
	 * 
	 * @param email
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	private void setEmailCommonAttributes(MultiPartEmail email) throws EmailException, UnsupportedEncodingException {
		final String THIS_METHOD = "setEmailCommonAttributes";
		
		email.setHostName(smtpIP);
		
		// set charset
		String tempCharset = "UTF-8";
		if (CHARSET_BIG5.equals(charset)) {
			email.setCharset("BIG5");
			tempCharset = "BIG5";
			iLog.debug(THIS_METHOD, "Email charset set to BIG5.");
		} else if (CHARSET_UTF8.equals(charset)) {
			email.setCharset("UTF-8");
			tempCharset = "UTF-8";
			iLog.debug(THIS_METHOD, "Email charset set to UTF-8.");
		} else if (CHARSET_GB.equals(charset)) {
			email.setCharset("GB2312");
			tempCharset = "GB2312";
			iLog.debug(THIS_METHOD, "Email charset set to GB2312.");
		} else {
				iLog.debug(THIS_METHOD, "Email charset left untouched.");
		}
		
		List<String> tmpList = null;
		String tmpAddrName[] = { "", "" };
		// prepare to addr
		tmpList = tokenize(toAddr);
		for (int i = 0; i < tmpList.size(); i++) {
			tmpAddrName = this.getEmailAddrAndName((String) tmpList.get(i));
			email.addTo(tmpAddrName[0], MimeUtility.encodeText(tmpAddrName[1], tempCharset, "B"));
		}

		// prepare cc addr
		tmpList = tokenize(ccAddr);
		for (int i = 0; i < tmpList.size(); i++) {
			tmpAddrName = this.getEmailAddrAndName((String) tmpList.get(i));
			email.addCc(tmpAddrName[0], MimeUtility.encodeText(tmpAddrName[1], tempCharset, "B"));
		}
		
		// prepare bcc addr
		tmpList = tokenize(bccAddr);
		for (int i = 0; i < tmpList.size(); i++) {
			tmpAddrName = this.getEmailAddrAndName((String) tmpList.get(i));
			email.addBcc(tmpAddrName[0], MimeUtility.encodeText(tmpAddrName[1], tempCharset, "B"));
		}

		if (null != tmpAddrName) {
			tmpAddrName = this.getEmailAddrAndName(replyAddr);
			email.addReplyTo(tmpAddrName[0], MimeUtility.encodeText(tmpAddrName[1], tempCharset, "B"));
		}

		if (null != tmpAddrName) {
			tmpAddrName = this.getEmailAddrAndName(fromAddr);
			email.setFrom(tmpAddrName[0], MimeUtility.encodeText(tmpAddrName[1], tempCharset, "B"));
		}
		
		// Set the priority of the email
		// X-Priority values are generally numbers like 1 (for highest priority), 3 (normal) and 5 (lowest).
		if (SEVERITY_INFO.equals(severity)) {
			email.addHeader("X-Priority", "3") ;
			iLog.debug(THIS_METHOD, "Email X-Priority set to normal.");
		} else if (SEVERITY_WARN.equals(severity)) {
			email.addHeader("X-Priority", "2") ;
			iLog.debug(THIS_METHOD, "Email X-Priority set to high.");
		} else if (SEVERITY_ERROR.equals(severity)) {
			email.addHeader("X-Priority", "1") ;
			iLog.debug(THIS_METHOD, "Email X-Priority set to highest.");
		} else {
			email.addHeader("X-Priority", "3") ;
			iLog.debug(THIS_METHOD, "Email X-Priority set to default (normal).");
		}
		
		// set email subject
		email.setSubject(subject);
	}

	/**
	 * Send email in HTML.  
	 * @throws Exception
	 */
	public void sendHtmlEmail() throws Exception {
		final String THIS_METHOD = "sendHtmlEmail";
		
		// Create the email message
		HtmlEmail email = new HtmlEmail();
		setEmailCommonAttributes(email);
		
		// prepare email body
		if (body.trim().length() != 0) {
			iLog.debug(THIS_METHOD, "Setting body content with direct argument");
			email.setMsg(body);
		} else if (bodyfile.trim().length() != 0) {
			iLog.debug(THIS_METHOD, "Setting body content with external file.");
			BufferedReader br = new BufferedReader(new FileReader(bodyfile));
			String eachLine = "";
			StringBuilder sb = new StringBuilder();
			while ((eachLine = br.readLine()) != null) {
				sb.append(eachLine).append("\n");
			}
			br.close();
			body = sb.toString();
			email.setHtmlMsg(body);
		}

		// add the attachment
		// Create the attachment
		List<String> tmpList = tokenize(attach);
		for (int i = 0; i < tmpList.size(); i++) {
			String tmpAttachFullPath = tmpList.get(i);

			iLog.debug(THIS_METHOD, "Adding attachment {}", tmpAttachFullPath);
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(tmpAttachFullPath);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);

			/*
			 * e.g. tmpAttachFullPath = /home/report/12345.pdf tmpL = { home,
			 * report, 12345 }
			 * 
			 * e.g. tmpAttachFullPath = c:\\temp\\report\\12345.pdf tmpL = { c,
			 * temp, report, 12345 }
			 * 
			 * e.g. tmpAttachFullPath = c:/temp/report/12345.pdf tmpL = { c,
			 * temp, report, 12345 }
			 */
			List<String> tmpL = tokenize(tmpAttachFullPath, ":/\\");
			String tmpFilename = (String) tmpL.get(tmpL.size() - 1);
			attachment.setDescription(tmpFilename);
			attachment.setName(tmpFilename);
			email.attach(attachment);
		}

		// send the email
		email.send();

		iLog.info(THIS_METHOD, "-- Finish --");
	}

	/**
	 * Remark: Both sendHtmlEmail and sendTextEmail coding are 99% similar to
	 * each other. Since Interface is absent in Jakarta Common Email, those
	 * codings are duplicated.
	 * 
	 * Difference: 1. create object of HtmlEmail and MultiPartEmail 2. send
	 * email message body setHtmlMsg() and setMsg()
	 * @throws Exception
	 */
	public void send() throws Exception {
		if (EmailSender.TYPE_HTML.equals(type)) {
			sendHtmlEmail();
		} else if (EmailSender.TYPE_TEXT.equals(type)) {
			sendTextEmail();
		} else if (EmailSender.TYPE_RICH.equals(type)) {
			sendRichEmail();			
		} else {
			throw new Exception("Incorrect Email Type");
		}
	}

	/**
	 * Send email in plain text.
	 * @throws Exception
	 */
	public void sendTextEmail() throws Exception {
		final String THIS_METHOD = "sendTextEmail";

		// Create the email message
		MultiPartEmail email = new MultiPartEmail();
		this.setEmailCommonAttributes(email);

		// prepare email body
		if (!StringUtil.isEmptyOrNull(body)) {
			iLog.debug(THIS_METHOD, "Setting body content with direct argument");
			email.setMsg(body);
		} else if (!StringUtil.isEmptyOrNull(bodyfile)) {
			iLog.debug(THIS_METHOD, "Setting body content with external file.");
			BufferedReader br = new BufferedReader(new FileReader(bodyfile));
			String eachLine = "";
			StringBuilder sb = new StringBuilder();
			while ((eachLine = br.readLine()) != null) {
				sb.append(eachLine).append("\n");
			}
			br.close();
			body = sb.toString();
			email.setMsg(body);
		}

		// add the attachment
		// Create the attachment
		List<String> tmpList = tokenize(attach);
		for (int i = 0; i < tmpList.size(); i++) {
			String tmpAttachFullPath = tmpList.get(i);

			iLog.debug(THIS_METHOD, "Adding attachment {}", tmpAttachFullPath);
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(tmpAttachFullPath);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);

			/*
			 * e.g. tmpAttachFullPath = /home/report/12345.pdf tmpL = { home,
			 * report, 12345 }
			 * 
			 * e.g. tmpAttachFullPath = c:\\temp\\report\\12345.pdf tmpL = { c,
			 * temp, report, 12345 }
			 * 
			 * e.g. tmpAttachFullPath = c:/temp/report/12345.pdf tmpL = { c,
			 * temp, report, 12345 }
			 */
			List<String> tmpL = tokenize(tmpAttachFullPath, ":/\\");
			String tmpFilename = (String) tmpL.get(tmpL.size() - 1);
			attachment.setDescription(tmpFilename);
			attachment.setName(tmpFilename);
			email.attach(attachment);
		}

		// send the email
		email.send();

		iLog.info(THIS_METHOD, "-- Finish --");

	}

	/**
	 * Send email in plain text.
	 * @throws Exception
	 */
	public void sendRichEmail() throws Exception {
		final String THIS_METHOD = "sendHtmlEmail";
		
		// Create the email message
		HtmlEmail email = new HtmlEmail();
		setEmailCommonAttributes(email);
		
		// prepare email body
		if (body.trim().length() != 0) {
			iLog.debug(THIS_METHOD, "Setting body content with direct argument");
			email.setHtmlMsg(body); // Not need markup
		} else if (bodyfile.trim().length() != 0) {
			iLog.debug(THIS_METHOD, "Setting body content with external file.");
			BufferedReader br = new BufferedReader(new FileReader(bodyfile));
			String eachLine = "";
			StringBuilder sb = new StringBuilder();
			while ((eachLine = br.readLine()) != null) {
				sb.append(eachLine).append("\n");
			}
			br.close();
			body = sb.toString();
			email.setHtmlMsg(body);
		}

		// add the attachment
		// Create the attachment
		List<String> tmpList = tokenize(attach);
		for (int i = 0; i < tmpList.size(); i++) {
			String tmpAttachFullPath = tmpList.get(i);

			iLog.debug(THIS_METHOD, "Adding attachment {}", tmpAttachFullPath);
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(tmpAttachFullPath);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			List<String> tmpL = tokenize(tmpAttachFullPath, ":/\\");
			String tmpFilename = (String) tmpL.get(tmpL.size() - 1);
			attachment.setDescription(tmpFilename);
			attachment.setName(tmpFilename);
			email.attach(attachment);
		}

		// send the email
		email.send();

		iLog.info(THIS_METHOD, "-- Finish --");
	}
	
	private List<String> tokenize(String paramStr) {
		return this.tokenize(paramStr, ";");
	}

	private List<String> tokenize(String paramStr, String paramDelimitor) {
		List<String> tokenList = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(paramStr, paramDelimitor);
		while (st.hasMoreTokens()) {
			tokenList.add(st.nextToken());
		}
		return tokenList;
	}

	/*
	 * e.g. input : <John Chan, Tai Man>johnchan@abcde.com output: result[0] =
	 * "johnchan@abcde.com" output: result[1] = "John Chan, Tai Man"
	 * 
	 * e.g. input: johnchan@abcde.com output: result[0] = "johnchan@abcde.com"
	 * output: result[1] = ""
	 */
	private String[] getEmailAddrAndName(String paramStr) {
		final String THIS_METHOD = "getEmailAddrAndName"; 
		String result[] = { paramStr, "" };
		int idxClose = paramStr.indexOf(">");
		int idxOpen = paramStr.indexOf("<");
		if ((idxClose != -1) && (idxOpen != -1)) {
			List<String> tmpList1 = BracketUtil.string2List(paramStr, "<", ">");
			result[0] = paramStr.substring(idxClose + 1).trim();
			result[1] = ((String) tmpList1.get(0)).trim();
		}

		iLog.debug(THIS_METHOD, "#{}#{}#", result[0], result[1]);

		return result;
	}
	
}
