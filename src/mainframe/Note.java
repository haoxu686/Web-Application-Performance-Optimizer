/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mainframe;

import java.awt.Component;

/**
 *
 * @author Administrator
 */
public class Note implements Runnable {

    private String message;
    private Component parentComponent;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public String getMessage() {
        return this.message;
    }

    public Component getParentComponent() {
        return this.parentComponent;
    }
    
    public void run() {
        javax.swing.JOptionPane.showMessageDialog(parentComponent, message);
    }

}
