package com.terator.controler;

import com.terator.service.TeratorExecutorJMetal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@RestController
@RequiredArgsConstructor
public class GeneratorController {

    private final TeratorExecutorJMetal teratorExecutor;

    @GetMapping("/")
    public DoubleSolution getTrajectories(@RequestParam String fileName) {
        return teratorExecutor.execute(fileName, 1, 1);
    }

}
