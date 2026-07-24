package io.github.youhong.minispring.exception;

import java.io.Serial;

/**
 * 按名称请求的 BeanDefinition 不存在时抛出的兼容异常。
 *
 * <p>新的查询代码可以统一使用 {@link NoSuchBeanDefinitionException}；保留该类型是为了
 * 兼容现有的按名称查找 API 和调用方。</p>
 *
 * @author YouHong5286
 * @since 2026-07-16
 * @version 1.0.0
 */
public class BeanDefinitionNotFoundException extends NoSuchBeanDefinitionException {

    @Serial
    private static final long serialVersionUID = -2442167059944191609L;

    /**
     * 根据未找到的 Bean 名称创建异常，并生成默认错误信息。
     *
     * @param beanName 未找到定义的 Bean 名称
     */
    public BeanDefinitionNotFoundException(String beanName) {
        super(beanName);
    }

    /**
     * 根据 Bean 名称和自定义错误信息创建异常。
     *
     * @param beanName 未找到定义的 Bean 名称
     * @param message  详细错误信息
     */
    public BeanDefinitionNotFoundException(String beanName, String message) {
        super(beanName, message);
    }

    /**
     * 根据 Bean 名称、自定义错误信息和根本原因创建异常。
     *
     * @param beanName 未找到定义的 Bean 名称
     * @param message  详细错误信息
     * @param cause    导致该异常的根本原因
     */
    public BeanDefinitionNotFoundException(String beanName, String message, Throwable cause) {
        super(beanName, message, cause);
    }
}
