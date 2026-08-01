package io.github.youhong.minispring.context;

import io.github.youhong.minispring.testfixture.primary.CheckoutService;
import io.github.youhong.minispring.testfixture.primary.PaymentGateway;
import io.github.youhong.minispring.testfixture.primary.PreferredPaymentGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@code @Primary} 扫描集成契约。
 *
 * <p>本课不再手动设置 BeanDefinition，而是从组件类上的 {@code @Primary}
 * 出发，验证 ApplicationContext 能把注解转换为元数据，并交给 BeanFactory
 * 完成按类型候选选择。</p>
 */
class PrimaryComponentScanningTest {

    private static final String BASE_PACKAGE =
            "io.github.youhong.minispring.testfixture.primary";

    @Test
    void shouldSelectPrimaryComponentAfterScanningAnnotationMetadata() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(BASE_PACKAGE);

        PaymentGateway paymentGateway = context.getBean(PaymentGateway.class);

        assertInstanceOf(PreferredPaymentGateway.class, paymentGateway);
        assertSame(
                context.getBean(PreferredPaymentGateway.class),
                paymentGateway
        );
    }

    @Test
    void shouldInjectScannedPrimaryComponentIntoConstructorDependency() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(BASE_PACKAGE);

        CheckoutService checkoutService = context.getBean(CheckoutService.class);
        PaymentGateway primaryPaymentGateway =
                context.getBean(PaymentGateway.class);

        assertSame(
                primaryPaymentGateway,
                checkoutService.getPaymentGateway()
        );
    }
}
