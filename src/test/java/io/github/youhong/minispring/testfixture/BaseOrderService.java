package io.github.youhong.minispring.testfixture;

import io.github.youhong.minispring.annotation.Autowired;

/**
 * 继承字段注入测试的抽象父类。
 *
 * <p>依赖字段故意声明为 {@code private}，用于验证容器能够遍历父类并通过反射完成注入。
 */
public abstract class BaseOrderService {

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }
}
