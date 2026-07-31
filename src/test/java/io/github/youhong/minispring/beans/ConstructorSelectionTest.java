package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Autowired;
import io.github.youhong.minispring.exception.BeanCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 构造器注入第二课：在基础选择契约之上增加 {@code @Autowired} 显式构造器选择。
 *
 * <p>第一课已覆盖“唯一构造器、无参回退、无规则时失败”三种基础分支。
 * 第二课新增：多构造器场景中，当恰好一个构造器标注了 {@code @Autowired} 时，
 * 容器应选择该构造器；多个构造器标注 {@code @Autowired} 时仍视为歧义。</p>
 */
class ConstructorSelectionTest {

    private DefaultListableBeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        beanFactory = new DefaultListableBeanFactory();
        UnselectedDependency.instanceCount.set(0);
        FirstAutowiredDependency.instanceCount.set(0);
        SecondAutowiredDependency.instanceCount.set(0);
    }

    @Test
    void shouldAllowAutowiredToDeclareConstructorInjectionPoint() {
        Target target = Autowired.class.getAnnotation(Target.class);

        assertTrue(
                Arrays.asList(target.value()).contains(ElementType.CONSTRUCTOR),
                "@Autowired should be applicable to constructors"
        );
    }

    @Test
    void shouldUseTheOnlyDeclaredConstructorWithoutAutowiredAnnotation() {
        registerBeanDefinition("orderRepository", OrderRepository.class);
        registerBeanDefinition("checkoutService", CheckoutService.class);

        CheckoutService checkoutService =
                beanFactory.getBean(CheckoutService.class);
        OrderRepository repository =
                beanFactory.getBean(OrderRepository.class);

        assertSame(repository, checkoutService.getOrderRepository());
    }

    @Test
    void shouldUseNoArgConstructorAsFallbackWhenSeveralConstructorsExist() {
        registerBeanDefinition("orderRepository", OrderRepository.class);
        registerBeanDefinition("fallbackCheckoutService", FallbackCheckoutService.class);

        FallbackCheckoutService service =
                beanFactory.getBean(FallbackCheckoutService.class);

        assertEquals("no-arg", service.getSelectedConstructor());
    }

    @Test
    void shouldReportAmbiguousConstructorsWhenNoSelectionRuleApplies() {
        registerBeanDefinition("orderRepository", OrderRepository.class);
        registerBeanDefinition("auditLog", AuditLog.class);
        registerBeanDefinition("ambiguousCheckoutService", AmbiguousCheckoutService.class);

        BeanCreationException exception = assertThrows(
                BeanCreationException.class,
                () -> beanFactory.getBean(AmbiguousCheckoutService.class)
        );

        assertEquals("ambiguousCheckoutService", exception.getBeanName());
        assertTrue(
                exception.getMessage().toLowerCase().contains("constructor"),
                "The failure should identify constructor selection as the cause"
        );
        assertTrue(
                exception.getMessage().toLowerCase().contains("ambiguous"),
                "The failure should explain that no unique constructor can be selected"
        );
    }

    @Test
    void shouldPreferAutowiredConstructorOverNoArgFallback() {
        registerBeanDefinition("orderRepository", OrderRepository.class);
        registerBeanDefinition(
                "autowiredOverNoArgCheckoutService",
                AutowiredOverNoArgCheckoutService.class
        );

        AutowiredOverNoArgCheckoutService service =
                beanFactory.getBean(AutowiredOverNoArgCheckoutService.class);
        OrderRepository repository =
                beanFactory.getBean(OrderRepository.class);

        assertEquals("autowired", service.getSelectedConstructor());
        assertSame(repository, service.getOrderRepository());
    }

    @Test
    void shouldSelectOnlyAutowiredConstructorAmongParameterizedConstructors() {
        registerBeanDefinition("unselectedDependency", UnselectedDependency.class);
        registerBeanDefinition("auditLog", AuditLog.class);
        registerBeanDefinition(
                "autowiredAmongParameterizedCheckoutService",
                AutowiredAmongParameterizedCheckoutService.class
        );

        AutowiredAmongParameterizedCheckoutService service =
                beanFactory.getBean(AutowiredAmongParameterizedCheckoutService.class);
        AuditLog auditLog = beanFactory.getBean(AuditLog.class);

        assertSame(auditLog, service.getAuditLog());
        assertEquals(0, UnselectedDependency.instanceCount.get());
    }

    @Test
    void shouldRejectMultipleAutowiredConstructorsBeforeResolvingArguments() {
        registerBeanDefinition(
                "firstAutowiredDependency",
                FirstAutowiredDependency.class
        );
        registerBeanDefinition(
                "secondAutowiredDependency",
                SecondAutowiredDependency.class
        );
        registerBeanDefinition(
                "conflictingAutowiredConstructorsService",
                ConflictingAutowiredConstructorsService.class
        );

        BeanCreationException exception = assertThrows(
                BeanCreationException.class,
                () -> beanFactory.getBean(ConflictingAutowiredConstructorsService.class)
        );

        assertEquals("conflictingAutowiredConstructorsService", exception.getBeanName());
        assertTrue(
                exception.getMessage().toLowerCase().contains("multiple"),
                "The failure should explain that multiple constructors were selected"
        );
        assertTrue(
                exception.getMessage().contains("@Autowired"),
                "The failure should identify @Autowired as the conflicting selection rule"
        );
        assertEquals(0, FirstAutowiredDependency.instanceCount.get());
        assertEquals(0, SecondAutowiredDependency.instanceCount.get());
    }

    private void registerBeanDefinition(String beanName, Class<?> beanClass) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setBeanClass(beanClass);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    static class OrderRepository {
    }

    static class AuditLog {
    }

    static class CheckoutService {

        private final OrderRepository orderRepository;

        CheckoutService(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;
        }

        OrderRepository getOrderRepository() {
            return orderRepository;
        }
    }

    static class FallbackCheckoutService {

        private final String selectedConstructor;

        FallbackCheckoutService() {
            this.selectedConstructor = "no-arg";
        }

        FallbackCheckoutService(OrderRepository orderRepository) {
            this.selectedConstructor = "dependency";
        }

        String getSelectedConstructor() {
            return selectedConstructor;
        }
    }

    static class AmbiguousCheckoutService {

        AmbiguousCheckoutService(OrderRepository orderRepository) {
        }

        AmbiguousCheckoutService(AuditLog auditLog) {
        }
    }

    static class AutowiredOverNoArgCheckoutService {

        private final String selectedConstructor;
        private final OrderRepository orderRepository;

        AutowiredOverNoArgCheckoutService() {
            this.selectedConstructor = "no-arg";
            this.orderRepository = null;
        }

        @Autowired
        AutowiredOverNoArgCheckoutService(OrderRepository orderRepository) {
            this.selectedConstructor = "autowired";
            this.orderRepository = orderRepository;
        }

        String getSelectedConstructor() {
            return selectedConstructor;
        }

        OrderRepository getOrderRepository() {
            return orderRepository;
        }
    }

    static class AutowiredAmongParameterizedCheckoutService {

        private final AuditLog auditLog;

        AutowiredAmongParameterizedCheckoutService(
                UnselectedDependency unselectedDependency) {
            this.auditLog = null;
        }

        @Autowired
        AutowiredAmongParameterizedCheckoutService(AuditLog auditLog) {
            this.auditLog = auditLog;
        }

        AuditLog getAuditLog() {
            return auditLog;
        }
    }

    static class UnselectedDependency {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        UnselectedDependency() {
            instanceCount.incrementAndGet();
        }
    }

    static class FirstAutowiredDependency {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        FirstAutowiredDependency() {
            instanceCount.incrementAndGet();
        }
    }

    static class SecondAutowiredDependency {

        private static final AtomicInteger instanceCount = new AtomicInteger();

        SecondAutowiredDependency() {
            instanceCount.incrementAndGet();
        }
    }

    static class ConflictingAutowiredConstructorsService {

        @Autowired
        ConflictingAutowiredConstructorsService(
                FirstAutowiredDependency firstAutowiredDependency) {
        }

        @Autowired
        ConflictingAutowiredConstructorsService(
                SecondAutowiredDependency secondAutowiredDependency) {
        }
    }
}
