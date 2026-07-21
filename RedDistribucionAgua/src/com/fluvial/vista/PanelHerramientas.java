package com.fluvial.vista;

import com.fluvial.controlador.ControladorRed;
import com.fluvial.modelo.*;
import com.fluvial.util.PersistenciaRed;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PanelHerramientas extends JPanel {
    
    private ControladorRed controlador;
    private VistaPrincipal vista;
    private PanelGrafo panelGrafo;
    
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtCapacidad;
    private JTextField txtDemanda;
    private JTextField txtPresion;
    private JTextField txtElevacion;
    private JTextField txtPoblacion;
    private JComboBox<String> cmbTipoConexion;
    private JComboBox<Integer> cmbPrioridad;
    private JComboBox<String> cmbEstado;
    
    private Nodo nodoSeleccionado;
    
    private JLabel lblEstado;
    private JLabel lblFlujo;
    private JLabel lblNodos;
    private JLabel lblConexiones;
    
    private JPanel panelCamposEmbalse;
    private JPanel panelCamposBarrio;
    private JPanel panelCamposConexion;
    private CardLayout cardLayoutCampos;
    private JPanel panelCardCampos;
    
    public PanelHerramientas(ControladorRed controlador, VistaPrincipal vista, PanelGrafo panelGrafo) {
        this.controlador = controlador;
        this.vista = vista;
        this.panelGrafo = panelGrafo;
        this.nodoSeleccionado = null;
        
        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 45));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(380, 0));
        
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        JPanel panelPaleta = crearPanelPaleta();
        add(panelPaleta, BorderLayout.NORTH);
        
        JPanel panelPropiedades = crearPanelPropiedades();
        add(panelPropiedades, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(35, 35, 45));
        panelInferior.add(crearPanelAcciones(), BorderLayout.NORTH);
        panelInferior.add(crearPanelEstado(), BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelPaleta() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 60));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(0, 130));
        
        JLabel titulo = new JLabel("Paleta de Nodos (Arrastre al Grafo)");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(Color.WHITE);
        panel.add(titulo, BorderLayout.NORTH);
        
        JPanel grid = new JPanel(new GridLayout(1, 3, 10, 10));
        grid.setBackground(new Color(45, 45, 60));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        grid.add(crearNodoArrastrable("Embalse", new Color(52, 152, 219)));
        grid.add(crearNodoArrastrable("Barrio", new Color(46, 204, 113)));
        grid.add(crearNodoArrastrable("Conexion", new Color(155, 89, 182)));
        
        panel.add(grid, BorderLayout.CENTER);
        
        JLabel instruccion = new JLabel("Arrastre al grafo o haga click para agregar");
        instruccion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        instruccion.setForeground(new Color(180, 180, 200));
        instruccion.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(instruccion, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearNodoArrastrable(String tipo, Color color) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    10, 10, color,
                    getWidth() - 10, getHeight() - 10, color.darker()
                );
                g2d.setPaint(gp);
                g2d.fillOval(10, 5, 30, 30);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String abrev = tipo.substring(0, 1);
                FontMetrics fm = g2d.getFontMetrics();
                int x = 10 + (30 - fm.stringWidth(abrev)) / 2;
                int y = 5 + (30 + fm.getAscent()) / 2 - 3;
                g2d.drawString(abrev, x, y);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                int ancho = fm.stringWidth(tipo);
                g2d.drawString(tipo, (getWidth() - ancho) / 2, getHeight() - 8);
            }
        };
        panel.setPreferredSize(new Dimension(90, 55));
        panel.setBackground(new Color(45, 45, 60));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.putClientProperty("tipo", tipo);
        
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(panel, DnDConstants.ACTION_COPY, 
            new DragGestureListener() {
                @Override
                public void dragGestureRecognized(DragGestureEvent dge) {
                    String tipoNodo = (String) panel.getClientProperty("tipo");
                    Transferable t = new StringSelection(tipoNodo);
                    dge.startDrag(null, t);
                }
            }
        );
        
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String tipoNodo = (String) panel.getClientProperty("tipo");
                agregarNodoAleatorio(tipoNodo);
            }
        });
        
        return panel;
    }
    
    private void agregarNodoAleatorio(String tipo) {
        int x = 200 + (int)(Math.random() * 400);
        int y = 100 + (int)(Math.random() * 300);
        String id = tipo.substring(0, 1) + System.currentTimeMillis() % 1000;
        String nombre = tipo + "_" + (int)(Math.random() * 1000);
        
        boolean exito = false;
        switch (tipo) {
            case "Embalse":
                exito = controlador.agregarEmbalse(id, nombre, 300, 100, x, y) != null;
                break;
            case "Barrio":
                exito = controlador.agregarBarrio(id, nombre, 1000, 120, 3.0, 5, x, y) != null;
                break;
            case "Conexion":
                exito = controlador.agregarConexion(id, nombre, "UNION", 3.0, 200, x, y) != null;
                break;
        }
        
        if (exito) {
            actualizarEstado();
            panelGrafo.actualizarGrafo();
            vista.repaint();
        }
    }
    
    private JPanel crearPanelPropiedades() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 60));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));
        
        JLabel titulo = new JLabel("Propiedades del Nodo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        JPanel panelComunes = new JPanel(new GridBagLayout());
        panelComunes.setBackground(new Color(45, 45, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblId = new JLabel("ID:");
        lblId.setForeground(Color.WHITE);
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panelComunes.add(lblId, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        txtId = new JTextField(12);
        txtId.setEditable(false);
        txtId.setBackground(new Color(60, 60, 80));
        txtId.setForeground(Color.WHITE);
        txtId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panelComunes.add(txtId, gbc);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panelComunes.add(lblNombre, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        txtNombre = new JTextField(12);
        txtNombre.setBackground(new Color(60, 60, 80));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panelComunes.add(txtNombre, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblEstadoNodo = new JLabel("Estado:");
        lblEstadoNodo.setForeground(Color.WHITE);
        lblEstadoNodo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panelComunes.add(lblEstadoNodo, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "MANTENIMIENTO"});
        cmbEstado.setBackground(new Color(200, 200, 220));
        cmbEstado.setForeground(Color.BLACK);
        cmbEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        panelComunes.add(cmbEstado, gbc);
        
        panel.add(panelComunes, BorderLayout.NORTH);
        
        cardLayoutCampos = new CardLayout();
        panelCardCampos = new JPanel(cardLayoutCampos);
        panelCardCampos.setBackground(new Color(45, 45, 60));
        panelCardCampos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80)),
            "Campos Especificos",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11),
            new Color(180, 180, 200)
        ));
        
        panelCamposEmbalse = crearCamposEmbalse();
        panelCardCampos.add(panelCamposEmbalse, "EMBALSE");
        
        panelCamposBarrio = crearCamposBarrio();
        panelCardCampos.add(panelCamposBarrio, "BARRIO");
        
        panelCamposConexion = crearCamposConexion();
        panelCardCampos.add(panelCamposConexion, "CONEXION");
        
        JPanel panelVacio = new JPanel();
        panelVacio.setBackground(new Color(45, 45, 60));
        JLabel lblSeleccion = new JLabel("Seleccione un nodo para ver sus campos");
        lblSeleccion.setForeground(new Color(150, 150, 180));
        lblSeleccion.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panelVacio.add(lblSeleccion);
        panelCardCampos.add(panelVacio, "VACIO");
        
        panel.add(panelCardCampos, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        panelBotones.setBackground(new Color(45, 45, 60));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton btnAplicar = crearBotonClaro("Aplicar Cambios", new Color(150, 200, 255));
        btnAplicar.addActionListener(e -> aplicarPropiedades());
        panelBotones.add(btnAplicar);
        
        JButton btnEliminar = crearBotonClaro("Eliminar Nodo", new Color(255, 180, 180));
        btnEliminar.addActionListener(e -> eliminarNodo());
        panelBotones.add(btnEliminar);
        
        JButton btnLimpiar = crearBotonClaro("Limpiar", new Color(220, 220, 240));
        btnLimpiar.addActionListener(e -> limpiarCampos());
        panelBotones.add(btnLimpiar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        cardLayoutCampos.show(panelCardCampos, "VACIO");
        
        return panel;
    }
    
    private JPanel crearCamposEmbalse() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblCapacidad = new JLabel("Capacidad (m³/h):");
        lblCapacidad.setForeground(new Color(200, 200, 220));
        lblCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblCapacidad, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtCapacidad = new JTextField(10);
        txtCapacidad.setBackground(new Color(60, 60, 80));
        txtCapacidad.setForeground(Color.WHITE);
        txtCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(txtCapacidad, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblElevacion = new JLabel("Elevacion (msnm):");
        lblElevacion.setForeground(new Color(200, 200, 220));
        lblElevacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblElevacion, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtElevacion = new JTextField(10);
        txtElevacion.setBackground(new Color(60, 60, 80));
        txtElevacion.setForeground(Color.WHITE);
        txtElevacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(txtElevacion, gbc);
        
        return panel;
    }
    
    private JPanel crearCamposBarrio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblDemanda = new JLabel("Demanda (m³/h):");
        lblDemanda.setForeground(new Color(200, 200, 220));
        lblDemanda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblDemanda, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtDemanda = new JTextField(10);
        txtDemanda.setBackground(new Color(60, 60, 80));
        txtDemanda.setForeground(Color.WHITE);
        txtDemanda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(txtDemanda, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPresion = new JLabel("Presion (bar):");
        lblPresion.setForeground(new Color(200, 200, 220));
        lblPresion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblPresion, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtPresion = new JTextField(10);
        txtPresion.setBackground(new Color(60, 60, 80));
        txtPresion.setForeground(Color.WHITE);
        txtPresion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(txtPresion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPoblacion = new JLabel("Poblacion:");
        lblPoblacion.setForeground(new Color(200, 200, 220));
        lblPoblacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblPoblacion, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtPoblacion = new JTextField(10);
        txtPoblacion.setBackground(new Color(60, 60, 80));
        txtPoblacion.setForeground(Color.WHITE);
        txtPoblacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(txtPoblacion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblPrioridad = new JLabel("Prioridad (1-10):");
        lblPrioridad.setForeground(new Color(200, 200, 220));
        lblPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblPrioridad, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        cmbPrioridad = new JComboBox<>();
        for (int i = 1; i <= 10; i++) {
            cmbPrioridad.addItem(i);
        }
        cmbPrioridad.setBackground(new Color(60, 60, 80));
        cmbPrioridad.setForeground(Color.WHITE);
        cmbPrioridad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(cmbPrioridad, gbc);
        
        return panel;
    }
    
    private JPanel crearCamposConexion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblCapacidad = new JLabel("Capacidad (m³/h):");
        lblCapacidad.setForeground(new Color(200, 200, 220));
        lblCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblCapacidad, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtCapacidad = new JTextField(10);
        txtCapacidad.setBackground(new Color(60, 60, 80));
        txtCapacidad.setForeground(Color.WHITE);
        txtCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(txtCapacidad, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblPresion = new JLabel("Presion (bar):");
        lblPresion.setForeground(new Color(200, 200, 220));
        lblPresion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblPresion, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtPresion = new JTextField(10);
        txtPresion.setBackground(new Color(60, 60, 80));
        txtPresion.setForeground(Color.WHITE);
        txtPresion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(txtPresion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblTipoConexion = new JLabel("Tipo Conexion:");
        lblTipoConexion.setForeground(new Color(200, 200, 220));
        lblTipoConexion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(lblTipoConexion, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        cmbTipoConexion = new JComboBox<>(new String[]{"UNION", "BOMBEO", "VALVULA"});
        cmbTipoConexion.setBackground(new Color(60, 60, 80));
        cmbTipoConexion.setForeground(Color.WHITE);
        cmbTipoConexion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        panel.add(cmbTipoConexion, gbc);
        
        return panel;
    }
    
    private JButton crearBotonClaro(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        return boton;
    }
    
    private JPanel crearPanelAcciones() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        panel.setBackground(new Color(35, 35, 45));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton btnCalcular = crearBotonClaro("Calcular Flujo", new Color(150, 200, 255));
        btnCalcular.addActionListener(e -> calcularFlujo());
        panel.add(btnCalcular);
        
        JButton btnAnalizar = crearBotonClaro("Analizar Red", new Color(150, 255, 180));
        btnAnalizar.addActionListener(e -> analizarRed());
        panel.add(btnAnalizar);
        
        JButton btnLimpiar = crearBotonClaro("Limpiar Red", new Color(255, 180, 180));
        btnLimpiar.addActionListener(e -> limpiarRed());
        panel.add(btnLimpiar);
        
        JButton btnGuardar = crearBotonClaro("Guardar Red", new Color(255, 220, 150));
        btnGuardar.addActionListener(e -> guardarRed());
        panel.add(btnGuardar);
        
        JButton btnCargar = crearBotonClaro("Cargar Red", new Color(200, 220, 255));
        btnCargar.addActionListener(e -> cargarRed());
        panel.add(btnCargar);
        
        JButton btnEscenarios = crearBotonClaro("Escenarios", new Color(200, 180, 255));
        btnEscenarios.addActionListener(e -> abrirEscenarios());
        panel.add(btnEscenarios);
        
        return panel;
    }
    
    private void guardarRed() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON (*.json)", "json"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getPath();
            if (!ruta.endsWith(".json")) {
                ruta += ".json";
            }
            if (PersistenciaRed.guardarRed(controlador.getGrafo(), ruta)) {
                JOptionPane.showMessageDialog(this, "Red guardada correctamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la red", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cargarRed() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON (*.json)", "json"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getPath();
            if (PersistenciaRed.cargarRed(controlador.getGrafo(), ruta)) {
                panelGrafo.actualizarGrafo();
                actualizarEstado();
                JOptionPane.showMessageDialog(this, "Red cargada correctamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al cargar la red", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void abrirEscenarios() {
        new PanelEscenario((JFrame) SwingUtilities.getWindowAncestor(this), controlador);
        panelGrafo.actualizarGrafo();
        actualizarEstado();
    }
    
    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 40, 55));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 10, 2, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JPanel indicador = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(46, 204, 113));
                g2d.fillOval(0, 0, 10, 10);
            }
        };
        indicador.setPreferredSize(new Dimension(10, 10));
        indicador.setOpaque(false);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(indicador, gbc);
        
        lblEstado = new JLabel("Sistema Listo");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstado.setForeground(new Color(200, 200, 220));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(lblEstado, gbc);
        
        lblFlujo = new JLabel("Flujo: 0.00 m³/h");
        lblFlujo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFlujo.setForeground(new Color(180, 180, 200));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(lblFlujo, gbc);
        
        lblNodos = new JLabel("Nodos: 0");
        lblNodos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNodos.setForeground(new Color(180, 180, 200));
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(lblNodos, gbc);
        
        lblConexiones = new JLabel("Tuberias: 0");
        lblConexiones.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblConexiones.setForeground(new Color(180, 180, 200));
        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(lblConexiones, gbc);
        
        return panel;
    }
    
    public void actualizarEstado() {
        Grafo grafo = controlador.getGrafo();
        lblNodos.setText("Nodos: " + grafo.getNodos().size());
        lblConexiones.setText("Tuberias: " + grafo.getAristas().size());
        
        double flujo = controlador.getFlujoMaximoCalculado();
        if (flujo > 0) {
            lblFlujo.setText(String.format("Flujo: %.2f m³/h", flujo));
            lblEstado.setText("Simulacion Completada");
        }
    }
    
    public void seleccionarNodo(Nodo nodo) {
        this.nodoSeleccionado = nodo;
        
        if (nodo == null) {
            limpiarCampos();
            cardLayoutCampos.show(panelCardCampos, "VACIO");
            return;
        }
        
        txtId.setText(nodo.getId());
        txtNombre.setText(nodo.getNombre());
        cmbEstado.setSelectedItem(nodo.getEstado());
        
        if (nodo instanceof Embalse) {
            cardLayoutCampos.show(panelCardCampos, "EMBALSE");
            Embalse e = (Embalse) nodo;
            txtCapacidad.setText(String.valueOf(e.getCapacidadMaxima()));
            txtElevacion.setText(String.valueOf(e.getElevacion()));
        } else if (nodo instanceof Barrio) {
            cardLayoutCampos.show(panelCardCampos, "BARRIO");
            Barrio b = (Barrio) nodo;
            txtDemanda.setText(String.valueOf(b.getDemanda()));
            txtPresion.setText(String.valueOf(b.getPresion()));
            txtPoblacion.setText(String.valueOf(b.getPoblacion()));
            cmbPrioridad.setSelectedItem(b.getPrioridad());
        } else if (nodo instanceof Conexion) {
            cardLayoutCampos.show(panelCardCampos, "CONEXION");
            Conexion c = (Conexion) nodo;
            txtCapacidad.setText(String.valueOf(c.getCapacidadMaxima()));
            txtPresion.setText(String.valueOf(c.getPresion()));
            cmbTipoConexion.setSelectedItem(c.getTipoConexion());
        }
    }
    
    private void aplicarPropiedades() {
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo primero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String nombre = txtNombre.getText().trim();
            if (!nombre.isEmpty()) {
                nodoSeleccionado.setNombre(nombre);
            }
            nodoSeleccionado.setEstado((String) cmbEstado.getSelectedItem());
            
            if (nodoSeleccionado instanceof Embalse) {
                Embalse e = (Embalse) nodoSeleccionado;
                if (!txtCapacidad.getText().isEmpty()) {
                    e.setCapacidadMaxima(Double.parseDouble(txtCapacidad.getText()));
                }
                if (!txtElevacion.getText().isEmpty()) {
                    e.setElevacion(Double.parseDouble(txtElevacion.getText()));
                }
                JOptionPane.showMessageDialog(this, "Propiedades del Embalse actualizadas");
            } else if (nodoSeleccionado instanceof Barrio) {
                Barrio b = (Barrio) nodoSeleccionado;
                if (!txtDemanda.getText().isEmpty()) {
                    b.setDemanda(Double.parseDouble(txtDemanda.getText()));
                }
                if (!txtPresion.getText().isEmpty()) {
                    b.setPresion(Double.parseDouble(txtPresion.getText()));
                }
                if (!txtPoblacion.getText().isEmpty()) {
                    b.setPoblacion(Integer.parseInt(txtPoblacion.getText()));
                }
                b.setPrioridad((Integer) cmbPrioridad.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Propiedades del Barrio actualizadas");
            } else if (nodoSeleccionado instanceof Conexion) {
                Conexion c = (Conexion) nodoSeleccionado;
                if (!txtCapacidad.getText().isEmpty()) {
                    c.setCapacidadMaxima(Double.parseDouble(txtCapacidad.getText()));
                }
                if (!txtPresion.getText().isEmpty()) {
                    c.setPresion(Double.parseDouble(txtPresion.getText()));
                }
                c.setTipoConexion((String) cmbTipoConexion.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Propiedades de la Conexion actualizadas");
            }
            
            panelGrafo.actualizarGrafo();
            actualizarEstado();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese valores numericos validos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarNodo() {
        if (nodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un nodo primero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Esta seguro de eliminar el nodo " + nodoSeleccionado.getNombre() + "?",
            "Confirmar Eliminacion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            controlador.getGrafo().eliminarNodo(nodoSeleccionado);
            nodoSeleccionado = null;
            limpiarCampos();
            actualizarEstado();
            panelGrafo.actualizarGrafo();
            vista.repaint();
        }
    }
    
    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtCapacidad.setText("");
        txtDemanda.setText("");
        txtPresion.setText("");
        txtElevacion.setText("");
        txtPoblacion.setText("");
        nodoSeleccionado = null;
        cardLayoutCampos.show(panelCardCampos, "VACIO");
    }
    
    // ==================== MÉTODO CALCULAR FLUJO CORREGIDO ====================
    
    private void calcularFlujo() {
        if (!controlador.validarRed()) {
            String mensaje = "La red no esta configurada correctamente.\n" +
                            "Embalses: " + controlador.getGrafo().obtenerEmbalses().size() + "\n" +
                            "Barrios: " + controlador.getGrafo().obtenerBarrios().size() + "\n" +
                            "Tuberias: " + controlador.getGrafo().getAristas().size();
            
            JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error de Configuracion",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        double flujo = controlador.calcularFlujoMaximo();
        
        if (flujo > 0) {
            actualizarEstado();
            panelGrafo.actualizarGrafo();
            
            Embalse fuente = controlador.getFuenteActual();
            Barrio sumidero = controlador.getSumideroActual();
            
            String desde = (fuente != null) ? fuente.getNombre() : "N/A";
            String hasta = (sumidero != null) ? sumidero.getNombre() : "N/A";
            
            JOptionPane.showMessageDialog(
                this,
                String.format("Flujo maximo calculado: %.2f m³/h\n" +
                              "Desde: %s\n" +
                              "Hasta: %s\n" +
                              "Iteraciones: %d",
                              flujo,
                              desde,
                              hasta,
                              controlador.getAlgoritmo().obtenerIteraciones()),
                "Resultado de la Simulacion",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo calcular el flujo maximo.\n" +
                "Verifique que la red este correctamente conectada.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void analizarRed() {
        if (!controlador.validarRed()) {
            JOptionPane.showMessageDialog(this, "La red no esta configurada correctamente.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String informe = controlador.generarInformeRed();
        JTextArea area = new JTextArea(informe);
        area.setEditable(false);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setBackground(new Color(40, 40, 55));
        area.setForeground(new Color(200, 200, 220));
        
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(650, 500));
        scroll.getViewport().setBackground(new Color(40, 40, 55));
        
        JOptionPane.showMessageDialog(this, scroll, "Analisis de la Red", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void limpiarRed() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Se perderan todos los datos. ¿Desea continuar?",
            "Limpiar Red",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            controlador.limpiarRed();
            limpiarCampos();
            actualizarEstado();
            panelGrafo.actualizarGrafo();
            vista.repaint();
        }
    }
}