/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainframe;

import java.io.*;
import java.util.List;
import javassist.*;
import org.dom4j.*;
import org.dom4j.io.*;

/**
 *
 * @author Administrator
 */
public class Modifier {

    private File rootPath;
    private ClassPool pool;

    public Modifier() {
    }

    public Modifier(File rootPath, ClassPool pool) {
        this.rootPath = rootPath;
        this.pool = pool;
    }

    public void setRootPath(File rootPath) {
        this.rootPath = rootPath;
    }

    public void setClassPool(ClassPool pool) {
        this.pool = pool;
    }

    public void modifyOnClass(File src) throws Exception {
        int length = rootPath.getPath().length();
        String className = src.getPath().substring(length + 17);
        className = className.replace('\\', '.');
        className = className.substring(0, className.length() - 6);
        CtClass ctClass = pool.get(className);
        if (ctClass.isInterface()) {
            ctClass.writeFile(src.getPath().substring(0, length + 16));
            return;
        }
        CtField ctField = CtField.make("private static testutilities.db.LogDAO testutil_logDao = new testutilities.db.LogDAO();", ctClass);
        ctClass.addField(ctField);
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        for (int i = 0; i < ctMethods.length; i++) {
            if (ctMethods[i].isEmpty()) {
                continue;
            }
            StringBuffer beforeBuf = new StringBuffer();
            beforeBuf.append("testutilities.db.Log testutil_startLog = new testutilities.db.Log();\n");
            beforeBuf.append("testutil_startLog.setMethod(\"");
            beforeBuf.append(ctMethods[i].getLongName());
            beforeBuf.append("\");\n");
            beforeBuf.append("testutil_startLog.setOperation(\"in\");\n");
            beforeBuf.append("testutil_startLog.setRequest(testutilities.RequestInstance.getRequest());\n");
            beforeBuf.append("testutil_startLog.setTime(new Long(System.currentTimeMillis()));\n");
            beforeBuf.append("testutil_logDao.getSession().beginTransaction();\n");
            beforeBuf.append("testutil_logDao.getSession().save(testutil_startLog);\n");
            beforeBuf.append("testutil_logDao.getSession().getTransaction().commit();\n");
            ctMethods[i].insertBefore(beforeBuf.toString());

            StringBuffer afterBuf = new StringBuffer();
            afterBuf.append("testutilities.db.Log testutil_afterLog = new testutilities.db.Log();\n");
            afterBuf.append("testutil_afterLog.setMethod(\"");
            afterBuf.append(ctMethods[i].getLongName());
            afterBuf.append("\");\n");
            afterBuf.append("testutil_afterLog.setOperation(\"out\");\n");
            afterBuf.append("testutil_afterLog.setRequest(testutilities.RequestInstance.getRequest());\n");
            afterBuf.append("testutil_afterLog.setTime(new Long(System.currentTimeMillis()));\n");
            afterBuf.append("testutil_logDao.getSession().beginTransaction();\n");
            afterBuf.append("testutil_logDao.getSession().save(testutil_afterLog);\n");
            afterBuf.append("testutil_logDao.getSession().getTransaction().commit();\n");
            ctMethods[i].insertAfter(afterBuf.toString());
        }
        ctClass.writeFile(src.getPath().substring(0, length + 16));
    }

    public void modifyOnJSP(File src) throws Exception {
        int rootLength = rootPath.getPath().length();
        String path = src.getPath().substring(rootLength + 1);
        path = path.replace('\\', '/');
        String fileName = src.getName();
        fileName = fileName.substring(0, fileName.length() - 4);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<%\n");
        buffer.append("testutilities.db.LogDAO testutil_LogDao_");
        buffer.append(fileName);
        buffer.append(" = new testutilities.db.LogDAO();\n");
        buffer.append("testutilities.db.Log testutil_beforeLog_");
        buffer.append(fileName);
        buffer.append(" = new testutilities.db.Log();\n");
        buffer.append("testutil_beforeLog_");
        buffer.append(fileName);
        buffer.append(".setMethod(\"");
        buffer.append(path);
        buffer.append("\");\n");
        buffer.append("testutil_beforeLog_");
        buffer.append(fileName);
        buffer.append(".setOperation(\"in\");\n");
        buffer.append("testutil_beforeLog_");
        buffer.append(fileName);
        buffer.append(".setRequest(testutilities.RequestInstance.getRequest());\n");
        buffer.append("testutil_beforeLog_");
        buffer.append(fileName);
        buffer.append(".setTime(new Long(System.currentTimeMillis()));\n");
        buffer.append("testutil_LogDao_");
        buffer.append(fileName);
        buffer.append(".getSession().beginTransaction();\n");
        buffer.append("testutil_LogDao_");
        buffer.append(fileName);
        buffer.append(".getSession().save(testutil_beforeLog_");
        buffer.append(fileName);
        buffer.append(");\n");
        buffer.append("testutil_LogDao_");
        buffer.append(fileName);
        buffer.append(".getSession().getTransaction().commit();\n");
        buffer.append("%>\n");

        FileInputStream fs = new FileInputStream(src);
        BufferedReader bufRdr = new BufferedReader(new InputStreamReader(fs));
        while (true) {
            String exp = bufRdr.readLine();
            if (exp == null) {
                break;
            }
            buffer.append(exp);
            buffer.append("\n\r");
        }
        buffer.append("<%\n");
        buffer.append("testutilities.db.Log testutil_afterLog_");
        buffer.append(fileName);
        buffer.append(" = new testutilities.db.Log();\n");
        buffer.append("testutil_afterLog_");
        buffer.append(fileName);
        buffer.append(".setMethod(\"");
        buffer.append(path);
        buffer.append("\");\n");
        buffer.append("testutil_afterLog_");
        buffer.append(fileName);
        buffer.append(".setOperation(\"out\");\n");
        buffer.append("testutil_afterLog_");
        buffer.append(fileName);
        buffer.append(".setRequest(testutilities.RequestInstance.getRequest());\n");
        buffer.append("testutil_afterLog_");
        buffer.append(fileName);
        buffer.append(".setTime(new Long(System.currentTimeMillis()));\n");
        buffer.append("testutil_LogDao_");
        buffer.append(fileName);
        buffer.append(".getSession().beginTransaction();\n");
        buffer.append("testutil_LogDao_");
        buffer.append(fileName);
        buffer.append(".getSession().save(testutil_afterLog_");
        buffer.append(fileName);
        buffer.append(");\n");
        buffer.append("testutil_LogDao_");
        buffer.append(fileName);
        buffer.append(".getSession().getTransaction().commit();\n");
        buffer.append("%>\n");

        PrintWriter prt = new PrintWriter(new FileOutputStream(src));
        prt.println(buffer.toString());
        prt.flush();
        fs.close();
        prt.close();
    }

    public void modifyOnWebXML(File f, List<String> requests) throws Exception {
        SAXReader saxReader = new SAXReader();
        saxReader.setEntityResolver(new LocalEntityResolver());
        Document document = saxReader.read(f);
        Element rootElement = document.getRootElement();
        Element filterElement = rootElement.addElement("filter");
        Element filterNameElement = filterElement.addElement("filter-name");
        filterNameElement.setText("request");
        Element filterClassElement = filterElement.addElement("filter-class");
        filterClassElement.setText("testutilities.RequestFilter");
        for (int i = 0; i < requests.size(); i++) {
            Element initParamElement = filterElement.addElement("init-param");
            Element paramNameElement = initParamElement.addElement("param-name");
            paramNameElement.setText(Integer.toString(i));
            Element paramValueElement = initParamElement.addElement("param-value");
            paramValueElement.setText(requests.get(i));
        }
        Element filterMappingElement = rootElement.addElement("filter-mapping");
        Element filterNameMElement = filterMappingElement.addElement("filter-name");
        filterNameMElement.setText("request");
        Element URLElement = filterMappingElement.addElement("url-pattern");
        URLElement.setText("/*");
        OutputFormat format = new OutputFormat(" ", true);
        format.setEncoding("UTF-8");
        format.setLineSeparator("\n\r");
        XMLWriter writer = new XMLWriter(new FileOutputStream(f), format);
        writer.write(document);
        writer.close();
    }

    public void modifyOnHibernateXML(File f, String userName, String password, String dbName) throws Exception {
        SAXReader saxReader = new SAXReader();
        saxReader.setEntityResolver(new LocalEntityResolver());
        Document document = saxReader.read(f);
        Element rootElement = document.getRootElement();
        Element sessionFactoryElement = rootElement.element("session-factory");
        List<Element> properties = sessionFactoryElement.elements("property");
        for (int i = 0; i < properties.size(); i++) {
            Element property = properties.get(i);
            String propertyName = property.attributeValue("name");
            if (propertyName.equals("connection.username")) {
                property.setText(userName);
            }
            if (propertyName.equals("connection.password")) {
                property.setText(password);
            }
            if (propertyName.equals("connection.url")) {
                property.setText("jdbc:mysql://localhost:3306/" + dbName);
            }
        }
        OutputFormat format = new OutputFormat(" ", true);
        format.setEncoding("UTF-8");
        format.setLineSeparator("\n\r");
        XMLWriter writer = new XMLWriter(new FileOutputStream(f), format);
        writer.write(document);
        writer.close();

        File log = new File(f, "../Log.hbm.xml");
        document = saxReader.read(log);
        Element logRootElement = document.getRootElement();
        Element logClassElement = logRootElement.element("class");
        logClassElement.addAttribute("catalog", dbName);
        writer = new XMLWriter(new FileOutputStream(log), format);
        writer.write(document);
        writer.close();

        File request = new File(f, "../Request.hbm.xml");
        document = saxReader.read(request);
        Element requestRootElement = document.getRootElement();
        Element requestClassElement = requestRootElement.element("class");
        requestClassElement.addAttribute("catalog", dbName);
        writer = new XMLWriter(new FileOutputStream(request), format);
        writer.write(document);
        writer.close();
    }
}
