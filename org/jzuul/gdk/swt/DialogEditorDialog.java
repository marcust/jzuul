/*
 * 	CVS: $Id: DialogEditorDialog.java,v 1.14 2004/07/26 10:09:11 marcus Exp $
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Parent;
import org.jzuul.engine.gui.utils.Util;

//TODO References not yet implemented

/**
 * 
 * Created on Jul 12, 2004
 * 
 * 
 * @version $Revision: 1.14 $
 */
public class DialogEditorDialog extends Dialog {

    protected class DialogObjectElement {

        private String dialogno, phase;

        private Text player, npc;

        private Combo type;

        private Label id;

        private Group group;

        private Combo nextPhase;

        public DialogObjectElement(String dialogno, String phase, Label id, Combo type, Combo nextPhase, Text player,
                Text npc, Group group) {
            this.dialogno = dialogno;
            this.phase = phase;
            this.id = id;
            this.type = type;
            this.player = player;
            this.npc = npc;
            this.group = group;
            this.nextPhase = nextPhase;
        }

        public void delete() {
            Composite c = group.getParent();
            group.dispose();
            c.layout();
            c.redraw();
        }

        public Element toXMLElement() {
            Element newEl = new Element("object"); //$NON-NLS-1$
            newEl.setAttribute("id", getId().toString()); //$NON-NLS-1$
            newEl.setAttribute("type", type.getText()); //$NON-NLS-1$
            String nextphase = nextPhase.getText();
            if (!(nextphase == null || nextphase.equals(""))) { //$NON-NLS-1$
                newEl.setAttribute("nextphase",nextphase ); //$NON-NLS-1$
            }
            newEl.addContent(new Element("say").setText(player.getText())); //$NON-NLS-1$
            newEl.addContent(new Element("reply").setText(npc.getText())); //$NON-NLS-1$
            return newEl;
        }

        public void fill(Element dialogObjectElement) {
            freeIds.push(this.getId());
            id.setText("ID: " + dialogObjectElement.getAttributeValue("id")); //$NON-NLS-1$ //$NON-NLS-2$
            type.select(type.indexOf(dialogObjectElement.getAttributeValue("type"))); //$NON-NLS-1$
            if (type.getText().equals("dialog_continue")) { //$NON-NLS-1$
                nextPhase.select(nextPhase.indexOf(dialogObjectElement.getAttributeValue("nextphase"))); //$NON-NLS-1$
                nextPhase.setVisible(true);
            }
            player.setText(dialogObjectElement.getChildText("say")); //$NON-NLS-1$
            npc.setText(dialogObjectElement.getChildText("reply")); //$NON-NLS-1$
        }

        public Integer getId() {
            String idString = id.getText();
            idString = idString.replaceAll("ID: ", ""); //$NON-NLS-1$ //$NON-NLS-2$
            return Integer.valueOf(idString);
        }

    }

    protected final String[] dialogTypes = { "dialog_continue", "dialog_custom_result_1", "dialog_custom_result_2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "dialog_custom_result_3", "dialog_error", "dialog_npc_give", "dialog_npc_take", "dialog_end_failure", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "dialog_end_success" }; //$NON-NLS-1$

    protected List dialogList;

    protected List phaseList;

    protected Combo itemCombo;

    protected Group dialogGroup;

    protected java.util.List dialogs;

    protected Stack dialogObjects;

    protected Element dialogOwnerElement;

    protected int globalMaxId;

    protected Stack freeIds;

    private Button lessButton;

    private Button moreButton;

    /**
     * @param arg0
     */
    public DialogEditorDialog(Shell arg0) {
        super(arg0);
        dialogs = new ArrayList();
        freeIds = new Stack();
        dialogObjects = new Stack();
        Arrays.sort(dialogTypes);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public DialogEditorDialog(Shell arg0, int arg1) {
        super(arg0, arg1);
        dialogs = new ArrayList();
        dialogObjects = new Stack();
        freeIds = new Stack();
        Arrays.sort(dialogTypes);
    }

    public Object open(Element dialogOwnerElement) {

        this.dialogOwnerElement = dialogOwnerElement;
        if (dialogOwnerElement == null) {
            dialogOwnerElement = new Element("dialogs"); //$NON-NLS-1$
        }
        findMaxId();
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.BORDER | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.MAX);
        Object[] formatArgs = { dialogOwnerElement.getAttributeValue("name")};
        shell.setText(MessageFormat.format(Messages.getString("EDIT_DIALOGS_FOR"), formatArgs) ); //$NON-NLS-1$ //$NON-NLS-2$
        shell.setLayout(new GridLayout(10, true));
        shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$
        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                cleanUpChanges();
            }
        });

        { //The dialog List
            Group dialogListGroup = new Group(shell, SWT.NONE);
            final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL
                    | GridData.GRAB_VERTICAL);
            gridData.horizontalSpan = 3;
            dialogListGroup.setLayoutData(gridData);
            dialogListGroup.setText(Messages.getString("DIALOG_NR")); //$NON-NLS-1$
            dialogListGroup.setLayout(new GridLayout(1, false));

            dialogList = new List(dialogListGroup, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.CHECK);
            GridData dialogListData = new GridData(GridData.FILL_BOTH);
            dialogList.setLayoutData(dialogListData);

            dialogList.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    cleanUpChanges();
                    showPhases(dialogList.getSelectionIndex() + 1);
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            Menu dialogPopup = new Menu(dialogList);
            dialogList.setMenu(dialogPopup);
            MenuItem add = new MenuItem(dialogPopup, SWT.NONE);
            add.setText(Messages.getString("ADD")); //$NON-NLS-1$
            add.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    Element newEl = new Element("dialog"); //$NON-NLS-1$
                    DialogEditorDialog.this.dialogOwnerElement.addContent(newEl);
                    dialogs.add(newEl);
                    showDialogs();
                    dialogList.select(dialogList.getItemCount() - 1);
                    dialogList.notifyListeners(SWT.Selection, new Event());
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            MenuItem delete = new MenuItem(dialogPopup, SWT.NONE);
            delete.setText(Messages.getString("DELETE")); //$NON-NLS-1$
            delete.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    if (dialogList.getSelectionIndex() > -1) {
                    deleteDialog(dialogList.getSelectionIndex() + 1);
                    }
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            // The precondition:
            Group preconditionGroup = new Group(dialogListGroup, SWT.NONE);
            preconditionGroup.setLayout(new GridLayout(1, false));
            preconditionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            Button edit = new Button(preconditionGroup, SWT.PUSH);
            GridData editData = new GridData(GridData.FILL_HORIZONTAL);
            edit.setLayoutData(editData);

            edit.setText(Messages.getString("EDIT_PRECONDITIONS")); //$NON-NLS-1$
            edit.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    PreconditionEditorDialog ed = new PreconditionEditorDialog(new Shell(arg0.display), SWT.NONE);
                    ed.open(getDialogByNr(dialogList.getSelectionIndex() + 1));
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {}
            });

        }
        { //The phase List
            Group phaseListGroup = new Group(shell, SWT.NONE);
            final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL
                    | GridData.GRAB_VERTICAL);
            gridData.horizontalSpan = 2;
            phaseListGroup.setLayoutData(gridData);
            phaseListGroup.setText(Messages.getString("DIALOG_PHASE")); //$NON-NLS-1$
            phaseListGroup.setLayout(new FillLayout());

            phaseList = new List(phaseListGroup, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.CHECK);

            phaseList.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    cleanUpChanges();
                    int index = phaseList.getSelectionIndex();
                    if ( (index > 0) && (index < phaseList.getItemCount())) {
                        System.err.println("Index is " + index); //$NON-NLS-1$
                        String phaseNo = phaseList.getItem(index);
                        dialogGroup.setText(Messages.getString("PHASE") + phaseNo); //$NON-NLS-1$
                        showDialogObject(dialogList.getSelectionIndex() + 1, phaseList.getSelectionIndex() + 1);
                    }
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            Menu phasePopup = new Menu(phaseList);
            phaseList.setMenu(phasePopup);
            MenuItem add = new MenuItem(phasePopup, SWT.NONE);
            add.setText(Messages.getString("ADD")); //$NON-NLS-1$
            add.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    int dialogNr = dialogList.getSelectionIndex() + 1;
                    Element currentDialog = getDialogByNr(dialogNr);
                    currentDialog.addContent(new Element("phase")); //$NON-NLS-1$
                    showPhases(dialogNr);
                    phaseList.select(phaseList.getItemCount() - 1);
                    phaseList.notifyListeners(SWT.Selection, new Event());
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            MenuItem delete = new MenuItem(phasePopup, SWT.NONE);
            delete.setText(Messages.getString("DELETE")); //$NON-NLS-1$
            delete.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    deletePhase(phaseList.getSelectionIndex() + 1);
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

        }
        { // The group with the action
            dialogGroup = new Group(shell, SWT.V_SCROLL);
            GridData layoutData = new GridData(GridData.FILL_BOTH);
            layoutData.horizontalSpan = 5;
            dialogGroup.setLayoutData(layoutData);

            dialogGroup.setText(Messages.getString("DIALOGS")); //$NON-NLS-1$
            dialogGroup.setLayout(new GridLayout(2, false));

            moreButton = new Button(dialogGroup, SWT.PUSH);
            moreButton.setText(Messages.getString("MORE")); //$NON-NLS-1$

            GridData buttondat = new GridData();
            buttondat.heightHint = 30;
            buttondat.widthHint = 80;
            moreButton.setLayoutData(buttondat);

            moreButton.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    generateDialogGroup(dialogGroup);
                    dialogGroup.layout();
                    dialogGroup.redraw();
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

            lessButton = new Button(dialogGroup, SWT.PUSH);
            lessButton.setText(Messages.getString("LESS")); //$NON-NLS-1$
            GridData buttondat2 = new GridData();
            buttondat2.heightHint = 30;
            buttondat2.widthHint = 80;
            lessButton.setLayoutData(buttondat2);
            lessButton.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    if (dialogObjects.isEmpty()) return;
                    DialogObjectElement toDelete = ((DialogObjectElement) dialogObjects.pop());
                    freeIds.push(toDelete.getId());
                    toDelete.delete();
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });

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
        showDialogs();
        String dialogNo = getDialogNo();
        if (dialogNo != null) {
            showPhases(dialogNo);
        } else {
            disableButtons();
        }

        shell.setSize(900, 600);
        shell.open();

        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        return null;
    }

    protected void generateDialogGroup(Group group) {
        /*
         * an action group is a combo box combined with a label to specify an
         * action
         */
        final Group dialogObjectGroup = new Group(group, SWT.NONE);
        dialogObjectGroup.setLayout(new GridLayout(4, false));

        GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
        groupData.horizontalSpan = 2;
        dialogObjectGroup.setLayoutData(groupData);

        // first row
        Label idLabel = new Label(dialogObjectGroup, SWT.NONE);
        idLabel.setText("ID: " + getNextId()); //$NON-NLS-1$

        final Combo combo = new Combo(dialogObjectGroup, SWT.NONE);
        GridData comboDat = new GridData(GridData.FILL_HORIZONTAL);
        comboDat.horizontalSpan = 1;
        combo.setLayoutData(comboDat);
        combo.setItems(dialogTypes);
        combo.select(combo.indexOf("dialog_end_failure")); //$NON-NLS-1$

        final Combo nextPhase = new Combo(dialogObjectGroup, SWT.NONE);
        GridData nextPhaseDat = new GridData(GridData.FILL_HORIZONTAL);
        nextPhaseDat.horizontalSpan = 1;
        nextPhase.setLayoutData(nextPhaseDat);
        nextPhase.setVisible(false);
        nextPhase.add("0"); //$NON-NLS-1$
        nextPhase.setItems(phaseList.getItems());
        nextPhase.select(nextPhase.indexOf("0")); //$NON-NLS-1$
        nextPhase.notifyListeners(SWT.Selection, new Event());
        
        combo.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                if (combo.getText().equals("dialog_continue")) { //$NON-NLS-1$
                    nextPhase.setVisible(true);
                } else {
                    nextPhase.setVisible(false);
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Button delete = new Button(dialogObjectGroup, SWT.PUSH);
        GridData deleteData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
        deleteData.horizontalSpan = 1;
        delete.setLayoutData(deleteData);
        delete.setText("Delete"); //$NON-NLS-1$
        delete.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                for (Iterator groupIter = dialogObjects.iterator(); groupIter.hasNext();) {
                    DialogObjectElement element = (DialogObjectElement) groupIter.next();
                    if (element.group.equals(dialogObjectGroup)) {
                        freeIds.push(element.getId());
                        element.delete();
                        groupIter.remove();
                    }
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        //second row
        Label playerLabel = new Label(dialogObjectGroup, SWT.NONE);
        playerLabel.setText(Messages.getString("PLAYER_SAY")); //$NON-NLS-1$

        Text playerText = new Text(dialogObjectGroup, SWT.BORDER);
        GridData textDat = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        textDat.horizontalSpan = 3;
        playerText.setLayoutData(textDat);

        Label npcLabel = new Label(dialogObjectGroup, SWT.NONE);
        npcLabel.setText(Messages.getString("NPC")); //$NON-NLS-1$

        Text npcText = new Text(dialogObjectGroup, SWT.BORDER);
        GridData npcDat = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        npcDat.horizontalSpan = 3;
        npcText.setLayoutData(npcDat);

        dialogObjects.push(new DialogObjectElement(getDialogNo(), getPhaseNo(), idLabel, combo, nextPhase, playerText,
                npcText, dialogObjectGroup));
    }

    protected void cleanUpChanges() {
        String dialogNo, phaseNo;
        dialogNo = phaseNo = null;
        java.util.List objects = new ArrayList();
        for (Iterator iter = dialogObjects.iterator(); iter.hasNext();) {
            DialogObjectElement element = (DialogObjectElement) iter.next();
            dialogNo = element.dialogno;
            phaseNo = element.phase;
            objects.add(element.toXMLElement());
            element.delete();
            iter.remove();
        }
        setObjects(dialogNo, phaseNo, objects);
    }

    protected void setObjects(String dialogNo, String phaseNo, java.util.List objects) {
        if (dialogNo == null || phaseNo == null) return;
        Element phase = getPhaseByNr(Integer.parseInt(dialogNo), Integer.parseInt(phaseNo));
        phase.removeContent();
        phase.addContent(objects);
    }

    protected void showPhases(String dialogNr) {
        showPhases(Integer.valueOf(dialogNr).intValue());
    }

    protected void showPhases(int dialogNr) {
        Element dialog = getDialogByNr(dialogNr);
        phaseList.removeAll();
        if (dialog != null) {
            java.util.List phases = dialog.getChildren("phase"); //$NON-NLS-1$
            if (phases == null || phases.size() == 0) {
                disableButtons();
            } else {
                enableButtons();
                for (int i = 1; i <= phases.size(); i++) {
                    phaseList.add(String.valueOf(i));
                }
            }
        } else {
            disableButtons();
        }
    }

    protected void showDialogs() {
        int dialogCount = dialogOwnerElement.getChildren().size();
        dialogList.removeAll();
        if (dialogCount == 0) {
            phaseList.setEnabled(false);
            phaseList.redraw();
        } else {
            phaseList.setEnabled(true);
            phaseList.redraw();
            System.err.println("phaseList.redraw()"); //$NON-NLS-1$
            for (int i = 1; i <= dialogOwnerElement.getChildren().size(); i++) {
                dialogList.add(String.valueOf(i));
            }
        }
    }

    protected Element getDialogByNr(int nr) {
        if (dialogOwnerElement != null) {
            nr--;
            java.util.List dialogs = dialogOwnerElement.getChildren("dialog"); //$NON-NLS-1$
            if (dialogs != null && nr >= 0) {
                if (nr < dialogs.size()) return (Element) dialogs.get(nr);
            }
        }
        Element newEl = new Element("dialog"); //$NON-NLS-1$
        dialogOwnerElement.addContent(newEl);
        return newEl;

    }

    protected Element getDialogByNr(String nr) {
        return getDialogByNr(Integer.valueOf(nr).intValue());
    }

    protected Element getPhaseByNr(int dialog, int phase) {
        Element dialogElement = getDialogByNr(dialog);
        phase--;
        java.util.List phases = dialogElement.getChildren("phase"); //$NON-NLS-1$
        if (phases != null) {
            System.err.println("size: " + phases.size() + " phase: " + phase); //$NON-NLS-1$ //$NON-NLS-2$
            if ((phases.size() > phase) && (phase > -1)) return (Element) phases.get(phase);
        }
        Element newEl = new Element("phase"); //$NON-NLS-1$
        dialogElement.addContent(newEl);
        return newEl;
    }

    protected void showDialogObject(int dialog, int phase) {
        enableButtons();
        Element phaseEl = getPhaseByNr(dialog, phase);
        java.util.List objects = phaseEl.getChildren("object"); //$NON-NLS-1$
        for (Iterator objIter = objects.iterator(); objIter.hasNext();) {
            Element element = (Element) objIter.next();
            generateDialogGroup(dialogGroup);
            ((DialogObjectElement) dialogObjects.peek()).fill(element);
        }
        dialogGroup.layout();
        dialogGroup.redraw();
    }

    protected void findMaxId() {
        globalMaxId = 0;
        if (dialogOwnerElement == null) return;
        java.util.List dialogs = dialogOwnerElement.getChildren("dialog"); //$NON-NLS-1$
        for (Iterator dialogIter = dialogs.iterator(); dialogIter.hasNext();) {
            Element dialogElement = (Element) dialogIter.next();
            java.util.List phases = dialogElement.getChildren("phase"); //$NON-NLS-1$
            for (Iterator phaseIter = phases.iterator(); phaseIter.hasNext();) {
                Element phaseElement = (Element) phaseIter.next();
                java.util.List objects = phaseElement.getChildren();
                for (Iterator objIter = objects.iterator(); objIter.hasNext();) {
                    Element element = (Element) objIter.next();
                    if (Integer.parseInt(element.getAttributeValue("id")) > globalMaxId) { //$NON-NLS-1$
                        globalMaxId = Integer.parseInt(element.getAttributeValue("id")); //$NON-NLS-1$
                    }

                }
            }
        }
        globalMaxId++;
    }

    protected int getNextId() {
        if (!freeIds.isEmpty()) {
            return ((Integer) freeIds.pop()).intValue();
        } else {
            return globalMaxId++;
        }

    }

    protected String getDialogNo() {
        int index = this.dialogList.getSelectionIndex();
        if (index >= 0) { return this.dialogList.getItem(index); }
        return null;
    }

    protected String getPhaseNo() {
        int index = this.phaseList.getSelectionIndex();
        if (index >= 0) { return this.phaseList.getItem(index); }
        return null;
    }

    protected void deletePhase(int phaseNo) {
        int dialogNo = Integer.valueOf(getDialogNo()).intValue();
        Element phase = getPhaseByNr(dialogNo, phaseNo);
        resetPhaseReferer(phaseNo);
        phaseList.setSelection(phaseList.getTopIndex());
        phaseList.notifyListeners(SWT.Selection, new Event());
        java.util.List ids = getIds(phase);
        phase.detach();
        addToFree(ids);
        showPhases(dialogNo);
    }

    protected void deleteDialog(int dialogNo) {
        Element dialog = getDialogByNr(dialogNo);
        dialogList.setSelection(dialogList.getTopIndex());
        dialogList.notifyListeners(SWT.Selection, new Event());
        dialog.detach();
        showDialogs();
    }

    protected String[] getItemNames() {
        Parent gamefile = dialogOwnerElement.getParent().getParent();
        java.util.List contents = gamefile.getContent();
        Element gameobjects = null;
        java.util.List retval = new ArrayList();
        for (Iterator contentIter = contents.iterator(); contentIter.hasNext();) {
            Content jdomContent = (Content) contentIter.next();
            if (jdomContent instanceof Element) {
                Element jdomElement = (Element) jdomContent;
                if (jdomElement.getName().equals("gameobjects")) { //$NON-NLS-1$
                    gameobjects = jdomElement;
                    break;
                }
            } else {
                System.err.println("not an Element: " + jdomContent.getValue() + "(" + jdomContent.getClass() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        if (gameobjects != null) {
            Element items = gameobjects.getChild("items"); //$NON-NLS-1$
            if (items != null) {
                java.util.List gameitems = items.getChildren("gameitem"); //$NON-NLS-1$
                for (Iterator gameItemIter = gameitems.iterator(); gameItemIter.hasNext();) {
                    Element gi = (Element) gameItemIter.next();
                    retval.add(gi.getAttributeValue("name")); //$NON-NLS-1$
                }
            }
        }

        String[] names = new String[retval.size()];
        retval.toArray(names);
        return names;
    }

    public void setButtonsEnabled(boolean enabled) {
        if (moreButton != null) moreButton.setEnabled(enabled);
        if (lessButton != null) lessButton.setEnabled(enabled);
    }

    public void enableButtons() {
        setButtonsEnabled(true);
    }

    public void disableButtons() {
        setButtonsEnabled(false);
    }

    public java.util.List getIds(Element phase) {
        java.util.List objects = phase.getChildren("object"); //$NON-NLS-1$
        java.util.List retval = new ArrayList();
        for (Iterator objIter = objects.iterator(); objIter.hasNext();) {
            Element element = (Element) objIter.next();
            retval.add(element.getAttributeValue("id")); //$NON-NLS-1$
        }
        return retval;
    }

    public void resetPhaseReferer(int phase) {
        int count = 0;

        Element dialog = getDialogByNr(getDialogNo());

        java.util.List phases = dialog.getChildren("phase"); //$NON-NLS-1$
        for (Iterator phaseIter = phases.iterator(); phaseIter.hasNext();) {
            Element phaseElement = (Element) phaseIter.next();
            java.util.List objects = phaseElement.getChildren("object"); //$NON-NLS-1$
            for (Iterator objectIter = objects.iterator(); objectIter.hasNext();) {
                Element objectElement = (Element) objectIter.next();
                String nextphase = objectElement.getAttributeValue("nextphase"); //$NON-NLS-1$
                if (nextphase != null) {
                    if (nextphase.equals(String.valueOf(phase))) {
                        objectElement.setAttribute("nextphase", "0"); //$NON-NLS-1$ //$NON-NLS-2$
                        objectElement.setAttribute("type", "dialog_end_failure"); //$NON-NLS-1$ //$NON-NLS-2$
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            MessageBox mb = new MessageBox(this.getParent(), SWT.ICON_INFORMATION);
            Object[] formatArgs = { new Integer(count) };
            mb.setMessage(MessageFormat.format(Messages.getString("DIALOG_INTEGRITY_MSG"),formatArgs));
            mb.open();
        }

    }

    public void addToFree(java.util.List ids) {
        for (Iterator idsIter = ids.iterator(); idsIter.hasNext();) {
            String strId = (String) idsIter.next();
            freeIds.push(Integer.valueOf(strId));
        }
    }

}