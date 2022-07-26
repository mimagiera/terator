package com.terator;

import com.terator.service.TeratorExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationWithOptimization {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.terator");
        var teratorExecutor = context.getBean(TeratorExecutor.class);
        var mockPath = "D:\\agh\\magisterka\\terator\\src\\main\\resources\\krk_min.osm.pbf";
        teratorExecutor.execute(mockPath, 3);
    }
}
