package ru.uproom.gate.notifications;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.util.*;

/**
 * Created by HEDIN on 12.09.2014.
 */
public class ClassesSearcher {
    public static Collection<Class<?>> findAnnotatedClasses(Class annotation) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath())
        );
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
