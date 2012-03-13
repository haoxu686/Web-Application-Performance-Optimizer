/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mainframe;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.skin.*;

/**
 *
 * @author Administrator
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    UIManager.setLookAndFeel(new SubstanceModerateLookAndFeel());
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                MainFrame mainFrame = new MainFrame();
                mainFrame.setLocation(200, 100);
                mainFrame.setTitle("J2EE Project Modifier");
                mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                mainFrame.setVisible(true);
            }
        });
    }

}
