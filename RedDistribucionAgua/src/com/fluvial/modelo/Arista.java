package com.fluvial.modelo;

public class Arista {
    
    private Nodo origen;
    private Nodo destino;
    private double capacidad;
    private double flujo;
    private String estado;
    private double longitud;
    private double diametro;
    
    public Arista(Nodo origen, Nodo destino, double capacidad) {
        this.origen = origen;
        this.destino = destino;
        this.capacidad = capacidad > 0 ? capacidad : 100;
        this.flujo = 0.0;
        this.estado = "ACTIVA";
        this.longitud = 0;
        this.diametro = 0;
    }
    
    public Arista(Nodo origen, Nodo destino, double capacidad, double longitud, double diametro) {
        this(origen, destino, capacidad);
        this.longitud = longitud;
        this.diametro = diametro;
    }
    
    public double capacidadResidual() {
        return Math.max(0, capacidad - flujo);
    }
    
    public boolean estaSaturada() {
        return capacidadResidual() == 0;
    }
    
    public boolean aumentarFlujo(double cantidad) {
        if (flujo + cantidad <= capacidad) {
            flujo += cantidad;
            actualizarEstado();
            return true;
        }
        return false;
    }
    
    public boolean disminuirFlujo(double cantidad) {
        if (flujo - cantidad >= 0) {
            flujo -= cantidad;
            actualizarEstado();
            return true;
        }
        return false;
    }
    
    private void actualizarEstado() {
        if (estado.equals("INACTIVA")) return;
        double porcentaje = (flujo / capacidad) * 100;
        if (porcentaje >= 95) {
            estado = "SATURADA";
        } else if (porcentaje >= 70) {
            estado = "ALTA_CARGA";
        } else {
            estado = "ACTIVA";
        }
    }
    
    public double calcularPorcentajeUso() {
        if (capacidad == 0) return 0;
        return (flujo / capacidad) * 100;
    }
    
    public boolean esAumentante() {
        return capacidadResidual() > 0;
    }
    
    // Getters y Setters
    public Nodo getOrigen() { return origen; }
    public Nodo getDestino() { return destino; }
    public double getCapacidad() { return capacidad; }
    public void setCapacidad(double capacidad) { 
        this.capacidad = capacidad > 0 ? capacidad : 100;
        actualizarEstado();
    }
    public double getFlujo() { return flujo; }
    public void setFlujo(double flujo) { 
        this.flujo = Math.min(flujo, capacidad);
        actualizarEstado();
    }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public double getDiametro() { return diametro; }
    public void setDiametro(double diametro) { this.diametro = diametro; }
    
    @Override
    public String toString() {
        return String.format("%s -> %s | Flujo: %.2f/%.2f (%.1f%%) | Estado: %s",
            origen.getNombre(), destino.getNombre(), flujo, capacidad, calcularPorcentajeUso(), estado);
    }
}