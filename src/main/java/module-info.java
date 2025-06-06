module main_pkg{
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.media;
    //requires javafx.swing;
    requires javafx.web;


    opens main_pkg to javafx.fxml;
    exports main_pkg;
}
