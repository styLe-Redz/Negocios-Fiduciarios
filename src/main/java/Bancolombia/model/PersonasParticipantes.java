package Bancolombia.model;


import java.util.ArrayList;
import java.util.List;

public class PersonasParticipantes {

    private int idPersona;
    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String numeroDocumento;
    private List<NegocioFiduciario> negociosFiduciarios;

    public PersonasParticipantes(int idPersona, String nombre, String apellido, String tipoDocumento, String numeroDocumento) {
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.negociosFiduciarios = new ArrayList<>();
    }

    // Getters y Setters
    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public List<NegocioFiduciario> getNegociosFiduciarios() {
        return negociosFiduciarios;
    }

    public void setNegociosFiduciarios(List<NegocioFiduciario> negociosFiduciarios) {
        this.negociosFiduciarios = negociosFiduciarios;
    }

    @Override
    public String toString() {
        return nombre + " (" + numeroDocumento + ")";
    }
}