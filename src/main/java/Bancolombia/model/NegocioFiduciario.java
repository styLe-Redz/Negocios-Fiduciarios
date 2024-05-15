package Bancolombia.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NegocioFiduciario {
    private int idNegocioFiduciario;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Obligacion> obligaciones;
    private List<PersonasParticipantes> participantes;

    public NegocioFiduciario(int idNegocioFiduciario, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idNegocioFiduciario = idNegocioFiduciario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.obligaciones = new ArrayList<>();
        this.participantes = new ArrayList<>();
    }

    public int getIdNegocioFiduciario() {
        return idNegocioFiduciario;
    }

    public void setIdNegocioFiduciario(int idNegocioFiduciario) {
        this.idNegocioFiduciario = idNegocioFiduciario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<Obligacion> getObligaciones() {
        return obligaciones;
    }

    public List<PersonasParticipantes> getParticipantes() {
        return participantes;
    }

    @Override
    public String toString() {
        return nombre + " (" + idNegocioFiduciario + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NegocioFiduciario that = (NegocioFiduciario) o;
        return idNegocioFiduciario == that.idNegocioFiduciario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNegocioFiduciario);
    }
}


