# Changelog

本文件记录 mini-spring 各个学习里程碑的主要变化。

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
