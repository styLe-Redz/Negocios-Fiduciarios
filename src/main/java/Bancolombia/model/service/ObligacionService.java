package Bancolombia.model.service;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import Bancolombia.model.dao.ObligacionDAO;
import Bancolombia.model.dao.NegocioFiduciarioDAO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ObligacionService {
    private ObligacionDAO obligacionDAO;
    private NegocioFiduciarioDAO negocioFiduciarioDAO;

    public ObligacionService(ObligacionDAO obligacionDAO, NegocioFiduciarioDAO negocioFiduciarioDAO) {
        this.obligacionDAO = obligacionDAO;
        this.negocioFiduciarioDAO = negocioFiduciarioDAO;
    }

    // ObligacionService.java
    public boolean agregarObligacion(String descripcion, BigDecimal monto, LocalDate fechaVencimiento, int idNegocio) {
        try {
            java.sql.Date fechaObligacionSql = java.sql.Date.valueOf(fechaVencimiento); // Conversión correcta a java.sql.Date
            // Comprobación de fechas
            if (!negocioFiduciarioDAO.validarFechasNegocio(idNegocio, fechaObligacionSql)) {
                throw new IllegalArgumentException("La fecha de la obligación no está dentro del rango de fechas del negocio fiduciario.");
            }
            // Continúa con la creación si las fechas son válidas
            Obligacion obligacion = new Obligacion(0, descripcion, monto, fechaObligacionSql);
            return obligacionDAO.insert(obligacion, idNegocio);
        } catch (SQLException e) {
            System.err.println("Error al crear la obligación: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarObligacionNegocio(int idObligacion, int idNegocioFiduciario) throws SQLException {
        if (!negocioFiduciarioDAO.existeNegocio(idNegocioFiduciario) || !obligacionDAO.existeObligacion(idObligacion)) {
            System.out.println("Verificación fallida de negocio u obligación.");
            return false;
        }

        // Obtener la obligación por ID
        Obligacion obligacion = obligacionDAO.findById(idObligacion);
        if (obligacion == null) {
            System.out.println("No se encontró la obligación.");
            return false;
        }

        // Convertir java.util.Date a java.sql.Date para la validación
        java.sql.Date fechaObligacionSql = new java.sql.Date(obligacion.getFechaVencimiento().getTime());

        // Validar las fechas
        if (!negocioFiduciarioDAO.validarFechasNegocio(idNegocioFiduciario, fechaObligacionSql)) {
            throw new IllegalArgumentException("La fecha de la obligación no está dentro del rango de fechas del negocio fiduciario.");
        }

        obligacionDAO.insertNegocioFiduciarioObligacion(idNegocioFiduciario, idObligacion);
        return true;
    }

    public Obligacion buscarObligacionPorId(int id) throws SQLException {
        return obligacionDAO.findById(id);
    }

    public int getIdNegocioFiduciario(int idObligacion) throws SQLException {
        return obligacionDAO.findIdNegocioFiduciario(idObligacion);
    }

    public boolean actualizarObligacion(int id, String descripcion, BigDecimal monto, LocalDate fechaVencimiento, int idNegocio) {
        try {
            java.sql.Date fechaObligacionSql = java.sql.Date.valueOf(fechaVencimiento);
            if (!negocioFiduciarioDAO.validarFechasNegocio(idNegocio, fechaObligacionSql)) {
                throw new IllegalArgumentException("La fecha de la obligación no está dentro del rango de fechas del negocio fiduciario.");
            }
            Obligacion obligacion = new Obligacion(id, descripcion, monto, fechaObligacionSql);
            return obligacionDAO.update(obligacion, idNegocio);
        } catch (SQLException e) {
            System.err.println("Error al actualizar la obligación: " + e.getMessage());
            return false;
        }
    }

    public List<Obligacion> findAllObligaciones() throws SQLException {
        return obligacionDAO.findAll();
    }

    public boolean eliminarObligacion(int id) throws SQLException {
        return obligacionDAO.delete(id);
    }

    public boolean quitarObligacionDeNegocio(int idObligacion, int idNegocioFiduciario) throws SQLException {
        if (!negocioFiduciarioDAO.existeNegocio(idNegocioFiduciario) || !obligacionDAO.existeObligacion(idObligacion)) {
            System.out.println("Verificación fallida de negocio u obligación.");
            return false;
        }

        return obligacionDAO.removeObligacionFromNegocio(idObligacion, idNegocioFiduciario);
    }

    public List<NegocioFiduciario> findNegociosByObligacion(int idObligacion) throws SQLException {
        return obligacionDAO.findNegociosByObligacion(idObligacion);
    }



}


