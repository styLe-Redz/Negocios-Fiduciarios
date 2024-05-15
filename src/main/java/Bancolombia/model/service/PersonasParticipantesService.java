package Bancolombia.model.service;

import Bancolombia.model.NegocioFiduciario;
import Bancolombia.model.Obligacion;
import Bancolombia.model.PersonasParticipantes;
import Bancolombia.model.dao.NegocioFiduciarioDAO;
import Bancolombia.model.dao.PersonasParticipantesDAO;

import java.sql.SQLException;
import java.util.List;

public class PersonasParticipantesService {
    private PersonasParticipantesDAO personasParticipantesDAO;
    private NegocioFiduciarioDAO negocioFiduciarioDAO;

    public PersonasParticipantesService(PersonasParticipantesDAO personasParticipantesDAO, NegocioFiduciarioDAO negocioFiduciarioDAO) {
        this.personasParticipantesDAO = personasParticipantesDAO;
        this.negocioFiduciarioDAO = negocioFiduciarioDAO;
    }

    public void agregarPersonaParticipante(PersonasParticipantes persona, int idNegocioFiduciario) throws SQLException {
        // Verificar si el Negocio Fiduciario existe
        if (!negocioFiduciarioDAO.existeNegocio(idNegocioFiduciario)) {
            throw new IllegalArgumentException("Negocio Fiduciario con ID " + idNegocioFiduciario + " no existe.");
        }
        personasParticipantesDAO.insert(persona, idNegocioFiduciario);
    }

    public void asignarPersonaNegocio(int idPersona, int idNegocioFiduciario) throws SQLException {
        // Verificar si el Negocio Fiduciario existe
        if (!negocioFiduciarioDAO.existeNegocio(idNegocioFiduciario)) {
            throw new IllegalArgumentException("Negocio Fiduciario con ID " + idNegocioFiduciario + " no existe.");
        }
        // Verificar si la persona existe
        if (!personasParticipantesDAO.existePersona(idPersona)) {
            throw new IllegalArgumentException("Persona con ID " + idPersona + " no existe.");
        }
        personasParticipantesDAO.insertPersonaNegocio(idPersona, idNegocioFiduciario);
    }

    public PersonasParticipantes buscarPersonaParticipantePorId(int id) throws SQLException {
        return personasParticipantesDAO.findById(id);
    }

    public boolean actualizarPersonaParticipante(PersonasParticipantes persona) throws SQLException {
        return personasParticipantesDAO.update(persona);
    }

    public List<PersonasParticipantes> findAllPersonasParticipantes() throws SQLException {
        return personasParticipantesDAO.findAll();
    }


    public boolean eliminarPersonaParticipante(int id) throws SQLException {
        return personasParticipantesDAO.delete(id);
    }

    public boolean quitarPersonaDeNegocio(int idPersona, int idNegocioFiduciario) throws SQLException {
        if (!negocioFiduciarioDAO.existeNegocio(idNegocioFiduciario) || !personasParticipantesDAO.existePersona(idPersona)) {
            System.out.println("Verificación fallida de negocio o persona.");
            return false;
        }
        return personasParticipantesDAO.removePersonaFromNegocio(idPersona, idNegocioFiduciario);
    }

    public boolean existeNumeroDocumento(String numeroDocumento) throws SQLException {
        return personasParticipantesDAO.existeNumeroDocumento(numeroDocumento);
    }

    //Generador de Excel

    public List<NegocioFiduciario> findNegociosByPersona(int idPersona) throws SQLException {
        return personasParticipantesDAO.findNegociosByPersonaId(idPersona);
    }

    public PersonasParticipantes buscarPersonaParticipantePorDocumento(String numeroDocumento) throws SQLException {
        return personasParticipantesDAO.buscarPorNumeroDocumento(numeroDocumento);
    }

    public List<Obligacion> findObligacionesByNegocioId(int idNegocioFiduciario) throws SQLException {
        return negocioFiduciarioDAO.findObligacionesByNegocioId(idNegocioFiduciario);
    }
}
