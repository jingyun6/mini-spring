package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Component;
import io.github.youhong.minispring.scanner.ClassPathScanner;

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
public class DefaultListableBeanFactory implements BeanDefinitionRegistry {


    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {

    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return null;
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return false;
    }
}
