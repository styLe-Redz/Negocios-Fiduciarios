package Bancolombia.controller;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.service.NegocioFiduciarioService;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class NegocioFiduciarioController {
    private NegocioFiduciarioService negocioFiduciarioService;

    public NegocioFiduciarioController(Connection connection) {
        this.negocioFiduciarioService = new NegocioFiduciarioService(connection);
    }

    public boolean crearNegocio(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                showAlert("El nombre del negocio fiduciario no puede estar vacío.", Alert.AlertType.ERROR);
                return false;
            }
            return negocioFiduciarioService.crearNegocio(nombre, descripcion, fechaInicio, fechaFin);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarNegocio(int id, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            NegocioFiduciario negocio = new NegocioFiduciario(id, nombre, descripcion, fechaInicio, fechaFin);
            return negocioFiduciarioService.actualizarNegocio(negocio);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public NegocioFiduciario findNegocioById(int id) throws SQLException {
        return negocioFiduciarioService.findById(id);
    }

    public List<NegocioFiduciario> findAllNegocios() throws SQLException {
        return negocioFiduciarioService.findAll();
    }

    public boolean eliminarNegocio(int id) {
        try {
            return negocioFiduciarioService.eliminarNegocio(id);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al eliminar el negocio: " + e.getMessage());
            return false;
        }
    }

    public NegocioFiduciario findNegocioByIdWithRelations(int id) throws SQLException {
        return negocioFiduciarioService.findByIdWithRelations(id);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }
}
