package org.molgenis.CLIParser;

import org.apache.commons.cli.*;

import java.io.File;

public class CLIParser {
    private Options options = new Options();
    private File consensusFile;

    public CLIParser() {
        options.addOption("c", "consensusFile", true, "Consensus file, containing the different UMCs and their classification for the variants");
    }

    public void parseCLI(String[] args) {
        CommandLineParser parser = new BasicParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("c")) {
                this.consensusFile = new File(cmd.getOptionValue("c"));
            } else {
                throw new IllegalArgumentException("Missing consensus file.");
            }
        } catch (ParseException e) {
            System.out.println("Something went wrong while parsing the command line arguments");
        }
    }

    public File getConsensusFile() {
        return this.consensusFile;
    }

}
