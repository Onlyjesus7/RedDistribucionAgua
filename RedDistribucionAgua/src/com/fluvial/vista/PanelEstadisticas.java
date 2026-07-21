package com.fluvial.vista;

import com.fluvial.controlador.ControladorRed;
import com.fluvial.modelo.*;
import javax.swing.*;
import java.awt.*;

public class PanelEstadisticas extends JPanel {
    
    private ControladorRed controlador;
    private JTextArea areaEstadisticas;
    
    public PanelEstadisticas(ControladorRed controlador) {
        this.controlador = controlador;
        
        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 45));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titulo = new JLabel("Estadisticas de la Red");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);
        
        areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaEstadisticas.setBackground(new Color(40, 40, 55));
        areaEstadisticas.setForeground(new Color(200, 200, 220));
        areaEstadisticas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(areaEstadisticas);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(40, 40, 55));
        add(scrollPane, BorderLayout.CENTER);
        
        actualizarEstadisticas();
    }
    
    public void actualizarEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("          ESTADISTICAS DE LA RED\n");
        sb.append("================================================\n\n");
        
        Grafo grafo = controlador.getGrafo();
        sb.append("  ESTADISTICAS GENERALES\n");
        sb.append("  ----------------------\n");
        sb.append("  Nodos totales: ").append(grafo.getNodos().size()).append("\n");
        sb.append("  Tuberias: ").append(grafo.getAristas().size()).append("\n");
        sb.append("  Embalses: ").append(grafo.obtenerEmbalses().size()).append("\n");
        sb.append("  Barrios: ").append(grafo.obtenerBarrios().size()).append("\n");
        sb.append("  Grafo conexo: ").append(grafo.esConexo() ? "SI" : "NO").append("\n\n");
        
        sb.append("  EMBALSES\n");
        sb.append("  --------\n");
        for (Embalse e : grafo.obtenerEmbalses()) {
            double flujo = e.getFlujoSalida();
            double cap = e.getCapacidadMaxima();
            double uso = cap > 0 ? (flujo / cap) * 100 : 0;
            String estado = uso > 90 ? "SOBRECARGADO" : (uso > 70 ? "ALTA CARGA" : "OPTIMO");
            sb.append(String.format("  %s: %.1f/%.1f m³/h (%.1f%%) - %s\n",
                e.getNombre(), flujo, cap, uso, estado));
        }
        sb.append("\n");
        
        sb.append("  BARRIOS - COBERTURA\n");
        sb.append("  -------------------\n");
        for (Barrio b : grafo.obtenerBarrios()) {
            double rec = b.getFlujoRecibido();
            double dem = b.getDemanda();
            double cob = dem > 0 ? (rec / dem) * 100 : 0;
            String estado;
            if (cob >= 100) {
                estado = "SATISFECHO";
            } else if (cob >= 70) {
                estado = "PARCIAL";
            } else if (cob >= 30) {
                estado = "DEFICIT";
            } else {
                estado = "CRITICO";
            }
            String barra = generarBarraProgreso(cob, 20);
            sb.append(String.format("  %s: %s\n", b.getNombre(), barra));
            sb.append(String.format("    %.1f/%.1f m³/h (%.1f%%) - %s\n",
                rec, dem, cob, estado));
        }
        sb.append("\n");
        
        sb.append("  TUBERIAS CRITICAS\n");
        sb.append("  -----------------\n");
        boolean hayCriticas = false;
        for (Arista a : grafo.getAristas()) {
            double uso = a.calcularPorcentajeUso();
            if (uso > 80) {
                hayCriticas = true;
                sb.append(String.format("  %s -> %s: %.1f%%\n",
                    a.getOrigen().getNombre(),
                    a.getDestino().getNombre(),
                    uso));
            }
        }
        if (!hayCriticas) {
            sb.append("  No se detectaron tuberias criticas.\n");
        }
        sb.append("\n");
        
        double flujoMax = controlador.getFlujoMaximoCalculado();
        if (flujoMax > 0) {
            sb.append("  FLUJO MAXIMO TOTAL\n");
            sb.append("  ------------------\n");
            sb.append("  ").append(String.format("%.2f", flujoMax)).append(" m³/h\n");
            sb.append("  Algoritmo: ").append(controlador.getAlgoritmo().obtenerNombre()).append("\n");
            sb.append("  Iteraciones: ").append(controlador.getAlgoritmo().obtenerIteraciones()).append("\n");
        }
        
        sb.append("\n================================================\n");
        areaEstadisticas.setText(sb.toString());
    }
    
    private String generarBarraProgreso(double porcentaje, int longitud) {
        int filled = (int) Math.round((porcentaje / 100.0) * longitud);
        filled = Math.max(0, Math.min(longitud, filled));
        
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < longitud; i++) {
            if (i < filled) {
                if (porcentaje >= 100) {
                    barra.append("█");
                } else if (porcentaje >= 70) {
                    barra.append("▓");
                } else if (porcentaje >= 30) {
                    barra.append("▒");
                } else {
                    barra.append("░");
                }
            } else {
                barra.append(" ");
            }
        }
        barra.append("]");
        return barra.toString();
    }
}