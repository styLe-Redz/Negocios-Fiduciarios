package Bancolombia.model.dao;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.PersonasParticipantes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonasParticipantesDAO {
    private Connection connection;

    public PersonasParticipantesDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(PersonasParticipantes persona, int idNegocioFiduciario) throws SQLException {
        String sqlPersona = "INSERT INTO personas_participantes (nombre, apellido, tipo_de_documento, numero_de_documento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlPersona, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, persona.getNombre());
            statement.setString(2, persona.getApellido());
            statement.setString(3, persona.getTipoDocumento());
            statement.setString(4, persona.getNumeroDocumento());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idPersona = rs.getInt(1);
                        persona.setIdPersona(idPersona);
                        insertPersonaNegocio(idPersona, idNegocioFiduciario);
                    }
                }
            }
        }
    }

    public void insertPersonaNegocio(int idPersona, int idNegocioFiduciario) throws SQLException {
        String sqlRelacion = "INSERT INTO personas_negocio_fiduciario (id_Persona, id_Negocio_Fiduciario) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlRelacion)) {
            statement.setInt(1, idPersona);
            statement.setInt(2, idNegocioFiduciario);
            statement.executeUpdate();
        }
    }

    public PersonasParticipantes findById(int id) throws SQLException {
        String sql = "SELECT * FROM personas_participantes WHERE id_Persona = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new PersonasParticipantes(
                        resultSet.getInt("id_Persona"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Apellido"),
                        resultSet.getString("Tipo_de_documento"),
                        resultSet.getString("Numero_de_documento")
                );
            }
        }
        return null;
    }

    public List<PersonasParticipantes> findAll() throws SQLException {
        List<PersonasParticipantes> personas = new ArrayList<>();
        String sql = "SELECT * FROM personas_participantes";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PersonasParticipantes persona = new PersonasParticipantes(
                        resultSet.getInt("id_Persona"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Apellido"),
                        resultSet.getString("Tipo_de_documento"),
                        resultSet.getString("Numero_de_documento")
                );
                personas.add(persona);
            }
        }
        return personas;
    }

    public boolean update(PersonasParticipantes persona) throws SQLException {
        String sql = "UPDATE personas_participantes SET Nombre = ?, Apellido = ?, Tipo_de_documento = ?, Numero_de_documento = ? WHERE id_Persona = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, persona.getNombre());
            statement.setString(2, persona.getApellido());
            statement.setString(3, persona.getTipoDocumento());
            statement.setString(4, persona.getNumeroDocumento());
            statement.setInt(5, persona.getIdPersona());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        deletePersonaNegocio(id);
        String sql = "DELETE FROM personas_participantes WHERE id_Persona = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePersonaNegocio(int idPersona) throws SQLException {
        String sql = "DELETE FROM personas_negocio_fiduciario WHERE id_Persona = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPersona);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean existePersona(int idPersona) throws SQLException {
        String sql = "SELECT COUNT(*) FROM personas_participantes WHERE id_Persona = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPersona);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        }
    }

    public boolean removePersonaFromNegocio(int idPersona, int idNegocioFiduciario) throws SQLException {
        String sql = "DELETE FROM personas_negocio_fiduciario WHERE id_Persona = ? AND id_Negocio_Fiduciario = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPersona);
            statement.setInt(2, idNegocioFiduciario);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean existeNumeroDocumento(String numeroDocumento) throws SQLException {
        String sql = "SELECT COUNT(*) FROM personas_participantes WHERE numero_de_documento = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, numeroDocumento);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        }
    }

    public List<NegocioFiduciario> findNegociosByPersonaId(int idPersona) throws SQLException {
        List<NegocioFiduciario> negocios = new ArrayList<>();
        String sql = "SELECT nf.* FROM negocio_fiduciario nf " +
                "JOIN personas_negocio_fiduciario pnf ON nf.Id_Negocio_Fiduciario = pnf.id_Negocio_Fiduciario " +
                "WHERE pnf.id_Persona = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idPersona);
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

    public PersonasParticipantes buscarPorNumeroDocumento(String numeroDocumento) throws SQLException {
        String query = "SELECT * FROM Personas_Participantes WHERE Numero_de_documento = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, numeroDocumento);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new PersonasParticipantes(
                        resultSet.getInt("id_Persona"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Apellido"),
                        resultSet.getString("Tipo_de_documento"),
                        resultSet.getString("Numero_de_documento")
                );
            }
        }
        return null;
    }
}

