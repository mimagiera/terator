package com.terator.controler;

import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.service.TeratorExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GeneratorController {

    private final TeratorExecutor teratorExecutor;

    @GetMapping("/")
    public GeneratedTrajectoriesAccuracy getTrajectories(@RequestParam String fileName) {
        var mockPath = "C:\\magisterka\\terator\\map_czarnowiejska.osm.pbf";

        return teratorExecutor.execute(mockPath);
    }

}
