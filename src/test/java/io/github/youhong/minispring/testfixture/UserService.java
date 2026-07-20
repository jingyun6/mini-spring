package io.github.youhong.minispring.testfixture;

import io.github.youhong.minispring.annotation.Component;

/**
 * 用户服务——用于测试 IoC 容器的组件扫描和 Bean 管理功能。
 *
 * <p>该类是一个简单的业务服务组件，被 {@link Component @Component} 注解标记，
 * 会被 mini-spring 的类路径扫描器自动发现并注册到 IoC 容器中。
 *
 * <p><b>测试用途：</b>
 * <ul>
 *     <li>验证 {@code @Component} 注解能被正确识别</li>
 *     <li>验证容器能正确创建该类的单例 Bean</li>
 *     <li>作为 {@link OrderService} 的依赖，验证自动装配功能</li>
 * </ul>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/20 16:16
 */

@Component
public class UserService {

    /**
     * 获取用户名称。
     *
     * <p>模拟从数据库或外部服务获取用户信息的场景。
     * 当前返回固定值用于测试验证。
     *
     * @return 用户名称字符串
     */
    public String getUserName() {
        return "YouHong";
    }
}
