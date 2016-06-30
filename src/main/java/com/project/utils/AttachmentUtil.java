package com.project.utils;

import java.io.File;
import java.util.Date;

import com.project.common.Constants;
import com.project.entity.common.Attachment;
import com.project.utils.time.DateUtil;

/**
 * @author mowei
 * 附件上传帮助类
 */
public class AttachmentUtil {

	private static final String RESOURCE_SERVER_ROOT_PATH = "/home/backup/files/";
	private static final String RESOURCE_SERVER_ADDRESS = "";
	
	/**
	 * 根据附件类型获取类别目录
	 * @param attachmentType
	 * @return
	 * 
	 */
	private static String getServerDirByType(String attachmentType) {
		attachmentType = attachmentType!=null&&!attachmentType.trim().equals("") ? attachmentType.trim() : "other";
		String serverDir = null;
		if(attachmentType.equals(Constants.ATTACHMENT_TYPE_IMAGE)) {
			serverDir = RESOURCE_SERVER_ROOT_PATH + File.separator + attachmentType + "s";
		} else if(attachmentType.equals(Constants.ATTACHMENT_TYPE_DOCUMENT)) {
			serverDir = RESOURCE_SERVER_ROOT_PATH + File.separator + attachmentType + "s";
		} else {
			attachmentType = Constants.ATTACHMENT_TYPE_OTHER;
			serverDir = RESOURCE_SERVER_ROOT_PATH + File.separator + attachmentType + "s";
		}
		return serverDir;
	}
	
	/**
	 * 获取资源服务器上的目标路径
	 * @param serverDir
	 * @param relativeFilePath
	 * @return
	 * 
	 */
	public static String getResourceServerPath(String serverDir, String relativeFilePath) {
		//根据附件类型构造路径:资源服务器根目录 + 类别目录 + 文件相对路径
		String path = serverDir + relativeFilePath;
		//判断资源服务器上的目录是否存在
		File dirPath = new File(path).getParentFile();
		if(!dirPath.exists())
			if(!dirPath.mkdirs())
				return "";
		return path;
	}

	/** 
	 * 装配文件存储在资源服务器上的相对路径
	 * @param attachment
	 * @param newFileName
	 * @return
	 * 
	 */
	public static String generateRelativeFilePath(String newFileName) {
		return File.separator + DateUtil.format(new Date(), "yyyy-MM-dd_HH") + File.separator + newFileName;
	}
	
	/**
	 * 
	 * 生成新的服务端文件名
	 * @param originalFileName
	 * @param tempFileName
	 * @return
	 * 
	 */
	public static String generateNewFileName(String originalFileName, String tempFileName) {
		//获得原始文件后缀名
		int position = originalFileName.lastIndexOf( "." );
		String extension = position>=0 ? originalFileName.substring( position ) : "";
		
		//获得不带后缀的临时文件名
		position = tempFileName.lastIndexOf( "." );
		String newFileName = position>0 ? tempFileName.substring( 0, position ) : tempFileName;
		
		//组成一个新的文件名称
		return newFileName + extension;
	}
	
	/**
	 * 将附件传输至资源服务器,并将附件路径设置为新的相对路径
	 * @param attachment
	 * @return
	 * 
	 */
	public static boolean transferToResourceServer(Attachment attachment) {
		File file = attachment.getFile();
		if(file.exists() && file.isFile()) {
			//生成新文件名
			String newFileName = generateNewFileName(attachment.getFileName(), file.getName());
			//生成文件存储的相对路径
			String relativeFilePath = generateRelativeFilePath(newFileName);
			//获取类别目录
			String serverDir = getServerDirByType(attachment.getType());
			//获取真实存储路径:资源服务器根目录 + 类别目录 + 文件相对路径
			String realPath = getResourceServerPath(serverDir, relativeFilePath);
			File newFile = new File(realPath);
			file.renameTo(newFile);
			//设定附件路径为资源服务器上的相对路径
			attachment.setPath(relativeFilePath);
			return true;
		}
		return false;
	}
	
	/**
	 * 删除已上传的附件
	 * @param attachment
	 * 
	 */
	public static void deleteUploadedAttachment(Attachment attachment) {
		if( attachment==null )
			return;
		
		if( attachment.getFile()==null && (attachment.getPath()==null || attachment.getPath().trim().equals("")) )
			return;
		
		//假定当前附件已被传输至资源服务器
		//先删除调用者所在服务器上的临时文件
		File file = attachment.getFile();
		if(file!=null && file.exists() && file.isFile()) {
			file.delete();
		}
		
		//重新装配资源服务器附件路径
		//删除资源服务器上的附件
		String relativeFilePath = null;
		String serverDir = getServerDirByType(attachment.getType());
		if( attachment.getPath()!=null && !attachment.getPath().trim().equals("")) {
			relativeFilePath = attachment.getPath();
		} else {
			String newFileName = generateNewFileName(attachment.getFileName(), file.getName());
			relativeFilePath = generateRelativeFilePath(newFileName);
		}
		
		File serverFile = new File(getResourceServerPath(serverDir, relativeFilePath));
		if(serverFile.exists() && serverFile.isFile()) {
			serverFile.delete();
		}
	}
	
	/**
	 * 根据附件相对路径获取Web访问URL
	 * @param attachment
	 * @return
	 * 
	 */
	public static String getURL(Attachment attachment) {
		return getServerAddressByType(attachment.getType()) + attachment.getPath().replace(File.separator, "/");
	}
	
	/**
	 * 根据附件类型获取Web Server路径
	 * @param attachmentType
	 * @return
	 * 
	 */
	private static String getServerAddressByType(String attachmentType) {
		String serverAddress = null;
		if(attachmentType.equals(Constants.ATTACHMENT_TYPE_IMAGE)) {
			serverAddress = RESOURCE_SERVER_ADDRESS + "/i";
		} else if(attachmentType.equals(Constants.ATTACHMENT_TYPE_DOCUMENT)) {
			serverAddress = RESOURCE_SERVER_ADDRESS + "/d";
		} else {
			serverAddress = RESOURCE_SERVER_ADDRESS + "/o";
		}
		return serverAddress;
	}
	
}
