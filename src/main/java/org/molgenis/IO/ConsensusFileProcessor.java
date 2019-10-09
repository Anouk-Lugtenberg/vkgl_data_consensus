package org.molgenis.IO;

import org.molgenis.model.Classification;
import org.molgenis.model.ConsensusVariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsensusFileProcessor {
    private File consensusFile;
    private Map<String, Integer> conflictingTiersTwoClassifications = new HashMap<>();
    private Map<String, Integer> conflictingTiersThreeClassifications = new HashMap<>();
    private Map<String, Integer> conflictingTiersFourClassifications = new HashMap<>();
    private Map<String, Integer> conflictingThreeTiersTwoClassifications = new HashMap<>();
    private ArrayList<ConsensusVariant> conflictingVariantsThreeTierBenignPathogenic = new ArrayList<>();

    public ConsensusFileProcessor(File consensusFile) {
        this.consensusFile = consensusFile;
        ArrayList<ConsensusVariant> consensusVariants = processFile(consensusFile);
        ArrayList<ConsensusVariant> consensusVariantsMultipleEntries = deleteSingleEntries(consensusVariants);
        processConsensusFileWithFiveTiers(consensusVariantsMultipleEntries);
    }

    private void processConsensusFileWithFiveTiers(ArrayList<ConsensusVariant> consensusVariants) {
        System.out.println("#### FIVE TIER CLASSIFICATION SYSTEM ####");
        ArrayList<ConsensusVariant> noConsensusVariants = determineConsensusMultipleEntries(consensusVariants);
        processConsensusFileWithThreeTiers(noConsensusVariants);
    }

    private void processConsensusFileWithThreeTiers(ArrayList<ConsensusVariant> consensusVariants) {
        System.out.println("#### THREE TIER CLASSIFICATION SYSTEM ####");
        determineConsensusThreeTier(consensusVariants);

    }

    private void determineConsensusThreeTier(ArrayList<ConsensusVariant> consensusVariants) {
        File directory = new File(consensusFile.getParentFile() + File.separator + "threeTier");
        directory.mkdir();
        ArrayList<ConsensusVariant> consensusThreeTierSystem = new ArrayList<>();
        ArrayList<ConsensusVariant> noConsensusTwoClassification = new ArrayList<>();
        ArrayList<ConsensusVariant> noConsensusMoreThanTwoClassifications = new ArrayList<>();
        createConflictingFiveTiersTwoClassificationsMap();
        for (ConsensusVariant variant : consensusVariants) {
            variant.createThreeTiers();
            Map<String, Classification> classifications = variant.getClassificationMapThreeTier();
            Set<Classification> setClassifications = new HashSet<>();
            for (Map.Entry<String, Classification> classificationEntry : classifications.entrySet()) {
                setClassifications.add(classificationEntry.getValue());
            }
            if (setClassifications.size() == 1) {
                consensusThreeTierSystem.add(variant);
            } else if (setClassifications.size() == 2) {
                reportConflictingThreeTiersTwoClassifications(setClassifications, variant);
                noConsensusTwoClassification.add(variant);
            } else {
                conflictingVariantsThreeTierBenignPathogenic.add(variant);
                noConsensusMoreThanTwoClassifications.add(variant);
            }
        }
        System.out.println("\n Consensus three tier: " + consensusThreeTierSystem.size());
        System.out.println("\n#### (three-tier) two classifications ####");
        for (Map.Entry<String, Integer> entry : conflictingThreeTiersTwoClassifications.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Number of variants with three classifications (three-tier): " + noConsensusMoreThanTwoClassifications.size());
        for (ConsensusVariant variant : noConsensusMoreThanTwoClassifications) {
            System.out.println("variant = " + variant);
        }

        System.out.println("#### CONSENSUS (three-tier) ####");
        writeConsensusMultipleEntriesToFile(consensusThreeTierSystem, directory);
        countConsensusPerUMC(consensusThreeTierSystem);

        System.out.println("#### NO CONSENSUS TWO CLASSIFICATIONS (three-tier) ####");
        writeNoConsensusTwoClassificationsToFile(noConsensusTwoClassification, directory);
        countConsensusPerUMC(noConsensusTwoClassification);

        System.out.println("#### NO CONSENSUS MORE THAN TWO (three-tier) ####");
        writeNoConsensusMoreThanTwoClassificationsToFile(noConsensusMoreThanTwoClassifications, directory);
        countConsensusPerUMC(noConsensusMoreThanTwoClassifications);

        writeConflictingPathogenicVersusBenign(conflictingVariantsThreeTierBenignPathogenic, directory);
    }

    private void countConsensusPerUMC(ArrayList<ConsensusVariant> variants) {
        int amc = 0;
        int erasmus = 0;
        int lumc = 0;
        int nki = 0;
        int radboud = 0;
        int umcg = 0;
        int umcu = 0;
        int vumc = 0;

        for (ConsensusVariant variant : variants) {
            if (variant.getAMC() != Classification.NO_CLASSIFICATION) {
                amc ++;
            }
            if (variant.getErasmus() != Classification.NO_CLASSIFICATION) {
                erasmus++;
            }
            if (variant.getLUMC() != Classification.NO_CLASSIFICATION) {
                lumc++;
            }
            if (variant.getNKI() != Classification.NO_CLASSIFICATION) {
                nki++;
            }
            if (variant.getRadboud() != Classification.NO_CLASSIFICATION) {
                radboud++;
            }
            if (variant.getUMCG() != Classification.NO_CLASSIFICATION) {
                umcg++;
            }
            if (variant.getUMCU() != Classification.NO_CLASSIFICATION) {
                umcu++;
            }
            if (variant.getVUMC() != Classification.NO_CLASSIFICATION) {
                vumc++;
            }
        }

        System.out.println("AMC: " + amc);
        System.out.println("Erasmus: " + erasmus);
        System.out.println("Lumc: " + lumc);
        System.out.println("NKI: " + nki);
        System.out.println("Radboud: " + radboud);
        System.out.println("umcg: " + umcg);
        System.out.println("umcu: " + umcu);
        System.out.println("vumc: " + vumc + "\n");
    }

    private ArrayList<ConsensusVariant> determineConsensusMultipleEntries(ArrayList<ConsensusVariant> multipleEntriesVariants) {
        File directory = new File(consensusFile.getParentFile() + File.separator + "fiveTier");
        directory.mkdir();
        createConflictingTiersTwoClassificationsMap();
        createConflictingTiersThreeClassificationsMap();
        createConflictingTiersFourClassificationsMap();
        int countFiveClassifications = 0;
        ArrayList<ConsensusVariant> consensusMultipleEntries = new ArrayList<>();
        ArrayList<ConsensusVariant> noConsensusTwoClassifications = new ArrayList<>();
        ArrayList<ConsensusVariant> noConsensusMoreThanTwoClassifications = new ArrayList<>();
        for (ConsensusVariant variant : multipleEntriesVariants) {
            variant.createClassificationMap();
            Map<String, Classification> classifications = variant.getClassificationMap();
            Set<Classification> setClassifications = new HashSet<>();
            for (Map.Entry<String, Classification> classificationEntry : classifications.entrySet()) {
                setClassifications.add(classificationEntry.getValue());
            }
            if (setClassifications.size() == 1) {
                consensusMultipleEntries.add(variant);
            } else if (setClassifications.size() == 2) {
                reportConflictingTiersTwoClassifications(setClassifications);
                noConsensusTwoClassifications.add(variant);
            } else if (setClassifications.size() == 3){
                reportConflictingTiersThreeClassifications(setClassifications);
                noConsensusMoreThanTwoClassifications.add(variant);
            } else if (setClassifications.size() == 4) {
                reportConflictingTiersFourClassifications(setClassifications);
                noConsensusMoreThanTwoClassifications.add(variant);
            } else if (setClassifications.size() == 5) {
                countFiveClassifications++;
            }
        }
        System.out.println("\n#### Conflicting two tiers classification ####");
        for (Map.Entry<String, Integer> entry : conflictingTiersTwoClassifications.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("\n#### Conflicting three tiers classification ####");
        for (Map.Entry<String, Integer> entry : conflictingTiersThreeClassifications.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\n#### Conflicting four tiers classification ####");
        for (Map.Entry<String, Integer> entry : conflictingTiersFourClassifications.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("countFiveClassifications = " + countFiveClassifications);

        System.out.println("\n#### COUNT CONSENSUS MULTIPLE ENTRIES (five-tier) ####");
        writeConsensusMultipleEntriesToFile(consensusMultipleEntries, directory);
        countConsensusPerUMC(consensusMultipleEntries);

        System.out.println("\n#### COUNT NO CONSENSUS TWO CLASSIFICATIONS (five-tier) ####");
        writeNoConsensusTwoClassificationsToFile(noConsensusTwoClassifications, directory);
        countConsensusPerUMC(noConsensusTwoClassifications);


        System.out.println("\n#### COUNT NO CONSENSUS MORE THAN TWO CLASSIFICATIONS (five-tier) ####");
        writeNoConsensusMoreThanTwoClassificationsToFile(noConsensusMoreThanTwoClassifications, directory);
        countConsensusPerUMC(noConsensusMoreThanTwoClassifications);


        return (ArrayList<ConsensusVariant>) Stream.of(noConsensusMoreThanTwoClassifications, noConsensusTwoClassifications)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void reportConflictingTiersTwoClassifications(Set<Classification> classifications) {
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.LIKELY_BENIGN)) {
            conflictingTiersTwoClassifications.put("BenignLikelyBenign", conflictingTiersTwoClassifications.get("BenignLikelyBenign") + 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.VOUS)) {
            conflictingTiersTwoClassifications.put("BenignVous", conflictingTiersTwoClassifications.get("BenignVous") + 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.LIKELY_PATHOGENIC)) {
            conflictingTiersTwoClassifications.put("BenignLikelyPathogenic", conflictingTiersTwoClassifications.get("BenignLikelyPathogenic") + 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersTwoClassifications.put("BenignPathogenic", conflictingTiersTwoClassifications.get("BenignPathogenic") + 1);
        }
        if (classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.VOUS)) {
            conflictingTiersTwoClassifications.put("LikelyBenignVous", conflictingTiersTwoClassifications.get("LikelyBenignVous") + 1);
        }
        if (classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.LIKELY_PATHOGENIC)) {
            conflictingTiersTwoClassifications.put("LikelyBenignLikelyPathogenic", conflictingTiersTwoClassifications.get("LikelyBenignLikelyPathogenic") + 1);
        }
        if (classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersTwoClassifications.put("LikelyBenignPathogenic", conflictingTiersTwoClassifications.get("LikelyBenignPathogenic") + 1);
        }
        if (classifications.contains(Classification.VOUS) && classifications.contains(Classification.LIKELY_PATHOGENIC)) {
            conflictingTiersTwoClassifications.put("VousLikelyPathogenic", conflictingTiersTwoClassifications.get("VousLikelyPathogenic") + 1);
        }
        if (classifications.contains(Classification.VOUS) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersTwoClassifications.put("VousPathogenic", conflictingTiersTwoClassifications.get("VousPathogenic") + 1);
        }
        if (classifications.contains(Classification.LIKELY_PATHOGENIC) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersTwoClassifications.put("LikelyPathogenicPathogenic", conflictingTiersTwoClassifications.get("LikelyPathogenicPathogenic") + 1);
        }
    }

    private void reportConflictingTiersThreeClassifications(Set<Classification> classifications) {
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.VOUS)) {
            conflictingTiersThreeClassifications.put("B_LB_V", conflictingTiersThreeClassifications.get("B_LB_V") + 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.LIKELY_PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("B_LB_LP", conflictingTiersThreeClassifications.get("B_LB_LP") + 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("B_LP_P", conflictingTiersThreeClassifications.get("B_LP_P") + 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.VOUS) && classifications.contains(Classification.LIKELY_PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("B_V_LP", conflictingTiersThreeClassifications.get("B_V_LP") + 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.VOUS) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("B_V_P", conflictingTiersThreeClassifications.get("B_V_P")+ 1);
        }
        if (classifications.contains(Classification.BENIGN) && classifications.contains(Classification.LIKELY_PATHOGENIC) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("B_LP_P", conflictingTiersThreeClassifications.get("B_LP_P") + 1);
        }
        if (classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.VOUS) && classifications.contains(Classification.LIKELY_PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("LB_V_LP", conflictingTiersThreeClassifications.get("LB_V_LP") + 1);
        }
        if (classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.VOUS) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("LB_V_P", conflictingTiersThreeClassifications.get("LB_V_P") + 1);
        }
        if (classifications.contains(Classification.LIKELY_BENIGN) && classifications.contains(Classification.LIKELY_PATHOGENIC) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("LB_LP_P", conflictingTiersThreeClassifications.get("LB_LP_P") + 1);
        }
        if (classifications.contains(Classification.VOUS) && classifications.contains(Classification.LIKELY_PATHOGENIC) && classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersThreeClassifications.put("V_LP_P", conflictingTiersThreeClassifications.get("V_LP_P") + 1);
        }
    }

    private void reportConflictingTiersFourClassifications(Set<Classification> classifications) {
        if (!classifications.contains(Classification.PATHOGENIC)) {
            conflictingTiersFourClassifications.put("B_LB_V_LP", conflictingTiersFourClassifications.get("B_LB_V_LP") + 1);
        }
        if (!classifications.contains(Classification.LIKELY_PATHOGENIC)) {
            conflictingTiersFourClassifications.put("B_LB_V_P", conflictingTiersFourClassifications.get("B_LB_V_P") + 1);
        }
        if (!classifications.contains(Classification.VOUS)) {
            conflictingTiersFourClassifications.put("B_LB_LP_P", conflictingTiersFourClassifications.get("B_LB_LP_P") + 1);
        }
        if (!classifications.contains(Classification.LIKELY_BENIGN)) {
            conflictingTiersFourClassifications.put("B_V_LP_P", conflictingTiersFourClassifications.get("B_V_LP_P") + 1);
        }
        if (!classifications.contains(Classification.BENIGN)) {
            conflictingTiersFourClassifications.put("LB_V_LP_P", conflictingTiersFourClassifications.get("LB_V_LP_P") + 1);
        }
    }

    private void reportConflictingThreeTiersTwoClassifications(Set<Classification> classifications, ConsensusVariant variant) {
        if (!classifications.contains(Classification.PATHOGENIC)) {
            conflictingThreeTiersTwoClassifications.put("B_V", conflictingThreeTiersTwoClassifications.get("B_V") + 1);
        }
        if (!classifications.contains(Classification.VOUS)) {
            conflictingVariantsThreeTierBenignPathogenic.add(variant);
            conflictingThreeTiersTwoClassifications.put("B_P", conflictingThreeTiersTwoClassifications.get("B_P") + 1);
        }
        if (!classifications.contains(Classification.BENIGN)) {
            conflictingThreeTiersTwoClassifications.put("V_P", conflictingThreeTiersTwoClassifications.get("V_P") + 1);
        }
    }

    private void createConflictingTiersTwoClassificationsMap() {
        conflictingTiersTwoClassifications.put("BenignLikelyBenign", 0);
        conflictingTiersTwoClassifications.put("BenignVous", 0);
        conflictingTiersTwoClassifications.put("BenignLikelyPathogenic", 0);
        conflictingTiersTwoClassifications.put("BenignPathogenic", 0);
        conflictingTiersTwoClassifications.put("LikelyBenignVous", 0);
        conflictingTiersTwoClassifications.put("LikelyBenignLikelyPathogenic", 0);
        conflictingTiersTwoClassifications.put("LikelyBenignPathogenic", 0);
        conflictingTiersTwoClassifications.put("VousLikelyPathogenic", 0);
        conflictingTiersTwoClassifications.put("VousPathogenic", 0);
        conflictingTiersTwoClassifications.put("LikelyPathogenicPathogenic", 0);
    }

    private void createConflictingTiersThreeClassificationsMap() {
        conflictingTiersThreeClassifications.put("B_LB_V", 0);
        conflictingTiersThreeClassifications.put("B_LB_LP", 0);
        conflictingTiersThreeClassifications.put("B_LB_P", 0);
        conflictingTiersThreeClassifications.put("B_V_LP", 0);
        conflictingTiersThreeClassifications.put("B_V_P", 0);
        conflictingTiersThreeClassifications.put("B_LP_P", 0);
        conflictingTiersThreeClassifications.put("LB_V_LP", 0);
        conflictingTiersThreeClassifications.put("LB_V_P", 0);
        conflictingTiersThreeClassifications.put("LB_LP_P", 0);
        conflictingTiersThreeClassifications.put("V_LP_P", 0);
    }

    private void createConflictingTiersFourClassificationsMap() {
        conflictingTiersFourClassifications.put("B_LB_V_LP", 0);
        conflictingTiersFourClassifications.put("B_LB_V_P", 0);
        conflictingTiersFourClassifications.put("B_LB_LP_P", 0);
        conflictingTiersFourClassifications.put("B_V_LP_P", 0);
        conflictingTiersFourClassifications.put("LB_V_LP_P", 0);
    }

    private void createConflictingFiveTiersTwoClassificationsMap() {
        conflictingThreeTiersTwoClassifications.put("B_V", 0);
        conflictingThreeTiersTwoClassifications.put("B_P", 0);
        conflictingThreeTiersTwoClassifications.put("V_P", 0);
    }

    private void writeConsensusMultipleEntriesToFile(ArrayList<ConsensusVariant> consensusMultipleEntries, File directory) {
        File file = new File(directory + File.separator + "consensusMultipleEntries.txt");
        System.out.println("NUMBER OF MULTIPLE ENTRIES WITH CONSENSUS: " + consensusMultipleEntries.size());
        new VariantWriter(file, consensusMultipleEntries);
    }

    private void writeNoConsensusTwoClassificationsToFile(ArrayList<ConsensusVariant> noConsensusTwoClassifications, File directory) {
        File file = new File(directory + File.separator +"noConsensusMultipleEntries.txt");
        System.out.println("NUMBER OF MULTIPLE ENTRIES WITHOUT CONSENSUS (two classifications): " + noConsensusTwoClassifications.size());
        new VariantWriter(file, noConsensusTwoClassifications);
    }

    private void writeNoConsensusMoreThanTwoClassificationsToFile(ArrayList<ConsensusVariant> noConsensusMoreThanTwoClassifications, File directory) {
        File file = new File(directory + File.separator + "noConsensusMoreThanTwoClassifications.txt");
        System.out.println("NUMBER OF MULTIPLE ENTRIES WITHOUT CONSENSUS (more than two classifications): " + noConsensusMoreThanTwoClassifications.size());
        new VariantWriter(file, noConsensusMoreThanTwoClassifications);
    }

    private void writeConflictingPathogenicVersusBenign(ArrayList<ConsensusVariant> conflictingPathogenicVersusBenign, File directory) {
        File file = new File(directory + File.separator + "conflictingPathogenicVersusBenign.txt");
        System.out.println("Number of conflicting variants: " + conflictingPathogenicVersusBenign.size());
        new VariantWriter(file, conflictingPathogenicVersusBenign);
    }

    private ArrayList<ConsensusVariant> deleteSingleEntries(ArrayList<ConsensusVariant> consensusVariants) {
        System.out.println("ALL ENTRIES: " + consensusVariants.size());
        ArrayList<ConsensusVariant> singleEntries = new ArrayList<>();
        ArrayList<ConsensusVariant> multipleEntries = new ArrayList<>();
        int twoEntries = 0;
        int threeEntries = 0;
        int fourEntries = 0;
        int fiveEntries = 0;
        int sixEntries = 0;
        int sevenEntries = 0;
        int eightEntries = 0;
        ArrayList<ConsensusVariant> twoEntriesVariants = new ArrayList<>();
        ArrayList<ConsensusVariant> threeEntriesVariants = new ArrayList<>();

        for (ConsensusVariant variant : consensusVariants) {
            if (variant.getCountUmcEntries() == 1) {
                singleEntries.add(variant);
            } else {
                multipleEntries.add(variant);
            }
            switch (variant.getCountUmcEntries()) {
                case 2:
                    twoEntries++;
                    twoEntriesVariants.add(variant);
                    break;
                case 3:
                    threeEntries++;
                    threeEntriesVariants.add(variant);
                    break;
                case 4:
                    fourEntries++;
                    break;
                case 5:
                    fiveEntries++;
                    break;
                case 6:
                    sixEntries++;
                    break;
                case 7:
                    sevenEntries++;
                    break;
                case 8:
                    eightEntries++;
                    break;
            }
        }
        System.out.println("#### COUNT SINGLE ENTRIES ####");
        countConsensusPerUMC(singleEntries);

        System.out.println("\n#### Entries per variant ####");
        System.out.println("Two entries: " + twoEntries);
        new ConsensusDeterminer(twoEntriesVariants);
        System.out.println("Three entries: " + threeEntries);
        System.out.println("Four entries: " + fourEntries);
        System.out.println("Five entries: " + fiveEntries);
        System.out.println("Six entries: " + sixEntries);
        System.out.println("Seven entries: " + sevenEntries);
        System.out.println("Eight entries: " + eightEntries);

        writeSingleEntriesToFile(singleEntries);
        writeMultipleEntriesToFile(multipleEntries);
        return multipleEntries;
    }

    private void writeSingleEntriesToFile(ArrayList<ConsensusVariant> singleEntryVariants) {
        File file = new File(consensusFile.getParentFile() + File.separator + "singleEntriesConsensus.txt");
        System.out.println("NUMBER OF SINGLE ENTRIES: " + singleEntryVariants.size());
        new VariantWriter(file, singleEntryVariants);
    }

    private void writeMultipleEntriesToFile(ArrayList<ConsensusVariant> multipleEntriesVariants) {
        File file = new File(consensusFile.getParentFile() + File.separator + "multipleEntriesConsensus.txt");
        System.out.println("NUMBER OF MULTIPLE ENTRIES: " + multipleEntriesVariants.size());
        new VariantWriter(file, multipleEntriesVariants);
    }

    private ArrayList<ConsensusVariant> processFile(File consensusFile) {
        ArrayList<ConsensusVariant> consensusVariantArrayList = new ArrayList<>();
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(consensusFile));
            //skip first line (header)
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                ConsensusVariant consensusVariant = createConsensusVariant(line);
                consensusVariantArrayList.add(consensusVariant);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return consensusVariantArrayList;
    }

    private ConsensusVariant createConsensusVariant(String line) {
        ConsensusVariant consensusVariant = new ConsensusVariant();
        String[] columns = line.split("\t");
        consensusVariant.setChr(columns[0]);
        consensusVariant.setPos(Integer.parseInt(columns[1]));
        consensusVariant.setREF(columns[2]);
        consensusVariant.setALT(columns[3]);
        consensusVariant.setAMC(columns[4]);
        consensusVariant.setErasmus(columns[5]);
        consensusVariant.setLUMC(columns[6]);
        consensusVariant.setNKI(columns[7]);
        consensusVariant.setRadboud(columns[8]);
        consensusVariant.setUMCG(columns[9]);
        consensusVariant.setUMCU(columns[10]);
        consensusVariant.setVUMC(columns[11]);
        consensusVariant.setCountUmcEntries();
        return consensusVariant;
    }
}
