package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Autowired;
import io.github.youhong.minispring.exception.BeanCreationException;
import io.github.youhong.minispring.exception.NoSuchBeanDefinitionException;
import io.github.youhong.minispring.exception.NoUniqueBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DefaultListableBeanFactory} 按类型查找 Bean 的单元测试。
 */
class DefaultListableBeanFactoryTest {

    private DefaultListableBeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        beanFactory = new DefaultListableBeanFactory();
        WechatPaymentService.instanceCount = 0;
        AlipayPaymentService.instanceCount = 0;
        ConcurrentSingleton.reset();
    }

    @Test
    void shouldRejectNullRequiredType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> beanFactory.getBean((Class<Object>) null)
        );

        assertEquals("requiredType must not be null", exception.getMessage());
    }

    @Test
    void shouldThrowNoSuchBeanDefinitionExceptionWhenNoCandidateExists() {
        NoSuchBeanDefinitionException exception = assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> beanFactory.getBean(PaymentService.class)
        );

        assertSame(PaymentService.class, exception.getRequiredType());
        assertEquals(0, WechatPaymentService.instanceCount);
        assertEquals(0, AlipayPaymentService.instanceCount);
    }

    @Test
    void shouldReturnUniqueCandidateByInterfaceType() {
        registerBeanDefinition("wechatPaymentService", WechatPaymentService.class);

        PaymentService paymentService = beanFactory.getBean(PaymentService.class);

        assertInstanceOf(WechatPaymentService.class, paymentService);
        assertSame(paymentService, beanFactory.getBean(PaymentService.class));
        assertEquals(1, WechatPaymentService.instanceCount);
    }

    @Test
    void shouldThrowNoUniqueBeanDefinitionExceptionWithoutCreatingCandidates() {
        registerBeanDefinition("wechatPaymentService", WechatPaymentService.class);
        registerBeanDefinition("alipayPaymentService", AlipayPaymentService.class);

        NoUniqueBeanDefinitionException exception = assertThrows(
                NoUniqueBeanDefinitionException.class,
                () -> beanFactory.getBean(PaymentService.class)
        );

        assertSame(PaymentService.class, exception.getRequiredType());
        assertEquals(2, exception.getNumberOfBeansFound());
        assertEquals(
                List.of("alipayPaymentService", "wechatPaymentService"),
                exception.getBeanNames()
        );
        assertEquals(0, WechatPaymentService.instanceCount);
        assertEquals(0, AlipayPaymentService.instanceCount);
    }

    @Test
    void shouldResolveConcreteTypeEvenWhenInterfaceHasMultipleCandidates() {
        registerBeanDefinition("wechatPaymentService", WechatPaymentService.class);
        registerBeanDefinition("alipayPaymentService", AlipayPaymentService.class);

        WechatPaymentService paymentService =
                beanFactory.getBean(WechatPaymentService.class);

        assertSame(paymentService, beanFactory.getBean("wechatPaymentService"));
        assertEquals(1, WechatPaymentService.instanceCount);
        assertEquals(0, AlipayPaymentService.instanceCount);
    }

    @Test
    void shouldDetectCircularDependencyAndReportCreationPath() {
        registerBeanDefinition("circularA", CircularA.class);
        registerBeanDefinition("circularB", CircularB.class);

        BeanCreationException exception = assertThrows(
                BeanCreationException.class,
                () -> beanFactory.getBean(CircularA.class)
        );

        assertEquals("circularA", exception.getBeanName());
        assertTrue(
                exception.getMessage().contains(
                        "circularA -> circularB -> circularA"
                )
        );
    }

    @Test
    void shouldDetectDirectSelfDependency() {
        registerBeanDefinition("selfDependentBean", SelfDependentBean.class);

        BeanCreationException exception = assertThrows(
                BeanCreationException.class,
                () -> beanFactory.getBean(SelfDependentBean.class)
        );

        assertEquals("selfDependentBean", exception.getBeanName());
        assertTrue(
                exception.getMessage().contains(
                        "selfDependentBean -> selfDependentBean"
                )
        );
    }

    @Test
    void shouldClearCreationPathAfterFailedDependencyResolution() {
        registerBeanDefinition("lateBoundConsumer", LateBoundConsumer.class);

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> beanFactory.getBean(LateBoundConsumer.class)
        );

        registerBeanDefinition("lateBoundDependency", LateBoundDependency.class);

        LateBoundConsumer consumer =
                beanFactory.getBean(LateBoundConsumer.class);
        LateBoundDependency dependency =
                beanFactory.getBean(LateBoundDependency.class);

        assertSame(dependency, consumer.getDependency());
    }

    @Test
    void shouldRegisterAndRetrieveBeanDefinition() {
        BeanDefinition beanDefinition =
                createBeanDefinition("wechatPaymentService", WechatPaymentService.class);

        beanFactory.registerBeanDefinition("wechatPaymentService", beanDefinition);

        assertTrue(beanFactory.containsBeanDefinition("wechatPaymentService"));
        assertSame(
                beanDefinition,
                beanFactory.getBeanDefinition("wechatPaymentService")
        );
        assertArrayEquals(
                new String[]{"wechatPaymentService"},
                beanFactory.getBeanDefinitionNames()
        );
    }

    @Test
    void shouldRejectNullBeanNameWhenRegisteringBeanDefinition() {
        BeanDefinition beanDefinition =
                createBeanDefinition("wechatPaymentService", WechatPaymentService.class);

        assertThrows(
                IllegalArgumentException.class,
                () -> beanFactory.registerBeanDefinition(null, beanDefinition)
        );
    }

    @Test
    void shouldRejectNullBeanDefinitionWhenRegisteringBeanDefinition() {
        assertThrows(
                IllegalArgumentException.class,
                () -> beanFactory.registerBeanDefinition("wechatPaymentService", null)
        );
    }

    @Test
    void shouldRejectDuplicateBeanNameAndPreserveOriginalDefinition() {
        BeanDefinition original =
                createBeanDefinition("paymentService", WechatPaymentService.class);
        BeanDefinition replacement =
                createBeanDefinition("paymentService", AlipayPaymentService.class);
        beanFactory.registerBeanDefinition("paymentService", original);

        assertThrows(
                IllegalStateException.class,
                () -> beanFactory.registerBeanDefinition("paymentService", replacement)
        );
        assertSame(original, beanFactory.getBeanDefinition("paymentService"));
        assertArrayEquals(
                new String[]{"paymentService"},
                beanFactory.getBeanDefinitionNames()
        );
    }

    @Test
    void shouldReturnDefensiveCopyOfBeanDefinitionNames() {
        registerBeanDefinition("wechatPaymentService", WechatPaymentService.class);
        registerBeanDefinition("alipayPaymentService", AlipayPaymentService.class);

        String[] returnedNames = beanFactory.getBeanDefinitionNames();
        returnedNames[0] = "modifiedByCaller";

        assertArrayEquals(
                new String[]{"wechatPaymentService", "alipayPaymentService"},
                beanFactory.getBeanDefinitionNames()
        );
    }

    @Test
    void shouldKeepDefinitionsAndNamesConsistentDuringConcurrentRegistration()
            throws Exception {

        int threadCount = 16;
        int definitionCount = 256;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < definitionCount; i++) {
                String beanName = "paymentService" + i;
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    await(start);
                    registerBeanDefinition(beanName, WechatPaymentService.class);
                }));
            }

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            await(futures);
        } finally {
            executor.shutdownNow();
        }

        String[] registeredNames = beanFactory.getBeanDefinitionNames();
        assertEquals(definitionCount, registeredNames.length);
        assertEquals(
                definitionCount,
                new HashSet<>(Arrays.asList(registeredNames)).size()
        );
        for (int i = 0; i < definitionCount; i++) {
            assertTrue(beanFactory.containsBeanDefinition("paymentService" + i));
        }
    }

    @Test
    void shouldAllowOnlyOneConcurrentRegistrationForTheSameBeanName()
            throws Exception {

        int threadCount = 32;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger rejectionCount = new AtomicInteger();
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    await(start);
                    try {
                        registerBeanDefinition(
                                "paymentService",
                                WechatPaymentService.class
                        );
                        successCount.incrementAndGet();
                    } catch (IllegalStateException exception) {
                        rejectionCount.incrementAndGet();
                    }
                }));
            }

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            await(futures);
        } finally {
            executor.shutdownNow();
        }

        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, rejectionCount.get());
        assertArrayEquals(
                new String[]{"paymentService"},
                beanFactory.getBeanDefinitionNames()
        );
    }

    @Test
    void shouldCreateSingletonOnlyOnceWhenRequestedConcurrently()
            throws Exception {

        int threadCount = 16;
        registerBeanDefinition("concurrentSingleton", ConcurrentSingleton.class);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    await(start);
                    return beanFactory.getBean(ConcurrentSingleton.class);
                }));
            }

            assertTrue(ready.await(5, TimeUnit.SECONDS));
            start.countDown();
            assertTrue(
                    ConcurrentSingleton.firstConstructorEntered.await(
                            5,
                            TimeUnit.SECONDS
                    )
            );

            // 给其他并发请求进入构造流程的机会。正确实现中它们应等待首个 Bean 创建完成。
            ConcurrentSingleton.secondConstructorEntered.await(
                    500,
                    TimeUnit.MILLISECONDS
            );
        } finally {
            ConcurrentSingleton.allowConstructionToFinish.countDown();
        }

        try {
            Object expectedSingleton = futures.getFirst().get(10, TimeUnit.SECONDS);
            for (Future<?> future : futures) {
                assertSame(
                        expectedSingleton,
                        future.get(10, TimeUnit.SECONDS)
                );
            }
            assertEquals(1, ConcurrentSingleton.instanceCount.get());
        } finally {
            executor.shutdownNow();
        }
    }

    private void registerBeanDefinition(String beanName, Class<?> beanClass) {
        beanFactory.registerBeanDefinition(
                beanName,
                createBeanDefinition(beanName, beanClass)
        );
    }

    private BeanDefinition createBeanDefinition(
            String beanName,
            Class<?> beanClass) {

        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanName(beanName);
        beanDefinition.setBeanClass(beanClass);
        return beanDefinition;
    }

    private static void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting to start concurrent test", exception);
        }
    }

    private static void await(List<Future<?>> futures) throws Exception {
        for (Future<?> future : futures) {
            future.get(10, TimeUnit.SECONDS);
        }
    }

    private interface PaymentService {
    }

    public static class WechatPaymentService implements PaymentService {

        private static int instanceCount;

        public WechatPaymentService() {
            instanceCount++;
        }
    }

    public static class AlipayPaymentService implements PaymentService {

        private static int instanceCount;

        public AlipayPaymentService() {
            instanceCount++;
        }
    }

    public static class CircularA {

        @Autowired
        private CircularB circularB;
    }

    public static class CircularB {

        @Autowired
        private CircularA circularA;
    }

    public static class SelfDependentBean {

        @Autowired
        private SelfDependentBean self;
    }

    public static class LateBoundConsumer {

        @Autowired
        private LateBoundDependency dependency;

        public LateBoundDependency getDependency() {
            return dependency;
        }
    }

    public static class LateBoundDependency {
    }

    public static class ConcurrentSingleton {

        private static final AtomicInteger instanceCount = new AtomicInteger();
        private static CountDownLatch firstConstructorEntered;
        private static CountDownLatch secondConstructorEntered;
        private static CountDownLatch allowConstructionToFinish;

        static void reset() {
            instanceCount.set(0);
            firstConstructorEntered = new CountDownLatch(1);
            secondConstructorEntered = new CountDownLatch(1);
            allowConstructionToFinish = new CountDownLatch(1);
        }

        public ConcurrentSingleton() {
            int currentInstanceCount = instanceCount.incrementAndGet();
            if (currentInstanceCount == 1) {
                firstConstructorEntered.countDown();
            }
            if (currentInstanceCount == 2) {
                secondConstructorEntered.countDown();
            }
            await(allowConstructionToFinish);
        }
    }

}
