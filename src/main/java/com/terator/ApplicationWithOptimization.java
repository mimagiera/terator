package com.terator;

import com.terator.service.TeratorExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationWithOptimization {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationWithOptimization.class);

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.terator");
        var teratorExecutor = context.getBean(TeratorExecutor.class);
        var osmFilePath = "krk_min.osm.pbf";
        int concurrentSimulations = 3;
        int concurrentRoutesGenerator = 2;
        if (args.length == 1) {
            concurrentSimulations = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            concurrentSimulations = Integer.parseInt(args[0]);
            concurrentRoutesGenerator = Integer.parseInt(args[1]);
        }

        LOGGER.info(
                "concurrent simulations: {}, concurrent routes generator: {}",
                concurrentSimulations,
                concurrentRoutesGenerator
        );

        teratorExecutor.execute(osmFilePath, concurrentSimulations, concurrentRoutesGenerator);
    }
}
