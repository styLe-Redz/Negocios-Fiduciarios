package Bancolombia.controller;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import Bancolombia.model.PersonasParticipantes;
import Bancolombia.model.service.PersonasParticipantesService;
import Bancolombia.model.dao.NegocioFiduciarioDAO;
import Bancolombia.model.dao.PersonasParticipantesDAO;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class PersonasParticipantesController {
    private PersonasParticipantesService personasParticipantesService;

    public PersonasParticipantesController(Connection connection) {
        NegocioFiduciarioDAO negocioFiduciarioDAO = new NegocioFiduciarioDAO(connection);
        PersonasParticipantesDAO personasParticipantesDAO = new PersonasParticipantesDAO(connection);
        this.personasParticipantesService = new PersonasParticipantesService(personasParticipantesDAO, negocioFiduciarioDAO);
    }

    public boolean agregarPersonaParticipante(String nombre, String apellido, String tipoDocumento, String numeroDocumento, int idNegocio) {
        try {
            if (personasParticipantesService.existeNumeroDocumento(numeroDocumento)) {
                showAlert("El número de documento ya existe. Por favor, ingrese un número de documento único.", Alert.AlertType.ERROR);
                return false;
            }
            PersonasParticipantes persona = new PersonasParticipantes(0, nombre, apellido, tipoDocumento, numeroDocumento);
            personasParticipantesService.agregarPersonaParticipante(persona, idNegocio);
            return true;
        } catch (SQLException e) {
            showAlert("Error al agregar la persona participante: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public boolean asignarPersonaNegocio(int idPersona, int idNegocioFiduciario) {
        try {
            personasParticipantesService.asignarPersonaNegocio(idPersona, idNegocioFiduciario);
            return true;
        } catch (IllegalArgumentException e) {
            showAlert("Error al asignar la persona al negocio: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        } catch (SQLException e) {
            showAlert("Error al asignar la persona al negocio: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public PersonasParticipantes buscarPersonaParticipantePorId(int id) {
        try {
            return personasParticipantesService.buscarPersonaParticipantePorId(id);
        } catch (SQLException e) {
            showAlert("Error al buscar la persona participante: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    public PersonasParticipantes buscarPersonaParticipantePorDocumento(String numeroDocumento) {
        try {
            return personasParticipantesService.buscarPersonaParticipantePorDocumento(numeroDocumento);
        } catch (SQLException e) {
            showAlert("Error al buscar la persona participante: " + e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
    }

    public boolean actualizarPersonaParticipante(PersonasParticipantes persona) {
        try {
            if (personasParticipantesService.existeNumeroDocumento(persona.getNumeroDocumento())) {
                showAlert("El número de documento ya existe. Por favor, ingrese un número de documento único.", Alert.AlertType.ERROR);
                return false;
            }
            return personasParticipantesService.actualizarPersonaParticipante(persona);
        } catch (SQLException e) {
            showAlert("Error al actualizar la persona participante: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public List<PersonasParticipantes> findAllPersonasParticipantes() {
        try {
            return personasParticipantesService.findAllPersonasParticipantes();
        } catch (SQLException e) {
            showAlert("Error al cargar las personas participantes: " + e.getMessage(), Alert.AlertType.ERROR);
            return Collections.emptyList();
        }
    }

    public boolean eliminarPersonaParticipante(int id) {
        try {
            return personasParticipantesService.eliminarPersonaParticipante(id);
        } catch (SQLException e) {
            showAlert("Error al eliminar la persona participante: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public boolean quitarPersonaDeNegocio(int idPersona, int idNegocioFiduciario) {
        try {
            return personasParticipantesService.quitarPersonaDeNegocio(idPersona, idNegocioFiduciario);
        } catch (SQLException e) {
            showAlert("Error al quitar la persona del negocio: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public boolean existeNumeroDocumento(String numeroDocumento) {
        try {
            return personasParticipantesService.existeNumeroDocumento(numeroDocumento);
        } catch (SQLException e) {
            showAlert("Error al verificar el número de documento: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    public List<NegocioFiduciario> findNegociosByPersona(int idPersona) throws SQLException {
        return personasParticipantesService.findNegociosByPersona(idPersona);
    }

    public List<Obligacion> findObligacionesByNegocioId(int idNegocio) throws SQLException {
        return personasParticipantesService.findObligacionesByNegocioId(idNegocio);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Información");
        alert.setHeaderText(null); // No header
        alert.setContentText(message);
        alert.showAndWait();
    }
}

