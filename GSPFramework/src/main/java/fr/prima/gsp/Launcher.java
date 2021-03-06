/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.gsp;

import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.CModuleFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author remonet
 */
public class Launcher {

    public static void main(String[] args) throws IOException {
        // TODO catch anything and display correct message (and localized)
        if (Arrays.asList(args).contains("--help")) {
            showHelp();
            return;
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return "=== " + record.getLevel().getName() + ": " + record.getMessage() + "\n";
            }
        });
        consoleHandler.setLevel(Level.ALL);
        Logger.getLogger(Assembly.class.getName()).addHandler(consoleHandler);
        Logger.getLogger(Assembly.class.getName()).setLevel(Level.FINE);
        Logger.getLogger(CModuleFactory.class.getName()).addHandler(consoleHandler);
        Logger.getLogger(CModuleFactory.class.getName()).setLevel(Level.FINE);


        Launcher cli = new Launcher();
        Assembly assembly = cli.load(args).getOr(null);
        if (assembly == null) {
            System.err.println("Could not load pipeline file: '" + args[0] + "'");
        }
    }

    private static void showHelp() {
        System.err.println("TODO");
    }

    private Option<Assembly> load(InputStream inputStream, String[] parameters) throws IOException {
        final Map<String, String> settings = readParameters(parameters);
        final List<String> unreplaced = new ArrayList<String>();

        final Set<String> modulesNotFound = new LinkedHashSet<String>();
        for (String p : settings.keySet()) {
            if (p.contains(".")) {
                modulesNotFound.add(p.split("[.]")[0]);
            }
        }

        final AtomicBoolean done = new AtomicBoolean(false);
        Assembly a = new Assembly();
        a.readFromXML(inputStream, Option.<Assembly.ReadFromXMLHandler>create(new Assembly.ReadFromXMLHandler() {
            @Override
            public void namespace(Element e) {
                patch(e);
            }

            @Override
            public void module(Element e) {
                patch(e);
                String id = e.getAttribute("id");
                if (modulesNotFound.remove(id)) {
                    // there were some parameters to apply
                    for (String p : settings.keySet()) {
                        if (p.startsWith(id + ".")) {
                            String att = p.split("[.]")[1];
                            e.setAttribute(att, settings.get(p));
                        }
                    }
                }
            }

            @Override
            public void connector(Element e) {
                patch(e);
            }

            @Override
            public void factory(Element e) {
                patch(e);
            }

            private void patch(Element e) {
                NamedNodeMap attributes = e.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    String val = attributes.item(i).getTextContent();
                    val = Utils.replaceAll(val, "[$][{][a-zA-Z0-9-_]*[}]", new Utils.Replacement() {
                        public String getReplacement(String matched) {
                            String var = matched.substring(2, matched.length() - 1);
                            String res = settings.get(var);
                            if (res == null) {
                                unreplaced.add(var);
                                return matched;
                            }
                            return res;
                        }
                    });
                    attributes.item(i).setTextContent(val);
                }
            }

            @Override
            public void beforeInit() {
                // TODO there should be some error reporting when readFromXML fails (currently it swallows exceptions)
                Assembly.AssemblySubException ex = new Assembly.AssemblySubException();
                if (!unreplaced.isEmpty()) {
                    ex.absorb(new Assembly.AssemblySubException("Unset variables: " + unreplaced.toString()));
                }
                if (!modulesNotFound.isEmpty()) {
                    ex.absorb(new Assembly.AssemblySubException("Modules from command line not found: " + modulesNotFound.toString()));
                }
                ex.throwIfNotEmpty();
            }

            @Override
            public void done() {
                done.set(true);
            }
            
            
        }));
        return Option.create(done.get() ? a : null);
    }

    private Map<String, String> readParameters(String[] parameters) {
        Map<String, String> res = new LinkedHashMap<String, String>(); // preserve insertion order
        // TODO use iterator and implement advanced stuffs (grouping)
        for (String p : parameters) {
            if (p.matches("[a-zA-Z0-9-_]+(.[a-zA-Z0-9-_]+)?=.*")) {
                String[] parts = p.split("=", 2);
                res.put(parts[0], parts[1]);
            }
        }
        return res;
    }

    // --------------------------------- //
    public Option<Assembly> load(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }
        Option<File> xmlDescriptorFile = findXmlDescriptor(args[0]);
        if (!xmlDescriptorFile.isPresent()) {
            return Option.create((Assembly) null);
        }
        String[] params = Arrays.copyOfRange(args, 1, args.length);
        return load(new FileInputStream(xmlDescriptorFile.get()), params);
    }

    private Option<File> findXmlDescriptor(String basename) {
        File res = new File(basename);
        // TODO: add environment variables handling here
        if (res.exists() && res.canRead()) {
            return Option.create(res);
        }
        return Option.create((File) null);
    }
}
