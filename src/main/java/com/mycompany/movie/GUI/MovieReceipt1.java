/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.movie.GUI;

import com.mycompany.movie.Book.Receipt;
import com.mycompany.movie.CLI.Format;
import com.mycompany.movie.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author DELL
 */
public class MovieReceipt1 extends javax.swing.JPanel {

    /**
     * Creates new form MovieReceipt
     */
    public MovieReceipt1(program prog) {
        initComponents1(prog);
        Database.getAllReceipts();
        setName("MovieReceipt");
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();



        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

//        pack();
    }// </editor-fold>                        



    private void initComponents1(program prog) {
        

        jPanel1 = new javax.swing.JPanel(new BorderLayout());
        jLabel1 = new javax.swing.JLabel();
        backButton=new JButton("Back");

        backButton.setBackground(new Color(51, 153, 255));
        backButton.setForeground(new Color(255, 255, 255));

        backButton.setFont(new Font("Arial", Font.PLAIN, 24));
        show=new JButton("Show");

        show.setBackground(new Color(51, 153, 255));
        show.setForeground(new Color(255, 255, 255));

        show.setFont(new Font("Arial", Font.PLAIN, 24));

        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }

            private void backButtonActionPerformed(ActionEvent evt) {
                prog.switchToPanel("BookMovie");
            }
        });
        show.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showActionPerformed(evt);
            }

            private void showActionPerformed(ActionEvent evt) {
                
              
       var seatsId=" "+BookSeats1.receipt.getSeatIDs().get(0);
        for (int i = 1; i < BookSeats1.receipt.getSeatIDs().size(); i++) {
            seatsId+= ", "+BookSeats1.receipt.getSeatIDs().get(i);

        }
        // Set text for jLabel1
        jLabel1.setText("<html>"+
                        "<hr><br>Name : " + BookSeats1.receipt.getCustomerName() +
                        "<br><br>Movie Name : "  + BookSeats1.receipt.getMovieName() +
                        "<br><br>Total Price : " + BookSeats1.receipt.getTotalPrice() + " LE" +
                        "<br><br>Hall ID : "     + BookSeats1.receipt.getHallID() +
                        "<br><br>Seats :  "+ seatsId+
                        "<br><br>Duration : "    + Format.duration(BookSeats1.receipt.getStartDate(), BookSeats1.receipt.getEndDate()) +
                        "<br><br>Start Date : "  + Format.date(BookSeats1.receipt.getEndDate()) +
                        "<br><br>End Date : "    + Format.date(BookSeats1.receipt.getStartDate()) + "</html>");
        jLabel1.setFont(new Font("Arial", Font.PLAIN, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // Center text horizontally
        jLabel1.setBorder(new EmptyBorder(20, 20, 20, 20));
            }
        });
        

        // Retrieve the last receipt
        
        // Add the label to the top-center of the panel



        jPanel1.add(backButton, BorderLayout.SOUTH); // Use BorderLayout.NORTH for top placement
        jPanel1.add(show, BorderLayout.NORTH); // Use BorderLayout.NORTH for top placement
        jPanel1.add(jLabel1, BorderLayout.CENTER); // Use BorderLayout.NORTH for top placement

        // Layout configuration for the frame
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

//        pack();
    }

    /**
     * 
     * 
     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MovieReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MovieReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MovieReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MovieReceipt.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MovieReceipt().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify                     
    private JLabel jLabel1=new JLabel();
    private javax.swing.JPanel jPanel1;
    // End of variables declaration                   
    private  JButton backButton;
    private  JButton show;
}