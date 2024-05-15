package Bancolombia.view;

import Bancolombia.controller.PersonasParticipantesController;
import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import Bancolombia.model.PersonasParticipantes;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.List;

public class GeneradorExcelView {

    private PersonasParticipantesController personasController;

    public GeneradorExcelView(Connection connection) {
        this.personasController = new PersonasParticipantesController(connection);
    }

    public VBox getView() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label instructionLabel = new Label("Ingrese el número de documento de la persona:");
        TextField documentoField = new TextField();
        documentoField.setPromptText("Número de documento");

        // Asegurarse de que solo se acepten números
        documentoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                documentoField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Button generarButton = new Button("Generar Excel");
        generarButton.setOnAction(e -> {
            String numeroDocumento = documentoField.getText().trim();
            if (!numeroDocumento.isEmpty()) {
                generarArchivoExcel(numeroDocumento);
            } else {
                showAlert("Por favor ingrese un número de documento.", Alert.AlertType.WARNING);
            }
        });

        layout.getChildren().addAll(instructionLabel, documentoField, generarButton);
        return layout;
    }

    private void generarArchivoExcel(String numeroDocumento) {
        try {
            PersonasParticipantes persona = personasController.buscarPersonaParticipantePorDocumento(numeroDocumento);
            if (persona == null) {
                showAlert("No se encontró ninguna persona con el número de documento: " + numeroDocumento, Alert.AlertType.INFORMATION);
                return;
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Resumen");

            // Crear el encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Resumen de Persona");

            // Crear la fila de información de la persona
            Row personaRow = sheet.createRow(1);
            personaRow.createCell(0).setCellValue("Nombre Completo:");
            personaRow.createCell(1).setCellValue(persona.getNombre() + " " + persona.getApellido());

            personaRow.createCell(2).setCellValue("Número de Documento:");
            personaRow.createCell(3).setCellValue(persona.getNumeroDocumento());

            // Crear la fila de encabezado para negocios y obligaciones
            Row negociosHeaderRow = sheet.createRow(3);
            negociosHeaderRow.createCell(0).setCellValue("Negocios");
            negociosHeaderRow.createCell(1).setCellValue("Obligaciones");

            int rowIndex = 4;

            List<NegocioFiduciario> negocios = personasController.findNegociosByPersona(persona.getIdPersona());
            for (NegocioFiduciario negocio : negocios) {
                List<Obligacion> obligaciones = personasController.findObligacionesByNegocioId(negocio.getIdNegocioFiduciario());

                for (Obligacion obligacion : obligaciones) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(negocio.getNombre());
                    row.createCell(1).setCellValue(obligacion.getDescripcion() + " (Monto: " + obligacion.getMonto() + ")");
                }
            }

            // Guardar el archivo temporalmente
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx"));
            fileChooser.setInitialFileName("Resumen_" + persona.getNombre() + "_" + persona.getApellido() + ".xlsx");
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                // Encriptar el archivo con la contraseña
                encryptExcelFile(file, numeroDocumento);
                showAlert("Archivo Excel generado exitosamente.", Alert.AlertType.INFORMATION);
            }
            workbook.close();
        } catch (Exception e) {
            showAlert("Error al generar el archivo Excel: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void encryptExcelFile(File file, String password) throws Exception {
        // Leer el archivo existente
        File tempFile = new File(file.getParent(), "temp_" + file.getName());
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        // Crear un sistema de archivos POIFS
        POIFSFileSystem fs = new POIFSFileSystem();

        // Crear la información de encriptación
        EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
        Encryptor encryptor = info.getEncryptor();
        encryptor.confirmPassword(password);

        // Escribir el archivo de Excel en el sistema de archivos POIFS
        try (FileInputStream fis = new FileInputStream(tempFile);
             OPCPackage opc = OPCPackage.open(fis);
             OutputStream os = encryptor.getDataStream(fs)) {
            opc.save(os);
        }

        // Guardar el archivo encriptado
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fs.writeFilesystem(fos);
        }

        // Eliminar el archivo temporal
        tempFile.delete();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}






