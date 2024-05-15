package Bancolombia.model;

import java.math.BigDecimal;
import java.util.Date;

public class Obligacion {

    private int idObligacion;
    private String descripcion;
    private BigDecimal monto;
    private Date fechaVencimiento;

    public Obligacion(int idObligacion, String descripcion, BigDecimal monto, Date fechaVencimiento) {
        this.idObligacion = idObligacion;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fechaVencimiento = fechaVencimiento;
    }

    public int getIdObligacion() {
        return idObligacion;
    }

    public void setIdObligacion(int idObligacion) {
        this.idObligacion = idObligacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public String toString() {
        return descripcion + " (" + idObligacion + ")";
    }

}