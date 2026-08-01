package io.github.youhong.minispring.testfixture.primary;

import io.github.youhong.minispring.annotation.Component;
import io.github.youhong.minispring.annotation.Primary;

/** 通过 {@code @Primary} 声明的默认支付网关候选。 */
@Primary
@Component
public class PreferredPaymentGateway implements PaymentGateway {
}
