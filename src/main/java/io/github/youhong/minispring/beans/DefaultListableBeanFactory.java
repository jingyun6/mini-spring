package io.github.youhong.minispring.beans;

import io.github.youhong.minispring.annotation.Autowired;
import io.github.youhong.minispring.exception.BeanCreationException;
import io.github.youhong.minispring.exception.BeanDefinitionNotFoundException;
import io.github.youhong.minispring.exception.NoSuchBeanDefinitionException;
import io.github.youhong.minispring.exception.NoUniqueBeanDefinitionException;
import io.github.youhong.minispring.factory.DefaultSingletonBeanRegistry;
import io.github.youhong.minispring.utils.Assert;
import io.github.youhong.minispring.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
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
 *     <li>{@code threadLocalCreationPath} — 当前线程正在创建的 Bean 路径，用于检测循环依赖</li>
 *     <li>{@code singletonCreationMonitor} — 串行化单例的检查、创建和注册流程</li>
 * </ul>
 *
 * <p><b>Bean 创建流程（{@link #getBean(String)}）：</b>
 * <ol>
 *     <li>查询单例缓存，命中时直接返回</li>
 *     <li>从 {@code beanDefinitionMap} 查找对应的 {@link BeanDefinition}</li>
 *     <li>prototype Bean 直接创建；singleton Bean 进入创建监视器并再次检查缓存</li>
 *     <li>记录当前线程的创建路径，选择构造器、解析参数并完成实例化和字段注入</li>
 *     <li>将创建完成的 singleton Bean 存入缓存并返回；prototype Bean 直接返回</li>
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
     * 创建一个空的 BeanFactory，等待后续注册 BeanDefinition。
     */
    public DefaultListableBeanFactory() {
    }

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
     * 当前线程的 Bean 创建路径。
     *
     * <p>同一线程递归解析依赖时共享一个有序路径，例如 {@code orderService -> userService}。
     * 若待创建的 Bean 已经出现在路径中，说明当前依赖链形成闭环。路径在每次创建结束时
     * 按栈顺序清理，并在线程路径为空时调用 {@link ThreadLocal#remove()}。</p>
     */
    private final ThreadLocal<Deque<String>> threadLocalCreationPath = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 单例创建监视器。
     *
     * <p>保护单例 Bean 的二次缓存检查、实例化、依赖注入和缓存注册，使并发请求只创建
     * 一个实例。缓存的首次查询位于监视器之外，已创建完成的单例无需参与锁竞争；
     * prototype Bean 也不会进入该监视器。</p>
     */
    private final Object singletonCreationMonitor = new Object();

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
     *     <li>校验 Bean 名称和 BeanDefinition</li>
     *     <li>检查是否已经存在同名定义</li>
     *     <li>将 BeanDefinition 存入 {@link #beanDefinitionMap}</li>
     *     <li>将 Bean 名称追加到 {@link #beanDefinitionNames} 数组</li>
     * </ol>
     *
     * <p>同名定义不允许覆盖。注册冲突发生时保留原 BeanDefinition，名称数组也不会改变。</p>
     *
     * @param beanName       Bean 的唯一标识名称，不能为 {@code null}
     * @param beanDefinition Bean 定义元数据，不能为 {@code null}
     * @throws IllegalArgumentException 如果 Bean 名称为空或 BeanDefinition 为 {@code null}
     * @throws IllegalStateException    如果已经存在同名 BeanDefinition
     */
    @Override
    public synchronized void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        // Bean 名称和定义是注册操作的必要输入，进入容器存储前快速失败。
        if (StringUtils.isBlank(beanName)) {
            throw new IllegalArgumentException("beanName must not be null or empty");
        }
        if (beanDefinition == null) {
            throw new IllegalArgumentException("beanDefinition must not be null");
        }

        // 禁止覆盖已有定义，确保 Map 与名称数组始终表示同一组 BeanDefinition。
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalStateException("Cannot register bean definition " + beanDefinition +
                    " for bean '" + beanName +
                    "': there is already " + getBeanDefinition(beanName) + " bound");
        }

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
     * @return 包含所有 Bean 名称的防御性副本；若无任何注册则返回空数组
     */
    @Override
    public synchronized String[] getBeanDefinitionNames() {
        // 返回副本，防止调用方通过数组引用修改容器内部注册状态。
        return beanDefinitionNames.clone();
    }

    /**
     * 根据 Bean 名称获取或创建 Bean 实例。
     *
     * <p>这是 IoC 容器最核心的方法，实现了"按名称获取 Bean"的契约。
     * 内部流程如下：
     * <ol>
     *     <li><b>快速单例检查：</b>查询 {@code singletonObjects} 缓存，若命中则直接返回</li>
     *     <li><b>定义查找：</b>从 {@code beanDefinitionMap} 获取对应的 {@link BeanDefinition}</li>
     *     <li><b>作用域分流：</b>prototype 直接创建；singleton 进入创建监视器</li>
     *     <li><b>单例二次检查：</b>等待监视器期间其他线程可能已经完成创建，因此再次查询缓存</li>
     *     <li><b>实例创建：</b>检测循环依赖后，通过反射实例化并完成字段注入</li>
     *     <li><b>缓存存储：</b>将创建完成的 singleton Bean 注册到 {@code singletonObjects}</li>
     * </ol>
     *
     * @param beanName Bean 的唯一标识名称，不能为 {@code null}
     * @return 与指定名称关联的 Bean 实例
     * @throws BeanDefinitionNotFoundException 如果容器中不存在指定名称的 Bean 定义
     * @throws BeanCreationException           如果检测到循环依赖，或实例化、依赖注入失败
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
        if (!beanDefinition.isSingleton()) {
            return createBeanWithCycleDetection(beanName, beanDefinition);
        }

        synchronized (singletonCreationMonitor) {
            // 等待监视器期间其他线程可能已经完成创建，因此必须再次查询单例缓存。
            singleton = getSingleton(beanName);
            if (singleton != null) {
                return singleton;
            }
            Object bean = createBeanWithCycleDetection(beanName, beanDefinition);
            // 将完整创建并完成依赖注入的实例注册到单例缓存。
            registerSingleton(beanName, bean);
            return bean;
        }
    }

    /**
     * 在当前线程的创建路径中检测循环依赖，并创建 Bean。
     *
     * <p>进入创建流程前将 Bean 名称追加到路径末尾；递归解析依赖时，如果同名 Bean
     * 已经存在于路径中，则截取闭环部分并抛出包含完整路径的
     * {@link BeanCreationException}，例如 {@code circularA -> circularB -> circularA}。</p>
     *
     * <p>路径清理位于 {@code finally} 中，因此实例化、依赖解析或字段注入失败都不会
     * 污染后续创建。该方法同时用于 singleton 和 prototype Bean。</p>
     *
     * @param beanName       当前待创建的 Bean 名称
     * @param beanDefinition 当前 Bean 的定义元数据
     * @return 已完成实例化和依赖注入的 Bean
     * @throws BeanCreationException 如果检测到循环依赖，或 Bean 创建过程失败
     */
    private Object createBeanWithCycleDetection(
            String beanName,
            BeanDefinition beanDefinition) {
        Object bean;
        Deque<String> creationPath = threadLocalCreationPath.get();
        if (creationPath.contains(beanName)) {
            List<String> circularPath = new ArrayList<>(creationPath);
            circularPath.add(beanName);

            int startIndex = circularPath.indexOf(beanName);
            circularPath = circularPath.subList(startIndex, circularPath.size());
            String path = String.join(" -> ", circularPath);

            throw new BeanCreationException(beanName,
                    "Circular dependency detected: " + path);
        }

        creationPath.addLast(beanName);
        try {
            // 3. 通过反射创建 Bean 实例
            bean = createBean(beanDefinition);
            return bean;
        } finally {
            creationPath.removeLast();
            if (creationPath.isEmpty()) {
                threadLocalCreationPath.remove();
            }
        }
    }

    /**
     * 根据 Bean 类型获取 Bean 实例（类型安全版本）。
     *
     * <p>该方法先根据 BeanDefinition 中的类型元数据收集全部候选者，再判断候选数量。
     * 只有存在唯一候选者时才会调用 {@link #getBean(String)} 获取实例，避免为类型匹配
     * 提前创建不需要的 Bean。
     *
     * <p><b>唯一性规则：</b>
     * <ul>
     *     <li>没有候选者时抛出 {@link NoSuchBeanDefinitionException}</li>
     *     <li>只有一个候选者时创建或复用该 Bean</li>
     *     <li>存在多个候选者时抛出 {@link NoUniqueBeanDefinitionException}</li>
     * </ul>
     * 后续可通过 {@code @Primary} 或 {@code @Qualifier} 扩展多候选选择策略。
     *
     * @param <T>          期望的 Bean 类型
     * @param requiredType 期望的 Bean 类型 Class 对象，不能为 {@code null}
     * @return 与指定类型匹配的 Bean 实例
     * @throws IllegalArgumentException        如果 {@code requiredType} 为 {@code null}
     * @throws NoSuchBeanDefinitionException   如果不存在匹配类型的 BeanDefinition
     * @throws NoUniqueBeanDefinitionException 如果存在多个匹配类型的 BeanDefinition
     * @throws BeanCreationException           如果唯一候选 Bean 的创建过程失败
     */
    @Override
    public <T> T getBean(Class<T> requiredType) {
        Assert.notNull(requiredType, "requiredType must not be null");

        // 候选发现只读取 BeanDefinition 元数据，不在这一阶段创建 Bean 实例。
        List<BeanDefinition> candidates = beanDefinitionMap.values().stream()
                .filter(beanDefinition -> requiredType.isAssignableFrom(beanDefinition.getBeanClass()))
                .toList();

        if (candidates.isEmpty()) {
            throw new NoSuchBeanDefinitionException(requiredType);
        }
        if (candidates.size() > 1) {
            List<String> candidateNames = candidates.stream()
                    .map(BeanDefinition::getBeanName)
                    .toList();
            throw new NoUniqueBeanDefinitionException(requiredType, candidateNames);
        }

        // 确定唯一候选者后，统一复用按名称获取流程及其单例缓存。
        BeanDefinition beanDefinition = candidates.getFirst();
        Object bean = getBean(beanDefinition.getBeanName());
        return requiredType.cast(bean);
    }

    /**
     * 通过反射创建 Bean 实例。
     *
     * <p>实例化阶段先按规则选择构造器，再通过 {@link #getBean(Class)} 解析构造器参数；
     * 实例化完成后继续执行字段填充。当前构造器选择规则为：
     * <ul>
     *     <li>只有一个构造器时直接选择</li>
     *     <li>存在多个构造器时使用无参构造器回退</li>
     *     <li>多个构造器且没有无参回退时，以明确的歧义异常失败</li>
     * </ul>
     * 多构造器上的 {@link Autowired @Autowired} 显式选择规则将在后续课程实现。</p>
     *
     * @param beanDefinition Bean 定义元数据，包含待实例化的类信息
     * @return 已完成实例化和字段填充的 Bean 实例
     * @throws BeanCreationException 如果实例化或依赖注入过程中发生反射异常
     */
    private Object createBean(BeanDefinition beanDefinition) {

        Object bean;
        try {
            // 构造器选择、参数解析与反射调用组成实例化阶段；随后执行字段属性填充。
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
     * <p>构造器参数注入已经在实例化阶段完成，本方法只负责字段属性填充。
     * 当前字段填充阶段的限制为：
     * <ul>
     *     <li>不支持 setter 方法注入</li>
     *     <li>不支持 {@code @Autowired(required=false)} 可选注入语义</li>
     *     <li>能够检测并拒绝循环依赖，但尚未通过早期 Bean 引用解决循环依赖</li>
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
     * @param bean           待填充属性的 Bean 实例
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
     * 按“选择构造器、解析参数、反射调用”三个步骤创建 Bean 实例。
     *
     * <p>选择阶段只读取构造器元数据，不触发依赖创建；确定唯一构造器后，参数解析阶段
     * 才会通过 BeanFactory 获取依赖。这样构造器歧义可以在没有实例化副作用的情况下失败。</p>
     *
     * @param beanDefinition Bean 定义元数据，提供 Bean 名称和待实例化类型
     * @return 已完成构造器注入的 Bean 实例
     * @throws ReflectiveOperationException 如果构造器无法访问、目标类型无法实例化或构造器执行失败
     */
    private Object instantiateBean(BeanDefinition beanDefinition) throws ReflectiveOperationException {
        Constructor<?> constructor = determineConstructor(beanDefinition);
        Object[] arguments = resolveConstructorArguments(constructor);
        return instantiateWithConstructor(constructor, arguments);
    }

    /**
     * 根据当前构造器选择契约确定唯一的实例化入口。
     *
     * <p>唯一构造器优先；存在多个构造器时使用无参构造器回退；无法唯一确定时抛出
     * 包含 Bean 名称和类型信息的 {@link BeanCreationException}。</p>
     */
    private Constructor<?> determineConstructor(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        if (declaredConstructors.length == 0) {
            throw new BeanCreationException(
                    beanDefinition.getBeanName(),
                    "No constructor is available on bean class '"
                            + beanClass.getName()
                            + "'"
            );
        }

        if (declaredConstructors.length == 1) {
            return declaredConstructors[0];
        }

        Optional<Constructor<?>> constructorOptional = Arrays
                .stream(declaredConstructors)
                .filter(obj -> obj.getParameterTypes().length == 0).findFirst();

        if (constructorOptional.isPresent()) {
            return constructorOptional.get();
        }
        throw new BeanCreationException(
                beanDefinition.getBeanName(),
                "Ambiguous constructors on bean class '"
                        + beanClass.getName()
                        + "': expected a single constructor or a no-argument fallback"
        );
    }

    /**
     * 按声明顺序解析所选构造器的全部参数。
     *
     * <p>每个参数统一委托给 {@link #getBean(Class)}，从而复用按类型候选选择、单例缓存、
     * 异常体系和当前线程的循环依赖检测。</p>
     */
    private Object[] resolveConstructorArguments(
            Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = getBean(parameterTypes[i]);
        }
        return parameters;
    }

    /**
     * 使用已经选定的构造器和解析完成的实参执行反射实例化。
     *
     * <p>无参和有参构造器统一经过该出口，确保访问控制和反射异常行为保持一致。</p>
     */
    private Object instantiateWithConstructor(
            Constructor<?> constructor,
            Object[] arguments)
            throws ReflectiveOperationException {
        constructor.setAccessible(true);
        return constructor.newInstance(arguments);
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
