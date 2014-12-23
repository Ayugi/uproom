package ru.uproom.gate.localinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by osipenko on 23.12.14.
 */
public class Main {


    //##############################################################################################################
    //######    fields


    private static final Logger LOG = LoggerFactory.getLogger(Main.class);


    //##############################################################################################################
    //######    entry point


    public static void main(String[] args) {

        LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        LOG.info("Gate Local starting ... ");
        // spring initialization
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");

    }
}
