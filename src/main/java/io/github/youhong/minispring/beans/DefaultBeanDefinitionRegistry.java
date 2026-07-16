package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.exception.BeanDefinitionNotFoundException;
import io.github.youhong.minispring.utils.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 默认的Bean定义注册表实现
 * </p>
 * <p>
 * 使用ConcurrentHashMap存储Bean名称与BeanDefinition的映射关系，提供线程安全的注册、获取和检查功能。
 * 这个类实现了BeanDefinitionRegistry接口，是Spring容器中管理Bean定义的核心组件之一。
 * </p>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/16 09:08
 */
public class DefaultBeanDefinitionRegistry implements BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        Assert.notNull(beanName, "Bean name must not be null");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");

        if (containsBeanDefinition(beanName)) {
            throw new IllegalStateException("BeanDefinition '" + beanName + "' already exists.");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        if (beanDefinitionMap.get(beanName) == null) {
            throw new BeanDefinitionNotFoundException(beanName);
        }
        return beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }
}
