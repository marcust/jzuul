/*
 * 	CVS: $Id: BeamRoomPropertyComposite.java,v 1.3 2004/07/19 12:25:30 marcus Exp $
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
package org.jzuul.gdk.swt;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;


/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.3 $
 */
public class BeamRoomPropertyComposite extends RoomPropertyComposite {
    Element room;
    String map;
    
    /**
     * @param arg0
     * @param arg1
     */
    public BeamRoomPropertyComposite(Composite arg0, int arg1, Element roomElement, String mapName) {
        super(arg0, arg1);
        room = roomElement;
        map = mapName;
        GridData wvDat = new GridData(GridData.FILL_BOTH);
        wvDat.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
        this.setLayoutData(wvDat);
        this.setLayout(new GridLayout(5,false));
    }
    
    public void showRoomProperties() {
        
    }
    
    
    public void clear() {}
    
}
