package io.github.youhong.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动装配注解——标记需要由 IoC 容器注入依赖的字段。
 *
 * <p>该注解作用于字段（Field）级别，在 Bean 实例化后的依赖注入阶段，
 * 容器会扫描被 {@code @Autowired} 标记的字段，并自动将容器中匹配类型的
 * Bean 注入其中。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * @Component
 * public class OrderService {
 *     @Autowired
 *     private UserService userService;  // 容器自动注入
 * }
 * }</pre>
 *
 * <p>当前版本支持按照字段类型完成自动装配。容器会通过 BeanFactory 查找唯一匹配的
 * Bean 并写入字段；暂不支持限定符、构造器注入、方法注入和循环依赖。</p>
 *
 * @author YouHong
 * @see Component
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
