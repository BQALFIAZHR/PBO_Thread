 import javax.swing.*;
 import javax.swing.border.Border;
 import java.awt.*;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.sql.SQLException; 
 import java.awt.image.BufferedImage;
 import java.io.File;
 import java.io.IOException;
 import javax.imageio.ImageIO;
 import javax.swing.ImageIcon;
 
 public class ManajemenWaktuRey extends JFrame {
 

     private Thread activeTimerThread;
     private int gameTimeEarned = 0;
     private BufferedImage headerImage;
 
     private DatabaseManager dbManager; 
 
     private final Color MAIN_BACKGROUND = new Color(245, 245, 245);
     private final Color CARD_BG_BLUE = Color.decode("#5dc4f3");
     private final Color BUTTON_RED = new Color(220, 53, 69);
     private final Color TEXT_COLOR_DARK = new Color(33, 37, 41);
 
     private final Font FONT_SANS_REG = new Font("SansSerif", Font.PLAIN, 14);
     private final Font FONT_SANS_BOLD = new Font("SansSerif", Font.BOLD, 18);
     private final Font FONT_SANS_TITLE = new Font("SansSerif", Font.BOLD, 24);
     private final Font FONT_TIMER = new Font("SansSerif", Font.BOLD, 36);
 
     private JSpinner studySpinner;
     private JLabel gameTimeLabel;
     private JLabel studyTimerLabel;
     private JLabel gameTimerLabel;
     private JButton studyButton;
     private JButton gameButton;
     private JTextArea logArea;
 
     public ManajemenWaktuRey() {
         setTitle("Manajemen Waktu Rey (ReyStudy)");
         setSize(800, 700);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setLocationRelativeTo(null);
         getContentPane().setBackground(MAIN_BACKGROUND);
         setLayout(new BorderLayout(10, 10));
         dbManager = new DatabaseManager();
 

         try {
             dbManager.initDatabase();
         } catch (Exception e) {
             showError("Koneksi Database Gagal! Pastikan XAMPP (MySQL) berjalan.\n" + e.getMessage());
         }
     
 
         loadHeaderImage();
 
         add(createHeaderPanel(), BorderLayout.NORTH);
         add(createMainContentPanel(), BorderLayout.CENTER);
         add(createLogPanel(), BorderLayout.SOUTH);

         loadLogs();
     }

     private void loadHeaderImage() {
         try {
             headerImage = ImageIO.read(new File("header.jpg"));
         } catch (IOException e) {
             System.err.println("Peringatan: Gagal memuat header.jpg: " + e.getMessage());
             headerImage = null;
         }
     }

     private JPanel createHeaderPanel() {
         JPanel headerPanel = new JPanel() {
             @Override
             protected void paintComponent(Graphics g) {
                 super.paintComponent(g);
                 if (headerImage != null) {
                     g.drawImage(headerImage, 0, 0, getWidth(), getHeight(), this);
                 } else {
                     g.setColor(new Color(121, 184, 255));
                     g.fillRect(0, 0, getWidth(), getHeight());
                 }
             }
         };
         headerPanel.setLayout(new BorderLayout());
         headerPanel.setPreferredSize(new Dimension(800, 80));
         JLabel title = new JLabel("ReyStudy", SwingConstants.CENTER);
         title.setForeground(Color.WHITE);
         title.setFont(FONT_SANS_TITLE.deriveFont(Font.BOLD, 40f));
         title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
         headerPanel.add(title, BorderLayout.CENTER);
         return headerPanel;
     }
 

     private JPanel createMainContentPanel() {
         JPanel mainPanel = new JPanel(new BorderLayout());
         mainPanel.setOpaque(false);
         mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
         mainPanel.add(createCardsPanel(), BorderLayout.CENTER);
         return mainPanel;
     }
 
 
     private JPanel createCardsPanel() {
         JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
         cardsPanel.setOpaque(false);
         cardsPanel.add(createStudyCard());
         cardsPanel.add(createGameCard());
         return cardsPanel;
     }
 

     private JPanel createStudyCard() {
         RoundedPanel card = new RoundedPanel(CARD_BG_BLUE); 
         card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
         card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
 
         JLabel imgLabel = createScaledImageLabel("study.jpg", "[ study.jpg tidak ditemukan ]");
         card.add(imgLabel);
         card.add(Box.createRigidArea(new Dimension(0, 10)));
 
         JPanel studyControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
         studyControl.setOpaque(false);
         JLabel studyLabel = new JLabel("Set Waktu Belajar (menit):");
         studyLabel.setFont(FONT_SANS_BOLD);
         studyLabel.setForeground(TEXT_COLOR_DARK);
         studyControl.add(studyLabel);
         studySpinner = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
         studySpinner.setFont(FONT_SANS_REG);
         studyControl.add(studySpinner);
         card.add(studyControl);
         card.add(Box.createRigidArea(new Dimension(0, 5)));
 
         JPanel timerPanel = new JPanel();
         timerPanel.setOpaque(false);
         studyTimerLabel = new JLabel("00:00:00");
         studyTimerLabel.setFont(FONT_TIMER);
         studyTimerLabel.setForeground(TEXT_COLOR_DARK);
         timerPanel.add(studyTimerLabel);
         card.add(timerPanel);
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.setOpaque(false);
         studyButton = new RoundedButton("Mulai Belajar", BUTTON_RED); 
         studyButton.addActionListener(e -> startStudyTimer());
         buttonPanel.add(studyButton);
         card.add(buttonPanel);
 
         return card;
     }

     private JPanel createGameCard() {
         RoundedPanel card = new RoundedPanel(CARD_BG_BLUE); 
         card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
         card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
 
         JLabel imgLabel = createScaledImageLabel("game.jpg", "[ game.jpg tidak ditemukan ]");
         card.add(imgLabel);
         card.add(Box.createRigidArea(new Dimension(0, 10)));
 
         JPanel gameControl = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
         gameControl.setOpaque(false);
         JLabel gameLabel = new JLabel("Waktu Bermain Didapat:");
         gameLabel.setFont(FONT_SANS_BOLD);
         gameLabel.setForeground(TEXT_COLOR_DARK);
         gameControl.add(gameLabel);
         gameTimeLabel = new JLabel("0 menit");
         gameTimeLabel.setFont(FONT_SANS_BOLD.deriveFont(18f));
         gameTimeLabel.setForeground(TEXT_COLOR_DARK);
         gameControl.add(gameTimeLabel);
         card.add(gameControl);
         card.add(Box.createRigidArea(new Dimension(0, 5)));
 
         JPanel timerPanel = new JPanel();
         timerPanel.setOpaque(false);
         gameTimerLabel = new JLabel("00:00:00");
         gameTimerLabel.setFont(FONT_TIMER);
         gameTimerLabel.setForeground(TEXT_COLOR_DARK);
         timerPanel.add(gameTimerLabel);
         card.add(timerPanel);
 
         JPanel buttonPanel = new JPanel();
         buttonPanel.setOpaque(false);
         gameButton = new RoundedButton("Mulai Bermain", BUTTON_RED); 
         gameButton.addActionListener(e -> startGameTimer());
         gameButton.setEnabled(false);
         buttonPanel.add(gameButton);
         card.add(buttonPanel);
 
         return card;
     }
 

     private JPanel createLogPanel() {
         JPanel logPanel = new JPanel(new BorderLayout());
         logPanel.setBackground(MAIN_BACKGROUND);
         logPanel.setBorder(BorderFactory.createCompoundBorder(
                 BorderFactory.createEmptyBorder(0, 20, 10, 20),
                 BorderFactory.createTitledBorder("Log Aktivitas")
         ));
         logArea = new JTextArea(5, 30);
         logArea.setEditable(false);
         logArea.setFont(FONT_SANS_REG);
         JScrollPane scrollPane = new JScrollPane(logArea);
         logPanel.add(scrollPane, BorderLayout.CENTER);
         return logPanel;
     }
 
 
     private JLabel createScaledImageLabel(String imagePath, String fallbackText) {
         JLabel imgLabel;
         try {
             BufferedImage img = ImageIO.read(new File(imagePath));
             Image scaledImg = img.getScaledInstance(250, 150, Image.SCALE_SMOOTH);
             imgLabel = new JLabel(new ImageIcon(scaledImg));
         } catch (Exception e) {
             System.err.println("Gagal memuat " + imagePath + ": " + e.getMessage());
             imgLabel = new JLabel(fallbackText);
             imgLabel.setFont(FONT_SANS_REG.deriveFont(Font.ITALIC));
             imgLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
             imgLabel.setPreferredSize(new Dimension(250, 150));
         }
         imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         return imgLabel;
     }

 
     private void startStudyTimer() {
         int studyMinutes = (Integer) studySpinner.getValue();
         int earnedMinutes = studyMinutes / 3;
 
         startTimer("Belajar", studyMinutes, () -> {
             gameTimeEarned += earnedMinutes;
             updateGameTimeLabel();
    
             try {
                 dbManager.logActivityToDatabase("Belajar", studyMinutes);
                 loadLogs();
             } catch (SQLException e) {
                 showError("Gagal menyimpan log: " + e.getMessage());
             }
            
         });
     }
 
     private void startGameTimer() {
         if (gameTimeEarned > 0) {
             startTimer("Bermain", gameTimeEarned, () -> {
        
                 try {
                     dbManager.logActivityToDatabase("Bermain", gameTimeEarned);
                     gameTimeEarned = 0; 
                     updateGameTimeLabel();
                     loadLogs();
                 } catch (SQLException e) {
                     showError("Gagal menyimpan log: " + e.getMessage());
                 }
                
             });
         }
     }
 
     private void updateGameTimeLabel() {
         gameTimeLabel.setText(gameTimeEarned + " menit");
         gameButton.setEnabled(gameTimeEarned > 0);
     }
 
 
     private void startTimer(String activityType, int durationMinutes, Runnable onFinishedCallback) {
         studyButton.setEnabled(false);
         gameButton.setEnabled(false);
         JLabel targetTimerLabel = (activityType.equals("Belajar")) ? studyTimerLabel : gameTimerLabel;
 
         activeTimerThread = new Thread(() -> {
             try {
                 long totalSeconds = durationMinutes * 60;
                 for (long i = totalSeconds; i >= 0; i--) {
                     long hours = i / 3600;
                     long minutes = (i % 3600) / 60;
                     long seconds = i % 60;
                     String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                     SwingUtilities.invokeLater(() -> targetTimerLabel.setText(timeString));
                     Thread.sleep(1000);
                 }
                 SwingUtilities.invokeLater(onFinishedCallback);
             } catch (InterruptedException e) {
                 System.out.println("Timer diinterupsi");
                 SwingUtilities.invokeLater(() -> targetTimerLabel.setText("00:00:00"));
             } finally {
                 SwingUtilities.invokeLater(() -> {
                     studyButton.setEnabled(true);
                     gameButton.setEnabled(gameTimeEarned > 0);
                     targetTimerLabel.setText("00:00:00");
                 });
             }
         });
         activeTimerThread.start();
     }
 
  
     private void loadLogs() {
         try {
             String logs = dbManager.loadLogs();
             logArea.setText(logs);
         } catch (SQLException e) {
             showError("Gagal memuat log dari DB: " + e.getMessage());
         }
     }
 
  
     private void showError(String message) {
         JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
     }
 
    
 }