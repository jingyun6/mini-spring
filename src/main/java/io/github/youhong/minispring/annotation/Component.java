package io.github.youhong.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件标记注解——将类声明为 Spring 管理的 Bean。
 *
 * <p>被 {@code @Component} 标注的类会被 {@link io.github.youhong.minispring.scanner.ClassPathScanner}
 * 在类路径扫描阶段自动发现，并注册为 IoC 容器中的 Bean 定义。
 *
 * <p>该注解作用于类型（Type）级别，运行时保留，是 mini-spring 实现
 * "约定优于配置"（Convention over Configuration）的核心机制。
 *
 * <p><b>Bean 命名规则：</b>默认使用类名首字母小写作为 Bean 名称。
 * 例如 {@code UserService} → {@code "userService"}。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * @Component
 * public class UserService {
 *     public void load() {
 *         System.out.println("loading...");
 *     }
 * }
 * }</pre>
 *
 * @author YouHong
 * @see Autowired
 * @see io.github.youhong.minispring.scanner.ClassPathScanner
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}
