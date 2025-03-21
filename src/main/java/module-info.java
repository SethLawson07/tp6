module lawson.lonchi.crossword {
    requires javafx.controls;
    requires javafx.fxml;

    opens lawson.lonchi.crossword to javafx.fxml;
    exports lawson.lonchi.crossword;
}
