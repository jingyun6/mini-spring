package io.github.youhong.minispring.context;

import io.github.youhong.minispring.beans.BeanFactory;

/**
 * Spring 应用上下文——IoC 容器的门面接口。
 *
 * <p>{@code ApplicationContext} 继承自 {@link BeanFactory}，在 Bean 工厂的基础能力之上，
 * 提供了更高级的应用级功能。它是整个 mini-spring 框架对外暴露的核心入口，
 * 客户端通过该接口获取已装配完成的 Bean 实例。
 *
 * <p><b>与 BeanFactory 的区别：</b>
 * <ul>
 *     <li>{@code BeanFactory} 仅提供基础的 Bean 获取能力，采用懒加载策略</li>
 *     <li>{@code ApplicationContext} 在启动时即完成所有单例 Bean 的预实例化（ eager initialization），
 *         并提供注解扫描、自动装配等企业级功能</li>
 * </ul>
 *
 * <p><b>典型使用方式：</b>
 * <pre>{@code
 * ApplicationContext ctx = new AnnotationConfigApplicationContext("com.example");
 * UserService userService = ctx.getBean(UserService.class);
 * }</pre>
 *
 * <p>当前版本中该接口作为标记接口存在，后续版本将扩展以下能力：
 * <ul>
 *     <li>环境配置（Environment）</li>
 *     <li>事件发布（Event Publishing）</li>
 *     <li>国际化消息（MessageSource）</li>
 *     <li>资源加载（ResourceLoader）</li>
 * </ul>
 *
 * @author YouHong5286
 * @see BeanFactory
 * @see AnnotationConfigApplicationContext
 * @since 1.0
 */
public interface ApplicationContext extends BeanFactory {

}
