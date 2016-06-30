package com.project.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * SHA256加密
     *
     * @param input 要加密的字符串
     * @return
     */

    public static String encryptBySHA(String input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes());

            byte byteData[] = md.digest();

            // 二进制转换为十六进制
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 替换s到e的字符串
     *
     * @param s       开始字符
     * @param e       结束字符
     * @param content 要截取的字符
     * @param value   替换内容
     * @return
     */

    public static String replaceInValue(String s, String e, String content, String value) {
        if (content == null) {
            return null;
        }
        int i = 0;
        int k = 0;
        if ((i = (content.indexOf(s, i))) >= 0
                && (k = (content.indexOf(e, k))) >= 0) {
            StringBuffer sbf = new StringBuffer();
            int s_length = s.length();
            int e_length = e.length();
            sbf.append(content.substring(0, i)).append(value);
            int n = i + s_length;
            int m = k + e_length;
            for (; ((i = (content.indexOf(s, n))) >= 0 &&
                    (k = (content.indexOf(e, m))) >= 0); ) {
                sbf.append(content.substring(m, i))
                        .append(value);
                n = i + s_length;
                m = k + e_length;
            }
            sbf.append(content.substring(m, content.length()));
            return sbf.toString();
        }
        return content;
    }

    /**
     * 截取字符串前面一顶长度的字符
     *
     * @param length  要截取长度
     * @param content 内容
     * @return
     */
    public static String cutString(int length, String content) {
        if (content.length() > length) {
            return content.substring(0, length);
        }
        return content;
    }

    /**
     * 截取字符串前面一顶长度的字符,同时增加"..."结尾
     * commons
     *
     * @param length
     * @param content
     * @return String
     */
    public static String cutSubString(int length, String content) {
        if (content.length() > length) {
            return content.substring(0, length) + "...";
        }
        return content;
    }


    /**
     * 解码
     *
     * @param src
     * @return
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /**
     * 让普通的string支持类似EL标签的用法 <br>
     * <code>
     * Map<String, Object> map = new HashMap<String, Object>();<br><br>
     * <p/>
     * Map<String, Object> test = new HashMap<String, Object>();<br>
     * test.put("a", "I'm a！");<br>
     * map.put("test", test);<br><br>
     * <p/>
     * Map<String, Object> test2 = new HashMap<String, Object>();<br>
     * test2.put("b", "I'm b！");	<br>
     * map.put("test2", test2);<br>
     * <p/>
     * String str = "${map.test[a]}";<br>
     * System.out.println(replaceKeys(map, str));<br><br>
     * <p/>
     * String str2 = "${map.test[a]}，还有一个，${map.test2[b]}";<br>
     * System.out.println(replaceKeys(map, str2));<br>
     * <p/>
     * </code>
     *
     * @param map
     * @param key
     * @return
     * @author Jay.Wu
     */
    @SuppressWarnings("unchecked")
    public static String replaceKeys(Map<String, Object> paraMap, String destStr) {
        if (paraMap == null)
            return destStr;

        String variableTypeMap = "map";

        for (String key : paraMap.keySet()) {
            String regex = "\\$\\{" + variableTypeMap + "\\." + key + "\\[(.+?)\\]\\}";
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(destStr);

            while (m.find()) {
                try {
                    String outStr = m.group(1);
                    Map<String, Object> targetMap = (Map<String, Object>) paraMap.get(key);
                    Object targetValue = targetMap.get(outStr);

                    if (targetValue != null) {
                        destStr = destStr.replace("${" + variableTypeMap + "." + key + "[" + outStr + "]}", targetValue.toString());
                    } else {
                        destStr = "";
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

        return destStr;
    }

    /**
     * 让普通的string支持类似EL标签的用法-简单版本 <br>
     * <code>${contents}</code>
     *
     * @param paraMap
     * @param destStr
     * @return
     * @author Jay.Wu
     */
    public static String replaceKeysSimple(Map<String, Object> paraMap, String destStr) {
        if (paraMap == null) {
            return destStr;
        }

        for (String key : paraMap.keySet()) {
            String value = paraMap.get(key) == null ? "" : (String) paraMap.get(key);

            key = "${" + key + "}";
            destStr = destStr.replace(key, value);
        }

        return destStr;
    }

    /**
     * @param destStr
     * @return
     * @description 【获取字符串中大写字母的位置】
     * @author zhangyun
     */
    public static int indexOf(String destStr) {
        int index = -1;
        if (StringUtils.isEmpty(destStr)) {
            return index;
        }
        char[] destChars = destStr.toCharArray();
        char c;
        for (int i = 0; i < destChars.length; i++) {
            c = destChars[i];
            if (Character.isUpperCase(c)) {
                index = i;
            }
        }
        return index;
    }

    /**
     * @param destStr
     * @return
     * @description 【实现abc_def ->abcDef格式的转换】
     * @author zhangyun
     */
    public static String toCenterUpperCase(String destStr) {
        String attribute = destStr.toLowerCase().trim();
        int index = -1;
        while ((index = attribute.indexOf("_")) != -1) {
            String str = attribute.substring(index + 1).trim();
            if ("".equals(str.trim())) {
                attribute = attribute.substring(0, index).trim();
                break;
            }
            attribute = attribute.substring(0, index).trim() + str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return attribute;
    }

    /**
     * 判断字符串为空
     *
     * @param str
     * @return
     */
    public static boolean isNotNull(String str) {
        if (str == null || str == "") {
            return false;
        } else {
            if (str.equals("") || str.equals("null"))
                return false;
        }
        return true;
    }

    /**
     * java截取中英文混合字符串 等宽显示
     *
     * @param text
     * @param length
     * @param endWith
     * @return
     */
    public static String subString(String text, int length, String endWith) {
        if (text != null) {
            int textLength = text.length();
            int byteLength = 0;
            StringBuffer returnStr = new StringBuffer();
            for (int i = 0; i < textLength && byteLength < length * 2; i++) {
                String str_i = text.substring(i, i + 1);
                if (str_i.getBytes().length == 1) {//英文
                    byteLength++;
                } else {//中文
                    byteLength += 2;
                }
                returnStr.append(str_i);
            }
            try {
                if (byteLength < text.getBytes("GBK").length && endWith != null) {//getBytes("GBK")每个汉字长2，getBytes("UTF-8")每个汉字长度为3
                    returnStr.append(endWith);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return returnStr.toString();
        } else {
            return "";
        }
    }

    /**
     * list拼接字符串，以、隔开
     */
    public static String list2String(List<String> list) {
        if (CollectionsUtil.isNotEmpty(list)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Object s : list) {
                stringBuilder.append(s);
                stringBuilder.append("、");
            }
            String channelName = stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("、")).toString();
            return channelName;
        }
        return null;
    }
}

