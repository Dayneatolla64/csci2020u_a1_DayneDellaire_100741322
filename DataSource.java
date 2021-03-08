package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/*
Turns the ArrayList into a FxCollections obserableArrayList 
(I know I didn't need another function for this but I wasn't able to get it working and when I solved the
issue I was too lazy to delete this file).
*/
public class DataSource {
    public static ObservableList<TestFile> getAllTests(List<TestFile> temp) {
        return FXCollections.observableArrayList(temp);
    }
}
