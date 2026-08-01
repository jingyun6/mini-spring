package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Qualifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@code @Qualifier} 第三课：构造器参数注入点的精确候选选择。
 *
 * <p>测试使用唯一构造器，让构造器选择规则保持不变，只观察参数解析行为：参数上的
 * 显式 qualifier 应优先于 primary；没有 qualifier 时仍沿用按类型和 primary 元数据
 * 选择候选的现有行为。{@code @Qualifier} 只修饰依赖参数，不负责选择构造器。</p>
 */
class ConstructorParameterQualifierInjectionTest {

    private DefaultListableBeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        beanFactory = new DefaultListableBeanFactory();
        StandardPaymentGateway.instanceCount.set(0);
        PreferredPaymentGateway.instanceCount.set(0);
    }

    @Test
    void shouldLetConstructorParameterQualifierOverridePrimaryCandidate() {
        registerPaymentGateways();
        registerBeanDefinition("qualifiedCheckoutService", QualifiedCheckoutService.class);

        QualifiedCheckoutService checkoutService =
                beanFactory.getBean(QualifiedCheckoutService.class);

        assertInstanceOf(
                StandardPaymentGateway.class,
                checkoutService.getPaymentGateway()
        );
        assertSame(
                beanFactory.getBean("standardPaymentGateway"),
                checkoutService.getPaymentGateway()
        );
        assertEquals(1, StandardPaymentGateway.instanceCount.get());
        assertEquals(
                0,
                PreferredPaymentGateway.instanceCount.get(),
                "The unselected primary candidate should not be instantiated"
        );
    }

    @Test
    void shouldKeepUsingPrimaryForUnqualifiedConstructorParameter() {
        registerPaymentGateways();
        registerBeanDefinition("unqualifiedCheckoutService", UnqualifiedCheckoutService.class);

        UnqualifiedCheckoutService checkoutService =
                beanFactory.getBean(UnqualifiedCheckoutService.class);

        assertInstanceOf(
                PreferredPaymentGateway.class,
                checkoutService.getPaymentGateway()
        );
        assertSame(
                beanFactory.getBean("preferredPaymentGateway"),
                checkoutService.getPaymentGateway()
        );
    }

    private void registerPaymentGateways() {
        registerBeanDefinition("standardPaymentGateway", StandardPaymentGateway.class);
        BeanDefinition preferred = registerBeanDefinition(
                "preferredPaymentGateway",
                PreferredPaymentGateway.class
        );
        preferred.setPrimary(true);
    }

    private BeanDefinition registerBeanDefinition(
            String beanName,
            Class<?> beanClass) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setBeanClass(beanClass);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return beanDefinition;
    }

    private interface PaymentGateway {
    }

    static class StandardPaymentGateway implements PaymentGateway {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        StandardPaymentGateway() {
            instanceCount.incrementAndGet();
        }
    }

    static class PreferredPaymentGateway implements PaymentGateway {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        PreferredPaymentGateway() {
            instanceCount.incrementAndGet();
        }
    }

    static class QualifiedCheckoutService {

        private final PaymentGateway paymentGateway;

        QualifiedCheckoutService(
                @Qualifier("standardPaymentGateway") PaymentGateway paymentGateway) {
            this.paymentGateway = paymentGateway;
        }

        PaymentGateway getPaymentGateway() {
            return paymentGateway;
        }
    }

    static class UnqualifiedCheckoutService {

        private final PaymentGateway paymentGateway;

        UnqualifiedCheckoutService(PaymentGateway paymentGateway) {
            this.paymentGateway = paymentGateway;
        }

        PaymentGateway getPaymentGateway() {
            return paymentGateway;
        }
    }
}
