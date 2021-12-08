package com.terator.generator;

import com.terator.model.SingleTrajectory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GeneratorController {

    private final TrajectoriesGenerator trajectoriesGenerator;

    public GeneratorController(TrajectoriesGenerator trajectoriesGenerator) {
        this.trajectoriesGenerator = trajectoriesGenerator;
    }

    @GetMapping("/")
    public List<SingleTrajectory> getTrajectories(@RequestParam String fileName) {
        var mockPath = "/Users/mmagiera/agh/magisterka/terator/src/main/java/com/terator/andorra-latest.osm.pbf";
        return trajectoriesGenerator.createTrajectoriesForFile(mockPath);
    }

}
