package com.project.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ListUtils;

/**
 * <h3>Class name</h3> 数组工具类 <h4>Description</h4> <h4>Special Notes</h4>
 * 
 * @author mowei
 */
public class ListUtil extends ListUtils
{

	/**
	 * 判断collection是否为空
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection)
	{
		if ( collection == null || collection.isEmpty() )
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断collection是否非空
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> collection)
	{
		return !isEmpty( collection );
	}

	/**
	 * 判断数组是否为空
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isEmpty(Object[] objects)
	{
		if ( objects == null || objects.length == 0 )
		{
			return true;
		}
		return false;
	}

	/**
	 * 判断数组是否不为空
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isNotEmpty(Object[] objects)
	{
		return !isEmpty( objects );
	}

	/**
	 * 删除list集合中重复值
	 * 
	 */
	public static List removeDuplicateBySet(List source) {
		Set set = new HashSet();
		List distinctResult = new ArrayList();
		for (Iterator iter = source.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				distinctResult.add(element);
		}
		source.clear();
		source.addAll(distinctResult);
		return source;
	}
	
	public static void main(String[] args) {
//		List a = new ArrayList();
//		a.add("11");
//		a.add("11");
//		a.add("121");
//		removeDuplicateBySet(a);
//		for(Iterator it = a.iterator();it.hasNext();){
//			String element = (String) it.next();
//			System.out.println(element);
//		}
	}

}
