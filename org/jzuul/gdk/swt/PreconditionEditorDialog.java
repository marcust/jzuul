/*
 * 	CVS: $Id: PreconditionEditorDialog.java,v 1.12 2004/07/25 21:40:56 marcus Exp $
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
public class PreconditionEditorDialog extends Dialog {
    Element element;
    List itemsList, preconditionList;
    private Button add;
    private Button delete;
    
    
    /**
     * @param arg0
     */
    public PreconditionEditorDialog(Shell arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public PreconditionEditorDialog(Shell arg0, int arg1) {
        super(arg0, arg1);
    }
    
    public void open(Element withPreconditions) {
        this.element = withPreconditions;
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.BORDER | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.setText(Messages.getString("EDIT_PRECONDITIONS")); //$NON-NLS-1$
        shell.setLayout(new GridLayout(3, true));
        shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$
        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                cleanUpChanges();
            }
        });

        //left hand items list
        itemsList = new List(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData ilData = new GridData(GridData.FILL_BOTH);
        ilData.horizontalSpan = 1;
        itemsList.setLayoutData(ilData);
        String[] itemNames =JdomHelpers.getItemNames(element);
        if (itemNames != null && (itemNames.length > 0)) {
            itemsList.setItems(itemNames);
        } else {
            MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR);
            mb.setMessage(Messages.getString("PRECON_ERROR")); //$NON-NLS-1$
            mb.open();
            shell.dispose();
            return;
        }
        itemsList.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {}

            public void widgetDefaultSelected(SelectionEvent e) {
                add.notifyListeners(SWT.Selection, new Event());
            }
        });
        
        Group buttonGroup = new Group(shell,SWT.NONE);
        GridData bgData = new GridData(GridData.FILL_BOTH);
        bgData.horizontalSpan = 1;
        buttonGroup.setLayoutData(bgData);
        buttonGroup.setLayout(new GridLayout(1,true));
        
        add = new Button(buttonGroup,SWT.PUSH);
        GridData addDat = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        add.setLayoutData(addDat);
        add.setText(">>"); //$NON-NLS-1$
        add.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] selected = itemsList.getSelection();
                
                for (int i = 0; i < selected.length; i++) {
                    preconditionList.add(selected[i]);
               
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        delete = new Button(buttonGroup,SWT.PUSH);
        delete.setText("<<"); //$NON-NLS-1$
        GridData delDat = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        delete.setLayoutData(delDat);
        delete.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                String[] selected = preconditionList.getSelection();
                
                for (int i = 0; i < selected.length; i++) {
                    
                        preconditionList.remove(selected[i]);
                    
                }

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        Button ok = new Button(buttonGroup, SWT.PUSH);
        ok.setText(Messages.getString("OK")); //$NON-NLS-1$
        GridData okDat = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        ok.setLayoutData(okDat);
        ok.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                cleanUpChanges();
                shell.dispose();

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        
        preconditionList = new List(shell,SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData plData = new GridData(GridData.FILL_BOTH);
        plData.horizontalSpan = 1;
        preconditionList.setLayoutData(plData);
        String[] precons = getPreconditionItemsNames();
        preconditionList.setItems(precons);
        preconditionList.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {}

            public void widgetDefaultSelected(SelectionEvent e) {
                delete.notifyListeners(SWT.Selection, new Event());
            }
        });
        
        shell.setSize(400,300);
        shell.open();

        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        
    }

    protected String[] getPreconditionItemsNames() {
        java.util.List precons = element.getChildren("precondition"); //$NON-NLS-1$
        java.util.List preconItemNames = new ArrayList();
        for (Iterator preconIter = precons.iterator(); preconIter.hasNext();) {
            Element preconElement = (Element) preconIter.next();
            preconItemNames.add(preconElement.getAttributeValue("item")); //$NON-NLS-1$
        }
        String[] retval = new String[preconItemNames.size()];
        preconItemNames.toArray(retval);
        return retval;
        
    }

    protected void cleanUpChanges() {
    element.removeChildren("precondition"); //$NON-NLS-1$
    String[] preconItems = preconditionList.getItems();
    for (int i = 0; i < preconItems.length; i++) {
        element.addContent(new Element("precondition").setAttribute("item",preconItems[i])); //$NON-NLS-1$ //$NON-NLS-2$
    }
    }
    
}
