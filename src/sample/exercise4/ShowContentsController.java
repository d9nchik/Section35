package sample.exercise4;

import com.sun.rowset.JdbcRowSetImpl;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShowContentsController {
    @FXML
    private ComboBox<String> name;

    @FXML
    private TableView<ObservableList<String>> table;
    private RowSet rowSet;

    public void initialize() {
        new Thread(() -> {
            try {
                rowSet = new JdbcRowSetImpl();
                rowSet.setUrl("jdbc:mysql://database-1.clvf3bby1e0i.us-east-1.rds.amazonaws.com/" +
                        "javaBook?serverTimezone=UTC");
                rowSet.setUsername("scott");//if you looking for db, you are not in right place.
                rowSet.setPassword("tiger");//I`ve already killed it to protect data from school hacker
                rowSet.setCommand("SHOW TABLES");
                rowSet.execute();
                ArrayList<String> tableNames = new ArrayList<>();
                while (rowSet.next())
                    tableNames.add(rowSet.getString(1));
                Platform.runLater(() -> name.getItems().addAll(tableNames));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void show() {
        try {
            table.getItems().clear();
            rowSet.setCommand("SELECT * FROM " + name.getSelectionModel().getSelectedItem());
            rowSet.execute();

            ResultSetMetaData rsMetaData = rowSet.getMetaData();
            for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                final int j = i - 1;
                TableColumn<ObservableList<String>, String> col = new TableColumn<>(rsMetaData.getColumnName(i));
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                table.getColumns().add(col);
            }
            System.out.println();

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rowSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rsMetaData.getColumnCount(); i++)
                    row.add(rowSet.getString(i));
                data.add(row);
            }
            table.setItems(data);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
