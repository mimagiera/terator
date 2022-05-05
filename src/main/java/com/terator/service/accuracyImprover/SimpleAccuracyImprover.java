package com.terator.service.accuracyImprover;

import com.terator.model.GeneratedTrajectoriesAccuracy;
import com.terator.model.generatorTable.Probabilities;
import org.springframework.stereotype.Service;

@Service
public class SimpleAccuracyImprover implements AccuracyImprover {
    @Override
    public void improve(Probabilities probabilities, GeneratedTrajectoriesAccuracy accuracy
    ) {

    }
}
