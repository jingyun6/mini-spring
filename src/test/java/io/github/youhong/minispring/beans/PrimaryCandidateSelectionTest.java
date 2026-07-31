package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Primary;
import io.github.youhong.minispring.exception.NoUniqueBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code @Primary} 第一课：定义注解、BeanDefinition 元数据和按类型选择契约。
 *
 * <p>本课手动设置 BeanDefinition 元数据，刻意绕过组件扫描。BeanFactory 只消费
 * 结构化元数据，不直接读取组件类上的注解；注解到元数据的映射留给下一课。</p>
 */
class PrimaryCandidateSelectionTest {

    private DefaultListableBeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        beanFactory = new DefaultListableBeanFactory();
        StandardPaymentService.instanceCount.set(0);
        PreferredPaymentService.instanceCount.set(0);
        BackupPaymentService.instanceCount.set(0);
    }

    @Test
    void shouldDeclarePrimaryAsRuntimeTypeAnnotation() {
        Class<Primary> primaryAnnotation = Primary.class;

        assertTrue(primaryAnnotation.isAnnotation());

        Target target = primaryAnnotation.getAnnotation(Target.class);
        assertNotNull(target);
        assertTrue(Arrays.asList(target.value()).contains(ElementType.TYPE));

        Retention retention = primaryAnnotation.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldStorePrimaryFlagInBeanDefinitionWithFalseDefault() {
        BeanDefinition beanDefinition =
                createBeanDefinition("preferredPaymentService", PreferredPaymentService.class);

        assertFalse(beanDefinition.isPrimary());

        beanDefinition.setPrimary(true);

        assertTrue(beanDefinition.isPrimary());
    }

    @Test
    void shouldSelectOnlyPrimaryCandidateWithoutCreatingOthers() {
        registerBeanDefinition("standardPaymentService", StandardPaymentService.class);
        BeanDefinition preferred = registerBeanDefinition(
                "preferredPaymentService",
                PreferredPaymentService.class
        );
        preferred.setPrimary(true);

        PaymentService paymentService = beanFactory.getBean(PaymentService.class);

        assertInstanceOf(PreferredPaymentService.class, paymentService);
        assertSame(paymentService, beanFactory.getBean("preferredPaymentService"));
        assertEquals(0, StandardPaymentService.instanceCount.get());
        assertEquals(1, PreferredPaymentService.instanceCount.get());
    }

    @Test
    void shouldRejectSeveralPrimaryCandidatesWithoutCreatingAnyCandidate() {
        registerBeanDefinition("standardPaymentService", StandardPaymentService.class);
        BeanDefinition preferred = registerBeanDefinition(
                "preferredPaymentService",
                PreferredPaymentService.class
        );
        BeanDefinition backup = registerBeanDefinition(
                "backupPaymentService",
                BackupPaymentService.class
        );
        preferred.setPrimary(true);
        backup.setPrimary(true);

        NoUniqueBeanDefinitionException exception = assertThrows(
                NoUniqueBeanDefinitionException.class,
                () -> beanFactory.getBean(PaymentService.class)
        );

        assertSame(PaymentService.class, exception.getRequiredType());
        assertEquals(
                List.of("backupPaymentService", "preferredPaymentService"),
                exception.getBeanNames()
        );
        assertTrue(
                exception.getMessage().contains("primary"),
                "The failure should identify multiple primary candidates as the conflict"
        );
        assertEquals(0, StandardPaymentService.instanceCount.get());
        assertEquals(0, PreferredPaymentService.instanceCount.get());
        assertEquals(0, BackupPaymentService.instanceCount.get());
    }

    private BeanDefinition registerBeanDefinition(
            String beanName,
            Class<?> beanClass) {
        BeanDefinition beanDefinition = createBeanDefinition(beanName, beanClass);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        return beanDefinition;
    }

    private BeanDefinition createBeanDefinition(
            String beanName,
            Class<?> beanClass) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setBeanClass(beanClass);
        return beanDefinition;
    }

    private interface PaymentService {
    }

    static class StandardPaymentService implements PaymentService {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        StandardPaymentService() {
            instanceCount.incrementAndGet();
        }
    }

    static class PreferredPaymentService implements PaymentService {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        PreferredPaymentService() {
            instanceCount.incrementAndGet();
        }
    }

    static class BackupPaymentService implements PaymentService {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        BackupPaymentService() {
            instanceCount.incrementAndGet();
        }
    }
}
