package sample.exercise3;

import com.sun.rowset.JdbcRowSetImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

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
            rowSet.setCommand("SELECT * FROM "+name.getText());
            rowSet.execute();

            ResultSetMetaData rsMetaData = rowSet.getMetaData();
            for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
                final int j = i-1;
                TableColumn col = new TableColumn(rsMetaData.getColumnName(i));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                        ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                table.getColumns().add(col);
            }
            System.out.println();

            // Iterate through the result and print the student names
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
