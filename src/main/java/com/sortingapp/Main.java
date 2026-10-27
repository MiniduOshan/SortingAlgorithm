package com.sortingapp;

import com.sortingapp.gui.MainFrame;

import javax.swing.*;

/**
 * Entry point for the Sorting Algorithm Performance Evaluation Java Application.

 * Group Assignment: IC4302 – Programming III
 * Department of ICT, Faculty of Technology, University of Colombo.
 */
public class Main {
    public static void main(String[] args) {
        // Set System Look and Feel for modern native UI rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to default Swing look and feel
        }

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
