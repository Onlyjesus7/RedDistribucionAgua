package com.fluvial.algoritmo;

import com.fluvial.modelo.Grafo;
import com.fluvial.modelo.Nodo;
import com.fluvial.modelo.Arista;
import com.fluvial.modelo.Embalse;
import com.fluvial.modelo.Barrio;
import com.fluvial.modelo.Conexion;
import java.util.*;

public class FordFulkerson implements AlgoritmoFlujoMaximo {
    
    private int iteraciones;
    private List<List<Arista>> caminosAumentantes;
    
    public FordFulkerson() {
        this.iteraciones = 0;
        this.caminosAumentantes = new ArrayList<>();
    }
    
    @Override
    public double calcularFlujoMaximo(Grafo grafo, Nodo fuente, Nodo sumidero) {
        if (grafo == null || fuente == null || sumidero == null) {
            throw new IllegalArgumentException("El grafo, la fuente y el sumidero no pueden ser nulos");
        }
        
        System.out.println("\n=== FORD-FULKERSON ===");
        System.out.println("Fuente: " + fuente.getNombre() + " (ID: " + fuente.getId() + ")");
        System.out.println("Sumidero: " + sumidero.getNombre() + " (ID: " + sumidero.getId() + ")");
        System.out.println("Nodos en grafo: " + grafo.getNodos().size());
        System.out.println("Aristas en grafo: " + grafo.getAristas().size());
        
        // VERIFICAR ADYACENCIA DEL GRAFO
        System.out.println("\n--- VERIFICANDO ADYACENCIA ---");
        for (Nodo nodo : grafo.getNodos()) {
            List<Arista> salientes = grafo.obtenerAristasSalientes(nodo);
            System.out.println("  " + nodo.getNombre() + " tiene " + salientes.size() + " aristas salientes");
            for (Arista a : salientes) {
                System.out.println("    -> " + a.getDestino().getNombre() + " (cap: " + a.getCapacidad() + ", residual: " + a.capacidadResidual() + ")");
            }
        }
        System.out.println("-----------------------------\n");
        
        this.iteraciones = 0;
        this.caminosAumentantes = new ArrayList<>();
        
        double flujoMaximo = 0.0;
        Map<Nodo, Arista> padre = new HashMap<>();
        
        while (true) {
            System.out.println("\n--- Iteración " + (iteraciones + 1) + " ---");
            padre.clear();
            
            // BFS para encontrar camino
            boolean encontrado = bfs(grafo, fuente, sumidero, padre);
            
            if (!encontrado) {
                System.out.println("No se encontraron más caminos aumentantes");
                break;
            }
            
            // Encontrar capacidad mínima
            double capacidadMinima = Double.MAX_VALUE;
            Nodo actual = sumidero;
            List<Arista> camino = new ArrayList<>();
            
            while (padre.containsKey(actual)) {
                Arista arista = padre.get(actual);
                camino.add(arista);
                double residual = arista.capacidadResidual();
                capacidadMinima = Math.min(capacidadMinima, residual);
                actual = arista.getOrigen();
            }
            
            System.out.println("Capacidad mínima: " + capacidadMinima);
            
            if (capacidadMinima <= 0) {
                break;
            }
            
            // Actualizar flujos
            for (Arista arista : camino) {
                arista.aumentarFlujo(capacidadMinima);
                System.out.println("  Actualizado: " + arista.getOrigen().getNombre() + 
                                  " -> " + arista.getDestino().getNombre() + 
                                  " (Flujo: " + arista.getFlujo() + "/" + arista.getCapacidad() + ")");
            }
            
            flujoMaximo += capacidadMinima;
            iteraciones++;
            caminosAumentantes.add(camino);
            
            System.out.println("Flujo acumulado: " + flujoMaximo);
        }
        
        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.println("Flujo máximo: " + flujoMaximo);
        System.out.println("Iteraciones: " + iteraciones);
        
        return flujoMaximo;
    }
    
    private boolean bfs(Grafo grafo, Nodo fuente, Nodo sumidero, Map<Nodo, Arista> padre) {
        System.out.println("  BFS: Buscando camino desde " + fuente.getNombre() + " hasta " + sumidero.getNombre());
        
        Set<Nodo> visitados = new HashSet<>();
        Queue<Nodo> cola = new LinkedList<>();
        cola.add(fuente);
        visitados.add(fuente);
        
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            System.out.println("    Visitando: " + actual.getNombre());
            
            if (actual.equals(sumidero)) {
                System.out.println("    ¡Camino encontrado!");
                return true;
            }
            
            // OBTENER ARISTAS SALIENTES - VERIFICAR QUE NO ESTÉ VACÍO
            List<Arista> salientes = grafo.obtenerAristasSalientes(actual);
            System.out.println("    Aristas salientes de " + actual.getNombre() + ": " + salientes.size());
            
            for (Arista arista : salientes) {
                double residual = arista.capacidadResidual();
                Nodo vecino = arista.getDestino();
                
                System.out.println("      Evaluando: " + vecino.getNombre() + " (residual: " + residual + ")");
                
                if (residual > 0 && !visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padre.put(vecino, arista);
                    cola.add(vecino);
                    System.out.println("      -> Agregando a cola: " + vecino.getNombre());
                }
            }
        }
        
        System.out.println("    No se encontró camino");
        return false;
    }
    
    @Override
    public String obtenerNombre() {
        return "Ford-Fulkerson (Edmonds-Karp)";
    }
    
    @Override
    public String obtenerDescripcion() {
        return "Algoritmo de flujo máximo con BFS";
    }
    
    @Override
    public int obtenerIteraciones() {
        return iteraciones;
    }
    
    @Override
    public List<List<Arista>> obtenerCaminosAumentantes() {
        return new ArrayList<>(caminosAumentantes);
    }
}