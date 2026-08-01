package io.github.youhong.minispring.annotation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code @Qualifier} 第一课：定义精确候选选择的注入点元数据契约。
 *
 * <p>红灯阶段曾通过类名反射加载尚未存在的注解；契约实现后改为直接引用
 * {@link Qualifier}，使后续重构继续受到编译器保护。</p>
 */
class QualifierContractTest {

    @Test
    void shouldDeclareQualifierAsRuntimeFieldAndParameterAnnotation() {
        Class<Qualifier> qualifierClass = Qualifier.class;

        assertTrue(qualifierClass.isAnnotation());

        Target target = qualifierClass.getAnnotation(Target.class);
        assertEquals(
                Set.of(ElementType.FIELD, ElementType.PARAMETER),
                Set.copyOf(Arrays.asList(target.value()))
        );

        Retention retention = qualifierClass.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldRequireExplicitBeanNameAsQualifierValue() throws Exception {
        Class<Qualifier> qualifierClass = Qualifier.class;

        Method valueMethod = qualifierClass.getDeclaredMethod("value");

        assertSame(String.class, valueMethod.getReturnType());
        assertNull(
                valueMethod.getDefaultValue(),
                "@Qualifier should require an explicit bean name"
        );
    }
}
