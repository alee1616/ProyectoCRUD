module com.example.proyectocrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.example.proyectocrud.controller to javafx.fxml;
    opens com.example.proyectocrud.model to javafx.fxml, javafx.base;
    opens com.example.proyectocrud to javafx.fxml;

    exports com.example.proyectocrud;
    exports com.example.proyectocrud.controller to javafx.fxml;
}