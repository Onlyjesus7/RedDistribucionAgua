package com.fluvial.modelo;

/**
 * Clase que representa un nodo de conexión intermedio en la red.
 * Las conexiones pueden ser estaciones de bombeo o puntos de unión de tuberías.
 * 
 * @author Equipo Fluvial
 * @version 1.0
 */
public class Conexion extends Nodo {
    
    /** Tipo de conexión: BOMBEO, UNION, VALVULA */
    private String tipoConexion;
    
    /** Presión en la conexión en bares */
    private double presion;
    
    /** Flujo total que pasa por la conexión en metros cúbicos por hora */
    private double flujoTotal;
    
    /** Capacidad máxima de la conexión en metros cúbicos por hora */
    private double capacidadMaxima;
    
    /** Factor de eficiencia de la conexión (0-1) */
    private double eficiencia;
    
    /**
     * Constructor para crear una conexión con todos sus parámetros
     * 
     * @param id Identificador único
     * @param nombre Nombre de la conexión
     * @param tipoConexion Tipo de conexión
     * @param presion Presión en bares
     * @param capacidadMaxima Capacidad máxima
     * @param posicionX Coordenada X para visualización
     * @param posicionY Coordenada Y para visualización
     */
    public Conexion(String id, String nombre, String tipoConexion,
                    double presion, double capacidadMaxima, 
                    int posicionX, int posicionY) {
        super(id, nombre, "CONEXION", posicionX, posicionY);
        this.tipoConexion = tipoConexion;
        this.presion = presion;
        this.capacidadMaxima = capacidadMaxima;
        this.flujoTotal = 0.0;
        this.eficiencia = 1.0; // 100% eficiente por defecto
    }
    
    /**
     * Calcula si la conexión está cerca de su capacidad máxima
     * 
     * @return true si el flujo supera el 85% de la capacidad
     */
    public boolean estaSaturada() {
        if (capacidadMaxima == 0) return false;
        return (flujoTotal / capacidadMaxima) > 0.85;
    }
    
    /**
     * Calcula la capacidad residual disponible
     * 
     * @return Capacidad disponible en m³/h
     */
    public double calcularCapacidadResidual() {
        return Math.max(0, capacidadMaxima - flujoTotal);
    }
    
    /**
     * Verifica si la conexión está operativa
     * 
     * @return true si está en estado ACTIVO y tiene eficiencia > 0.5
     */
    public boolean estaOperativa() {
        return getEstado().equals("ACTIVO") && eficiencia > 0.5;
    }
    
    /**
     * Calcula el porcentaje de capacidad utilizada
     * 
     * @return Porcentaje de uso (0-100)
     */
    public double calcularPorcentajeUso() {
        if (capacidadMaxima == 0) return 0;
        return (flujoTotal / capacidadMaxima) * 100;
    }
    
    // Getters y Setters
    public String getTipoConexion() { return tipoConexion; }
    public void setTipoConexion(String tipoConexion) { 
        this.tipoConexion = tipoConexion; 
    }
    
    public double getPresion() { return presion; }
    public void setPresion(double presion) { this.presion = presion; }
    
    public double getFlujoTotal() { return flujoTotal; }
    public void setFlujoTotal(double flujoTotal) { this.flujoTotal = flujoTotal; }
    
    public double getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(double capacidadMaxima) { 
        this.capacidadMaxima = capacidadMaxima; 
    }
    
    public double getEficiencia() { return eficiencia; }
    public void setEficiencia(double eficiencia) { 
        this.eficiencia = Math.max(0, Math.min(1, eficiencia)); 
    }
    
    @Override
    public String obtenerDescripcion() {
        return String.format(
            "Conexión: %s%n" +
            "Tipo: %s%n" +
            "Presión: %.2f bares%n" +
            "Flujo total: %.2f m³/h%n" +
            "Capacidad máxima: %.2f m³/h%n" +
            "Uso: %.1f%%%n" +
            "Eficiencia: %.1f%%%n" +
            "Estado: %s",
            getNombre(),
            tipoConexion,
            presion,
            flujoTotal,
            capacidadMaxima,
            calcularPorcentajeUso(),
            eficiencia * 100,
            estaOperativa() ? "OPERATIVA" : "NO OPERATIVA"
        );
    }
}