/*
 * 	CVS: $Id: XMLSchemaValidator.java,v 1.2 2004/07/16 16:22:35 marcus Exp $
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
package org.jzuul.tools;

import java.io.File;
import java.io.IOException;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * TODO Document new class
 * 
 * Created on Jun 4, 2004
 * 
 * 
 * @version $Revision: 1.2 $
 */
public class XMLSchemaValidator {
    private class MyErrorHandler implements ErrorHandler {
       
        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
         */
        public void error(SAXParseException exception) throws SAXException {
            System.out.println(exception.getMessage());
            throw exception;
            
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
         */
        public void fatalError(SAXParseException exception) throws SAXException {
            System.out.println(exception.getMessage());
            throw exception;
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
         */
        public void warning(SAXParseException exception) throws SAXException {
            System.out.println(exception.getMessage());
            throw exception;
        }
        
    }
    
    
    public static void main(String[] args) {
        if (args.length == 0) {
           System.out.println("Usage: XMLSchemaValidator file.xml"); 
           System.exit(0);
        }
        
        SAXBuilder builder =
            new SAXBuilder("org.apache.xerces.parsers.SAXParser", false);

        builder.setFeature("http://xml.org/sax/features/namespaces",true);
        builder.setFeature("http://apache.org/xml/features/validation/schema", true);
        builder.setFeature("http://apache.org/xml/features/standard-uri-conformant", true);
        
        XMLSchemaValidator bar = new XMLSchemaValidator();
        
        MyErrorHandler foo = bar.new MyErrorHandler();
        builder.setErrorHandler(foo);
        
        try {
            builder.build(new File(args[0]));
            System.out.println("The file seem to be ok");
        } catch (JDOMException e) {
            System.out.println("Parser Error: " + e.getMessage());
        
        } catch (IOException e) {
            System.out.println("IOError: " + e.getMessage());
        
            
        }
        
    }
}
