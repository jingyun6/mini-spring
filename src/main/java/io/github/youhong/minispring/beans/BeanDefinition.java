package io.github.youhong.minispring.beans;

/**
 * <p>
 * Bean 定义元数据模型，描述一个 Bean 的类型、名称及作用域信息。
 * </p>
 * <p>
 * 该类是 mini-spring IoC 容器的基础数据载体，每个被 {@code @Component} 标注的类
 * 在扫描阶段都会封装为一个 {@code BeanDefinition} 实例，由 {@link BeanDefinitionRegistry} 统一管理。
 * 默认作用域为单例（singleton=true），后续可扩展支持原型（prototype）等其他作用域。
 * </p>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/15 17:33
 */
public class BeanDefinition {

    /** Bean 对应的 Java 类，容器通过反射调用其构造器创建实例 */
    private Class<?> beanClass;

    /** Bean 的唯一标识名称，默认由类名首字母小写生成 */
    private String beanName;

    /** 是否为单例，默认为 {@code true}；后续可扩展支持原型（prototype）等其他作用域 */
    private boolean singleton = true;

    /**
     * 创建空的 Bean 定义实例，所有属性需通过 setter 后续设置。
     */
    public BeanDefinition() {
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
