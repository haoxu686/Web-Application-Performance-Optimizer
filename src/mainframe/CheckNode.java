/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mainframe;

import java.util.StringTokenizer;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Administrator
 */
public class CheckNode extends DefaultMutableTreeNode {
    private boolean isSelected;
    private String value;
    public CheckNode(String value) {
        super(value);
        this.value = value;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected == false) {
            for (int i = 0; i < this.getChildCount(); i++) {
                CheckNode child = (CheckNode) this.getChildAt(i);
                if (child == null) {
                    continue;
                }
                child.setSelected(false);
            }
         } else {
            CheckNode parentNode = (CheckNode) this.getParent();
            if (parentNode == null) {
                return;
            }
            parentNode.setSelected(true);
         }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CheckNode getChildByValue(String value) {
        CheckNode node = null;
        for (int i = 0; i < this.getChildCount(); i++) {
            CheckNode child = (CheckNode) this.getChildAt(i);
            if (child.getValue().equals(value)) {
                node = child;
            }
        }
        return node;
    }

    public CheckNode getNodeByPath(String path) {
        StringTokenizer token = new StringTokenizer(path, "\\");
        CheckNode node = this;
        while (token.hasMoreElements()) {
            String nextstep = token.nextToken();
            node = node.getChildByValue(nextstep);
            if (node == null) {
                break;
            }
        }
        return node;
    }
    
}
