package com.fluvial.algoritmo;

import com.fluvial.modelo.Grafo;
import com.fluvial.modelo.Nodo;
import com.fluvial.modelo.Arista;
import java.util.List;

/**
 * Interfaz que define el contrato para algoritmos de flujo máximo.
 * Cualquier algoritmo de flujo máximo debe implementar esta interfaz.
 * 
 * @author Equipo Fluvial
 * @version 1.0
 */
public interface AlgoritmoFlujoMaximo {
    
    /**
     * Calcula el flujo máximo en el grafo desde la fuente hasta el sumidero
     * 
     * @param grafo Grafo que representa la red de distribución
     * @param fuente Nodo fuente (embalse)
     * @param sumidero Nodo sumidero (barrio)
     * @return El valor del flujo máximo calculado
     */
    double calcularFlujoMaximo(Grafo grafo, Nodo fuente, Nodo sumidero);
    
    /**
     * Obtiene el nombre del algoritmo
     * 
     * @return Nombre descriptivo del algoritmo
     */
    String obtenerNombre();
    
    /**
     * Obtiene la descripción del algoritmo
     * 
     * @return Descripción detallada
     */
    String obtenerDescripcion();
    
    /**
     * Obtiene el número de iteraciones realizadas durante el cálculo
     * 
     * @return Número de iteraciones
     */
    int obtenerIteraciones();
    
    /**
     * Obtiene los caminos aumentantes encontrados durante la ejecución
     * 
     * @return Lista de caminos (cada camino es una lista de aristas)
     */
    List<List<Arista>> obtenerCaminosAumentantes();
}