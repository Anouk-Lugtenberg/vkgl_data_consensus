package org.molgenis.model;

import org.molgenis.service.ClassificationDeterminer;

import java.util.HashMap;
import java.util.Map;

public class ConsensusVariant {
    private ClassificationDeterminer classificationDeterminer = new ClassificationDeterminer();
    private int countUmcEntries;
    private String chr;
    private int pos;
    private String REF;
    private String ALT;
    private Classification AMC;
    private Classification Erasmus;
    private Classification LUMC;
    private Classification NKI;
    private Classification Radboud;
    private Classification UMCG;
    private Classification UMCU;
    private Classification VUMC;
    private Classification AMCThreeTier;
    private Classification ErasmusThreeTier;
    private Classification LUMCThreeTier;
    private Classification NKIThreeTier;
    private Classification RadboudThreeTier;
    private Classification UMCGThreeTier;
    private Classification UMCUThreeTier;
    private Classification VUMCThreeTier;
    private Map<String, Classification> classificationMap = new HashMap<>();
    private Map<String, Classification> classificationMapThreeTier = new HashMap<>();

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getREF() {
        return REF;
    }

    public void setREF(String REF) {
        this.REF = REF;
    }

    public String getALT() {
        return ALT;
    }

    public void setALT(String ALT) {
        this.ALT = ALT;
    }

    public Classification getAMC() {
        return AMC;
    }

    public void setAMC(String AMC) {
        this.AMC = classificationDeterminer.determine(AMC);
    }

    public Classification getErasmus() {
        return Erasmus;
    }

    public void setErasmus(String erasmus) {
        Erasmus = classificationDeterminer.determine(erasmus);
    }

    public Classification getLUMC() {
        return LUMC;
    }

    public void setLUMC(String LUMC) {
        this.LUMC = classificationDeterminer.determine(LUMC);
    }

    public Classification getNKI() {
        return NKI;
    }

    public void setNKI(String NKI) {
        this.NKI = classificationDeterminer.determine(NKI);
    }

    public Classification getRadboud() {
        return Radboud;
    }

    public void setRadboud(String radboud) {
        Radboud = classificationDeterminer.determine(radboud);
    }

    public Classification getUMCG() {
        return UMCG;
    }

    public void setUMCG(String UMCG) {
        this.UMCG = classificationDeterminer.determine(UMCG);
    }

    public Classification getUMCU() {
        return UMCU;
    }

    public void setUMCU(String UMCU) {
        this.UMCU = classificationDeterminer.determine(UMCU);
    }

    public Classification getVUMC() {
        return VUMC;
    }

    public void setVUMC(String VUMC) {
        this.VUMC = classificationDeterminer.determine(VUMC);
    }

    public int getCountUmcEntries() {
        return countUmcEntries;
    }

    public void setCountUmcEntries() {
        int count = 0;
        if (AMC != Classification.NO_CLASSIFICATION) { count++; }
        if (Erasmus != Classification.NO_CLASSIFICATION) { count++; }
        if (LUMC != Classification.NO_CLASSIFICATION) { count++; }
        if (NKI != Classification.NO_CLASSIFICATION) { count++; }
        if (Radboud != Classification.NO_CLASSIFICATION) { count++; }
        if (UMCG != Classification.NO_CLASSIFICATION) { count++; }
        if (UMCU != Classification.NO_CLASSIFICATION) { count++; }
        if (VUMC != Classification.NO_CLASSIFICATION) { count++; }
        countUmcEntries = count;
    }

    public void createClassificationMap() {
        if (AMC != Classification.NO_CLASSIFICATION) {
            classificationMap.put("AMC", AMC);
        }
        if (Erasmus != Classification.NO_CLASSIFICATION) {
            classificationMap.put("Erasmus", Erasmus);
        }
        if (LUMC != Classification.NO_CLASSIFICATION) {
            classificationMap.put("LUMC", LUMC);
        }
        if (NKI != Classification.NO_CLASSIFICATION) {
            classificationMap.put("NKI", NKI);
        }
        if (Radboud != Classification.NO_CLASSIFICATION) {
            classificationMap.put("Radboud", Radboud);
        }
        if (UMCG != Classification.NO_CLASSIFICATION) {
            classificationMap.put("UMCG", UMCG);
        }
        if (UMCU != Classification.NO_CLASSIFICATION) {
            classificationMap.put("UMCU", UMCU);
        }
        if (VUMC != Classification.NO_CLASSIFICATION) {
            classificationMap.put("VUMC", VUMC);
        }
    }

    public Map<String, Classification> getClassificationMap() {
        return this.classificationMap;
    }

    public void createThreeTiers() {
        setAMCThreeTier();
        setErasmusThreeTier();
        setLUMCThreeTier();
        setNKIThreeTier();
        setRadboudThreeTier();
        setUMCGThreeTier();
        setUMCUThreeTier();
        setVUMCThreeTier();
        createClassificationMapThreeTier();
    }

    public Classification getAMCThreeTier() {
        return this.AMCThreeTier;
    }

    public void setAMCThreeTier() {
        this.AMCThreeTier = checkLikelyClassification(AMC);
    }

    public Classification getErasmusThreeTier() {
        return ErasmusThreeTier;
    }

    public void setErasmusThreeTier() {
        ErasmusThreeTier = checkLikelyClassification(Erasmus);
    }

    public Classification getLUMCThreeTier() {
        return LUMCThreeTier;
    }

    public void setLUMCThreeTier() {
        this.LUMCThreeTier = checkLikelyClassification(LUMC);
    }

    public Classification getNKIThreeTier() {
        return NKIThreeTier;
    }

    public void setNKIThreeTier() {
        this.NKIThreeTier = checkLikelyClassification(NKI);
    }

    public Classification getRadboudThreeTier() {
        return RadboudThreeTier;
    }

    public void setRadboudThreeTier() {
        RadboudThreeTier = checkLikelyClassification(Radboud);
    }

    public Classification getUMCGThreeTier() {
        return UMCGThreeTier;
    }

    public void setUMCGThreeTier() {
        this.UMCGThreeTier = checkLikelyClassification(UMCG);
    }

    public Classification getUMCUThreeTier() {
        return UMCUThreeTier;
    }

    public void setUMCUThreeTier() {
        this.UMCUThreeTier = checkLikelyClassification(UMCU);
    }

    public Classification getVUMCThreeTier() {
        return VUMCThreeTier;
    }

    public void setVUMCThreeTier() {
        this.VUMCThreeTier = checkLikelyClassification(VUMC);
    }

    private Classification checkLikelyClassification(Classification classification) {
        if (classification == Classification.LIKELY_PATHOGENIC) {
            classification = Classification.PATHOGENIC;
        } else if (classification == Classification.LIKELY_BENIGN) {
            classification = Classification.BENIGN;
        }
        return classification;
    }

    public void createClassificationMapThreeTier() {
        if (AMCThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("AMC", AMCThreeTier);
        }
        if (ErasmusThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("Erasmus", ErasmusThreeTier);
        }
        if (LUMCThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("LUMC", LUMCThreeTier);
        }
        if (NKIThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("NKI", NKIThreeTier);
        }
        if (RadboudThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("Radboud", RadboudThreeTier);
        }
        if (UMCGThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("UMCG", UMCGThreeTier);
        }
        if (UMCUThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("UMCU", UMCUThreeTier);
        }
        if (VUMCThreeTier != Classification.NO_CLASSIFICATION) {
            classificationMapThreeTier.put("VUMC", VUMCThreeTier);
        }
    }

    public Map<String, Classification> getClassificationMapThreeTier() {
        return classificationMapThreeTier;
    }

    @Override
    public String toString() {
        return "chr: " + chr + "\tpos: " + pos + "\tref: " + REF + "\talt: " + ALT + "\tamc: " + AMC + "\terasmus: "
                + Erasmus + "\tlumc: " + LUMC + "\tnki: " + NKI + "\tradboud: " + Radboud + "\tumcg: " + UMCG
                + "\tumcu: " + UMCU + "\tvumc: " + VUMC;
    }
}
