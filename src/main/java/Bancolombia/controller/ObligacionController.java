package Bancolombia.controller;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import Bancolombia.model.dao.NegocioFiduciarioDAO;
import Bancolombia.model.dao.ObligacionDAO;
import Bancolombia.model.service.ObligacionService;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ObligacionController {
    private ObligacionService obligacionService;

    public ObligacionController(Connection connection) {
        NegocioFiduciarioDAO negocioFiduciarioDAO = new NegocioFiduciarioDAO(connection);
        ObligacionDAO obligacionDAO = new ObligacionDAO(connection, negocioFiduciarioDAO);
        this.obligacionService = new ObligacionService(obligacionDAO, negocioFiduciarioDAO);
    }

    public boolean crearObligacion(String descripcion, BigDecimal monto, LocalDate fechaVencimiento, int idNegocio) {
        try {
            boolean resultado = obligacionService.agregarObligacion(descripcion, monto, fechaVencimiento, idNegocio);
            if (!resultado) {
                showAlert("No se pudo crear la obligación. Verifique los datos ingresados.", Alert.AlertType.ERROR);
            }
            return resultado;
        } catch (IllegalArgumentException e) {
            // Mostrar mensaje de error al usuario
            showAlert("Error al crear la obligación: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public boolean asignarObligacionNegocio(int idObligacion, int idNegocioFiduciario) {
        try {
            return obligacionService.asignarObligacionNegocio(idObligacion, idNegocioFiduciario);
        } catch (IllegalArgumentException e) {
            showAlert("Error al asignar la obligación: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        } catch (SQLException e) {
            showAlert("Error al asignar la obligación: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }



    public Obligacion buscarObligacionPorId(int id) {
        try {
            return obligacionService.buscarObligacionPorId(id);
        } catch (SQLException e) {
            showAlert("Error al buscar la obligación: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    public int getIdNegocioFiduciario(int idObligacion) {
        try {
            return obligacionService.getIdNegocioFiduciario(idObligacion);
        } catch (SQLException e) {
            showAlert("Error al obtener el ID del negocio fiduciario: " + e.getMessage(), Alert.AlertType.ERROR);
            return -1;
        }
    }

    public boolean actualizarObligacion(int id, String descripcion, BigDecimal monto, LocalDate fechaVencimiento, int idNegocio) {
        try {
            return obligacionService.actualizarObligacion(id, descripcion, monto, fechaVencimiento, idNegocio);
        } catch (IllegalArgumentException e) {
            showAlert("Error al actualizar la obligación: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public List<Obligacion> findAllObligaciones() {
        try {
            return obligacionService.findAllObligaciones();
        } catch (SQLException e) {
            showAlert("Error al cargar las obligaciones: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    public boolean eliminarObligacion(int id) {
        try {
            return obligacionService.eliminarObligacion(id);
        } catch (SQLException e) {
            showAlert("Error al eliminar la obligación: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public boolean quitarObligacionDeNegocio(int idObligacion, int idNegocioFiduciario) {
        try {
            return obligacionService.quitarObligacionDeNegocio(idObligacion, idNegocioFiduciario);
        } catch (SQLException e) {
            showAlert("Error al quitar la obligación del negocio: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public List<NegocioFiduciario> findNegociosByObligacion(int idObligacion) {
        try {
            return obligacionService.findNegociosByObligacion(idObligacion);
        } catch (SQLException e) {
            showAlert("Error al buscar los negocios por obligación: " + e.getMessage(), Alert.AlertType.ERROR);
            return new ArrayList<>();
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }
}





