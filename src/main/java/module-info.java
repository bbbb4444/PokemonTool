module com.example.pokemontool {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.pokemontool to javafx.fxml;
    exports com.example.pokemontool;
}