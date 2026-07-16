package io.github.youhong.minispring.beans;

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
 * @since 2026/7/15 17:33
 */
public class BeanDefinition {
    private Class<?> beanClass;
    private String beanName;
    private boolean singleton = true;

    public BeanDefinition() {
        this.beanClass = beanClass;
        this.beanName = beanName;
        this.singleton = singleton;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
