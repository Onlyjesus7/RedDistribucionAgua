package com.fluvial.controlador;

import com.fluvial.modelo.*;
import com.fluvial.algoritmo.*;
import java.util.*;

public class ControladorRed {
    
    private Grafo grafo;
    private AlgoritmoFlujoMaximo algoritmo;
    private double flujoMaximoCalculado;
    private Embalse fuenteActual;
    private Barrio sumideroActual;
    private Map<Barrio, Double> flujoPorBarrio;
    private Map<Embalse, Double> flujoPorEmbalse;
    
    public ControladorRed() {
        this.grafo = new Grafo();
        this.algoritmo = new FordFulkerson();
        this.flujoMaximoCalculado = 0.0;
        this.fuenteActual = null;
        this.sumideroActual = null;
        this.flujoPorBarrio = new HashMap<>();
        this.flujoPorEmbalse = new HashMap<>();
    }
    
    // ==================== MÉTODOS PARA AGREGAR NODOS ====================
    
    public Embalse agregarEmbalse(String id, String nombre, double capacidadMaxima,
                                  double elevacion, int x, int y) {
        Embalse embalse = new Embalse(id, nombre, capacidadMaxima, elevacion, x, y);
        if (grafo.agregarNodo(embalse)) {
            return embalse;
        }
        return null;
    }
    
    public Barrio agregarBarrio(String id, String nombre, int poblacion,
                                double demanda, double presion, int prioridad,
                                int x, int y) {
        Barrio barrio = new Barrio(id, nombre, poblacion, demanda, presion, prioridad, x, y);
        if (grafo.agregarNodo(barrio)) {
            return barrio;
        }
        return null;
    }
    
    public Conexion agregarConexion(String id, String nombre, String tipoConexion,
                                    double presion, double capacidadMaxima,
                                    int x, int y) {
        Conexion conexion = new Conexion(id, nombre, tipoConexion, presion, 
                                         capacidadMaxima, x, y);
        if (grafo.agregarNodo(conexion)) {
            return conexion;
        }
        return null;
    }
    
    public Arista agregarTuberia(String idOrigen, String idDestino, 
                                 double capacidad, double longitud, double diametro) {
        Nodo origen = buscarNodoPorId(idOrigen);
        Nodo destino = buscarNodoPorId(idDestino);
        
        if (origen == null || destino == null) {
            System.err.println("ERROR: Nodo origen o destino no encontrado: " + idOrigen + " -> " + idDestino);
            return null;
        }
        
        if (capacidad <= 0) {
            capacidad = 150;
        }
        
        Arista arista = new Arista(origen, destino, capacidad, longitud, diametro);
        if (grafo.agregarArista(arista)) {
            return arista;
        }
        return null;
    }
    
    public Nodo buscarNodoPorId(String id) {
        for (Nodo nodo : grafo.getNodos()) {
            if (nodo.getId().equals(id)) {
                return nodo;
            }
        }
        return null;
    }
    
    // ==================== VALIDACIÓN DE RED ====================
    
    public boolean validarRed() {
        List<Embalse> embalses = grafo.obtenerEmbalses();
        List<Barrio> barrios = grafo.obtenerBarrios();
        
        if (embalses.isEmpty()) {
            System.err.println("ERROR: No hay embalses en la red");
            return false;
        }
        
        if (barrios.isEmpty()) {
            System.err.println("ERROR: No hay barrios en la red");
            return false;
        }
        
        if (grafo.getAristas().isEmpty()) {
            System.err.println("ERROR: No hay tuberias en la red");
            return false;
        }
        
        for (Arista arista : grafo.getAristas()) {
            if (arista.getCapacidad() <= 0) {
                System.err.println("ERROR: Tuberia con capacidad 0: " + arista);
                return false;
            }
        }
        
        boolean hayCamino = false;
        for (Embalse e : embalses) {
            for (Barrio b : barrios) {
                if (existeCamino(e, b)) {
                    hayCamino = true;
                    break;
                }
            }
            if (hayCamino) break;
        }
        
        if (!hayCamino) {
            System.err.println("ERROR: No hay camino desde ningun embalse a ningun barrio");
            return false;
        }
        
        return true;
    }
    
    private boolean existeCamino(Nodo origen, Nodo destino) {
        Set<Nodo> visitados = new HashSet<>();
        Queue<Nodo> cola = new LinkedList<>();
        cola.add(origen);
        visitados.add(origen);
        
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            if (actual.equals(destino)) {
                return true;
            }
            for (Arista arista : grafo.obtenerAristasSalientes(actual)) {
                Nodo vecino = arista.getDestino();
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.add(vecino);
                }
            }
        }
        return false;
    }
    
    // ==================== CÁLCULO DE FLUJO TOTAL DE LA RED ====================
    
    public double calcularFlujoMaximo() {
        List<Embalse> embalses = grafo.obtenerEmbalses();
        List<Barrio> barrios = grafo.obtenerBarrios();
        
        System.out.println("=== CALCULANDO FLUJO TOTAL DE LA RED ===");
        System.out.println("Embalses: " + embalses.size());
        System.out.println("Barrios: " + barrios.size());
        System.out.println("Tuberias: " + grafo.getAristas().size());
        
        if (embalses.isEmpty() || barrios.isEmpty()) {
            return -1;
        }
        
        // Reiniciar flujos
        for (Arista a : grafo.getAristas()) {
            a.setFlujo(0.0);
        }
        
        for (Barrio b : barrios) {
            b.setFlujoRecibido(0.0);
        }
        
        for (Embalse e : embalses) {
            e.setFlujoSalida(0.0);
        }
        
        flujoPorBarrio.clear();
        flujoPorEmbalse.clear();
        
        double flujoTotal = 0.0;
        int caminosEncontrados = 0;
        
        // Para cada embalse, calcular flujo a cada barrio
        for (Embalse fuente : embalses) {
            double flujoEmbalse = 0.0;
            
            for (Barrio sumidero : barrios) {
                if (existeCamino(fuente, sumidero)) {
                    // Guardar estado actual de flujos
                    Map<Arista, Double> flujosAnteriores = new HashMap<>();
                    for (Arista a : grafo.getAristas()) {
                        flujosAnteriores.put(a, a.getFlujo());
                    }
                    
                    double flujo = algoritmo.calcularFlujoMaximo(grafo, fuente, sumidero);
                    
                    if (flujo > 0) {
                        flujoTotal += flujo;
                        flujoEmbalse += flujo;
                        caminosEncontrados++;
                        System.out.println("  " + fuente.getNombre() + " -> " + sumidero.getNombre() + 
                                          ": " + String.format("%.2f", flujo) + " m³/h");
                    } else {
                        // Restaurar flujos si no hay camino
                        for (Map.Entry<Arista, Double> entry : flujosAnteriores.entrySet()) {
                            entry.getKey().setFlujo(entry.getValue());
                        }
                    }
                }
            }
            
            fuente.setFlujoSalida(flujoEmbalse);
            flujoPorEmbalse.put(fuente, flujoEmbalse);
            System.out.println("  Flujo total de " + fuente.getNombre() + ": " + 
                              String.format("%.2f", flujoEmbalse) + " m³/h");
        }
        
        // Actualizar flujo recibido por los barrios
        for (Barrio barrio : barrios) {
            double flujoRecibido = 0.0;
            for (Arista arista : grafo.obtenerAristasEntrantes(barrio)) {
                flujoRecibido += arista.getFlujo();
            }
            barrio.setFlujoRecibido(flujoRecibido);
            flujoPorBarrio.put(barrio, flujoRecibido);
            System.out.println("  " + barrio.getNombre() + " recibe: " + 
                              String.format("%.2f", flujoRecibido) + "/" + 
                              barrio.getDemanda() + " m³/h (" +
                              String.format("%.1f", barrio.calcularCobertura()) + "%)");
        }
        
        this.flujoMaximoCalculado = flujoTotal;
        System.out.println("\n=== FLUJO TOTAL DE LA RED ===");
        System.out.println("Flujo total: " + String.format("%.2f", flujoTotal) + " m³/h");
        System.out.println("Caminos encontrados: " + caminosEncontrados);
        
        return flujoTotal;
    }
    
    public double calcularFlujoMaximo(String idEmbalse, String idBarrio) {
        Nodo embalse = buscarNodoPorId(idEmbalse);
        Nodo barrio = buscarNodoPorId(idBarrio);
        
        if (embalse == null || barrio == null) {
            return -1;
        }
        
        if (!(embalse instanceof Embalse) || !(barrio instanceof Barrio)) {
            return -1;
        }
        
        this.fuenteActual = (Embalse) embalse;
        this.sumideroActual = (Barrio) barrio;
        
        // Reiniciar flujos
        for (Arista a : grafo.getAristas()) {
            a.setFlujo(0.0);
        }
        
        this.flujoMaximoCalculado = algoritmo.calcularFlujoMaximo(
            grafo, embalse, barrio
        );
        
        this.fuenteActual.setFlujoSalida(this.flujoMaximoCalculado);
        this.sumideroActual.setFlujoRecibido(this.flujoMaximoCalculado);
        
        return this.flujoMaximoCalculado;
    }
    
    // ==================== REPORTES ====================
    
    public String generarInformeRed() {
        StringBuilder informe = new StringBuilder();
        informe.append("=== INFORME DE LA RED ===\n\n");
        informe.append("Nodos totales: ").append(grafo.getNodos().size()).append("\n");
        informe.append("Tuberias: ").append(grafo.getAristas().size()).append("\n");
        informe.append("Embalses: ").append(grafo.obtenerEmbalses().size()).append("\n");
        informe.append("Barrios: ").append(grafo.obtenerBarrios().size()).append("\n\n");
        
        informe.append("EMBALSES:\n");
        for (Embalse e : grafo.obtenerEmbalses()) {
            informe.append("  ").append(e.getNombre()).append(": ")
                   .append(e.getCapacidadMaxima()).append(" m³/h, ")
                   .append("Flujo: ").append(e.getFlujoSalida()).append(" m³/h\n");
        }
        
        informe.append("\nBARRIOS:\n");
        for (Barrio b : grafo.obtenerBarrios()) {
            informe.append("  ").append(b.getNombre()).append(": ")
                   .append(b.getDemanda()).append(" m³/h demandados, ")
                   .append("Recibe: ").append(b.getFlujoRecibido()).append(" m³/h (")
                   .append(String.format("%.1f", b.calcularCobertura())).append("%)\n");
        }
        
        informe.append("\nTUBERIAS:\n");
        for (Arista a : grafo.getAristas()) {
            informe.append("  ").append(a.toString()).append("\n");
        }
        
        if (flujoMaximoCalculado > 0) {
            informe.append("\nFLUJO MAXIMO TOTAL: ").append(flujoMaximoCalculado).append(" m³/h\n");
        }
        
        return informe.toString();
    }
    
    // ==================== GETTERS ====================
    
    public Grafo getGrafo() { return grafo; }
    public AlgoritmoFlujoMaximo getAlgoritmo() { return algoritmo; }
    public double getFlujoMaximoCalculado() { return flujoMaximoCalculado; }
    public Embalse getFuenteActual() { return fuenteActual; }
    public Barrio getSumideroActual() { return sumideroActual; }
    public Map<Barrio, Double> getFlujoPorBarrio() { return flujoPorBarrio; }
    public Map<Embalse, Double> getFlujoPorEmbalse() { return flujoPorEmbalse; }
    
    public void setAlgoritmo(AlgoritmoFlujoMaximo algoritmo) {
        this.algoritmo = algoritmo;
    }
    
    public void limpiarRed() {
        grafo.limpiar();
        flujoMaximoCalculado = 0.0;
        fuenteActual = null;
        sumideroActual = null;
        flujoPorBarrio.clear();
        flujoPorEmbalse.clear();
    }
}