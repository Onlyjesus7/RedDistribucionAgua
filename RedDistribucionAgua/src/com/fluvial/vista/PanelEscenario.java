package com.fluvial.vista;

import com.fluvial.controlador.ControladorRed;
import com.fluvial.modelo.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PanelEscenario extends JDialog {
    
    private ControladorRed controlador;
    private Grafo grafoOriginal;
    private JPanel panelConfiguracion;
    private JTextArea areaResultados;
    private JComboBox<String> cmbEscenarios;
    private JButton btnEjecutar;
    private JButton btnRestaurar;
    private JPanel panelConfigDinamico;
    
    public PanelEscenario(JFrame parent, ControladorRed controlador) {
        super(parent, "Simulador de Escenarios - Que pasaria si...", true);
        this.controlador = controlador;
        this.grafoOriginal = copiarGrafo(controlador.getGrafo());
        
        setSize(750, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(35, 35, 45));
        
        inicializarComponentes();
        setVisible(true);
    }
    
    private void inicializarComponentes() {
        // Panel superior: Título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(45, 45, 60));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Simulador de Escenarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        
        JLabel lblSubTitulo = new JLabel("Que pasaria si...");
        lblSubTitulo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubTitulo.setForeground(new Color(180, 200, 255));
        panelTitulo.add(lblSubTitulo, BorderLayout.EAST);
        
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel de selección
        JPanel panelSeleccion = new JPanel(new BorderLayout());
        panelSeleccion.setBackground(new Color(45, 45, 60));
        panelSeleccion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(60, 60, 80)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblSeleccion = new JLabel("Seleccione un escenario:");
        lblSeleccion.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSeleccion.setForeground(Color.WHITE);
        panelSeleccion.add(lblSeleccion, BorderLayout.WEST);
        
        JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelOpciones.setBackground(new Color(45, 45, 60));
        
        cmbEscenarios = new JComboBox<>(new String[]{
            "Falla de Tuberia",
            "Aumento de Demanda",
            "Nuevo Barrio",
            "Reduccion de Capacidad",
            "Mantenimiento Programado"
        });
        cmbEscenarios.setBackground(new Color(200, 200, 220));
        cmbEscenarios.setForeground(Color.BLACK);
        cmbEscenarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEscenarios.setPreferredSize(new Dimension(200, 35));
        cmbEscenarios.addActionListener(e -> actualizarConfiguracion());
        panelOpciones.add(cmbEscenarios);
        
        btnEjecutar = crearBoton("Ejecutar Simulacion", new Color(150, 200, 255));
        btnEjecutar.addActionListener(e -> ejecutarEscenario());
        panelOpciones.add(btnEjecutar);
        
        btnRestaurar = crearBoton("Restaurar Original", new Color(255, 200, 150));
        btnRestaurar.addActionListener(e -> restaurarOriginal());
        panelOpciones.add(btnRestaurar);
        
        panelSeleccion.add(panelOpciones, BorderLayout.CENTER);
        add(panelSeleccion, BorderLayout.NORTH);
        
        // Panel de configuración
        panelConfiguracion = new JPanel(new BorderLayout());
        panelConfiguracion.setBackground(new Color(45, 45, 60));
        panelConfiguracion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 80)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panelConfiguracion.setPreferredSize(new Dimension(0, 140));
        
        JLabel lblConfig = new JLabel("Configuracion del Escenario");
        lblConfig.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfig.setForeground(Color.WHITE);
        panelConfiguracion.add(lblConfig, BorderLayout.NORTH);
        
        panelConfigDinamico = new JPanel(new GridBagLayout());
        panelConfigDinamico.setBackground(new Color(45, 45, 60));
        panelConfiguracion.add(panelConfigDinamico, BorderLayout.CENTER);
        
        add(panelConfiguracion, BorderLayout.CENTER);
        
        // Panel de resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(new Color(45, 45, 60));
        panelResultados.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelResultados.setPreferredSize(new Dimension(0, 250));
        
        JLabel lblResultados = new JLabel("Resultados de la Simulacion");
        lblResultados.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResultados.setForeground(Color.WHITE);
        panelResultados.add(lblResultados, BorderLayout.NORTH);
        
        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaResultados.setBackground(new Color(40, 40, 55));
        areaResultados.setForeground(new Color(200, 200, 220));
        areaResultados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        areaResultados.setText("Seleccione un escenario y presione 'Ejecutar Simulacion'");
        
        JScrollPane scroll = new JScrollPane(areaResultados);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(40, 40, 55));
        panelResultados.add(scroll, BorderLayout.CENTER);
        
        add(panelResultados, BorderLayout.SOUTH);
        
        SwingUtilities.invokeLater(() -> actualizarConfiguracion());
    }
    
    private void actualizarConfiguracion() {
        if (panelConfigDinamico == null) {
            return;
        }
        
        panelConfigDinamico.removeAll();
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        String escenario = (String) cmbEscenarios.getSelectedItem();
        if (escenario == null) {
            escenario = "Falla de Tuberia";
        }
        
        int y = 0;
        
        switch (escenario) {
            case "Falla de Tuberia":
                gbc.gridy = y++; gbc.gridx = 0;
                JLabel lbl1 = new JLabel("Seleccione tuberia a fallar:");
                lbl1.setForeground(Color.WHITE);
                lbl1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panelConfigDinamico.add(lbl1, gbc);
                gbc.gridx = 1;
                JComboBox<String> cmbTuberias = new JComboBox<>();
                for (Arista a : controlador.getGrafo().getAristas()) {
                    cmbTuberias.addItem(a.getOrigen().getNombre() + " -> " + a.getDestino().getNombre());
                }
                if (cmbTuberias.getItemCount() == 0) {
                    cmbTuberias.addItem("No hay tuberias disponibles");
                }
                cmbTuberias.setBackground(new Color(60, 60, 80));
                cmbTuberias.setForeground(Color.WHITE);
                cmbTuberias.setPreferredSize(new Dimension(250, 30));
                panelConfigDinamico.add(cmbTuberias, gbc);
                panelConfigDinamico.putClientProperty("cmbTuberias", cmbTuberias);
                break;
                
            case "Aumento de Demanda":
                gbc.gridy = y++; gbc.gridx = 0;
                JLabel lbl2 = new JLabel("Porcentaje de aumento:");
                lbl2.setForeground(Color.WHITE);
                lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panelConfigDinamico.add(lbl2, gbc);
                gbc.gridx = 1;
                JSpinner spinnerDemanda = new JSpinner(new SpinnerNumberModel(20, 5, 100, 5));
                spinnerDemanda.setBackground(new Color(60, 60, 80));
                spinnerDemanda.setForeground(Color.WHITE);
                spinnerDemanda.setPreferredSize(new Dimension(100, 30));
                panelConfigDinamico.add(spinnerDemanda, gbc);
                panelConfigDinamico.putClientProperty("spinnerDemanda", spinnerDemanda);
                break;
                
            case "Nuevo Barrio":
                gbc.gridy = y++; gbc.gridx = 0;
                JLabel lbl3 = new JLabel("Demanda del nuevo barrio (m³/h):");
                lbl3.setForeground(Color.WHITE);
                lbl3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panelConfigDinamico.add(lbl3, gbc);
                gbc.gridx = 1;
                JTextField txtDemanda = new JTextField("100");
                txtDemanda.setBackground(new Color(60, 60, 80));
                txtDemanda.setForeground(Color.WHITE);
                txtDemanda.setPreferredSize(new Dimension(100, 30));
                panelConfigDinamico.add(txtDemanda, gbc);
                panelConfigDinamico.putClientProperty("txtDemanda", txtDemanda);
                break;
                
            case "Reduccion de Capacidad":
                gbc.gridy = y++; gbc.gridx = 0;
                JLabel lbl4 = new JLabel("Porcentaje de reduccion:");
                lbl4.setForeground(Color.WHITE);
                lbl4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panelConfigDinamico.add(lbl4, gbc);
                gbc.gridx = 1;
                JSpinner spinnerReduccion = new JSpinner(new SpinnerNumberModel(30, 5, 100, 5));
                spinnerReduccion.setBackground(new Color(60, 60, 80));
                spinnerReduccion.setForeground(Color.WHITE);
                spinnerReduccion.setPreferredSize(new Dimension(100, 30));
                panelConfigDinamico.add(spinnerReduccion, gbc);
                panelConfigDinamico.putClientProperty("spinnerReduccion", spinnerReduccion);
                break;
                
            case "Mantenimiento Programado":
                gbc.gridy = y++; gbc.gridx = 0;
                JLabel lbl5 = new JLabel("Seleccione tuberia en mantenimiento:");
                lbl5.setForeground(Color.WHITE);
                lbl5.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panelConfigDinamico.add(lbl5, gbc);
                gbc.gridx = 1;
                JComboBox<String> cmbMantenimiento = new JComboBox<>();
                for (Arista a : controlador.getGrafo().getAristas()) {
                    cmbMantenimiento.addItem(a.getOrigen().getNombre() + " -> " + a.getDestino().getNombre());
                }
                if (cmbMantenimiento.getItemCount() == 0) {
                    cmbMantenimiento.addItem("No hay tuberias disponibles");
                }
                cmbMantenimiento.setBackground(new Color(60, 60, 80));
                cmbMantenimiento.setForeground(Color.WHITE);
                cmbMantenimiento.setPreferredSize(new Dimension(250, 30));
                panelConfigDinamico.add(cmbMantenimiento, gbc);
                panelConfigDinamico.putClientProperty("cmbMantenimiento", cmbMantenimiento);
                
                gbc.gridy = y++; gbc.gridx = 0;
                JLabel lbl6 = new JLabel("Duracion (horas):");
                lbl6.setForeground(Color.WHITE);
                lbl6.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panelConfigDinamico.add(lbl6, gbc);
                gbc.gridx = 1;
                JSpinner spinnerDuracion = new JSpinner(new SpinnerNumberModel(4, 1, 24, 1));
                spinnerDuracion.setBackground(new Color(60, 60, 80));
                spinnerDuracion.setForeground(Color.WHITE);
                spinnerDuracion.setPreferredSize(new Dimension(100, 30));
                panelConfigDinamico.add(spinnerDuracion, gbc);
                panelConfigDinamico.putClientProperty("spinnerDuracion", spinnerDuracion);
                break;
        }
        
        panelConfigDinamico.revalidate();
        panelConfigDinamico.repaint();
    }
    
    private void ejecutarEscenario() {
        String escenario = (String) cmbEscenarios.getSelectedItem();
        if (escenario == null) {
            escenario = "Falla de Tuberia";
        }
        
        StringBuilder resultados = new StringBuilder();
        resultados.append("=== ESCENARIO: ").append(escenario.toUpperCase()).append(" ===\n\n");
        
        Grafo grafoActual = controlador.getGrafo();
        Grafo grafoBackup = copiarGrafo(grafoActual);
        
        try {
            // Aplicar el escenario
            switch (escenario) {
                case "Falla de Tuberia":
                    ejecutarFallaTuberia(resultados);
                    break;
                case "Aumento de Demanda":
                    ejecutarAumentoDemanda(resultados);
                    break;
                case "Nuevo Barrio":
                    ejecutarNuevoBarrio(resultados);
                    break;
                case "Reduccion de Capacidad":
                    ejecutarReduccionCapacidad(resultados);
                    break;
                case "Mantenimiento Programado":
                    ejecutarMantenimiento(resultados);
                    break;
            }
            
            // ===== CALCULAR FLUJO DESPUÉS DEL ESCENARIO =====
            // Usar el método que selecciona automáticamente el primer embalse y barrio
            double flujo = controlador.calcularFlujoMaximo();
            
            resultados.append("\n--- RESULTADOS ---\n");
            resultados.append("Flujo maximo calculado: ").append(String.format("%.2f", flujo)).append(" m³/h\n");
            
            // OBTENER FUENTE Y SUMIDERO DEL CONTROLADOR
            Embalse fuente = controlador.getFuenteActual();
            Barrio sumidero = controlador.getSumideroActual();
            
            if (fuente != null && sumidero != null) {
                resultados.append("Desde: ").append(fuente.getNombre()).append("\n");
                resultados.append("Hasta: ").append(sumidero.getNombre()).append("\n");
                resultados.append("Iteraciones: ").append(controlador.getAlgoritmo().obtenerIteraciones()).append("\n");
            } else {
                resultados.append("Desde: (No se encontró un camino válido)\n");
                resultados.append("Hasta: (No se encontró un camino válido)\n");
            }
            
            // Análisis de impacto
            resultados.append("\n--- ANALISIS DE IMPACTO ---\n");
            
            // Calcular flujo original para comparar
            double flujoOriginal = 0.0;
            try {
                // Restaurar temporalmente para calcular flujo original
                restaurarGrafo(grafoActual, grafoBackup);
                flujoOriginal = controlador.calcularFlujoMaximo();
                // Volver a aplicar el escenario
                restaurarGrafo(grafoActual, grafoBackup);
                // Re-aplicar el escenario
                switch (escenario) {
                    case "Falla de Tuberia":
                        ejecutarFallaTuberia(new StringBuilder());
                        break;
                    case "Aumento de Demanda":
                        ejecutarAumentoDemanda(new StringBuilder());
                        break;
                    case "Nuevo Barrio":
                        ejecutarNuevoBarrio(new StringBuilder());
                        break;
                    case "Reduccion de Capacidad":
                        ejecutarReduccionCapacidad(new StringBuilder());
                        break;
                    case "Mantenimiento Programado":
                        ejecutarMantenimiento(new StringBuilder());
                        break;
                }
                // Re-calcular flujo después del escenario
                flujo = controlador.calcularFlujoMaximo();
            } catch (Exception e) {
                resultados.append("Error al calcular el flujo original: ").append(e.getMessage()).append("\n");
            }
            
            if (flujoOriginal > 0) {
                double impacto = ((flujo - flujoOriginal) / flujoOriginal) * 100;
                resultados.append("Flujo original: ").append(String.format("%.2f", flujoOriginal)).append(" m³/h\n");
                resultados.append("Flujo con escenario: ").append(String.format("%.2f", flujo)).append(" m³/h\n");
                resultados.append("Impacto: ").append(String.format("%.1f", impacto)).append("%\n");
                
                if (impacto < -30) {
                    resultados.append("Estado: CRITICO - Se requiere accion inmediata\n");
                } else if (impacto < -15) {
                    resultados.append("Estado: ADVERTENCIA - Monitorear la situacion\n");
                } else if (impacto < 0) {
                    resultados.append("Estado: AFECTADO - Impacto menor\n");
                } else {
                    resultados.append("Estado: ESTABLE - Sin impacto significativo\n");
                }
            }
            
            // Mostrar detalles de tuberías
            resultados.append("\n--- DETALLE DE TUBERIAS ---\n");
            for (Arista a : controlador.getGrafo().getAristas()) {
                resultados.append("  ").append(a.getOrigen().getNombre()).append(" -> ")
                         .append(a.getDestino().getNombre()).append(": ")
                         .append(String.format("%.2f", a.getFlujo())).append("/")
                         .append(String.format("%.2f", a.getCapacidad())).append(" m³/h (")
                         .append(String.format("%.1f", a.calcularPorcentajeUso())).append("%)\n");
            }
            
            // Mostrar cobertura de barrios
            resultados.append("\n--- COBERTURA DE BARRIOS ---\n");
            for (Barrio b : controlador.getGrafo().obtenerBarrios()) {
                double rec = b.getFlujoRecibido();
                double dem = b.getDemanda();
                double cob = dem > 0 ? (rec / dem) * 100 : 0;
                String estado = cob >= 100 ? "SATISFECHO" : (cob >= 70 ? "PARCIAL" : "DEFICIT");
                resultados.append("  ").append(b.getNombre()).append(": ")
                         .append(String.format("%.2f", rec)).append("/")
                         .append(String.format("%.2f", dem)).append(" m³/h (")
                         .append(String.format("%.1f", cob)).append("%) - ")
                         .append(estado).append("\n");
            }
            
            // Restaurar el grafo original (después de mostrar resultados)
            restaurarGrafo(grafoActual, grafoBackup);
            
        } catch (Exception e) {
            resultados.append("ERROR: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
            restaurarGrafo(grafoActual, grafoBackup);
        }
        
        areaResultados.setText(resultados.toString());
    }
    
    private void ejecutarFallaTuberia(StringBuilder resultados) {
        JComboBox<String> cmb = (JComboBox<String>) panelConfigDinamico.getClientProperty("cmbTuberias");
        if (cmb == null || cmb.getItemCount() == 0 || cmb.getItemAt(0).equals("No hay tuberias disponibles")) {
            if (resultados != null) resultados.append("ERROR: No hay tuberias disponibles para fallar\n");
            return;
        }
        String seleccion = (String) cmb.getSelectedItem();
        
        if (resultados != null) {
            resultados.append("Aplicando falla en tuberia: ").append(seleccion).append("\n");
        }
        
        for (Arista a : controlador.getGrafo().getAristas()) {
            String nombre = a.getOrigen().getNombre() + " -> " + a.getDestino().getNombre();
            if (nombre.equals(seleccion)) {
                controlador.getGrafo().eliminarArista(a);
                if (resultados != null) {
                    resultados.append("Tuberia eliminada correctamente\n");
                }
                return;
            }
        }
        if (resultados != null) {
            resultados.append("ERROR: No se encontro la tuberia seleccionada\n");
        }
    }
    
    private void ejecutarAumentoDemanda(StringBuilder resultados) {
        JSpinner spinner = (JSpinner) panelConfigDinamico.getClientProperty("spinnerDemanda");
        if (spinner == null) {
            if (resultados != null) resultados.append("ERROR: No se encontro el control de porcentaje\n");
            return;
        }
        int porcentaje = (Integer) spinner.getValue();
        
        if (resultados != null) {
            resultados.append("Aumentando demanda en ").append(porcentaje).append("%\n");
        }
        
        for (Barrio b : controlador.getGrafo().obtenerBarrios()) {
            double nuevaDemanda = b.getDemanda() * (1 + porcentaje / 100.0);
            b.setDemanda(nuevaDemanda);
            if (resultados != null) {
                resultados.append("  ").append(b.getNombre()).append(": ").append(String.format("%.2f", b.getDemanda())).append(" m³/h\n");
            }
        }
    }
    
    private void ejecutarNuevoBarrio(StringBuilder resultados) {
        JTextField txt = (JTextField) panelConfigDinamico.getClientProperty("txtDemanda");
        if (txt == null) {
            if (resultados != null) resultados.append("ERROR: No se encontro el campo de demanda\n");
            return;
        }
        double demanda;
        try {
            demanda = Double.parseDouble(txt.getText());
        } catch (NumberFormatException e) {
            if (resultados != null) resultados.append("ERROR: Demanda invalida\n");
            return;
        }
        
        if (resultados != null) {
            resultados.append("Agregando nuevo barrio con demanda: ").append(demanda).append(" m³/h\n");
        }
        
        Conexion conexion = null;
        for (Nodo n : controlador.getGrafo().getNodos()) {
            if (n instanceof Conexion) {
                conexion = (Conexion) n;
                break;
            }
        }
        
        if (conexion != null) {
            String id = "B" + System.currentTimeMillis() % 1000;
            Barrio nuevo = new Barrio(id, "NuevoBarrio", 2000, demanda, 2.8, 5, 
                                      conexion.getPosicionX() + 150, conexion.getPosicionY() + 100);
            controlador.getGrafo().agregarNodo(nuevo);
            Arista arista = new Arista(conexion, nuevo, 150);
            controlador.getGrafo().agregarArista(arista);
            if (resultados != null) {
                resultados.append("Barrio creado y conectado a ").append(conexion.getNombre()).append("\n");
            }
        } else {
            if (resultados != null) {
                resultados.append("ERROR: No se encontró una conexion para conectar el nuevo barrio\n");
            }
        }
    }
    
    private void ejecutarReduccionCapacidad(StringBuilder resultados) {
        JSpinner spinner = (JSpinner) panelConfigDinamico.getClientProperty("spinnerReduccion");
        if (spinner == null) {
            if (resultados != null) resultados.append("ERROR: No se encontro el control de reduccion\n");
            return;
        }
        int porcentaje = (Integer) spinner.getValue();
        
        if (resultados != null) {
            resultados.append("Reduciendo capacidad de tuberias en ").append(porcentaje).append("%\n");
        }
        
        for (Arista a : controlador.getGrafo().getAristas()) {
            double nuevaCapacidad = a.getCapacidad() * (1 - porcentaje / 100.0);
            a.setCapacidad(Math.max(10, nuevaCapacidad));
            if (resultados != null) {
                resultados.append("  ").append(a.getOrigen().getNombre()).append(" -> ").append(a.getDestino().getNombre())
                         .append(": ").append(String.format("%.2f", a.getCapacidad())).append(" m³/h\n");
            }
        }
    }
    
    private void ejecutarMantenimiento(StringBuilder resultados) {
        JComboBox<String> cmb = (JComboBox<String>) panelConfigDinamico.getClientProperty("cmbMantenimiento");
        JSpinner spinner = (JSpinner) panelConfigDinamico.getClientProperty("spinnerDuracion");
        
        if (cmb == null || cmb.getItemCount() == 0 || cmb.getItemAt(0).equals("No hay tuberias disponibles")) {
            if (resultados != null) resultados.append("ERROR: No hay tuberias disponibles para mantenimiento\n");
            return;
        }
        String seleccion = (String) cmb.getSelectedItem();
        int horas = spinner != null ? (Integer) spinner.getValue() : 4;
        
        if (resultados != null) {
            resultados.append("Aplicando mantenimiento en tuberia: ").append(seleccion).append("\n");
            resultados.append("Duracion estimada: ").append(horas).append(" horas\n");
        }
        
        for (Arista a : controlador.getGrafo().getAristas()) {
            String nombre = a.getOrigen().getNombre() + " -> " + a.getDestino().getNombre();
            if (nombre.equals(seleccion)) {
                a.setEstado("MANTENIMIENTO");
                double capacidadOriginal = a.getCapacidad();
                a.setCapacidad(capacidadOriginal * 0.3);
                if (resultados != null) {
                    resultados.append("Tuberia en mantenimiento - Capacidad reducida al 30%\n");
                }
                return;
            }
        }
        if (resultados != null) {
            resultados.append("ERROR: No se encontro la tuberia seleccionada\n");
        }
    }
    
    private Grafo copiarGrafo(Grafo original) {
        Grafo copia = new Grafo();
        Map<String, Nodo> mapaNodos = new HashMap<>();
        
        for (Nodo n : original.getNodos()) {
            copia.agregarNodo(n);
            mapaNodos.put(n.getId(), n);
        }
        
        for (Arista a : original.getAristas()) {
            Arista nueva = new Arista(a.getOrigen(), a.getDestino(), a.getCapacidad());
            nueva.setFlujo(a.getFlujo());
            nueva.setEstado(a.getEstado());
            copia.agregarArista(nueva);
        }
        
        return copia;
    }
    
    private void restaurarGrafo(Grafo actual, Grafo backup) {
        actual.limpiar();
        for (Nodo n : backup.getNodos()) {
            actual.agregarNodo(n);
        }
        for (Arista a : backup.getAristas()) {
            actual.agregarArista(a);
        }
    }
    
    private void restaurarOriginal() {
        restaurarGrafo(controlador.getGrafo(), grafoOriginal);
        areaResultados.setText("Red restaurada a su estado original");
        controlador.calcularFlujoMaximo();
        JOptionPane.showMessageDialog(this, "Red restaurada a su estado original");
    }
    
    private JButton crearBoton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 200), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        return boton;
    }
}