package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;

public class TestData{
    private Map<String, Integer> spamWordFreq; //Stores how frequent a word appears among all the files
    private Map<String, Integer> hamWordFreq; //Stores how frequent a word appears among all the files
    private Map<String, Double> spamProbMap; //Probability map for spam files
    private Map<String, Double> hamProbMap; //Probability map for ham files
    private Map<String, Double> probabilities;
    private List<TestFile> testClasses = new ArrayList<TestFile>(); //Holds all TestFile objects
    private int amountOfSpam; //Total amount of spam files
    private int amountOfHam; //Total amount of ham files
    
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
    
    /*
    readFile
    @File
    @Map<String, Integer>
    
    This method will take a file and a map and fill up the testFile list with TestFiles. It does this
    by reading each file word for word and storing the words of the current file into map. It then
    passes the map to the calculateProbabilities method which returns a the probability for the TestFile
    object. It then creates a TestFile object and stores it in the list. This method repeats for all documents
    */
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
    
    //Taken from Course Examples
    private void countWord(String word, Map<String, Integer> map){
        if(map.containsKey(word)){
            int previous = map.get(word);
            map.put(word, previous+1);
        }else{
            map.put(word, 1);
        }
    }
    
    //Taken from Course Examples
    private boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        return word.matches(allLetters);
    }
    
    /*
    calculateProbability

    @Map<String, Integer>
    
    This method will take a map which contains all word in a file and do
    calculations to determine the probability that the given file is spam
    or not (doesnt work)
    
    @return double
    */
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
    
    /*
    calculateAccuracy
    
    This method will do calculations to determine the accuracy of the program
    
    @return double
    */
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
    
    /*
    calculatePrecision
    
    This method will do calculations to determine the precision of the program
    
    @return double
    */
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
    
    /*
    readDirectory
    
    @String path
    
    This method will take a String that represents the main path and will go into both the
    spam and ham folders so each has its words counted and probabilities generated
    */
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
