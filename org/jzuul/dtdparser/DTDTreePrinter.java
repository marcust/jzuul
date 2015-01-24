/*
 * 	CVS: $Id: DTDTreePrinter.java,v 1.2 2004/07/16 16:22:34 marcus Exp $
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

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * TODO Document new class
 * 
 * Created on Jun 7, 2004
 * 
 * 
 * @version $Revision: 1.2 $
 */
public class DTDTreePrinter {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: DTDTreePrinter <file.dtd>");
        } else {
            DTDParser parser ;
            try {
               
                for (int i = 0; i < args.length; i++) {
                    System.out.println("Parsing " + args[i] + "...");
                    parser = new DTDParser(args[i]);
                    parser.printTree();
                }
                
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DTDParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
    }
}
