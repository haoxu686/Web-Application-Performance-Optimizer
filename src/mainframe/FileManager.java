/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainframe;

import java.io.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Administrator
 */

/**All file operations*/
public class FileManager {

    /**Copy from dir src to dir dst*/
    public static void copyAllFiles(File src, File dst) {
        if (!src.isDirectory()) {
            FileManager.copyFile(src, dst);
        } else {
            if (!dst.exists()) {
                dst.mkdirs();
            }
            int length = src.getPath().length();
            File[] files = src.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileManager.copyAllFiles(files[i], new File(dst, files[i].getPath().substring(length)));
            }
        }
    }

    /**Copy file src to dir dst*/
    public static void copyFile(File src, File dst) {
        FileInputStream is = null;
        FileOutputStream os = null;
        byte[] buf = new byte[1024];
        int read = 0;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dst);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        try {
            while ((read = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, read);
            }
            is.close();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }

    public static void deleteAllFiles(File f) {
        if (f.isFile()) {
            f.delete();
            return;
        }
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            FileManager.deleteAllFiles(files[i]);
        }
        f.delete();
    }

    /**Unzip file src to path dst*/
    public static void extractFromZipFile(File src, File dst) throws Exception {
        ZipFile zipFile = new ZipFile(src);
        String basePath = "temp/";
        File dstDir = null;
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();

        /**Iterate over enumeration*/
        while (entries.hasMoreElements()) {
            dstDir = new File(basePath);
            ZipEntry entry = entries.nextElement();
            String fileName = entry.getName();
            int index = fileName.lastIndexOf("/");

            if (index != -1) {
                String dirName = fileName.substring(0, index);
                dstDir = new File(dstDir, dirName);
                if (!dstDir.exists()) {
                    dstDir.mkdirs();
                }
            }

            File dstFile = new File(dstDir, fileName.substring(index + 1));

            if (dstFile.isDirectory()) {
                continue;
            }

            OutputStream os = new BufferedOutputStream(new FileOutputStream(dstFile));
            InputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
            byte[] buf = new byte[1024];
            int read = 0;
            while ((read = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, read);
            }
            os.close();
            is.close();
        }
    }

    /**Zip all files from a dir*/
    public static void compressFile(File src, File dst) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(dst)));
        String path = "";
        FileManager.doZip(zos, src, path);
        zos.close();
    }

    /**Recursively zip*/
    private static void doZip (ZipOutputStream zos, File src, String path) throws Exception {
        if (src.isDirectory()) {
            File[] files = src.listFiles();
            for (int i = 0;i < files.length; i++) {
                FileManager.doZip(zos, files[i], path+"/"+files[i].getName());
            }
        } else {
            FileInputStream is = new FileInputStream(src);
            zos.putNextEntry(new ZipEntry(path));
            byte[] buf = new byte[1024];
            int read = 0;
            while ((read = is.read(buf, 0, 1024)) != -1) {
                zos.write(buf, 0, read);
            }
        }
    }

}
