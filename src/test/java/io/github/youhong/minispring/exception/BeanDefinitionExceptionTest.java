package io.github.youhong.minispring.exception;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BeanDefinition 查询异常的数据和不可变性测试。
 */
class BeanDefinitionExceptionTest {

    @Test
    void shouldRetainBeanNameForNameLookupFailure() {
        NoSuchBeanDefinitionException exception =
                new NoSuchBeanDefinitionException("missingService");

        assertEquals("missingService", exception.getBeanName());
        assertNull(exception.getRequiredType());
        assertTrue(exception.getMessage().contains("missingService"));
    }

    @Test
    void shouldRetainRequiredTypeForTypeLookupFailure() {
        NoSuchBeanDefinitionException exception =
                new NoSuchBeanDefinitionException(SampleService.class);

        assertNull(exception.getBeanName());
        assertSame(SampleService.class, exception.getRequiredType());
        assertTrue(exception.getMessage().contains(SampleService.class.getName()));
    }

    @Test
    void shouldKeepExistingNameLookupExceptionCompatible() {
        BeanDefinitionNotFoundException exception =
                new BeanDefinitionNotFoundException("missingService");

        assertEquals("missingService", exception.getBeanName());
        assertNull(exception.getRequiredType());
    }

    @Test
    void shouldRetainSortedImmutableCandidatesForAmbiguousType() {
        List<String> candidates = new ArrayList<>(
                List.of("wechatPaymentService", "alipayPaymentService")
        );

        NoUniqueBeanDefinitionException exception =
                new NoUniqueBeanDefinitionException(SampleService.class, candidates);
        candidates.clear();

        assertSame(SampleService.class, exception.getRequiredType());
        assertEquals(2, exception.getNumberOfBeansFound());
        assertEquals(
                List.of("alipayPaymentService", "wechatPaymentService"),
                exception.getBeanNames()
        );
        assertThrows(
                UnsupportedOperationException.class,
                () -> exception.getBeanNames().add("anotherService")
        );
    }

    @Test
    void shouldRejectNoUniqueExceptionWithFewerThanTwoCandidates() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new NoUniqueBeanDefinitionException(
                        SampleService.class,
                        List.of("onlyCandidate")
                )
        );
    }

    private interface SampleService {
    }
}
