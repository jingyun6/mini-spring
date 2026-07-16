package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Component;
import io.github.youhong.minispring.scanner.ClassPathScanner;
import io.github.youhong.minispring.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Bean定义注册中心，负责扫描并注册带有 {@link Component} 注解的类。
 * </p>
 * <p>
 * 该类是 mini-spring IoC 容器的核心组件之一，通过 {@link ClassPathScanner} 扫描指定包路径下的类，
 * 筛选出被 {@code @Component} 标注的类，将其封装为 {@link BeanDefinition} 并注册到内部维护的 Map 中。
 * 默认所有注册的 Bean 均为单例（singleton），Bean 名称由类名首字母小写生成。
 * </p>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/16 08:11
 */
public class BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 批量注册一组候选类中所有被 {@link Component} 注解标记的 Bean。
     * <p>
     * 遍历传入的类集合，对每个标注了 {@code @Component} 的类创建对应的 {@link BeanDefinition}，
     * 并设置 Bean 名称（类名首字母小写）、类型及单例模式，随后存入注册中心。
     * </p>
     *
     * @param classes 待扫描注册的候选类集合，通常由 {@link ClassPathScanner} 扫描获得
     */
    public void register(Set<Class<?>> classes) {

        for (Class<?> clazz : classes) {
            // 跳过未被 @Component 注解标记的类
            if (!clazz.isAnnotationPresent(Component.class)) {
                continue;
            }
            // 构建 BeanDefinition 并设置元信息：名称、类型、作用域
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanName(StringUtils.lowerFirst(clazz.getSimpleName()));
            beanDefinition.setBeanClass(clazz);
            beanDefinition.setSingleton(true);

            beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        }
    }

    /**
     * 获取当前注册中心中所有已注册的 Bean 定义。
     *
     * @return 以 Bean 名称为键、{@link BeanDefinition} 为值的不可变映射视图（外部请勿直接修改）
     */
    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }
}
