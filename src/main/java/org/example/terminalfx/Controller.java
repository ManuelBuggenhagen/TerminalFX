package org.example.terminalfx;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    int userID;
    String userName;

    @FXML
    private AnchorPane loginAP;
    @FXML
    private AnchorPane mainWindowAP;
    @FXML
    private JFXButton loginBT;
    @FXML
    private JFXButton logoutBT;
    @FXML
    private TextField loginTB;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loginBT.setOnMouseClicked(event -> {
            if (onlyNumbers(loginTB.getText())) {
                userID = Integer.parseInt(loginTB.getText());
                if (checkID(userID)) {
                    loginAP.setVisible(false);
                    mainWindowAP.setVisible(true);
                    System.out.println("success");
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Achtung");
                    alert.setHeaderText("login erfolglos");
                    alert.setContentText("gib deine richtige Insassen ID ein");
                    if (alert.showAndWait().get() == ButtonType.OK) {
                        loginTB.clear();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Achtung");
                alert.setHeaderText("nur Zahlen erlaubt");
                alert.setContentText("gib deine richtige Insassen ID ein");
                if (alert.showAndWait().get() == ButtonType.OK) {
                    loginTB.clear();
                }
            }


        });

        logoutBT.setOnMouseClicked(event -> {
            mainWindowAP.setVisible(false);
            loginAP.setVisible(true);
        });


    }

    private boolean checkID(int id) {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String username = "DieKnastiGmbH";
            String passwort = "DieKnastiGmbH";
            String url = "jdbc:oracle:thin:@rs03-db-inf-min.ad.fh-bielefeld.de:1521:ORCL";
            con = DriverManager.getConnection(url, username, passwort);
            System.out.println("connected successfully!");

            String command = "{call compareidforlogin(?,?)}";
            CallableStatement cs = con.prepareCall(command);

            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.VARCHAR);

            try {
                cs.execute();
                String result = cs.getString(2);
                this.userName = result;
                System.out.println("stored procedure result: " + result);
                con.close();
                return true;
            } catch (Exception e) {
                System.out.println("kein inmate mit dieser Nummer vorhanden!");
                con.close();
                return false;
            }

        } catch (Exception ex) {
            System.out.println("could not connect with database!");
            System.out.println(ex.getMessage());
        }
        try {
            assert con != null;
            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private boolean onlyNumbers(String s) {
        return s.chars().allMatch(Character::isDigit);
    }

}