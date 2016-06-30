package com.project.utils.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.project.bean.misc.BASE64Decoder;
import com.project.utils.ResourceBundleUtil;


/**
 * 支持断点续传的FTP上传/下载/删除文件类
 * @author momo
 * @version 1.0 实现上传下载的断点续传
 * @version 2.0 实现上传下载的进度汇报
 */
public class FTPUtil {
	
	private static final String ftpHostName = ResourceBundleUtil.getString("ftp.hostname");
	private static final int ftpPort = Integer.parseInt(ResourceBundleUtil.getString("ftp.port"));
	private static final String ftpUsername = ResourceBundleUtil.getString("ftp.username");
	private static final String ftpPassword = ResourceBundleUtil.getString("ftp.password");
	private static FTPClient ftpClient = new FTPClient();
	
//	static{
//		//设置将过程中使用到的命令输出到控制台
//		ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
//	}
	
	/**
	 * 连接到FTP服务器
	 * @param hostname 主机名
	 * @param port 端口
	 * @param username 用户名
	 * @param password 密码
	 * @return 是否连接成功
	 */
	private static boolean connect(String hostname,int port,String username,String password) {
		try {
			ftpClient.connect(hostname, port);
			ftpClient.setControlEncoding("GBK");
			if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
				if(ftpClient.login(username, password)){
					return true;
				}
			}
			disconnect();
		} catch (IOException e) {
		}
		return false;
	}
	
	/**
	 * 断开与远程ftp服务器的连接
	 * @throws IOException
	 */
	private static void disconnect() {
		try {
			if(ftpClient.isConnected()){
				ftpClient.logout();
				ftpClient.disconnect();
			}
		} catch (IOException e) {
		}
	}
	
	/**
	 * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
	 * @param remote 远程文件路径
	 * @param local 本地文件路径
	 * @return 上传的状态
	 */
	public static DownloadStatus download(String remote,String local) {
		DownloadStatus result = null;
		if(connect(ftpHostName, ftpPort, ftpUsername, ftpPassword)){
			try {
				//设置被动模式
				ftpClient.enterLocalPassiveMode();
				//设置以二进制方式传输
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				
				//检查远程文件是否存在
				FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("GBK"),"iso-8859-1"));
				if(files.length != 1){
					System.out.println("远程文件不存在");
					return DownloadStatus.Remote_File_Noexist;
				}
				
				long lRemoteSize = files[0].getSize();
				File f = new File(local);
				//本地存在文件，进行断点下载
				if(f.exists()){
					long localSize = f.length();
					//判断本地文件大小是否大于远程文件大小
					if(localSize >= lRemoteSize){
						System.out.println("本地文件大于远程文件，下载中止");
						return DownloadStatus.Local_Bigger_Remote;
					}
					
					//进行断点续传，并记录状态
					FileOutputStream out = new FileOutputStream(f,true);
					ftpClient.setRestartOffset(localSize);
					InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"),"iso-8859-1"));
					byte[] bytes = new byte[4096];
					long step = lRemoteSize /100;
					long process=localSize /step;
					int c;
					while((c = in.read(bytes))!= -1){
						out.write(bytes,0,c);
						localSize+=c;
						long nowProcess = localSize /step;
						if(nowProcess > process){
							process = nowProcess;
							if(process % 10 == 0)
								System.out.println("下载进度："+process);
							// 更新文件下载进度,值存放在process变量中
						}
					}
					in.close();
					out.close();
					boolean isDo = ftpClient.completePendingCommand();
					if(isDo){
						result = DownloadStatus.Download_From_Break_Success;
					}else {
						result = DownloadStatus.Download_From_Break_Failed;
					}
				}else {
					OutputStream out = new FileOutputStream(f);
					InputStream in= ftpClient.retrieveFileStream(new String(remote.getBytes("GBK"),"iso-8859-1"));
					byte[] bytes = new byte[4096];
					long step = lRemoteSize /100;
					long process=0;
					long localSize = 0L;
					int c;
					while((c = in.read(bytes))!= -1){
						out.write(bytes, 0, c);
						localSize+=c;
						long nowProcess = localSize /step;
						if(nowProcess > process){
							process = nowProcess;
							if(process % 10 == 0)
								System.out.println("下载进度："+process);
							// 更新文件下载进度,值存放在process变量中
						}
					}
					in.close();
					out.close();
					boolean upNewStatus = ftpClient.completePendingCommand();
					if(upNewStatus){
						result = DownloadStatus.Download_New_Success;
					}else {
						result = DownloadStatus.Download_New_Failed;
					}
				}
			} catch (IOException e) {
			}
		}
		return result;
	}
	
	/**
	 * 上传文件到FTP服务器，支持断点续传
	 * @param local 本地文件名称，绝对路径
	 * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
	 * @param isResumeBrokenTransfer 是否断点续传（否：如果服务器存在此文件，先删除后上传；是：断点续传）
	 * @return 上传结果
	 */
	public static UploadStatus upload(String local,String remote, boolean isResumeBrokenTransfer) {
		return upload(new File(local), remote, isResumeBrokenTransfer);
	}
	
	/**
	 * 上传文件到FTP服务器，支持断点续传
	 * @param localFile 本地文件对象
	 * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
	 * @param isResumeBrokenTransfer 是否断点续传（否：如果服务器存在此文件，先删除后上传；是：断点续传）
	 * @return 上传结果
	 */
	public static UploadStatus upload(File localFile, String remote, boolean isResumeBrokenTransfer) {
		UploadStatus result = null;
		if(connect(ftpHostName, ftpPort, ftpUsername, ftpPassword)){
			try {
				//设置PassiveMode传输
				ftpClient.enterLocalPassiveMode();
				//设置以二进制流的方式传输
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.setControlEncoding("GBK");
				//对远程目录的处理
				String remoteFileName = remote;
				if(remote.contains("/")){
					remoteFileName = remote.substring(remote.lastIndexOf("/")+1);
					//创建服务器远程目录结构，创建失败直接返回
					if(createDirecroty(remote, ftpClient)==UploadStatus.Create_Directory_Fail){
						return UploadStatus.Create_Directory_Fail;
					}
				}
				
				//检查远程是否存在文件
				FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));
				if(files.length == 1){
					if(isResumeBrokenTransfer){
						long remoteSize = files[0].getSize();
						long localSize = localFile.length();
						if(remoteSize==localSize){
							return UploadStatus.File_Exits;
						}else if(remoteSize > localSize){
							return UploadStatus.Remote_Bigger_Local;
						}
						
						//尝试移动文件内读取指针,实现断点续传
						result = uploadFile(remoteFileName, localFile, ftpClient, remoteSize);
						
						//如果断点续传没有成功，则删除服务器上文件，重新上传
						if(result == UploadStatus.Upload_From_Break_Failed){
							if(!ftpClient.deleteFile(remoteFileName)){
								return UploadStatus.Delete_Remote_Faild;
							}
							result = uploadFile(remoteFileName, localFile, ftpClient, 0);
						}
					}else{
						if(!ftpClient.deleteFile(remoteFileName)){
							return UploadStatus.Delete_Remote_Faild;
						}
						result = uploadFile(remoteFileName, localFile, ftpClient, 0);
					}
				}else {
					result = uploadFile(remoteFileName, localFile, ftpClient, 0);
				}
			} catch (IOException e) {
			}
		}
		return result;
	}
	
	/**
	 * 递归创建远程服务器目录
	 * @param remote 远程服务器文件绝对路径
	 * @param ftpClient FTPClient对象
	 * @return 目录创建是否成功
	 */
	private static UploadStatus createDirecroty(String remote,FTPClient ftpClient) {
		UploadStatus status = UploadStatus.Create_Directory_Success;
		try{
			String directory = remote.substring(0,remote.lastIndexOf("/")+1);
			if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"iso-8859-1"))){
				//如果远程目录不存在，则递归创建远程服务器目录
				int start=0;
				int end = 0;
				if(directory.startsWith("/")){
					start = 1;
				}else{
					start = 0;
				}
				end = directory.indexOf("/",start);
				while(true){
					String subDirectory = new String(remote.substring(start,end).getBytes("GBK"),"iso-8859-1");
					if(!ftpClient.changeWorkingDirectory(subDirectory)){
						if(ftpClient.makeDirectory(subDirectory)){
							ftpClient.changeWorkingDirectory(subDirectory);
						}else {
							System.out.println("创建目录失败");
							return UploadStatus.Create_Directory_Fail;
						}
					}
					
					start = end + 1;
					end = directory.indexOf("/",start);
					
					//检查所有目录是否创建完毕
					if(end <= start){
						break;
					}
				}
			}
		} catch (IOException e) {
		}
		return status;
	}
	
	/**
	 * 上传文件到服务器,新上传和断点续传
	 * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变
	 * @param localFile 本地文件File句柄，绝对路径
	 * @param processStep 需要显示的处理进度步进值
	 * @param ftpClient FTPClient引用
	 * @return
	 */
	private static UploadStatus uploadFile(String remoteFile,File localFile,FTPClient ftpClient,long remoteSize) {
		UploadStatus status = null;
		try{
			//显示进度的上传
			long step = localFile.length() / 100;
			long process = 0;
			long localreadbytes = 0L;
			RandomAccessFile raf = new RandomAccessFile(localFile,"r");
			OutputStream out = ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"),"iso-8859-1"));
			//断点续传
			if(remoteSize>0){
				ftpClient.setRestartOffset(remoteSize);
				process = remoteSize /step;
				raf.seek(remoteSize);
				localreadbytes = remoteSize;
			}
			byte[] bytes = new byte[4096];
			int c;
			while((c = raf.read(bytes))!= -1){
				out.write(bytes,0,c);
				localreadbytes+=c;
				if(localreadbytes / step != process){
					process = localreadbytes / step;
					System.out.println("上传进度:" + process);
					// 汇报上传状态
				}
			}
			out.flush();
			raf.close();
			out.close();
			boolean result = ftpClient.completePendingCommand();
			if(remoteSize > 0){
				status = result?UploadStatus.Upload_From_Break_Success:UploadStatus.Upload_From_Break_Failed;
			}else {
				status = result?UploadStatus.Upload_New_File_Success:UploadStatus.Upload_New_File_Failed;
			}
		} catch (IOException e) {
		}
		return status;
	}
	
	/**
	 * 上传64位编码的图片文件到FTP服务器
	 * @param imgCode 64位编码的图片文件
	 * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
	 * @return 上传结果
	 */
	public static UploadStatus uploadImgBase64Code(String imgCode,String remote) {
		UploadStatus result = null;
		if(connect(ftpHostName, ftpPort, ftpUsername, ftpPassword)){
			try {
				//设置PassiveMode传输
				ftpClient.enterLocalPassiveMode();
				//设置以二进制流的方式传输
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.setControlEncoding("GBK");
				//对远程目录的处理
				String remoteFileName = remote;
				if(remote.contains("/")){
					remoteFileName = remote.substring(remote.lastIndexOf("/")+1);
					//创建服务器远程目录结构，创建失败直接返回
					if(createDirecroty(remote, ftpClient)==UploadStatus.Create_Directory_Fail){
						return UploadStatus.Create_Directory_Fail;
					}
				}
				
				//检查远程是否存在文件，如果存在则先删除后上传
				FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));
				if(files.length == 1){
					if(!ftpClient.deleteFile(remoteFileName)){
						return UploadStatus.Delete_Remote_Faild;
					}
				}
				
				BASE64Decoder decoder = new BASE64Decoder();
				try {
					// Base64解码
					byte[] b = decoder.decodeBuffer(imgCode);
					for (int i = 0; i < b.length; ++i) {
						if (b[i] < 0) {// 调整异常数据
							b[i] += 256;
						}
					}
					// 生成图片
					OutputStream out = ftpClient.appendFileStream(new String(remoteFileName.getBytes("GBK"),"iso-8859-1"));
					out.write(b);
					out.flush();
					out.close();
					result = ftpClient.completePendingCommand()?UploadStatus.Upload_New_File_Success:UploadStatus.Upload_New_File_Failed;
				} catch (Exception e) {
				}
			} catch (IOException e) {
			}
		}
		return result;
	}
	
	/** 
     *  
     * 删除ftp服务器上的文件
     * @param srcFname 
     * @return true || false 
     */  
    public static boolean removeFile(String remoteFile){  
        boolean flag = false;  
        if(connect(ftpHostName, ftpPort, ftpUsername, ftpPassword)){
	        if( ftpClient!=null ){  
	            try {  
	                flag = ftpClient.deleteFile(remoteFile);  
	            } catch (IOException e) {  
	                disconnect();  
	            }  
	        } 
        }
        return flag;  
    }  
	
	public static void main(String[] args) {
//		System.out.println(FTPUtil.connect("192.168.1.159", 21, "ftpuser", "123456"));
		System.out.println(FTPUtil.upload("E:\\apache-tomcat-7.0.42\\webapps\\fqms\\webapp\\images\\image\\20140903\\20140903170920_306.jpg", "/news/1.jpg", false));
//		System.out.println(FTPUtil.download("/1.jpg", "F:\\2.jpg"));
//		System.out.println(FTPUtil.removeFile("/1.jpg"));
	}
}