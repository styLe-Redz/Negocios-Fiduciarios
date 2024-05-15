package Bancolombia.view;

import Bancolombia.util.DatabaseConnection;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.sql.Connection;

public class MainView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestión Fiduciaria");

        // Obtener conexión de base de datos utilizando la clase utilitaria
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("No se pudo obtener la conexión a la base de datos.");
            return; // Salir si no hay conexión
        }

        // Contenedor principal
        BorderPane root = new BorderPane();

        // TabPane para gestionar diferentes partes de la aplicación
        TabPane tabPane = new TabPane();

        // Crear pestañas
        Tab tabNegocios = new Tab("Negocios Fiduciarios");
        Tab tabObligaciones = new Tab("Obligaciones");
        Tab tabPersonas = new Tab("Personas Participantes");
        Tab tabRelaciones = new Tab("Listado General");

        // Configurar que las pestañas no sean cerrables
        tabNegocios.setClosable(false);
        tabPersonas.setClosable(false);
        tabObligaciones.setClosable(false);
        tabRelaciones.setClosable(false);

        // Agregar pestañas al TabPane
        tabPane.getTabs().addAll(tabNegocios, tabObligaciones, tabPersonas, tabRelaciones);

        // Configurar el contenido de cada pestaña (aún por implementar)
        tabNegocios.setContent(new NegocioFiduciarioView(connection).getView());
        tabObligaciones.setContent(new ObligacionView(connection).getView());
        tabPersonas.setContent(new PersonasParticipantesView(connection).getView());
        tabRelaciones.setContent(new RelacionesView(connection).getView());
        tabPane.getTabs().add(new Tab("Generador Excel", new GeneradorExcelView(connection).getView()));

        // Añadir el TabPane al contenedor principal
        root.setCenter(tabPane);

        // Crear la escena y mostrarla
        Scene scene = new Scene(root, 850, 513);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
