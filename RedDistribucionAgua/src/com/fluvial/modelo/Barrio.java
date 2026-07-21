package com.fluvial.modelo;

/**
 * Clase que representa un barrio o sumidero en la red de distribución.
 * Un barrio es un nodo que consume agua de la red.
 * 
 * @author Equipo Fluvial
 * @version 1.0
 */
public class Barrio extends Nodo {
    
    /** Población del barrio (número de habitantes) */
    private int poblacion;
    
    /** Demanda de agua en metros cúbicos por hora */
    private double demanda;
    
    /** Flujo de agua que llega al barrio en metros cúbicos por hora */
    private double flujoRecibido;
    
    /** Nivel de presión en el barrio en bares */
    private double presion;
    
    /** Prioridad del barrio (1-10, siendo 10 la máxima prioridad) */
    private int prioridad;
    
    /**
     * Constructor para crear un barrio con todos sus parámetros
     * 
     * @param id Identificador único
     * @param nombre Nombre del barrio
     * @param poblacion Número de habitantes
     * @param demanda Demanda de agua en m³/h
     * @param presion Presión en bares
     * @param prioridad Prioridad (1-10)
     * @param posicionX Coordenada X para visualización
     * @param posicionY Coordenada Y para visualización
     */
    public Barrio(String id, String nombre, int poblacion, double demanda,
                  double presion, int prioridad, int posicionX, int posicionY) {
        super(id, nombre, "BARRIO", posicionX, posicionY);
        this.poblacion = poblacion;
        this.demanda = demanda;
        this.presion = presion;
        this.prioridad = Math.max(1, Math.min(10, prioridad)); // Validar rango
        this.flujoRecibido = 0.0;
    }
    
    /**
     * Calcula si el barrio está recibiendo suficiente agua
     * 
     * @return true si el flujo recibido es mayor o igual a la demanda
     */
    public boolean tieneSuministroSuficiente() {
        return flujoRecibido >= demanda;
    }
    
    /**
     * Calcula el déficit de agua en el barrio
     * 
     * @return Cantidad de agua faltante (0 si no hay déficit)
     */
    public double calcularDeficit() {
        if (flujoRecibido >= demanda) {
            return 0.0;
        }
        return demanda - flujoRecibido;
    }
    
    /**
     * Calcula el porcentaje de la demanda que está siendo cubierta
     * 
     * @return Porcentaje de cobertura (0-100)
     */
    public double calcularCobertura() {
        if (demanda == 0) return 100.0;
        return (flujoRecibido / demanda) * 100;
    }
    
    /**
     * Verifica si el barrio tiene baja presión (peligro de suministro)
     * 
     * @return true si la presión es menor a 2 bares
     */
    public boolean tieneBajaPresion() {
        return presion < 2.0;
    }
    
    // Getters y Setters
    public int getPoblacion() { return poblacion; }
    public void setPoblacion(int poblacion) { this.poblacion = poblacion; }
    
    public double getDemanda() { return demanda; }
    public void setDemanda(double demanda) { this.demanda = demanda; }
    
    public double getFlujoRecibido() { return flujoRecibido; }
    public void setFlujoRecibido(double flujoRecibido) { 
        this.flujoRecibido = flujoRecibido; 
    }
    
    public double getPresion() { return presion; }
    public void setPresion(double presion) { this.presion = presion; }
    
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { 
        this.prioridad = Math.max(1, Math.min(10, prioridad)); 
    }
    
    @Override
    public String obtenerDescripcion() {
        return String.format(
            "Barrio: %s%n" +
            "Población: %d habitantes%n" +
            "Demanda: %.2f m³/h%n" +
            "Flujo recibido: %.2f m³/h%n" +
            "Cobertura: %.1f%%%n" +
            "Presión: %.2f bares%n" +
            "Prioridad: %d/10%n" +
            "Estado: %s",
            getNombre(),
            poblacion,
            demanda,
            flujoRecibido,
            calcularCobertura(),
            presion,
            prioridad,
            tieneSuministroSuficiente() ? "SUMINISTRO ADECUADO" : "DÉFICIT"
        );
    }
}