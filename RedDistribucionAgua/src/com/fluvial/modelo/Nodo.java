package com.fluvial.modelo;

/**
 * Clase abstracta que representa un nodo en el grafo de la red de distribución.
 * Un nodo puede ser un embalse (fuente), un barrio (sumidero) o una conexión
 * intermedia (estación de bombeo o punto de unión).
 * 
 * @author Equipo Fluvial
 * @version 1.0
 */
public abstract class Nodo {
    
    /** Identificador único del nodo */
    private String id;
    
    /** Nombre descriptivo del nodo */
    private String nombre;
    
    /** Coordenada X para la visualización en el grafo */
    private int posicionX;
    
    /** Coordenada Y para la visualización en el grafo */
    private int posicionY;
    
    /** Tipo de nodo: EMBALSE, BARRIO, CONEXION */
    private String tipo;
    
    /** Estado del nodo: ACTIVO, INACTIVO, MANTENIMIENTO */
    private String estado;
    
    /**
     * Constructor para crear un nodo básico
     * 
     * @param id Identificador único del nodo
     * @param nombre Nombre descriptivo
     * @param tipo Tipo de nodo (EMBALSE, BARRIO, CONEXION)
     */
    public Nodo(String id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = "ACTIVO";
        this.posicionX = 0;
        this.posicionY = 0;
    }
    
    /**
     * Constructor con coordenadas para visualización
     * 
     * @param id Identificador único del nodo
     * @param nombre Nombre descriptivo
     * @param tipo Tipo de nodo
     * @param posicionX Coordenada X
     * @param posicionY Coordenada Y
     */
    public Nodo(String id, String nombre, String tipo, int posicionX, int posicionY) {
        this(id, nombre, tipo);
        this.posicionX = posicionX;
        this.posicionY = posicionY;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public int getPosicionX() { return posicionX; }
    public void setPosicionX(int posicionX) { this.posicionX = posicionX; }
    
    public int getPosicionY() { return posicionY; }
    public void setPosicionY(int posicionY) { this.posicionY = posicionY; }
    
    /**
     * Método abstracto que debe ser implementado por las subclases
     * para definir el comportamiento específico de cada tipo de nodo
     * 
     * @return Descripción del nodo
     */
    public abstract String obtenerDescripcion();
    
    @Override
    public String toString() {
        return nombre + " (" + tipo + ") - ID: " + id;
    }
}