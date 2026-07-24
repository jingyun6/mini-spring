package io.github.youhong.minispring.exception;

import java.io.Serial;

/**
 * 容器中不存在指定名称或指定类型的 BeanDefinition 时抛出的异常。
 *
 * <p>该异常记录本次查询使用的名称或类型。按名称查询时
 * {@link #getBeanName()} 有值；按类型查询时 {@link #getRequiredType()} 有值。
 * 两个查询条件不会同时存在。</p>
 */
public class NoSuchBeanDefinitionException extends BeansException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 按名称查询时使用的 Bean 名称；按类型查询时为 {@code null}。 */
    private final String beanName;

    /** 按类型查询时使用的目标类型；按名称查询时为 {@code null}。 */
    private final Class<?> requiredType;

    /**
     * 创建一个按名称查询失败的异常。
     *
     * @param beanName 未找到定义的 Bean 名称
     */
    public NoSuchBeanDefinitionException(String beanName) {
        this(
                beanName,
                null,
                "No bean definition found with name '" + beanName + "' in container."
        );
    }

    /**
     * 创建一个按类型查询失败的异常。
     *
     * @param requiredType 未找到 BeanDefinition 的目标类型
     */
    public NoSuchBeanDefinitionException(Class<?> requiredType) {
        this(
                null,
                requiredType,
                "No bean definition of type '" + requiredType.getName() + "' found in container."
        );
    }

    /**
     * 供子类使用自定义错误信息创建按类型查询异常。
     *
     * @param requiredType 查询使用的目标类型
     * @param message      详细错误信息
     */
    protected NoSuchBeanDefinitionException(
            Class<?> requiredType,
            String message) {

        this(null, requiredType, message);
    }

    /**
     * 供兼容异常类型使用自定义错误信息创建按名称查询异常。
     *
     * @param beanName 查询使用的 Bean 名称
     * @param message  详细错误信息
     */
    protected NoSuchBeanDefinitionException(
            String beanName,
            String message) {

        this(beanName, null, message);
    }

    /**
     * 供兼容异常类型保留底层原因。
     *
     * @param beanName 查询使用的 Bean 名称
     * @param message  详细错误信息
     * @param cause    导致查询失败的底层异常
     */
    protected NoSuchBeanDefinitionException(
            String beanName,
            String message,
            Throwable cause) {

        super(message, cause);
        this.beanName = beanName;
        this.requiredType = null;
    }

    private NoSuchBeanDefinitionException(
            String beanName,
            Class<?> requiredType,
            String message) {

        super(message);
        this.beanName = beanName;
        this.requiredType = requiredType;
    }

    /**
     * 获取查询使用的 Bean 名称。
     *
     * @return Bean 名称；按类型查询时返回 {@code null}
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * 获取查询使用的目标类型。
     *
     * @return 目标类型；按名称查询时返回 {@code null}
     */
    public Class<?> getRequiredType() {
        return requiredType;
    }
}
