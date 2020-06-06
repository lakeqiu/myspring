package org.myspringframework.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lakeqiu
 */
@Slf4j
public class ClassUtil {

    public static final String FILE_PROTOCOL = "file";

    /**
     * 获取包下类集合
     * @param packageName 包名
     * @return 类集合
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        // 1、获取项目类加载器
        ClassLoader classLoader = getClassLoader();

        // 2、通过类加载器加载要获取的资源
        // packageName中是用.来区分类的路径的，但是系统是使用'/'的，所以需要替换一下
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        // packageName下没有资源，记录日志，返回
        if (url == null) {
            log.warn("packageName下没有可以加载的资源");
            return null;
        }

        // 3、依据不同的资源类型，采用不同的方式获取资源的集合
        // 里面有文件夹、不同类型的文件。对于文件夹，我们需要再遍历，获取其下的文件
        // 对于文件，我们只要class文件，对其进行加载，获取类对象
        Set<Class<?>> classSet = null;
        // 过滤出文件类型的资源
        if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
            classSet = new HashSet<>();
            File packageDirectory = new File(url.getPath());
            extractClassFile(classSet, packageDirectory, packageName);
        }
        return classSet;
    }

    /**
     * 递归获取目标包内所有的class文件（包含子包内的class文件）
     * @param classSet 装载目标类的集合
     * @param fileSource 文件或目录
     * @param packageName 包名
     */
    private static void extractClassFile(Set<Class<?>> classSet, File fileSource, String packageName) {
        // 递归基：如果不是目录，结束
        if (!fileSource.isDirectory()) {
            return;
        }

        // 如果是一个文件夹，则调用其listFiles获取文件夹的的文件夹
        // 在过滤中，如果发现是class类型的文件，我们直接加载装入classSet中
        File[] files = fileSource.listFiles(new FileFilter() {
            // 过滤器，如果是文件夹的话则放入files中,之后再继续遍历，class文件则直接加载进classSet中
            @Override
            public boolean accept(File file) {
                // 是文件夹
                if (file.isDirectory()) {
                    return true;
                } else { // 其他文件
                    // 获取文件的绝对路径
                    String absoluteFilePath = file.getAbsolutePath();

                    if (absoluteFilePath.endsWith(".class")) {
                        // 如果是class文件，直接加载
                        addToClassSet(absoluteFilePath);
                    }
                }

                // 其他文件，直接忽略
                return false;
            }

            // 根据class文件的绝对路径，获取并生成class对象，并放入classSet中
            private void addToClassSet(String absoluteFilePath) {
                // 1.从class文件的绝对值路径里提取出包含了package的类名
                // 如/Users/baidu/imooc/springframework/sampleframework/target/classes/com/imooc/entity/dto/MainPageInfoDTO.class
                // 需要弄成com.imooc.entity.dto.MainPageInfoDTO
                // 将文件分隔符，替换为"."
                absoluteFilePath = absoluteFilePath.replace(File.separator, ".");
                // 截取类名
                String className = absoluteFilePath.substring(absoluteFilePath.indexOf(packageName));
                // 去除.class
                className = className.substring(0, className.lastIndexOf("."));
                // 2.通过反射机制获取对应的Class对象并加入到classSet里
                Class targetClass = loadClass(className);
                classSet.add(targetClass);
            }
        });

        // files不为空的话，我们继续递归遍历
        if (files != null) {
            for (File file : files) {
                extractClassFile(classSet, file, packageName);
            }
        }
    }

    /**
     * 获取class类
     * @param className class全名=package + 类名
     * @return
     */
    public static Class loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("加载class出错:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前ClassLoader
     * @return 当前ClassLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
