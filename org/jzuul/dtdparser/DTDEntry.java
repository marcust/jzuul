/*
 * 	CVS: $Id: DTDEntry.java,v 1.2 2004/07/16 16:22:34 marcus Exp $
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


/**
 * TODO Document new class
 * 
 * Created on Jun 7, 2004
 * 
 * 
 * @version $Revision: 1.2 $
 */
public abstract class DTDEntry {
    protected String name;

    public DTDEntry(String name) {
        if (name == null) throw new IllegalArgumentException("Name must not be null");
        this.name = name;
    }
    
    /**
     * @param name The name to set.
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

}
