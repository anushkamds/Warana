package com.cse.warana.utility.AggregatedProfileGenerator.ProfileMaker.Skills;

import com.cse.warana.utility.AggregatedProfileGenerator.PhraseExtractor.AlgorithmComparator;
import com.cse.warana.utility.AggregatedProfileGenerator.utils.Config;
import com.cse.warana.utility.AggregatedProfileGenerator.utils.FileManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thilina on 11/11/2014.
 */
public class SkillAnalyzer {

    private String aggregateSkillsPath = Config.aggregatedSkillsPath;
    private String aggregateProfilesPath = Config.aggregatedProfilesPath;
    private String normalizedProfilesPath = Config.normalizedProfilesPath;
    private String profileName;
    private Map<String, Double> sortedSkills = new HashMap<String, Double>();
    private Map<String, Double> aggregatedAllSkillTerms = new HashMap<String, Double>();
    FileManager fileManager;

    public SkillAnalyzer() {

        fileManager = new FileManager();
        aggregatedAllSkillTerms = fileManager.FileToMap(new File(Config.aggregatedAllDocsPath + "/SkillDocs.csv"));
    }

    public void SortSkillsBatch() {
        sortedSkills = new HashMap<String, Double>();
        File skillDocs = new File(aggregateSkillsPath);
        String[] skillNames = skillDocs.list();
        double score = 0;
        AlgorithmComparator comparotor = new AlgorithmComparator();
        comparotor.Compare(Config.profilesOutputPath, Config.normalizedProfilesPath, Config.aggregatedProfilesPath, Config.abbreviationsProfilesPath);
        File profileRoot = new File(Config.aggregatedProfilesPath);
        for (File file : profileRoot.listFiles()) {
            HashMap<String, Double> profileMap = fileManager.FileToMap(file);
            for (String skillName : skillNames) {
//                if (skillName.contains("_out")){
                score = analyzeSkill(skillName, profileMap);
                sortedSkills.put(skillName.split("_")[0], score);
//                }
            }
//            System.out.println("\n" + file.getName() + "====================\n");
            sortedSkills = fileManager.SortByComparator(sortedSkills);
            fileManager.WriteFile(file.getName(), sortedSkills, Config.processedProfilesPath);
        }
    }

    public void SortSkillsBatch(String srcPath, String destPath) {
        sortedSkills = new HashMap<String, Double>();
        File skillDocs = new File(Config.aggregatedSkillsPath);
        String[] skillNames = skillDocs.list();
        new File(destPath).mkdirs();
        double score = 0;
        File profileRoot = new File(srcPath);
        for (File file : profileRoot.listFiles()) {
            System.out.println(file.getName());
            HashMap<String, Double> profileMap = fileManager.FileToMap(file);
            for (String skillName : skillNames) {
                score = analyzeSkill(skillName, profileMap, srcPath);
                sortedSkills.put(skillName.split("_")[0], score);
                System.out.println(skillName+"-"+score);
            }

            sortedSkills = fileManager.SortByComparator(sortedSkills);
            fileManager.WriteFile(file.getName(), sortedSkills, destPath);
        }
    }

    public Map<String, Double> SortSkills(long id) {                                   // analyse skills for 1 user
        sortedSkills = new HashMap<String, Double>();
        File skillDocs = new File(aggregateSkillsPath);
        String[] skillNames = skillDocs.list();
        double score = 0;
        File profileRoot = new File(Config.aggregatedProfilesPath);
        File file = new File(Config.aggregatedProfilesPath + File.separator + id + ".csv");

        if (!file.exists()) {
            System.out.println("no file :" + file.getPath());
            return null;
        }

        HashMap<String, Double> profileMap = fileManager.FileToMap(file);
        for (String skillName : skillNames) {
//                if (skillName.contains("_out")){
            score = analyzeSkill(skillName, profileMap);
            if (score > 0) {
                sortedSkills.put(skillName.split(".csv")[0].toLowerCase(), score);
//            System.out.println(skillName.split("_")[0]+ score+"**********************************");
            }
//                }
        }
//        System.out.println("\n" + file.getName() + "====================\n");
        sortedSkills = fileManager.SortByComparator(sortedSkills);
        fileManager.WriteFile(file.getName(), sortedSkills, Config.processedProfilesPath);

        return sortedSkills;
    }


    public void ClusterSkills() {
        this.profileName = profileName;
        File skillDocs = new File(aggregateSkillsPath);
        String[] skillNames = skillDocs.list();
        double score = 0;
        AlgorithmComparator comparotor = new AlgorithmComparator();
        comparotor.Compare(Config.profilesPath, Config.normalizedProfilesPath, Config.aggregatedProfilesPath, Config.abbreviationsProfilesPath);
//        File profileTermEx=new File("D:\\Projects\\Repositories\\Final Year Project\\SigmaCV finder 2\\src\\com.cse.warana.utility.AggregatedProfileGenerator.ProfileMaker\\Skills\\Aggregated\\UserDocs\\Thilina Premasiri_out.csv");
        File profileRoot = new File(Config.aggregatedSkillsPath);
        for (File file : profileRoot.listFiles()) {
            HashMap<String, Double> profileMap = fileManager.FileToMap(file);

            for (String skillName : skillNames) {
                if (skillName.contains("_out")) {
                    score = analyzeSkill(skillName, profileMap);
                    sortedSkills.put(skillName.split("_")[0], score);
                }
            }
//            System.out.println("\n" + file.getName() + "====================\n");
            sortedSkills = fileManager.SortByComparator(sortedSkills);
//            for (Map.Entry<String, Double> entry : sortedSkills.entrySet()) {
//                System.out.println(entry.getKey() + " : " + entry.getValue());
//            }
            fileManager.NormalizeMap((HashMap<String, Double>) sortedSkills);
            fileManager.WriteFile(file.getName(), sortedSkills, Config.processedSkillsPath);
        }

    }

    private double analyzeSkill(String skillName, HashMap<String, Double> profileTermEx) {
        File aggregatedSkillFile = new File(aggregateSkillsPath + "/" + skillName);
//        fileManager.Normalize(aggregatedSkillFile);
        HashMap<String, Double> skillMap = fileManager.FileToMap(aggregatedSkillFile);
        double score = 0;
        int i = 0;

        for (Map.Entry<String, Double> profileEntry : profileTermEx.entrySet()) {
            i = 0;
            for (Map.Entry<String, Double> skillEntry : skillMap.entrySet()) {
//                if(skillEntry.getKey().contains(profileEntry.getKey()) && aggregatedAllSkillTerms.get(skillEntry.getKey())<30){
//                    score+=profileEntry.getValue()*skillEntry.getValue()*(profileEntry.getKey().length()/skillEntry.getKey().length());
////                    score++;
//                }
                if (skillEntry.getKey().contains(profileEntry.getKey())) {
                    score += profileEntry.getValue() * (skillEntry.getValue() - aggregatedAllSkillTerms.get(skillEntry.getKey())) * (profileEntry.getKey().length() / skillEntry.getKey().length());
//                    score++;
                }
//                if(skillEntry.getKey().equals(profileEntry.getKey())){
////                  score+=profileEntry.getValue()*skillEntry.getValue();
//                    score++;
//                }
                i++;
                if (i > Config.maxEntries)
                    break;
            }
        }
//        System.out.println(skillName+" : "+score);
        return score;
    }
    private double analyzeSkill(String skillName, HashMap<String, Double> profileTermEx, String srcPath) {
        File aggregatedSkillFile = new File(Config.aggregatedSkillsPath + "/" + skillName);
        HashMap<String, Double> skillMap = fileManager.FileToMap(aggregatedSkillFile);
        double score = 0;
        int i = 0;

        for (Map.Entry<String, Double> profileEntry : profileTermEx.entrySet()) {
            i = 0;
            for (Map.Entry<String, Double> skillEntry : skillMap.entrySet()) {
                if (skillEntry.getKey().contains(profileEntry.getKey())) {
                    score += profileEntry.getValue() * (skillEntry.getValue()) * (profileEntry.getKey().length() / skillEntry.getKey().length());
                }

                i++;
                if (i > Config.maxEntries)
                    break;
            }
        }
        return score;
    }

    public static void main(String[] args) {
        Config.initialize("C:\\Warana");
        SkillAnalyzer sk = new SkillAnalyzer();
//        Map<String,Double> skillScores=sk.SortSkillsBatch("Nisansa Dilushan de Silva");
        sk.SortSkillsBatch();
//        sk.ClusterSkills();
    }
}
