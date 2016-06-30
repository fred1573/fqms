package com.project.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.project.utils.ResourceBundleUtil;

/**
 * 纯文本邮件服务类.
 * 
 * @author calvin
 */
@Component
public class SimpleMailService {
	private static Logger logger = LoggerFactory.getLogger(SimpleMailService.class);

	private JavaMailSender mailSender;
	private String textTemplate;
	
	//使用代理服务器时打开
	/*static{
		Properties props = System.getProperties(); 
		props.setProperty("proxySet","true"); 
		props.setProperty("ProxyHost","proxy1.wanda.cn"); 
		props.setProperty("ProxyPort","8080"); 
		props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
	}*/

	/**
	 * 发送纯文本的邮件.
	 */
	public void sendNotificationMail(String mailTo,String subject,String content) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(ResourceBundleUtil.getString("mailFrom"));
		msg.setTo(mailTo);
		msg.setSubject(subject);
		msg.setText(content);
		
		sendMail(msg);
	}
	
	/**
	 * 使用文本模板发送纯文本的邮件.
	 * @param userName
	 * @param mailTo
	 * @param subject
	 */
	public void sendNotificationMailWithTextTemplate(String mailTo,String subject,Object ... args) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(ResourceBundleUtil.getString("mailFrom"));
		msg.setTo(mailTo);
		msg.setSubject(subject);

		//将用户名与当期日期格式化到邮件内容的字符串模板
		String content = String.format(textTemplate, args);
		msg.setText(content);
		
		sendMail(msg);
	}
	
	private void sendMail(SimpleMailMessage msg){
		try {
			mailSender.send(msg);
			logger.info("纯文本邮件已发送至{}", StringUtils.arrayToCommaDelimitedString(msg.getTo()));
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
		}
	}

	/**
	 * Spring的MailSender.
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * 邮件内容的字符串模板.
	 */
	public void setTextTemplate(String textTemplate) {
		this.textTemplate = textTemplate;
	}
}
