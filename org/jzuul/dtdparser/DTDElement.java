/*
 * 	CVS: $Id: DTDElement.java,v 1.6 2004/07/16 16:22:34 marcus Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * TODO Document new class
 * 
 * Created on Jun 7, 2004
 * 
 * 
 * @version $Revision: 1.6 $
 */
public class DTDElement extends DTDEntry {
    public static final int EMPTY = 10;
    public static final int PCDATA = 20;
    public static final int ELEMENT = 30;

    protected Map children, attributes;
    protected int type;

    public DTDElement(String name) {
        super(name);
        children = new HashMap();
        attributes = new HashMap();
    }
    
    public List getChildElements() {
        return new ArrayList(children.values()); 
    }
    
    public DTDTreeElement getChildElement(String name) {
        return (DTDTreeElement)children.get(name);
    }
    
    public List getAttributes() {
        return new ArrayList(attributes.values());
    }
    
    public DTDAttribute getAttribute(String name) {
        return null;
    }
        
    public void addAttribute(DTDAttribute at) {
        attributes.put(at.getName(), at);
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public void addChild(DTDElement child) {
        this.children.put(child.getName(),child);
    }

    public void mergeSubElements(DTDElement originElement) {
        List child = originElement.getChildElements();
        for (Iterator iter = child.iterator(); iter.hasNext();) {
            DTDElement element = (DTDElement) iter.next();
                this.addChild(element);
        }
    }
    
}
