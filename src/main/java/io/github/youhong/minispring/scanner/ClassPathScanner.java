package io.github.youhong.minispring.scanner;


import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 类路径扫描器
 * </p>
 * <p>
 * 用于扫描指定包下面的所有类
 * </p>
 *
 * @author PengYouHong
 * @version 1.0.0
 * @since 2026/7/15 16:13
 */
public class ClassPathScanner {
    /**
     * 扫描指定包路径下的所有类文件，将其加载为 {@link Class} 对象并返回。
     * <p>通过当前线程的上下文类加载器定位包对应的物理路径，递归遍历目录下所有
     * {@code .class} 文件，逐个加载并收集。</p>
     *
     * @param basePackage 待扫描的包名，使用 {@code .} 作为分隔符（如 {@code com.example.service}）
     * @return 包下所有成功加载的类对象集合，若包下无类文件则返回空集合
     * @throws RuntimeException 当指定的包路径在类路径中不存在时抛出
     */
    public Set<Class<?>> scan(String basePackage) {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = basePackage.replace(".", "/");

        //获取当前线程的上下文类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(packagePath);
        if (resource == null) {
            throw new RuntimeException("包不存在：" + basePackage);
        }
        File directory = new File(resource.getFile());
        scanDirectory(directory, basePackage, classes);
        //获取file下面的所有class
        return classes;
    }

    /**
     * 递归扫描指定目录下的所有 {@code .class} 文件，将其加载并收集到给定的类集合中。
     * <p>若当前文件对象为目录，则遍历其子文件递归调用自身；若为 {@code .class} 文件，
     * 则通过上下文类加载器加载该类并加入结果集合。</p>
     *
     * @param directory   待扫描的文件或目录，不可为 {@code null}
     * @param basePackage 当前递归层级对应的包名，使用 {@code .} 作为分隔符
     * @param classes     用于收集已加载的 {@link Class} 对象的集合，扫描结果会直接写入此集合
     */
    private void scanDirectory(File directory, String basePackage, Set<Class<?>> classes) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                return;
            }
            for (File file : files) {
                scanDirectory(file, basePackage, classes);
            }

        } else {
            //判断是不是class文件
            String directoryName = directory.getName();
            if (directoryName.endsWith(".class")) {
                String className = basePackage + "." + directoryName.replace(".class", "");
                try {
                    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
