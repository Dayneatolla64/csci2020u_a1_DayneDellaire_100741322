import java.io.*;
import java.util.*;

public class TrainData{
    private Map<String, Integer> spamWordFreq;
    private Map<String, Integer> hamWordFreq;
    private int amountOfSpam;
    private int amountOfHam;

    public TrainData() {
        spamWordFreq = new TreeMap<>();
        hamWordFreq = new TreeMap<>();
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

    private boolean isValidWord(String word){
		String allLetters = "^[a-zA-Z]+$";
		return word.matches(allLetters);
	}

    public Map<String, Double> generateProbabilityMap(){
        Map<String, Double> probabilities = new TreeMap<>(); //Map that holds the probabilities or Pr(S|W)
        Map<String, Double> prWS = new TreeMap<>(); //Map that holds the Pr(W|S)
        Map<String, Double> prWH = new TreeMap<>(); //Map that holds the Pr(W|H)
        
        //Check if the spam and ham map have key, value pairs
        if(spamWordFreq != null && hamWordFreq != null){
            //Calculate Pr(W|S) and Pr(W|H)
            for (Map.Entry<String, Integer> entry : spamWordFreq.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                probabilities.put(entry.getKey(), 0.0);
                prWS.put(entry.getKey(), ((double)entry.getValue()/(double)amountOfSpam));
                
            }
            for (Map.Entry<String, Integer> entry : hamWordFreq.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                probabilities.put(entry.getKey(), 0.0);
                prWH.put(entry.getKey(), ((double)entry.getValue()/(double)amountOfSpam));
            }
            //Calculate Pr(S|W)
            for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
                double ws = 0.0;
                double wh = 0.0;
                
                if(prWS.get(entry.getKey()) != null){
                    ws = prWS.get(entry.getKey());
                } 
                if (prWH.get(entry.getKey()) != null){
                    wh = prWH.get(entry.getKey());
                }

                probabilities.put(entry.getKey(), ws/(ws + wh));
            }
        } else {
            System.out.println("nothing in spam and ham maps. Try using [ .readDirectory ] first!");
        }

        return probabilities;
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
        TrainData trainCounter = new TrainData();
        
        trainCounter.readDirectory("D:/Programs/Java/csci2020uAssignment/data/train");

        Map<String, Integer> spamWordFreq = trainCounter.getSpamWordFreq();

        System.out.println(spamWordFreq);
        trainCounter.generateProbabilityMap();
        System.out.println(trainCounter.getAmountOfHam());
        System.out.println(trainCounter.getAmountOfSpam());
    }
}