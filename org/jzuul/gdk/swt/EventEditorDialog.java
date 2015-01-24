/*
 * 	CVS: $Id: EventEditorDialog.java,v 1.10 2004/07/25 21:40:56 marcus Exp $
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

import java.util.Arrays;
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
 * Created on Jul 12, 2004
 * 
 * 
 * @version $Revision: 1.10 $
 */
public class EventEditorDialog extends Dialog {

    protected String[] events = { "default", "takeup", "drop", "playerenter", "playerleave", "use", "dialog_continue", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
            "dialog_custom_result_1", "dialog_custom_result_2", "dialog_custom_result_3", "dialog_error", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "dialog_npc_give", "dialog_npc_take", "dialog_end_failure", "dialog_end_success", "use_success", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "use_failure", "timer" }; //$NON-NLS-1$ //$NON-NLS-2$

    protected List eventList;

    protected ActionEditorComposite actionComposite;

    protected String currentEvent;

    protected java.util.List eventElementsList;

    /**
     * @param arg0
     */
    public EventEditorDialog(Shell arg0) {
        super(arg0);
        Arrays.sort(events);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public EventEditorDialog(Shell arg0, int arg1) {
        super(arg0, arg1);
        Arrays.sort(events);
    }

    public void setEvents(String[] events) {
        this.events = events;
        
    }

    public Object open(java.util.List eventElements) {
        eventElementsList = eventElements;
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.BORDER | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.setText(Messages.getString("EDIT_EVENT_ACTIONS")); //$NON-NLS-1$
        shell.setLayout(new GridLayout(10, true));
        shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$
        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                cleanUpChanges();
            }
        });

        { //The list on the left side
            eventList = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.CHECK);
            final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL
                    | GridData.GRAB_VERTICAL);
            gridData.horizontalSpan = 3;
            eventList.setLayoutData(gridData);
            eventList.setItems(events);
            eventList.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    cleanUpChanges();
                    currentEvent = eventList.getItem(eventList.getSelectionIndex());
                    //actionGroup.setText("Actions for event " + currentEvent);
                    showActionsByEvent(currentEvent);
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

        }
        { // The group with the action
            actionComposite = new ActionEditorComposite(shell, SWT.NONE,findEvent(null));
        }
        { // The buttons on the bottom
            final Group bottomGroup = new Group(shell, SWT.NONE);
            GridData groupLD = new GridData(GridData.FILL_HORIZONTAL);
            groupLD.heightHint = 40;
            groupLD.horizontalSpan = 10;
            bottomGroup.setLayoutData(groupLD);

            bottomGroup.setLayout(new GridLayout(10, true));

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

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });
            ok.setLayoutData(buttonData);

        }
        eventList.select(0);
        eventList.notifyListeners(SWT.Selection, new Event());
        
        shell.setSize(620, 480);
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        return null;
    }

    public Element findEvent(String name) {
        if (name == null) { name = events[0]; }
        for (Iterator elementIter = eventElementsList.iterator(); elementIter.hasNext();) {
            Element eventElement = (Element) elementIter.next();
            if (eventElement.getAttributeValue("name").equals(name)) { return eventElement; } //$NON-NLS-1$
        }
        // we didn't find the element, create a new one:
        Element newEl = new Element("event"); //$NON-NLS-1$
        newEl.setAttribute("name", name); //$NON-NLS-1$
        eventElementsList.add(newEl);
        return newEl;
    }

    public void showActionsByEvent(String event) {
        Element eventEl = findEvent(event);
        Element actions = eventEl.getChild("actions"); //$NON-NLS-1$
        if (actions == null) return;
        java.util.List singleActions = actions.getChildren();
        actionComposite.showActionElements(singleActions);
    }

    protected void cleanUpChanges() {
        replaceActions(currentEvent, actionComposite.getElementList());
    }

    protected void replaceActions(String eventName, java.util.List actionElements) {
        Element event = findEvent(eventName);
        if (actionElements.isEmpty()) {
            event.detach();
            return;
        }
        
        event.removeChildren("actions"); //$NON-NLS-1$
        Element actions = new Element("actions"); //$NON-NLS-1$
        actions.addContent(actionElements);
        event.addContent(actions);

        System.err.println("Added " + actionElements.size() + " Elements to event " + eventName); //$NON-NLS-1$ //$NON-NLS-2$
    }

}