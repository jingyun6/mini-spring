package io.github.youhong.minispring.testfixture.primary;

import io.github.youhong.minispring.annotation.Component;

/** 通过唯一构造器依赖多候选支付网关的测试组件。 */
@Component
public class CheckoutService {

    private final PaymentGateway paymentGateway;

    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }
}
