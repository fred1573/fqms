package com.project.entity.common;

import java.io.File;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.project.entity.IdEntity;
import com.project.utils.AttachmentUtil;

/**
 * AttachmentInfo entity.
 * 附件信息表
 * @author mowei
 */

@Entity
@Table(name="TOMATO_ATTACH")
//默认的缓存策略.
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Attachment extends IdEntity{

	private static final long serialVersionUID = 1L;
	
	//编码
	private String code;
	//类型
	private String type;
	//文件名
	private String fileName;
	//存储路径(相对路径)
	private String path;
	//描述
	private String description;
	//上传时间
	private Date uploadTime;
	//上传人
	private String uploadUser;
	
	//调用端临时存储路径
	private String clientTempPath;
	//附件
	private File file;
	//文件类型(MIME Type)
	private String contentType;

	// Constructors
	/** default constructor */
	public Attachment() {
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	/**
	 * @return the clientTempPath
	 */
	@Transient
	public String getClientTempPath() {
		return clientTempPath;
	}

	/**
	 * @param clientTempPath the clientTempPath to set
	 */
	@Transient
	public void setClientTempPath(String clientTempPath) {
		this.clientTempPath = clientTempPath;
	}

	/**
	 * @return the file
	 */
	@Transient
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	@Transient
	public void setFile(File file) {
		if(file!=null){
		this.file = file;
		this.clientTempPath = this.file.getPath();
		}
	}

	/**
	 * @return the contentType
	 */
	@Transient
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param fileFileName the fileName to set
	 */
	@Transient
	public void setFileFileName(String fileFileName) {
		this.fileName = fileFileName;
	}

	/**
	 * @param fileContentType the fileContentType to set
	 */
	@Transient
	public void setFileContentType(String fileContentType) {
		this.contentType = fileContentType;
	}

	@Transient
	public String getURL() {
		return this.path!=null&&!this.path.equals("") ? AttachmentUtil.getURL(this) : "";
	}
	
}