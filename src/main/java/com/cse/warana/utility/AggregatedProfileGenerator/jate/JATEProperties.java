package com.cse.warana.utility.AggregatedProfileGenerator.jate;

import com.cse.warana.utility.AggregatedProfileGenerator.utils.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 */


public class JATEProperties {
    private Properties _properties = new Properties();
    private static JATEProperties _ref = null;

    //public static final String NP_FILTER_PATTERN = "[^a-zA-Z0-9\\-]";
    //replaced by the following var:
    public static final String TERM_CLEAN_PATTERN = "[^a-zA-Z0-9\\-]";

    public static final String NLP_PATH = "jate.system.nlp";
    public static final String TEST_PATH = "jate.system.test";
    public static final String TERM_MAX_WORDS = "jate.system.term.maxwords";
    public static final String TERM_IGNORE_DIGITS = "jate.system.term.ignore_digits";
    public static final String MULTITHREAD_COUNTER_NUMBERS="jate.system.term.frequency.counter.multithread";

    private JATEProperties() {
//        read();
    }

    public static JATEProperties getInstance() {
        if (_ref == null) {
            _ref = new JATEProperties();
        }
        return _ref;
    }

    private void read() {
        InputStream in = null;
        try {
            /*InputStream x= getClass().getResourceAsStream("/indexing.properties");*/
            in = getClass().getResourceAsStream("/com/cse/warana/utility/AggregatedProfileGenerator/jate/jate.properties");
            _properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) try {
                in.close();
                in = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getProperty(String key) {
        return _properties.getProperty(key);
    }

    public String getNLPPath() {
        return Config.NLP_PATH;
    }
    public String getTestPath() {
        return Config.TEST_PATH;
    }
    public int getMaxMultipleWords() {
        try {
            return Config.TERM_MAX_WORDS;
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    public int getMultithreadCounterNumbers() {
        try {
            return Config.MULTITHREAD_COUNTER_NUMBERS;
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    public boolean isIgnoringDigits() {
        try {
            return Config.TERM_IGNORE_DIGITS;
        } catch (Exception e) {
            return true;
        }
    }

}
