/*
 * 	CVS: $Id: CombinationEditorDialog.java,v 1.9 2004/07/25 21:40:56 marcus Exp $
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
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.jzuul.engine.gui.utils.Util;




/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.9 $
 */
public class CombinationEditorDialog extends Dialog {
    Element itemElement;
    List itemList;    
    ActionEditorComposite actionGroup;
    String currentWithObject;
    
    /**
     * @param arg0
     */
    public CombinationEditorDialog(Shell arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public CombinationEditorDialog(Shell arg0, int arg1) {
        super(arg0, arg1);
    }
    
    public void open(Element currentItem, String[] itemNames) {
        itemElement = currentItem;
        
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.BORDER | SWT.MIN  | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.setText(Messages.getString("EDIT_COMBINATIONS")); //$NON-NLS-1$
        shell.setLayout(new GridLayout(10,true));
        shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png"));              	 //$NON-NLS-1$
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
			    cleanUpChanges();
			}
		});
        
            { //The list on the left side
                itemList = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.CHECK);
                final GridData gridData =
    				new GridData(GridData.HORIZONTAL_ALIGN_FILL |
    				        	 GridData.VERTICAL_ALIGN_FILL |
    				        	 GridData.GRAB_VERTICAL );
    			gridData.horizontalSpan = 3;
    			itemList.setLayoutData(gridData);
    			for (int i = 0; i < itemNames.length; i++) {
    			    if (!itemNames[i].equals(itemElement.getAttributeValue("name"))) { //$NON-NLS-1$
    			        itemList.add(itemNames[i]);
    			    }
                }
                
                itemList.addSelectionListener(new SelectionListener() {

                    public void widgetSelected(SelectionEvent arg0) {
                        cleanUpChanges();
                        currentWithObject = itemList.getItem(itemList.getSelectionIndex());
                      //  actionGroup.setText("Combinations for " + itemElement.getAttributeValue("name") + " with "  + currentWithObject);
                        showCombinationsWithObject(currentWithObject);
                    }

                    public void widgetDefaultSelected(SelectionEvent arg0) {
                    }
                });
                
            }
            { // The group with the action
                actionGroup = new ActionEditorComposite(shell, SWT.NONE , itemElement);
                }
            { // The buttons on the bottom
                final Group bottomGroup = new Group(shell, SWT.NONE);
                GridData groupLD = new GridData(GridData.FILL_HORIZONTAL);
                groupLD.heightHint = 40;
                groupLD.horizontalSpan = 10;
                bottomGroup.setLayoutData(groupLD);
                
                bottomGroup.setLayout(new GridLayout(10,true));
                
                GridData buttonData = new GridData(GridData.VERTICAL_ALIGN_END);
                buttonData.heightHint = 30;
                buttonData.widthHint = 80;
                
                Button ok = new Button(bottomGroup, SWT.PUSH);
                ok.setText(Messages.getString("OK")); //$NON-NLS-1$
                ok.addSelectionListener(new SelectionListener() {

                    public void widgetSelected(SelectionEvent arg0) {
                        cleanUpChanges();
                        shell.dispose();
                    }

                    public void widgetDefaultSelected(SelectionEvent arg0) {}
                });
                ok.setLayoutData(buttonData);
                
             }
        
        shell.setSize(620,480);
        if (itemList.getItemCount() > 0) {
        	itemList.select(0);
        	itemList.notifyListeners(SWT.Selection, new Event());
        }
        
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
                 if (!display.readAndDispatch()) display.sleep();
        }
        
    }
    
    protected void cleanUpChanges() {
        replaceActionList(currentWithObject,actionGroup.getElementList());
    }
    
    protected void showCombinationsWithObject(String withObjectName) {
        actionGroup.showActionElements(getActionsForWithItem(withObjectName));
    }
    
    protected Element getWithObject(String withObjectName) {
        if (withObjectName == null) return null;
        Element combinations = itemElement.getChild("combinations"); //$NON-NLS-1$
        
        if (combinations != null) {
            java.util.List withObjects = combinations.getChildren("with-object"); //$NON-NLS-1$
            for (Iterator withObjIter = withObjects.iterator(); withObjIter.hasNext();) {
                Element element = (Element) withObjIter.next();
                if (element.getAttributeValue("name").equals(withObjectName)) { //$NON-NLS-1$
                    return element;
                }
            }
            Element newEl = new Element("with-object"); //$NON-NLS-1$
            newEl.setAttribute("name", withObjectName); //$NON-NLS-1$
            combinations.addContent(newEl);
            return newEl;
        } else {
            itemElement.addContent(new Element("combinations")); //$NON-NLS-1$
            return getWithObject(withObjectName);
        }
        
    }

    protected java.util.List getActionsForWithItem(String withObjectName) {
        Element withObject = getWithObject(withObjectName);
        Element actions = withObject.getChild("actions"); //$NON-NLS-1$
        if (actions == null) return null;
        return actions.getChildren(); 
    }
    
    protected void replaceActionList(String currentWithObject, java.util.List actionList) {
        Element withObj = getWithObject(currentWithObject);
        if (withObj == null) return;
        withObj.removeChild("actions"); //$NON-NLS-1$
        Element newActions = new Element("actions"); //$NON-NLS-1$
        newActions.addContent(actionList);
        withObj.addContent(newActions);
    }
    
}
