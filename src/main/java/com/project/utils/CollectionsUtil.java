package com.project.utils;

import com.project.core.utils.reflection.ReflectionUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Collections工具集.
 * <p/>
 * 在JDK的Collections和Guava的Collections2后, 命名为Collections3.
 * <p/>
 * 函数主要由两部分组成，一是自反射提取元素的功能，二是源自Apache Commons Collection, 争取不用在项目里引入它。
 *
 * @author calvin
 */
public class CollectionsUtil {

    /**
     * 提取集合中的对象的两个属性(通过Getter函数), 组合成Map.
     *
     * @param collection        来源集合.
     * @param keyPropertyName   要提取为Map中的Key值的属性名.
     * @param valuePropertyName 要提取为Map中的Value值的属性名.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map extractToMap(final Collection collection, final String keyPropertyName,
                                   final String valuePropertyName) {
        Map map = new HashMap(collection.size());

        try {
            for (Object obj : collection) {
                map.put(PropertyUtils.getProperty(obj, keyPropertyName),
                        PropertyUtils.getProperty(obj, valuePropertyName));
            }
        } catch (Exception e) {
            throw ReflectionUtil.convertReflectionExceptionToUnchecked(e);
        }

        return map;
    }

    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成List.
     *
     * @param collection   来源集合.
     * @param propertyName 要提取的属性名.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List extractToList(final Collection collection, final String propertyName) {
        List list = new ArrayList(collection.size());

        try {
            for (Object obj : collection) {
                list.add(PropertyUtils.getProperty(obj, propertyName));
            }
        } catch (Exception e) {
            throw ReflectionUtil.convertReflectionExceptionToUnchecked(e);
        }

        return list;
    }

    /**
     * 提取集合中的对象的一个属性(通过Getter函数), 组合成由分割符分隔的字符串.
     *
     * @param collection   来源集合.
     * @param propertyName 要提取的属性名.
     * @param separator    分隔符.
     */
    @SuppressWarnings("rawtypes")
    public static String extractToString(final Collection collection, final String propertyName, final String separator) {
        List list = extractToList(collection, propertyName);
        return StringUtils.join(list, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    @SuppressWarnings("rawtypes")
    public static String convertToString(final Collection collection, final String separator) {
        return StringUtils.join(collection, separator);
    }

    /**
     * 转换Collection所有元素(通过toString())为String, 每个元素的前面加入prefix，后面加入postfix，如<div>mymessage</div>。
     */
    @SuppressWarnings("rawtypes")
    public static String convertToString(final Collection collection, final String prefix, final String postfix) {
        StringBuilder builder = new StringBuilder();
        for (Object o : collection) {
            builder.append(prefix).append(o).append(postfix);
        }
        return builder.toString();
    }

    /**
     * 将字符串数组转换成用于数据库查询IN的条件
     * 例如：'1','2','3','4'
     * @param collection
     * @return
     */
    public static String convertToDBString(final List<String> collection) {
        return convertToDBString(collection, ",", "'", "'");
    }

    /**
     * 将字符串数组转换成用于数据库查询IN的条件
     * 例如：'1','2','3','4'
     * @param collection
     * @param separator
     * @param prefix
     * @param postfix
     * @return
     */
    public static String convertToDBString(final List<String> collection, final String separator, final String prefix, final String postfix) {
        StringBuilder builder = new StringBuilder();
        if(isNotEmpty(collection)) {
            int size = collection.size();
            for(int i = 0; i < size; i ++) {
                String element = collection.get(i);
                builder.append(prefix);
                builder.append(element);
                builder.append(postfix);
                if(i < size - 1) {
                    builder.append(separator);
                }
            }
        }
        return builder.toString();
    }

    /**
     * 判断是否为空.
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection collection) {
        return ((collection == null) || collection.isEmpty());
    }

    /**
     * 判断是否为空.
     */
    @SuppressWarnings("rawtypes")
    public static boolean isNotEmpty(Collection collection) {
        return ((collection != null) && !(collection.isEmpty()));
    }

    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        return collection.iterator().next();
    }

    /**
     * 获取Collection的最后一个元素 ，如果collection为空返回null.
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        // 当类型为List时，直接取得最后一个元素 。
        if (collection instanceof List) {
            List<T> list = (List<T>) collection;
            return list.get(list.size() - 1);
        }

        // 其他类型通过iterator滚动到最后一个元素.
        Iterator<T> iterator = collection.iterator();
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }

    /**
     * 返回a+b的新List.
     */
    public static <T> List<T> union(final Collection<T> a, final Collection<T> b) {
        List<T> result = new ArrayList<T>(a);
        result.addAll(b);
        return result;
    }

    /**
     * 返回a-b的新List.
     */
    public static <T> List<T> subtract(final Collection<T> a, final Collection<T> b) {
        List<T> list = new ArrayList<T>(a);
        for (T element : b) {
            list.remove(element);
        }

        return list;
    }

    /**
     * 返回a与b的交集的新List.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        List<T> list = new ArrayList<T>();

        for (T element : a) {
            if (b.contains(element)) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * 将指定集合分页
     *
     * @param list     需要分页的集合对象
     * @param pageSize 每页的大小
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int pageSize) {
        // list的大小
        int listSize = list.size();
        // 页数
        int page = (listSize + (pageSize - 1)) / pageSize;
        // 创建list数组 ,用来保存分割后的list
        List<List<T>> listArray = new ArrayList<List<T>>();
        // 按照数组大小遍历
        for (int i = 0; i < page; i++) {
            // 数组每一位放入一个分割后的list
            List<T> subList = new ArrayList<T>();
            // 遍历待分割的list
            for (int j = 0; j < listSize; j++) {
                // 当前记录的页码(第几页)
                int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize;
                // 当前记录的页码等于要放入的页码时
                if (pageIndex == (i + 1)) {
                    //放入list中的元素到分割后的list(subList)
                    subList.add(list.get(j));
                }
                // 当放满一页时退出当前循环
                if ((j + 1) == ((j + 1) * pageSize)) {
                    break;
                }
            }
            // 将分割后的list放入对应的数组的位中
            listArray.add(subList);
        }
        return listArray;
    }

}
