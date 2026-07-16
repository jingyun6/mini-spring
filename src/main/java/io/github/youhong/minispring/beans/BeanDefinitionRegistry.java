package io.github.youhong.minispring.beans;


/**
 * <p>
 * Bean 定义注册表接口，提供 Bean 定义的注册、查找与存在性校验能力。
 * </p>
 * <p>
 * 该接口定义了 IoC 容器管理 Bean 元数据的核心契约。实现类负责维护 Bean 名称到
 * {@link BeanDefinition} 的映射关系，为容器的依赖注入和 Bean 实例化提供数据基础。
 * </p>
 */
public interface BeanDefinitionRegistry {

    /**
     * 注册一个 Bean 定义到当前注册表中。
     *
     * @param beanName       Bean 的唯一标识名称
     * @param beanDefinition 待注册的 Bean 定义元数据
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 根据 Bean 名称获取对应的 Bean 定义。
     *
     * @param beanName Bean 的唯一标识名称
     * @return 与名称关联的 {@link BeanDefinition}，若不存在则返回 {@code null}
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 判断指定名称的 Bean 定义是否已注册。
     *
     * @param beanName Bean 的唯一标识名称
     * @return {@code true} 表示已注册，{@code false} 表示未注册
     */
    boolean containsBeanDefinition(String beanName);
}
