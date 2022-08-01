package com.terator;

import com.terator.service.TeratorExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationWithOptimization {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.terator");
        var teratorExecutor = context.getBean(TeratorExecutor.class);
        var osmFilePath = "krk_min.osm.pbf";
        teratorExecutor.execute(osmFilePath, 8);
    }
}
