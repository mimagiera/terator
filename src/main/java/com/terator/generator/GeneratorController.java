package com.terator.generator;

import com.terator.model.SingleTrajectory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class GeneratorController {

    private final TrajectoriesFromFileGenerator trajectoriesFromFileGenerator;

    public GeneratorController(TrajectoriesFromFileGenerator trajectoriesFromFileGenerator) {
        this.trajectoriesFromFileGenerator = trajectoriesFromFileGenerator;
    }

    @GetMapping("/")
    public List<SingleTrajectory> getTrajectories(@RequestParam String fileName) throws IOException {
        var mockPath = "D:\\agh\\magisterka\\terator\\src\\main\\resources\\map.osm.pbf";

        return trajectoriesFromFileGenerator.createTrajectoriesForFile(mockPath);
    }

}
