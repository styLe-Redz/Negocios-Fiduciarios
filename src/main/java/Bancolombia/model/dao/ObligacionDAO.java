package Bancolombia.model.dao;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObligacionDAO {

    private Connection connection;
    private NegocioFiduciarioDAO negocioFiduciarioDAO; // Agregar esta línea

    public ObligacionDAO(Connection connection, NegocioFiduciarioDAO negocioFiduciarioDAO) {
        this.connection = connection;
        this.negocioFiduciarioDAO = negocioFiduciarioDAO; // Inicializar aquí
    }

    // Insertar una nueva obligación y su relación con negocio fiduciario
    public boolean insert(Obligacion obligacion, int idNegocioFiduciario) throws SQLException {
        String sqlObligacion = "INSERT INTO obligacion (Descripcion, Monto, Fecha_de_vencimiento) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlObligacion, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, obligacion.getDescripcion());
            statement.setBigDecimal(2, obligacion.getMonto());
            statement.setDate(3, new java.sql.Date(obligacion.getFechaVencimiento().getTime()));

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idObligacion = rs.getInt(1);
                        obligacion.setIdObligacion(idObligacion);
                        // Insertar relación en la tabla de unión
                        insertNegocioFiduciarioObligacion(idNegocioFiduciario, idObligacion);
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }


    public boolean insertNegocioFiduciarioObligacion(int idNegocioFiduciario, int idObligacion) throws SQLException {
        String sqlRelacion = "INSERT INTO negocio_fiduciario_obligacion (id_Negocio_Fiduciario, id_Obligacion) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlRelacion)) {
            statement.setInt(1, idNegocioFiduciario);
            statement.setInt(2, idObligacion);
            statement.executeUpdate();
            return true;
        }
    }

    public boolean existeObligacion(int idObligacion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM obligacion WHERE id_Obligacion = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idObligacion);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Buscar obligación por ID
    public Obligacion findById(int id) throws SQLException {
        String sql = "SELECT * FROM obligacion WHERE id_Obligacion = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Obligacion(
                        resultSet.getInt("id_Obligacion"),
                        resultSet.getString("Descripcion"),
                        resultSet.getBigDecimal("Monto"),
                        resultSet.getDate("Fecha_de_vencimiento")
                );
            }
        }
        return null;
    }

    // Listar todas las obligaciones
    public List<Obligacion> findAll() throws SQLException {
        List<Obligacion> obligaciones = new ArrayList<>();
        String sql = "SELECT * FROM obligacion";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Obligacion obligacion = new Obligacion(
                        resultSet.getInt("id_Obligacion"),
                        resultSet.getString("Descripcion"),
                        resultSet.getBigDecimal("Monto"),
                        resultSet.getDate("Fecha_de_vencimiento")
                );
                obligaciones.add(obligacion);
            }
        }
        return obligaciones;
    }

    // Actualizar una obligación existente
    public boolean update(Obligacion obligacion, int idNegocioFiduciario) throws SQLException {
        String sql = "UPDATE obligacion SET Descripcion = ?, Monto = ?, Fecha_de_vencimiento = ? WHERE id_Obligacion = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, obligacion.getDescripcion());
            statement.setBigDecimal(2, obligacion.getMonto());
            statement.setDate(3, new java.sql.Date(obligacion.getFechaVencimiento().getTime()));
            statement.setInt(4, obligacion.getIdObligacion());

            if (statement.executeUpdate() > 0) {
                // Actualizar la relación en la tabla de unión
                String sqlRelacion = "UPDATE negocio_fiduciario_obligacion SET id_Negocio_Fiduciario = ? WHERE id_Obligacion = ?";
                try (PreparedStatement statementRelacion = connection.prepareStatement(sqlRelacion)) {
                    statementRelacion.setInt(1, idNegocioFiduciario);
                    statementRelacion.setInt(2, obligacion.getIdObligacion());
                    statementRelacion.executeUpdate();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    // Eliminar una obligación
    // Método para eliminar la relación en la tabla de unión
    public boolean deleteNegocioFiduciarioObligacion(int idObligacion) throws SQLException {
        String sql = "DELETE FROM negocio_fiduciario_obligacion WHERE id_Obligacion = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idObligacion);
            return statement.executeUpdate() > 0;
        }
    }

    // Eliminar una obligación
    public boolean delete(int id) throws SQLException {
        // Primero eliminar la relación en la tabla de unión
        deleteNegocioFiduciarioObligacion(id);

        // Luego eliminar la obligación en la tabla obligacion
        String sql = "DELETE FROM obligacion WHERE id_Obligacion = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public int findIdNegocioFiduciario(int idObligacion) throws SQLException {
        String sql = "SELECT id_Negocio_Fiduciario FROM negocio_fiduciario_obligacion WHERE id_Obligacion = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idObligacion);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_Negocio_Fiduciario");
                }
            }
        }
        return -1; // Si no encuentra la relación, retorna -1.
    }

    public boolean removeObligacionFromNegocio(int idObligacion, int idNegocioFiduciario) throws SQLException {
        String sql = "DELETE FROM negocio_fiduciario_obligacion WHERE id_Obligacion = ? AND id_Negocio_Fiduciario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idObligacion);
            statement.setInt(2, idNegocioFiduciario);
            return statement.executeUpdate() > 0;
        }
    }

    public List<NegocioFiduciario> findNegociosByObligacion(int idObligacion) throws SQLException {
        List<NegocioFiduciario> negocios = new ArrayList<>();
        String sql = "SELECT nf.* FROM negocio_fiduciario nf " +
                "JOIN negocio_fiduciario_obligacion nfo ON nf.Id_Negocio_Fiduciario = nfo.id_Negocio_Fiduciario " +
                "WHERE nfo.id_Obligacion = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idObligacion);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                NegocioFiduciario negocio = new NegocioFiduciario(
                        resultSet.getInt("Id_Negocio_Fiduciario"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Descripcion"),
                        resultSet.getDate("Fecha_de_inicio").toLocalDate(),
                        resultSet.getDate("Fecha_de_fin").toLocalDate()
                );
                negocios.add(negocio);
            }
        }
        return negocios;
    }



}
