package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class DataSource {
    public static ObservableList<TestFile> getAllTests(List<TestFile> temp) {
        return FXCollections.observableArrayList(temp);
    }
}