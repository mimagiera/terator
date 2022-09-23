package com.terator;

import com.terator.service.TeratorExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationWithCheckingDays {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.terator");
        var teratorExecutor = context.getBean(TeratorExecutor.class);
        var osmFilePath = "krk_min.osm.pbf";

        if (args.length == 0) {
            args = new String[]{"0"};
        }

        teratorExecutor.execute(osmFilePath, Integer.parseInt(args[0]));

    }
}
