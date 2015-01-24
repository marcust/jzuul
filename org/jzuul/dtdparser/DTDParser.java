/*
 * 	CVS: $Id: DTDParser.java,v 1.6 2004/07/16 16:22:34 marcus Exp $
 * 
 *  This file is part of zuul.
 *
 *  zuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  zuul is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with zuul; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Copyrigth 2004 by marcus, leh
 * 
 */
package org.jzuul.dtdparser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Document new class
 * 
 * Created on Jun 7, 2004
 * 
 * 
 * @version $Revision: 1.6 $
 */
public class DTDParser {
    
    protected static void debug(String message) {
        if (false) System.out.println(message);
    }

    private DTDTreeElement root;

    private List flattenedElements;

    private HashMap entities, attributes, orphanedElements, elements;

    protected DTDParser() {
        flattenedElements = new Vector();
        entities = new HashMap();
        attributes = new HashMap();
        orphanedElements = new HashMap();
        elements = new HashMap();
    }

    public DTDParser(String filename) throws DTDParserException, FileNotFoundException, IOException {
        this();
        InputStream is = new FileInputStream(filename);
        this.parseStream(is);
    }

    public DTDParser(InputStream file) throws DTDParserException, IOException {
        this();
        this.parseStream(file);
    }

    private void parseStream(InputStream s) throws IOException, DTDParserException {
        StringBuffer fileContents = new StringBuffer(1024 * 1024); // Reserve 1
                                                                   // MB,
                                                                   // everything
                                                                   // else is
                                                                   // fucking
                                                                   // slow
                                                                   // because he
                                                                   // keeps
                                                                   // resizing
                                                                   // the buffer
                                                                   // under the
                                                                   // String all
                                                                   // the time
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        String line;
        while ((line = br.readLine()) != null) {
            fileContents.append(line + " ");
        }
        String strFileContents = fileContents.toString();
        strFileContents = strFileContents.replaceAll("\\n+", " ");
        strFileContents = strFileContents.replaceAll("\\t+", " ");
        strFileContents = strFileContents.replaceAll("\\r+", " ");
        strFileContents = strFileContents.replaceAll("\\f+", " ");
        strFileContents = strFileContents.replaceAll(" +", " ");

        List tags = tokeniseString(strFileContents);

        buildEntityMap(tags);

        buildAttributeList(getAttributeTokens(tags));
        buildElementTree(getElementTokens(tags));
        
   
    }

    private List tokeniseString(String contents) {
        contents = contents.replaceAll("<!--.+?-->", "");

        Vector tokens = new Vector(Arrays.asList(contents.split(">")));
        for (Iterator iter = tokens.iterator(); iter.hasNext();) {

            String element = (String) iter.next();
            element = element.replaceAll("^\\s+", "");
            if (element.equals("")) {
                iter.remove();
            } else {
                element += ">";
                DTDParser.debug(element);
            }
        }
        return tokens;
    }

    public DTDTreeElement getRoot() {
        return root;
    }

    public void writeTo(String filename) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void writeTo(OutputStream out) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected List getTokenByPrefix(String prefix, List tokens) {
        ArrayList tokenList = new ArrayList();
        for (Iterator iter = tokens.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            if (element.matches("^\\s*" + prefix + ".+")) {
                tokenList.add(element);
            }
        }
        DTDParser.debug("Found " + tokenList.size() + " for prefix " + prefix);
        return tokenList;
    }

    private List getElementTokens(List tokens) {
        return getTokenByPrefix("<!ELEMENT", tokens);
    }

    private List getAttributeTokens(List tokens) {
        return getTokenByPrefix("<!ATTLIST", tokens);
    }

    private void buildElementTree(List elmentTokens) throws DTDParserException {
        final String ow = "\\s*";

        final String begintag = ow + "<!ELEMENT" + ow;

        final String identifier = "[-\\w:]+";
        final String quantifier = "(?>\\?|\\*|\\+)?";

        final String subelementsep = ow + "(\\||,)?" + ow;
        
        final String op = "\\(";
        final String cp = "\\)";
        
        final String subelements = op + ".+" + cp + quantifier; 
        
        final String contents = "(EMPTY|ANY|" + subelements + ")";

        final String pattern = begintag + "(" + identifier + ")" + ow + contents + ow;

        DTDParser.debug("Pattern is: " + pattern);

        Pattern elementPattern = Pattern.compile(pattern);
        Pattern subelPattern = Pattern.compile("(" + identifier + ")(" + quantifier + ")(" + subelementsep + ")");

        for (Iterator iter = elmentTokens.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            element = resolveEntity(element);
            if (element == null) break;
            Matcher m = elementPattern.matcher(element);
            if (m.matches()) {
                DTDParser.debug("MATCH: " + element);
                for (int i = 1; i <= m.groupCount(); i++) {
                    DTDParser.debug(i + ": " + m.group(i));
                }
                String name = m.group(1);
                String subels = m.group(2);
                
                DTDTreeElement e = getOrCreateElement(name);

                if (subels.equalsIgnoreCase("EMPTY")) {
                    e.setType(DTDElement.EMPTY);
                } else { // we got some subelements
                    Matcher n = subelPattern.matcher(subels);
                    while (n.find()) {
                        DTDParser.debug("\t:" + n.group());
                        String subname = n.group(1);
                        String cardinal = n.group(2);
                        DTDTreeElement subel = null;
                        if ((subel = findOrphanedElement(subname)) == null) {
                            subel = new DTDTreeElement(subname);
                            this.flattenedElements.add(subel);
                            this.elements.put(subname,subel);
                        }
                        subel.setParent(e);
                        subel.setCardinality(cardinal);
                        e.addChild(subel);
                    }
                }
                Vector v = (Vector)attributes.get(e.getName());
                if (v != null) {
                    for (Iterator iterator = v.iterator(); iterator.hasNext();) {
                        DTDAttribute att = (DTDAttribute) iterator.next();
                        assignAttribute(e.getName(), att);
                    }
                }
                mergeSubElements(e);
                
                findRoot(e);
            } else {
                String message = "Syntax error at contents |" + element + "|\n";
                throw new DTDParserException(message);
            }
        }
    }

    public void printTree() {
        if (root == null) throw new IllegalStateException("No root Element found");
        debug("Starting to print tree");
        System.out.println(root.toString());
    }

    protected void findRoot(DTDTreeElement e) {
        while (e.getParent() != null) {
            if (e.getParent().getName().equals(e.name)) break;
            e = e.getParent();
        }
        root = e;
        DTDParser.debug("Root seems to be " + root.getName());

    }

    protected DTDTreeElement findOrphanedElement(String name) {
        if (name == null) throw new IllegalArgumentException("Name must not be null");
        DTDTreeElement el = (DTDTreeElement)orphanedElements.get(name);
        if (el != null ) {
            orphanedElements.remove(name);
            flattenedElements.add(el);
            elements.put(name,el);
        }
        return el;
    }

    protected DTDTreeElement getOrCreateElement(String name) {
        DTDElement el = getElement(name);
        if (el != null) { return (DTDTreeElement)el; }
        
        DTDTreeElement newEl = new DTDTreeElement(name);
        orphanedElements.put(name,newEl);
        return newEl;
    }

    protected DTDElement getElement(String name) {
        DTDTreeElement el = (DTDTreeElement)elements.get(name);
        if (el != null) { return el; }
   
        DTDParser.debug("Element " + name + " not found!");
        return null;
    }

    protected void mergeSubElements(DTDElement e) {
      
        debug("Merging sub elements for " + e.getName());
        for (Iterator iter = flattenedElements.iterator(); iter.hasNext();) {
            DTDTreeElement element = (DTDTreeElement) iter.next();
            if (element.getName().equals(e.getName())) {
                element.mergeSubElements(e);
            }
        }
    }

    protected void buildAttributeList(List tokens) throws DTDParserException {
        
        final String whitespace = "\\s*";

        final String begintag = whitespace + "<!ATTLIST" + whitespace;

        final String identifier = "[-\\w:]+";

        final String enumeration = "\\(" + whitespace + identifier + whitespace +
        	"\\)|\\((?>" + whitespace + identifier + whitespace  + "\\|)+" + whitespace + identifier
        	+ whitespace + "\\)";

        final String valuetype = "(CDATA|" + enumeration + ")";

        final String defvalue = "(?>\"[^\"]+\")";
        final String modifier = "(?>#REQUIRED|#IMPLIED|#FIXED)"; // FIXME There
        // are some
        // more

        final String flag = "(" + defvalue + "|" + modifier + ")";
        final String attdef = "(?>" + whitespace + "(" + identifier + ")" + whitespace + valuetype + whitespace + flag
                + whitespace + flag + "?)";

        final String pattern = begintag + "(" + identifier + ")" + "(" + ".+" + ")" + whitespace;

        DTDParser.debug("Pattern is: " + pattern);

        Pattern elementPattern = Pattern.compile(pattern);
        Pattern enumValuePattern = Pattern.compile("(" + identifier + ")" + "\\|?");
        Pattern attributePattern = Pattern.compile(attdef);

        for (Iterator iter = tokens.iterator(); iter.hasNext();) {
            String line = (String) iter.next();
            line = resolveEntity(line);
            if (line == null) break;
            Matcher m = elementPattern.matcher(line);
            if (m.matches()) {
                DTDParser.debug("MATCH: |" + line + "|");
                for (int i = 1; i <= m.groupCount(); i++) {
                    DTDParser.debug(i + ": " + m.group(i));
                }
                String element = m.group(1);
                String attributeLine = m.group(2);
                Matcher o = attributePattern.matcher(attributeLine);
                while (o.find()) {
                    for (int i = 1; i <= o.groupCount(); i++) {
                        DTDParser.debug("\t" + i + ": " + o.group(i));
                    }

                    String name = o.group(1);
                    String type = o.group(2);
                    String defvalOrFlag = o.group(3);

                    DTDAttribute a = new DTDAttribute(name);
                    a.setElementName(element);
                    if (type.equalsIgnoreCase("CDATA")) {
                        a.setType(DTDAttribute.CDATA);
                    } else {
                        Matcher n = enumValuePattern.matcher(type);
                        while (n.find()) {
                            a.addEnumValue(n.group(1));
                        }
                    }

                    if (defvalOrFlag.startsWith("#")) {
                        a.setFlag(defvalOrFlag);
                        if (defvalOrFlag.equals("#FIXED")) {
                            a.setDefaultValue(o.group(4));
                        }
                    } else {
                        a.setDefaultValue(defvalOrFlag.replaceAll("\"", ""));
                    }

                    addAttribute(a);
                }

            } else {
                String message = "Syntax error at contents |" + line + "|\n";
                throw new DTDParserException(message);

            }
        }

        
    }

    protected void assignAttribute(String elementName, DTDAttribute attribute) {
        DTDElement e = (DTDElement)orphanedElements.get(elementName);
        if (e == null) {
            e = getElement(elementName);
        }
        e.addAttribute(attribute);
    }

    private void buildEntityMap(List tokens) throws DTDParserException {
        final String whitespace = "\\s*";
        final String reftoken = "%?";

        final String name = "[-\\w\\d._]+";
        final String pedef = "\"([^\"]*)\"";

        final String prefix = whitespace + "<!ENTITY" + whitespace + reftoken + whitespace + "(" + name + ")"
                + whitespace;

        final String peDeclTail = pedef + whitespace;

        final String geDeclTail = "(?>SYSTEM|PUBLIC)" + whitespace + pedef + whitespace + pedef + "?" + whitespace;

        final String pattern = prefix + "(?>" + peDeclTail + "|" + geDeclTail + ")";

        DTDParser.debug("Pattern is: " + pattern);

        Pattern entityPattern = Pattern.compile(pattern);
        for (Iterator iter = tokens.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            String newelement = resolveEntity(element);
            if (newelement == null) {
                break;
            }
            if (newelement.matches(".+INCLUDE\\[.+")) System.exit(9);
            Matcher m = entityPattern.matcher(newelement);
            if (m.matches()) {
                String nameVal = m.group(1);
                String pedefVal = m.group(2);
                for (int i = 1; i <= m.groupCount(); i++) {
                    DTDParser.debug(i + ": " + m.group(i));
                }
                if (pedefVal == null) pedefVal = m.group(4);
                DTDParser.debug("Adding |" + nameVal + "|->" + pedefVal);
                entities.put(nameVal, pedefVal);
            } else {
                // String message = "Syntax error at contents |" + newelement +
                // "|\n";
                // throw new DTDParserException(message);
            }

        }

    }

    protected String resolveEntity(String element) throws DTDParserException {
        String newelement = new String(element);
        final String entref = "%([^%;\"]+);";

        Pattern entity = Pattern.compile(entref);
        
        if (newelement.matches("^\\s*<!\\[IGNORE.+")) return null;
        
        DTDParser.debug(newelement);
        Matcher m = entity.matcher(newelement);
        while (m.find()) {
            String name = m.group(1);
            DTDParser.debug("Resolving :" + name + ":");
            String replace = (String) entities.get(name);
            if (replace != null)// throw new DTDParserException("No entity " +
                                  //name + " near " + newelement);
                    newelement = newelement.replaceAll("%" + name + ";", replace);
            DTDParser.debug(newelement);
        }
        DTDParser.debug(newelement);
        newelement = newelement.replaceAll("<!\\[INCLUDE\\[","");
        DTDParser.debug("newelement is now: " + newelement);
        
        return newelement;
    }

    protected void addAttribute(DTDAttribute attribute) {
        if (attributes.containsKey(attribute.getElementName())) {
            ((Vector)attributes.get(attribute.getElementName())).add(attribute);
        } else {
            Vector v = new Vector();
            v.add(attribute);
            attributes.put(attribute.getElementName(),v);
        }
        
    }
    
}