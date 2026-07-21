package com.fluvial.vista;

import com.fluvial.controlador.ControladorRed;
import com.fluvial.modelo.Arista;
import com.fluvial.modelo.Barrio;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel de control con diseño moderno para gestionar la simulacion.
 * 
 * @author Equipo Fluvial
 * @version 2.0
 */
public class PanelControl extends JPanel {
    
    /** Controlador de la red */
    private ControladorRed controlador;
    
    /** Referencia a la vista principal */
    private VistaPrincipal vista;
    
    /** Botones de control con diseño moderno */
    private JButton btnCalcularFlujo;
    private JButton btnAnalizarRed;
    private JButton btnLimpiar;
    private JButton btnAgregarNodo;
    
    /** Campos de texto para entrada de datos */
    private JTextField txtCapacidad;
    private JTextField txtDemanda;
    
    /** Etiquetas de estado */
    private JLabel lblEstado;
    private JLabel lblFlujoActual;
    private JLabel lblTiempo;
    
    /** Panel de estado con gradiente */
    private JPanel panelEstado;
    
    /**
     * Constructor del panel de control
     */
    public PanelControl(ControladorRed controlador, VistaPrincipal vista) {
        this.controlador = controlador;
        this.vista = vista;
        
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(0, 280));
        
        inicializarComponentes();
    }
    
    /**
     * Inicializa todos los componentes con diseño moderno
     */
    private void inicializarComponentes() {
        // Panel superior: botones principales
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panelBotones.setBackground(new Color(248, 249, 250));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Botón Calcular Flujo - Azul
        btnCalcularFlujo = crearBotonModerno(
            "Calcular Flujo", 
            new Color(41, 128, 185),
            new Color(52, 152, 219)
        );
        btnCalcularFlujo.addActionListener(e -> calcularFlujo());
        panelBotones.add(btnCalcularFlujo);
        
        // Botón Analizar Red - Verde
        btnAnalizarRed = crearBotonModerno(
            "Analizar Red",
            new Color(39, 174, 96),
            new Color(46, 204, 113)
        );
        btnAnalizarRed.addActionListener(e -> analizarRed());
        panelBotones.add(btnAnalizarRed);
        
        // Botón Limpiar Red - Rojo
        btnLimpiar = crearBotonModerno(
            "Limpiar Red",
            new Color(192, 57, 43),
            new Color(231, 76, 60)
        );
        btnLimpiar.addActionListener(e -> limpiarRed());
        panelBotones.add(btnLimpiar);
        
        // Botón Agregar Nodo - Naranja
        btnAgregarNodo = crearBotonModerno(
            "Agregar Nodo",
            new Color(211, 84, 0),
            new Color(243, 156, 18)
        );
        btnAgregarNodo.addActionListener(e -> mostrarDialogoAgregarNodo());
        panelBotones.add(btnAgregarNodo);
        
        add(panelBotones, BorderLayout.NORTH);
        
        // Panel central: configuracion rapida
        JPanel panelConfiguracion = new JPanel(new GridBagLayout());
        panelConfiguracion.setBackground(Color.WHITE);
        panelConfiguracion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título de configuración
        JLabel lblConfigTitulo = new JLabel("Configuracion Rapida");
        lblConfigTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblConfigTitulo.setForeground(new Color(50, 50, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        panelConfiguracion.add(lblConfigTitulo, gbc);
        
        // Capacidad
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel lblCapacidad = new JLabel("Capacidad Tuberia (m³/h):");
        lblCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelConfiguracion.add(lblCapacidad, gbc);
        
        gbc.gridx = 1;
        txtCapacidad = new JTextField("100");
        txtCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCapacidad.setPreferredSize(new Dimension(80, 30));
        panelConfiguracion.add(txtCapacidad, gbc);
        
        // Demanda
        gbc.gridx = 2;
        JLabel lblDemanda = new JLabel("Demanda Barrio (m³/h):");
        lblDemanda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelConfiguracion.add(lblDemanda, gbc);
        
        gbc.gridx = 3;
        txtDemanda = new JTextField("50");
        txtDemanda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDemanda.setPreferredSize(new Dimension(80, 30));
        panelConfiguracion.add(txtDemanda, gbc);
        
        // Botón aplicar
        gbc.gridx = 4;
        gbc.gridwidth = 2;
        JButton btnAplicarConfig = crearBotonModerno(
            "Aplicar Configuracion",
            new Color(100, 100, 140),
            new Color(130, 130, 180)
        );
        btnAplicarConfig.addActionListener(e -> aplicarConfiguracion());
        panelConfiguracion.add(btnAplicarConfig, gbc);
        
        add(panelConfiguracion, BorderLayout.CENTER);
        
        // Panel inferior: estado con gradiente
        panelEstado = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(240, 242, 245),
                    getWidth(), 0, new Color(230, 235, 240)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelEstado.setOpaque(false);
        panelEstado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panelEstado.setPreferredSize(new Dimension(0, 50));
        
        GridBagConstraints gbcEstado = new GridBagConstraints();
        gbcEstado.insets = new Insets(0, 20, 0, 20);
        gbcEstado.fill = GridBagConstraints.HORIZONTAL;
        
        // Indicador de estado (círculo verde)
        JPanel indicador = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(46, 204, 113));
                g2d.fillOval(0, 0, 12, 12);
            }
        };
        indicador.setPreferredSize(new Dimension(12, 12));
        indicador.setOpaque(false);
        gbcEstado.gridx = 0;
        gbcEstado.gridy = 0;
        gbcEstado.weightx = 0;
        panelEstado.add(indicador, gbcEstado);
        
        lblEstado = new JLabel("Sistema Listo");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstado.setForeground(new Color(50, 50, 80));
        gbcEstado.gridx = 1;
        gbcEstado.weightx = 0.3;
        panelEstado.add(lblEstado, gbcEstado);
        
        lblFlujoActual = new JLabel("Flujo Actual: 0.00 m³/h");
        lblFlujoActual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFlujoActual.setForeground(new Color(80, 80, 120));
        gbcEstado.gridx = 2;
        gbcEstado.weightx = 0.3;
        panelEstado.add(lblFlujoActual, gbcEstado);
        
        lblTiempo = new JLabel("Tiempo de Simulacion: --");
        lblTiempo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTiempo.setForeground(new Color(80, 80, 120));
        gbcEstado.gridx = 3;
        gbcEstado.weightx = 0.4;
        panelEstado.add(lblTiempo, gbcEstado);
        
        add(panelEstado, BorderLayout.SOUTH);
    }
    
    /**
     * Crea un botón con diseño moderno
     */
    private JButton crearBotonModerno(String texto, Color colorInicio, Color colorFin) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradiente del botón
                GradientPaint gp = new GradientPaint(
                    0, 0, colorInicio,
                    getWidth(), getHeight(), colorFin
                );
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                
                // Sombra
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.draw(new RoundRectangle2D.Double(0, 2, getWidth(), getHeight(), 8, 8));
                
                // Texto
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(140, 40));
        
        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 2),
                    BorderFactory.createEmptyBorder(8, 18, 8, 18)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            }
        });
        
        return boton;
    }
    
    /**
     * Actualiza el estado del panel después de una simulación
     */
    public void actualizarEstado() {
        double flujo = controlador.getFlujoMaximoCalculado();
        if (flujo > 0) {
            lblFlujoActual.setText(String.format("Flujo Actual: %.2f m³/h", flujo));
            lblEstado.setText("Simulacion Completada");
            lblEstado.setForeground(new Color(39, 174, 96));
            
            // Cambiar color del indicador
            panelEstado.repaint();
        }
    }
    
    /**
     * Acción: Calcular flujo máximo
     */
    private void calcularFlujo() {
        if (!controlador.validarRed()) {
            JOptionPane.showMessageDialog(
                this,
                "La red no esta configurada correctamente.\n" +
                "Asegurese de tener al menos un embalse y un barrio.",
                "Error de Configuracion",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Simular tiempo de procesamiento
        long inicio = System.currentTimeMillis();
        
        double flujo = controlador.calcularFlujoMaximo();
        
        long fin = System.currentTimeMillis();
        double tiempo = (fin - inicio) / 1000.0;
        lblTiempo.setText(String.format("Tiempo: %.2f seg", tiempo));
        
        if (flujo > 0) {
            actualizarEstado();
            vista.repaint();
            
            JOptionPane.showMessageDialog(
                this,
                String.format(
                    "Flujo maximo calculado: %.2f m³/h\n" +
                    "Algoritmo: %s\n" +
                    "Iteraciones: %d\n" +
                    "Tiempo: %.2f segundos",
                    flujo,
                    controlador.getAlgoritmo().obtenerNombre(),
                    controlador.getAlgoritmo().obtenerIteraciones(),
                    tiempo
                ),
                "Resultado del Flujo Maximo",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo calcular el flujo maximo.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Acción: Analizar red
     */
    private void analizarRed() {
        if (!controlador.validarRed()) {
            JOptionPane.showMessageDialog(
                this,
                "La red no esta configurada correctamente.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        String informe = controlador.generarInformeRed();
        JTextArea areaTexto = new JTextArea(informe);
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaTexto.setBackground(new Color(250, 251, 253));
        
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setPreferredSize(new Dimension(650, 500));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Analisis de la Red",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Acción: Limpiar red
     */
    private void limpiarRed() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "Se perderan todos los datos. ¿Desea continuar?",
            "Limpiar Red",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            controlador.limpiarRed();
            lblEstado.setText("Red Limpia");
            lblFlujoActual.setText("Flujo Actual: 0.00 m³/h");
            lblTiempo.setText("Tiempo: --");
            vista.repaint();
        }
    }
    
    /**
     * Acción: Aplicar configuración rápida
     */
    private void aplicarConfiguracion() {
        try {
            double capacidad = Double.parseDouble(txtCapacidad.getText());
            double demanda = Double.parseDouble(txtDemanda.getText());
            
            if (capacidad <= 0 || demanda <= 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Los valores deben ser positivos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Aplicar a todas las tuberías y barrios
            for (Arista arista : controlador.getGrafo().getAristas()) {
                arista.setCapacidad(capacidad);
            }
            
            for (Barrio barrio : controlador.getGrafo().obtenerBarrios()) {
                barrio.setDemanda(demanda);
            }
            
            JOptionPane.showMessageDialog(
                this,
                "Configuracion aplicada exitosamente.",
                "Exito",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            vista.repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor ingrese valores numericos validos.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Muestra el diálogo para agregar nodos
     */
    private void mostrarDialogoAgregarNodo() {
        String[] tipos = {"Embalse", "Barrio", "Conexion"};
        String tipo = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione el tipo de nodo:",
            "Agregar Nodo",
            JOptionPane.QUESTION_MESSAGE,
            null,
            tipos,
            tipos[0]
        );
        
        if (tipo == null) return;
        
        String nombre = JOptionPane.showInputDialog(this, "Nombre del nodo:");
        if (nombre == null || nombre.trim().isEmpty()) return;
        
        String id = JOptionPane.showInputDialog(this, "ID del nodo:");
        if (id == null || id.trim().isEmpty()) return;
        
        boolean exito = false;
        switch (tipo) {
            case "Embalse":
                String capStr = JOptionPane.showInputDialog(this, "Capacidad maxima (m³/h):");
                if (capStr != null) {
                    try {
                        double cap = Double.parseDouble(capStr);
                        exito = controlador.agregarEmbalse(id, nombre, cap, 100, 300, 300) != null;
                    } catch (NumberFormatException e) {}
                }
                break;
            case "Barrio":
                String demStr = JOptionPane.showInputDialog(this, "Demanda (m³/h):");
                if (demStr != null) {
                    try {
                        double dem = Double.parseDouble(demStr);
                        exito = controlador.agregarBarrio(id, nombre, 1000, dem, 3.0, 5, 300, 300) != null;
                    } catch (NumberFormatException e) {}
                }
                break;
            case "Conexion":
                String capConStr = JOptionPane.showInputDialog(this, "Capacidad maxima (m³/h):");
                if (capConStr != null) {
                    try {
                        double cap = Double.parseDouble(capConStr);
                        exito = controlador.agregarConexion(id, nombre, "UNION", 3.0, cap, 300, 300) != null;
                    } catch (NumberFormatException e) {}
                }
                break;
        }
        
        if (exito) {
            JOptionPane.showMessageDialog(
                this,
                "Nodo agregado exitosamente.",
                "Exito",
                JOptionPane.INFORMATION_MESSAGE
            );
            vista.repaint();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo agregar el nodo. Verifique los datos.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}