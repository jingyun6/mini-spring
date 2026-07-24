package io.github.youhong.minispring.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 字符串工具类——提供与 Apache Commons Lang {@code StringUtils} 对等的常用静态方法。
 *
 * <p>该类为纯工具类，禁止实例化。所有方法均为静态方法，专注于轻量级字符串操作，
 * 避免引入第三方依赖。
 *
 * <p><b>空值约定：</b>
 * <ul>
 *     <li>所有方法均对输入 {@code null} 进行安全处理，不会抛出 {@link NullPointerException}</li>
 *     <li>方法名中 {@code Empty} 表示 {@code null} 或 {@code ""}（{@link String#isEmpty()}）</li>
 *     <li>方法名中 {@code Blank} 表示 {@code null} 或 {@link String#isBlank()}（仅含空白字符）</li>
 * </ul>
 *
 * @author YouHong5286
 * @version 2.0.0
 * @since 2026/7/15 17:38
 */
public class StringUtils {

    /** 空字符串常量 {@code ""} */
    public static final String EMPTY = "";

    /** 空格字符 {@code ' '} */
    public static final char SPACE_CHAR = ' ';

    /** 换行符——Unix 风格 {@code "\n"} */
    public static final String LF = "\n";

    /** 换行符——Windows 风格 {@code "\r\n"} */
    public static final String CRLF = "\r\n";

    /** 未找到索引时的返回值 {@code -1} */
    public static final int INDEX_NOT_FOUND = -1;

    private StringUtils() {
        throw new AssertionError("No StringUtils instances for you!");
    }

    // ========================= 判空 =========================

    /**
     * 判断字符串是否为 {@code null} 或空字符串（长度为 0）。
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 若字符串为 {@code null} 或空字符串则返回 {@code true}
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为 {@code null} 且非空字符串。
     *
     * <p>与 {@link #isEmpty(String)} 逻辑相反。</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 若字符串不为 {@code null} 且非空则返回 {@code true}
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为 {@code null}、空字符串或仅包含空白字符。
     *
     * <p>空白字符的定义参见 {@link Character#isWhitespace(char)}。</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 若字符串为 {@code null}、空或仅含空白字符则返回 {@code true}
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * 判断字符串是否不为 {@code null}、非空且包含非空白字符。
     *
     * <p>与 {@link #isBlank(String)} 逻辑相反。</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 若字符串有实际内容则返回 {@code true}
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    // ========================= 默认值 =========================

    /**
     * 若字符串为 {@code null}，则返回空字符串 {@code ""}。
     *
     * <pre>
     * StringUtils.defaultString(null)  = ""
     * StringUtils.defaultString("")    = ""
     * StringUtils.defaultString("bat") = "bat"
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 字符串本身或空字符串
     */
    public static String defaultString(String str) {
        return str == null ? EMPTY : str;
    }

    /**
     * 若字符串为 {@code null}，则返回指定的默认值。
     *
     * <pre>
     * StringUtils.defaultString(null, "NULL")   = "NULL"
     * StringUtils.defaultString("", "NULL")     = ""
     * StringUtils.defaultString("bat", "NULL")  = "bat"
     * </pre>
     *
     * @param str        待检查的字符串，可为 {@code null}
     * @param defaultStr 字符串为 {@code null} 时返回的默认值
     * @return 字符串本身或默认值
     */
    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 若字符串为 {@code null} 或空，则返回指定的默认值。
     *
     * <pre>
     * StringUtils.defaultIfEmpty(null, "NULL")   = "NULL"
     * StringUtils.defaultIfEmpty("", "NULL")     = "NULL"
     * StringUtils.defaultIfEmpty("bat", "NULL")  = "bat"
     * </pre>
     *
     * @param str        待检查的字符串，可为 {@code null}
     * @param defaultStr 字符串为空时返回的默认值
     * @return 字符串本身（非空时）或默认值
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 若字符串为 {@code null}、空或仅含空白字符，则返回指定的默认值。
     *
     * <pre>
     * StringUtils.defaultIfBlank(null, "NULL")   = "NULL"
     * StringUtils.defaultIfBlank("", "NULL")     = "NULL"
     * StringUtils.defaultIfBlank(" ", "NULL")    = "NULL"
     * StringUtils.defaultIfBlank("bat", "NULL")  = "bat"
     * </pre>
     *
     * @param str        待检查的字符串，可为 {@code null}
     * @param defaultStr 字符串为空白时返回的默认值
     * @return 字符串本身（有实际内容时）或默认值
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    // ========================= 大小写转换 =========================

    /**
     * 将字符串的首字母转换为小写形式。
     *
     * <pre>
     * StringUtils.lowerFirst(null)      = null
     * StringUtils.lowerFirst("")        = ""
     * StringUtils.lowerFirst("Bob")     = "bob"
     * StringUtils.lowerFirst("BOB")     = "bOB"
     * </pre>
     *
     * @param str 待转换的字符串，可为 {@code null}
     * @return 首字母转为小写后的新字符串；若入参为 {@code null} 或空字符串，则原样返回
     */
    public static String lowerFirst(String str) {
        if (isEmpty(str)) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * 将字符串的首字母转换为大写形式。
     *
     * <pre>
     * StringUtils.upperFirst(null)      = null
     * StringUtils.upperFirst("")        = ""
     * StringUtils.upperFirst("bob")     = "Bob"
     * StringUtils.upperFirst("Bob")     = "Bob"
     * </pre>
     *
     * @param str 待转换的字符串，可为 {@code null}
     * @return 首字母转为大写后的新字符串；若入参为 {@code null} 或空字符串，则原样返回
     */
    public static String upperFirst(String str) {
        if (isEmpty(str)) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * 将字符串转为全小写。
     *
     * <pre>
     * StringUtils.lowerCase(null)  = null
     * StringUtils.lowerCase("")    = ""
     * StringUtils.lowerCase("Bob") = "bob"
     * </pre>
     *
     * @param str 待转换的字符串，可为 {@code null}
     * @return 全小写字符串；若入参为 {@code null} 则返回 {@code null}
     */
    public static String lowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    /**
     * 将字符串转为全大写。
     *
     * <pre>
     * StringUtils.upperCase(null)  = null
     * StringUtils.upperCase("")    = ""
     * StringUtils.upperCase("Bob") = "BOB"
     * </pre>
     *
     * @param str 待转换的字符串，可为 {@code null}
     * @return 全大写字符串；若入参为 {@code null} 则返回 {@code null}
     */
    public static String upperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    /**
     * 交换字符串中每个字符的大小写。
     *
     * <pre>
     * StringUtils.swapCase(null)          = null
     * StringUtils.swapCase("")            = ""
     * StringUtils.swapCase("The dog")     = "tHE DOG"
     * StringUtils.swapCase("aBcDeF")      = "AbCdEf"
     * </pre>
     *
     * @param str 待转换的字符串，可为 {@code null}
     * @return 大小写互换后的新字符串；若入参为 {@code null} 则返回 {@code null}
     */
    public static String swapCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int len = str.length();
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                chars[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                chars[i] = Character.toUpperCase(ch);
            } else {
                chars[i] = ch;
            }
        }
        return new String(chars);
    }

    // ========================= 截取 =========================

    /**
     * 安全地获取子字符串，支持负索引（从末尾倒数）。
     *
     * <p>负索引表示从字符串末尾向前偏移，例如 {@code -1} 表示最后一个字符。
     * 超出边界的索引会被自动修正到字符串范围内。</p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", *, *)      = ""
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, -1)  = "b"
     * StringUtils.substring("abc", -3, -1) = "ab"
     * </pre>
     *
     * @param str   源字符串，可为 {@code null}
     * @param start 起始索引（含），支持负数
     * @param end   结束索引（不含），支持负数；为 {@code 0} 时返回空字符串
     * @return 截取后的子字符串
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        int len = str.length();

        // 修正负索引
        if (start < 0) {
            start += len;
        }
        if (end < 0) {
            end += len;
        }

        // 边界修正
        start = Math.max(start, 0);
        end = Math.min(end, len);

        if (start >= end) {
            return EMPTY;
        }
        return str.substring(start, end);
    }

    /**
     * 截取从指定位置到字符串末尾的子字符串。
     *
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", -2) = "bc"
     * </pre>
     *
     * @param str   源字符串，可为 {@code null}
     * @param start 起始索引（含），支持负数
     * @return 从起始位置到末尾的子字符串
     */
    public static String substring(String str, int start) {
        return substring(str, start, str == null ? 0 : str.length());
    }

    /**
     * 从字符串左侧截取指定长度的子字符串。
     *
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @param len 期望截取的长度，不允许为负数
     * @return 左侧子字符串
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        return str.substring(0, Math.min(len, str.length()));
    }

    /**
     * 从字符串右侧截取指定长度的子字符串。
     *
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right("", *)      = ""
     * StringUtils.right("abc", 0)   = ""
     * StringUtils.right("abc", 2)   = "bc"
     * StringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @param len 期望截取的长度，不允许为负数
     * @return 右侧子字符串
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        return str.substring(Math.max(0, str.length() - len));
    }

    /**
     * 从字符串中间截取指定长度的子字符串。
     *
     * <pre>
     * StringUtils.mid(null, *, *)    = null
     * StringUtils.mid("", *, *)      = ""
     * StringUtils.mid("abc", 0, 2)   = "ab"
     * StringUtils.mid("abc", 0, 4)   = "abc"
     * StringUtils.mid("abc", 2, 4)   = "c"
     * StringUtils.mid("abc", 4, 2)   = ""
     * StringUtils.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @param pos 起始位置，支持负数（自动修正为 0）
     * @param len 期望截取的长度
     * @return 中间子字符串
     */
    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos >= str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        return str.substring(pos, Math.min(pos + len, str.length()));
    }

    // ========================= 截断 / 省略 =========================

    /**
     * 将字符串截断到指定最大长度，超出部分用 {@code "..."} 替代。
     *
     * <p>返回的字符串总长度（包括省略号）不超过 {@code maxWidth}。</p>
     *
     * <pre>
     * StringUtils.abbreviate(null, *)      = null
     * StringUtils.abbreviate("", 4)        = ""
     * StringUtils.abbreviate("abcdefg", 6) = "abc..."
     * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str     源字符串，可为 {@code null}
     * @param maxWidth 最大宽度，必须大于等于 4
     * @return 截断后的字符串
     * @throws IllegalArgumentException 如果 {@code maxWidth} 小于 4
     */
    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, "...", maxWidth);
    }

    /**
     * 将字符串截断到指定最大长度，超出部分用指定省略符替代。
     *
     * <pre>
     * StringUtils.abbreviate(null, ".", *)      = null
     * StringUtils.abbreviate("", ".", 4)        = ""
     * StringUtils.abbreviate("abcdefg", ".", 6) = "abcd."
     * StringUtils.abbreviate("abcdefg", ".", 7) = "abcdefg"
     * </pre>
     *
     * @param str         源字符串，可为 {@code null}
     * @param abbrevEllipsis 省略符
     * @param maxWidth    最大宽度，必须大于等于省略符长度 + 1
     * @return 截断后的字符串
     * @throws IllegalArgumentException 如果 {@code maxWidth} 太小
     */
    public static String abbreviate(String str, String abbrevEllipsis, int maxWidth) {
        if (str == null) {
            return null;
        }
        int ellipsisLen = abbrevEllipsis == null ? 3 : abbrevEllipsis.length();
        if (maxWidth < ellipsisLen + 1) {
            throw new IllegalArgumentException("maxWidth must be at least " + (ellipsisLen + 1));
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        String ellipsis = abbrevEllipsis == null ? "..." : abbrevEllipsis;
        return str.substring(0, maxWidth - ellipsisLen) + ellipsis;
    }

    // ========================= 裁剪 =========================

    /**
     * 去除字符串两端的空白字符。
     *
     * <p>若字符串为 {@code null}，则返回 {@code null}。
     * 等效于 {@link String#trim()}，但对 {@code null} 安全。</p>
     *
     * <pre>
     * StringUtils.trim(null)   = null
     * StringUtils.trim("")     = ""
     * StringUtils.trim("  a b c  ") = "a b c"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @return 去除两端空白后的字符串
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 去除字符串两端的空白字符，结果为 {@code null} 时返回 {@code null}。
     *
     * <p>与 {@link String#trim()} 的区别：当 trim 结果为空字符串时返回 {@code null}。</p>
     *
     * <pre>
     * StringUtils.trimToNull(null)      = null
     * StringUtils.trimToNull("")        = null
     * StringUtils.trimToNull("  \t\r\n ") = null
     * StringUtils.trimToNull("abc")     = "abc"
     * StringUtils.trimToNull("  abc  ") = "abc"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @return 去除两端空白后的字符串，结果为空时返回 {@code null}
     */
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 去除字符串两端的空白字符，结果为 {@code null} 时返回空字符串。
     *
     * <pre>
     * StringUtils.trimToEmpty(null)      = ""
     * StringUtils.trimToEmpty("")        = ""
     * StringUtils.trimToEmpty("  \t\r\n ") = ""
     * StringUtils.trimToEmpty("abc")     = "abc"
     * StringUtils.trimToEmpty("  abc  ") = "abc"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @return 去除两端空白后的字符串，结果为空时返回空字符串
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * 去除字符串左侧的空白字符。
     *
     * <pre>
     * StringUtils.trimStart(null)     = null
     * StringUtils.trimStart("")       = ""
     * StringUtils.trimStart("abc  ")  = "abc  "
     * StringUtils.trimStart("  abc")  = "abc"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @return 去除左侧空白后的字符串
     */
    public static String trimStart(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int start = 0;
        while (start < str.length() && Character.isWhitespace(str.charAt(start))) {
            start++;
        }
        return str.substring(start);
    }

    /**
     * 去除字符串右侧的空白字符。
     *
     * <pre>
     * StringUtils.trimEnd(null)     = null
     * StringUtils.trimEnd("")       = ""
     * StringUtils.trimEnd("  abc")  = "  abc"
     * StringUtils.trimEnd("abc  ")  = "abc"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @return 去除右侧空白后的字符串
     */
    public static String trimEnd(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        int end = str.length() - 1;
        while (end >= 0 && Character.isWhitespace(str.charAt(end))) {
            end--;
        }
        return str.substring(0, end + 1);
    }

    /**
     * 去除字符串中所有的空白字符（包括空格、制表符、换行符等）。
     *
     * <pre>
     * StringUtils.deleteWhitespace(null)            = null
     * StringUtils.deleteWhitespace("")              = ""
     * StringUtils.deleteWhitespace("abc")           = "abc"
     * StringUtils.deleteWhitespace("  ab c  d e ") = "abcde"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @return 去除所有空白后的新字符串
     */
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int len = str.length();
        char[] chars = new char[len];
        int count = 0;
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chars[count++] = str.charAt(i);
            }
        }
        return count == len ? str : new String(chars, 0, count);
    }

    // ========================= 填充 =========================

    /**
     * 将字符串居中，并用指定字符在左右两侧填充至目标长度。
     *
     * <p>若字符串长度已达到或超过 {@code size}，则原样返回。</p>
     *
     * <pre>
     * StringUtils.center(null, *, *)  = null
     * StringUtils.center("", 4, ' ')  = "    "
     * StringUtils.center("ab", 4, ' ')   = " ab "
     * StringUtils.center("abcd", 4, ' ') = "abcd"
     * StringUtils.center("a", 4, ' ')    = " a  "
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param size   目标总长度
     * @param padStr 填充字符
     * @return 居中后的字符串
     */
    public static String center(String str, int size, char padStr) {
        return center(str, size, String.valueOf(padStr));
    }

    /**
     * 将字符串居中，并用指定字符串在左右两侧填充至目标长度。
     *
     * <pre>
     * StringUtils.center(null, *, *)    = null
     * StringUtils.center("", 4, " ")    = "    "
     * StringUtils.center("ab", 4, " ")  = " ab "
     * StringUtils.center("ab", 4, "xy") = "xab y"
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param size   目标总长度
     * @param padStr 填充字符串
     * @return 居中后的字符串
     */
    public static String center(String str, int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int strLen = str.length();
        int padLen = size - strLen;
        if (padLen <= 0) {
            return str;
        }
        str = leftPad(str, strLen + padLen / 2, padStr);
        return rightPad(str, size, padStr);
    }

    /**
     * 在字符串左侧填充指定字符，直到达到目标长度。
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)   = null
     * StringUtils.leftPad("", 3, '0')   = "000"
     * StringUtils.leftPad("bat", 5, 'z')= "zzbat"
     * StringUtils.leftPad("bat", 3, 'z')= "bat"
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param size   目标总长度
     * @param padStr 填充字符
     * @return 左填充后的字符串
     */
    public static String leftPad(String str, int size, char padStr) {
        return leftPad(str, size, String.valueOf(padStr));
    }

    /**
     * 在字符串左侧填充指定字符串，直到达到目标长度。
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param size   目标总长度
     * @param padStr 填充字符串，为 {@code null} 时使用空格
     * @return 左填充后的字符串
     */
    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = size - str.length();
        if (padLen <= 0) {
            return str;
        }
        return repeat(padStr, padLen / padStr.length() + 1, 0, padLen) + str;
    }

    /**
     * 在字符串右侧填充指定字符，直到达到目标长度。
     *
     * <pre>
     * StringUtils.rightPad(null, *, *)   = null
     * StringUtils.rightPad("", 3, '0')   = "000"
     * StringUtils.rightPad("bat", 5, 'z')= "batzz"
     * StringUtils.rightPad("bat", 3, 'z')= "bat"
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param size   目标总长度
     * @param padStr 填充字符
     * @return 右填充后的字符串
     */
    public static String rightPad(String str, int size, char padStr) {
        return rightPad(str, size, String.valueOf(padStr));
    }

    /**
     * 在字符串右侧填充指定字符串，直到达到目标长度。
     *
     * <pre>
     * StringUtils.rightPad(null, *, *)      = null
     * StringUtils.rightPad("", 3, "z")      = "zzz"
     * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtils.rightPad("bat", 3, "yz")  = "bat"
     * StringUtils.rightPad("bat", 5, null)  = "bat  "
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param size   目标总长度
     * @param padStr 填充字符串，为 {@code null} 时使用空格
     * @return 右填充后的字符串
     */
    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = size - str.length();
        if (padLen <= 0) {
            return str;
        }
        return str + repeat(padStr, padLen / padStr.length() + 1, 0, padLen);
    }

    // ========================= 重复 =========================

    /**
     * 将字符串重复指定次数。
     *
     * <pre>
     * StringUtils.repeat(null, *)   = null
     * StringUtils.repeat("", *)     = ""
     * StringUtils.repeat("ab", 0)   = ""
     * StringUtils.repeat("ab", 1)   = "ab"
     * StringUtils.repeat("ab", 2)   = "abab"
     * StringUtils.repeat("ab", 3)   = "ababab"
     * </pre>
     *
     * @param str   待重复的字符串，可为 {@code null}
     * @param count 重复次数，不允许为负数
     * @return 重复后的新字符串
     */
    public static String repeat(String str, int count) {
        if (str == null) {
            return null;
        }
        if (count <= 0) {
            return EMPTY;
        }
        if (count == 1) {
            return str;
        }
        int len = str.length();
        long longLen = (long) len * count;
        if (longLen > Integer.MAX_VALUE) {
            throw new OutOfMemoryError("Required array size too large: " + longLen);
        }
        char[] chars = new char[(int) longLen];
        str.getChars(0, len, chars, 0);
        int copied = len;
        int target = Math.min(copied, chars.length - copied);
        while (target > 0) {
            System.arraycopy(chars, 0, chars, copied, target);
            copied += target;
            target = Math.min(copied, chars.length - copied);
        }
        return new String(chars);
    }

    /**
     * 将字符串重复指定次数，并用分隔符连接。
     *
     * <pre>
     * StringUtils.repeat(null, null, 2)    = null
     * StringUtils.repeat("", null, 2)      = ""
     * StringUtils.repeat(null, ",", 2)     = null
     * StringUtils.repeat("?", ", ", 3)     = "?, ?, ?"
     * </pre>
     *
     * @param str   待重复的字符串，可为 {@code null}
     * @param separator 分隔符
     * @param count 重复次数
     * @return 用分隔符连接的重复字符串
     */
    public static String repeat(String str, String separator, int count) {
        if (str == null || separator == null) {
            return repeat(str, count);
        }
        if (count <= 0) {
            return EMPTY;
        }
        StringJoiner joiner = new StringJoiner(separator);
        for (int i = 0; i < count; i++) {
            joiner.add(str);
        }
        return joiner.toString();
    }

    /**
     * 获取重复字符串的子区间（从 {@code beginIndex} 开始，到 {@code endIndex} 结束，不含）。
     *
     * <p>内部方法，用于填充操作中截取重复序列的一部分。</p>
     *
     * @param str        待重复的字符串
     * @param repeat     重复次数
     * @param beginIndex 起始索引（含）
     * @param endIndex   结束索引（不含）
     * @return 子区间字符串
     */
    private static String repeat(String str, int repeat, int beginIndex, int endIndex) {
        int len = str.length();
        int count = endIndex - beginIndex;
        if (count <= 0) {
            return EMPTY;
        }
        char[] chars = new char[count];
        for (int i = 0; i < count; i++) {
            chars[i] = str.charAt((beginIndex + i) % len);
        }
        return new String(chars);
    }

    // ========================= 替换 =========================

    /**
     * 将字符串中的所有指定子串替换为新的子串。
     *
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("aba", "a", null)  = "nullbnull"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * StringUtils.replace("aaa", "a", "aa")  = "aaaaaa"
     * </pre>
     *
     * @param text         源字符串，可为 {@code null}
     * @param searchString 待替换的子串
     * @param replacement  替换后的子串
     * @return 替换后的新字符串
     */
    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * 将字符串中指定子串替换为新的子串，限制替换次数。
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("ababab", "ab", "z", 2)= "zab"
     * StringUtils.replace("ababab", "ab", "z", -1)= "zzz"
     * </pre>
     *
     * @param text         源字符串，可为 {@code null}
     * @param searchString 待替换的子串
     * @param replacement  替换后的子串
     * @param max          最大替换次数，{@code -1} 表示全部替换
     * @return 替换后的新字符串
     */
    public static String replace(String text, String searchString, String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        int replLength = searchString.length();
        // 预估增长量
        int increase = replacement.length() - replLength;
        increase = Math.max(increase, 0);
        increase *= (max < 0 ? 16 : Math.min(max, 64));
        StringBuilder buf = new StringBuilder(text.length() + increase);
        int count = 0;
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            count++;
            if (max > 0 && count >= max) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    /**
     * 将字符串中第一次出现的指定子串替换为新的子串。
     *
     * <pre>
     * StringUtils.replaceOnce(null, *, *)      = null
     * StringUtils.replaceOnce("", *, *)        = ""
     * StringUtils.replaceOnce("aba", "a", "z") = "zba"
     * </pre>
     *
     * @param text         源字符串，可为 {@code null}
     * @param searchString 待替换的子串
     * @param replacement  替换后的子串
     * @return 替换后的新字符串
     */
    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    /**
     * 移除字符串中所有出现的指定子串。
     *
     * <pre>
     * StringUtils.remove(null, *)      = null
     * StringUtils.remove("", *)        = ""
     * StringUtils.remove("queued", "ue") = "qd"
     * StringUtils.remove("queued", "zz") = "queued"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @param remove 待移除的子串
     * @return 移除后的新字符串
     */
    public static String remove(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        return replace(str, remove, EMPTY, -1);
    }

    // ========================= 反转 =========================

    /**
     * 反转字符串。
     *
     * <pre>
     * StringUtils.reverse(null)   = null
     * StringUtils.reverse("")     = ""
     * StringUtils.reverse("bat")  = "tab"
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @return 反转后的字符串
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    // ========================= 包含判断 =========================

    /**
     * 判断源字符串是否包含指定的子串。
     *
     * <p>对 {@code null} 安全——若源字符串或搜索串为 {@code null}，返回 {@code false}。</p>
     *
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param str       源字符串，可为 {@code null}
     * @param searchStr 待查找的子串，可为 {@code null}
     * @return 若包含则返回 {@code true}
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.contains(searchStr);
    }

    /**
     * 判断源字符串是否包含指定的字符。
     *
     * <pre>
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains("abc", 'a') = true
     * StringUtils.contains("abc", 'z') = false
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param searchChar 待查找的字符
     * @return 若包含则返回 {@code true}
     */
    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) != INDEX_NOT_FOUND;
    }

    /**
     * 判断源字符串是否包含指定的子串（忽略大小写）。
     *
     * <pre>
     * StringUtils.containsIgnoreCase(null, *)     = false
     * StringUtils.containsIgnoreCase(*, null)     = false
     * StringUtils.containsIgnoreCase("abc", "A")  = true
     * StringUtils.containsIgnoreCase("abc", "z")  = false
     * </pre>
     *
     * @param str       源字符串，可为 {@code null}
     * @param searchStr 待查找的子串，可为 {@code null}
     * @return 若包含（忽略大小写）则返回 {@code true}
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    // ========================= 前后缀判断 =========================

    /**
     * 判断字符串是否以指定前缀开头。
     *
     * <pre>
     * StringUtils.startsWith(null, null)    = true
     * StringUtils.startsWith("abcdef", "abc") = true
     * StringUtils.startsWith("ABCDEF", "abc") = false
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param prefix 前缀，可为 {@code null}
     * @return 若以指定前缀开头则返回 {@code true}
     */
    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    /**
     * 判断字符串是否以指定前缀开头（忽略大小写）。
     *
     * <pre>
     * StringUtils.startsWithIgnoreCase(null, null)    = true
     * StringUtils.startsWithIgnoreCase("abcdef", "abc") = true
     * StringUtils.startsWithIgnoreCase("ABCDEF", "abc") = true
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param prefix 前缀，可为 {@code null}
     * @return 若以指定前缀开头（忽略大小写）则返回 {@code true}
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    /**
     * 判断字符串是否以指定后缀结尾。
     *
     * <pre>
     * StringUtils.endsWith(null, null)    = true
     * StringUtils.endsWith("abcdef", "def") = true
     * StringUtils.endsWith("ABCDEF", "def") = false
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param suffix 后缀，可为 {@code null}
     * @return 若以指定后缀结尾则返回 {@code true}
     */
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * 判断字符串是否以指定后缀结尾（忽略大小写）。
     *
     * <pre>
     * StringUtils.endsWithIgnoreCase(null, null)    = true
     * StringUtils.endsWithIgnoreCase("abcdef", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "def") = true
     * </pre>
     *
     * @param str    源字符串，可为 {@code null}
     * @param suffix 后缀，可为 {@code null}
     * @return 若以指定后缀结尾（忽略大小写）则返回 {@code true}
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == null && suffix == null;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, str.length() - suffix.length(), suffix, 0, suffix.length());
    }

    // ========================= 索引查找 =========================

    /**
     * 查找子串在源字符串中第一次出现的位置。
     *
     * <pre>
     * StringUtils.indexOf(null, *)       = INDEX_NOT_FOUND
     * StringUtils.indexOf("", "")        = 0
     * StringUtils.indexOf("aabaabaa", "a") = 0
     * StringUtils.indexOf("aabaabaa", "b") = 2
     * StringUtils.indexOf("aabaabaa", "z") = INDEX_NOT_FOUND
     * </pre>
     *
     * @param str       源字符串，可为 {@code null}
     * @param searchStr 待查找的子串
     * @return 第一次出现的索引位置，未找到返回 {@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        return str.indexOf(searchStr);
    }

    /**
     * 从指定位置开始查找子串在源字符串中第一次出现的位置。
     *
     * @param str       源字符串，可为 {@code null}
     * @param searchStr 待查找的子串
     * @param startPos  起始搜索位置
     * @return 第一次出现的索引位置，未找到返回 {@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        return str.indexOf(searchStr, startPos);
    }

    /**
     * 查找子串在源字符串中最后一次出现的位置。
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)       = INDEX_NOT_FOUND
     * StringUtils.lastIndexOf("aabaabaa", "a") = 7
     * StringUtils.lastIndexOf("aabaabaa", "b") = 5
     * StringUtils.lastIndexOf("aabaabaa", "z") = INDEX_NOT_FOUND
     * </pre>
     *
     * @param str       源字符串，可为 {@code null}
     * @param searchStr 待查找的子串
     * @return 最后一次出现的索引位置，未找到返回 {@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        return str.lastIndexOf(searchStr);
    }

    // ========================= 字符串类型判断 =========================

    /**
     * 判断字符串是否仅由字母字符组成。
     *
     * <pre>
     * StringUtils.isAlpha(null)   = false
     * StringUtils.isAlpha("")     = false
     * StringUtils.isAlpha("abc")  = true
     * StringUtils.isAlpha("ab2c") = false
     * StringUtils.isAlpha("ab-c") = false
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 若仅包含字母则返回 {@code true}
     */
    public static boolean isAlpha(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否仅由数字字符组成。
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12.3") = false
     * StringUtils.isNumeric("12a3") = false
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 若仅包含数字则返回 {@code true}
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否仅由字母或数字字符组成。
     *
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = false
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str 待检查的字符串，可为 {@code null}
     * @return 若仅包含字母或数字则返回 {@code true}
     */
    public static boolean isAlphanumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // ========================= 比较 =========================

    /**
     * 比较两个字符串是否相等，对 {@code null} 安全。
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @param str1 第一个字符串，可为 {@code null}
     * @param str2 第二个字符串，可为 {@code null}
     * @return 若两个字符串相等则返回 {@code true}
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    /**
     * 比较两个字符串是否相等（忽略大小写），对 {@code null} 安全。
     *
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "abc")  = false
     * StringUtils.equalsIgnoreCase("abc", null)  = false
     * StringUtils.equalsIgnoreCase("abc", "abc") = true
     * StringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @param str1 第一个字符串，可为 {@code null}
     * @param str2 第二个字符串，可为 {@code null}
     * @return 若两个字符串相等（忽略大小写）则返回 {@code true}
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    // ========================= 分割 =========================

    /**
     * 按照分隔符将字符串分割为数组。
     *
     * <p>分隔符不作为正则表达式处理。连续分隔符会产生空字符串元素。
     * 与 {@link String#split(String)} 的区别：不对分隔符做正则解析，且不会移除尾部空串。</p>
     *
     * <pre>
     * StringUtils.split(null, *)       = null
     * StringUtils.split("", *)         = []
     * StringUtils.split("abc def", " ")= ["abc", "def"]
     * StringUtils.split("abc  def", " ")= ["abc", "", "def"]
     * StringUtils.split("a.b.c", ".")  = ["a", "b", "c"]
     * </pre>
     *
     * @param str         源字符串，可为 {@code null}
     * @param separatorChars 分隔符字符（逐字符匹配），为 {@code null} 时按空白分隔
     * @return 分割后的字符串数组
     */
    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * 按照分隔符将字符串分割为数组，限制返回元素个数。
     *
     * @param str         源字符串，可为 {@code null}
     * @param separatorChars 分隔符字符，为 {@code null} 时按空白分隔
     * @param max         最大返回元素个数，为 {@code -1} 时不限制
     * @return 分割后的字符串数组
     */
    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return new String[]{};
        }

        List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;

        if (separatorChars == null) {
            // 按空白分隔
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                } else {
                    lastMatch = true;
                    match = true;
                    i++;
                }
            }
        } else if (separatorChars.length() == 1) {
            // 单字符分隔——跳过 indexOf 调用
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                } else {
                    lastMatch = true;
                    match = true;
                    i++;
                }
            }
        } else {
            // 多字符分隔符——任一匹配即分割
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                } else {
                    lastMatch = true;
                    match = true;
                    i++;
                }
            }
        }

        if (match || (preserveTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[0]);
    }

    // ========================= 连接 =========================

    /**
     * 使用分隔符将字符串数组连接为单个字符串。
     *
     * <pre>
     * StringUtils.join(null, *)           = null
     * StringUtils.join([], *)             = ""
     * StringUtils.join(["a", "b"], ",")   = "a,b"
     * StringUtils.join(["a", "b", "c"], " | ") = "a | b | c"
     * </pre>
     *
     * @param array       待连接的字符串数组，可为 {@code null}
     * @param separator   分隔符
     * @return 连接后的字符串
     */
    public static String join(String[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * 使用分隔符将字符串数组的指定区间连接为单个字符串。
     *
     * @param array       待连接的字符串数组，可为 {@code null}
     * @param separator   分隔符
     * @param startIndex  起始索引（含）
     * @param endIndex    结束索引（不含）
     * @return 连接后的字符串
     */
    public static String join(String[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0) {
            return EMPTY;
        }
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].length()) + separator.length());
        StringBuilder buf = new StringBuilder(bufSize);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    // ========================= 计数 =========================

    /**
     * 计算子串在源字符串中出现的次数。
     *
     * <pre>
     * StringUtils.countMatches(null, *)     = 0
     * StringUtils.countMatches("", *)       = 0
     * StringUtils.countMatches("ab", "")    = 0
     * StringUtils.countMatches("ab", "a")   = 1
     * StringUtils.countMatches("aaa", "a")  = 3
     * StringUtils.countMatches("ababab", "ab") = 3
     * </pre>
     *
     * @param str       源字符串，可为 {@code null}
     * @param sub       待计数的子串
     * @return 出现次数
     */
    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return str == null ? 0 : 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != INDEX_NOT_FOUND) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * 计算字符在源字符串中出现的次数。
     *
     * <pre>
     * StringUtils.countMatches(null, *)     = 0
     * StringUtils.countMatches("", *)       = 0
     * StringUtils.countMatches("aaba", 'a') = 3
     * </pre>
     *
     * @param str 源字符串，可为 {@code null}
     * @param ch  待计数的字符
     * @return 出现次数
     */
    public static int countMatches(String str, char ch) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    // ========================= 差异比较 =========================

    /**
     * 比较两个字符串，返回从第几个字符开始不同的索引。
     *
     * <pre>
     * StringUtils.indexOfDifference(null, null)       = INDEX_NOT_FOUND
     * StringUtils.indexOfDifference("", "")           = INDEX_NOT_FOUND
     * StringUtils.indexOfDifference("", "abc")        = 0
     * StringUtils.indexOfDifference("abc", "")        = 0
     * StringUtils.indexOfDifference("abc", "abc")     = INDEX_NOT_FOUND
     * StringUtils.indexOfDifference("ab", "abxyz")    = 2
     * StringUtils.indexOfDifference("abcde", "abxyz") = 2
     * </pre>
     *
     * @param str1 第一个字符串，可为 {@code null}
     * @param str2 第二个字符串，可为 {@code null}
     * @return 第一个不同字符的索引，若完全相同则返回 {@link #INDEX_NOT_FOUND}
     */
    public static int indexOfDifference(String str1, String str2) {
        if (str1 == str2) {
            return INDEX_NOT_FOUND;
        }
        if (str1 == null || str2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < str1.length() && i < str2.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        if (i < str2.length() || i < str1.length()) {
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回两个字符串从差异位置开始的不同部分。
     *
     * <pre>
     * StringUtils.difference(null, null)   = null
     * StringUtils.difference("", "")       = ""
     * StringUtils.difference("", "abc")    = "abc"
     * StringUtils.difference("abc", "")    = ""
     * StringUtils.difference("abc", "abc") = ""
     * StringUtils.difference("abcde", "abxyz") = "xyz"
     * </pre>
     *
     * @param str1 第一个字符串，可为 {@code null}
     * @param str2 第二个字符串，可为 {@code null}
     * @return 从差异位置开始的 {@code str2} 部分
     */
    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return EMPTY;
        }
        int diff = indexOfDifference(str1, str2);
        if (diff == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str2.substring(diff);
    }
}
