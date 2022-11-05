module com.example.windowapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires javafx.media;


    opens com.example.windowapp to javafx.fxml;
    exports com.example.windowapp;
}