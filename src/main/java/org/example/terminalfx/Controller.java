package org.example.terminalfx;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import oracle.jdbc.OracleTypes;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    int userID;
    String userName;
    String secondName;
    private ArrayList<String> angebotDescrList = new ArrayList<>();
    private ArrayList<Integer> angebotsIDList = new ArrayList<>();

    @FXML
    private AnchorPane loginAP;
    @FXML
    private AnchorPane mainWindowAP;
    @FXML
    private AnchorPane vertragAP;
    @FXML
    private AnchorPane profilAP;
    @FXML
    private JFXButton loginBT;
    @FXML
    private JFXButton logoutBT;
    @FXML
    private JFXButton profilBT;
    @FXML
    private JFXButton vertragBT;
    @FXML
    private JFXButton zuruckBT1;
    @FXML
    private JFXButton zuruckBT2;
    @FXML
    private JFXButton sendenBT;
    @FXML
    private Label nameLabel;
    @FXML
    private Label vornameLB;
    @FXML
    private Label nachnameLB;
    @FXML
    private Label wohnortLB;
    @FXML
    private Label geburtsdatumLB;
    @FXML
    private Label zelleLB;
    @FXML
    private Label gefangnissLB;
    @FXML
    private Label punkteLB;
    @FXML
    private Label kontoLB;
    @FXML
    private Label allevertrageLB;
    @FXML
    private Label erstelltamLB;
    @FXML
    private Label angbeschreibungLabel;
    @FXML
    private TextField loginTB;
    @FXML
    private TextArea grundTF;
    @FXML
    private ChoiceBox<String> angebotChoice;
    @FXML
    private ChoiceBox<String> dauerChoice;
    @FXML
    private DatePicker beginnChoice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loginBT.setOnMouseClicked(event -> {
            if (onlyNumbers(loginTB.getText()) && !loginTB.getText().isEmpty()) {
                userID = Integer.parseInt(loginTB.getText());
                if (checkID(userID)) {
                    nameLabel.setText(userName + " " + secondName);
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
            loginTB.clear();
            mainWindowAP.setVisible(false);
            loginAP.setVisible(true);
        });

        vertragBT.setOnMouseClicked(event -> {

            beginnChoice.getEditor().clear();
            angbeschreibungLabel.setText(" ");
            grundTF.setText(" ");

            angebotChoice.getItems().clear();
            angebotChoice.getSelectionModel().clearSelection();

            String[] alleAngebote = options();
            angebotChoice.getItems().addAll(alleAngebote);
            dauerChoice.getItems().clear();
            dauerChoice.getItems().addAll("1", "2", "3", "4", "noch nicht sicher");
            mainWindowAP.setVisible(false);
            vertragAP.setVisible(true);
        });

        profilBT.setOnMouseClicked(event -> {
            fillPrisonerData();
            mainWindowAP.setVisible(false);
            profilAP.setVisible(true);
        });

        zuruckBT1.setOnMouseClicked(event -> {
            vertragAP.setVisible(false);
            mainWindowAP.setVisible(true);
        });

        zuruckBT2.setOnMouseClicked(event -> {
            profilAP.setVisible(false);
            mainWindowAP.setVisible(true);
        });

        angebotChoice.setOnAction(event -> {
            if (!angebotChoice.getSelectionModel().isEmpty()) {
                String description = angebotDescrList.get(angebotChoice.getSelectionModel().getSelectedIndex());
                angbeschreibungLabel.setText(description);
            }
        });

        sendenBT.setOnMouseClicked(event -> {
            if (true) {
                sendVertrag();
                vertragAP.setVisible(false);
                mainWindowAP.setVisible(true);
            } else {
                System.out.println("kommt noch");
            }
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

            String command = "{call compareidforlogin(?,?,?)}";
            CallableStatement cs = con.prepareCall(command);

            cs.setInt(1, id);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.registerOutParameter(3, Types.VARCHAR);

            try {
                cs.execute();
                this.userName = cs.getString(2);
                this.secondName = cs.getString(3);
                //System.out.println("stored procedure result: " + this.userName + "  " + secondName);
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

    private String[] options() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String username = "DieKnastiGmbH";
            String passwort = "DieKnastiGmbH";
            String url = "jdbc:oracle:thin:@rs03-db-inf-min.ad.fh-bielefeld.de:1521:ORCL";
            con = DriverManager.getConnection(url, username, passwort);
            System.out.println("connected successfully!");

            String command = "{call get_angebote_fur_insasse(?,?)}";
            CallableStatement cs = con.prepareCall(command);

            cs.setInt(1, userID);
            cs.registerOutParameter(2, OracleTypes.CURSOR);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(2);
            ArrayList<String> angebotList = new ArrayList<>();
            while (rs.next()) {
                angebotList.add(rs.getString(1));
                angebotDescrList.add(rs.getString(2));
                angebotsIDList.add(rs.getInt(3));
            }
            System.out.println(angebotList);
            con.close();
            System.out.println("con closed!");
            return angebotList.toArray(new String[0]);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            try {
                assert con != null;
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    public void sendVertrag() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String username = "DieKnastiGmbH";
            String passwort = "DieKnastiGmbH";
            String url = "jdbc:oracle:thin:@rs03-db-inf-min.ad.fh-bielefeld.de:1521:ORCL";
            con = DriverManager.getConnection(url, username, passwort);
            System.out.println("\nconnected successfully!");

            String command = "{call new_vertrag(?,?,?,?,?)}";
            CallableStatement cs = con.prepareCall(command);


            int angID = angebotsIDList.get(angebotChoice.getSelectionModel().getSelectedIndex());
            System.out.println("angID: " + angID);
            int insID = userID;
            System.out.println("insID: " + insID);
            String startDate = buildStartDate();
            System.out.println("startDate:  " + startDate);
            String endDate = calcEndDate();
            System.out.println("endDate:  " + endDate);
            String notiz = grundTF.getText();
            System.out.println("notiz:  " + notiz);

            cs.setInt(1, angID);
            cs.setInt(2, insID);
            cs.setString(3, startDate);
            cs.setString(4, endDate);
            cs.setString(5, notiz);
            cs.execute();
            con.close();
            System.out.println("neuer Vertrag wurde eingefügt");

        } catch (Exception ex) {
            System.out.println("error!");
            System.out.println(ex.getMessage());
            try {
                assert con != null;
                con.close();
                System.out.println("con closed!");
            } catch (SQLException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    private String buildStartDate() {
        LocalDate time = beginnChoice.getValue();
        return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String calcEndDate() {
        if (dauerChoice.getValue().equals("noch nicht sicher")) {
            return null;
        } else {
            String start = buildStartDate();
            String year = start.substring(start.length() - 4);
            String rest = start.substring(0, 6);
            int dauer = Integer.parseInt(dauerChoice.getValue());
            dauer = dauer + Integer.parseInt(year);
            String endDate = rest + dauer;
            return endDate;
        }
    }

    private void fillPrisonerData() {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String username = "DieKnastiGmbH";
            String passwort = "DieKnastiGmbH";
            String url = "jdbc:oracle:thin:@rs03-db-inf-min.ad.fh-bielefeld.de:1521:ORCL";
            conn = DriverManager.getConnection(url, username, passwort);
            System.out.println("connected successfully!");
            String query = "{call getGefangenenDaten(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            CallableStatement stmt = conn.prepareCall(query);
            stmt.setInt(1, userID);
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.registerOutParameter(5, Types.VARCHAR);
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.registerOutParameter(8, Types.NUMERIC);
            stmt.registerOutParameter(9, Types.NUMERIC);
            stmt.registerOutParameter(10, Types.VARCHAR);
            stmt.registerOutParameter(11, Types.DATE);
            stmt.execute();

            vornameLB.setText(stmt.getString(2));
            nachnameLB.setText(stmt.getString(3));
            wohnortLB.setText(stmt.getString(4));
            geburtsdatumLB.setText(stmt.getString(5));
            zelleLB.setText(stmt.getString(6));
            gefangnissLB.setText(stmt.getString(7));
            punkteLB.setText(stmt.getString(8));
            kontoLB.setText(stmt.getString(9));
            allevertrageLB.setText(stmt.getString(10));
            erstelltamLB.setText(String.valueOf(stmt.getDate(11)));

            conn.close();

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.printf(ex.getMessage());
            try {
                assert conn != null;
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }


}