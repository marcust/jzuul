/*
 * 	CVS: $Id: ContentsEditorDialog.java,v 1.12 2004/07/25 21:40:56 marcus Exp $
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.jzuul.engine.gui.utils.Util;

/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.12 $
 */
public class ContentsEditorDialog extends Dialog {

    Element content;
    private List characters;
    private List contentChars;
    private List items;
    private List contentItems;
    private Button charAdd;
    private Button charDelete;
    private Button itemsAdd;
    private Button itemsDelete;

    /**
     * @param arg0
     */
    public ContentsEditorDialog(Shell arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ContentsEditorDialog(Shell arg0, int arg1) {
        super(arg0, arg1);
    }

    public void open(Element elementWithContents) {
        content = elementWithContents;
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.BORDER | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.setText(Messages.getString("EDIT_CONTENTS")); //$NON-NLS-1$
        shell.setLayout(new GridLayout(1, true));
        shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$

        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                cleanUpChanges();
            }
        });

        // characters
        Group charEditGroup = new Group(shell, SWT.NONE);
        charEditGroup.setText(Messages.getString("CHARACTERS")); //$NON-NLS-1$
        GridData cegData = new GridData(GridData.FILL_BOTH);
        charEditGroup.setLayoutData(cegData);
        charEditGroup.setLayout(new GridLayout(3, true));

        characters = new List(charEditGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData clData = new GridData(GridData.FILL_BOTH);
        characters.setLayoutData(clData);
        {
        String[] items = JdomHelpers.getCharacterNames(content);
        if (items != null) {
            characters.setItems(items);
            characters.select(0);
            characters.notifyListeners(SWT.Selection, new Event());
        	} 
        }
        characters.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {}

            public void widgetDefaultSelected(SelectionEvent e) {
               	charAdd.notifyListeners(SWT.Selection, new Event());
            }
        });
        Group charButtonGroup = new Group(charEditGroup, SWT.NONE);
        GridData cbgData = new GridData(GridData.FILL_BOTH);
        charButtonGroup.setLayoutData(cbgData);
        charButtonGroup.setLayout(new GridLayout(1,true));

        charAdd = new Button(charButtonGroup,SWT.PUSH);
        GridData charAddDat = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        charAdd.setLayoutData(charAddDat);
        charAdd.setText(">>"); //$NON-NLS-1$
        charAdd.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] selected = characters.getSelection();
               
                for (int i = 0; i < selected.length; i++) {
                        contentChars.add(selected[i]);
                    }
                }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        charDelete = new Button(charButtonGroup,SWT.PUSH);
        charDelete.setText("<<"); //$NON-NLS-1$
        GridData delDat = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        charDelete.setLayoutData(delDat);
        charDelete.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] selected = contentChars.getSelection();
               
                for (int i = 0; i < selected.length; i++) {
                        contentChars.remove(selected[i]);
                    
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        
        contentChars = new List(charEditGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData ccData = new GridData(GridData.FILL_BOTH);
        contentChars.setLayoutData(ccData);
        contentChars.setItems(containedChars());
        contentChars.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {}

            public void widgetDefaultSelected(SelectionEvent e) {
                charDelete.notifyListeners(SWT.Selection, new Event());
            }
        });
        
        // items
        Group itemEditGroup = new Group(shell, SWT.NONE);
        itemEditGroup.setText(Messages.getString("ITEMS")); //$NON-NLS-1$
        GridData iegData = new GridData(GridData.FILL_BOTH);
        itemEditGroup.setLayoutData(iegData);
        itemEditGroup.setLayout(new GridLayout(3, true));

        items = new List(itemEditGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData ilData = new GridData(GridData.FILL_BOTH);
        items.setLayoutData(ilData);
        String[] itemNames = JdomHelpers.getItemNames(content);
        if (itemNames != null) {
            items.setItems(itemNames);
            items.select(0);
            items.notifyListeners(SWT.Selection, new Event());
        }
        items.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {}

            public void widgetDefaultSelected(SelectionEvent e) {
                itemsAdd.notifyListeners(SWT.Selection, new Event());
            }
        });
        
        Group itemButtonGroup = new Group(itemEditGroup, SWT.NONE);
        GridData ibgData = new GridData(GridData.FILL_BOTH);
        itemButtonGroup.setLayoutData(ibgData);
        itemButtonGroup.setLayout(new GridLayout(1,true));
        
        itemsAdd = new Button(itemButtonGroup,SWT.PUSH);
        GridData itemsAddDat = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        itemsAdd.setLayoutData(itemsAddDat);
        itemsAdd.setText(">>"); //$NON-NLS-1$
        itemsAdd.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] selected = items.getSelection();
               
                for (int i = 0; i < selected.length; i++) {
                        contentItems.add(selected[i]);
                    }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        itemsDelete = new Button(itemButtonGroup,SWT.PUSH);
        itemsDelete.setText("<<"); //$NON-NLS-1$
        GridData deleDat = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        itemsDelete.setLayoutData(deleDat);
        itemsDelete.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] selected = contentItems.getSelection();
               
                for (int i = 0; i < selected.length; i++) {
                       contentItems.remove(selected[i]);
                    
                }

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        contentItems = new List(itemEditGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData ciData = new GridData(GridData.FILL_BOTH);
        contentItems.setLayoutData(ciData);
        contentItems.setItems(containedItems());
        contentItems.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {}

            public void widgetDefaultSelected(SelectionEvent e) {
                itemsDelete.notifyListeners(SWT.Selection, new Event());
            }
        });
        
        Group bottomGroup = new Group(shell, SWT.NONE);
        GridData bgData = new GridData(GridData.FILL_HORIZONTAL);
        bottomGroup.setLayoutData(bgData);
        bottomGroup.setLayout(new GridLayout(5,true));
        
        Button ok = new Button(bottomGroup, SWT.PUSH);
        GridData okDat = new GridData(GridData.FILL_HORIZONTAL);
        ok.setLayoutData(okDat);
        ok.setText(Messages.getString("OK")); //$NON-NLS-1$
        ok.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                cleanUpChanges();
                shell.dispose();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });

        
        
        shell.setSize(620, 480);
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }

    }

    public void cleanUpChanges() {
        content.removeChild("contents"); //$NON-NLS-1$
        Element contents = new Element("contents"); //$NON-NLS-1$
        content.addContent(contents);
        String[] items = contentItems.getItems();
        for (int i = 0; i < items.length; i++) {
            contents.addContent(new Element("item").setAttribute("name", items[i])); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String[] chars = contentChars.getItems();
        for (int i = 0; i < chars.length; i++) {
            contents.addContent(new Element("character").setAttribute("name",chars[i])); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    public String[] containedItems() {
        Element contents = content.getChild("contents"); //$NON-NLS-1$
        java.util.List itemNames = new ArrayList();
        if (contents != null ) {
            java.util.List items = contents.getChildren("item");  //$NON-NLS-1$
            for (Iterator itemIter = items.iterator(); itemIter.hasNext();) {
                Element itemElement = (Element) itemIter.next();
                itemNames.add(itemElement.getAttributeValue("name")); //$NON-NLS-1$
            }
        }
        String[] retval = new String[itemNames.size()];
        itemNames.toArray(retval);
        return retval;        
    }

    public String[] containedChars() {
        Element contents = content.getChild("contents"); //$NON-NLS-1$
        java.util.List charNames = new ArrayList();
        if (contents != null ) {
            java.util.List items = contents.getChildren("character");  //$NON-NLS-1$
            for (Iterator itemIter = items.iterator(); itemIter.hasNext();) {
                Element itemElement = (Element) itemIter.next();
                charNames.add(itemElement.getAttributeValue("name")); //$NON-NLS-1$
            }
        }
        String[] retval = new String[charNames.size()];
        charNames.toArray(retval);
        return retval;
    }
    
}