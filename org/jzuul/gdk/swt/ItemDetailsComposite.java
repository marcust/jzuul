/*
 * 	CVS: $Id: ItemDetailsComposite.java,v 1.9 2004/07/25 21:40:56 marcus Exp $
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
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
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
 * @version $Revision: 1.9 $
 */
public class ItemDetailsComposite extends Composite {

    protected Text description;

    protected PropertyComposite properties;

    protected Element item;

    private Button bt2;

    private Button bt1;

    /**
     * @param arg0
     * @param arg1
     */
    public ItemDetailsComposite(Composite arg0, int arg1) {
        super(arg0, arg1);

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

        bt2 = new Button((Composite) buttonGroup, SWT.NONE);
        bt2.setText(Messages.getString("EDIT_EVENTS")); //$NON-NLS-1$
        bt2.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                java.util.List events = item.getChildren("event"); //$NON-NLS-1$
                EventEditorDialog ed = new EventEditorDialog(new Shell(arg0.display));
                ed.open(events);

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });

        bt1 = new Button((Composite) buttonGroup, SWT.NONE);
        bt1.setText(Messages.getString("EDIT_COMBINATIONS")); //$NON-NLS-1$
        bt1.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] items = getItemNames();
                if (items != null && items.length > 1) {
                CombinationEditorDialog ce = new CombinationEditorDialog(new Shell(arg0.display));
                ce.open(item, items);
                } else {
                    MessageBox mb = new MessageBox(new Shell(arg0.display), SWT.ICON_ERROR);
                    mb.setMessage(Messages.getString("COMB_ERROR_MSG")); //$NON-NLS-1$
                    mb.open();
                    return;
                }
                
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });

        group.layout();
    }

    public void showItemElement(Element item) {
        cleanUpChanges();
        this.item = item;
        if (item == null) {
            this.clear();
            this.disable();
            return;
        }
        this.enable();
        if (item.getChildText("description") != null && !item.getChildText("description").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            description.setText(item.getChildText("description")); //$NON-NLS-1$
        } else {
            description.setText(""); //$NON-NLS-1$
            description.setFocus();
        }
        properties.showProperties(item.getChildren("property")); //$NON-NLS-1$
        
    }

    public void cleanUpChanges() {
        if (item == null) return;
        item.removeChildren("property"); //$NON-NLS-1$
        item.addContent(properties.getElementList());
        Element descr = item.getChild("description"); //$NON-NLS-1$
        descr.setText(description.getText());

    }

    protected String[] getItemNames() {
        if (item == null) return null;
        java.util.List names = new ArrayList();

        Parent p = item.getParent();
        java.util.List contents = p.getContent();

        for (Iterator contentIter = contents.iterator(); contentIter.hasNext();) {
            Content contentElement = (Content) contentIter.next();
            if (contentElement instanceof Element) {
                String name = ((Element) contentElement).getAttributeValue("name"); //$NON-NLS-1$
                System.err.println("Found new Element: " + name); //$NON-NLS-1$
                names.add(name);
            }
        }
        String[] retval = new String[names.size()];
        names.toArray(retval);
        Arrays.sort(retval);
        return retval;
    }

    protected void clear() {
        description.setText(""); //$NON-NLS-1$
        item = null;
        properties.showProperties(null);
    }
    
    public void disable() {
        setEnabled(false);    
    }
    
    public void enable() {
        setEnabled(true);
    }
    
    public void setEnabled(boolean enabled) {
        bt1.setEnabled(enabled);
        bt2.setEnabled(enabled);
        description.setEnabled(enabled);
    }

}