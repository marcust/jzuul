/*
 * 	CVS: $Id: DTDAttribute.java,v 1.5 2004/07/16 16:22:34 marcus Exp $
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
import java.util.List;


/**
 * TODO Document new class
 * 
 * Created on Jun 7, 2004
 * 
 * 
 * @version $Revision: 1.5 $
 */
public class DTDAttribute extends DTDEntry {
    public static final int CDATA = 2;
    public static final int ENUMERATION = 4;
    
    public static final int REQUIRED = 10;
    public static final int IMPLIED = 20;
    public static final int FIXED = 30;
    
    protected DTDElement element;
    protected int type;
    protected List enumValues;
    protected String defaultValue;
    protected int flag;
    protected String elementName;
    
    public DTDAttribute(String name) {
        super(name);
        type = ENUMERATION;
        enumValues = new ArrayList();
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public List getEnumeration() {
        return enumValues;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }

    public void assignElement(DTDElement e) {
        if (e == null) throw new IllegalArgumentException("DTDElement must not be null");
        element = e;
        element.addAttribute(this);
    }
    
    public boolean hasDefaultValue() {
        return (defaultValue != null);
    }
    
    public void addEnumValue(String value) {
        enumValues.add(value);
    }
    
    public DTDElement getElement() {
        return this.element;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    protected void setFlag(String flag) {
        if (flag.equalsIgnoreCase("#REQUIRED")) setFlag(REQUIRED);
        if (flag.equalsIgnoreCase("#IMPLIED")) setFlag(IMPLIED);
        if (flag.equalsIgnoreCase("#FIXED")) setFlag(FIXED);
    }
    
    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return this.flag;
    }
    
    public String toString(int indent) {
        String retval = "";
        for (int i=0; i <= indent; i++) retval += " ";
        retval += " :" + getName();
        if (this.hasDefaultValue()) retval += "=" + getDefaultValue();
        if (this.getType() == CDATA) {
            retval += " CDATA";
        }
        if (this.getType() == ENUMERATION) {
            retval += " ENUMERATION";
        }
        if (this.getFlag() == REQUIRED) {
            retval += " REQUIRED";
        }
        if (this.getFlag() == IMPLIED) {
            retval += " IMPLIED";
        }
        if (this.getFlag() == FIXED) {
            retval += " FIXED";
        }

        
        retval += "\n";
        
        return retval;
    }
    
    public String getElementName() {
        return this.elementName;
    }
    
    public void setElementName(String name) {
        this.elementName = name;
    }
    
}
