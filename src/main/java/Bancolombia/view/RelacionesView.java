package Bancolombia.view;

import Bancolombia.controller.NegocioFiduciarioController;
import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import Bancolombia.model.PersonasParticipantes;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.Connection;
import java.util.List;

public class RelacionesView {

    private NegocioFiduciarioController negocioController;
    private TableView<NegocioFiduciario> table;

    public RelacionesView(Connection connection) {
        this.negocioController = new NegocioFiduciarioController(connection);
    }

    public VBox getView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // Filtro con título
        ComboBox<NegocioFiduciario> negocioFilter = new ComboBox<>();
        negocioFilter.setPromptText("Filtrar por negocio");
        loadNegociosFiduciarios(negocioFilter);

        Button loadAllButton = new Button("Mostrar Todo");
        loadAllButton.setOnAction(event -> {
            loadAllData();
            negocioFilter.setValue(null); // Reset the ComboBox value
        });

        HBox filters = new HBox(10);
        VBox negocioBox = new VBox(5, negocioFilter, loadAllButton);
        filters.getChildren().addAll(negocioBox);
        layout.getChildren().add(filters);

        // Tabla
        table = new TableView<>();
        setupTable();

        layout.getChildren().add(table);

        // Handlers para el filtro
        negocioFilter.setOnAction(event -> filterTableByNegocio(negocioFilter));

        // Cargar todos los datos inicialmente
        loadAllData();

        return layout;
    }

    private void setupTable() {
        TableColumn<NegocioFiduciario, Integer> idColumn = new TableColumn<>("ID Negocio");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idNegocioFiduciario"));
        idColumn.setCellFactory(createIntegerCellFactory());

        TableColumn<NegocioFiduciario, String> nombreColumn = new TableColumn<>("Nombre Negocio");
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nombreColumn.setCellFactory(createStringCellFactory());

        TableColumn<NegocioFiduciario, String> descripcionColumn = new TableColumn<>("Descripción Negocio");
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        descripcionColumn.setCellFactory(createStringCellFactory());

        TableColumn<NegocioFiduciario, String> obligacionesColumn = new TableColumn<>("Obligaciones");
        obligacionesColumn.setCellValueFactory(cellData -> {
            List<Obligacion> obligaciones = cellData.getValue().getObligaciones();
            StringBuilder sb = new StringBuilder();
            for (Obligacion obligacion : obligaciones) {
                sb.append(obligacion.getDescripcion()).append(" (").append(obligacion.getIdObligacion()).append(")").append("\n");
            }
            return new SimpleStringProperty(sb.toString());
        });
        obligacionesColumn.setCellFactory(createStringCellFactory());

        TableColumn<NegocioFiduciario, String> personasColumn = new TableColumn<>("Personas Participantes");
        personasColumn.setCellValueFactory(cellData -> {
            List<PersonasParticipantes> personas = cellData.getValue().getParticipantes();
            StringBuilder sb = new StringBuilder();
            for (PersonasParticipantes persona : personas) {
                sb.append(persona.getNombre()).append(" ").append(persona.getApellido()).append(" (").append(persona.getIdPersona()).append(")").append("\n");
            }
            return new SimpleStringProperty(sb.toString());
        });
        personasColumn.setCellFactory(createStringCellFactory());

        table.getColumns().addAll(idColumn, nombreColumn, descripcionColumn, obligacionesColumn, personasColumn);
    }

    private void loadAllData() {
        try {
            List<NegocioFiduciario> negocios = negocioController.findAllNegocios();
            ObservableList<NegocioFiduciario> allData = FXCollections.observableArrayList();
            for (NegocioFiduciario negocio : negocios) {
                NegocioFiduciario negocioConRelaciones = negocioController.findNegocioByIdWithRelations(negocio.getIdNegocioFiduciario());
                allData.add(negocioConRelaciones);
            }
            table.setItems(allData);
        } catch (Exception e) {
            showAlert("Error al cargar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterTableByNegocio(ComboBox<NegocioFiduciario> negocioFilter) {
        NegocioFiduciario negocio = negocioFilter.getSelectionModel().getSelectedItem();
        if (negocio != null) {
            try {
                NegocioFiduciario negocioConRelaciones = negocioController.findNegocioByIdWithRelations(negocio.getIdNegocioFiduciario());
                table.setItems(FXCollections.observableArrayList(negocioConRelaciones));
            } catch (Exception e) {
                showAlert("Error al filtrar por negocio: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            loadAllData();
        }
    }

    private void loadNegociosFiduciarios(ComboBox<NegocioFiduciario> comboBox) {
        try {
            List<NegocioFiduciario> negocios = negocioController.findAllNegocios();
            comboBox.setItems(FXCollections.observableArrayList(negocios));
        } catch (Exception e) {
            showAlert("Error al cargar los negocios fiduciarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para crear un CellFactory para centrar celdas con contenido Integer
    private Callback<TableColumn<NegocioFiduciario, Integer>, TableCell<NegocioFiduciario, Integer>> createIntegerCellFactory() {
        return column -> new TableCell<NegocioFiduciario, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }

    // Método para crear un CellFactory para centrar celdas con contenido String
    private Callback<TableColumn<NegocioFiduciario, String>, TableCell<NegocioFiduciario, String>> createStringCellFactory() {
        return column -> new TableCell<NegocioFiduciario, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }
}





