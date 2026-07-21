package com.fluvial.vista;

import com.fluvial.controlador.ControladorRed;
import com.fluvial.modelo.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class VistaPrincipal extends JFrame {
    
    private ControladorRed controlador;
    private PanelGrafo panelGrafo;
    private PanelHerramientas panelHerramientas;
    private PanelEstadisticas panelEstadisticas;
    private JDialog dialogEstadisticas;
    private JPanel panelTitulo;
    private JLabel lblInfoNodo;
    
    public VistaPrincipal() {
        this.controlador = new ControladorRed();
        inicializarInterfaz();
    }
    
    private void inicializarInterfaz() {
        setTitle("Simulador de Red de Distribucion de Agua");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 950);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 40));
        
        crearPanelTitulo();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(1000);
        splitPane.setBorder(null);
        splitPane.setBackground(new Color(30, 30, 40));
        splitPane.setResizeWeight(0.7);
        
        JPanel panelGrafoContainer = new JPanel(new BorderLayout());
        panelGrafoContainer.setBackground(new Color(35, 35, 45));
        panelGrafoContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panelGrafo = new PanelGrafo(controlador, this);
        panelGrafo.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 1));
        panelGrafoContainer.add(panelGrafo, BorderLayout.CENTER);
        splitPane.setLeftComponent(panelGrafoContainer);
        
        panelHerramientas = new PanelHerramientas(controlador, this, panelGrafo);
        panelHerramientas.setPreferredSize(new Dimension(400, 0));
        splitPane.setRightComponent(panelHerramientas);
        
        JPanel panelInferior = crearPanelInferior();
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(30, 30, 40));
        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        crearDialogoEstadisticas();
        
        setVisible(true);
    }
    
    private void crearPanelTitulo() {
        panelTitulo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(20, 40, 80),
                    getWidth(), 0, new Color(40, 60, 120)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelTitulo.setPreferredSize(new Dimension(0, 70));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        JLabel titulo = new JLabel("Red de Distribucion Fluvial");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);
        panelTitulo.add(titulo, BorderLayout.WEST);
        
        JLabel subtitulo = new JLabel("Simulador Profesional v6.0");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(180, 200, 255));
        panelTitulo.add(subtitulo, BorderLayout.EAST);
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(35, 35, 45));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 80)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.setPreferredSize(new Dimension(0, 70));
        
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(45, 45, 60));
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        lblInfoNodo = new JLabel("Seleccione un nodo en el grafo para ver su informacion");
        lblInfoNodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfoNodo.setForeground(new Color(200, 200, 220));
        panelInfo.add(lblInfoNodo, BorderLayout.CENTER);
        
        panel.add(panelInfo, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelBotones.setBackground(new Color(35, 35, 45));
        
        JButton btnEstadisticas = crearBotonClaro("Ver Estadisticas", new Color(200, 230, 255));
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());
        panelBotones.add(btnEstadisticas);
        
        JButton btnAyuda = crearBotonClaro("Ayuda", new Color(220, 220, 240));
        btnAyuda.addActionListener(e -> mostrarAyuda());
        panelBotones.add(btnAyuda);
        
        panel.add(panelBotones, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton crearBotonClaro(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        return boton;
    }
    
    private void crearDialogoEstadisticas() {
        dialogEstadisticas = new JDialog(this, "Estadisticas de la Red", false);
        dialogEstadisticas.setSize(650, 600);
        dialogEstadisticas.setLocationRelativeTo(this);
        dialogEstadisticas.setLayout(new BorderLayout());
        dialogEstadisticas.getContentPane().setBackground(new Color(35, 35, 45));
        
        panelEstadisticas = new PanelEstadisticas(controlador);
        dialogEstadisticas.add(panelEstadisticas, BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(220, 220, 240));
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCerrar.addActionListener(e -> dialogEstadisticas.setVisible(false));
        
        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelCerrar.setBackground(new Color(35, 35, 45));
        panelCerrar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCerrar.add(btnCerrar);
        dialogEstadisticas.add(panelCerrar, BorderLayout.SOUTH);
    }
    
    private void mostrarEstadisticas() {
        panelEstadisticas.actualizarEstadisticas();
        dialogEstadisticas.setVisible(true);
    }
    
    private void mostrarAyuda() {
        String ayuda = 
            "SIMULADOR DE RED DE DISTRIBUCION DE AGUA\n" +
            "========================================\n\n" +
            "INSTRUCCIONES:\n\n" +
            "1. Arrastre nodos desde la paleta al grafo\n" +
            "2. Seleccione un nodo para configurar sus propiedades\n" +
            "3. Click derecho en un nodo para iniciar una conexion\n" +
            "4. Click en otro nodo para completar la tuberia\n" +
            "5. Doble click en una tuberia para editarla\n" +
            "6. Use 'Guardar Red' y 'Cargar Red' para persistencia\n" +
            "7. Use 'Escenarios' para simulaciones avanzadas\n\n" +
            "ATAJOS:\n" +
            "- Rueda mouse: Zoom\n" +
            "- Arrastrar: Mover el grafo";
        
        JOptionPane.showMessageDialog(this, ayuda, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void actualizarInformacionNodo(String info) {
        lblInfoNodo.setText(info);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new VistaPrincipal());
    }
}