package io.github.youhong.minispring.utils;

/**
 * <p>
 * 字符串工具类，提供常用的字符串静态处理方法。
 * </p>
 * <p>
 * 该类为纯工具类，禁止实例化。所有方法均为静态方法，
 * 专注于轻量级字符串转换操作，避免引入第三方依赖。
 * </p>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/15 17:38
 */
public class StringUtils {
    private StringUtils() {
        throw new AssertionError("No StringUtils instances for you!");
    }


    /**
     * 将字符串的首字母转换为小写形式。
     *
     * @param str 待转换的字符串，可为 {@code null}
     * @return 首字母转为小写后的新字符串；若入参为 {@code null} 或空字符串，则原样返回
     */
    public static String lowerFirst(String str) {
        // 空值保护，null 或空字符串直接返回
        if (str == null || str.isEmpty()) {
            return str;
        }
        char[] charArray = str.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }


}
