package sample.exercise2;

import com.sun.rowset.JdbcRowSetImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.sql.RowSet;
import java.sql.*;

public class StaffController {
    @FXML
    private TextField last, first, mi, address, city, state, telephone, email, zip;

    @FXML
    private Text message;

    private RowSet rowSet;

    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                rowSet = new JdbcRowSetImpl();
                rowSet.setUrl("jdbc:mysql://database-2.clvf3bby1e0i.us-east-1.rds.amazonaws.com/" +
                        "javaBook?serverTimezone=UTC");
                rowSet.setUsername("scott");//if you looking for db, you are not in right place.
                rowSet.setPassword("tiger");//I`ve already killed it to protect data from school hacker
                rowSet.setCommand("SELECT * FROM Address");
                rowSet.execute();
                first();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {//close resource
            while (last.getScene() == null || last.getScene().getWindow() == null) {
                Thread.yield();
            }
            last.getScene().getWindow().setOnCloseRequest(event -> {
                if (rowSet != null) {
                    try {
                        rowSet.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }).start();
    }

    @FXML
    public void first(){
        new Thread(() -> {
            try {
                rowSet.first();
                Platform.runLater(this::view);
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    @FXML
    public void last(){
        new Thread(() -> {
            try {
                rowSet.last();
                Platform.runLater(this::view);
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    @FXML
    public void previous() {
        new Thread(() -> {
            try {
                rowSet.previous();
                Platform.runLater(this::view);
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    @FXML
    public void next() {
        new Thread(() -> {
            try {
                rowSet.next();
                Platform.runLater(this::view);
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    private void view() {
        try {
            last.setText(rowSet.getString("lastname"));
            first.setText(rowSet.getString("firstname"));
            mi.setText(rowSet.getString("mi"));
            address.setText(rowSet.getString("street"));
            city.setText(rowSet.getString("city"));
            state.setText(rowSet.getString("state"));
            telephone.setText(rowSet.getString("telephone"));
            email.setText(rowSet.getString("email"));
            zip.setText(rowSet.getString("zip"));
            message.setText("ID found");
        } catch (SQLException throwables) {
            message.setText("ID not found.");
        }
    }

    @FXML
    public void insert() {
        new Thread(() -> {
            try {
                rowSet.moveToInsertRow();
                rowSet.updateString("lastname", last.getText());
                rowSet.updateString("firstname", first.getText());
                rowSet.updateString("mi", mi.getText());
                updateBio();
                rowSet.insertRow();
                rowSet.moveToCurrentRow();
                message.setText("Successfully inserted");
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    @FXML
    public void update() {
        new Thread(() -> {
            try {//TODO: solve problem of synchronization
                rowSet.setCommand("Select * FROM  Address Where firstname=? and mi=? and lastname=?");
                rowSet.setString(1, first.getText());
                rowSet.setString(2, mi.getText());
                rowSet.setString(3, last.getText());
                rowSet.execute();
                rowSet.next();
                updateBio();
                rowSet.updateRow();

                message.setText("Successfully updated");
                rowSet.setCommand("Select * From Address");
                rowSet.execute();
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    private void updateBio() throws SQLException {
        rowSet.updateString("street", address.getText());
        rowSet.updateString("city", city.getText());
        rowSet.updateString("state", state.getText());
        rowSet.updateString("telephone", telephone.getText());
        rowSet.updateString("email", email.getText());
        rowSet.updateString("zip", zip.getText());
    }

    @FXML
    public void clear() {
        new Thread(() -> {
            try {
                rowSet.setCommand("Select * FROM  Address Where firstname=? and mi=? and lastname=?");
                rowSet.setString(1, first.getText());
                rowSet.setString(2, mi.getText());
                rowSet.setString(3, last.getText());
                rowSet.execute();
                rowSet.next();
                rowSet.deleteRow();
                message.setText("Cleared successfully");
                rowSet.setCommand("Select * From Address");
                rowSet.execute();
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }
}
