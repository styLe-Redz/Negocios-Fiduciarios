package Bancolombia.view;

import Bancolombia.controller.NegocioFiduciarioController;
import Bancolombia.model.NegocioFiduciario;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;


public class NegocioFiduciarioView {

    private NegocioFiduciarioController controller;

    public NegocioFiduciarioView(Connection connection) {
        this.controller = new NegocioFiduciarioController(connection);
    }

    public TabPane getView() {
        TabPane tabPane = new TabPane();

        // Crear pestañas para las diferentes funciones
        Tab tabCrear = new Tab("Crear Negocio");
        tabCrear.setContent(crearTab());
        tabCrear.setClosable(false);

        Tab tabActualizar = new Tab("Actualizar Negocio");
        tabActualizar.setContent(actualizarTab());
        tabActualizar.setClosable(false);

        Tab tabListar = new Tab("Listar Negocios");
        tabListar.setContent(listarTab());
        tabListar.setClosable(false);

        Tab tabEliminar = new Tab("Eliminar Negocio");
        tabEliminar.setContent(eliminarTab());
        tabEliminar.setClosable(false);

        // Añadir pestañas al TabPane
        tabPane.getTabs().addAll(tabCrear, tabActualizar, tabListar, tabEliminar);

        return tabPane;
    }

    private VBox crearTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del Negocio");
        TextField descripcionField = new TextField();
        descripcionField.setPromptText("Descripción");
        DatePicker fechaInicioPicker = new DatePicker();
        DatePicker fechaFinPicker = new DatePicker();

        fechaInicioPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            fechaFinPicker.setDayCellFactory(d -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(item.isBefore(newDate));
                }
            });
        });

        Button submitButton = new Button("Crear Negocio");
        submitButton.setOnAction(event -> {
            if (fechaFinPicker.getValue() != null && fechaFinPicker.getValue().isBefore(fechaInicioPicker.getValue())) {
                showAlert("La fecha de fin no puede ser anterior a la fecha de inicio.", Alert.AlertType.ERROR);
                return;
            }
            boolean created = controller.crearNegocio(
                    nombreField.getText(),
                    descripcionField.getText(),
                    fechaInicioPicker.getValue(),
                    fechaFinPicker.getValue()
            );
            if (created) {
                showAlert("Negocio creado con éxito.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error: El nombre del negocio ya existe.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Nombre:"), nombreField,
                new Label("Descripción:"), descripcionField,
                new Label("Fecha de Inicio:"), fechaInicioPicker,
                new Label("Fecha de Fin:"), fechaFinPicker,
                submitButton
        );

        return layout;
    }


    private VBox actualizarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField idField = new TextField();
        idField.setPromptText("ID del Negocio");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del Negocio");
        TextField descripcionField = new TextField();
        descripcionField.setPromptText("Descripción");
        DatePicker fechaInicioPicker = new DatePicker();
        DatePicker fechaFinPicker = new DatePicker();

        fechaInicioPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            fechaFinPicker.setDayCellFactory(d -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(item.isBefore(newDate));
                }
            });
        });

        Button updateButton = new Button("Actualizar Negocio");
        updateButton.setOnAction(event -> {
            if (fechaFinPicker.getValue() != null && fechaFinPicker.getValue().isBefore(fechaInicioPicker.getValue())) {
                showAlert("La fecha de fin no puede ser anterior a la fecha de inicio.", Alert.AlertType.ERROR);
                return;
            }
            try {
                boolean updated = controller.actualizarNegocio(
                        Integer.parseInt(idField.getText()),
                        nombreField.getText(),
                        descripcionField.getText(),
                        fechaInicioPicker.getValue(),
                        fechaFinPicker.getValue()
                );
                if (updated) {
                    showAlert("Negocio actualizado con éxito.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error al actualizar: El nombre del negocio ya existe o el ID no es válido.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException nfe) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("ID del Negocio:"), idField,
                new Label("Nombre:"), nombreField,
                new Label("Descripción:"), descripcionField,
                new Label("Fecha de Inicio:"), fechaInicioPicker,
                new Label("Fecha de Fin:"), fechaFinPicker,
                updateButton
        );

        return layout;
    }


    private VBox listarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("ID del Negocio");
        Button searchButton = new Button("Buscar por ID");
        Button loadAllButton = new Button("Cargar Todos");
        TableView<NegocioFiduciario> table = new TableView<>();
        setupTable(table);

        searchButton.setOnAction(e -> {
            if (searchField.getText().trim().isEmpty()) {
                showAlert("Por favor ingrese un ID.", Alert.AlertType.WARNING);
                return;
            }
            int id = Integer.parseInt(searchField.getText().trim());
            loadNegocioById(table, id);
        });

        loadAllButton.setOnAction(e -> {
            try {
                ObservableList<NegocioFiduciario> negocios = FXCollections.observableArrayList(controller.findAllNegocios());
                if (negocios.isEmpty()) {
                    showAlert("No se encontraron negocios.", Alert.AlertType.INFORMATION);
                } else {
                    table.setItems(negocios);
                    showAlert(negocios.size() + " negocios cargados.", Alert.AlertType.INFORMATION);
                }
            } catch (Exception ex) {
                showAlert("Error al cargar los negocios: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        HBox searchBox = new HBox(10, searchField, searchButton, loadAllButton);
        layout.getChildren().addAll(searchBox, table);
        return layout;
    }

    private void setupTable(TableView<NegocioFiduciario> table) {
        TableColumn<NegocioFiduciario, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idNegocioFiduciario"));
        idColumn.setCellFactory(column -> createCenteredCell());

        TableColumn<NegocioFiduciario, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nameColumn.setCellFactory(column -> createCenteredCell());

        TableColumn<NegocioFiduciario, String> descColumn = new TableColumn<>("Descripción");
        descColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        descColumn.setCellFactory(column -> createCenteredCell());

        TableColumn<NegocioFiduciario, LocalDate> startColumn = new TableColumn<>("Fecha de Inicio");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        startColumn.setCellFactory(column -> createCenteredCell());

        TableColumn<NegocioFiduciario, LocalDate> endColumn = new TableColumn<>("Fecha de Fin");
        endColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        endColumn.setCellFactory(column -> createCenteredCell());

        table.getColumns().addAll(idColumn, nameColumn, descColumn, startColumn, endColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private <T> TableCell<NegocioFiduciario, T> createCenteredCell() {
        return new TableCell<NegocioFiduciario, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setGraphic(null);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        };
    }

    private void loadNegocioById(TableView<NegocioFiduciario> table, int id) {
        try {
            NegocioFiduciario negocio = controller.findNegocioById(id);
            if (negocio != null) {
                table.setItems(FXCollections.observableArrayList(negocio));
            } else {
                table.setItems(FXCollections.observableArrayList());
                showAlert("No se encontró el negocio con ID " + id, Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error al buscar el negocio: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox eliminarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField idField = new TextField();
        idField.setPromptText("ID del Negocio a eliminar");

        Button deleteButton = new Button("Eliminar Negocio");
        deleteButton.setOnAction(event -> {
            try {
                int id = Integer.parseInt(idField.getText());
                boolean isDeleted = controller.eliminarNegocio(id);
                if (isDeleted) {
                    showAlert("Negocio eliminado con éxito.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("No se encontró el negocio con ID " + id + ", o no se pudo eliminar.", Alert.AlertType.WARNING);
                }
            } catch (NumberFormatException e) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Error al eliminar el negocio: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Ingrese el ID del Negocio a eliminar:"),
                idField,
                deleteButton
        );
        return layout;
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

}
