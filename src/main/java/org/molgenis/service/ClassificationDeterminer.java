package org.molgenis.service;

import org.molgenis.model.Classification;

public class ClassificationDeterminer {
    public Classification determine(String umcClassification) {
        Classification classification;
        switch (umcClassification) {
            case "BENIGN":
                classification = Classification.BENIGN;
                break;
            case "LIKELY_BENIGN":
                classification = Classification.LIKELY_BENIGN;
                break;
            case "VOUS":
                classification = Classification.VOUS;
                break;
            case "LIKELY_PATHOGENIC":
                classification = Classification.LIKELY_PATHOGENIC;
                break;
            case "PATHOGENIC":
                classification = Classification.PATHOGENIC;
                break;
            default:
                classification = Classification.NO_CLASSIFICATION;
        }
        return classification;
    }
}
