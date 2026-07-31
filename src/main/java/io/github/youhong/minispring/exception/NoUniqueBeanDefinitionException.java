package io.github.youhong.minispring.exception;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

/**
 * 按类型查询时找到多个候选 Bean，无法确定唯一结果时抛出的异常。
 *
 * <p>异常保存导致歧义的候选名称：没有 primary 时为全部类型候选，
 * 存在多个 primary 时为相互冲突的 primary 候选。名称会被排序并防御性复制，
 * 避免异常创建后被调用方修改。</p>
 */
public class NoUniqueBeanDefinitionException extends NoSuchBeanDefinitionException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 导致本次歧义的候选 Bean 名称。 */
    private final String[] beanNames;

    /**
     * 根据目标类型和候选 Bean 名称创建异常。
     *
     * @param requiredType 查询使用的目标类型
     * @param beanNames    导致本次歧义的候选 Bean 名称
     * @throws IllegalArgumentException 如果候选 Bean 少于两个
     */
    public NoUniqueBeanDefinitionException(
            Class<?> requiredType,
            Collection<String> beanNames) {

        this(requiredType, sortedCopy(beanNames), "matching candidates");
    }

    /**
     * 创建一个由多个 primary 候选引起的歧义异常。
     *
     * <p>该工厂方法使异常消息明确指向 primary 配置冲突，并且只保存
     * 相互冲突的 primary Bean 名称，便于调用方缩小排查范围。</p>
     *
     * @param requiredType         查询使用的目标类型
     * @param primaryBeanNames     相互冲突的 primary Bean 名称
     * @return 包含 primary 冲突诊断信息的异常
     * @throws IllegalArgumentException 如果 primary 候选 Bean 少于两个
     */
    public static NoUniqueBeanDefinitionException forPrimaryCandidates(
            Class<?> requiredType,
            Collection<String> primaryBeanNames) {

        return new NoUniqueBeanDefinitionException(
                requiredType,
                sortedCopy(primaryBeanNames),
                "conflicting primary candidates"
        );
    }

    private NoUniqueBeanDefinitionException(
            Class<?> requiredType,
            List<String> beanNames,
            String candidateDescription) {

        super(requiredType, buildMessage(requiredType, beanNames, candidateDescription));
        if (beanNames.size() < 2) {
            throw new IllegalArgumentException(
                    "NoUniqueBeanDefinitionException requires at least two candidate bean names"
            );
        }
        this.beanNames = beanNames.toArray(String[]::new);
    }

    /**
     * 获取导致本次歧义的候选 Bean 数量。
     *
     * @return 冲突候选 Bean 数量
     */
    public int getNumberOfBeansFound() {
        return beanNames.length;
    }

    /**
     * 获取不可变的冲突候选 Bean 名称列表。
     *
     * @return 按名称排序的冲突候选 Bean 列表
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
            List<String> beanNames,
            String candidateDescription) {

        if (requiredType == null) {
            throw new IllegalArgumentException("requiredType must not be null");
        }
        return "Expected single bean of type '"
                + requiredType.getName()
                + "' but found "
                + beanNames.size()
                + " "
                + candidateDescription
                + ": "
                + String.join(", ", beanNames);
    }
}
