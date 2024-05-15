package Bancolombia.view;

import Bancolombia.controller.NegocioFiduciarioController;
import Bancolombia.controller.ObligacionController;
import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public class ObligacionView {

    private ObligacionController controller;
    private NegocioFiduciarioController negocioController;

    private ComboBox<NegocioFiduciario> comboBoxNegocioFiduciarioCrear = new ComboBox<>();
    private ComboBox<NegocioFiduciario> comboBoxNegociosAsignar = new ComboBox<>();
    private ComboBox<NegocioFiduciario> comboBoxNegociosQuitar = new ComboBox<>();
    private ComboBox<NegocioFiduciario> comboBoxNegociosActualizar = new ComboBox<>();
    private ComboBox<Obligacion> comboBoxObligacionAsignar = new ComboBox<>();
    private ComboBox<Obligacion> comboBoxObligacionExistenteQuitar = new ComboBox<>();
    private ComboBox<Obligacion> comboBoxObligacionExistenteActualizar = new ComboBox<>();

    public ObligacionView(Connection connection) {
        this.controller = new ObligacionController(connection);
        this.negocioController = new NegocioFiduciarioController(connection);
    }

    public TabPane getView() {
        TabPane tabPane = new TabPane();

        Tab tabCrear = new Tab("Crear Obligación");
        tabCrear.setContent(crearTab());
        tabCrear.setClosable(false);

        Tab tabAsignar = new Tab("Asignar Obligación Existente");
        tabAsignar.setContent(asignarTab());
        tabAsignar.setClosable(false);

        Tab tabQuitar = new Tab("Quitar Obligación de Negocio");
        tabQuitar.setContent(quitarObligacionDeNegocioTab());
        tabQuitar.setClosable(false);

        Tab tabActualizar = new Tab("Actualizar Obligación");
        tabActualizar.setContent(actualizarTab());
        tabActualizar.setClosable(false);

        Tab tabListar = new Tab("Listar Obligaciones");
        tabListar.setContent(listarTab());
        tabListar.setClosable(false);

        Tab tabEliminar = new Tab("Eliminar Obligación");
        tabEliminar.setContent(eliminarTab());
        tabEliminar.setClosable(false);

        tabPane.getTabs().addAll(tabCrear, tabAsignar, tabQuitar, tabActualizar, tabListar, tabEliminar);

        return tabPane;
    }

    private VBox crearTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField descripcionField = new TextField();
        descripcionField.setPromptText("Descripción de la obligación");
        TextField montoField = new TextField();
        montoField.setPromptText("Monto");
        DatePicker fechaVencimientoPicker = new DatePicker();

        comboBoxNegocioFiduciarioCrear.setPromptText("Seleccione un negocio fiduciario");
        loadNegociosFiduciarios(comboBoxNegocioFiduciarioCrear);

        Button submitButton = new Button("Crear Obligación");
        submitButton.setOnAction(event -> {
            try {
                BigDecimal monto = new BigDecimal(montoField.getText().trim());
                LocalDate fechaVencimiento = fechaVencimientoPicker.getValue();
                NegocioFiduciario negocioSeleccionado = comboBoxNegocioFiduciarioCrear.getSelectionModel().getSelectedItem();
                if (negocioSeleccionado == null) {
                    showAlert("Por favor seleccione un negocio fiduciario.", Alert.AlertType.ERROR);
                    return;
                }
                int idNegocio = negocioSeleccionado.getIdNegocioFiduciario();
                boolean creada = controller.crearObligacion(descripcionField.getText(), monto, fechaVencimiento, idNegocio);
                if (creada) {
                    showAlert("Obligación creada con éxito.", Alert.AlertType.INFORMATION);
                    updateComboBoxes(); // Actualizar todos los ComboBox
                } else {
                    showAlert("No se pudo crear la obligación. Verifique los datos ingresados.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Por favor ingrese un monto válido.", Alert.AlertType.ERROR);
            } catch (NullPointerException e) {
                showAlert("Por favor seleccione un negocio fiduciario.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(new Label("Descripción:"), descripcionField,
                new Label("Monto:"), montoField,
                new Label("Fecha de Vencimiento:"), fechaVencimientoPicker,
                new Label("Negocio Fiduciario:"), comboBoxNegocioFiduciarioCrear,
                submitButton);
        return layout;
    }

    private VBox asignarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        comboBoxObligacionAsignar.setPromptText("Seleccione una obligación existente");
        loadObligaciones(comboBoxObligacionAsignar);

        comboBoxNegociosAsignar.setPromptText("Seleccione un negocio fiduciario");
        loadNegociosFiduciarios(comboBoxNegociosAsignar);

        Button asignarButton = new Button("Asignar Obligación");
        asignarButton.setOnAction(event -> {
            Obligacion obligacionSeleccionada = comboBoxObligacionAsignar.getSelectionModel().getSelectedItem();
            NegocioFiduciario negocioSeleccionado = comboBoxNegociosAsignar.getSelectionModel().getSelectedItem();
            if (obligacionSeleccionada == null || negocioSeleccionado == null) {
                showAlert("Por favor seleccione una obligación y un negocio fiduciario.", Alert.AlertType.ERROR);
                return;
            }
            boolean asignado = controller.asignarObligacionNegocio(obligacionSeleccionada.getIdObligacion(), negocioSeleccionado.getIdNegocioFiduciario());
            if (asignado) {
                showAlert("Obligación asignada con éxito.", Alert.AlertType.INFORMATION);
                updateComboBoxes(); // Actualizar todos los ComboBox
            } else {
                showAlert("No se pudo asignar la obligación. Verifique los datos ingresados.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Obligación Existente:"), comboBoxObligacionAsignar,
                new Label("Negocio Fiduciario:"), comboBoxNegociosAsignar,
                asignarButton
        );

        return layout;
    }

    private VBox quitarObligacionDeNegocioTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        comboBoxObligacionExistenteQuitar.setPromptText("Seleccione una obligación existente");
        loadObligaciones(comboBoxObligacionExistenteQuitar);

        comboBoxNegociosQuitar.setPromptText("Seleccione un negocio fiduciario");

        comboBoxObligacionExistenteQuitar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadNegociosFiduciariosByObligacion(comboBoxNegociosQuitar, newValue.getIdObligacion());
            } else {
                comboBoxNegociosQuitar.getItems().clear();
            }
        });

        Button quitarButton = new Button("Quitar Obligación");
        quitarButton.setOnAction(event -> {
            Obligacion obligacionSeleccionada = comboBoxObligacionExistenteQuitar.getSelectionModel().getSelectedItem();
            NegocioFiduciario negocioSeleccionado = comboBoxNegociosQuitar.getSelectionModel().getSelectedItem();
            if (obligacionSeleccionada == null || negocioSeleccionado == null) {
                showAlert("Por favor seleccione una obligación y un negocio fiduciario.", Alert.AlertType.ERROR);
                return;
            }
            boolean quitada = controller.quitarObligacionDeNegocio(obligacionSeleccionada.getIdObligacion(), negocioSeleccionado.getIdNegocioFiduciario());
            if (quitada) {
                showAlert("Obligación quitada del negocio con éxito.", Alert.AlertType.INFORMATION);
                updateComboBoxes(); // Actualizar todos los ComboBox
            } else {
                showAlert("No se pudo quitar la obligación del negocio. Verifique los datos ingresados.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Obligación Existente:"), comboBoxObligacionExistenteQuitar,
                new Label("Negocio Fiduciario:"), comboBoxNegociosQuitar,
                quitarButton
        );

        return layout;
    }

    private VBox actualizarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        comboBoxObligacionExistenteActualizar.setPromptText("Seleccione una obligación");
        loadObligaciones(comboBoxObligacionExistenteActualizar);

        TextField descripcionField = new TextField();
        descripcionField.setPromptText("Descripción");
        TextField montoField = new TextField();
        montoField.setPromptText("Monto");
        DatePicker fechaVencimientoPicker = new DatePicker();

        comboBoxNegociosActualizar.setPromptText("Seleccione un negocio fiduciario");
        loadNegociosFiduciarios(comboBoxNegociosActualizar);

        Button buscarButton = new Button("Buscar Obligación");
        buscarButton.setOnAction(event -> {
            Obligacion obligacion = comboBoxObligacionExistenteActualizar.getSelectionModel().getSelectedItem();
            if (obligacion != null) {
                descripcionField.setText(obligacion.getDescripcion());
                montoField.setText(obligacion.getMonto().toString());
                LocalDate fechaVencimiento = Instant.ofEpochMilli(obligacion.getFechaVencimiento().getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                fechaVencimientoPicker.setValue(fechaVencimiento);
                try {
                    NegocioFiduciario negocio = negocioController.findNegocioById(controller.getIdNegocioFiduciario(obligacion.getIdObligacion()));
                    comboBoxNegociosActualizar.getSelectionModel().select(negocio);
                } catch (SQLException e) {
                    showAlert("Error al obtener el negocio fiduciario: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("No se encontró la obligación con el ID proporcionado.", Alert.AlertType.INFORMATION);
            }
        });

        Button actualizarButton = new Button("Actualizar Obligación");
        actualizarButton.setOnAction(event -> {
            try {
                int idObligacion = comboBoxObligacionExistenteActualizar.getSelectionModel().getSelectedItem().getIdObligacion();
                BigDecimal monto = new BigDecimal(montoField.getText().trim());
                LocalDate fechaVencimiento = fechaVencimientoPicker.getValue();
                NegocioFiduciario negocioSeleccionado = comboBoxNegociosActualizar.getSelectionModel().getSelectedItem();
                if (negocioSeleccionado == null) {
                    showAlert("Por favor seleccione un negocio fiduciario.", Alert.AlertType.ERROR);
                    return;
                }
                int idNegocio = negocioSeleccionado.getIdNegocioFiduciario();
                boolean actualizado = controller.actualizarObligacion(idObligacion, descripcionField.getText(), monto, fechaVencimiento, idNegocio);
                if (actualizado) {
                    showAlert("Obligación actualizada con éxito.", Alert.AlertType.INFORMATION);
                    updateComboBoxes(); // Actualizar todos los ComboBox
                } else {
                    showAlert("No se pudo actualizar la obligación. Verifique los datos ingresados.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Por favor ingrese un monto válido.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(new Label("Obligación:"), comboBoxObligacionExistenteActualizar,
                new Label("Descripción:"), descripcionField,
                new Label("Monto:"), montoField,
                new Label("Fecha de Vencimiento:"), fechaVencimientoPicker,
                new Label("Negocio Fiduciario:"), comboBoxNegociosActualizar,
                buscarButton,
                actualizarButton);
        return layout;
    }

    private VBox listarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("ID de la Obligación");
        Button searchButton = new Button("Buscar por ID");
        Button loadAllButton = new Button("Cargar Todas");
        TableView<Obligacion> table = new TableView<>();
        setupTable(table);

        searchButton.setOnAction(e -> {
            String idText = searchField.getText().trim();
            if (idText.isEmpty()) {
                showAlert("Por favor ingrese un ID para buscar.", Alert.AlertType.WARNING);
                return;
            }
            try {
                int id = Integer.parseInt(idText);
                loadObligacionById(table, id);
            } catch (NumberFormatException ex) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            }
        });

        loadAllButton.setOnAction(e -> {
            try {
                ObservableList<Obligacion> obligaciones = FXCollections.observableArrayList(controller.findAllObligaciones());
                if (obligaciones.isEmpty()) {
                    showAlert("No se encontraron obligaciones.", Alert.AlertType.INFORMATION);
                } else {
                    table.setItems(obligaciones);
                    showAlert(obligaciones.size() + " obligaciones cargadas.", Alert.AlertType.INFORMATION);
                }
            } catch (Exception ex) {
                showAlert("Error al cargar las obligaciones: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        HBox searchBox = new HBox(10, searchField, searchButton, loadAllButton);
        layout.getChildren().addAll(searchBox, table);
        return layout;
    }

    private void setupTable(TableView<Obligacion> table) {
        TableColumn<Obligacion, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Obligacion, String> descripcionColumn = new TableColumn<>("Descripción");
        TableColumn<Obligacion, BigDecimal> montoColumn = new TableColumn<>("Monto");
        TableColumn<Obligacion, java.util.Date> fechaVencimientoColumn = new TableColumn<>("Fecha de Vencimiento");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idObligacion"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        montoColumn.setCellValueFactory(new PropertyValueFactory<>("monto"));
        fechaVencimientoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));

        table.getColumns().addAll(idColumn, descripcionColumn, montoColumn, fechaVencimientoColumn);

        double columnWidth = 1.0 / table.getColumns().size();
        table.getColumns().forEach(column -> column.prefWidthProperty().bind(table.widthProperty().multiply(columnWidth)));

        Callback<TableColumn<Obligacion, Integer>, TableCell<Obligacion, Integer>> integerCellFactory =
                column -> new TableCell<Obligacion, Integer>() {
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

        Callback<TableColumn<Obligacion, String>, TableCell<Obligacion, String>> stringCellFactory =
                column -> new TableCell<Obligacion, String>() {
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

        Callback<TableColumn<Obligacion, BigDecimal>, TableCell<Obligacion, BigDecimal>> bigDecimalCellFactory =
                column -> new TableCell<Obligacion, BigDecimal>() {
                    @Override
                    protected void updateItem(BigDecimal item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            setAlignment(Pos.CENTER);
                        }
                    }
                };

        Callback<TableColumn<Obligacion, java.util.Date>, TableCell<Obligacion, java.util.Date>> dateCellFactory =
                column -> new TableCell<Obligacion, java.util.Date>() {
                    @Override
                    protected void updateItem(java.util.Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                            setAlignment(Pos.CENTER);
                        }
                    }
                };

        idColumn.setCellFactory(integerCellFactory);
        descripcionColumn.setCellFactory(stringCellFactory);
        montoColumn.setCellFactory(bigDecimalCellFactory);
        fechaVencimientoColumn.setCellFactory(dateCellFactory);
    }

    private void loadObligacionById(TableView<Obligacion> table, int id) {
        Obligacion obligacion = controller.buscarObligacionPorId(id);
        if (obligacion != null) {
            table.setItems(FXCollections.observableArrayList(obligacion));
        } else {
            table.setItems(FXCollections.observableArrayList());
            showAlert("No se encontró la obligación con ID " + id, Alert.AlertType.INFORMATION);
        }
    }

    private VBox eliminarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField idField = new TextField();
        idField.setPromptText("ID de la Obligación a eliminar");

        Button deleteButton = new Button("Eliminar Obligación");
        deleteButton.setOnAction(event -> {
            try {
                int id = Integer.parseInt(idField.getText());
                boolean isDeleted = controller.eliminarObligacion(id);
                if (isDeleted) {
                    showAlert("Obligación eliminada con éxito.", Alert.AlertType.INFORMATION);
                    updateComboBoxes(); // Actualizar todos los ComboBox
                } else {
                    showAlert("No se encontró la obligación con ID " + id + ", o no se pudo eliminar.", Alert.AlertType.WARNING);
                }
            } catch (NumberFormatException e) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Ingrese el ID de la Obligación a eliminar:"),
                idField,
                deleteButton
        );

        return layout;
    }

    private void loadObligaciones(ComboBox<Obligacion> comboBox) {
        List<Obligacion> obligaciones = controller.findAllObligaciones();
        if (obligaciones.isEmpty()) {
            System.out.println("No se encontraron obligaciones.");
        } else {
            System.out.println("Obligaciones cargadas: " + obligaciones.size());
            for (Obligacion obligacion : obligaciones) {
                System.out.println("Obligación: " + obligacion.getDescripcion() + " (ID: " + obligacion.getIdObligacion() + ")");
            }
        }
        ObservableList<Obligacion> observableList = FXCollections.observableArrayList(obligaciones);
        comboBox.setItems(observableList);
    }

    private void loadNegociosFiduciarios(ComboBox<NegocioFiduciario> comboBox) {
        try {
            List<NegocioFiduciario> negocios = negocioController.findAllNegocios();
            if (negocios.isEmpty()) {
                System.out.println("No se encontraron negocios fiduciarios.");
            } else {
                System.out.println("Negocios fiduciarios cargados: " + negocios.size());
                for (NegocioFiduciario negocio : negocios) {
                    System.out.println("Negocio: " + negocio.getNombre() + " (ID: " + negocio.getIdNegocioFiduciario() + ")");
                }
            }
            comboBox.setItems(FXCollections.observableArrayList(negocios));
        } catch (SQLException e) {
            System.out.println("Error al cargar los negocios fiduciarios: " + e.getMessage());
            showAlert("Error al cargar los negocios fiduciarios.", Alert.AlertType.ERROR);
        }
    }

    private void loadNegociosFiduciariosByObligacion(ComboBox<NegocioFiduciario> comboBox, int idObligacion) {
        List<NegocioFiduciario> negocios = controller.findNegociosByObligacion(idObligacion);
        comboBox.setItems(FXCollections.observableArrayList(negocios));
    }

    private void updateComboBoxes() {
        loadObligaciones(comboBoxObligacionAsignar);
        loadObligaciones(comboBoxObligacionExistenteQuitar);
        loadObligaciones(comboBoxObligacionExistenteActualizar);
        loadNegociosFiduciarios(comboBoxNegocioFiduciarioCrear);
        loadNegociosFiduciarios(comboBoxNegociosAsignar);
        loadNegociosFiduciarios(comboBoxNegociosQuitar);
        loadNegociosFiduciarios(comboBoxNegociosActualizar);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }
}








