package ru.uproom.gate.transport.domain;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by HEDIN on 12.09.2014.
 */
public class ClassesSearcher {

    // logger
    private static final Logger LOG = LoggerFactory.getLogger(ClassesSearcher.class);

    public static List<Class> getAnnotatedClasses(Class<? extends Annotation> annotation) {

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath())
        );
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(annotation);

        return new ArrayList<Class>(annotated);
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
