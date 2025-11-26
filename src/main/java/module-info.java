module org.drk.reto2dida {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires static lombok;
    requires java.naming;


    opens org.drk.reto2dida;
    exports org.drk.reto2dida;
    opens org.drk.reto2dida.utils;
    exports org.drk.reto2dida.utils;
    opens org.drk.reto2dida.controllers;
    exports org.drk.reto2dida.controllers;
    opens org.drk.reto2dida.user;
    exports org.drk.reto2dida.user;
    opens org.drk.reto2dida.pelicula;
    exports org.drk.reto2dida.pelicula;
    opens org.drk.reto2dida.copia;
    exports org.drk.reto2dida.copia;
}