package com.fluvial.vista;

import com.fluvial.controlador.ControladorRed;
import com.fluvial.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

public class PanelGrafo extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, DropTargetListener {
    
    private ControladorRed controlador;
    private VistaPrincipal vista;
    private Nodo nodoSeleccionado;
    private Nodo nodoOrigenConexion;
    private double zoom;
    private int offsetX;
    private int offsetY;
    private Point puntoInicioArrastre;
    private Point posicionMouse;
    
    public PanelGrafo(ControladorRed controlador, VistaPrincipal vista) {
        this.controlador = controlador;
        this.vista = vista;
        this.zoom = 1.0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.nodoSeleccionado = null;
        this.nodoOrigenConexion = null;
        this.posicionMouse = new Point(0, 0);
        
        setBackground(new Color(35, 35, 45));
        setPreferredSize(new Dimension(800, 600));
        
        new DropTarget(this, this);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public void actualizarGrafo() {
        repaint();
    }
    
    public Nodo getNodoSeleccionado() {
        return nodoSeleccionado;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Fondo
        g2d.setColor(new Color(35, 35, 45));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(new Color(50, 50, 65));
        g2d.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < getWidth(); x += 40) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += 40) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        
        g2d.translate(offsetX, offsetY);
        g2d.scale(zoom, zoom);
        
        // Dibujar aristas
        for (Arista arista : controlador.getGrafo().getAristas()) {
            dibujarArista(g2d, arista);
        }
        
        // Línea de conexión temporal
        if (nodoOrigenConexion != null) {
            int x1 = nodoOrigenConexion.getPosicionX();
            int y1 = nodoOrigenConexion.getPosicionY();
            int x2 = (int) ((posicionMouse.x - offsetX) / zoom);
            int y2 = (int) ((posicionMouse.y - offsetY) / zoom);
            
            g2d.setColor(new Color(52, 152, 219, 150));
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 4}, 0));
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Dibujar nodos
        for (Nodo nodo : controlador.getGrafo().getNodos()) {
            dibujarNodo(g2d, nodo);
        }
        
        // Información del nodo seleccionado
        if (nodoSeleccionado != null) {
            dibujarInfoNodo(g2d, nodoSeleccionado);
        }
    }
    
    private void dibujarArista(Graphics2D g2d, Arista arista) {
        Nodo origen = arista.getOrigen();
        Nodo destino = arista.getDestino();
        
        int x1 = origen.getPosicionX();
        int y1 = origen.getPosicionY();
        int x2 = destino.getPosicionX();
        int y2 = destino.getPosicionY();
        
        double uso = arista.calcularPorcentajeUso();
        Color color;
        if (uso > 85) {
            color = new Color(231, 76, 60);
        } else if (uso > 70) {
            color = new Color(243, 156, 18);
        } else if (uso > 0) {
            color = new Color(46, 204, 113);
        } else {
            color = new Color(100, 100, 120);
        }
        
        float grosor = (float) (2.0 + (uso / 100.0) * 4.0);
        g2d.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(color);
        g2d.drawLine(x1, y1, x2, y2);
        
        // Flecha (solo si hay flujo)
        if (arista.getFlujo() > 0) {
            int tamFlecha = 12;
            double angulo = Math.atan2(y2 - y1, x2 - x1);
            double mx = (x1 + x2) / 2.0;
            double my = (y1 + y2) / 2.0;
            
            int px = (int) (mx + tamFlecha * 0.5 * Math.cos(angulo));
            int py = (int) (my + tamFlecha * 0.5 * Math.sin(angulo));
            int a1x = (int) (px - tamFlecha * Math.cos(angulo - Math.PI / 6));
            int a1y = (int) (py - tamFlecha * Math.sin(angulo - Math.PI / 6));
            int a2x = (int) (px - tamFlecha * Math.cos(angulo + Math.PI / 6));
            int a2y = (int) (py - tamFlecha * Math.sin(angulo + Math.PI / 6));
            
            g2d.fillPolygon(new int[]{px, a1x, a2x}, new int[]{py, a1y, a2y}, 3);
        }
        
        // Etiqueta de flujo/capacidad
        String texto = String.format("%.0f/%.0f", arista.getFlujo(), arista.getCapacidad());
        Font font = new Font("Segoe UI", Font.PLAIN, 10);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int ancho = fm.stringWidth(texto);
        
        int mx = (x1 + x2) / 2;
        int my = (y1 + y2) / 2;
        
        g2d.setColor(new Color(40, 40, 55, 220));
        g2d.fillRoundRect(mx - ancho/2 - 6, my - 10, ancho + 12, 20, 6, 6);
        g2d.setColor(new Color(200, 200, 220));
        g2d.drawString(texto, mx - ancho/2, my + 4);
    }
    
    private void dibujarNodo(Graphics2D g2d, Nodo nodo) {
        int x = nodo.getPosicionX();
        int y = nodo.getPosicionY();
        int radio = 30;
        
        Color color = getColorNodo(nodo);
        
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillOval(x - radio + 3, y - radio + 3, radio * 2, radio * 2);
        
        if (nodo.equals(nodoSeleccionado)) {
            RadialGradientPaint rgp = new RadialGradientPaint(
                x, y, radio * 2,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(255, 215, 0, 80), new Color(255, 215, 0, 0)}
            );
            g2d.setPaint(rgp);
            g2d.fillOval(x - radio * 2, y - radio * 2, radio * 4, radio * 4);
        }
        
        GradientPaint gp = new GradientPaint(
            x - radio/2, y - radio/2, color,
            x + radio/2, y + radio/2, color.darker()
        );
        g2d.setPaint(gp);
        g2d.fillOval(x - radio, y - radio, radio * 2, radio * 2);
        
        g2d.setColor(new Color(255, 255, 255, 60));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - radio, y - radio, radio * 2, radio * 2);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
        String texto = nodo.getNombre();
        FontMetrics fm = g2d.getFontMetrics();
        int ancho = fm.stringWidth(texto);
        if (ancho > radio * 2) {
            texto = texto.substring(0, Math.min(6, texto.length()));
            ancho = fm.stringWidth(texto);
        }
        g2d.drawString(texto, x - ancho/2, y + 4);
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        String tipo = nodo.getTipo().substring(0, Math.min(1, nodo.getTipo().length()));
        int anchoTipo = fm.stringWidth(tipo);
        g2d.drawString(tipo, x - anchoTipo/2, y + radio + 14);
    }
    
    private Color getColorNodo(Nodo nodo) {
        String tipo = nodo.getTipo();
        switch (tipo) {
            case "EMBALSE": 
                Embalse e = (Embalse) nodo;
                if (e.estaSobrecargado()) {
                    return new Color(231, 76, 60);
                }
                return new Color(52, 152, 219);
            case "BARRIO": 
                Barrio b = (Barrio) nodo;
                if (b.tieneBajaPresion()) {
                    return new Color(231, 76, 60);
                }
                if (!b.tieneSuministroSuficiente()) {
                    return new Color(243, 156, 18);
                }
                return new Color(46, 204, 113);
            case "CONEXION": 
                Conexion c = (Conexion) nodo;
                if (c.estaSaturada()) {
                    return new Color(243, 156, 18);
                }
                return new Color(155, 89, 182);
            default: 
                return new Color(150, 150, 170);
        }
    }
    
    private void dibujarInfoNodo(Graphics2D g2d, Nodo nodo) {
        int x = nodo.getPosicionX() + 50;
        int y = nodo.getPosicionY() - 40;
        
        String info = nodo.obtenerDescripcion();
        String[] lineas = info.split("\n");
        
        Font font = new Font("Segoe UI", Font.PLAIN, 11);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        
        int anchoMax = 0;
        for (String linea : lineas) {
            anchoMax = Math.max(anchoMax, fm.stringWidth(linea));
        }
        
        int padding = 12;
        int ancho = anchoMax + padding * 2;
        int alto = lineas.length * fm.getHeight() + padding * 2;
        
        g2d.setColor(new Color(20, 20, 30, 230));
        g2d.fillRoundRect(x, y, ancho, alto, 10, 10);
        g2d.setColor(new Color(60, 60, 80));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, ancho, alto, 10, 10);
        
        g2d.setColor(new Color(200, 200, 220));
        int yPos = y + padding + fm.getAscent();
        for (String linea : lineas) {
            g2d.drawString(linea, x + padding, yPos);
            yPos += fm.getHeight();
        }
    }
    
    private Nodo encontrarNodoEnPosicion(int x, int y) {
        int radio = 30;
        for (Nodo nodo : controlador.getGrafo().getNodos()) {
            int dx = x - nodo.getPosicionX();
            int dy = y - nodo.getPosicionY();
            if (dx * dx + dy * dy <= radio * radio) {
                return nodo;
            }
        }
        return null;
    }
    
    private Arista encontrarAristaCercana(int x, int y) {
        Arista masCercana = null;
        double distanciaMinima = Double.MAX_VALUE;
        
        for (Arista arista : controlador.getGrafo().getAristas()) {
            Nodo origen = arista.getOrigen();
            Nodo destino = arista.getDestino();
            
            int mx = (origen.getPosicionX() + destino.getPosicionX()) / 2;
            int my = (origen.getPosicionY() + destino.getPosicionY()) / 2;
            
            double distancia = Math.sqrt((x - mx) * (x - mx) + (y - my) * (y - my));
            if (distancia < distanciaMinima && distancia < 50) {
                distanciaMinima = distancia;
                masCercana = arista;
            }
        }
        return masCercana;
    }
    
    private void actualizarEstadoPanelHerramientas() {
        Container parent = getParent();
        while (parent != null) {
            parent = parent.getParent();
            if (parent instanceof VistaPrincipal) {
                Component[] comps = ((VistaPrincipal) parent).getContentPane().getComponents();
                for (Component comp : comps) {
                    if (comp instanceof JPanel) {
                        JPanel panel = (JPanel) comp;
                        if (panel.getComponentCount() > 1) {
                            Component comp2 = panel.getComponent(1);
                            if (comp2 instanceof JSplitPane) {
                                JSplitPane split = (JSplitPane) comp2;
                                Component right = split.getRightComponent();
                                if (right instanceof PanelHerramientas) {
                                    ((PanelHerramientas) right).actualizarEstado();
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }
    
    public void agregarNodoDesdePaleta(String tipo, int x, int y) {
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
            actualizarGrafo();
            actualizarEstadoPanelHerramientas();
        }
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY);
    }
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            Transferable t = dtde.getTransferable();
            String tipo = (String) t.getTransferData(DataFlavor.stringFlavor);
            
            Point p = dtde.getLocation();
            int x = (int) ((p.x - offsetX) / zoom);
            int y = (int) ((p.y - offsetY) / zoom);
            
            agregarNodoDesdePaleta(tipo, x, y);
            dtde.dropComplete(true);
            
        } catch (Exception ex) {
            dtde.dropComplete(false);
            ex.printStackTrace();
        }
    }
    
    @Override
    public void dragExit(DropTargetEvent dte) {}
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int x = (int) ((e.getX() - offsetX) / zoom);
            int y = (int) ((e.getY() - offsetY) / zoom);
            
            Arista aristaCercana = encontrarAristaCercana(x, y);
            if (aristaCercana != null) {
                PanelTuberia panel = new PanelTuberia((JFrame) SwingUtilities.getWindowAncestor(this), aristaCercana);
                if (panel.fueModificado()) {
                    actualizarGrafo();
                    actualizarEstadoPanelHerramientas();
                }
                return;
            }
        }
        
        int x = (int) ((e.getX() - offsetX) / zoom);
        int y = (int) ((e.getY() - offsetY) / zoom);
        
        Nodo nodo = encontrarNodoEnPosicion(x, y);
        
        if (nodo != null) {
            if (SwingUtilities.isRightMouseButton(e) || nodoOrigenConexion != null) {
                if (nodoOrigenConexion == null) {
                    nodoOrigenConexion = nodo;
                    setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                    JOptionPane.showMessageDialog(this, 
                        "Seleccione el nodo destino para crear una tuberia\n" +
                        "Click derecho para cancelar",
                        "Crear Tuberia",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else if (!nodo.equals(nodoOrigenConexion)) {
                    Arista arista = new Arista(nodoOrigenConexion, nodo, 150);
                    if (controlador.getGrafo().agregarArista(arista)) {
                        nodoOrigenConexion = null;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        actualizarGrafo();
                        actualizarEstadoPanelHerramientas();
                        JOptionPane.showMessageDialog(this, "Tuberia creada correctamente");
                    }
                }
                return;
            }
            
            nodoSeleccionado = nodo;
            vista.actualizarInformacionNodo("Nodo: " + nodo.getNombre() + " (" + nodo.getTipo() + ")");
            
            Container parent = getParent();
            while (parent != null) {
                parent = parent.getParent();
                if (parent instanceof VistaPrincipal) {
                    Component[] comps = ((VistaPrincipal) parent).getContentPane().getComponents();
                    for (Component comp : comps) {
                        if (comp instanceof JPanel) {
                            JPanel panel = (JPanel) comp;
                            if (panel.getComponentCount() > 1) {
                                Component comp2 = panel.getComponent(1);
                                if (comp2 instanceof JSplitPane) {
                                    JSplitPane split = (JSplitPane) comp2;
                                    Component right = split.getRightComponent();
                                    if (right instanceof PanelHerramientas) {
                                        ((PanelHerramientas) right).seleccionarNodo(nodo);
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
            }
            
        } else {
            if (nodoOrigenConexion == null) {
                nodoSeleccionado = null;
                vista.actualizarInformacionNodo("Seleccione un nodo en el grafo");
            }
        }
        
        repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && nodoOrigenConexion != null) {
            nodoOrigenConexion = null;
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            repaint();
            return;
        }
        puntoInicioArrastre = new Point(e.getX(), e.getY());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (puntoInicioArrastre != null) {
            int dx = e.getX() - puntoInicioArrastre.x;
            int dy = e.getY() - puntoInicioArrastre.y;
            offsetX += dx;
            offsetY += dy;
            puntoInicioArrastre = new Point(e.getX(), e.getY());
            repaint();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        puntoInicioArrastre = null;
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        posicionMouse = e.getPoint();
        if (nodoOrigenConexion != null) {
            repaint();
        }
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double factor = 1.0 - (e.getWheelRotation() * 0.05);
        zoom = Math.max(0.3, Math.min(2.5, zoom * factor));
        repaint();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
}