package com.fluvial.modelo;

import java.util.*;

public class Grafo {
    
    private List<Nodo> nodos;
    private List<Arista> aristas;
    private Map<Nodo, List<Arista>> adyacencia;
    
    public Grafo() {
        this.nodos = new ArrayList<>();
        this.aristas = new ArrayList<>();
        this.adyacencia = new HashMap<>();
    }
    
    public boolean agregarNodo(Nodo nodo) {
        if (nodo == null || nodos.contains(nodo)) {
            return false;
        }
        nodos.add(nodo);
        adyacencia.put(nodo, new ArrayList<>());
        System.out.println("DEBUG: Nodo agregado: " + nodo.getNombre() + " (ID: " + nodo.getId() + ")");
        return true;
    }
    
    public boolean eliminarNodo(Nodo nodo) {
        if (!nodos.contains(nodo)) {
            return false;
        }
        
        List<Arista> aristasAEliminar = new ArrayList<>();
        for (Arista arista : aristas) {
            if (arista.getOrigen().equals(nodo) || arista.getDestino().equals(nodo)) {
                aristasAEliminar.add(arista);
            }
        }
        for (Arista arista : aristasAEliminar) {
            eliminarArista(arista);
        }
        
        nodos.remove(nodo);
        adyacencia.remove(nodo);
        return true;
    }
    
    public boolean agregarArista(Arista arista) {
        if (arista == null) {
            System.err.println("ERROR: Arista es null");
            return false;
        }
        
        if (aristas.contains(arista)) {
            System.err.println("ERROR: Arista ya existe: " + arista);
            return false;
        }
        
        Nodo origen = arista.getOrigen();
        Nodo destino = arista.getDestino();
        
        // Verificar que los nodos existen en el grafo
        if (!nodos.contains(origen)) {
            System.err.println("ERROR: Nodo origen no existe: " + origen.getNombre());
            return false;
        }
        if (!nodos.contains(destino)) {
            System.err.println("ERROR: Nodo destino no existe: " + destino.getNombre());
            return false;
        }
        
        // Agregar la arista
        aristas.add(arista);
        
        // ACTUALIZAR ADYACENCIA - CORREGIDO
        List<Arista> salientes = adyacencia.get(origen);
        if (salientes == null) {
            salientes = new ArrayList<>();
            adyacencia.put(origen, salientes);
        }
        salientes.add(arista);
        
        System.out.println("DEBUG: Arista agregada: " + origen.getNombre() + " -> " + destino.getNombre() + 
                          " (Capacidad: " + arista.getCapacidad() + ")");
        System.out.println("DEBUG: Adyacencia de " + origen.getNombre() + " tiene " + 
                          adyacencia.get(origen).size() + " aristas");
        
        return true;
    }
    
    public boolean eliminarArista(Arista arista) {
        if (!aristas.contains(arista)) {
            return false;
        }
        
        aristas.remove(arista);
        List<Arista> salientes = adyacencia.get(arista.getOrigen());
        if (salientes != null) {
            salientes.remove(arista);
        }
        return true;
    }
    
    public List<Arista> obtenerAristasSalientes(Nodo nodo) {
        List<Arista> resultado = adyacencia.get(nodo);
        if (resultado == null) {
            System.out.println("DEBUG: No hay adyacencia para " + nodo.getNombre());
            return new ArrayList<>();
        }
        System.out.println("DEBUG: " + nodo.getNombre() + " tiene " + resultado.size() + " aristas salientes");
        return new ArrayList<>(resultado);
    }
    
    public List<Arista> obtenerAristasEntrantes(Nodo nodo) {
        List<Arista> entrantes = new ArrayList<>();
        for (Arista arista : aristas) {
            if (arista.getDestino().equals(nodo)) {
                entrantes.add(arista);
            }
        }
        return entrantes;
    }
    
    public Arista obtenerArista(Nodo origen, Nodo destino) {
        for (Arista arista : obtenerAristasSalientes(origen)) {
            if (arista.getDestino().equals(destino)) {
                return arista;
            }
        }
        return null;
    }
    
    public boolean existeArista(Nodo origen, Nodo destino) {
        return obtenerArista(origen, destino) != null;
    }
    
    public List<Nodo> obtenerNodosPorTipo(String tipo) {
        List<Nodo> resultado = new ArrayList<>();
        for (Nodo nodo : nodos) {
            if (nodo.getTipo().equals(tipo)) {
                resultado.add(nodo);
            }
        }
        return resultado;
    }
    
    public List<Embalse> obtenerEmbalses() {
        List<Embalse> embalses = new ArrayList<>();
        for (Nodo nodo : nodos) {
            if (nodo instanceof Embalse) {
                embalses.add((Embalse) nodo);
            }
        }
        return embalses;
    }
    
    public List<Barrio> obtenerBarrios() {
        List<Barrio> barrios = new ArrayList<>();
        for (Nodo nodo : nodos) {
            if (nodo instanceof Barrio) {
                barrios.add((Barrio) nodo);
            }
        }
        return barrios;
    }
    
    public boolean esConexo() {
        if (nodos.isEmpty()) {
            return true;
        }
        
        Set<Nodo> visitados = new HashSet<>();
        Queue<Nodo> cola = new LinkedList<>();
        cola.add(nodos.get(0));
        visitados.add(nodos.get(0));
        
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            for (Arista arista : obtenerAristasSalientes(actual)) {
                Nodo vecino = arista.getDestino();
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.add(vecino);
                }
            }
        }
        
        return visitados.size() == nodos.size();
    }
    
    public double obtenerFlujoTotal() {
        double total = 0.0;
        for (Embalse embalse : obtenerEmbalses()) {
            total += embalse.getFlujoSalida();
        }
        return total;
    }
    
    public List<Nodo> getNodos() { return new ArrayList<>(nodos); }
    public List<Arista> getAristas() { return new ArrayList<>(aristas); }
    public Map<Nodo, List<Arista>> getAdyacencia() { return new HashMap<>(adyacencia); }
    
    public void limpiar() {
        nodos.clear();
        aristas.clear();
        adyacencia.clear();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GRAFO ===\n");
        sb.append("Nodos: ").append(nodos.size()).append("\n");
        sb.append("Aristas: ").append(aristas.size()).append("\n");
        sb.append("Embalses: ").append(obtenerEmbalses().size()).append("\n");
        sb.append("Barrios: ").append(obtenerBarrios().size()).append("\n");
        sb.append("\nAdyacencia:\n");
        for (Map.Entry<Nodo, List<Arista>> entry : adyacencia.entrySet()) {
            sb.append("  ").append(entry.getKey().getNombre()).append(" -> ");
            for (Arista a : entry.getValue()) {
                sb.append(a.getDestino().getNombre()).append("(").append(a.getCapacidad()).append(") ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}