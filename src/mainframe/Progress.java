/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mainframe;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Administrator
 */
public class Progress implements Runnable {

    private String message;
    private int state;
    private JProgressBar jProgress;
    private JLabel jLabelNote;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setProgress(JProgressBar jProgress) {
        this.jProgress = jProgress;
    }

    public void setNoteLabel(JLabel jLabelNote) {
        this.jLabelNote = jLabelNote;
    }

    public String getMessage() {
        return this.message;
    }

    public int getState() {
        return this.state;
    }
    
    public JProgressBar getProgress() {
        return this.jProgress;
    }

    public JLabel getNoteLabel() {
        return this.jLabelNote;
    }
    
    public void run() {
        jProgress.setValue(state);
        jLabelNote.setText(message);
    }

}
