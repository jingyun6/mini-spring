package io.github.youhong.minispring.exception;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

/**
 * 按类型查询时找到多个候选 Bean，无法确定唯一结果时抛出的异常。
 *
 * <p>候选名称会被复制为不可变列表，避免异常创建后被调用方修改。</p>
 */
public class NoUniqueBeanDefinitionException extends NoSuchBeanDefinitionException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 与目标类型匹配的全部候选 Bean 名称。 */
    private final String[] beanNames;

    /**
     * 根据目标类型和候选 Bean 名称创建异常。
     *
     * @param requiredType 查询使用的目标类型
     * @param beanNames    与目标类型匹配的候选 Bean 名称
     * @throws IllegalArgumentException 如果候选 Bean 少于两个
     */
    public NoUniqueBeanDefinitionException(
            Class<?> requiredType,
            Collection<String> beanNames) {

        this(requiredType, sortedCopy(beanNames));
    }

    private NoUniqueBeanDefinitionException(
            Class<?> requiredType,
            List<String> beanNames) {

        super(requiredType, buildMessage(requiredType, beanNames));
        if (beanNames.size() < 2) {
            throw new IllegalArgumentException(
                    "NoUniqueBeanDefinitionException requires at least two candidate bean names"
            );
        }
        this.beanNames = beanNames.toArray(String[]::new);
    }

    /**
     * 获取与目标类型匹配的候选 Bean 数量。
     *
     * @return 候选 Bean 数量
     */
    public int getNumberOfBeansFound() {
        return beanNames.length;
    }

    /**
     * 获取不可变的候选 Bean 名称列表。
     *
     * @return 按名称排序的候选 Bean 列表
     */
    public List<String> getBeanNames() {
        return List.of(beanNames);
    }

    private static List<String> sortedCopy(Collection<String> beanNames) {
        if (beanNames == null) {
            throw new IllegalArgumentException("beanNames must not be null");
        }
        return beanNames.stream()
                .sorted()
                .toList();
    }

    private static String buildMessage(
            Class<?> requiredType,
            List<String> beanNames) {

        if (requiredType == null) {
            throw new IllegalArgumentException("requiredType must not be null");
        }
        return "Expected single bean of type '"
                + requiredType.getName()
                + "' but found "
                + beanNames.size()
                + ": "
                + String.join(", ", beanNames);
    }
}
