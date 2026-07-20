package io.github.youhong.minispring.testfixture;

import io.github.youhong.minispring.annotation.Autowired;
import io.github.youhong.minispring.annotation.Component;

/**
 * 订单服务——用于测试 IoC 容器的依赖注入功能。
 *
 * <p>该类是一个包含依赖关系的业务服务组件，被 {@link Component @Component} 注解标记。
 * 其内部通过 {@link Autowired @Autowired} 注解注入 {@link UserService} 依赖，
 * 用于验证 mini-spring 容器的自动装配能力。
 *
 * <p><b>测试用途：</b>
 * <ul>
 *     <li>验证 {@code @Component} 注解能被正确识别</li>
 *     <li>验证容器能正确创建该类的单例 Bean</li>
 *     <li>验证 {@code @Autowired} 字段依赖注入功能</li>
 *     <li>测试按类型获取 Bean 的功能</li>
 * </ul>
 *
 * <p>容器创建该 Bean 时会按照字段类型解析 UserService，并在属性填充阶段完成注入。</p>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @see UserService
 * @see io.github.youhong.minispring.annotation.Autowired
 * @since 2026/7/20 16:17
 */
@Component
public class OrderService {

    /**
     * 用户服务依赖，由容器按照字段类型自动注入。
     */
    @Autowired
    private UserService userService;

    /**
     * 订单描述信息——用于测试 Bean 的属性访问。
     */
    private String desc;

    /**
     * 获取用户服务实例。
     *
     * @return 容器注入的 {@link UserService} 单例
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * 获取订单描述信息。
     *
     * @return 订单描述字符串
     */
    public String getDesc() {
        return desc;
    }
}
