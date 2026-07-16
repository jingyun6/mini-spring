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
 * <p>当前版本中该注解已定义但依赖注入逻辑尚未实现，属于预留扩展点。
 *
 * @author YouHong
 * @see Component
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
