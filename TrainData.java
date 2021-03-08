package sample;

import java.io.*;
import java.util.*;

public class TrainData{
    private Map<String, Integer> spamWordFreq; //Frequency map of how many times a word appears
    private Map<String, Integer> hamWordFreq; //Frequency map of how many times a word appears
    private Map<String, Double> spamProbMap; //Spam probabilities
    private Map<String, Double> hamProbMap; //Ham probabilities
    private int amountOfSpam;
    private int amountOfHam;

    public TrainData() {
        spamWordFreq = new TreeMap<>();
        hamWordFreq = new TreeMap<>();
        spamProbMap = new TreeMap<>();
        hamProbMap = new TreeMap<>();
        amountOfSpam = 0;
        amountOfHam = 0;
    }

    public Map<String, Integer> getSpamWordFreq(){return spamWordFreq;}
    public int getAmountOfSpam(){return amountOfSpam;}
    public int getAmountOfHam(){return amountOfHam;}

    public void iterateUsingLambda(Map<String, Integer> map) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }
    
    /*
    readFile
    
    @File file
    @Map<String, Integer> map
    
    This method will take a file and a map and will read the given files recursively until a frequencyMap is
    generated for how many times a word appears accross all the files
    */
    public void readFile(File file, Map<String, Integer> map) throws IOException{
        System.out.println("Starting parsing the file:" + file.getAbsolutePath());

        if(file.isDirectory()){
            File[] content = file.listFiles();
            for(File current: content){
                readFile(current, map);
            }
        }else{
            Scanner scanner = new Scanner(file);
            List temp = new ArrayList<>();

            if(file.getParentFile().getName().equals("ham")){
                amountOfHam++;
            } else if(file.getParentFile().getName().equals("spam")){
                amountOfSpam++;
            }

            while (scanner.hasNext()){
                String word = scanner.next();
                if (isValidWord(word) && temp.indexOf(word) == -1){
                    temp.add(word);

                    if(map.containsKey(word)){
                        int previous = map.get(word);
                        map.put(word, previous+1);
                    }else{
                        map.put(word, 1);
                    }
                }
            }
        }
    }
    
    //Taken from Class Examples
    private boolean isValidWord(String word){
        String allLetters = "^[a-zA-Z]+$";
        return word.matches(allLetters);
    }
    
    /*
    generateSpamProbabilityMap
  
    This method will calculate the probability map for spam
    
    @return Map<String, Double>
    */
    public Map<String, Double> generateSpamProbabilityMap(){
        //Check if the spam and ham map have key, value pairs
        if(spamWordFreq != null) {
            //Calculate Pr(W|S) and Pr(W|H)
            for (Map.Entry<String, Integer> entry : spamWordFreq.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                spamProbMap.put(entry.getKey(), ((double)entry.getValue() / (double)amountOfSpam));
            }
        }

        return spamProbMap;
    }
    
    /*
    generateHamProbabilityMap
  
    This method will calculate the probability map for ham
    
    @return Map<String, Double>
    */
    public Map<String, Double> generateHamProbabilityMap(){
        if(hamWordFreq != null) {
            for (Map.Entry<String, Integer> entry : hamWordFreq.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                hamProbMap.put(entry.getKey(), ((double)entry.getValue()/(double)amountOfHam));
            }
        }
            return hamProbMap;
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
}
