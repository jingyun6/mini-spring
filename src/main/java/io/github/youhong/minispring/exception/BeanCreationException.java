package io.github.youhong.minispring.exception;

import java.io.Serial;

/**
 * Bean 在实例化或属性填充过程中创建失败时抛出的异常。
 *
 * <p>该异常将构造器调用、反射访问等底层受检异常转换为 mini-spring 的领域异常，
 * 避免 BeanFactory 的使用者依赖具体反射 API。原始异常会作为 cause 保留，
 * 便于定位实际失败原因。</p>
 *
 * @see BeansException
 */
public class BeanCreationException extends BeansException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 创建失败的 Bean 名称。 */
    private final String beanName;

    /**
     * 根据 Bean 名称和错误描述创建异常。
     *
     * @param beanName 创建失败的 Bean 名称
     * @param message  创建失败的具体描述
     */
    public BeanCreationException(
            String beanName,
            String message) {

        super(buildMessage(beanName, message));
        this.beanName = beanName;
    }

    /**
     * 根据 Bean 名称、错误描述和底层原因创建异常。
     *
     * @param beanName 创建失败的 Bean 名称
     * @param message  创建失败的具体描述
     * @param cause    引发创建失败的底层异常
     */
    public BeanCreationException(
            String beanName,
            String message,
            Throwable cause) {

        super(buildMessage(beanName, message), cause);
        this.beanName = beanName;
    }

    /**
     * 获取创建失败的 Bean 名称。
     *
     * @return 创建失败的 Bean 名称
     */
    public String getBeanName() {
        return beanName;
    }

    /** 生成包含 Bean 名称的统一错误信息。 */
    private static String buildMessage(
            String beanName,
            String message) {

        return "Failed to create bean '"
                + beanName
                + "': "
                + message;
    }
}
