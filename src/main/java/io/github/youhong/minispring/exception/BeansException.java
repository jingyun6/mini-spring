package io.github.youhong.minispring.exception;

import java.io.Serial;

/**
 * mini-spring 异常体系的根异常。
 *
 * <p>容器在 BeanDefinition 注册、Bean 创建、依赖解析和生命周期处理过程中
 * 发生的异常，都应转换为该异常的具体子类。</p>
 *
 * <p>该异常继承 {@link RuntimeException}，调用者无需处理反射 API 暴露的
 * 受检异常，但仍然可以通过 {@link #getCause()} 获取底层原因。</p>
 *
 * <p>框架内部应尽量抛出语义更具体的子类；调用者也可以统一捕获该类型，
 * 处理所有来自 Bean 容器的运行时异常。</p>
 */
public class BeansException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用指定错误信息创建容器异常。
     *
     * @param message 描述容器失败原因的错误信息
     */
    public BeansException(String message) {
        super(message);
    }

    /**
     * 使用指定错误信息和根本原因创建容器异常。
     *
     * @param message 描述容器失败原因的错误信息
     * @param cause   引发当前容器异常的底层原因
     */
    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
