package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;

public class TestData{
    private Map<String, Integer> spamWordFreq;
    private Map<String, Integer> hamWordFreq;
    private Map<String, Double> spamProbMap;
    private Map<String, Double> hamProbMap;
    private Map<String, Double> probabilities;
    private List<TestFile> testClasses = new ArrayList<TestFile>();
    private int amountOfSpam;
    private int amountOfHam;

    public TestData(Map<String, Double> spamProbMap, Map<String, Double> hamProbMap) {
        spamWordFreq = new TreeMap<>();
        hamWordFreq = new TreeMap<>();
        this.spamProbMap = spamProbMap;
        this.hamProbMap = hamProbMap;
    }

    public Map<String, Integer> getSpamWordFreq(){return spamWordFreq;}
    public Map<String, Double> getProbabilities(){return probabilities;}
    public void setProbabilities(Map<String, Double> probabilities){this.probabilities = probabilities;}
    public List<TestFile> getClasses(){return testClasses;}

    public void readFile(File file, Map<String, Integer> map) throws IOException{
        //System.out.println("Starting parsing the file:" + file.getAbsolutePath());
        map = new TreeMap<>();

        if(file.isDirectory()){
            File[] content = file.listFiles();
            for(File current: content){
                readFile(current, map);
            }
        }else{
            Scanner scanner = new Scanner(file);

            if(file.getParentFile().getName().equals("ham")){
                amountOfHam++;
            } else if(file.getParentFile().getName().equals("spam")){
                amountOfSpam++;
            }

            while (scanner.hasNext()){
                String  token = scanner.next();
                if (isValidWord(token)){
                    countWord(token, map);
                }
            }

            System.out.println(map); //Calculate Probability Here
            System.out.println(file.getName() + "   " + file.getParentFile().getName());
            testClasses.add(new TestFile(file.getName(), calculateProbability(map), file.getParentFile().getName()));
        }
    }

    private void countWord(String word, Map<String, Integer> map){
        if(map.containsKey(word)){
            int previous = map.get(word);
            map.put(word, previous+1);
        }else{
            map.put(word, 1);
        }
    }

    private boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        return word.matches(allLetters);
    }

    private Double calculateProbability(Map<String, Integer> map){
        int totalWords = 0;
        double n = 0.0;
        double sw = 0.0;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            double ws = 0.0;
            double wh = 0.0;

            if(spamProbMap.get(entry.getKey()) != null){
                ws = spamProbMap.get(entry.getKey());
            }
            if(hamProbMap.get(entry.getKey()) != null){
                wh = hamProbMap.get(entry.getKey());
            }

            sw = ws/(ws + wh);
            n += Math.log(1-ws)-Math.log(ws);
        }

        if(n == 0){
            return 0.0;
        }

        return 1/(1+Math.pow(Math.E,Math.abs(n)));
    }

    public Double calculateAccuracy(){
        int numOfFiles = amountOfHam + amountOfSpam;
        int numTruePositives = 0;
        int numTrueNegatives = 0;
        int numFalsePositives = 0;
        int numFalseNegatives = 0;

        for(int i = 0; i < testClasses.size(); i++){
            if(testClasses.get(i).getActualClass().equals("ham") && testClasses.get(i).getSpamProbability() < 50.0){
                numTrueNegatives++;
            } else if(testClasses.get(i).getActualClass().equals("ham") && testClasses.get(i).getSpamProbability() > 50.0){
                numFalsePositives++;
            } else if(testClasses.get(i).getActualClass().equals("spam") && testClasses.get(i).getSpamProbability() > 50.0) {
                numTruePositives++;
            } else {
                numFalseNegatives ++;
            }
        }

        return ((double)numTruePositives + (double)numTrueNegatives)/numOfFiles;
    }

    public Double calculatePrecision(){
        int numOfFiles = amountOfHam + amountOfSpam;
        int numTruePositives = 0;
        int numTrueNegatives = 0;
        int numFalsePositives = 0;
        int numFalseNegatives = 0;

        for(int i = 0; i < testClasses.size(); i++){
            if(testClasses.get(i).getActualClass().equals("ham") && testClasses.get(i).getSpamProbability() < 50.0){
                numTrueNegatives++;
            } else if(testClasses.get(i).getActualClass().equals("ham") && testClasses.get(i).getSpamProbability() > 50.0){
                numFalsePositives++;
            } else if(testClasses.get(i).getActualClass().equals("spam") && testClasses.get(i).getSpamProbability() > 50.0) {
                numTruePositives++;
            } else {
                numFalseNegatives ++;
            }
        }

        return (double)numTruePositives/((double)numFalsePositives + (double)numTruePositives);
    }

    public void readDirectory(String path){
        File file = new File(path);                                                         //Given a path to either Test or Trial locate spam and ham

        if(file.isDirectory()){
            File[] content = file.listFiles();                                              //Cycle through the files in the directory

            for(File current: content){
                if(current.isDirectory() && current.getName().equals("spam")){
                    File spam = current;
                    System.out.println("Spam Located!");

                    try{
                        readFile(spam, spamWordFreq);
                    }catch(FileNotFoundException e){
                        System.err.println("Invalid input dir: " + spam.getAbsolutePath());
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } else if(current.isDirectory() && current.getName().equals("ham")){
                    File ham = current;
                    System.out.println("Ham Located!");

                    try{
                        readFile(ham, hamWordFreq);
                    }catch(FileNotFoundException e){
                        System.err.println("Invalid input dir: " + ham.getAbsolutePath());
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Did not locate spam or ham folders at: " + path);
                    System.exit(0);
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}
