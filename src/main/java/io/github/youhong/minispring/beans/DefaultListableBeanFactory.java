package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Autowired;
import io.github.youhong.minispring.exception.BeanCreationException;
import io.github.youhong.minispring.exception.BeanDefinitionNotFoundException;
import io.github.youhong.minispring.exception.BeansException;
import io.github.youhong.minispring.factory.DefaultSingletonBeanRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link BeanFactory} 接口的默认实现——mini-spring IoC 容器的核心引擎。
 *
 * <p>该类是整个 mini-spring 框架的中枢，同时承担两大职责：
 * <ul>
 *     <li><b>Bean 定义仓库</b>（通过 {@link BeanDefinitionRegistry}）：管理所有 {@link BeanDefinition} 的注册与查询</li>
 *     <li><b>Bean 工厂</b>（通过 {@link BeanFactory}）：根据定义创建、缓存并返回 Bean 实例</li>
 * </ul>
 *
 * <p><b>继承体系：</b>
 * <pre>{@code
 * DefaultSingletonBeanRegistry   ← 提供单例缓存（一级缓存 singletonObjects）
 *         ↑
 * DefaultListableBeanFactory     ← 当前类，整合定义仓库与工厂能力
 *         ↑ 实现
 * BeanFactory + BeanDefinitionRegistry
 * }</pre>
 *
 * <p><b>核心数据结构：</b>
 * <ul>
 *     <li>{@code beanDefinitionMap} — Bean 名称到定义的映射，作为定义仓库的主存储</li>
 *     <li>{@code singletonObjects}（继承）— Bean 名称到单例实例的缓存</li>
 * </ul>
 *
 * <p><b>Bean 创建流程（{@link #getBean(String)}）：</b>
 * <ol>
 *     <li>从 {@code beanDefinitionMap} 查找对应的 {@link BeanDefinition}</li>
 *     <li>调用 {@link #createBean(BeanDefinition)} 完成实例化和字段依赖注入</li>
 *     <li>若为单例，将实例存入 {@code singletonObjects} 缓存</li>
 *     <li>返回可用的 Bean 实例</li>
 * </ol>
 *
 * <p><b>典型使用场景：</b>
 * <pre>{@code
 * // 通常不直接使用，而是通过 ApplicationContext 间接调用
 * DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
 * factory.registerBeanDefinition("userService", beanDefinition);
 * UserService userService = (UserService) factory.getBean("userService");
 * }</pre>
 *
 * @author YouHong5286
 * @version 1.0.0
 * @see BeanFactory
 * @see BeanDefinitionRegistry
 * @see DefaultSingletonBeanRegistry
 * @since 2026/7/15 17:33
 */
public class DefaultListableBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

    /**
     * Bean 定义映射表——存储所有已注册的 BeanDefinition。
     *
     * <p>以 Bean 名称为 key，对应的 {@link BeanDefinition} 为 value。
     * 该映射是 {@link BeanDefinitionRegistry} 接口的核心数据结构，
     * 所有的注册（{@link #registerBeanDefinition}）、查询（{@link #getBeanDefinition}）、
     * 判断（{@link #containsBeanDefinition}）操作都基于此 Map 完成。
     *
     * <p>使用 {@link ConcurrentHashMap} 确保并发场景下的线程安全。
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * Bean 定义名称数组——维护所有已注册 Bean 的名称列表。
     *
     * <p>与 {@link #beanDefinitionMap} 配合使用：
     * <ul>
     *     <li>{@code beanDefinitionMap} 提供 O(1) 的按名查找能力</li>
     *     <li>{@code beanDefinitionNames} 保留注册顺序，支持按顺序遍历</li>
     * </ul>
     *
     * <p>在容器启动的预实例化阶段（{@code preInstantiateSingletons}），
     * 需要按注册顺序遍历所有 BeanDefinition，此时该数组比 Map.keySet() 更合适，
     * 因为后者不保证遍历顺序。
     */
    private String[] beanDefinitionNames = new String[0];

    /**
     * 将 BeanDefinition 注册到容器中。
     *
     * <p>注册流程：
     * <ol>
     *     <li>将 BeanDefinition 存入 {@link #beanDefinitionMap}（按名称索引）</li>
     *     <li>将 Bean 名称追加到 {@link #beanDefinitionNames} 数组</li>
     * </ol>
     *
     * <p>当前实现未校验重复名称：同名定义会覆盖 Map 中的旧值，同时名称数组仍会追加一次。
     * 这是学习版本的简化行为，调用方不应依赖重复注册；后续应明确选择禁止覆盖或受控覆盖策略。
     *
     * @param beanName       Bean 的唯一标识名称，不能为 {@code null}
     * @param beanDefinition Bean 定义元数据，不能为 {@code null}
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
        beanDefinitionNames = addElement(beanDefinitionNames, beanName);
    }

    /**
     * 判断容器中是否包含指定名称的 BeanDefinition。
     *
     * @param beanName 待检查的 Bean 名称，不能为 {@code null}
     * @return 如果存在对应的 BeanDefinition 则返回 {@code true}，否则返回 {@code false}
     */
    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    /**
     * 根据名称获取 BeanDefinition。
     *
     * @param beanName Bean 的唯一标识名称，不能为 {@code null}
     * @return 与指定名称关联的 {@link BeanDefinition} 实例
     * @throws BeanDefinitionNotFoundException 如果容器中不存在指定名称的 Bean 定义
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeanDefinitionNotFoundException(beanName);
        }
        return beanDefinition;
    }

    /**
     * 获取所有已注册的 BeanDefinition 名称。
     *
     * <p>返回的数组保持注册顺序，即先注册的 Bean 名称在前。
     * 该方法主要用于容器启动时的预实例化阶段，
     * 按顺序遍历所有 BeanDefinition 并创建单例 Bean。
     *
     * @return 包含所有 Bean 名称的数组，若无任何注册则返回空数组
     */
    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionNames;
    }

    /**
     * 根据 Bean 名称获取或创建 Bean 实例。
     *
     * <p>这是 IoC 容器最核心的方法，实现了"按名称获取 Bean"的契约。
     * 内部流程如下：
     * <ol>
     *     <li><b>单例检查：</b>查询 {@code singletonObjects} 缓存，若命中则直接返回</li>
     *     <li><b>定义查找：</b>从 {@code beanDefinitionMap} 获取对应的 {@link BeanDefinition}</li>
     *     <li><b>实例创建：</b>调用 {@link #createBean(BeanDefinition)} 通过反射创建</li>
     *     <li><b>缓存存储：</b>若为单例，将实例存入 {@code singletonObjects}</li>
     * </ol>
     *
     * @param beanName Bean 的唯一标识名称，不能为 {@code null}
     * @return 与指定名称关联的 Bean 实例
     * @throws BeanDefinitionNotFoundException 如果容器中不存在指定名称的 Bean 定义
     * @throws BeanCreationException           如果 Bean 实例化或依赖注入过程中发生反射异常
     */
    @Override
    public Object getBean(String beanName) {
        // 1. 单例缓存查询——若已创建则直接返回，避免重复创建
        Object singleton = getSingleton(beanName);
        if (singleton != null) {
            return singleton;
        }

        // 2. 获取 BeanDefinition——不存在则抛出异常
        BeanDefinition beanDefinition = getBeanDefinition(beanName);

        // 3. 通过反射创建 Bean 实例
        Object bean = createBean(beanDefinition);

        // 4. 单例注册——存入缓存供后续复用
        if (beanDefinition.isSingleton()) {
            registerSingleton(beanName, bean);
        }

        return bean;
    }

    /**
     * 根据 Bean 类型获取 Bean 实例（类型安全版本）。
     *
     * <p>该方法遍历所有已注册的 BeanDefinition，查找 {@code beanClass}
     * 与 {@code requiredType} 匹配的第一个 Bean 并返回。
     * 使用泛型参数 {@code <T>} 避免调用方手动进行类型转换。
     *
     * <p><b>注意：</b>当前实现不支持多 Bean 场景下的优先级选择（如 {@code @Primary}）。
     * 若存在多个同类型 Bean，返回的是遍历顺序中的第一个，行为不确定。
     * 后续可通过 {@code @Primary} 注解或类型优先级策略增强。
     *
     * @param <T>          期望的 Bean 类型
     * @param requiredType 期望的 Bean 类型 Class 对象，不能为 {@code null}
     * @return 与指定类型匹配的 Bean 实例
     * @throws BeansException 如果不存在指定类型的 Bean，或匹配 Bean 的创建过程失败
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        // 遍历所有 BeanDefinition，按类型匹配
        for (BeanDefinition beanDefinition : beanDefinitionMap.values()) {
            if (requiredType.isAssignableFrom(beanDefinition.getBeanClass())) {
                return (T) getBean(beanDefinition.getBeanName());
            }
        }

        throw new BeansException("No bean of type " + requiredType.getName() + " found");
    }

    /**
     * 通过反射创建 Bean 实例。
     *
     * <p>当前实现仅支持无参构造器创建，后续可扩展支持：
     * <ul>
     *     <li>构造器注入（{@code @Autowired} 标注构造器）</li>
     *     <li>静态工厂方法（{@code @Bean}）</li>
     *     <li>实例工厂方法（{@code factory-bean / factory-method}）</li>
     * </ul>
     *
     * @param beanDefinition Bean 定义元数据，包含待实例化的类信息
     * @return 已完成实例化和字段填充的 Bean 实例
     * @throws BeanCreationException 如果实例化或依赖注入过程中发生反射异常
     */
    private Object createBean(BeanDefinition beanDefinition) {

        Object bean;
        try {
            // 获取无参构造器并创建实例
            bean = instantiateBean(beanDefinition);
            populateBean(beanDefinition, bean);
        } catch (ReflectiveOperationException exception) {
            throw new BeanCreationException(
                    beanDefinition.getBeanName(),
                    "Bean instantiation or dependency injection failed",
                    exception
            );
        }

        return bean;
    }

    /**
     * 对已创建的 Bean 实例进行属性填充（依赖注入）。
     *
     * <p>沿 Bean 的继承层次逐级扫描声明字段，对标注了 {@link Autowired @Autowired} 的字段
     * 递归调用 {@link #getBean(Class)} 获取依赖实例，并通过反射完成字段注入。
     *
     * <p><b>当前限制：</b>
     * <ul>
     *     <li>仅支持字段注入，不支持构造器注入和 setter 注入</li>
     *     <li>不支持 {@code @Autowired(required=false)} 可选注入语义</li>
     *     <li>不检测循环依赖，若存在循环引用将导致 {@link StackOverflowError}</li>
     * </ul>
     *
     * @param beanDefinition Bean 定义元数据，用于获取 Bean 的 Class 信息
     * @param bean           已实例化但尚未填充属性的 Bean 对象
     * @throws IllegalAccessException 如果目标字段无法通过反射写入
     */
    private void populateBean(
            BeanDefinition beanDefinition,
            Object bean)
            throws IllegalAccessException {

        // getDeclaredFields() 不会返回继承字段，因此需要逐级遍历父类，直到 Object 为止。
        Class<?> currentClass = beanDefinition.getBeanClass();
        while (currentClass != null && currentClass != Object.class) {
            populateAutowiredFields(bean, currentClass);
            currentClass = currentClass.getSuperclass();
        }
    }

    /**
     * 注入指定类直接声明的 {@code @Autowired} 字段。
     *
     * <p>传入的 Bean 可以是该声明类的子类实例；反射仍可以写入实例中由父类声明的字段。
     *
     * @param bean         待填充属性的 Bean 实例
     * @param declaringClass 当前正在检查的字段声明类
     * @throws IllegalAccessException 如果目标字段无法通过反射写入
     */
    private void populateAutowiredFields(Object bean, Class<?> declaringClass) throws IllegalAccessException {
        Field[] fields = declaringClass.getDeclaredFields();

        for (Field field : fields) {
            // 仅处理标注了 @Autowired 的字段
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }

            // 按字段类型从容器获取依赖 Bean（递归触发依赖的创建）
            Object dependency = getBean(field.getType());

            // 突破 private 访问限制，通过反射注入依赖
            field.setAccessible(true);
            field.set(bean, dependency);
        }
    }

    /**
     * 通过反射调用无参构造器创建 Bean 实例。
     *
     * <p>该方法是 Bean 生命周期的第一步（实例化）
     * 获取无参构造器并调用 {@link java.lang.reflect.Constructor#newInstance(Object...)} 创建对象。
     *
     * @param beanDefinition Bean 定义元数据，提供待实例化的目标类
     * @return 通过无参构造器创建的 Bean 实例
     * @throws NoSuchMethodException     如果目标类不存在无参构造器
     * @throws InstantiationException    如果目标类是抽象类、接口、数组类型、基本类型或 {@code void}
     * @throws IllegalAccessException    如果无参构造器不可访问（例如为 {@code private}）
     * @throws InvocationTargetException 如果构造器内部抛出了异常
     */
    private static Object instantiateBean(BeanDefinition beanDefinition) throws ReflectiveOperationException {
        // 1. 获取目标类的 Class 对象
        // 2. 获取该类声明的无参构造器（包含 private）
        // 3. 通过构造器反射创建实例
        return beanDefinition.getBeanClass()
                .getDeclaredConstructor()
                .newInstance();
    }

    /**
     * 向数组末尾追加一个元素，返回新的数组。
     *
     * <p>由于 Java 数组长度固定，需要创建新数组并复制原内容。
     * 该工具方法用于维护 {@link #beanDefinitionNames} 数组的增长。
     *
     * @param array   原数组，可以为空数组但不能为 {@code null}
     * @param element 待追加的元素
     * @return 包含原元素和新元素的新数组
     */
    private String[] addElement(String[] array, String element) {
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = element;
        return newArray;
    }
}
