module org.example.terminalfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.sql;
    requires com.oracle.database.jdbc;


    opens org.example.terminalfx to javafx.fxml;
    exports org.example.terminalfx;
}