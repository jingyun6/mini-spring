package io.github.youhong.minispring.context;

import io.github.youhong.minispring.beans.BeanDefinition;
import io.github.youhong.minispring.beans.DefaultListableBeanFactory;
import io.github.youhong.minispring.exception.BeansException;
import io.github.youhong.minispring.scanner.ClassPathScanner;

import java.beans.Introspector;
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
 * <p><b>当前限制：</b>容器支持基于类型的 {@code @Autowired} 字段注入、唯一构造器的
 * 隐式注入和多构造器中的显式选择。BeanFactory 已支持 primary 元数据，但上下文扫描
 * 尚未把组件类上的 {@code @Primary} 映射到 BeanDefinition，也不支持 {@code @Qualifier}；
 * 循环依赖能够被检测并拒绝，但尚不能通过早期 Bean 引用解决。
 *
 * @author YouHong5286
 * @version 1.0.0
 * @see ApplicationContext
 * @see DefaultListableBeanFactory
 * @see ClassPathScanner
 * @since 2026/7/16
 */
public class AnnotationConfigApplicationContext implements ApplicationContext {

    /**
     * 底层 Bean 工厂，负责保存 BeanDefinition、创建 Bean 并管理单例实例。
     * ApplicationContext 本身只负责组织容器的启动流程，并将 Bean 查询操作委托给该工厂。
     */
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    /**
     * 创建应用上下文并启动容器。
     *
     * <p>传入基础包路径后立即执行 {@link #refresh(String)} 完成扫描与预实例化。
     *
     * @param basePackage 待扫描的基础包路径，如 {@code "com.example"}
     * @throws BeansException 如果 BeanDefinition 查找、Bean 创建或依赖注入失败
     */
    public AnnotationConfigApplicationContext(String basePackage) {
        refresh(basePackage);
    }

    /**
     * 容器刷新——执行扫描、注册、预实例化的完整启动流程。
     *
     * <p>该方法模拟 Spring 中 {@code AbstractApplicationContext#refresh()} 的流程编排思想，
     * 先完成全部 BeanDefinition 的扫描和注册，再统一预实例化单例 Bean。两个阶段必须分离，
     * 从而避免 Bean 的创建结果依赖类路径扫描顺序。</p>
     *
     * @param basePackage 待扫描的基础包路径
     */
    private void refresh(String basePackage) {
        scanBeanDefinitions(basePackage);
        preInstantiateSingletons();
    }

    /**
     * 扫描指定包下的组件，并将扫描结果转换为 BeanDefinition 注册到 BeanFactory。
     *
     * <p>该阶段只处理 Bean 元数据，不创建任何 Bean 实例。完成全部定义注册后，
     * {@link #preInstantiateSingletons()} 才会进入实例创建阶段。</p>
     *
     * @param basePackage 待扫描的基础包路径
     */
    private void scanBeanDefinitions(String basePackage) {
        ClassPathScanner classPathScanner = new ClassPathScanner();
        Set<Class<?>> classes = classPathScanner.scan(basePackage);

        for (Class<?> clazz : classes) {
            // 使用 JavaBeans 命名规则生成默认 Bean 名称，例如 UserService -> userService。
            String beanName = Introspector.decapitalize(clazz.getSimpleName());
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanName(beanName);
            beanDefinition.setBeanClass(clazz);
            beanFactory.registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * 提前创建当前容器中的全部单例 Bean。
     *
     * <p>该方法遍历已经完成注册的 BeanDefinition，仅对单例定义调用
     * {@link DefaultListableBeanFactory#getBean(String)}。统一通过 getBean 创建对象，
     * 可以复用单例缓存查询、BeanDefinition 查找和实例注册流程。</p>
     *
     * <p>当前尚未实现懒加载，因此所有单例都会在 ApplicationContext 启动时创建。</p>
     */
    private void preInstantiateSingletons() {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            if (beanDefinition.isSingleton()) {
                beanFactory.getBean(beanDefinitionName);
            }
        }
    }

    /**
     * 按名称获取 Bean 实例（委托给底层 BeanFactory）。
     *
     * @throws BeansException 如果 Bean 查找或创建失败
     */
    @Override
    public Object getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }

    /**
     * 按类型获取 Bean 实例（委托给底层 BeanFactory）。
     *
     * @throws BeansException 如果 Bean 解析或创建失败
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }
}
