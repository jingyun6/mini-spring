package io.github.youhong.minispring.factory;

import io.github.youhong.minispring.utils.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link SingletonBeanRegistry} 接口的默认实现。
 *
 * <p>使用 {@link ConcurrentHashMap} 作为单例 Bean 的存储容器，
 * 提供单例 Bean 的注册、获取和存在性检查功能。该注册表只负责缓存的单次读写安全；
 * 完整 Bean 创建流程的并发唯一性由上层
 * {@link io.github.youhong.minispring.beans.DefaultListableBeanFactory} 的创建监视器协调。
 *
 * <p><b>设计要点：</b>
 * <ul>
 *     <li>存储容器选用 {@code ConcurrentHashMap}，保证单次缓存读写安全</li>
 *     <li>不允许重复注册同名单例 Bean，重复注册将抛出 {@link IllegalArgumentException}</li>
 *     <li>所有公共方法均对参数进行非空断言，遵循快速失败（fail-fast）原则</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * DefaultSingletonBeanRegistry registry = new DefaultSingletonBeanRegistry();
 * registry.registerSingleton("myBean", new MyBean());
 * Object bean = registry.getSingleton("myBean");
 * boolean exists = registry.containsSingleton("myBean"); // true
 * }</pre>
 *
 * @author YouHong
 * @see SingletonBeanRegistry
 * @since 1.0
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    /**
     * 创建一个空的单例 Bean 注册表。
     */
    public DefaultSingletonBeanRegistry() {
    }

    /**
     * 单例 Bean 缓存池。
     *
     * <p>key 为 Bean 名称，value 为对应的单例 Bean 实例。
     * 使用 {@link ConcurrentHashMap} 保证单次缓存读写的线程安全。
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * 根据 Bean 名称获取已注册的单例 Bean 实例。
     *
     * @param beanName Bean 名称，不能为 {@code null}
     * @return 与指定名称关联的单例 Bean 实例；若该名称未注册，则返回 {@code null}
     * @throws IllegalArgumentException 如果 {@code beanName} 为 {@code null}
     */
    @Override
    public Object getSingleton(String beanName) {
        Assert.notNull(beanName, "beanName must not be null");
        return singletonObjects.get(beanName);
    }

    /**
     * 将指定的 Bean 实例注册为单例。
     *
     * <p>注册前会校验该名称是否已存在，若已存在则抛出异常，
     * 以此保证同一名称只对应一个单例实例。
     *
     * @param beanName        Bean 名称，不能为 {@code null}
     * @param singletonObject 单例 Bean 实例，不能为 {@code null}
     * @throws IllegalArgumentException 如果 {@code beanName} 或 {@code singletonObject} 为 {@code null}，
     *                                  或者该名称的单例已存在
     */
    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        Assert.notNull(beanName, "beanName must not be null");
        Assert.notNull(singletonObject, "singletonObject must not be null");
        if (containsSingleton(beanName)) {
            throw new IllegalArgumentException("beanName '" + beanName + "' already exists");
        }
        singletonObjects.put(beanName, singletonObject);
    }

    /**
     * 检查指定名称的单例 Bean 是否已注册。
     *
     * @param beanName Bean 名称
     * @return 如果该名称已注册单例 Bean，返回 {@code true}；否则返回 {@code false}
     */
    @Override
    public boolean containsSingleton(String beanName) {
        return singletonObjects.containsKey(beanName);
    }
}
