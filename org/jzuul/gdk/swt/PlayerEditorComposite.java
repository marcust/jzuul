/*
 * 	CVS: $Id: PlayerEditorComposite.java,v 1.7 2004/07/25 21:40:56 marcus Exp $
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;


/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.7 $
 */
public class PlayerEditorComposite extends Composite {
    Element player;
    
    /**
     * @param arg0
     * @param arg1
     */
    public PlayerEditorComposite(Composite arg0, int arg1) {
        super(arg0, arg1);
        this.setLayout(new FillLayout());
        
        Group mainGroup = new Group(this, SWT.NONE);
        mainGroup.setText(Messages.getString("PLAYER_OPTIONS")); //$NON-NLS-1$
        mainGroup.setLayout(new GridLayout(1,true));
        
        Button contentButton = new Button(mainGroup,SWT.PUSH);
        contentButton.setText(Messages.getString("EDIT_CONTENTS")); //$NON-NLS-1$
        
        contentButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                ContentsEditorDialog ed = new ContentsEditorDialog(new Shell(arg0.display), SWT.NONE);
                ed.open(player);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        Button targetButton = new Button(mainGroup, SWT.PUSH);
        targetButton.setText(Messages.getString("EDIT_TARGETS")); //$NON-NLS-1$
        
        targetButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                TargetEditor ed = new TargetEditor(new Shell(arg0.display), SWT.NONE);
                ed.open(player);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        
    }
    
    public void showPlayer(Element player) {
        this.player = player;
    }
    
    public void clear() {
        this.player = null;
    }
    
	public void updateData() {
	    // I believe nothing to be done here
	}


}
