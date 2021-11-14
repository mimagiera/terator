package com.terator.generator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneratorController {

    @GetMapping("/")
    public String getTrajectories() {
        return "Trajectories";
    }

}
