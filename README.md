# mini-spring

一个从零实现 Spring 核心机制的学习项目。

> [!NOTE]
> mini-spring 用于记录 IoC 容器的设计、推导和演进过程，不追求完整复刻
> Spring Framework，也不适合直接用于生产环境。

## 当前版本

**v0.3.0 — IoC 容器健壮性增强版**

当前版本在基础 IoC 主链路之上，增加了循环依赖检测和并发单例唯一创建：

```text
扫描 @Component
→ 注册全部 BeanDefinition
→ 预实例化单例 Bean
→ 反射调用无参构造器
→ 按类型填充 @Autowired 字段
→ 使用线程级创建路径检测循环依赖
→ 使用单例创建监视器协调并发请求
→ 注册到单例缓存
→ 按名称或类型获取 Bean
```

当前已经完成后续路线中的 2/7 个里程碑。本版本的学习重点是理解以下分层与协作关系：

- `BeanDefinition` 描述“如何创建 Bean”
- `BeanDefinitionRegistry` 管理 Bean 元数据
- `BeanFactory` 负责查找、创建和装配 Bean
- `SingletonBeanRegistry` 保存已经创建完成的单例
- `ApplicationContext` 组织扫描、注册和预实例化流程
- `ThreadLocal<Deque<String>>` 记录当前线程的 Bean 创建路径
- 单例创建监视器保护“二次检查、创建、注入、注册”的完整流程

## 已实现能力

### BeanDefinition 注册

- 按名称注册、查询和遍历 BeanDefinition
- 拒绝空名称、空定义和同名覆盖
- 保持注册顺序
- 返回名称数组的防御性副本
- 使用同一监视器锁保证并发注册时 Map 与名称列表的一致性

### Bean 创建与获取

- `getBean(String)`：按名称获取 Bean
- `getBean(Class<T>)`：按类型安全获取 Bean
- 使用 `Class#isAssignableFrom` 支持接口和父类匹配
- 没有候选者时抛出 `NoSuchBeanDefinitionException`
- 多个候选者时抛出 `NoUniqueBeanDefinitionException`
- 唯一候选者确定后才创建 Bean，避免无意义的实例化副作用
- singleton Bean 创建后进入缓存，prototype Bean 每次获取时重新创建

按类型查找遵循以下契约：

| 候选数量 | 行为 |
|---:|---|
| 0 | 抛出 `NoSuchBeanDefinitionException` |
| 1 | 创建或复用唯一 Bean |
| 大于 1 | 抛出 `NoUniqueBeanDefinitionException` |

### 依赖注入

- `@Component` 组件发现
- `@Autowired` 字段注入
- 按字段类型解析依赖
- 支持私有字段
- 沿继承层次注入父类声明的字段
- 依赖解析统一复用 `BeanFactory#getBean(Class)`
- 检测直接自依赖和多个 Bean 形成的循环依赖
- 循环依赖异常包含完整闭环路径，例如 `a -> b -> a`

### 单例与异常

- 基于 `ConcurrentHashMap` 的一级单例缓存
- 同名单例禁止重复注册
- 缓存命中使用无锁快速路径
- 使用专用监视器和锁内二次检查保证并发请求只创建一个单例
- prototype Bean 不参与单例创建锁竞争
- `BeansException` 作为容器异常根类
- `BeanCreationException` 保留底层反射异常
- 循环依赖以 `BeanCreationException` 失败，不再退化为 `StackOverflowError`
- 缺失定义和多候选场景使用明确的领域异常

### 自动化测试

- BeanDefinition 注册契约测试
- 按类型查找的无候选、唯一候选和多候选测试
- 并发注册一致性测试
- 异常数据与不可变性测试
- 组件扫描、字段注入、父类字段注入和单例复用集成测试
- 互相依赖、直接自依赖和创建失败后的路径清理测试
- 16 个并发请求只创建并返回同一个单例的测试
- 当前共 26 个自动化测试

## 技术栈

- Java 21
- Gradle 9.6.0（Kotlin DSL）
- JUnit 5
- 核心容器运行时无第三方依赖

## 快速开始

### 环境要求

- JDK 21 或更高版本
- 不需要预先安装 Gradle，仓库包含 Gradle Wrapper

### 定义组件

```java
@Component
public class UserService {
}

@Component
public class OrderService {

    @Autowired
    private UserService userService;
}
```

### 启动容器

```java
ApplicationContext context =
        new AnnotationConfigApplicationContext("com.example");

OrderService orderService =
        context.getBean(OrderService.class);
```

### 运行测试

Linux/macOS：

```bash
./gradlew test
```

Windows：

```powershell
.\gradlew.bat test
```

## 项目结构

```text
io.github.youhong.minispring
├── annotation
│   ├── Autowired
│   └── Component
├── beans
│   ├── BeanDefinition
│   ├── BeanDefinitionRegistry
│   ├── BeanFactory
│   └── DefaultListableBeanFactory
├── context
│   ├── ApplicationContext
│   └── AnnotationConfigApplicationContext
├── exception
│   ├── BeansException
│   ├── BeanCreationException
│   ├── BeanDefinitionNotFoundException
│   ├── NoSuchBeanDefinitionException
│   └── NoUniqueBeanDefinitionException
├── factory
│   ├── SingletonBeanRegistry
│   └── DefaultSingletonBeanRegistry
├── scanner
│   └── ClassPathScanner
└── utils
    ├── Assert
    └── StringUtils
```

## 设计说明

### 元数据注册与对象创建分离

扫描阶段只注册 BeanDefinition，不立即创建对象。完成全部定义注册后，再统一预实例化单例：

```text
扫描阶段：Class → BeanDefinition → Registry
创建阶段：BeanDefinition → Bean 实例 → Singleton Registry
```

这样 `A` 依赖 `B` 时，不会因为类路径先扫描到 `A` 而找不到尚未注册的 `B`。

### 候选发现与实例化分离

按类型查找时，容器先读取 BeanDefinition 的类型元数据并收集全部候选名称。只有候选唯一时，
才进入 `getBean(String)` 创建流程。多候选错误不会提前执行构造器或污染单例缓存。

### 循环依赖检测

容器使用线程级有序路径记录当前调用链中的 Bean。准备创建的 Bean 已经存在于路径中时，
容器截取闭环部分并抛出包含完整依赖顺序的 `BeanCreationException`：

```text
创建 a：路径 [a]
→ 创建 b：路径 [a, b]
→ 再次请求 a：检测到 a -> b -> a
```

路径在 `finally` 中按栈顺序清理，创建失败不会污染同一线程的后续请求。当前实现只负责检测并
拒绝循环依赖，尚未通过早期 Bean 引用解决循环依赖。

### 当前并发边界

- BeanDefinition 注册和名称快照使用同一实例锁
- `ConcurrentHashMap` 保证单次缓存操作安全
- 已完成单例缓存的锁外快速查询和锁内二次查询
- 专用创建监视器保护单例的实例化、依赖注入和缓存注册
- 并发获取同名单例时只实例化一次，并向所有调用者返回同一对象
- 当前使用 BeanFactory 级监视器，不同单例的首次创建仍会串行执行

## 当前限制

- 仅支持字段注入，不支持构造器和方法注入
- 多候选场景尚无 `@Primary` / `@Qualifier` 选择规则
- 能够检测并拒绝循环依赖，但尚未通过早期 Bean 引用解决
- 不支持 Bean 初始化与销毁回调
- 不支持 BeanPostProcessor / BeanFactoryPostProcessor
- 类路径扫描仅支持文件目录，不支持 JAR
- 不支持 `@Configuration`、`@Bean` 和 `@Value`
- 默认要求组件具有可访问的无参构造器

## 后续路线

详细的版本规划、工作量权重、课程拆分和验收标准见 [ROADMAP.md](ROADMAP.md)。
当前完成 3/8 个版本里程碑，按里程碑数量为 37.5%，按工作量加权为 30%。

- [x] 循环依赖检测，避免以 `StackOverflowError` 失败（v0.2.0）
- [x] 单例创建的并发唯一性（v0.3.0）
- [ ] 构造器注入和依赖选择规则
- [ ] Bean 生命周期与 `BeanPostProcessor`
- [ ] 三级缓存和早期 Bean 引用
- [ ] JDK 动态代理与 AOP
- [ ] Environment、资源加载和事件发布

## 开发记录

| 日期 | 进展 |
|---|---|
| 2026-07-15 | 添加 `@Component`、`@Autowired` 和文件目录类路径扫描器 |
| 2026-07-16 | 实现 BeanDefinition 注册表、单例缓存和 ApplicationContext |
| 2026-07-20 | 分离 BeanDefinition 注册与单例预实例化阶段 |
| 2026-07-20 | 实现字段依赖注入、异常体系和 JUnit 5 集成测试 |
| 2026-07-21 | 支持父类私有字段注入 |
| 2026-07-22 | 增加唯一候选解析和明确的多候选异常 |
| 2026-07-24 | 完善 BeanDefinition 注册契约、并发一致性测试和首版发布文档 |
| 2026-07-24 | v0.2.0：增加循环依赖检测、完整闭环路径和异常后状态清理 |
| 2026-07-24 | v0.3.0：保证并发获取同名单例时只实例化一次 |

## 开发约定

每完成一个学习里程碑，按以下顺序收尾：

1. 运行完整自动化测试并确认全部通过
2. 更新 README 和 ROADMAP 的当前进度、能力、限制、路线图和开发记录
3. 更新 CHANGELOG，记录新增能力、行为变化和已知限制
4. 核对版本号、Git 状态和发布标签

整体规划详见 [ROADMAP.md](ROADMAP.md)，版本变更详见 [CHANGELOG.md](CHANGELOG.md)。

## License

本项目使用 [Apache License 2.0](LICENSE)。

---

*上班摸鱼，快乐编程。* 🐟
