package io.github.youhong.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动装配注解——标记需要由 IoC 容器解析依赖的字段或构造器。
 *
 * <p>标注字段时，容器会在 Bean 实例化后查找唯一匹配类型并写入字段；标注构造器时，
 * 该注解用于表达显式的构造器注入意图。唯一构造器可以隐式注入；存在多个构造器时，
 * 恰好一个 {@code @Autowired} 构造器会覆盖无参回退。多个构造器同时标注该注解时，
 * 容器会因无法确定唯一实例化入口而拒绝创建 Bean。</p>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * @Component
 * public class OrderService {
 *     @Autowired
 *     private UserService userService;  // 容器自动注入
 *
 *     public OrderService(OrderRepository orderRepository) {
 *         // 唯一构造器无需显式标注 @Autowired
 *     }
 * }
 * }</pre>
 *
 * <p>字段和构造器参数未标注 {@link Qualifier @Qualifier} 时按类型解析，标注后按指定
 * Bean 名称精确解析；显式 qualifier 优先于 primary 默认候选。两类注入点最终都复用
 * BeanFactory 的创建、单例缓存和循环依赖检测；同时仍不支持方法注入，也尚未通过
 * 早期 Bean 引用解决循环依赖。</p>
 *
 * @author YouHong
 * @see Component
 * @since 1.0
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
