package io.github.youhong.minispring.context;

import io.github.youhong.minispring.testfixture.InheritedOrderService;
import io.github.youhong.minispring.testfixture.OrderService;
import io.github.youhong.minispring.testfixture.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link AnnotationConfigApplicationContext} 的集成测试。
 *
 * <p>从公开 ApplicationContext API 验证组件扫描、按类型获取、字段注入和单例复用，
 * 避免测试依赖 BeanFactory 的私有实现细节。</p>
 */
class AnnotationConfigApplicationContextTest {

    private static final String BASE_PACKAGE =
            "io.github.youhong.minispring.testfixture";

    @Test
    void shouldDiscoverComponentAndGetBeanByType() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(BASE_PACKAGE);

        UserService userService =
                context.getBean(UserService.class);

        assertNotNull(userService);
        assertEquals("YouHong", userService.getUserName());
    }

    @Test
    void shouldInjectAutowiredFieldByType() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(BASE_PACKAGE);

        OrderService orderService =
                context.getBean(OrderService.class);

        assertNotNull(orderService.getUserService());
        assertEquals(
                "YouHong",
                orderService.getUserService().getUserName()
        );
    }

    @Test
    void shouldInjectSingletonFromContainer() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(BASE_PACKAGE);

        OrderService orderService =
                context.getBean(OrderService.class);

        UserService userService =
                context.getBean(UserService.class);

        assertSame(
                userService,
                orderService.getUserService()
        );
    }

    @Test
    void shouldNotModifyFieldWithoutAutowired() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(BASE_PACKAGE);

        OrderService orderService =
                context.getBean(OrderService.class);

        assertNull(orderService.getDesc());
    }

    @Test
    void shouldInjectAutowiredFieldDeclaredInSuperclass() {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(BASE_PACKAGE);
        InheritedOrderService inheritedOrderService =
                context.getBean(InheritedOrderService.class);
        UserService userService =
                context.getBean(UserService.class);

        assertNotNull(inheritedOrderService.getUserService());
        assertSame(userService, inheritedOrderService.getUserService());
    }
}
