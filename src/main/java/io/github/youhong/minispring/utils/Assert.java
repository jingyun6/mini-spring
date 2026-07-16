package io.github.youhong.minispring.utils;

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
 * @since 2026/7/16 09:47
 */
public class Assert {

    private Assert() {
    }

    public static void notNull(
            Object object,
            String message) {

        if (object == null) {
            throw new IllegalArgumentException(message);
        }

    }
}
