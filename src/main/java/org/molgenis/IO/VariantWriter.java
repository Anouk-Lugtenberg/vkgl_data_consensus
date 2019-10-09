package org.molgenis.IO;

import org.molgenis.model.Classification;
import org.molgenis.model.ConsensusVariant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class VariantWriter {

    public VariantWriter(File fileToWrite, ArrayList<ConsensusVariant> variants) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite));
            writer.append("chr\tpos\tref\talt\tamc\terasmus\tlumc\tnki\tradboud\tumcg\tumcu\tvumc\n");
            writer.close();
            for (ConsensusVariant variant : variants) {
                String line = createVariantLine(variant);
                BufferedWriter writerVariants = new BufferedWriter(new FileWriter(fileToWrite, true));
                writerVariants.append(line);
                writerVariants.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createVariantLine(ConsensusVariant variant) {
        return variant.getChr() +
                "\t" +
                variant.getPos() +
                "\t" +
                variant.getREF() +
                "\t" +
                variant.getALT() +
                "\t" +
                convertNoClassificationToNull(variant.getAMC()) +
                "\t" +
                convertNoClassificationToNull(variant.getErasmus()) +
                "\t" +
                convertNoClassificationToNull(variant.getLUMC()) +
                "\t" +
                convertNoClassificationToNull(variant.getNKI()) +
                "\t" +
                convertNoClassificationToNull(variant.getRadboud()) +
                "\t" +
                convertNoClassificationToNull(variant.getUMCG()) +
                "\t" +
                convertNoClassificationToNull(variant.getUMCU()) +
                "\t" +
                convertNoClassificationToNull(variant.getVUMC()) +
                "\n";
    }

    private String convertNoClassificationToNull(Classification UMCClassification) {
        if (UMCClassification == Classification.NO_CLASSIFICATION) {
            return null;
        } else {
            return UMCClassification.toString();
        }
    }
}
