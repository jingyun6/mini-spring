package io.github.youhong.minispring.beans;


/**
 * Bean 定义注册表，负责管理容器中的 {@link BeanDefinition Bean 定义}。
 *
 * <p>BeanDefinition 是 Bean 的元数据描述，而不是已经创建好的 Bean 实例。
 * 注册表通过 Bean 名称维护这些元数据，使扫描、注册与实例化三个阶段能够彼此分离。
 * BeanFactory 后续创建 Bean、解析依赖和判断作用域时，都会以这里保存的定义为依据。</p>
 *
 * <p>该接口只描述 BeanDefinition 的管理能力，不负责创建 Bean 实例。实例创建由
 * {@link BeanFactory} 及其实现类负责。</p>
 *
 * @see BeanDefinition
 * @see BeanFactory
 */
public interface BeanDefinitionRegistry {

    /**
     * 注册一个 Bean 定义到当前注册表中。
     *
     * @param beanName       Bean 的唯一标识名称
     * @param beanDefinition 待注册的 Bean 定义元数据
     * @throws IllegalArgumentException 如果参数不合法
     * @throws IllegalStateException 如果已经存在同名 BeanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 根据 Bean 名称获取对应的 Bean 定义。
     *
     * @param beanName Bean 的唯一标识名称
     * @return 与名称关联的 {@link BeanDefinition}
     * @throws io.github.youhong.minispring.exception.BeanDefinitionNotFoundException
     *         如果不存在指定名称的 BeanDefinition
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 判断指定名称的 Bean 定义是否已注册。
     *
     * @param beanName Bean 的唯一标识名称
     * @return {@code true} 表示已注册，{@code false} 表示未注册
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 获取当前注册表中全部 BeanDefinition 的名称。
     *
     * <p>返回结果用于遍历容器中的 Bean 定义，例如在 ApplicationContext 启动阶段
     * 预实例化所有单例 Bean。名称顺序由具体实现决定，调用方不应依赖返回顺序。</p>
     *
     * @return BeanDefinition 名称数组；没有定义时返回空数组
     */
    String[] getBeanDefinitionNames();
}
