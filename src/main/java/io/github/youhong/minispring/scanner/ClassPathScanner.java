package io.github.youhong.minispring.scanner;

import io.github.youhong.minispring.annotation.Component;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 类路径扫描器——在指定包下递归查找所有被 {@link Component @Component} 标注的类。
 *
 * <p>该类是 mini-spring 实现"零配置"自动装配的核心组件。通过当前线程的上下文类加载器
 * 定位包对应的物理路径，递归遍历目录下的所有 {@code .class} 文件，筛选出携带
 * {@code @Component} 注解的类并返回。
 *
 * <p><b>扫描策略：</b>
 * <ul>
 *     <li>仅处理文件系统中的 {@code .class} 文件（不处理 JAR 包内的类）</li>
 *     <li>自动跳过内部类（文件名包含 {@code $} 的类）</li>
 *     <li>通过注解过滤——只收集标注了 {@code @Component} 的类</li>
 *     <li>URL 解码处理——处理路径中可能存在的特殊字符（如空格编码为 {@code %20}）</li>
 * </ul>
 *
 * <p><b>当前限制：</b>
 * <ul>
 *     <li>不支持扫描 JAR 包内的类</li>
 *     <li>不支持自定义过滤器（如 Spring 的 {@code TypeFilter}）</li>
 *     <li>不支持排除规则（如 Spring 的 {@code @ComponentScan#excludeFilters}）</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * ClassPathScanner scanner = new ClassPathScanner();
 * Set<Class<?>> components = scanner.scan("com.example.service");
 * for (Class<?> clazz : components) {
 *     System.out.println("Found component: " + clazz.getName());
 * }
 * }</pre>
 *
 * @author PengYouHong
 * @version 1.0.0
 * @since 2026/7/15 16:13
 * @see Component
 */
public class ClassPathScanner {

    /**
     * 扫描指定包路径下的所有 {@code @Component} 类。
     *
     * <p>通过当前线程的上下文类加载器定位包对应的物理路径，
     * 递归遍历目录下所有 {@code .class} 文件，筛选并加载带有
     * {@link Component @Component} 注解的类。
     *
     * @param basePackage 待扫描的包名，使用 {@code .} 作为分隔符（如 {@code com.example.service}）
     * @return 包下所有被 {@code @Component} 标注的类对象集合；若包下无匹配类则返回空集合
     * @throws RuntimeException 当指定的包路径在类路径中不存在时抛出
     */
    public Set<Class<?>> scan(String basePackage) {

        Set<Class<?>> classes = new HashSet<>();

        // 将包名转换为路径格式（com.example → com/example）
        String packagePath = basePackage.replace('.', '/');

        // 通过上下文类加载器定位包对应的物理目录
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(packagePath);

        if (resource == null) {
            throw new RuntimeException("Package not found: " + basePackage);
        }

        // URL 解码处理路径中的特殊字符
        String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
        File root = new File(filePath);

        // 递归扫描目录树
        scanDirectory(root, basePackage, classes);

        return classes;
    }

    /**
     * 递归扫描目录，查找并收集所有 {@code @Component} 类。
     *
     * <p>核心逻辑：
     * <ol>
     *     <li>若为子目录 → 将子目录名追加到包名，递归进入</li>
     *     <li>若为非 {@code .class} 文件 → 跳过</li>
     *     <li>若为内部类（文件名含 {@code $}）→ 跳过</li>
     *     <li>若为普通 {@code .class} 文件 → 加载并检查是否带 {@code @Component} 注解</li>
     * </ol>
     *
     * @param directory   当前待扫描的文件或目录
     * @param packageName 当前递归层级对应的完整包名，用于构建类的全限定名
     * @param classes     用于收集已加载的 {@link Class} 对象的集合
     */
    private void scanDirectory(File directory, String packageName, Set<Class<?>> classes) {

        File[] files = directory.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            // 子目录：将目录名追加到包名，递归进入下一层
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
                continue;
            }

            // 跳过非 class 文件
            if (!file.getName().endsWith(".class")) {
                continue;
            }

            // 跳过内部类（文件名含 $，如 Outer$Inner.class）
            if (file.getName().contains("$")) {
                continue;
            }

            // 构建全限定类名并加载
            String className = packageName + "." + file.getName().replace(".class", "");

            try {
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);

                // 仅收集标注了 @Component 的类
                if (clazz.isAnnotationPresent(Component.class)) {
                    classes.add(clazz);
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load class: " + className, e);
            }
        }
    }

}