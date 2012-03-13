/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainframe;

import java.awt.Component;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Administrator
 */

/**All business logic
 * javassist, dom4j and text editing techniques used
 * javassist used to modify .class files
 * dom4j used to modify xml files and JSPs are treated as plain text files
 * Things done to the selected J2EE project:
 * Created a filter to intercept all HTTP requests, assign a global unique ID for each request
 * Tomcat creates a separate thread for each request so this ID is made thread-local
 * This ensures that different methods within a thread will retrieve the same ID and IDs accross different threads are different*/
public class Generate implements Runnable {

    private File rootPath;
    private File dstPath;
    private CheckNode rootNode;
    private Modifier modifier;
    private String type;
    private String prjName;
    private ClassPool pool;
    private Component parentCompenent;
    private Note note;
    private Progress progress;
    private JProgressBar jProgress;
    private JLabel jLabelNote;
    private String dbUserName;
    private String dbPassword;
    private String dbName;
    private List<String> requests;

    public Generate() {
        note = new Note();
        progress = new Progress();
    }

    public void setRootPath(File rootPath) {
        this.rootPath = rootPath;
    }

    public void setDstPath(File dstPath) {
        this.dstPath = dstPath;
    }

    public void setRootNode(CheckNode rootNode) {
        this.rootNode = rootNode;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentCompenent = parentComponent;
        this.note.setParentComponent(parentComponent);
    }

    public void setProgressBar(JProgressBar jProgress) {
        this.jProgress = jProgress;
        progress.setProgress(jProgress);
    }

    public void setNoteLabel(JLabel jLabelNote) {
        this.jLabelNote = jLabelNote;
        progress.setNoteLabel(jLabelNote);
    }

    public void setDBUserName(String userName) {
        this.dbUserName = userName;
    }

    public void setDBPassword(String password) {
        this.dbPassword = password;
    }

    public void setDBName(String dbName) {
        this.dbName = dbName;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    public File getRootPath() {
        return this.rootPath;
    }

    public File getDstPath() {
        return this.dstPath;
    }

    public CheckNode getRootNode() {
        return this.rootNode;
    }

    public Modifier getModifier() {
        return this.modifier;
    }

    public String getType() {
        return this.type;
    }

    public String getPrjName() {
        return this.prjName;
    }

    public Component getParentComponent() {
        return this.parentCompenent;
    }

    public String getDBUserName() {
        return this.dbUserName;
    }

    public String getDBPassword() {
        return this.dbPassword;
    }

    public String getDBName() {
        return this.dbName;
    }

    public List<String> getRequests() {
        return this.requests;
    }

    public void run() {
        progress.setState(0);
        progress.setMessage("Scanning class paths...");
        SwingUtilities.invokeLater(progress);
        /**Using javassit to modify .class files
         * Recompile is needed so first ClassPath should be configured right*/
        File libDir = new File("./temp/WEB-INF/lib");
        pool = new ClassPool();
        pool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));//Add system classpath
        try {
            File[] libs = libDir.listFiles();
            for (int i = 0; i < libs.length; i++) {
                pool.appendClassPath(libs[i].getPath());//libraires from the original project
            }
            pool.appendClassPath("./temp/WEB-INF/classes");//classes from the original project
        } catch (Exception ex) {
            ex.printStackTrace();
            note.setMessage("Project hiearchy damaged");
            SwingUtilities.invokeLater(note);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        File compileLib = new File("./compilelib");//libraries needed to compile servlets, copied from tomcat
        try {
            File[] compileLibs = compileLib.listFiles();
            for (int i = 0; i < compileLibs.length; i++) {
                pool.appendClassPath(compileLibs[i].getPath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            note.setMessage("Files from compilelib damaged");
            SwingUtilities.invokeLater(note);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        /**Add Hibernate libs from copylib*/
        File copyLib = new File("./copylib");
        try {
            File[] copyLibs = copyLib.listFiles();
            for (int i = 0; i < copyLibs.length; i++) {
                pool.appendClassPath(copyLibs[i].getPath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            note.setMessage("Files from copylib damaged");
            SwingUtilities.invokeLater(note);
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        File copyClass = new File("./copyclasses");//Inject testing tools (e.g.request filters)
        try {
            pool.appendClassPath(copyClass.getPath());
        } catch (Exception ex) {
            ex.printStackTrace();
            note.setMessage("Files from copyclasses damaged");
            SwingUtilities.invokeLater(note);
            Logger.getLogger(Generate.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        progress.setState(30);
        progress.setMessage("Configuring...");
        SwingUtilities.invokeLater(progress);
        
        modifier.setClassPool(pool);
        modifier.setRootPath(rootPath);

        SwingUtilities.invokeLater(progress);
        File webXML = new File(rootPath, "WEB-INF/web.xml");//Edit web.xml
        if (!webXML.exists()) {
            note.setMessage("web.xml damaged");
            SwingUtilities.invokeLater(note);
            return;
        }
        try {
            modifier.modifyOnWebXML(webXML, requests);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            note.setMessage("Failed to write back web.xml");
            SwingUtilities.invokeLater(note);
            return;
        }
        /**Configure database connections of logging, not conflict with db connection from the original project*/
        File hibernateXML = new File(copyClass, "testutilities/db/hibernate.cfg.xml");
        try {
            modifier.modifyOnHibernateXML(hibernateXML, dbUserName, dbPassword, dbName);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Generate.class.getName()).log(Level.SEVERE, null, ex);
            note.setMessage("Failed to write back hibernate.cfg.xml");
            SwingUtilities.invokeLater(note);
            return;
        }
        progress.setState(40);
        progress.setMessage("Generating");
        SwingUtilities.invokeLater(progress);

        /**.class and JSP*/
        File[] files = rootPath.listFiles();
        try {
            for (int i = 0; i < files.length; i++) {
                this.modifyFiles(files[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            note.setMessage("Unexpected failure");
            SwingUtilities.invokeLater(note);
            return;
        }
        progress.setState(80);
        progress.setMessage("Exporting");
        SwingUtilities.invokeLater(progress);
        
        FileManager.copyAllFiles(copyLib, new File(rootPath, "WEB-INF/lib"));
        FileManager.copyAllFiles(copyClass, new File(rootPath, "WEB-INF/classes"));

        if (type.equals("Directory")) {
            FileManager.copyAllFiles(rootPath, dstPath);
        } else if (type.equals("WAR Archive")) {
            if (!dstPath.exists()) {
                dstPath.mkdirs();
            }
            try {
                FileManager.compressFile(rootPath, new File(dstPath, prjName + ".war"));
            } catch (Exception ex) {
                ex.printStackTrace();
                note.setMessage("Failed to compress");
                SwingUtilities.invokeLater(note);
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        progress.setState(100);
        progress.setMessage("Success!");
        SwingUtilities.invokeLater(progress);
        note.setMessage("Success");
        SwingUtilities.invokeLater(note);
    }

    private void modifyFiles(File f) throws Exception {
        int length = rootPath.getPath().length();
        String path = f.getPath().substring(length);
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                this.modifyFiles(files[i]);
            }
        } else {
            CheckNode node = rootNode.getNodeByPath(path);
            if (node == null) {
                return;
            }
            if (node.isSelected() == false) {
                return;
            }
            if (path.toLowerCase().endsWith(".class")) {
                modifier.modifyOnClass(f);
            } else if (path.toLowerCase().endsWith(".jsp")) {
                modifier.modifyOnJSP(f);
            }
        }
    }
}
