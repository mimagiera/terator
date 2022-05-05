package com.terator.controler;

import com.terator.model.generatorTable.Probabilities;
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
    public Probabilities getTrajectories(@RequestParam String fileName) {
        var mockPath = "D:\\agh\\magisterka\\terator\\src\\main\\resources\\map.osm.pbf";

        return teratorExecutor.execute(mockPath);
    }

}