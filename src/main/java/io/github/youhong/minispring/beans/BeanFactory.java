package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.exception.BeanDefinitionNotFoundException;

import java.lang.reflect.InvocationTargetException;

/**
 * IoC 容器的根接口——定义访问 Bean 的基本契约。
 *
 * <p>作为 mini-spring 框架中最核心的接口之一，{@code BeanFactory} 提供了从
 * IoC 容器中获取 Bean 的入口。它代表了 Spring 中 ApplicationContext 的
 * 最底层抽象，遵循"控制反转"（Inversion of Control）的设计原则：
 * 对象的创建、装配和生命周期管理交由容器负责，调用者只需按名称或类型获取即可。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *     <li>按名称（{@link #getBean(String)}）获取 Bean 实例</li>
 *     <li>按类型（{@link #getBean(Class)}）获取 Bean 实例，支持泛型自动推断</li>
 * </ul>
 *
 * <p><b>设计模式：</b>
 * <ul>
 *     <li><b>工厂方法模式</b> — 将对象的创建逻辑封装在工厂内部，客户端无需关心实例化细节</li>
 *     <li><b>模板方法模式</b> — 子类（如 {@code AbstractBeanFactory}）可扩展具体的
 *         Bean 创建流程，而接口仅定义顶层契约</li>
 * </ul>
 *
 * <p><b>典型使用场景：</b>
 * <pre>{@code
 * BeanFactory factory = new DefaultListableBeanFactory();
 * // 按名称获取
 * UserService userService = (UserService) factory.getBean("userService");
 * // 按类型获取（泛型安全，无需强制转换）
 * OrderService orderService = factory.getBean(OrderService.class);
 * }</pre>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/16 08:25
 */
public interface BeanFactory {

    /**
     * 根据 Bean 名称从容器中获取 Bean 实例。
     *
     * <p>如果指定名称的 Bean 尚未创建，容器将负责完成其完整的生命周期流程：
     * 实例化 → 属性填充 → 初始化 → 返回可用实例。
     *
     * @param beanName Bean 的唯一标识名称，不能为 {@code null}
     * @return 与指定名称关联的 Bean 实例
     * @throws BeanDefinitionNotFoundException 如果容器中不存在指定名称的 Bean 定义
     * @throws NoSuchMethodException           如果 Bean 类不存在无参构造器
     * @throws InstantiationException          如果 Bean 类不能被实例化
     * @throws IllegalAccessException          如果无参构造器不可访问
     * @throws InvocationTargetException       如果构造器内部抛出异常
     */
    Object getBean(String beanName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    /**
     * 根据 Bean 类型从容器中获取 Bean 实例。
     *
     * <p>使用泛型参数 {@code <T>} 进行类型安全的 Bean 获取，调用方无需
     * 手动进行类型转换。容器会自动推断目标类型并返回匹配的实例。
     *
     * <p>如果容器中存在多个匹配类型的 Bean，实现类应根据优先级规则
     * （如 {@code @Primary} 注解）选择合适的实例。
     *
     * @param <T>          期望的 Bean 类型
     * @param requiredType 期望的 Bean 类型 Class 对象，不能为 {@code null}
     * @return 与指定类型匹配的 Bean 实例
     * @throws RuntimeException          如果不存在指定类型的 Bean，或存在多个匹配类型的 Bean
     * @throws NoSuchMethodException     如果匹配 Bean 的类不存在无参构造器
     * @throws InstantiationException    如果匹配 Bean 的类不能被实例化
     * @throws IllegalAccessException    如果无参构造器不可访问
     * @throws InvocationTargetException 如果构造器内部抛出异常
     */
    <T> T getBean(Class<T> requiredType) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
