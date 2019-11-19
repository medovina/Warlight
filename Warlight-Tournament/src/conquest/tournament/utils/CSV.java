package conquest.tournament.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV parser.
 * 
 * @author Jimmy
 */
public class CSV {

    private String delimiter;
    
    public List<String> keys;
    
    public List<CSVRow> rows = new ArrayList<CSVRow>();

    /**
     *
     * @param filename
     * @param delimiter
     * @throws FileNotFoundException
     * @throws IOException
     */
    public CSV(File file, String delimiter, boolean containsHeaderRow) throws FileNotFoundException, IOException {
        super();
        
        this.delimiter = delimiter;
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        try {
            String[] line = nextLine(reader);
            
            if (line == null) {
                throw new RuntimeException("File '" + file.getAbsolutePath() + "' does not contain any line!");
            }
            
            if (containsHeaderRow) {
                keys = new ArrayList<String>();
                for (String item : line) {
                    keys.add(item);
                }
            } else {
                keys = new ArrayList<String>();
                for (int i = 0; i < line.length; ++i) {
                    keys.add(String.valueOf(i));
                }
                processLine(line);
            }
            
            while (reader.ready()) {
                processLine(nextLine(reader));
            }
        } finally {
            reader.close();
        }
    }
    
    private String[] nextLine(BufferedReader reader) throws IOException {
        if (reader.ready()) {
            String line = reader.readLine();
            return line.split(delimiter);
        }
        return null;
    }
    
    private CSVRow processLine(String[] line) {
        CSVRow result = new CSVRow();
        for (int i = 0; i < line.length; ++i) {
            if (i >= keys.size()) {
                keys.add(String.valueOf(i));
            }
            String key = keys.get(i);
            String value = line[i];
            result.add(key, value);
        }
        rows.add(result);
        return result;
    }

    public static class CSVRow {

        private Map<String, String> row;

        /**
         *
         * @param row
         */
        public CSVRow() {
            this.row = new HashMap<String, String>();
        }
        
        protected void add(String key, String value) {
            row.put(key, value);
        }

        public String getString(String name) {
            String val = row.get(name);
            if (val == null) {
                return "";
            }
            return val;
        }

        public Integer getInt(String name) {
            try {
                int number = Integer.parseInt(getString(name));
                return number;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public Double getDouble(String name) {
            try {
                double number = Double.parseDouble(getString(name));
                return number;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
