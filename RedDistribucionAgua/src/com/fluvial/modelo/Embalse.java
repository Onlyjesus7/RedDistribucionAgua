package com.fluvial.modelo;

/**
 * Clase que representa un embalse o fuente de agua en la red de distribución.
 * Un embalse es un nodo fuente que genera flujo hacia la red.
 * 
 * @author Equipo Fluvial
 * @version 1.0
 */
public class Embalse extends Nodo {
    
    /** Capacidad máxima de suministro del embalse en metros cúbicos por hora */
    private double capacidadMaxima;
    
    /** Altura del embalse sobre el nivel del mar en metros */
    private double elevacion;
    
    /** Volumen actual de agua almacenada en metros cúbicos */
    private double volumenActual;
    
    /** Flujo total que sale del embalse hacia la red en metros cúbicos por hora */
    private double flujoSalida;
    
    /**
     * Constructor para crear un embalse con todos sus parámetros
     * 
     * @param id Identificador único
     * @param nombre Nombre del embalse
     * @param capacidadMaxima Capacidad máxima de suministro
     * @param elevacion Altura sobre el nivel del mar
     * @param posicionX Coordenada X para visualización
     * @param posicionY Coordenada Y para visualización
     */
    public Embalse(String id, String nombre, double capacidadMaxima, 
                   double elevacion, int posicionX, int posicionY) {
        super(id, nombre, "EMBALSE", posicionX, posicionY);
        this.capacidadMaxima = capacidadMaxima;
        this.elevacion = elevacion;
        this.volumenActual = capacidadMaxima * 0.8; // 80% de capacidad inicial
        this.flujoSalida = 0.0;
    }
    
    /**
     * Verifica si el embalse tiene suficiente agua para suministrar
     * 
     * @param cantidad Cantidad requerida en metros cúbicos
     * @return true si hay suficiente agua, false en caso contrario
     */
    public boolean tieneAguaSuficiente(double cantidad) {
        return volumenActual >= cantidad;
    }
    
    /**
     * Reduce el volumen de agua del embalse cuando se suministra a la red
     * 
     * @param cantidad Cantidad de agua extraída en metros cúbicos
     */
    public void extraerAgua(double cantidad) {
        if (tieneAguaSuficiente(cantidad)) {
            volumenActual -= cantidad;
        } else {
            // Si no hay suficiente, se extrae lo que queda
            volumenActual = 0;
        }
    }
    
    /**
     * Calcula el porcentaje de capacidad utilizada
     * 
     * @return Porcentaje de uso (0-100)
     */
    public double calcularPorcentajeUso() {
        if (capacidadMaxima == 0) return 0;
        return (flujoSalida / capacidadMaxima) * 100;
    }
    
    /**
     * Verifica si el embalse está cerca de su capacidad máxima
     * 
     * @return true si está sobrecargado (uso > 90%)
     */
    public boolean estaSobrecargado() {
        return calcularPorcentajeUso() > 90.0;
    }
    
    // Getters y Setters
    public double getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(double capacidadMaxima) { 
        this.capacidadMaxima = capacidadMaxima; 
    }
    
    public double getElevacion() { return elevacion; }
    public void setElevacion(double elevacion) { this.elevacion = elevacion; }
    
    public double getVolumenActual() { return volumenActual; }
    public void setVolumenActual(double volumenActual) { 
        this.volumenActual = volumenActual; 
    }
    
    public double getFlujoSalida() { return flujoSalida; }
    public void setFlujoSalida(double flujoSalida) { 
        this.flujoSalida = flujoSalida; 
    }
    
    @Override
    public String obtenerDescripcion() {
        return String.format(
            "Embalse: %s%n" +
            "Capacidad máxima: %.2f m³/h%n" +
            "Elevación: %.2f msnm%n" +
            "Volumen actual: %.2f m³%n" +
            "Flujo salida: %.2f m³/h%n" +
            "Uso: %.1f%%",
            getNombre(),
            capacidadMaxima,
            elevacion,
            volumenActual,
            flujoSalida,
            calcularPorcentajeUso()
        );
    }
}