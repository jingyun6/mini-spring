# Changelog

本文件记录 mini-spring 各个学习里程碑的主要变化。

## [Unreleased]

### Added

- 唯一构造器的隐式构造器注入
- 多构造器场景中的无参构造器回退规则
- 构造器参数通过 `BeanFactory#getBean(Class)` 统一解析
- 构造器缺失和构造器歧义的诊断性 `BeanCreationException`
- 4 个构造器选择与注入契约测试，自动化测试总数增加到 30 个

### Changed

- `@Autowired` 可以声明在字段和构造器上
- Bean 实例化拆分为构造器选择、参数解析和反射调用三个职责
- 无参和有参构造器统一执行访问处理和反射调用

### Documentation

- README 同步到 v0.3.0 的实际能力、限制和完成进度
- 新增 ROADMAP，记录整体权重、版本课程拆分和验收标准
- 固化每课完成后同步测试、README、ROADMAP、CHANGELOG 和版本状态的收尾流程
- 同步 v0.4.0 第一课的构造器注入进度、限制、设计说明和下一课安排

### Known limitations

- 多个构造器尚不能通过 `@Autowired` 显式选择
- 构造器和字段依赖仍只支持按类型选择，不支持 `@Primary` / `@Qualifier`

## [0.3.0] - 2026-07-24

单例并发创建安全版本。

### Added

- BeanFactory 级单例创建监视器
- 单例缓存的锁外快速查询和锁内二次查询
- 16 个并发请求只创建一个实例并返回同一引用的自动化测试
- 单例并发创建与循环依赖检测的源码设计说明

### Changed

- singleton Bean 的检查、创建、依赖注入和注册在同一监视器内完成
- prototype Bean 绕过单例创建监视器，每次请求独立创建
- 更新容器、应用上下文和注入注解中关于并发与循环依赖的说明

### Known limitations

- 不同 singleton Bean 的首次创建仍使用同一个 BeanFactory 级监视器串行执行
- 尚未通过早期 Bean 引用解决循环依赖

## [0.2.0] - 2026-07-24

循环依赖检测版本。

### Added

- 基于 `ThreadLocal<Deque<String>>` 的线程级 Bean 创建路径
- 直接自依赖和多个 Bean 相互依赖检测
- 包含完整闭环顺序的循环依赖错误信息
- 创建失败后的路径清理与重试测试

### Changed

- 循环依赖不再以 `StackOverflowError` 失败，改为抛出 `BeanCreationException`
- Bean 创建路径在 `finally` 中清理，空路径对应的 ThreadLocal 会被移除

### Known limitations

- 当前只检测并拒绝循环依赖，尚未通过早期 Bean 引用解决
- 本版本尚未保证并发获取同一单例时只实例化一次

## [0.1.0] - 2026-07-24

首个可发布的 IoC 容器基础版本。

### Added

- `@Component` 组件扫描
- `@Autowired` 字段依赖注入
- 父类私有字段注入
- BeanDefinition 注册表和单例一级缓存
- 注解驱动的 `ApplicationContext`
- 按名称和按类型获取 Bean
- 缺失定义、多候选和 Bean 创建异常
- BeanFactory 单元测试与 ApplicationContext 集成测试
- BeanDefinition 并发注册一致性测试
- Gradle Wrapper

### Changed

- BeanDefinition 注册与 Bean 创建分阶段执行
- 按类型查找先收集候选元数据，再决定是否创建 Bean
- 同名 BeanDefinition 不再静默覆盖
- BeanDefinition 名称查询返回防御性副本

### Known limitations

- 尚未检测或解决循环依赖
- 尚未实现构造器注入和多候选选择规则
- 尚未实现 Bean 生命周期扩展点和 AOP
- 尚未保证并发获取同一单例时只实例化一次
- 类路径扫描暂不支持 JAR
