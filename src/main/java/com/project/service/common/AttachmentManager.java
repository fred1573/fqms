package com.project.service.common;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.common.AttachmentDao;
import com.project.entity.common.Attachment;
import com.project.exception.ServiceException;
import com.project.utils.AttachmentUtil;
import com.project.utils.encode.RandomUtil;

/**
 * 
 * @author mowei
 */
@Component
@Transactional
public class AttachmentManager {

	private static Logger logger = LoggerFactory.getLogger(AttachmentManager.class);

	@Autowired
	private AttachmentDao attachmentDao;

	/**
	 * 上传&保存附件
	 * @param attachment
	 * @throws ServiceException
	 */
	public void saveAttachmentInfo(Attachment attachment) throws ServiceException {
		if (attachment == null || attachment.getFile() == null)
			return;

		//将附件传输至资源服务器
		if (AttachmentUtil.transferToResourceServer(attachment)) {
			Long code = RandomUtil.getRandomNumber(19);
			attachment.setCode(code.toString());
			attachment.setUploadTime(new Date());
			attachment.setUploadUser(SpringSecurityUtil.getCurrentUserName());
			//保存附件信息
			attachmentDao.save(attachment);
			logger.info("保存附件"+attachment.getFileName()+"成功");
		} else {
			//从磁盘上删除文件
			AttachmentUtil.deleteUploadedAttachment(attachment);
			throw new ServiceException("保存附件信息出错!");
		}
	}

	/**
	 * 根据code删除附件
	 * @param code
	 * @throws ServiceException
	 */
	public void deleteAttachmentByCode(String code) throws ServiceException {
		if (code == null || code.trim().equals(""))
			return;

		//获取附件信息
		Attachment attachment = this.getAttachmentInfoByCode(code);
		if (attachment != null) {
			//删除附件信息记录
			attachmentDao.delete(attachment.getId());
			//从磁盘上删除文件
			AttachmentUtil.deleteUploadedAttachment(attachment);
		}
	}

	/**
	 * 根据code获取文件url
	 * @param code
	 * @return
	 * @throws ServiceException
	 */
	public String getURLByCode(String code) throws ServiceException {
		if (code == null || code.trim().equals(""))
			return "";
		Attachment attachment = this.getAttachmentInfoByCode(code);
		return attachment != null ? AttachmentUtil.getURL(attachment) : "";
	}

	/**
	 * 根据附件编码得到上传文件的原名
	 * @param code
	 * @return
	 * @author lei.zhou
	 * @throws ServiceException
	 */
	public String getFileNameByCode(String code) throws ServiceException {
		Attachment attachment = this.getAttachmentInfoByCode(code);
		return attachment != null ? attachment.getFileName() : "";
	}

	/** 
	 * 通过附件编码获得附件信息:Bean or Model
	 * @param code
	 * @return
	 * @throws ServiceException 
	 */
	@Transactional(readOnly = true)
	private Attachment getAttachmentInfoByCode(String code) throws ServiceException {
		if (code == null || code.trim().equals(""))
			return null;
		return attachmentDao.findUniqueBy("code", code);
	}

	/**
	 * 通过path查找Attachment
	 * @param pictureUrlPortal
	 * @return
	 */
	@Transactional(readOnly = true)
	public Attachment getAttachmentByPath(String path) {
		if (path == null || path.trim().equals(""))
			return null;
		return attachmentDao.findUniqueBy("path", path);
	}

}
