package Bancolombia.model.dao;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import Bancolombia.model.PersonasParticipantes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NegocioFiduciarioDAO {
    private Connection connection;

    public NegocioFiduciarioDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insert(NegocioFiduciario negocio) throws SQLException {
        String sql = "INSERT INTO negocio_fiduciario (Nombre, Descripcion, Fecha_de_inicio, Fecha_de_fin) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, negocio.getNombre());
            statement.setString(2, negocio.getDescripcion());
            statement.setDate(3, Date.valueOf(negocio.getFechaInicio())); // Convertir LocalDate a java.sql.Date
            statement.setDate(4, Date.valueOf(negocio.getFechaFin())); // Convertir LocalDate a java.sql.Date

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        negocio.setIdNegocioFiduciario(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public NegocioFiduciario findById(int id) throws SQLException {
        String sql = "SELECT * FROM negocio_fiduciario WHERE id_Negocio_Fiduciario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new NegocioFiduciario(
                            resultSet.getInt("id_Negocio_Fiduciario"),
                            resultSet.getString("Nombre"),
                            resultSet.getString("Descripcion"),
                            resultSet.getDate("Fecha_de_inicio").toLocalDate(), // Convertir java.sql.Date a LocalDate
                            resultSet.getDate("Fecha_de_fin").toLocalDate() // Convertir java.sql.Date a LocalDate
                    );
                }
            }
        }
        return null;
    }

    public List<NegocioFiduciario> findAll() throws SQLException {
        List<NegocioFiduciario> list = new ArrayList<>();
        String sql = "SELECT * FROM negocio_fiduciario";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(new NegocioFiduciario(
                        resultSet.getInt("id_Negocio_Fiduciario"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Descripcion"),
                        resultSet.getDate("Fecha_de_inicio").toLocalDate(), // Convertir java.sql.Date a LocalDate
                        resultSet.getDate("Fecha_de_fin").toLocalDate() // Convertir java.sql.Date a LocalDate
                ));
            }
        }
        return list;
    }

    public boolean update(NegocioFiduciario negocio) throws SQLException {
        String sql = "UPDATE negocio_fiduciario SET Nombre = ?, Descripcion = ?, Fecha_de_inicio = ?, Fecha_de_fin = ? WHERE id_Negocio_Fiduciario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, negocio.getNombre());
            statement.setString(2, negocio.getDescripcion());
            statement.setDate(3, Date.valueOf(negocio.getFechaInicio())); // Convertir LocalDate a java.sql.Date
            statement.setDate(4, Date.valueOf(negocio.getFechaFin())); // Convertir LocalDate a java.sql.Date
            statement.setInt(5, negocio.getIdNegocioFiduciario());
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM negocio_fiduciario WHERE id_Negocio_Fiduciario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;  // Retorna true si se eliminó al menos un registro
        }
    }

    public boolean existeNegocio(int idNegocioFiduciario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM negocio_fiduciario WHERE id_Negocio_Fiduciario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idNegocioFiduciario);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;  // Retorna true si el count es mayor que 0
                }
            }
        }
        return false;
    }

    public boolean nombreExiste(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM negocio_fiduciario WHERE Nombre = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean validarFechasNegocio(int idNegocioFiduciario, Date fechaObligacion) throws SQLException {
        String sql = "SELECT Fecha_de_inicio, Fecha_de_fin FROM negocio_fiduciario WHERE Id_Negocio_Fiduciario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idNegocioFiduciario);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Date fechaInicio = resultSet.getDate("Fecha_de_inicio");
                    Date fechaFin = resultSet.getDate("Fecha_de_fin");
                    return !fechaObligacion.before(fechaInicio) && !fechaObligacion.after(fechaFin);
                }
            }
        }
        return false; // Si no encuentra el negocio, retorna falso.
    }

    public NegocioFiduciario findByIdWithRelations(int id) throws SQLException {
        NegocioFiduciario negocio = findById(id);
        if (negocio == null) {
            return null;
        }

        // Obtener obligaciones del negocio
        String sqlObligaciones = "SELECT o.* FROM obligacion o " +
                "JOIN negocio_fiduciario_obligacion nfo ON o.id_obligacion = nfo.id_obligacion " +
                "WHERE nfo.id_Negocio_Fiduciario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlObligaciones)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Obligacion obligacion = new Obligacion(
                        rs.getInt("id_obligacion"),
                        rs.getString("Descripcion"),
                        rs.getBigDecimal("Monto"),
                        rs.getDate("Fecha_de_vencimiento")
                );
                negocio.getObligaciones().add(obligacion);
            }
        }

        // Obtener personas participantes del negocio
        String sqlPersonas = "SELECT p.* FROM personas_participantes p " +
                "JOIN personas_negocio_fiduciario pnf ON p.id_Persona = pnf.id_Persona " +
                "WHERE pnf.id_Negocio_Fiduciario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlPersonas)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PersonasParticipantes persona = new PersonasParticipantes(
                        rs.getInt("id_Persona"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Tipo_de_documento"),
                        rs.getString("Numero_de_documento")
                );
                negocio.getParticipantes().add(persona);
            }
        }

        return negocio;
    }

    public List<Obligacion> findObligacionesByNegocioId(int idNegocioFiduciario) throws SQLException {
        List<Obligacion> obligaciones = new ArrayList<>();
        String sql = "SELECT o.* FROM obligacion o " +
                "JOIN negocio_fiduciario_obligacion nfo ON o.id_obligacion = nfo.id_obligacion " +
                "WHERE nfo.id_Negocio_Fiduciario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idNegocioFiduciario);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Obligacion obligacion = new Obligacion(
                        rs.getInt("id_obligacion"),
                        rs.getString("Descripcion"),
                        rs.getBigDecimal("Monto"),
                        rs.getDate("Fecha_de_vencimiento")
                );
                obligaciones.add(obligacion);
            }
        }
        return obligaciones;
    }

}

