/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cadastrefilesorter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import java.util.logging.*;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author filippov
 * 
 */
public class CadastreFileSorter {

    /**
     * @param args the command line arguments
     * args[0] sources folder
     * 
     */
    private final static Logger logger = Logger.getLogger("cadastrefilesorter");
    
    public static void main(String[] args) {
        String consoleEncoding = System.getProperty("consoleEncoding");
        if (consoleEncoding != null) {
            try {
                System.setOut(new PrintStream(System.out, true, consoleEncoding));
            } catch (java.io.UnsupportedEncodingException ex) {
                System.err.println("Unsupported encoding set for console: "+consoleEncoding);
            }
        }
        DocumentBuilderFactory domFactory;
        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = null;

        File inPath = new File(args[0]);
        File xmlfile = null;
        
        File[] files = inPath.listFiles();
        for (File file : files) {
            try {
                xmlfile = file;
                builder = domFactory.newDocumentBuilder();
            }catch (ParserConfigurationException ex) {
                logger.severe(ex.getMessage());
                return;
            }
            try {
                Document doc;
                doc = builder.parse(xmlfile);
                for (int i = 0; i<doc.getFirstChild().getChildNodes().getLength(); i++) {
                    if (doc.getFirstChild().getChildNodes().item(i).getLocalName().equals("Coord_Systems")) {
                        String coordSys = doc.getFirstChild().getChildNodes().item(i).getFirstChild().getAttributes().getNamedItem("Name").getTextContent();
                        //logger.log(Level.INFO, coordSys);
                        Path targetDir = Paths.get(inPath.getParentFile() + File.separator + 
                                coordSys + File.separator);
                        Path target = Paths.get(inPath.getParentFile() + File.separator + 
                                coordSys + File.separator + file.getName());
                        if (Files.exists(targetDir)) {
                            Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Files.createDirectory(targetDir);
                            logger.log(Level.INFO, "output directory created: {0}", target.toString());
                            Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }catch (SAXException | IOException | DOMException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
            }
        }
    }
    
}
