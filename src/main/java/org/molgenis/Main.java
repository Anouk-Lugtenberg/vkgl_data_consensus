package org.molgenis;

import org.molgenis.CLIParser.CLIParser;
import org.molgenis.IO.ConsensusFileProcessor;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        CLIParser cliParser = new CLIParser();
        cliParser.parseCLI(args);
        File consensusFile = cliParser.getConsensusFile();
        new ConsensusFileProcessor(consensusFile);
    }
}
