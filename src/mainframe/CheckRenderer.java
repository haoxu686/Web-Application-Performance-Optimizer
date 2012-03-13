/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mainframe;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Administrator
 */


public class CheckRenderer implements TreeCellRenderer {

    private JCheckBox checkBox;//声明一个JTree节点中的打勾的框框

    public CheckRenderer() {
        checkBox = new JCheckBox();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        CheckNode node = (CheckNode) value;
        checkBox.setText(node.getValue());
        checkBox.setSelected(node.isSelected());//这条语句直接控制屏幕上的框框打勾了没有
        return checkBox;
    }

}
