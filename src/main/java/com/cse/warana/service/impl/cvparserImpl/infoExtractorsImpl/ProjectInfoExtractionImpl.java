package com.cse.warana.service.impl.cvparserImpl.infoExtractorsImpl;

import com.cse.warana.service.cvparser.infoExtractors.ProjectInfoExtraction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nadeeshaan on 11/7/2014.
 */
public class ProjectInfoExtractionImpl implements ProjectInfoExtraction {

    public static ArrayList<String> technologies = new ArrayList<String>();


    /**
     * Extract the project information
     *
     * @param lines
     * @param headingLines
     * @param allHeadings
     * @param linesCopy
     * @throws java.io.IOException
     */
    @Override
    public void extractProjectInfo(ArrayList<String> lines, ArrayList<Integer> headingLines, ArrayList<String> allHeadings, ArrayList<String> linesCopy) {
        /**
         * TODO NEED TO CHECK FOR THE TECHNOLOGIES
         * using the selected word phrase matching
         * mobile application
         * android application, management system, open source contribution, etc..
         */

        BufferedReader br = null;
        String biWrod = "";
        try {
            br = new BufferedReader(new FileReader("input/projBiwordIndex.txt"));
            biWrod = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String lineText = "";
        String[] indexTokens = null;

        ArrayList<String> biWordList = new ArrayList<String>();

        int wordMatchCounter = 0;
        int startProjectLine = -1;
        int endProjectLine = -1;

        StringTokenizer tokenizer = null;

        Pattern pattern = null;
        Matcher matcher = null;

        populateByFile("F:\\Accademic\\Semister 7\\Final_Year_Project\\Codes\\SectionIdentifier2\\input\\technologies", technologies);


        /**
         * Heuristic is:
         * People usually include the project related technologies after or within the project description and
         * the project description is always included after the name of the project
         */
        System.out.println("----Beginning Project Information----");
        for (int a = 0; a < headingLines.size(); a++) {
            startProjectLine = -1;
            endProjectLine = -1;
            for (int b = (headingLines.get(a).intValue() + 1); b < lines.size(); b++) {
                lineText = lines.get(b);
                if (allHeadings.contains(String.valueOf(b))) {
                    break;
                } else {

                    // Set the start of the set of lines which is used to describe the project
                    if (startProjectLine == -1) {
                        startProjectLine = b;
                    }
                    if (checkForTechnologiesDescription(lineText)) {
                        endProjectLine = b;
                        for (int x = startProjectLine; x < endProjectLine; x++) {
                            tokenizer = new StringTokenizer(lines.get(x), " ");
                            if (tokenizer.countTokens() > 2) {
                                System.out.println(lines.get(x));
                            }
                        }
                        startProjectLine = -1;
                    }
                }
            }
        }
        System.out.println("----Ending Project Information----\n");
    }


    /**
     * TODO check what if the technologies are included in the next line also as an example
     * Technologies :
     * C#, Java, etc..
     *
     * @param lineText
     * @return
     */
    @Override
    public boolean checkForTechnologiesDescription(String lineText) {
        String tempText = lineText;
        String tempText2 = lineText;
        String temp = "";
        String[] tokens = null;
        StringTokenizer tokenizer = null;

        /**
         * Check whether the line contains the word technologies or technology
         * In order to identify the line which describes the technologies
         */
        if (tempText.toLowerCase().contains("technologies") || tempText.toLowerCase().contains("technology")) {

            String[] technologyArr = tempText.substring(tempText.toLowerCase().indexOf("technologies") + 12).split(",");
            if (technologyArr.length > 1){
                for (int a = 0; a < technologyArr.length; a++){
                    technologyArr[a] = technologyArr[a].replaceAll("[:]","");
                    System.out.println("************" + technologyArr[a].toLowerCase().trim());
                    if (!technologies.contains(technologyArr[a].toLowerCase().trim())) {
                        // Write to the technologies file after lowering the case
                        // Also added to the technologies in order to avoid the duplicate entries entering the file.
                        technologies.add(technologyArr[a].toLowerCase().trim());
                    }
                }
                return true;
            }else {
                tokenizer = new StringTokenizer(tempText, " ");
                if (tokenizer.countTokens() <= 2) {
                    System.out.println("*************" + tempText);
                    return true;
                }
            }
        }

        // Check the lines which are usually included technologies in the last line
        tokens = tempText2.split(",");
        if (tokens.length > 0) {
            for (int x = 0; x < tokens.length; x++) {
                if (technologies.contains(tokens[x].toLowerCase().trim())) {
                    for (int y = 0; y < tokens.length; y++) {
                        if (!technologies.contains(tokens[y].toLowerCase().trim())) {

                            /**
                             * TODO Write to the technologies file after lowering the case (enrich the gazeteer list)
                             * Also added to the technologies in order to avoid the duplicate entries entering the file.
                             */
                            technologies.add(tokens[y].toLowerCase().trim());
                        }
                    }
                    return true;
                }
            }
        }

        /**
         * If the technologies are included within brackets and without above two checks satisfying. As an example
         * module Programming Challenge 2 (Java swing ,Joomla CMS)
         */
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(tempText2);
        if (matcher.find()) {
            String[] tokenArr = (matcher.group(1)).split(",");
            if (tokenArr.length > 0) {
                for (int x = 0; x < tokenArr.length; x++) {
                    if (technologies.contains(tokenArr[x].toLowerCase().trim())) {
                        for (int y = 0; y < tokenArr.length; y++) {
                            if (!technologies.contains(tokenArr[y].toLowerCase().trim())) {

                                /**
                                 * TODO Write to the technologies file after lowering the case (enrich the gazeteer list)
                                 * Also added to the technologies in order to avoid the duplicate entries entering the file.
                                 */
                                technologies.add(tokenArr[y].toLowerCase().trim());
                            }
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
     * Read the Gazeteer List from the file and load it to the memory
     *
     * @param path
     * @param list
     */
    @Override
    public void populateByFile(String path, ArrayList<String> list) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            while (line != null) {
                list.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
