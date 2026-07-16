package io.github.youhong.minispring.utils;

/**
 * 断言工具类——提供简洁的参数校验方法。
 *
 * <p>该类为纯工具类（utility class），禁止实例化。所有方法均为静态方法，
 * 遵循 <b>快速失败（fail-fast）</b> 原则：在参数不合法时立即抛出异常，
 * 避免错误状态在系统中传播。
 *
 * <p><b>设计动机：</b>避免在业务代码中反复编写相同的参数校验逻辑，
 * 将校验动作语义化为一目了然的方法调用。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * public void register(String beanName, Object bean) {
 *     Assert.notNull(beanName, "beanName must not be null");
 *     Assert.notNull(bean, "bean must not be null");
 *     // ... 正常业务逻辑
 * }
 * }</pre>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/16 09:47
 */
public class Assert {

    /**
     * 私有构造器，防止外部实例化工具类。
     */
    private Assert() {
    }

    /**
     * 断言指定对象不为 {@code null}。
     *
     * <p>若对象为 {@code null}，则抛出 {@link IllegalArgumentException}，
     * 异常消息为传入的 {@code message}。
     *
     * @param object  待校验的对象
     * @param message 校验失败时的异常消息
     * @throws IllegalArgumentException 如果 {@code object} 为 {@code null}
     */
    public static void notNull(
            Object object,
            String message) {

        if (object == null) {
            throw new IllegalArgumentException(message);
        }

    }
}
