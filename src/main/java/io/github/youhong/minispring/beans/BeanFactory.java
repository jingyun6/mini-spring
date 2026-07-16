package io.github.youhong.minispring.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * [一句话描述该类的核心职责]
 * </p>
 * <p>
 * [详细描述：介绍应用场景、注意事项、核心算法或与其他类的协作关系等（可选）]
 * </p>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @since 2026/7/16 08:25
 */
public class BeanFactory {
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final Map<String, Object> singletonBeans = new ConcurrentHashMap<>();

    public Object getBean(String name) {

        return null;
    }

    public BeanFactory(Map<String, BeanDefinition> beanDefinitionMap) {
        this.beanDefinitionMap = beanDefinitionMap;
    }
}
