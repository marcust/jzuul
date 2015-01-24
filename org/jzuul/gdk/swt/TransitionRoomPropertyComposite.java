/*
 * 	CVS: $Id: TransitionRoomPropertyComposite.java,v 1.9 2004/07/25 21:40:56 marcus Exp $
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;


/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.9 $
 */
public class TransitionRoomPropertyComposite extends RoomPropertyComposite {
    //Combo itemCombo;
    Element room;
    String map;
    private Combo target;
    private Button isFinal;
    
    /**
     * @param arg0
     * @param arg1
     */
    public TransitionRoomPropertyComposite(Composite arg0, int arg1,Element roomElement,String mapName) {
        super(arg0, arg1);
        room = roomElement;
        map = mapName;
        GridData wvDat = new GridData(GridData.FILL_BOTH);
        wvDat.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
        this.setLayoutData(wvDat);
        this.setLayout(new GridLayout(2,false));
        
        Button precondition = new Button(this, SWT.PUSH);
        GridData pDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        pDat.horizontalSpan = 2;
        precondition.setLayoutData(pDat);
        precondition.setText(Messages.getString("EDIT_PRECONDITIONS")); //$NON-NLS-1$
        precondition.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                PreconditionEditorDialog ed = new PreconditionEditorDialog(new Shell(arg0.display));
                ed.open(room);
                
                
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        
        Label targetLabel = new Label(this, SWT.NONE);
        targetLabel.setText(Messages.getString("TARGET_MAP")); //$NON-NLS-1$
        
        target = new Combo(this, SWT.READ_ONLY);
        GridData tDat = new GridData(GridData.FILL_HORIZONTAL);
        target.setLayoutData(tDat);
        String[] items = JdomHelpers.getMapNames(room);
        if (items != null) {
            target.setItems(items);
            if (target.getItemCount() > 0) {
                target.select(0);
                target.notifyListeners(SWT.Selection, new Event());
            }
        } else {
            target.removeAll();
        }
        
        isFinal = new Button(this, SWT.CHECK);
        isFinal.setText(Messages.getString("ENDS_GAME")); //$NON-NLS-1$
    }
    
    public void showRoomProperties() {
        isFinal.setSelection(room.getAttributeValue("final").equals("true")); //$NON-NLS-1$ //$NON-NLS-2$
        String targetName = room.getAttributeValue("target"); //$NON-NLS-1$
        if (targetName != null) {
        	target.select(target.indexOf(targetName));
        } else {
            if (target.getItemCount() > 0) {
                target.select(0);
                target.notifyListeners(SWT.Selection, new Event());
            }
        }
    }
  
    public void dispose() {
        room.setAttribute("final", String.valueOf(isFinal.getSelection())); //$NON-NLS-1$
        room.setAttribute("target", target.getText()); //$NON-NLS-1$
      
        super.dispose();
    }

    public void clear() {
        room = null;
    }
    
    
}
