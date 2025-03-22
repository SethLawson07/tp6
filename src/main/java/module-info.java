module lawson.lonchi.crossword {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.base;
    // requires org.junit.jupiter.api;


    opens lawson.lonchi.crossword to javafx.fxml;
    exports lawson.lonchi.crossword;
        // Exporte le package controller pour que javafx.fxml puisse y accéder
        exports lawson.lonchi.crossword.controller to javafx.fxml;
        opens lawson.lonchi.crossword.controller to javafx.fxml;


        // Exporte les autres packages si nécessaire
        exports lawson.lonchi.crossword.model;
}
