package com.project.utils.encode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.exception.ServiceException;

/**
 * 实现XML to Java Object
 * @author mowei
 * 
 */
public class BetWixtHelper {

	private static Logger logger = LoggerFactory.getLogger(BetWixtHelper.class);

	public static String o2Xml(Object object) {
		if (object == null) {
			return "";
		}
		String result = "";
		try {
			Writer outputWriter = new StringWriter();
			BeanWriter beanWriter = new BeanWriter(outputWriter);
			// 元素第一个字母大写
//			beanWriter.getXMLIntrospector().getConfiguration().setElementNameMapper(new CapitalizeNameMapper());
			// 是否属性显示
			beanWriter.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
			// 是否添加Map ID
			beanWriter.getBindingConfiguration().setMapIDs(false);
			// 格式化打印
			beanWriter.enablePrettyPrint();
			// 是否显示空节点结束标签
			beanWriter.setEndTagForEmptyElement(true);
			// 是否显示空元素
			beanWriter.setWriteEmptyElements(true);
			beanWriter.writeXmlDeclaration("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			beanWriter.write(getClassName(object.getClass().getName()), object);
			result = outputWriter.toString();
			outputWriter.close();
		} catch (Exception e) {
			logger.info("解析错误", e);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Object xml2O(String xml, Class targat) throws ServiceException {
		if (xml == null || targat == null) {
			return "";
		}
		Object result = null;
		try {
			Reader xmlReader = new StringReader(xml);
			BeanReader beanReader = new BeanReader();
			//xml中各节点元素第一个字母是否大写
//			beanReader.getXMLIntrospector().getConfiguration().setElementNameMapper(new CapitalizeNameMapper());
			beanReader.getBindingConfiguration().setMapIDs(false);
			beanReader.registerBeanClass(getClassName(targat.getName()), targat);
			result = beanReader.parse(xmlReader);
			xmlReader.close();
		} catch (Exception e) {
			throw new ServiceException("转换出错: " + e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static Object xmlFile2O(String fileName, Class targat) throws ServiceException {
		InputStream in = null;
		try {
			in = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			throw new ServiceException("文件未找到：" + fileName);
		}
		Object result = null;
		try {
			BeanReader beanReader = new BeanReader();
			beanReader.getXMLIntrospector().getConfiguration()
					.setAttributesForPrimitives(false);
			beanReader.getBindingConfiguration().setMapIDs(false);
			beanReader
					.registerBeanClass(getClassName(targat.getName()), targat);
			result = beanReader.parse(in);
		} catch (Exception e) {
			throw new ServiceException(fileName +"");
		}
		return result;
	}

	public static void o2XmlFile(Object object, String fileName, String charSet) throws ServiceException {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(fileName, charSet);
		} catch (FileNotFoundException e) {
			throw new ServiceException("文件未找到:  " + fileName);
		} catch (UnsupportedEncodingException e) {
			throw new ServiceException("编码错误：" + charSet);
		}
		String xml = o2Xml(object);
		pw.print(xml);
		pw.close();
	}

	/**
	 * @param wholeName
	 * @return
	 */
	private static String getClassName(String wholeName) {
		int lastIndex = wholeName.lastIndexOf(".");
		String className = wholeName.substring(lastIndex + 1);
		String name = className.substring(0, 1).toLowerCase()
				+ className.substring(1);
		return name;
	}
	
	public static void main(String args[]){
		
	}
}
