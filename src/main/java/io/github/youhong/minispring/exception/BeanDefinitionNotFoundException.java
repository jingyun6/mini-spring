package io.github.youhong.minispring.exception;

import java.io.Serial;

/**
 * 当在容器（Registry）中找不到指定的 BeanDefinition（Bean定义/图纸）时抛出该异常。
 *
 * @author YouHong5286
 * @since 2026-07-16
 * @version 1.0.0
 */
public class BeanDefinitionNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 导致异常的 Bean 名称 */
    private final String beanName;

    /**
     * 最常用的构造方法：直接传入找不到的 beanName
     *
     * @param beanName 丢失的 Bean 定义名称
     */
    public BeanDefinitionNotFoundException(String beanName) {
        super("No bean definition found with name '" + beanName + "' in container.");
        this.beanName = beanName;
    }

    /**
     * 允许自定义详细错误描述的构造方法
     *
     * @param beanName 丢失的 Bean 定义名称
     * @param message  详细错误信息
     */
    public BeanDefinitionNotFoundException(String beanName, String message) {
        super(message);
        this.beanName = beanName;
    }

    /**
     * 允许传入根源异常（Cause）的构造方法（用于异常链传递）
     *
     * @param beanName 丢失的 Bean 定义名称
     * @param message  详细错误信息
     * @param cause    导致该异常的根本原因
     */
    public BeanDefinitionNotFoundException(String beanName, String message, Throwable cause) {
        super(message, cause);
        this.beanName = beanName;
    }

    /**
     * 获取找不到的 Bean 名称
     */
    public String getBeanName() {
        return this.beanName;
    }
}