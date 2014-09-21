package ru.uproom.gate.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by HEDIN on 12.09.2014.
 */
public class ClassesSearcher {

    // logger
    private static final Logger LOG = LoggerFactory.getLogger(ClassesSearcher.class);

    // find classes with some annotation
    public static List<Class> getAnnotatedClasses(String packName, Class<? extends Annotation> annotation) {
        List<Class> useClasses = new ArrayList<Class>();

        Class[] allClasses = new Class[]{};
        try {
            allClasses = getClasses(packName);
        } catch (ClassNotFoundException e) {
            LOG.debug("[ERR] >>> ClassesSearcher >>> Class Not Found >>> {}", e);
        } catch (IOException e) {
            LOG.debug("[ERR] >>> ClassesSearcher >>> I/O Exception >>> {}", e);
        }

        for (Class clazz : allClasses) {
            if (clazz.isAnnotationPresent(annotation)) useClasses.add(clazz);
        }

        return useClasses;
    }


    private static List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {

        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    // return new instance
    public static Object instantiate(Class<?> handler) {
        try {
            return handler.newInstance();
        } catch (InstantiationException e) {
            LOG.error("unexpected InstantiationException instantiating " + handler, e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            LOG.error("unexpected IllegalAccessException instantiating " + handler, e);
            throw new RuntimeException(e);
        }
    }

}
