package br.uff.ic.gems.phoenix;

import java.util.logging.Logger;

import br.uff.ic.gems.phoenix.exception.PhoenixDiffException;

public class PhoenixCLI {
    
    //private static Logger LOG = Logger.getLogger(PhoenixCLI.class.getName());

    private static String xmlfilepath1 = null, 
                          xmlfilepath2 = null;

    private static double threshold = 0.7f;
    
    private static boolean ignoreTrivial = true,
                           automaticAllocation = true,
                           ignoreThresholdOnRoot = true,
                           allowDataTypeSimilarity = true,
                           ignoreCaseOnSimilarity = true;
    
    private static String dateFormat = "eng";

    public static void main(String[] args) {
        
        if (args.length < 1) {
            showUsage();
            System.exit(1);
        }
        
        processArguments(args);
        
        SettingsHelper.setIgnoreTrivialSimilarities(ignoreTrivial);
        SettingsHelper.setAutomaticWeightAllocation(automaticAllocation);
        SettingsHelper.setSimilarityThreshold(threshold);
        SettingsHelper.setIgnoreThresholdOnRoot(ignoreThresholdOnRoot);
        SettingsHelper.setAllowDataTypeSimilarity(allowDataTypeSimilarity);
        SettingsHelper.setDateFormat(dateFormat);
        SettingsHelper.setIgnoreCaseOnSimilarity(ignoreCaseOnSimilarity);
        
        if (xmlfilepath1 == null || xmlfilepath2 == null) {
            showErrorAndExit("Missing argument(s)");
        }

        logInfo();
        
        long initialTimestamp = System.currentTimeMillis();
        long timestamp =  initialTimestamp;
        
        PhoenixDiffCalculator cmp = null;
        try {
            cmp = new PhoenixDiffCalculator(xmlfilepath1,xmlfilepath2);
            cmp.setOutputStream(System.out);
        } 
        catch (PhoenixDiffException ex) {
            showErrorAndExit(ex.getMessage());
        }

        //LOG.info("Time parsing files: " + (System.currentTimeMillis() - timestamp) + " ms.");
        timestamp = System.currentTimeMillis();
        

        try {
            cmp.executeComparison();
        }
        catch (Exception ex) {
            showErrorAndExit(ex.getMessage());
        }
        
        //LOG.info("Total execution time: " + (System.currentTimeMillis() - initialTimestamp) + " ms.");
    }

    private static void logInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Phoenix parameters: ignoreTrivial=" + SettingsHelper.getIgnoreTrivialSimilarities());
        sb.append(" | similarityThreshold=" + SettingsHelper.getSimilarityThreshold());
        sb.append(" | ignoreThresholdOnRoot=" + SettingsHelper.getIgnoreThresholdOnRoot());
        sb.append(" | automaticWeightAllocation=" + SettingsHelper.getAutomaticWeightAllocation());
        sb.append(" | allowDataTypeSimilarity=" + SettingsHelper.getAllowDataTypeSimilarity());
        if(SettingsHelper.getAllowDataTypeSimilarity())
            sb.append(" | dateFormat=" + SettingsHelper.getDateFormat());
        sb.append(" | ignoreCaseOnSimilarity=" + SettingsHelper.getIgnoreCaseOnSimilarity());
        if (!SettingsHelper.getAutomaticWeightAllocation()) {
            sb.append(" | nameWeight=" + SettingsHelper.getNameSimilarityWeight());
            sb.append(" | valueWeight=" + SettingsHelper.getValueSimilarityWeight());
            sb.append(" | attribWeight=" + SettingsHelper.getAttributeSimilarityWeight());
            sb.append(" | childrenWeight=" + SettingsHelper.getChildrenSimilarityWeight());
        }
        //LOG.info(sb.toString());
    }

    private static void processArguments(String[] args) {
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                processOption(arg);
            }
            else if (xmlfilepath1 == null) {
                xmlfilepath1 = arg;
            }
            else if (xmlfilepath2 == null) {
                xmlfilepath2 = arg;
            }
            else {
                showErrorAndExit("Invalid parameter: " + arg);
                System.exit(1);
            }
        }
    }

    private static void processOption(String arg) {
        switch (arg.charAt(1)) {
            case 'h':
            case 'H':
                showOptions();
                break;
                
            case 't':
            case 'T':
                try {
                    String value = arg.split("=")[1];
                    threshold = Double.parseDouble(value);
                    if (threshold < 0 || threshold > 1) {
                        throw new Exception();
                    }
                }
                catch (Exception e) {
                    showErrorAndExit("Wrong value for option 'Threshold': must be a number in [0..1] range!");
                }
                break;
            
            case 'i':
            case 'I':
                try {
                    String value = arg.split("=")[1];
                    ignoreTrivial = Boolean.parseBoolean(value);
                }
                catch (Exception e) {
                    showErrorAndExit("Wrong value for option 'IgnoreTrivialSimilarities': must be 'true' or 'false'!");
                }
                break;
                
            case 'a':
            case 'A':
                try {
                    String value = arg.split("=")[1];
                    automaticAllocation = Boolean.parseBoolean(value);
                }
                catch (Exception e) {
                    showErrorAndExit("Wrong value for option 'AutomaticWeightAllocation': must be 'true' or 'false'!");
                }
                break;

            case 's':
            case 'S':
                try {
                    String value = arg.split("=")[1];
                    allowDataTypeSimilarity = Boolean.parseBoolean(value);
                }
                catch (Exception e) {
                    showErrorAndExit("Wrong value for option 'Allow Data Type Similarity': must be 'true' or 'false'!");
                }
                break;
                
            case 'f':
            case 'F':
                try {
                    dateFormat = arg.split("=")[1];
                    if( !( dateFormat.equalsIgnoreCase("pt") || dateFormat.equalsIgnoreCase("eng")) )
                        throw new Exception();
                }
                catch (Exception e) {
                    showErrorAndExit("Wrong value for option 'Date format': must be 'eng' or 'pt'!");
                }
                break;
                
            case 'r':
            case 'R':
                try {
                    String value = arg.split("=")[1];
                    ignoreThresholdOnRoot = Boolean.parseBoolean(value);
                }
                catch (Exception e) {
                    showErrorAndExit("Wrong value for option 'RootIgnoreThreshold': must be 'true' or 'false'!");
                }
                break;
                
            case 'c':
            case 'C':
                try {
                    String value = arg.split("=")[1];
                    ignoreCaseOnSimilarity = Boolean.parseBoolean(value);
                }
                catch (Exception e) {
                    showErrorAndExit("Wrong value for option 'IgnoreCaseOnSimilarity': must be 'true' or 'false'!");
                }
                break;
                
            default:
                showErrorAndExit("Invalid Option: " + arg);
                
        }
    }

    private static void showOptions() {
        System.out.println("\nUsage: PhoenixCLI [options] <xmlfile1> <xmlfile2>");
        System.out.println("\n\tOptions:\n");
        System.out.println("\t-h       : This help.");
        System.out.println("\t-t=VALUE : Similarity threshold value. VALUE must be a number in [0..1] range. (Default: 0.7)");
        System.out.println("\t-n=VALUE : Name similarity Required. VALUE must be a 'true' or 'false'. (Default: true)");
        System.out.println("\t-i=VALUE : Ignore trivial similarities. VALUE must be a 'true' or 'false'. (Default: true)");
        System.out.println("\t-a=VALUE : Automatic weight allocation. VALUE must be a 'true' or 'false'. (Default: true)");
        System.out.println("\t-r=VALUE : Ignore Threshold on Root. VALUE must be a 'true' or 'false'. (Default: true)");
        System.out.println("\t-s=VALUE : Allow data types similarity. VALUE must be a 'true' or 'false'. (Default: true)");
        System.out.println("\t-f=VALUE : Date format. VALUE must be 'eng' or 'pt'. (Default: eng)");
        System.out.println("\t-c=VALUE : Ignore case on similarity. VALUE must be a 'true' or 'false'. (Default: true)");
        System.out.println();
        System.exit(0);
    }

    private static void showUsage() {
        System.out.println("\nUsage: PhoenixCLI [options] <xmlfile1> <xmlfile2>");
        System.out.println("\tUse '-h' for options listing.\n");
        System.exit(0);
    }
    
    private static void showErrorAndExit(String message) {
        //LOG.severe("Erro! " + message);
        System.exit(1);
    }
}
