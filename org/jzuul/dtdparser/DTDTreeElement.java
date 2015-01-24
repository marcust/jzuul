/*
 * 	CVS: $Id: DTDTreeElement.java,v 1.6 2004/07/17 12:49:28 leh Exp $
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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * TODO Document new class
 * 
 * Created on Jun 7, 2004
 * 
 * 
 * @version $Revision: 1.6 $
 */
public class DTDTreeElement extends DTDElement {

    public static final int ONE_TIME = 1;

    public static final int ONE_OR_ZERO_TIMES = 2;

    public static final int ONE_OR_MORE_TIMES = 3;

    public static final int ZERO_OR_MORE_TIMES = 4;

    protected int card;

    protected DTDTreeElement parent;

    /**
     * @param name
     */
    public DTDTreeElement(String name) {
        super(name);
        setCardinality(ONE_TIME);
    }

    public int getCardinality() {
        return 0;
    }

    public void setCardinality(String cardToken) {
        if (cardToken.equals("*")) setCardinality(ZERO_OR_MORE_TIMES);
        if (cardToken.equals("?")) setCardinality(ONE_OR_ZERO_TIMES);
        if (cardToken.equals("+")) setCardinality(ONE_OR_MORE_TIMES);

    }

    public void setCardinality(int card) {
        this.card = card;
    }

    public void setParent(DTDTreeElement parent) {
        DTDParser.debug(this.getName() + " Got new Parent " + parent.getName());
        this.parent = parent;
    }

    public DTDTreeElement getParent() {
        return parent;
    }

    public String toString() {
        return this.toString(0, new Vector());
    }

    public String toString(int indent, List seen) {
        // FIXME This may lead to a loop if parent.parent is a child
        DTDParser.debug("Depth is " + indent);
        String retval = "";

        if (!seen.contains(name)) {

            retval += repeat(' ', indent);
            retval += name + cardToString() + "\n";
            System.out.println(name);

            for (Iterator iter = attributes.values().iterator(); iter.hasNext();) {
                DTDAttribute at = (DTDAttribute) iter.next();
                retval += at.toString(indent);

            }
            for (Iterator iter = children.values().iterator(); iter.hasNext();) {
                DTDTreeElement element = (DTDTreeElement) iter.next();
        
                seen.add(name);
                retval += element.toString(indent + 1, seen);
                seen.remove(name);

            }
        } else {
            retval += repeat(' ', indent) + name + "-->" + "\n";
            seen.remove(name);
        }
        return retval;

    }

    private String cardToString() {
        switch (card) {
        case ONE_TIME:
            return "";
        case ONE_OR_MORE_TIMES:
            return "+";
        case ZERO_OR_MORE_TIMES:
            return "*";
        case ONE_OR_ZERO_TIMES:
            return "?";
        }
        return "ERROR";
    }

    private String repeat(char sign, int times) {
        String retval = "";
        for (int i = 1; i < times; i++) {
            retval += sign;
        }
        return retval;
    }

}