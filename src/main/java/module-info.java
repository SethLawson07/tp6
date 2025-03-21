module lawson.lonchi.crossword {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens lawson.lonchi.crossword to javafx.fxml;
    exports lawson.lonchi.crossword;
}
