package io.github.youhong.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记同一类型的多个 Bean 候选中的默认首选项。
 *
 * <p>该注解是配置来源；扫描阶段负责把它转换为
 * {@link io.github.youhong.minispring.beans.BeanDefinition BeanDefinition} 的 primary 元数据，
 * ApplicationContext 在组件扫描注册阶段完成该转换，BeanFactory 只读取
 * 结构化元数据完成候选选择，不依赖元数据的注解来源。</p>
 *
 * <p>同一目标类型存在且仅存在一个 primary 候选时，容器选择该 Bean；多个 primary
 * 候选仍属于歧义配置，并以
 * {@link io.github.youhong.minispring.exception.NoUniqueBeanDefinitionException} 失败。</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Primary {
}
