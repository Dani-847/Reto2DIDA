package org.drk.reto2dida.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DataProvider {

    private static SessionFactory sessionFactory = null;

    private DataProvider() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            if (dbUser == null || dbPassword == null) {
                JavaFXUtil.showModal(javafx.scene.control.Alert.AlertType.ERROR,
                        "Error conexión BD",
                        "Variables de entorno no definidas",
                        "Asegúrate de que Docker esté corriendo y que las variables `DB_USER` y `DB_PASSWORD` estén definidas.");
                throw new IllegalStateException("DB environment variables missing");
            }

            try {
                var configuration = new Configuration().configure();
                configuration.setProperty("hibernate.connection.username", dbUser);
                configuration.setProperty("hibernate.connection.password", dbPassword);

                try {
                    sessionFactory = configuration.buildSessionFactory();
                } catch (Exception ex) {
                    JavaFXUtil.showModal(javafx.scene.control.Alert.AlertType.ERROR,
                            "Error conexión BD",
                            "No se pudo conectar a la base de datos",
                            "¿Está Docker corriendo?");
                    throw ex;
                }
            } catch (Exception ex) {
                JavaFXUtil.showModal(javafx.scene.control.Alert.AlertType.ERROR,
                        "Error inicializando Hibernate",
                        "Fallo al configurar la conexión",
                        "Detalle: " + ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
        return sessionFactory;
    }
}
