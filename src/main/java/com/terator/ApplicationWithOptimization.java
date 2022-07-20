package com.terator;

import com.terator.service.TeratorExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationWithOptimization {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.terator");
        var a = context.getBean(TeratorExecutor.class);
        var mockPath = "C:\\magisterka\\terator\\map_czarnowiejska.osm.pbf";
        a.execute(mockPath);
    }
}
