package sample.exercise2;

import com.sun.rowset.JdbcRowSetImpl;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.sql.RowSet;
import java.sql.*;

public class StaffController {
    @FXML
    private TextField id, last, first, mi, address, city, state, telephone, email;

    @FXML
    private Text message;

    private RowSet rowSet;

    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                rowSet = new JdbcRowSetImpl();
                rowSet.setUrl("jdbc:mysql://localhost/javaBookserverTimezone=UTC");
                rowSet.setUsername("scott");
                rowSet.setPassword("(76%EtjM");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();


        new Thread(() -> {//close resource
            while (id.getScene() == null || id.getScene().getWindow() == null) {
                Thread.yield();
            }
            id.getScene().getWindow().setOnCloseRequest(event -> {
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
                rowSet.execute();
                view();
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
                rowSet.execute();
                view();
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
                rowSet.execute();
                view();
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
                rowSet.execute();
                view();
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    private void view() {
        try {
            last.setText(rowSet.getString(2));
            first.setText(rowSet.getString(3));
            mi.setText(rowSet.getString(4));
            address.setText(rowSet.getString(5));
            city.setText(rowSet.getString(6));
            state.setText(rowSet.getString(7));
            telephone.setText(rowSet.getString(8));
            email.setText(rowSet.getString(9));
            message.setText("ID found");
        } catch (SQLException throwables) {
            message.setText("ID not found.");
            throwables.printStackTrace();
        }
    }

    @FXML
    public void insert() {
        new Thread(() -> {
            try {
                rowSet.setCommand("INSERT Into Staff VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                rowSet.setString(1, id.getText());//TODO: extract method
                rowSet.setString(2, last.getText());
                rowSet.setString(3, first.getText());
                rowSet.setString(4, mi.getText());
                rowSet.setString(5, address.getText());
                rowSet.setString(6, city.getText());
                rowSet.setString(7, state.getText());
                rowSet.setString(8, telephone.getText());
                rowSet.setString(9, email.getText());
                rowSet.execute();
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
                rowSet.setCommand("UPDATE Staff SET lastName=?, firstName=?, mi=?, address=?," +
                        "city=?, state=?, telephone=?, email=? WHERE id=?");
                rowSet.setString(1, id.getText());
                rowSet.setString(2, last.getText());
                rowSet.setString(3, first.getText());
                rowSet.setString(4, mi.getText());
                rowSet.setString(5, address.getText());
                rowSet.setString(6, city.getText());
                rowSet.setString(7, state.getText());
                rowSet.setString(8, telephone.getText());
                rowSet.setString(9, email.getText());
                rowSet.execute();
                message.setText("Successfully updated");
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }

    @FXML
    public void clear() {
        new Thread(() -> {
            try {
                rowSet.setCommand("DELETE FROM Staff WHERE id=?");
                rowSet.setString(1, id.getText());
                rowSet.execute();
                message.setText("Cleared successfully");
            } catch (SQLException throwables) {
                message.setText(throwables.getMessage());
            }
        }).start();
    }
}
