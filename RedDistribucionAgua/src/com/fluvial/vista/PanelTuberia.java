package com.fluvial.vista;

import com.fluvial.modelo.Arista;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class PanelTuberia extends JDialog {
    
    private Arista arista;
    private JTextField txtCapacidad;
    private JTextField txtLongitud;
    private JTextField txtDiametro;
    private JTextField txtFlujo;
    private JComboBox<String> cmbEstado;
    private boolean modificado = false;
    
    public PanelTuberia(JFrame parent, Arista arista) {
        super(parent, "Editar Tuberia", true);
        this.arista = arista;
        inicializarComponentes();
        cargarDatos();
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(45, 45, 60));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(45, 45, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titulo = new JLabel("Editar Tuberia");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel lblInfo = new JLabel(arista.getOrigen().getNombre() + " -> " + arista.getDestino().getNombre());
        lblInfo.setForeground(new Color(200, 200, 220));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(lblInfo, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        JLabel lblCapacidad = new JLabel("Capacidad (m³/h):");
        lblCapacidad.setForeground(Color.WHITE);
        lblCapacidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        panel.add(lblCapacidad, gbc);
        gbc.gridx = 1;
        txtCapacidad = new JTextField(10);
        txtCapacidad.setBackground(new Color(60, 60, 80));
        txtCapacidad.setForeground(Color.WHITE);
        txtCapacidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(txtCapacidad, gbc);
        
        gbc.gridy = 3;
        JLabel lblLongitud = new JLabel("Longitud (m):");
        lblLongitud.setForeground(Color.WHITE);
        lblLongitud.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        panel.add(lblLongitud, gbc);
        gbc.gridx = 1;
        txtLongitud = new JTextField(10);
        txtLongitud.setBackground(new Color(60, 60, 80));
        txtLongitud.setForeground(Color.WHITE);
        txtLongitud.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(txtLongitud, gbc);
        
        gbc.gridy = 4;
        JLabel lblDiametro = new JLabel("Diámetro (mm):");
        lblDiametro.setForeground(Color.WHITE);
        lblDiametro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        panel.add(lblDiametro, gbc);
        gbc.gridx = 1;
        txtDiametro = new JTextField(10);
        txtDiametro.setBackground(new Color(60, 60, 80));
        txtDiametro.setForeground(Color.WHITE);
        txtDiametro.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(txtDiametro, gbc);
        
        gbc.gridy = 5;
        JLabel lblFlujo = new JLabel("Flujo Actual (m³/h):");
        lblFlujo.setForeground(Color.WHITE);
        lblFlujo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        panel.add(lblFlujo, gbc);
        gbc.gridx = 1;
        txtFlujo = new JTextField(10);
        txtFlujo.setEditable(false);
        txtFlujo.setBackground(new Color(60, 60, 80));
        txtFlujo.setForeground(Color.WHITE);
        txtFlujo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(txtFlujo, gbc);
        
        gbc.gridy = 6;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0;
        panel.add(lblEstado, gbc);
        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(new String[]{"ACTIVA", "INACTIVA", "SATURADA", "MANTENIMIENTO"});
        cmbEstado.setBackground(new Color(200, 200, 220));
        cmbEstado.setForeground(Color.BLACK);
        cmbEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(cmbEstado, gbc);
        
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(45, 45, 60));
        
        JButton btnGuardar = crearBoton("Guardar Cambios", new Color(150, 200, 255));
        btnGuardar.addActionListener(e -> guardarCambios());
        panelBotones.add(btnGuardar);
        
        JButton btnCancelar = crearBoton("Cancelar", new Color(220, 220, 240));
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);
        
        panel.add(panelBotones, gbc);
        
        add(panel, BorderLayout.CENTER);
    }
    
    private JButton crearBoton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(Color.BLACK);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 200), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        return boton;
    }
    
    private void cargarDatos() {
        txtCapacidad.setText(String.valueOf(arista.getCapacidad()));
        txtLongitud.setText(String.valueOf(arista.getLongitud()));
        txtDiametro.setText(String.valueOf(arista.getDiametro()));
        txtFlujo.setText(String.valueOf(arista.getFlujo()));
        cmbEstado.setSelectedItem(arista.getEstado());
    }
    
    private void guardarCambios() {
        try {
            double capacidad = Double.parseDouble(txtCapacidad.getText());
            double longitud = Double.parseDouble(txtLongitud.getText());
            double diametro = Double.parseDouble(txtDiametro.getText());
            
            if (capacidad <= 0) {
                JOptionPane.showMessageDialog(this, "La capacidad debe ser mayor que 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            arista.setCapacidad(capacidad);
            arista.setLongitud(longitud);
            arista.setDiametro(diametro);
            arista.setEstado((String) cmbEstado.getSelectedItem());
            
            modificado = true;
            JOptionPane.showMessageDialog(this, "Tuberia actualizada correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numericos validos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean fueModificado() {
        return modificado;
    }
}