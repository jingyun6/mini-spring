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
 * 依赖选择整合课：记录字段与构造器参数必须遵循的共同解析语义。
 *
 * <p>本课属于受测试保护的结构重构，因此新增测试从绿灯开始：先证明两个注入入口
 * 已经具有一致的外部行为，再把重复的 qualifier/type 分支收敛到统一依赖描述和
 * 解析入口。重构前后，这些可观察行为都不能改变。</p>
 */
class DependencyResolutionConsistencyTest {

    private DefaultListableBeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        beanFactory = new DefaultListableBeanFactory();
        StandardPaymentGateway.instanceCount.set(0);
        PreferredPaymentGateway.instanceCount.set(0);
    }

    @Test
    void shouldResolveQualifiedFieldAndConstructorParameterToSameNamedSingleton() {
        registerPaymentGateways();
        registerBeanDefinition("qualifiedCheckoutService", QualifiedCheckoutService.class);

        QualifiedCheckoutService checkoutService =
                beanFactory.getBean(QualifiedCheckoutService.class);

        assertInstanceOf(
                StandardPaymentGateway.class,
                checkoutService.getConstructorGateway()
        );
        assertSame(
                checkoutService.getConstructorGateway(),
                checkoutService.getFieldGateway()
        );
        assertSame(
                beanFactory.getBean("standardPaymentGateway"),
                checkoutService.getFieldGateway()
        );
        assertEquals(1, StandardPaymentGateway.instanceCount.get());
        assertEquals(0, PreferredPaymentGateway.instanceCount.get());
    }

    @Test
    void shouldResolveUnqualifiedFieldAndConstructorParameterToSamePrimarySingleton() {
        registerPaymentGateways();
        registerBeanDefinition("unqualifiedCheckoutService", UnqualifiedCheckoutService.class);

        UnqualifiedCheckoutService checkoutService =
                beanFactory.getBean(UnqualifiedCheckoutService.class);

        assertInstanceOf(
                PreferredPaymentGateway.class,
                checkoutService.getConstructorGateway()
        );
        assertSame(
                checkoutService.getConstructorGateway(),
                checkoutService.getFieldGateway()
        );
        assertSame(
                beanFactory.getBean("preferredPaymentGateway"),
                checkoutService.getFieldGateway()
        );
        assertEquals(0, StandardPaymentGateway.instanceCount.get());
        assertEquals(1, PreferredPaymentGateway.instanceCount.get());
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

        private final PaymentGateway constructorGateway;

        @Autowired
        @Qualifier("standardPaymentGateway")
        private PaymentGateway fieldGateway;

        QualifiedCheckoutService(
                @Qualifier("standardPaymentGateway") PaymentGateway constructorGateway) {
            this.constructorGateway = constructorGateway;
        }

        PaymentGateway getConstructorGateway() {
            return constructorGateway;
        }

        PaymentGateway getFieldGateway() {
            return fieldGateway;
        }
    }

    static class UnqualifiedCheckoutService {

        private final PaymentGateway constructorGateway;

        @Autowired
        private PaymentGateway fieldGateway;

        UnqualifiedCheckoutService(PaymentGateway constructorGateway) {
            this.constructorGateway = constructorGateway;
        }

        PaymentGateway getConstructorGateway() {
            return constructorGateway;
        }

        PaymentGateway getFieldGateway() {
            return fieldGateway;
        }
    }
}
