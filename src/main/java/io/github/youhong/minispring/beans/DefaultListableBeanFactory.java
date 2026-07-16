package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.exception.BeanDefinitionNotFoundException;
import io.github.youhong.minispring.factory.DefaultSingletonBeanRegistry;
import io.github.youhong.minispring.utils.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mini-spring 的核心 IoC 容器实现——同时提供 Bean 定义注册与 Bean 实例获取能力。
 *
 * <p>该类是整个框架的中枢，将 <b>Bean 定义管理</b> 与 <b>Bean 实例化</b> 两大职责集成于一体：
 *
 * <p><b>类层次结构：</b>
 * <pre>
 * BeanFactory                      SingletonBeanRegistry
 *   ↑ (getBean)                      ↑ (单例缓存)
 *   |                                |
 *   |         DefaultSingletonBeanRegistry
 *   |           ↑ (继承)
 *   |           |
 * DefaultListableBeanFactory  ← 同时实现 BeanFactory + BeanDefinitionRegistry
 * </pre>
 *
 * <p><b>核心流程——getBean(String)：</b>
 * <ol>
 *     <li>查单例缓存（一级缓存）：命中则直接返回</li>
 *     <li>获取 Bean 定义：从 {@code beanDefinitionMap} 中查找对应名称的定义</li>
 *     <li>创建 Bean 实例：通过反射调用默认构造器</li>
 *     <li>注册为单例：将新实例放入单例缓存，后续请求直接命中缓存</li>
 * </ol>
 *
 * <p><b>线程安全：</b>使用 {@link ConcurrentHashMap} 存储 Bean 定义，
 * 保证并发环境下的读写安全。单例缓存同样由父类 {@link DefaultSingletonBeanRegistry}
 * 的 {@code ConcurrentHashMap} 保证线程安全。
 *
 * <p><b>当前限制（后续版本将完善）：</b>
 * <ul>
 *     <li>仅支持无参构造器实例化</li>
 *     <li>尚未实现依赖注入（{@code @Autowired}）</li>
 *     <li>尚未实现 Bean 后处理器（BeanPostProcessor）</li>
 *     <li>不支持原型作用域（prototype scope）</li>
 * </ul>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/16 08:11
 * @see BeanFactory
 * @see BeanDefinitionRegistry
 * @see DefaultSingletonBeanRegistry
 */
public class DefaultListableBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

    /**
     * Bean 定义注册表——存储所有已注册的 Bean 定义元数据。
     *
     * <p>key 为 Bean 名称，value 为对应的 {@link BeanDefinition} 元数据。
     * 使用 {@link ConcurrentHashMap} 保证并发安全。
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 向容器注册一个 Bean 定义。
     *
     * <p>注册前会对参数进行非空校验，并检查是否已存在同名的 Bean 定义，
     * 若已存在则抛出 {@link IllegalStateException}。
     *
     * @param beanName       Bean 的唯一标识名称，不能为 {@code null}
     * @param beanDefinition 待注册的 Bean 定义元数据，不能为 {@code null}
     * @throws IllegalStateException    如果该名称已存在对应的 Bean 定义
     * @throws IllegalArgumentException 如果任一参数为 {@code null}
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");

        if (containsBeanDefinition(beanName)) {
            throw new IllegalStateException("BeanDefinition '" + beanName + "' already exists.");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 根据 Bean 名称获取 Bean 定义元数据。
     *
     * @param beanName Bean 的唯一标识名称
     * @return 与指定名称关联的 {@link BeanDefinition}
     * @throws BeanDefinitionNotFoundException 如果未找到对应的 Bean 定义
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        if (beanDefinitionMap.get(beanName) == null) {
            throw new BeanDefinitionNotFoundException(beanName);
        }
        return beanDefinitionMap.get(beanName);
    }

    /**
     * 判断指定名称的 Bean 定义是否已注册。
     *
     * @param beanName Bean 的唯一标识名称
     * @return {@code true} 如果已注册，否则返回 {@code false}
     */
    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    /**
     * 根据名称获取（或创建）Bean 实例。
     *
     * <p>执行流程：
     * <ol>
     *     <li>查询单例缓存，命中则直接返回</li>
     *     <li>获取对应的 {@link BeanDefinition}</li>
     *     <li>通过反射创建 Bean 实例（当前仅支持无参构造器）</li>
     *     <li>将新实例注册到单例缓存</li>
     * </ol>
     *
     * @param beanName Bean 的唯一标识名称
     * @return 对应的 Bean 实例（单例）
     * @throws BeanDefinitionNotFoundException 如果未找到对应的 Bean 定义
     * @throws RuntimeException                如果实例化过程中发生反射异常
     */
    @Override
    public Object getBean(String beanName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 步骤 1：查单例缓存
        Object singleton = getSingleton(beanName);
        if (singleton != null) {
            return singleton;
        }
        // 步骤 2：获取 Bean 定义（找不到时自动抛出 BeanDefinitionNotFoundException）
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        // 步骤 3-4：创建实例并注册为单例
        Object bean = createBean(beanDefinition);
        registerSingleton(beanName, bean);
        return bean;
    }

    /**
     * 根据类型获取 Bean 实例。
     *
     * <p>遍历所有已注册的 {@link BeanDefinition}，使用 {@link Class#isAssignableFrom(Class)}
     * 进行类型匹配，因此支持匹配子类和接口实现类。
     *
     * <p>若找到多个匹配类型的 Bean，将抛出异常提示类型不唯一。
     *
     * @param <T>          期望的 Bean 类型
     * @param requiredType 期望的 Bean 类型 Class 对象
     * @return 匹配类型的 Bean 实例
     * @throws RuntimeException 如果未找到匹配类型的 Bean，或找到多个匹配类型的 Bean
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String beanName = null;
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            // 使用 isAssignableFrom 支持子类和接口实现类匹配
            if (requiredType.isAssignableFrom(beanDefinition.getBeanClass())) {

                if (beanName != null) {
                    throw new RuntimeException(
                            "Multiple beans of type "
                                    + requiredType.getName());
                }

                beanName = beanDefinition.getBeanName();
            }
        }
        if (beanName == null) {
            throw new RuntimeException("No bean of type "
                    + requiredType.getName());
        }
        return (T) getBean(beanName);
    }

    /**
     * 通过反射创建 Bean 实例。
     *
     * <p>当前仅支持调用无参构造器（{@code getDeclaredConstructor().newInstance()}）。
     * 后续版本将扩展支持：
     * <ul>
     *     <li>构造器注入（Constructor Injection）</li>
     *     <li>依赖注入——处理 {@code @Autowired} 注解的字段</li>
     *     <li>{@code @PostConstruct} 初始化回调</li>
     * </ul>
     *
     * @param beanDefinition Bean 定义元数据
     * @return 新创建的 Bean 实例
     * @throws NoSuchMethodException     如果 Bean 类没有可访问的无参构造器
     * @throws InstantiationException    如果 Bean 类是抽象类或接口
     * @throws IllegalAccessException    如果构造器不可访问
     * @throws InvocationTargetException 如果构造器内部抛出异常
     */
    private Object createBean(BeanDefinition beanDefinition) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return beanDefinition.getBeanClass().getDeclaredConstructor().newInstance();
    }
}
