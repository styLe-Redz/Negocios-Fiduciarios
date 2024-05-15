package Bancolombia.model.service;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.dao.NegocioFiduciarioDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class NegocioFiduciarioService {
    private NegocioFiduciarioDAO dao;

    public NegocioFiduciarioService(Connection connection) {
        this.dao = new NegocioFiduciarioDAO(connection);
    }

    public boolean crearNegocio(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        if (dao.nombreExiste(nombre)) {
            System.out.println("El nombre del negocio ya existe.");
            return false;
        }

        NegocioFiduciario nuevoNegocio = new NegocioFiduciario(0, nombre, descripcion, fechaInicio, fechaFin);
        return dao.insert(nuevoNegocio);
    }

    public boolean actualizarNegocio(NegocioFiduciario negocio) throws SQLException {
        NegocioFiduciario existente = dao.findById(negocio.getIdNegocioFiduciario());
        if (existente != null && !existente.getNombre().equals(negocio.getNombre()) && dao.nombreExiste(negocio.getNombre())) {
            return false;
        }
        return dao.update(negocio);
    }

    public NegocioFiduciario findById(int id) throws SQLException {
        return dao.findById(id);
    }

    public List<NegocioFiduciario> findAll() throws SQLException {
        return dao.findAll();
    }

    public boolean eliminarNegocio(int id) throws SQLException {
        return dao.delete(id);
    }

    public NegocioFiduciario findByIdWithRelations(int id) throws SQLException {
        return dao.findByIdWithRelations(id);
    }
}
