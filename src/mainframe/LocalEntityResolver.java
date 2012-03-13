/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mainframe;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 *
 * @author Administrator
 */
public class LocalEntityResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId) {
        String prjPath = System.getProperty("user.dir");
        return new InputSource(prjPath+"/workflow_2_8.dtd");
    }

}
