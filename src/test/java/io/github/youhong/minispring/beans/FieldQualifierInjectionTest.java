package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Autowired;
import io.github.youhong.minispring.annotation.Qualifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@code @Qualifier} 第二课：字段注入点的精确候选选择。
 *
 * <p>本课直接向 BeanFactory 注册 BeanDefinition，排除组件扫描和上下文预实例化的影响，
 * 只验证字段依赖解析规则：显式 qualifier 优先于 primary；没有 qualifier 时仍沿用
 * 按类型和 primary 元数据选择候选的现有行为。</p>
 */
class FieldQualifierInjectionTest {

    private DefaultListableBeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        beanFactory = new DefaultListableBeanFactory();
        StandardPaymentGateway.instanceCount.set(0);
        PreferredPaymentGateway.instanceCount.set(0);
    }

    @Test
    void shouldLetFieldQualifierOverridePrimaryCandidate() {
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
    void shouldKeepUsingPrimaryForUnqualifiedAutowiredField() {
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

        @Autowired
        @Qualifier("standardPaymentGateway")
        private PaymentGateway paymentGateway;

        PaymentGateway getPaymentGateway() {
            return paymentGateway;
        }
    }

    static class UnqualifiedCheckoutService {

        @Autowired
        private PaymentGateway paymentGateway;

        PaymentGateway getPaymentGateway() {
            return paymentGateway;
        }
    }
}
