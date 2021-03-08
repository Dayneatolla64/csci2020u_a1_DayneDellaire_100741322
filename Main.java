package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import java.io.*;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Spam Probability Checker");

        //Filename Column
        TableColumn<TestFile, String> filenameColumn = new TableColumn<>("File");
        filenameColumn.setMinWidth(500);
        filenameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));

        //Spam Probability Column
        TableColumn<TestFile, String> spamColumn = new TableColumn<>("Spam Probability");
        spamColumn.setMinWidth(200);
        spamColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));

        //Class Column
        TableColumn<TestFile, String> classColumn = new TableColumn<>("Actual Class");
        classColumn.setMinWidth(200);
        classColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

        //Directory Chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(primaryStage);

        TrainData trainData = new TrainData();
        trainData.readDirectory(getDirectories(mainDirectory, "train"));
        TestData testData = new TestData(trainData.generateSpamProbabilityMap(), trainData.generateHamProbabilityMap());

        testData.readDirectory(getDirectories(mainDirectory, "test"));
        List<TestFile> temp = testData.getClasses();

        //Initialize Tableview and Data
        TableView table = new TableView();
        DataSource data = new DataSource();

        //Set Items on Table
        table.setItems(data.getAllTests(temp));
        table.getColumns().addAll(filenameColumn, classColumn, spamColumn);

        //Display Accuracy and Precision
        Label accuracyLabel = new Label("Accuracy : " + testData.calculateAccuracy().toString());
        Label precisionLabel = new Label("Precision : " + testData.calculatePrecision().toString());

        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(20,20,20,20));
        vBox.getChildren().addAll(table, accuracyLabel, precisionLabel);

        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();
    }
    
    //This Method returns subdirectiors from the main directory
    public static String getDirectories(File dir, String key){
        if(dir.isDirectory()){
            File[] content = dir.listFiles();

            for(File current: content){
                if(current.getName().equals(key)){
                    return current.getAbsolutePath();
                }
            }
        }
        return "null";
    }

    public static void main(String[] args) {
        launch(args);
    }
    }


