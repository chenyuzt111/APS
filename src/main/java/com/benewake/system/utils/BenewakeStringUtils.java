package com.benewake.system.utils;

import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.utils.core.StrFormatter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class BenewakeStringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 空字符串
     */
    private static final String NULLSTR = "";

    /**
     * 下划线
     */
    private static final char SEPARATOR = '_';


    public static String getGenval(String inputString) {
        String patternString = "available (=|>=|<=|LIKE|>|<|NOT LIKE|<>) #\\{ew\\.paramNameValuePairs\\.MPGENVAL(\\d+)}";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(inputString);

        // 处理匹配的字符串
        if (matcher.find()) {
            // 获取匹配的数字
            String genvalNumber = matcher.group(1);
            // 处理MPGENVAL部分
            return "MPGENVAL" + genvalNumber;
        } else {
            return null;
        }
    }

    public static String replaceAvailable(String inputString) {
        String patternString = "available (=|>=|<=|LIKE|>|<|NOT LIKE|<>) #\\{ew\\.paramNameValuePairs\\.MPGENVAL(\\d+)}";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(inputString);
        StringBuffer result = new StringBuffer();
        int count = 0;
        while (matcher.find()) {
            count++;
            String replacementString = "1=1";
            // 替换匹配的字符串
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacementString));
        }
        if (count > 1) {
            throw new BeneWakeException("是否可用筛选条件只能设置一个");
        }
        // 将剩余的部分追加到结果中
        matcher.appendTail(result);
        return result.toString();
    }


    public static String removeAs(String input) {
        if (input.toUpperCase().startsWith("CASE")) {
            return processCaseStatement(input);
        } else {
            // 使用正则表达式将 "AS" 及其后面的部分替换为空字符串
            return input.replaceAll("(?i)\\s+as\\s+.*$", "");
        }
    }

    private static String processCaseStatement(String input) {
        Pattern pattern = Pattern.compile("CASE\\s+WHEN\\s+.*?\\s+END\\s+AS\\s+([\\w_]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "1";
    }

    public static Date parse(String date, String dateFormat) throws ParseException {
        if (StringUtils.isEmpty(date) || StringUtils.isEmpty(dateFormat)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date parse = format.parse(date);
        return parse;
    }

    public static String formatTime(String inputTime) {
        // 切分时间字符串
        String[] parts = inputTime.split(":");

        // 確保有兩個部分，如果只有一個部分就添加 '0' 以前置
        if (parts.length == 2) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return String.format("%02d:%02d", hours, minutes);
        } else {
            return inputTime;  // 如果格式不正确，返回原始字符串
        }
    }


    public static String formatDate(String inputTime) {
        // 切分时间字符串
        String[] parts = inputTime.split("-");

        // 確保有兩個部分，如果只有一個部分就添加 '0' 以前置
        if (parts.length == 2) {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return String.format("%02d-%02d", hours, minutes);
        } else {
            return inputTime;  // 如果格式不正确，返回原始字符串
        }
    }

    /**
     * 获取参数不为空值
     *
     * @param value defaultValue 要判断的value
     * @return value 返回值
     */
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * * 判断一个Collection是否为空， 包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    /**
     * * 判断一个Collection是否非空，包含List，Set，Queue
     *
     * @param coll 要判断的Collection
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * * 判断一个对象数组是否为空
     *
     * @param objects 要判断的对象数组
     *                * @return true：为空 false：非空
     */
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    /**
     * * 判断一个对象数组是否非空
     *
     * @param objects 要判断的对象数组
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * * 判断一个对象是否是数组类型（Java基本型别的数组）
     *
     * @param object 对象
     * @return true：是数组 false：不是数组
     */
    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    /**
     * 去空格
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @return 结果
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return NULLSTR;
        }

        if (start < 0) {
            start = str.length() + start;
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return NULLSTR;
        }

        return str.substring(start);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    public static String toLowerCaseFirstLetter(String className) {
        if (className == null || className.isEmpty()) {
            return className;
        }
        char firstChar = Character.toLowerCase(className.charAt(0));
        return firstChar + className.substring(1);
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    /**
     * 是否为http(s)://开头
     *
     * @param link 链接
     * @return 结果
     */
//    public static boolean ishttp(String link)
//    {
//        return StringUtils.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
//    }

    /**
     * 字符串转set
     *
     * @param str 字符串
     * @param sep 分隔符
     * @return set集合
     */
    public static final Set<String> str2Set(String str, String sep) {
        return new HashSet<String>(str2List(str, sep, true, false));
    }

    /**
     * 字符串转list
     *
     * @param str         字符串
     * @param sep         分隔符
     * @param filterBlank 过滤纯空白
     * @param trim        去掉首尾空白
     * @return list集合
     */
    public static final List<String> str2List(String str, String sep, boolean filterBlank, boolean trim) {
        List<String> list = new ArrayList<String>();
        if (BenewakeStringUtils.isEmpty(str)) {
            return list;
        }

        // 过滤空白字符串
        if (filterBlank && BenewakeStringUtils.isBlank(str)) {
            return list;
        }
        String[] split = str.split(sep);
        for (String string : split) {
            if (filterBlank && BenewakeStringUtils.isBlank(string)) {
                continue;
            }
            if (trim) {
                string = string.trim();
            }
            list.add(string);
        }

        return list;
    }

    /**
     * 判断给定的collection列表中是否包含数组array 判断给定的数组array中是否包含给定的元素value
     *
     * @param collection 给定的集合
     * @param array      给定的数组
     * @return boolean 结果
     */
    public static boolean containsAny(Collection<String> collection, String... array) {
        if (isEmpty(collection) || isEmpty(array)) {
            return false;
        } else {
            for (String str : array) {
                if (collection.contains(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串同时串忽略大小写
     *
     * @param cs                  指定字符串
     * @param searchCharSequences 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     */
    public static boolean containsAnyIgnoreCase(CharSequence cs, CharSequence... searchCharSequences) {
        if (isEmpty(cs) || isEmpty(searchCharSequences)) {
            return false;
        }
        for (CharSequence testStr : searchCharSequences) {
            if (containsIgnoreCase(cs, testStr)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 驼峰转下划线命名
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前置字符是否大写
        boolean preCharIsUpperCase = true;
        // 当前字符是否大写
        boolean curreCharIsUpperCase = true;
        // 下一字符是否大写
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * 驼峰式命名法
     * 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        if (s.indexOf(SEPARATOR) == -1) {
            return s;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
     *
     * @param str  指定字符串
     * @param strs 需要检查的字符串数组
     * @return 是否匹配
     */
    public static boolean matches(String str, List<String> strs) {
        if (isEmpty(str) || isEmpty(strs)) {
            return false;
        }
        for (String pattern : strs) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置:
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     *
     * @param pattern 匹配规则
     * @param url     需要匹配的url
     * @return
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * 数字左边补齐0，使之达到指定长度。注意，如果数字转换为字符串后，长度大于size，则只保留 最后size个字符。
     *
     * @param num  数字对象
     * @param size 字符串指定长度
     * @return 返回数字的字符串格式，该字符串为指定长度。
     */
    public static final String padl(final Number num, final int size) {
        return padl(num.toString(), size, '0');
    }

    /**
     * 字符串左补齐。如果原始字符串s长度大于size，则只保留最后size个字符。
     *
     * @param s    原始字符串
     * @param size 字符串指定长度
     * @param c    用于补齐的字符
     * @return 返回指定长度的字符串，由原字符串左补齐或截取得到。
     */
    public static final String padl(final String s, final int size, final char c) {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null) {
            final int len = s.length();
            if (s.length() <= size) {
                for (int i = size - len; i > 0; i--) {
                    sb.append(c);
                }
                sb.append(s);
            } else {
                return s.substring(len - size, len);
            }
        } else {
            for (int i = size; i > 0; i--) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String findAndIncrementMaxProcessScheme(List<String> processSchemes) {
        String maxScheme = null;
        int maxNumber = Integer.MIN_VALUE;

        for (String processScheme : processSchemes) {
            // Assuming the format is "ProcessScheme-X" where X is a number
            String[] parts = processScheme.split("-");
            if (parts.length == 2) {
                try {
                    int number = Integer.parseInt(parts[1]);
                    if (number > maxNumber) {
                        maxNumber = number;
                        maxScheme = processScheme;
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid format
                }
            }
        }

        if (maxScheme != null) {
            try {
                int incrementedNumber = maxNumber + 1;
                String[] parts = maxScheme.split("-");
                if (parts.length == 2) {
                    return parts[0] + "-" + incrementedNumber;
                }
            } catch (NumberFormatException e) {
                // Handle invalid format
            }
        }

        return maxScheme; // Return original if unable to increment
    }

    public static String incrementAndExtractLast(List<String> processSchemeNames) {
        Integer max = Integer.MIN_VALUE;
        Integer maxLength = 0;
        String maxProcessSchemeName = null;
        for (String processSchemeName : processSchemeNames) {
            if (processSchemeName == null || processSchemeName.isEmpty()) {
                // 处理空字符串或空值情况
                return processSchemeName;
            }
            // 找到字符串中的最后一个数字
            int lastIndex = processSchemeName.length() - 1;
            while (lastIndex >= 0 && Character.isDigit(processSchemeName.charAt(lastIndex))) {
                lastIndex--;
            }

            // 提取数字部分
            String numericPart = processSchemeName.substring(lastIndex + 1);

            if (numericPart.isEmpty()) {
                // 如果没有数字部分，无法执行加一操作
                return processSchemeName;
            }
            int numericValue = Integer.parseInt(numericPart);
            if (max < numericValue) {
                max = numericValue;
                maxLength = lastIndex;
                maxProcessSchemeName = processSchemeName;
            }
        }


        try {
            // 解析数字并加一
            max++;
            // 构建新的字符串，替换原来的数字部分
            String incrementedValue = maxProcessSchemeName.substring(0, maxLength + 1) + max;

            return incrementedValue;
        } catch (NumberFormatException e) {
            // 如果解析数字失败，返回原始字符串
            return null;
        }
    }

}