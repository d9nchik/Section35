package sample.exercise3;

import com.sun.rowset.JdbcRowSetImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ShowContentsController {
    @FXML
    private TextField name;

    @FXML
    private TableView<ObservableList<String>> table;
    private RowSet rowSet;

    public void initialize() {
        new Thread(() -> {
            try {
                rowSet = new JdbcRowSetImpl();
                rowSet.setUrl("jdbc:mysql://database-1.clvf3bby1e0i.us-east-1.rds.amazonaws.com/" +//TODO: change to normal DB
                        "javaBook?serverTimezone=UTC");
                rowSet.setUsername("scott");//if you looking for db, you are not in right place.
                rowSet.setPassword("tiger");//I`ve already killed it to protect data from school hacker
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void show() {
        try {
            table.getItems().clear();
            rowSet.setCommand("SELECT * FROM " + name.getText());
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
