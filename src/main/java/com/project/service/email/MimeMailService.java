package com.project.service.email;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.project.utils.ResourceBundleUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * MIME邮件服务类.
 * 
 * 演示由Freemarker引擎生成的的html格式邮件, 并带有附件.
 * 
 * @author calvin
 */
@Component
public class MimeMailService {

	private static final String DEFAULT_ENCODING = "utf-8";

	private static Logger logger = LoggerFactory.getLogger(MimeMailService.class);

	private JavaMailSender mailSender;
	
	private Template template;

	/**
	 * Spring的MailSender.
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * 注入Freemarker引擎配置,构造Freemarker 邮件内容模板.
	 */
	public void setFreemarkerConfiguration(Configuration freemarkerConfiguration) throws IOException {
		//根据freemarkerConfiguration的templateLoaderPath载入文件.
		template = freemarkerConfiguration.getTemplate("mailTemplate.ftl", DEFAULT_ENCODING);
	}

	/**
	 * 发送MIME格式的用户修改通知邮件.
	 * @param mailTo 
	 * @param subject
	 * @param content 内容
	 */
	public void sendNotificationMail(String mailTo,String subject,String content) {
		sendNotificationMail(mailTo,subject,content,false);
	}
	
	/**
	 * 发送MIME格式的用户修改通知邮件.
	 * @param mailTo
	 * @param subject
	 * @param parameterMap 模板参数
	 */
	public void sendNotificationMail(String mailTo,String subject,Map<String,String> parameterMap) {
		sendNotificationMail(mailTo,subject,parameterMap,false);
	}
	
	/**
	 * 发送MIME格式的用户修改通知邮件.
	 * @param mailTo
	 * @param subject
	 * @param content 内容
	 * @param useAttachmentUrl 是否使用默认路径（config.properties中mailAttachmentUrl的）附件
	 */
	public void sendNotificationMail(String mailTo,String subject,String content,Boolean useAttachmentUrl) {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true, DEFAULT_ENCODING);

			helper.setFrom(ResourceBundleUtil.getString("mailFrom"));
			helper.setTo(mailTo);
			helper.setSubject(subject);

			helper.setText(content, true);

			if(useAttachmentUrl){
				File attachment = generateAttachment();
				helper.addAttachment(attachment.getName(), attachment);
			}

			mailSender.send(msg);
			logger.info("HTML版邮件已发送至"+mailTo);
		} catch (MessagingException e) {
			logger.error("构造邮件失败", e);
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
		}
	}
	
	/**
	 * 发送MIME格式的用户修改通知邮件.
	 * @param mailTo
	 * @param subject
	 * @param content 内容
	 * @param attachment 附件
	 */
	public void sendNotificationMail(String mailTo,String subject,String content,File attachment) {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true, DEFAULT_ENCODING);

			helper.setFrom(ResourceBundleUtil.getString("mailFrom"));
			helper.setTo(mailTo);
			helper.setSubject(subject);

			helper.setText(content, true);

			if(attachment!=null)
				helper.addAttachment(attachment.getName(), attachment);

			mailSender.send(msg);
			logger.info("HTML版邮件已发送至"+mailTo);
		} catch (MessagingException e) {
			logger.error("构造邮件失败", e);
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
		}
	}
	
	/**
	 * 
	 * 发送MIME格式的用户修改通知邮件.
	 * @param mailTo
	 * @param subject
	 * @param parameterMap 模板参数
	 * @param useAttachmentUrl 是否使用默认路径（config.properties中mailAttachmentUrl的）附件
	 */
	public void sendNotificationMail(String mailTo,String subject,Map<String,String> parameterMap,Boolean useAttachmentUrl) {

		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true, DEFAULT_ENCODING);

			helper.setFrom(ResourceBundleUtil.getString("mailFrom"));
			helper.setTo(mailTo);
			helper.setSubject(subject);

			String content = generateContent(parameterMap);
			helper.setText(content, true);

			if(useAttachmentUrl){
				File attachment = generateAttachment();
				helper.addAttachment(attachment.getName(), attachment);
			}

			mailSender.send(msg);
			logger.info("HTML版邮件已发送至"+mailTo);
		} catch (MessagingException e) {
			logger.error("构造邮件失败", e);
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
		}
	}
	
	/**
	 * 
	 * 发送MIME格式的用户修改通知邮件.
	 * @param mailTo
	 * @param subject
	 * @param parameterMap 模板参数
	 * @param attachment 附件
	 */
	public void sendNotificationMail(String mailTo,String subject,Map<String,String> parameterMap,File attachment) {

		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true, DEFAULT_ENCODING);

			helper.setFrom(ResourceBundleUtil.getString("mailFrom"));
			helper.setTo(mailTo);
			helper.setSubject(subject);

			String content = generateContent(parameterMap);
			helper.setText(content, true);

			if(attachment!=null)
				helper.addAttachment(attachment.getName(), attachment);

			mailSender.send(msg);
			logger.info("HTML版邮件已发送至"+mailTo);
		} catch (MessagingException e) {
			logger.error("构造邮件失败", e);
		} catch (Exception e) {
			logger.error("发送邮件失败", e);
		}
	}
	
	/**
	 * 使用Freemarker生成html格式内容.
	 */
	private String generateContent(Map<String,String> parameterMap) throws MessagingException {
		try {
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, parameterMap);
		} catch (IOException e) {
			logger.error("生成邮件内容失败, FreeMarker模板不存在", e);
			throw new MessagingException("FreeMarker模板不存在", e);
		} catch (TemplateException e) {
			logger.error("生成邮件内容失败, FreeMarker处理失败", e);
			throw new MessagingException("FreeMarker处理失败", e);
		}
	}

	/**
	 * 获取classpath中的附件.
	 */
	private File generateAttachment() throws MessagingException {
		try {
			Resource resource = new ClassPathResource(ResourceBundleUtil.getString("mailAttachmentUrl"));
			return resource.getFile();
		} catch (IOException e) {
			logger.error("构造邮件失败,附件文件不存在", e);
			throw new MessagingException("附件文件不存在", e);
		}
	}
}
