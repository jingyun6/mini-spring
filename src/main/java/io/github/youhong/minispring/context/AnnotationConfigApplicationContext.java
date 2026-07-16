package io.github.youhong.minispring.context;

import io.github.youhong.minispring.beans.BeanDefinition;
import io.github.youhong.minispring.beans.DefaultListableBeanFactory;
import io.github.youhong.minispring.scanner.ClassPathScanner;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * 基于注解配置的 {@link ApplicationContext} 实现——mini-spring 应用启动的入口。
 *
 * <p>该类在构造时接收一个基础包路径，通过类路径扫描自动发现所有被
 * {@link io.github.youhong.minispring.annotation.Component @Component} 标注的类，
 * 将其注册为 Bean 定义并完成预实例化。用户无需编写 XML 或配置类，
 * 只需在类上添加注解即可交由 IoC 容器管理。
 *
 * <p><b>启动流程（refresh）：</b>
 * <ol>
 *     <li><b>扫描：</b>通过 {@link ClassPathScanner} 扫描指定包下所有 {@code @Component} 类</li>
 *     <li><b>注册：</b>为每个类创建 {@link BeanDefinition}，注册到 {@link DefaultListableBeanFactory}</li>
 *     <li><b>预实例化：</b>调用 {@code getBean()} 触发所有单例 Bean 的提前创建</li>
 * </ol>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * ApplicationContext ctx = new AnnotationConfigApplicationContext("com.example");
 * UserService userService = ctx.getBean(UserService.class);
 * userService.doSomething();
 * }</pre>
 *
 * <p><b>当前限制：</b>Bean 的自动装配（{@code @Autowired}）尚未实现，
 * 后续版本将在 {@code refresh()} 中增加依赖注入步骤。
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/16
 * @see ApplicationContext
 * @see DefaultListableBeanFactory
 * @see ClassPathScanner
 */
public class AnnotationConfigApplicationContext implements ApplicationContext {

    /** 底层 Bean 工厂，实际承担 Bean 定义管理与实例创建职责 */
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    /**
     * 创建应用上下文并启动容器。
     *
     * <p>传入基础包路径后立即执行 {@link #refresh(String)} 完成扫描与预实例化。
     *
     * @param basePackage 待扫描的基础包路径，如 {@code "com.example"}
     * @throws InvocationTargetException 如果 Bean 构造器内部抛出异常
     * @throws NoSuchMethodException     如果 Bean 类缺少无参构造器
     * @throws InstantiationException    如果 Bean 类是抽象类或接口
     * @throws IllegalAccessException    如果无参构造器不可访问
     */
    public AnnotationConfigApplicationContext(String basePackage) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        refresh(basePackage);
    }

    /**
     * 容器刷新——执行扫描、注册、预实例化的完整启动流程。
     *
     * <p>该方法模拟了 Spring 中 {@code AbstractApplicationContext#refresh()} 的核心思路，
     * 但做了极大简化。后续版本将拆分为更细粒度的生命周期步骤。
     *
     * @param basePackage 待扫描的基础包路径
     */
    private void refresh(String basePackage) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // 步骤 1：类路径扫描
        ClassPathScanner classPathScanner = new ClassPathScanner();
        Set<Class<?>> classes = classPathScanner.scan(basePackage);

        // 步骤 2-3：注册 BeanDefinition 并预实例化所有单例
        for (Class<?> clazz : classes) {
            // Bean 名称：类名首字母小写（与 Spring 默认行为一致）
            String beanName = Introspector.decapitalize(clazz.getSimpleName());
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanName(beanName);
            beanDefinition.setBeanClass(clazz);
            beanFactory.registerBeanDefinition(beanName, beanDefinition);

            // getBean() 内部已完成实例化 + 单例注册，无需外层再次注册
            beanFactory.getBean(beanName);
        }
    }

    /**
     * 按名称获取 Bean 实例（委托给底层 BeanFactory）。
     */
    @Override
    public Object getBean(String beanName) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return beanFactory.getBean(beanName);
    }

    /**
     * 按类型获取 Bean 实例（委托给底层 BeanFactory）。
     */
    @Override
    public <T> T getBean(Class<T> requiredType) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return beanFactory.getBean(requiredType);
    }
}
