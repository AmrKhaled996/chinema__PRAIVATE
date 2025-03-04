package com.mycompany.movie.GUI;
/*
        todo: get the screentimes for this movie and add it into the drop-down list.-Done-
        todo: send the list of selected seats to Conferm Panel to display it. -done-
        todo: edit bookMovie to get the movie and send the screenTimes to the bookSeats.
        todo: make database method that make change in the seats of the hall. -Done-
        
        */

import com.mycompany.movie.Book.Order;
import com.mycompany.movie.Book.Receipt;
import com.mycompany.movie.CLI.Format;
import com.mycompany.movie.CLI.Print;
import static com.mycompany.movie.CLI.Print.seats;
import com.mycompany.movie.Database;
import com.mycompany.movie.GUI.BookMovie1;
import com.mycompany.movie.Hall.Hall;
import com.mycompany.movie.Movies.Movie;
import com.mycompany.movie.Movies.MovieLibrary;
import com.mycompany.movie.Movies.ScreenTime;
import com.mycompany.movie.GUI.CustomerInput;
import com.mycompany.movie.GUI.program;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author DELL
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookSeats1 extends javax.swing.JPanel {
    private List<JLabel> seatLabels; // To store seat JLabels

    public BookSeats1(program prog) {
        initComponents1(prog);
        MovieLibrary.getMovies();
        Database.getAllReceipts();
        setName("BookSeats");
    
    
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt,program prog) {
        float totalPrice = Order.getPrice(CheckedSeats);
        int response = JOptionPane.showConfirmDialog(null,
                "Price all is " + totalPrice +
                "\nMovie Title: " + movie.getTitle()+
                "\nHall ID: " + selectedScreenTime.getHall().getID()+
                "\nStart Date: " + Format.date(selectedScreenTime.getStartDate())+
               "\nEnd Date: " + Format.date(selectedScreenTime.getEndDate())+
                
                "\nDuration: " + Format.duration(movie.getDuration())+
                "\nSeats: " + Print.SeatsIDs(CheckedSeats)
                , "Confirmation"
                ,JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
            for (Hall.Seat CheckedSeat : CheckedSeats) {
                Database.updateSeatAvailability(CheckedSeat.getID(),(byte) selectedScreenTime.getHall().getID(), false);
                CheckedSeat.setAvailability(false);
            }
            Database.addCustomer(CustomerInput.customer.getName(), CustomerInput.customer.getAge());
            Database.addReceipt(movie.getID() , CustomerInput.customer.getID() , totalPrice, selectedScreenTime.getHall().getID(), Format.seatsIDList(CheckedSeats)); // todo : add the customer
            receipt= new Receipt(Database.getAllReceipts().getLast().getID()+1,CustomerInput.customer.getName(), movie.getTitle(), totalPrice, selectedScreenTime.getHall().getID(),Format.seatsIDList(CheckedSeats), selectedScreenTime);
            prog.switchToPanel("MovieReceipt");
        }       
           
    }

    

    public void initComponents1(program prog) {
        
        seatLabels = new ArrayList<>(); // Initialize the list of seat labels
        JPanel jPanel1 = new JPanel();
        JComboBox<String> jComboBox1 = new JComboBox<>(new String[]{"--Select--"});
        JLabel jLabel2 = new JLabel("Select Screen Time:   ");
        JButton jButton1 = new JButton("Confirm");
        JPanel jPanel2 = new JPanel();
        JPanel gridPanel = new JPanel();
        
        jButton1.setBackground(new Color(51, 153, 255));
        jButton1.setForeground(new Color(255, 255, 255));
        jButton1.setFont(new Font("Arial", Font.BOLD, 16));
        jButton1.setEnabled(false);
        backButton=new JButton("Back");
        
        backButton.setBackground(new Color(51, 153, 255));
        backButton.setForeground(new Color(255, 255, 255));
        
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt,prog);
            }
        });
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }

            private void backButtonActionPerformed(ActionEvent evt) {
                prog.switchToPanel("BookMovie");
            }
        });
        
        
//       var lib =MovieLibrary.getMovies();
        movie=MovieLibrary.getMovies().get(BookMovie1.MovieId);
        
        screenTimes=movie.getScreenTimes();
         
        
        
      // Populate the combo box with formatted start dates from screenTimes
// Populate the combo box with formatted start dates from screenTimes
for (ScreenTime screenTime : screenTimes) {
    Date startDate = screenTime.getStartDate();
    jComboBox1.addItem(Database.formatDate(startDate)); // Display formatted dates
}

// Add an action listener to the combo box
jComboBox1.addActionListener(evt -> {
    // Get the selected item from the combo box
    String selectedItem = (String) jComboBox1.getSelectedItem();
            if (!"--Select--".equals(selectedItem)) {
                System.out.println("Selected: " + selectedItem);
           
    // Match the selected item with the corresponding ScreenTime
    for (ScreenTime screenTime : screenTimes) {
        if (screenTime.getStartDate().equals(Database.parseDate(selectedItem))) {
            selectedScreenTime = screenTime; // Store the selected ScreenTime
            System.out.println("Selected ScreenTime: " + selectedScreenTime.toString());

            // Use the Hall from the selected ScreenTime
            Hall hall = selectedScreenTime.getHall();
            Hall.Seat[][] seats =Database.getHallById((byte)hall.getID()).getSeats();
            System.out.println(seats[1][1]);
            
            Database.insertSeatsIntoHall(hall, hall.getSeats());

            // Configure gridPanel with the dimensions of the hall
            gridPanel.setLayout(new GridLayout(hall.getNumberOfRows(), hall.getNumberOfCols(), 5, 5));
            gridPanel.removeAll(); // Clear previous seats

            // Loop through the seats and populate the gridPanel
            for (int row = 0; row < hall.getNumberOfRows(); row++) {
                for (int col = 0; col < hall.getNumberOfCols(); col++) {

                    JLabel label = new JLabel();
                    label.setMaximumSize(new Dimension(150, 70));
                    label.setPreferredSize(new Dimension(150, 70));
                    label.setMinimumSize(new Dimension(150, 70));
                    
                    // Set seat availability and icons
                    if (seats[row][col].isAvailable()) {
                        label.setIcon(new ImageIcon(AvailaSeats));
                    } else {
                        label.setIcon(new ImageIcon(notAvailaSeats));
                    }

                    // Set label text to indicate seat position
                    label.setText((char) ('A' + row) + "" + (col + 1));

                    // Add border for visual clarity
                    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    // Add MouseListener for label clicks
                    var seat = seats[row][col]; // Capture the current seat
                    label.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            if (seat.isAvailable()) {
                                if (!CheckedSeats.contains(seat)) {
                                    SetChecked(label); // Custom method to mark as checked
                                    CheckedSeats.add(seat); // Add to selected seats list
                                } else {
                                    setUnChecked(label); // Custom method to unmark
                                    CheckedSeats.remove(seat); // Remove from selected seats list
                                }
                            }
                            if (CheckedSeats.isEmpty()) {
                            if (jButton1.isEnabled()) {
                                    jButton1.setEnabled(false); // Disable the button
                                 
                             }
                            } else {
                                 jButton1.setEnabled(true); // Enable the button
                                 
                             }
                        }
                    });

                    // Add label to the grid panel
                    gridPanel.add(label);
                }
            }

            // Refresh the UI
            gridPanel.revalidate();
            gridPanel.repaint();
            break; // Exit the loop once the matching ScreenTime is found
        }
    }
  }
});

// Wrap the gridPanel in a JScrollPane
JScrollPane scrollPane = new JScrollPane(gridPanel);
scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

// Configure layouts and add components to the panel
jPanel2.setLayout(new BorderLayout());
jPanel2.add(jComboBox1, BorderLayout.NORTH); // Add combo box at the top
jPanel2.add(scrollPane, BorderLayout.CENTER); // Add scrollPane at the center

jPanel1.setPreferredSize(new Dimension(750, 400));
jPanel1.setLayout(new BorderLayout());
jPanel1.add(jPanel2, BorderLayout.CENTER); // Add jPanel2 to jPanel1
jPanel1.add(jButton1, BorderLayout.SOUTH); // Add a button at the bottom
jPanel1.add(backButton, BorderLayout.NORTH); // Add a button at the bottom

// Add the main panel to the container
add(jPanel1);

    }

    private void SetChecked( JLabel label) {
        
        label.setText(label.getText().substring(0,2)+" checked");
        label.setIcon(new ImageIcon(cSeats));
        
    }
    private void setUnChecked( JLabel label) {
     
        label.setText(label.getText().substring(0,2)+" unchecked");
        label.setIcon(new ImageIcon(AvailaSeats));
        
    }



    /**
     * @param args the command line arguments
     */
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
//            java.util.logging.Logger.getLogger(BookSeats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(BookSeats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(BookSeats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(BookSeats1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new BookSeats1().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration                   
//    private List<JLabel> Seats =new  ArrayList<JLabel>();
//    private JPanel gridPanel=new JPanel();
//    private JPanel labels =new JPanel();
    private final String AvailaSeats="C:\\Users\\DELL\\OneDrive\\Desktop\\AvailableSeat1.png";
    private final String notAvailaSeats="C:\\Users\\DELL\\OneDrive\\Desktop\\notAvailableSeat1.png";
    private final String cSeats="C:\\Users\\DELL\\OneDrive\\Desktop\\CheckedSeat.png";
    private List<Hall.Seat> CheckedSeats=new ArrayList<Hall.Seat>(); 
    private List<ScreenTime> screenTimes=new ArrayList<ScreenTime>();
    private ScreenTime selectedScreenTime;
    public Hall hall;
    private Movie movie;
    private JButton backButton;
    public static Receipt receipt;
}