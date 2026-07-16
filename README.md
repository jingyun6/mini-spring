# mini-spring

个人小项目，实现一些 Spring 的基本功能，只有在上班摸鱼时写一下。

---

## 项目概述

mini-spring 是一个精简版的 IoC（控制反转）容器，旨在通过手写核心逻辑来深入理解 Spring Framework 的设计思想。项目从零开始构建，逐步实现 Bean 定义管理、类路径扫描、依赖注入等 Spring 核心功能。

## 技术栈

- **语言**: Java 21
- **构建工具**: Gradle 9.3 (Kotlin DSL)
- **外部依赖**: 无（纯 JDK 实现，不依赖任何第三方库）

## 项目架构

```
io.github.youhong.minispring
├── annotation          # 自定义注解
│   ├── Autowired       # 自动装配（已定义，DI 逻辑待实现）
│   └── Component       # 组件标记（用于类路径扫描发现）
├── beans               # Bean 定义与工厂核心
│   ├── BeanFactory             # IoC 容器根接口（getBean）
│   ├── BeanDefinition          # Bean 元数据模型（class, name, scope）
│   ├── BeanDefinitionRegistry  # Bean 定义注册表接口
│   └── DefaultListableBeanFactory  # 核心容器实现（集大成者）
├── context             # 应用上下文
│   ├── ApplicationContext              # ApplicationContext 门面接口
│   └── AnnotationConfigApplicationContext  # 注解驱动的启动入口
├── factory             # 单例管理
│   ├── SingletonBeanRegistry        # 单例注册表接口
│   └── DefaultSingletonBeanRegistry # 单例注册表实现（ConcurrentHashMap）
├── scanner             # 类路径扫描
│   └── ClassPathScanner  # @Component 类扫描器
├── exception           # 异常体系
│   └── BeanDefinitionNotFoundException  # Bean 定义未找到异常
├── utils               # 工具类
│   ├── Assert       # 参数断言（fail-fast）
│   └── StringUtils  # 字符串工具（首字母小写）
└── demo               # 演示用例
    ├── Test         # 集成测试入口
    └── UserService  # 演示用 @Component Bean
```

### 类层次结构

```
                    BeanFactory                    SingletonBeanRegistry
                   ↑ (getBean)                    ↑ (单例缓存)
                   |                              |
     ApplicationContext              DefaultSingletonBeanRegistry
              ↑                                  ↑
              |                  继承              |
    AnnotationConfigApplicationContext ──── DefaultListableBeanFactory
                                              ↑
                                    BeanDefinitionRegistry (Bean定义管理)
```

**核心设计：** `DefaultListableBeanFactory` 是整个框架的中枢类，同时承担三项职责：
1. **Bean 定义管理** — 实现 `BeanDefinitionRegistry`，维护 beanName → BeanDefinition 映射
2. **Bean 实例获取** — 实现 `BeanFactory`，按名称/类型获取已装配的 Bean
3. **单例缓存** — 继承 `DefaultSingletonBeanRegistry`，复用线程安全的单例存储

## 已实现功能

### ✅ 1. Bean 定义注册与管理
- `BeanDefinition` — Bean 元数据模型（class、beanName、singleton）
- `BeanDefinitionRegistry` — 注册/查找/存在性检查
- `DefaultListableBeanFactory` — 使用 `ConcurrentHashMap` 存储，线程安全
- 重复注册检测，快速失败

### ✅ 2. 单例管理
- `SingletonBeanRegistry` — 单例注册、获取、存在性检查
- `DefaultSingletonBeanRegistry` — 基于 `ConcurrentHashMap` 的一级缓存
- 不允许重复注册同名单例

### ✅ 3. 类路径扫描
- `ClassPathScanner` — 递归扫描指定包下的 `.class` 文件
- 自动过滤：只收集 `@Component` 标注的类
- 自动跳过内部类（文件名含 `$`）
- URL 解码（处理路径中的特殊字符）

### ✅ 4. 注解支持
- `@Component` — 标记类为 IoC 管理的 Bean
- `@Autowired` — 字段级自动装配（注解已定义，DI 逻辑待实现）

### ✅ 5. 注解驱动启动
- `AnnotationConfigApplicationContext` — 传入包路径即可启动容器
- 自动扫描 → 注册 BeanDefinition → 预实例化单例

### ✅ 6. Bean 获取
- `getBean(String)` — 按名称获取（优先走单例缓存）
- `getBean(Class<T>)` — 按类型获取（使用 `isAssignableFrom` 支持子类/接口匹配）

### ✅ 7. 异常体系
- `BeanDefinitionNotFoundException` — Bean 定义不存在时抛出
- `IllegalArgumentException` — 参数校验失败
- `IllegalStateException` — Bean 定义重复注册

### ✅ 8. 工具类
- `Assert` — 运行时参数断言
- `StringUtils` — 字符串首字母小写（用于生成默认 Bean 名称）

## 快速开始

```java
// 1. 定义 Bean
@Component
public class UserService {
    public void load() {
        System.out.println("UserService loaded!");
    }
}

// 2. 启动容器
ApplicationContext ctx = new AnnotationConfigApplicationContext("com.example");

// 3. 获取 Bean
UserService userService = ctx.getBean(UserService.class);
userService.load();
```

运行 `io.github.youhong.minispring.demo.Test.main()` 可查看集成测试结果。

## 待实现功能

### 🔲 依赖注入（DI）
- 处理 `@Autowired` 注解，自动注入依赖的 Bean
- 支持字段注入（Field Injection）
- 循环依赖检测与处理

### 🔲 Bean 生命周期
- Bean 后处理器（`BeanPostProcessor`）
- `@PostConstruct` / `@PreDestroy` 回调
- `InitializingBean` / `DisposableBean` 接口
- 原型作用域（prototype scope）

### 🔲 扩展容器能力
- `ApplicationContext` 扩展：Environment、ResourceLoader、EventPublisher
- BeanFactory 后处理器（`BeanFactoryPostProcessor`）
- 条件装配（`@Conditional`）

### 🔲 三级缓存
- 解决循环依赖的 singletonFactory 三级缓存机制

### 🔲 AOP 支持
- 动态代理（JDK / CGLib）
- `@Aspect` / `@Before` / `@After` 注解
- 切面织入

### 🔲 类路径扫描增强
- 支持扫描 JAR 包内的类
- 支持自定义包含/排除过滤器
- 支持 `@ComponentScan` 注解配置扫描规则

### 🔲 其他
- 构造器注入
- `@Value` 属性占位符解析
- `@Configuration` + `@Bean` Java Config 支持
- 单元测试覆盖

## 开发日志

| 日期 | 进展 |
|------|------|
| 2026-07-15 | 添加注解定义（@Component, @Autowired）和类路径扫描器 |
| 2026-07-16 | 实现 BeanDefinition 注册管理、单例缓存、ApplicationContext 启动流程 |
| 2026-07-16 | 修复扫描器递归时的包名拼接 bug、getBean 重复注册单例 bug |
| 2026-07-16 | 全项目补全 JavaDoc 注释，更新 README |

---

*上班摸鱼，快乐编程。* 🐟
