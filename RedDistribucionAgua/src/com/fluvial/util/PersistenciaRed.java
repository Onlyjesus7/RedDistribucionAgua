package com.fluvial.util;

import com.fluvial.modelo.*;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PersistenciaRed {
    
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Nodo.class, new NodoAdapter())
        .registerTypeAdapter(Arista.class, new AristaAdapter())
        .create();
    
    /**
     * Guarda la red en un archivo JSON
     */
    public static boolean guardarRed(Grafo grafo, String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            DatosRed datos = new DatosRed();
            datos.nodos = grafo.getNodos();
            datos.aristas = grafo.getAristas();
            gson.toJson(datos, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Carga la red desde un archivo JSON
     */
    public static boolean cargarRed(Grafo grafo, String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            // Leer el JSON completo
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            
            // Obtener los arrays de nodos y aristas
            JsonArray nodosArray = jsonObject.getAsJsonArray("nodos");
            JsonArray aristasArray = jsonObject.getAsJsonArray("aristas");
            
            // Limpiar el grafo actual
            grafo.limpiar();
            
            // MAPA para guardar la relación ID -> Nodo
            Map<String, Nodo> mapaNodos = new HashMap<>();
            
            // ===== 1. RESTAURAR NODOS =====
            for (JsonElement elemento : nodosArray) {
                JsonObject obj = elemento.getAsJsonObject();
                Nodo nodo = deserializarNodo(obj);
                if (nodo != null) {
                    grafo.agregarNodo(nodo);
                    mapaNodos.put(nodo.getId(), nodo);
                    System.out.println("DEBUG: Nodo restaurado: " + nodo.getNombre() + " (ID: " + nodo.getId() + ")");
                }
            }
            
            // ===== 2. RESTAURAR ARISTAS =====
            for (JsonElement elemento : aristasArray) {
                JsonObject obj = elemento.getAsJsonObject();
                Arista arista = deserializarArista(obj, mapaNodos);
                if (arista != null) {
                    grafo.agregarArista(arista);
                    System.out.println("DEBUG: Arista restaurada: " + 
                        arista.getOrigen().getNombre() + " -> " + arista.getDestino().getNombre() + 
                        " (Capacidad: " + arista.getCapacidad() + ")");
                }
            }
            
            System.out.println("=== RED CARGADA CORRECTAMENTE ===");
            System.out.println("Nodos restaurados: " + grafo.getNodos().size());
            System.out.println("Aristas restauradas: " + grafo.getAristas().size());
            
            return true;
            
        } catch (IOException e) {
            System.err.println("ERROR: No se pudo leer el archivo");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("ERROR: Formato JSON invalido");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deserializa un nodo desde JSON
     */
    private static Nodo deserializarNodo(JsonObject obj) {
        String id = obj.get("id").getAsString();
        String nombre = obj.get("nombre").getAsString();
        String tipo = obj.get("tipo").getAsString();
        String estado = obj.has("estado") ? obj.get("estado").getAsString() : "ACTIVO";
        int posX = obj.has("posicionX") ? obj.get("posicionX").getAsInt() : 0;
        int posY = obj.has("posicionY") ? obj.get("posicionY").getAsInt() : 0;
        
        Nodo nodo = null;
        
        switch (tipo) {
            case "EMBALSE":
                double capMax = obj.has("capacidadMaxima") ? obj.get("capacidadMaxima").getAsDouble() : 300.0;
                double elevacion = obj.has("elevacion") ? obj.get("elevacion").getAsDouble() : 100.0;
                nodo = new Embalse(id, nombre, capMax, elevacion, posX, posY);
                if (obj.has("volumenActual")) {
                    ((Embalse) nodo).setVolumenActual(obj.get("volumenActual").getAsDouble());
                }
                break;
                
            case "BARRIO":
                int poblacion = obj.has("poblacion") ? obj.get("poblacion").getAsInt() : 1000;
                double demanda = obj.has("demanda") ? obj.get("demanda").getAsDouble() : 100.0;
                double presion = obj.has("presion") ? obj.get("presion").getAsDouble() : 3.0;
                int prioridad = obj.has("prioridad") ? obj.get("prioridad").getAsInt() : 5;
                nodo = new Barrio(id, nombre, poblacion, demanda, presion, prioridad, posX, posY);
                break;
                
            case "CONEXION":
                String tipoConexion = obj.has("tipoConexion") ? obj.get("tipoConexion").getAsString() : "UNION";
                double pres = obj.has("presion") ? obj.get("presion").getAsDouble() : 3.0;
                double capMaxima = obj.has("capacidadMaxima") ? obj.get("capacidadMaxima").getAsDouble() : 200.0;
                nodo = new Conexion(id, nombre, tipoConexion, pres, capMaxima, posX, posY);
                if (obj.has("eficiencia")) {
                    ((Conexion) nodo).setEficiencia(obj.get("eficiencia").getAsDouble());
                }
                break;
                
            default:
                System.err.println("ERROR: Tipo de nodo desconocido: " + tipo);
                return null;
        }
        
        if (nodo != null) {
            nodo.setEstado(estado);
        }
        return nodo;
    }
    
    /**
     * Deserializa una arista desde JSON
     */
    private static Arista deserializarArista(JsonObject obj, Map<String, Nodo> mapaNodos) {
        String idOrigen = obj.get("idOrigen").getAsString();
        String idDestino = obj.get("idDestino").getAsString();
        double capacidad = obj.has("capacidad") ? obj.get("capacidad").getAsDouble() : 150.0;
        double flujo = obj.has("flujo") ? obj.get("flujo").getAsDouble() : 0.0;
        String estado = obj.has("estado") ? obj.get("estado").getAsString() : "ACTIVA";
        double longitud = obj.has("longitud") ? obj.get("longitud").getAsDouble() : 0.0;
        double diametro = obj.has("diametro") ? obj.get("diametro").getAsDouble() : 0.0;
        
        Nodo origen = mapaNodos.get(idOrigen);
        Nodo destino = mapaNodos.get(idDestino);
        
        if (origen == null) {
            System.err.println("ERROR: Nodo origen no encontrado: " + idOrigen);
            return null;
        }
        if (destino == null) {
            System.err.println("ERROR: Nodo destino no encontrado: " + idDestino);
            return null;
        }
        
        Arista arista = new Arista(origen, destino, capacidad, longitud, diametro);
        arista.setFlujo(flujo);
        arista.setEstado(estado);
        
        return arista;
    }
    
    // ===== CLASES AUXILIARES PARA SERIALIZACIÓN =====
    
    private static class DatosRed {
        List<Nodo> nodos = new ArrayList<>();
        List<Arista> aristas = new ArrayList<>();
    }
    
    /**
     * Adaptador para serializar/deserializar Nodo
     */
    private static class NodoAdapter implements JsonSerializer<Nodo>, JsonDeserializer<Nodo> {
        
        @Override
        public JsonElement serialize(Nodo nodo, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", nodo.getId());
            obj.addProperty("nombre", nodo.getNombre());
            obj.addProperty("tipo", nodo.getTipo());
            obj.addProperty("estado", nodo.getEstado());
            obj.addProperty("posicionX", nodo.getPosicionX());
            obj.addProperty("posicionY", nodo.getPosicionY());
            
            if (nodo instanceof Embalse) {
                Embalse e = (Embalse) nodo;
                obj.addProperty("capacidadMaxima", e.getCapacidadMaxima());
                obj.addProperty("elevacion", e.getElevacion());
                obj.addProperty("volumenActual", e.getVolumenActual());
            } else if (nodo instanceof Barrio) {
                Barrio b = (Barrio) nodo;
                obj.addProperty("poblacion", b.getPoblacion());
                obj.addProperty("demanda", b.getDemanda());
                obj.addProperty("presion", b.getPresion());
                obj.addProperty("prioridad", b.getPrioridad());
            } else if (nodo instanceof Conexion) {
                Conexion c = (Conexion) nodo;
                obj.addProperty("tipoConexion", c.getTipoConexion());
                obj.addProperty("presion", c.getPresion());
                obj.addProperty("capacidadMaxima", c.getCapacidadMaxima());
                obj.addProperty("eficiencia", c.getEficiencia());
            }
            return obj;
        }
        
        @Override
        public Nodo deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            // Este método no se usa directamente, usamos deserializarNodo()
            return null;
        }
    }
    
    /**
     * Adaptador para serializar Arista
     */
    private static class AristaAdapter implements JsonSerializer<Arista>, JsonDeserializer<Arista> {
        
        @Override
        public JsonElement serialize(Arista arista, Type type, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idOrigen", arista.getOrigen().getId());
            obj.addProperty("idDestino", arista.getDestino().getId());
            obj.addProperty("capacidad", arista.getCapacidad());
            obj.addProperty("flujo", arista.getFlujo());
            obj.addProperty("estado", arista.getEstado());
            obj.addProperty("longitud", arista.getLongitud());
            obj.addProperty("diametro", arista.getDiametro());
            return obj;
        }
        
        @Override
        public Arista deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            // Este método no se usa directamente, usamos deserializarArista()
            return null;
        }
    }
}