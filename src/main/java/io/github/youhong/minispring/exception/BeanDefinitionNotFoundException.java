package io.github.youhong.minispring.exception;

import java.io.Serial;

/**
 * 请求的 BeanDefinition 不存在时抛出的异常。
 *
 * <p>该异常表示 Bean 元数据查找失败，而不是 Bean 在实例化过程中失败。
 * 异常会保留查询使用的 Bean 名称，便于调用方诊断注册遗漏或名称拼写错误。</p>
 *
 * @author YouHong5286
 * @since 2026-07-16
 * @version 1.0.0
 */
public class BeanDefinitionNotFoundException extends BeansException {

    @Serial
    private static final long serialVersionUID = -2442167059944191609L;

    /** 未找到对应 BeanDefinition 的 Bean 名称。 */
    private final String beanName;

    /**
     * 根据未找到的 Bean 名称创建异常，并生成默认错误信息。
     *
     * @param beanName 未找到定义的 Bean 名称
     */
    public BeanDefinitionNotFoundException(String beanName) {
        super("No bean definition found with name '" + beanName + "' in container.");
        this.beanName = beanName;
    }

    /**
     * 根据 Bean 名称和自定义错误信息创建异常。
     *
     * @param beanName 未找到定义的 Bean 名称
     * @param message  详细错误信息
     */
    public BeanDefinitionNotFoundException(String beanName, String message) {
        super(message);
        this.beanName = beanName;
    }

    /**
     * 根据 Bean 名称、自定义错误信息和根本原因创建异常。
     *
     * @param beanName 未找到定义的 Bean 名称
     * @param message  详细错误信息
     * @param cause    导致该异常的根本原因
     */
    public BeanDefinitionNotFoundException(String beanName, String message, Throwable cause) {
        super(message, cause);
        this.beanName = beanName;
    }

    /**
     * 获取未找到定义的 Bean 名称。
     *
     * @return 未找到定义的 Bean 名称
     */
    public String getBeanName() {
        return this.beanName;
    }
}
