package com.terator.service.accuracyImprover;

import com.terator.model.generatorTable.Probabilities;

public interface AccuracyImprover {
    void improve(Probabilities probabilities, String accuracy);
}
