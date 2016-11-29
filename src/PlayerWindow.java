import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;

/**
 *
 * @author manoel
 */
public class PlayerWindow extends javax.swing.JFrame {

    /**
     * Creates new JFrame PlayerWindow
     */
    public PlayerWindow() {
        appPlayer = new AppPlayer();
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        pathField = new javax.swing.JTextField();
        openButton = new javax.swing.JButton();
        songTimeSlide = new javax.swing.JSlider();
        playButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        volumeSlide = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        openButton.setText("...");
        openButton.addActionListener(evt -> openButtonActionPerformed(evt));

        playButton.setText("Play");
        playButton.addActionListener(evt -> playButtonActionPerformed(evt));

        pauseButton.setText("Pause");
        pauseButton.addActionListener(evt -> pauseButtonActionPerformed(evt));

        stopButton.setText("Stop");
        stopButton.addActionListener(evt -> stopButtonActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(songTimeSlide, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(playButton)
                                                .addGap(18, 18, 18)
                                                .addComponent(pauseButton)
                                                .addGap(18, 18, 18)
                                                .addComponent(stopButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(volumeSlide, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(pathField, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(openButton)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(pathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(openButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(songTimeSlide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(playButton)
                                                .addComponent(pauseButton)
                                                .addComponent(stopButton))
                                        .addComponent(volumeSlide, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    /** This audio file chooser event handler. The filter narrows the options the three formats below.
     * If the file chooser has executed successfully the AppPlayer will receive the song
     * and make it ready to play.
     * */
    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {
        FileFilter filter = new FileNameExtensionFilter("Audio Files", "mp3", "wma", "flac");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);

        if(returnValue == JFileChooser.APPROVE_OPTION){
            File songFile = fileChooser.getSelectedFile();
            pathField.setText(songFile.getPath());
            String path = null;
            try {
                path = songFile.toURI().toURL().toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            appPlayer.setSong(path);
        }
    }

    /** Play Button event handler*/
    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {
        appPlayer.resumeSong();
    }

    /** Pause Button event handler*/
    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        appPlayer.pauseSong();
    }

    /** Stop Button event handler*/
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {
        appPlayer.stopSong();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlayerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayerWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlayerWindow().setVisible(true);
            }
        });
    }

    // Variables declaration
    private JTextField pathField;
    private JButton openButton;
    private JButton stopButton;
    private JButton pauseButton;
    private JButton playButton;
    private JSlider songTimeSlide;
    private JSlider volumeSlide;
    private AppPlayer appPlayer;

    // End of variables declaration
}
