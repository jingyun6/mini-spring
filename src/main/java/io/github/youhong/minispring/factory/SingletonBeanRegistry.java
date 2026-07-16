package io.github.youhong.minispring.factory;

/**
 * 单例 Bean 注册表接口——定义单例 Bean 的注册、获取与存在性检查的契约。
 *
 * <p>该接口抽象了单例 Bean 的存储与访问能力，与 {@link io.github.youhong.minispring.beans.BeanFactory}
 * 解耦。它属于 IoC 容器基础设施层，负责管理已完成实例化的单例 Bean 的运行时缓存。
 *
 * <p><b>设计意图：</b>将"单例缓存的管理"与"Bean 的创建流程"分离，遵循单一职责原则（SRP）。
 * {@link DefaultSingletonBeanRegistry} 使用线程安全的 {@code ConcurrentHashMap}
 * 作为默认存储实现。
 *
 * <p><b>与 Spring 的对应关系：</b>对应 Spring Framework 中的
 * {@code org.springframework.beans.factory.support.SingletonBeanRegistry} 接口，
 * 是 Spring 三级缓存机制的基础组件。
 *
 * @author YouHong
 * @see DefaultSingletonBeanRegistry
 * @since 1.0
 */
public interface SingletonBeanRegistry {

    /**
     * 根据名称获取已注册的单例 Bean 实例。
     *
     * @param beanName Bean 的唯一标识名称，不能为 {@code null}
     * @return 与指定名称关联的单例实例；若未注册则返回 {@code null}
     */
    Object getSingleton(String beanName);

    /**
     * 将指定的对象注册为单例 Bean。
     *
     * <p>同一名称只允许注册一次，重复注册将触发异常。
     *
     * @param beanName        Bean 的唯一标识名称，不能为 {@code null}
     * @param singletonObject 待注册的单例实例，不能为 {@code null}
     * @throws IllegalArgumentException 如果该名称已存在对应的单例
     */
    void registerSingleton(String beanName, Object singletonObject);

    /**
     * 判断指定名称的单例 Bean 是否已注册。
     *
     * @param beanName Bean 的唯一标识名称
     * @return {@code true} 如果该名称已注册单例，否则返回 {@code false}
     */
    boolean containsSingleton(String beanName);
}
