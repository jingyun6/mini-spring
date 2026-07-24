package io.github.youhong.minispring.testfixture;

import io.github.youhong.minispring.annotation.Component;

/**
 * 用于验证父类 {@code @Autowired} 字段注入的测试组件。
 */
@Component
public class InheritedOrderService extends BaseOrderService {
}
