package io.github.youhong.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在依赖注入点上指定需要的 Bean 名称。
 *
 * <p>{@code @Primary} 表达多候选场景的全局默认选择，而 {@code @Qualifier}
 * 表达当前字段或构造器参数的局部显式选择。字段依赖解析已经遵循
 * 显式 qualifier 优先于 primary 默认候选的规则。</p>
 *
 * <pre>{@code
 * @Autowired
 * @Qualifier("alipayPaymentGateway")
 * private PaymentGateway paymentGateway;
 *
 * CheckoutService(
 *         @Qualifier("alipayPaymentGateway") PaymentGateway paymentGateway) {
 *     this.paymentGateway = paymentGateway;
 * }
 * }</pre>
 *
 * <p>{@link #value()} 没有默认值，使用时必须显式声明 Bean 名称。当前字段注入
 * 已支持按该名称精确选择候选；构造器参数的候选解析将在后续课程接入。</p>
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {

    /**
     * 返回当前注入点要求的 Bean 名称。
     *
     * @return 必须显式指定的 Bean 名称
     */
    String value();
}
