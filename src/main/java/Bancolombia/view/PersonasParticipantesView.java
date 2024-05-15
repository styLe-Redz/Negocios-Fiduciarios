package Bancolombia.view;

import Bancolombia.controller.PersonasParticipantesController;
import Bancolombia.controller.NegocioFiduciarioController;
import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.PersonasParticipantes;
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
import java.util.List;

public class PersonasParticipantesView {
    private PersonasParticipantesController controller;
    private NegocioFiduciarioController negocioController;

    private ComboBox<PersonasParticipantes> comboBoxPersonaAsignar = new ComboBox<>();
    private ComboBox<PersonasParticipantes> comboBoxPersonasQuitar = new ComboBox<>();
    private ComboBox<PersonasParticipantes> comboBoxPersonasActualizar = new ComboBox<>();
    private ComboBox<NegocioFiduciario> comboBoxNegocioAsignar = new ComboBox<>();
    private ComboBox<NegocioFiduciario> comboBoxNegocioQuitar = new ComboBox<>();
    private ComboBox<NegocioFiduciario> comboBoxNegocioCrear = new ComboBox<>();
    private ComboBox<NegocioFiduciario> comboBoxNegocioActualizar = new ComboBox<>();

    public PersonasParticipantesView(Connection connection) {
        this.controller = new PersonasParticipantesController(connection);
        this.negocioController = new NegocioFiduciarioController(connection);
    }

    public TabPane getView() {
        TabPane tabPane = new TabPane();

        Tab tabCrear = new Tab("Crear Persona");
        tabCrear.setContent(crearTab());
        tabCrear.setClosable(false);

        Tab tabAsignar = new Tab("Asignar Persona a Negocio");
        tabAsignar.setContent(asignarTab());
        tabAsignar.setClosable(false);

        Tab tabQuitar = new Tab("Quitar Persona de Negocio");
        tabQuitar.setContent(quitarPersonaDeNegocioTab());
        tabQuitar.setClosable(false);

        Tab tabActualizar = new Tab("Actualizar Persona");
        tabActualizar.setContent(actualizarTab());
        tabActualizar.setClosable(false);

        Tab tabListar = new Tab("Listar Personas");
        tabListar.setContent(listarTab());
        tabListar.setClosable(false);

        Tab tabEliminar = new Tab("Eliminar Persona");
        tabEliminar.setContent(eliminarTab());
        tabEliminar.setClosable(false);

        tabPane.getTabs().addAll(tabCrear, tabAsignar, tabQuitar, tabActualizar, tabListar, tabEliminar);

        return tabPane;
    }

    private VBox crearTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");

        TextField apellidoField = new TextField();
        apellidoField.setPromptText("Apellido");

        ComboBox<String> tipoDocumentoComboBox = new ComboBox<>();
        tipoDocumentoComboBox.setPromptText("Tipo de Documento");
        tipoDocumentoComboBox.getItems().addAll("Cédula de Ciudadanía (CC)", "Tarjeta de Identidad (TI)", "Registro Civil (RC)", "Cédula de Extranjería (CE)");

        TextField numeroDocumentoField = new TextField();
        numeroDocumentoField.setPromptText("Número de Documento");

        comboBoxNegocioCrear.setPromptText("Seleccione un negocio fiduciario");
        loadNegociosFiduciarios(comboBoxNegocioCrear);

        Button submitButton = new Button("Crear Persona");
        submitButton.setOnAction(event -> {
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            String tipoDocumento = tipoDocumentoComboBox.getSelectionModel().getSelectedItem();
            String numeroDocumento = numeroDocumentoField.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || tipoDocumento == null || numeroDocumento.isEmpty()) {
                showAlert("Por favor complete todos los campos.", Alert.AlertType.ERROR);
                return;
            }

            NegocioFiduciario negocioSeleccionado = comboBoxNegocioCrear.getSelectionModel().getSelectedItem();
            if (negocioSeleccionado == null) {
                showAlert("Por favor seleccione un negocio fiduciario.", Alert.AlertType.ERROR);
                return;
            }
            if (!nombre.matches("[a-zA-Z]+") || !apellido.matches("[a-zA-Z]+")) {
                showAlert("El nombre y el apellido deben contener solo letras.", Alert.AlertType.ERROR);
                return;
            }

            if (!numeroDocumento.matches("\\d+")) {
                showAlert("El número de documento debe contener solo números.", Alert.AlertType.ERROR);
                return;
            }

            int idNegocio = negocioSeleccionado.getIdNegocioFiduciario();
            boolean creada = controller.agregarPersonaParticipante(nombre, apellido, tipoDocumento, numeroDocumento, idNegocio);
            if (creada) {
                showAlert("Persona creada con éxito y asignada al negocio " + negocioSeleccionado.getNombre() + ".", Alert.AlertType.INFORMATION);
                updateComboBoxes(); // Actualizar todos los ComboBox
            } else {
                showAlert("No se pudo crear la persona. Verifique los datos ingresados.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(new Label("Nombre:"), nombreField,
                new Label("Apellido:"), apellidoField,
                new Label("Tipo de Documento:"), tipoDocumentoComboBox,
                new Label("Número de Documento:"), numeroDocumentoField,
                new Label("Negocio Fiduciario:"), comboBoxNegocioCrear,
                submitButton);

        return layout;
    }

    private VBox asignarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        comboBoxPersonaAsignar.setPromptText("Seleccione una persona participante");
        loadPersonasParticipantes(comboBoxPersonaAsignar);

        comboBoxNegocioAsignar.setPromptText("Seleccione un negocio fiduciario");
        loadNegociosFiduciarios(comboBoxNegocioAsignar);

        Button asignarButton = new Button("Asignar Persona");
        asignarButton.setOnAction(event -> {
            PersonasParticipantes personaSeleccionada = comboBoxPersonaAsignar.getSelectionModel().getSelectedItem();
            NegocioFiduciario negocioSeleccionado = comboBoxNegocioAsignar.getSelectionModel().getSelectedItem();
            if (personaSeleccionada == null || negocioSeleccionado == null) {
                showAlert("Por favor seleccione una persona y un negocio fiduciario.", Alert.AlertType.ERROR);
                return;
            }
            boolean asignado = controller.asignarPersonaNegocio(personaSeleccionada.getIdPersona(), negocioSeleccionado.getIdNegocioFiduciario());
            if (asignado) {
                showAlert("Persona asignada con éxito.", Alert.AlertType.INFORMATION);
                updateComboBoxes(); // Actualizar todos los ComboBox
            } else {
                showAlert("No se pudo asignar la persona. Verifique los datos ingresados.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Persona Participante:"), comboBoxPersonaAsignar,
                new Label("Negocio Fiduciario:"), comboBoxNegocioAsignar,
                asignarButton
        );

        return layout;
    }

    private VBox quitarPersonaDeNegocioTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        comboBoxPersonasQuitar.setPromptText("Seleccione una persona");
        loadPersonasParticipantes(comboBoxPersonasQuitar);

        comboBoxNegocioQuitar.setPromptText("Seleccione un negocio fiduciario");

        comboBoxPersonasQuitar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadNegociosPorPersona(comboBoxNegocioQuitar, newValue.getIdPersona());
            } else {
                comboBoxNegocioQuitar.getItems().clear();
            }
        });

        Button quitarButton = new Button("Quitar Persona");
        quitarButton.setOnAction(event -> {
            PersonasParticipantes personaSeleccionada = comboBoxPersonasQuitar.getSelectionModel().getSelectedItem();
            NegocioFiduciario negocioSeleccionado = comboBoxNegocioQuitar.getSelectionModel().getSelectedItem();
            if (personaSeleccionada == null || negocioSeleccionado == null) {
                showAlert("Por favor seleccione una persona y un negocio fiduciario.", Alert.AlertType.ERROR);
                return;
            }
            boolean quitada = controller.quitarPersonaDeNegocio(personaSeleccionada.getIdPersona(), negocioSeleccionado.getIdNegocioFiduciario());
            if (quitada) {
                showAlert("Persona quitada del negocio con éxito.", Alert.AlertType.INFORMATION);
                updateComboBoxes(); // Actualizar todos los ComboBox
            } else {
                showAlert("No se pudo quitar la persona del negocio. Verifique los datos ingresados.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Persona Existente:"), comboBoxPersonasQuitar,
                new Label("Negocio Fiduciario:"), comboBoxNegocioQuitar,
                quitarButton
        );

        return layout;
    }

    private void loadNegociosPorPersona(ComboBox<NegocioFiduciario> comboBox, int idPersona) {
        try {
            List<NegocioFiduciario> negocios = controller.findNegociosByPersona(idPersona);
            comboBox.setItems(FXCollections.observableArrayList(negocios));
        } catch (SQLException e) {
            showAlert("Error al cargar los negocios fiduciarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox actualizarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        comboBoxPersonasActualizar.setPromptText("Seleccione una persona");
        loadPersonasParticipantes(comboBoxPersonasActualizar);

        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");
        TextField apellidoField = new TextField();
        apellidoField.setPromptText("Apellido");

        ComboBox<String> tipoDocumentoComboBox = new ComboBox<>();
        tipoDocumentoComboBox.setPromptText("Tipo de Documento");
        tipoDocumentoComboBox.setItems(FXCollections.observableArrayList("Cédula de Ciudadanía (CC)", "Tarjeta de Identidad (TI)", "Registro Civil (RC)", "Cédula de Extranjería (CE)"));

        TextField numeroDocumentoField = new TextField();
        numeroDocumentoField.setPromptText("Número de Documento");

        Button buscarButton = new Button("Buscar Persona");
        buscarButton.setOnAction(event -> {
            try {
                int idPersona = comboBoxPersonasActualizar.getSelectionModel().getSelectedItem().getIdPersona();
                PersonasParticipantes persona = controller.buscarPersonaParticipantePorId(idPersona);
                if (persona != null) {
                    nombreField.setText(persona.getNombre());
                    apellidoField.setText(persona.getApellido());
                    tipoDocumentoComboBox.setValue(persona.getTipoDocumento());
                    numeroDocumentoField.setText(persona.getNumeroDocumento());
                } else {
                    showAlert("No se encontró la persona con el ID proporcionado.", Alert.AlertType.INFORMATION);
                }
            } catch (NumberFormatException e) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            }
        });

        Button actualizarButton = new Button("Actualizar Persona");
        actualizarButton.setOnAction(event -> {
            try {
                int idPersona = comboBoxPersonasActualizar.getSelectionModel().getSelectedItem().getIdPersona();
                String nombre = nombreField.getText().trim();
                String apellido = apellidoField.getText().trim();
                String tipoDocumento = tipoDocumentoComboBox.getSelectionModel().getSelectedItem();
                String numeroDocumento = numeroDocumentoField.getText().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || tipoDocumento == null || numeroDocumento.isEmpty()) {
                    showAlert("Por favor complete todos los campos.", Alert.AlertType.ERROR);
                    return;
                }

                if (!nombre.matches("[a-zA-Z]+") || !apellido.matches("[a-zA-Z]+")) {
                    showAlert("El nombre y el apellido deben contener solo letras.", Alert.AlertType.ERROR);
                    return;
                }

                if (!numeroDocumento.matches("\\d+")) {
                    showAlert("El número de documento debe contener solo números.", Alert.AlertType.ERROR);
                    return;
                }

                if (controller.existeNumeroDocumento(numeroDocumento)) {
                    showAlert("El número de documento ya existe. Por favor, ingrese un número de documento único.", Alert.AlertType.ERROR);
                    return;
                }

                PersonasParticipantes persona = new PersonasParticipantes(idPersona, nombre, apellido, tipoDocumento, numeroDocumento);
                boolean actualizado = controller.actualizarPersonaParticipante(persona);
                if (actualizado) {
                    showAlert("Persona actualizada con éxito.", Alert.AlertType.INFORMATION);
                    updateComboBoxes(); // Actualizar todos los ComboBox
                } else {
                    showAlert("No se pudo actualizar la persona. Verifique los datos ingresados.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(new Label("ID de la Persona:"), comboBoxPersonasActualizar,
                new Label("Nombre:"), nombreField,
                new Label("Apellido:"), apellidoField,
                new Label("Tipo de Documento:"), tipoDocumentoComboBox,
                new Label("Número de Documento:"), numeroDocumentoField,
                buscarButton,
                actualizarButton);
        return layout;
    }

    private VBox listarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("ID de la Persona");
        Button searchButton = new Button("Buscar por ID");
        Button loadAllButton = new Button("Cargar Todas");
        TableView<PersonasParticipantes> table = new TableView<>();
        setupTable(table);

        searchButton.setOnAction(e -> {
            String idText = searchField.getText().trim();
            if (idText.isEmpty()) {
                showAlert("Por favor ingrese un ID para buscar.", Alert.AlertType.WARNING);
                return;
            }
            try {
                int id = Integer.parseInt(idText);
                loadPersonaById(table, id);
            } catch (NumberFormatException ex) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            }
        });

        loadAllButton.setOnAction(e -> {
            try {
                ObservableList<PersonasParticipantes> personas = FXCollections.observableArrayList(controller.findAllPersonasParticipantes());
                if (personas.isEmpty()) {
                    showAlert("No se encontraron personas.", Alert.AlertType.INFORMATION);
                } else {
                    table.setItems(personas);
                    showAlert(personas.size() + " personas cargadas.", Alert.AlertType.INFORMATION);
                }
            } catch (Exception ex) {
                showAlert("Error al cargar las personas: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        HBox searchBox = new HBox(10, searchField, searchButton, loadAllButton);
        layout.getChildren().addAll(searchBox, table);
        return layout;
    }

    private void loadPersonaById(TableView<PersonasParticipantes> table, int id) {
        PersonasParticipantes persona = controller.buscarPersonaParticipantePorId(id);
        if (persona != null) {
            table.setItems(FXCollections.observableArrayList(persona));
        } else {
            table.setItems(FXCollections.observableArrayList());
            showAlert("No se encontró la persona con ID " + id, Alert.AlertType.INFORMATION);
        }
    }

    private void setupTable(TableView<PersonasParticipantes> table) {
        TableColumn<PersonasParticipantes, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<PersonasParticipantes, String> nombreColumn = new TableColumn<>("Nombre");
        TableColumn<PersonasParticipantes, String> apellidoColumn = new TableColumn<>("Apellido");
        TableColumn<PersonasParticipantes, String> tipoDocumentoColumn = new TableColumn<>("Tipo de Documento");
        TableColumn<PersonasParticipantes, String> numeroDocumentoColumn = new TableColumn<>("Número de Documento");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("idPersona"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidoColumn.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        tipoDocumentoColumn.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));
        numeroDocumentoColumn.setCellValueFactory(new PropertyValueFactory<>("numeroDocumento"));

        table.getColumns().addAll(idColumn, nombreColumn, apellidoColumn, tipoDocumentoColumn, numeroDocumentoColumn);

        // Ajustar el ancho de las columnas para ocupar todo el espacio disponible uniformemente
        double columnWidth = 1.0 / table.getColumns().size();
        table.getColumns().forEach(column -> column.prefWidthProperty().bind(table.widthProperty().multiply(columnWidth)));

        // Centrar el contenido de las columnas
        Callback<TableColumn<PersonasParticipantes, Integer>, TableCell<PersonasParticipantes, Integer>> integerCellFactory =
                column -> {
                    TableCell<PersonasParticipantes, Integer> cell = new TableCell<PersonasParticipantes, Integer>() {
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
                    return cell;
                };

        Callback<TableColumn<PersonasParticipantes, String>, TableCell<PersonasParticipantes, String>> stringCellFactory =
                column -> {
                    TableCell<PersonasParticipantes, String> cell = new TableCell<PersonasParticipantes, String>() {
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
                    return cell;
                };

        idColumn.setCellFactory(integerCellFactory);
        nombreColumn.setCellFactory(stringCellFactory);
        apellidoColumn.setCellFactory(stringCellFactory);
        tipoDocumentoColumn.setCellFactory(stringCellFactory);
        numeroDocumentoColumn.setCellFactory(stringCellFactory);
    }

    private VBox eliminarTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField idField = new TextField();
        idField.setPromptText("ID de la Persona a eliminar");

        Button deleteButton = new Button("Eliminar Persona");
        deleteButton.setOnAction(event -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                boolean isDeleted = controller.eliminarPersonaParticipante(id);
                if (isDeleted) {
                    showAlert("Persona eliminada con éxito.", Alert.AlertType.INFORMATION);
                    updateComboBoxes(); // Actualizar todos los ComboBox
                } else {
                    showAlert("No se encontró la persona con ID " + id + ", o no se pudo eliminar.", Alert.AlertType.WARNING);
                }
            } catch (NumberFormatException e) {
                showAlert("Por favor ingrese un ID válido.", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(
                new Label("Ingrese el ID de la Persona a eliminar:"),
                idField,
                deleteButton
        );

        return layout;
    }

    private void loadPersonasParticipantes(ComboBox<PersonasParticipantes> comboBox) {
        try {
            List<PersonasParticipantes> personas = controller.findAllPersonasParticipantes();
            ObservableList<PersonasParticipantes> observableList = FXCollections.observableArrayList(personas);
            comboBox.setItems(observableList);
        } catch (Exception e) {
            showAlert("Error al cargar las personas participantes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadNegociosFiduciarios(ComboBox<NegocioFiduciario> comboBox) {
        try {
            List<NegocioFiduciario> negocios = negocioController.findAllNegocios();
            ObservableList<NegocioFiduciario> observableList = FXCollections.observableArrayList(negocios);
            comboBox.setItems(observableList);
        } catch (SQLException e) {
            showAlert("Error al cargar los negocios fiduciarios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateComboBoxes() {
        loadPersonasParticipantes(comboBoxPersonaAsignar);
        loadPersonasParticipantes(comboBoxPersonasQuitar);
        loadPersonasParticipantes(comboBoxPersonasActualizar);
        loadNegociosFiduciarios(comboBoxNegocioAsignar);
        loadNegociosFiduciarios(comboBoxNegocioQuitar);
        loadNegociosFiduciarios(comboBoxNegocioCrear);
        loadNegociosFiduciarios(comboBoxNegocioActualizar);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }
}


