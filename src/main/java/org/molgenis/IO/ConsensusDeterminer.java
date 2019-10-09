package org.molgenis.IO;

import org.molgenis.model.Classification;
import org.molgenis.model.ConsensusVariant;

import java.util.ArrayList;
import java.util.Map;

public class ConsensusDeterminer {
    private ArrayList<ConsensusVariant> variants;

    public ConsensusDeterminer (ArrayList<ConsensusVariant> variants) {
        this.variants = variants;
        determineConsensus();
    }

    private void determineConsensus() {
        for (ConsensusVariant variant : variants) {
            Map<String, Classification> classifications =  variant.getClassificationMap();

        }
    }
}
