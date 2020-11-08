package tournament;

import java.io.File;
import java.util.Iterator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class WarlightTableConsole {
    
    private static final char ARG_TABLE_FILE_SHORT = 't';
    
    private static final String ARG_TABLE_FILE_LONG = "table-file-in";
    
    private static final char ARG_SUMMARY_FILE_SHORT = 's';
    
    private static final String ARG_SUMMARY_FILE_LONG = "summmary-file-out";
    
    private static JSAP jsap;

    private static String tableFileName;
    
    private static String summaryFileName;
    
    private static File tableFile;
    
    private static File summaryFile;

    private static boolean headerOutput = false;

    private static JSAPResult config;

    private static void fail(String errorMessage) {
        fail(errorMessage, null);
    }

    private static void fail(String errorMessage, Throwable e) {
        header();
        System.out.println("ERROR: " + errorMessage);
        System.out.println();
        if (e != null) {
            e.printStackTrace();
            System.out.println("");
        }        
        System.out.println("Usage: java ... ");
        System.out.println("                " + jsap.getUsage());
        System.out.println();
        System.out.println(jsap.getHelp());
        System.out.println();
        throw new RuntimeException("FAILURE: " + errorMessage);
    }

    private static void header() {
        if (headerOutput) return;
        System.out.println();
        System.out.println("==============");
        System.out.println("Warlight Table");
        System.out.println("==============");
        System.out.println();
        headerOutput = true;
    }
        
    private static void initJSAP() throws JSAPException {
        jsap = new JSAP();
        
        FlaggedOption opt322 = new FlaggedOption(ARG_TABLE_FILE_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false)
            .setDefault("./results/results-table.csv")
            .setShortFlag(ARG_TABLE_FILE_SHORT)
            .setLongFlag(ARG_TABLE_FILE_LONG);    
        opt322.setHelp("Table file to process (single file will be read) or Directory containing table files (all the .csv files found within the directory will be read).");
        
        jsap.registerParameter(opt322);
        
        FlaggedOption opt6 = new FlaggedOption(ARG_SUMMARY_FILE_LONG)
            .setStringParser(JSAP.STRING_PARSER)
            .setRequired(false)
            .setDefault("./results/results-table-summary.csv")
            .setShortFlag(ARG_SUMMARY_FILE_SHORT)
            .setLongFlag(ARG_SUMMARY_FILE_LONG);    
        opt6.setHelp("Where to output table summary (will be overwritten if exists).");
    
        jsap.registerParameter(opt6);
       }

    private static void readConfig(String[] args) {
        System.out.println("Parsing command arguments.");
        
        try {
            config = jsap.parse(args);
        } catch (Exception e) {
            fail(e.getMessage());
            System.out.println("");
            e.printStackTrace();
            throw new RuntimeException("FAILURE!");
        }
        
        if (!config.success()) {
            String error = "Invalid arguments specified.";
            
            @SuppressWarnings("unchecked")
            Iterator<String> errorIter = config.getErrorMessageIterator();
            if (!errorIter.hasNext()) {
                error += "\n-- No details given.";
            } else {
                while (errorIter.hasNext()) {
                    error += "\n-- " + errorIter.next();
                }
            }
            fail(error);
        }

        tableFileName = config.getString(ARG_TABLE_FILE_LONG);
        
        summaryFileName = config.getString(ARG_SUMMARY_FILE_LONG);
    }
    
    private static void sanityChecks() {
        System.out.println("Sanity checks...");
        
        tableFile = new File(tableFileName);
        System.out.println("-- table file/dir: " + tableFileName + " --> " + tableFile.getAbsolutePath());
    
        if (!tableFile.exists()) {
            fail("Table file does not exists. Parsed as: " + tableFileName + " --> " + tableFile.getAbsolutePath());
        }
        System.out.println("---- table file/dir exists, ok");
        
        summaryFile = new File(summaryFileName);
        System.out.println("-- summary file: " + summaryFileName + " --> " + summaryFile.getAbsolutePath());
    
        if (summaryFile.exists()) {
            System.out.println("---- summary file exists, deleting: " + summaryFileName + " --> " + summaryFile.getAbsolutePath());
            summaryFile.delete();
        }
        
        System.out.println("Sanity checks OK!");
    }
    
    private static void summarize() {
        System.out.println("Summarizing!");
        WarlightTable table = new WarlightTable(tableFile, summaryFile);
        table.run();
    }
        
    // ==============
    // TEST ARGUMENTS
    // ==============
    public static String[] getTestArgs() {
        return new String[] {
                  "-t", "./results/fights"             // input directory containing CSVs
                , "-s", "./results/fights-summary.csv" // output summary CSV                
        };
    }
    
    public static void main(String[] args) throws JSAPException {
        // -----------
        // FOR TESTING
        // -----------
        //args = getTestArgs();        
        
        // --------------
        // IMPLEMENTATION
        // --------------
        
        initJSAP();
        
        header();
        
        readConfig(args);
        
        sanityChecks();
        
        summarize();
        
        System.out.println("---// FINISHED //---");
        
        System.exit(0);
    }

}
