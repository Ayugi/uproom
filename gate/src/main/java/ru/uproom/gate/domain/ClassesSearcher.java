package ru.uproom.gate.domain;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by HEDIN on 12.09.2014.
 */
public class ClassesSearcher {

    // logger
    private static final Logger LOG = LoggerFactory.getLogger(ClassesSearcher.class);

    // find classes with some annotation
    public static Collection<Class<?>> findAnnotatedClasses(Class annotation) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath())
        );
        return reflections.getTypesAnnotatedWith(annotation);
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
