/*
 * 	CVS: $Id: CharacterDetailsComposite.java,v 1.11 2004/07/25 21:40:56 marcus Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Parent;

/**
 * 
 * Created on Jul 12, 2004
 * 
 * 
 * @version $Revision: 1.11 $
 */
public class CharacterDetailsComposite extends Composite {

    Text description;

    Combo action;

    List actions; // Element lists;

    Element character;

    PropertyComposite properties;

    private Button bt1;

    private Button bt2;

    /**
     * @param arg0
     * @param arg1
     */
    public CharacterDetailsComposite(Composite arg0, int arg1) {
        super(arg0, arg1);

        actions = new ArrayList();
        character = new Element("person"); //$NON-NLS-1$

        // layout the Composite
        GridData layoutGridData = new GridData(GridData.FILL_BOTH);
        layoutGridData.horizontalSpan = 4;
        this.setLayoutData(layoutGridData);
        GridLayout layout = new GridLayout(1, false);
        this.setLayout(layout);

        // layout the main group
        final Group group = new Group(this, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        group.setLayoutData(gridData);
        GridLayout groupLayout = new GridLayout(2, false);
        group.setLayout(groupLayout);

        new Label((Composite) group, SWT.NONE).setText(Messages.getString("DESCRIPTION_COLON")); //$NON-NLS-1$
        description = new Text((Composite) group, SWT.BORDER);
        description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        properties = new PropertyComposite((Composite) this, SWT.NONE);

        // layout the button group

        final Group buttonGroup = new Group(this, SWT.NONE);
        GridData buttonGridData = new GridData(GridData.FILL_HORIZONTAL);
        buttonGridData.horizontalSpan = 1;
        buttonGroup.setLayoutData(buttonGridData);
        GridLayout buttonGroupLayout = new GridLayout(2, false);
        buttonGroup.setLayout(buttonGroupLayout);

        bt1 = new Button((Composite) buttonGroup, SWT.NONE);
        bt1.setText(Messages.getString("EDIT_EVENTS")); //$NON-NLS-1$
        bt1.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                EventEditorDialog ed = new EventEditorDialog(new Shell(arg0.display));
                ed.open(character.getChildren("event")); //$NON-NLS-1$
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        bt2 = new Button((Composite) buttonGroup, SWT.PUSH);
        bt2.setText(Messages.getString("EDIT_DIALOGS")); //$NON-NLS-1$
        bt2.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                DialogEditorDialog ed = new DialogEditorDialog(new Shell(arg0.display));
                ed.open(getDialogForCharacter());
                
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        group.layout();
    }

    public void showCharacterElement(Element character) {
        if (character != null) {
            this.enable();
            this.character = character;
            String desc = character.getChildText("description"); //$NON-NLS-1$
            if (desc != null && !desc.equals("")) { //$NON-NLS-1$
                description.setText(desc);
            } else {
                description.setText(""); //$NON-NLS-1$
                description.setFocus();
            }
            description.setSelection(description.getText().length());
            properties.showProperties(character.getChildren("property")); //$NON-NLS-1$

        } else {
            this.clean();
        }
    }

    public void updateElement() {
        if (character != null) {
            enable();
            Element e = character.getChild("description"); //$NON-NLS-1$
            if (e != null) {
                e.setText(description.getText());
            } else {
                character.addContent(new Element("description").setText(description.getText())); //$NON-NLS-1$
            }
            character.removeChildren("property"); //$NON-NLS-1$
            character.addContent(properties.getElementList());
        } else {
            disable();
        }
    }

    protected Element getDialogForCharacter() {
        String name = character.getAttributeValue("name"); //$NON-NLS-1$
        if (name == null) return null;
        Parent gamefile = character;
        
        List contents = JdomHelpers.getFirstLevelElements(character);
        Element dialogs = null;
        for (Iterator contentIter = contents.iterator(); contentIter.hasNext();) {
            Content jdomContent = (Content) contentIter.next();
            if (jdomContent instanceof Element) {
                Element jdomElement = (Element) jdomContent;
                if (jdomElement.getName().equals("dialogs")) { //$NON-NLS-1$
                    dialogs = jdomElement;
                    break;
                }
            }
        }
        if (dialogs != null) {
            System.err.println("Dialog != null"); //$NON-NLS-1$
            List dialogOwner = dialogs.getChildren("dialog-owner"); //$NON-NLS-1$
            for (Iterator dialogOwnerIter = dialogOwner.iterator(); dialogOwnerIter.hasNext();) {
                Element ownerElement = (Element) dialogOwnerIter.next();
                if (ownerElement.getAttributeValue("name").equals(name)) { return ownerElement; } //$NON-NLS-1$
            }
        }
        Element newEl = new Element("dialog-owner"); //$NON-NLS-1$
        newEl.setAttribute("name", name); //$NON-NLS-1$
       
        dialogs.addContent(newEl);
       
        return newEl;
    }

    protected void clean() {
        this.description.setText(""); //$NON-NLS-1$
        if (this.action != null) this.action.select(0);
        this.actions = null;
        this.character = null;
        this.properties.showProperties(null);
        disable();
    }
    
    public void enable() {
        setEnabled(true);
    }
    
    public void disable() {
        setEnabled(false);
    }
    
    public void setEnabled(boolean enabled) {
        description.setEnabled(enabled);
        if (action != null) {
            action.setEnabled(enabled);
        }
        properties.setEnabled(enabled);
        bt1.setEnabled(enabled);
        bt2.setEnabled(enabled);
        properties.setEnabled(enabled);
    }

}