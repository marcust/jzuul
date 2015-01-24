/*
 * 	CVS: $Id: ActionEditorComposite.java,v 1.11 2004/07/25 21:40:56 marcus Exp $
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
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.jdom.Element;

/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.11 $
 */
public class ActionEditorComposite extends Composite {

    protected class ActionGroup {

        protected String eventName;

        protected Combo action;

        protected Widget value;

        protected Widget value2;

        protected Group actionGroup;

        protected Button delete;

        protected Element actionElement;

        public ActionGroup(Combo actionCombo, Widget valueText, Group actionGroupGroup) {
            this.action = actionCombo;
            this.value = valueText;
            this.actionGroup = actionGroupGroup;

            actionCombo.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent e) {
                    String t = action.getText();
                    if (value2 != null) {
                        value2.dispose();
                        value2 = null;
                    }
                    if (t.equals("player-say") || t.equals("npc-say")) { //$NON-NLS-1$ //$NON-NLS-2$
                        makeTextInput();
                    }
                    if (t.equals("delete-item") || t.equals("room-item") || (t.equals("inventory-item"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        makeItemInput();
                    }
                    if (t.equals("action")) { //$NON-NLS-1$
                        makeActionInput();
                    }
                    if (t.equals("target")) { //$NON-NLS-1$
                        makeTargetInput();
                    }
                    if (t.equals("random-success")) { //$NON-NLS-1$
                        makeNoInput();
                    }
                    if (t.equals("alter-item")) { //$NON-NLS-1$
                        makeAlterItemInput();
                    }
                    actionGroup.layout();
                    actionGroup.redraw();
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }

        public Element toXMLElement() {
            if (actionElement != null) {
                java.util.List targetChilds = actionElement.getChildren();
                if ( (targetChilds != null) && (targetChilds.size() > 0)) {
                    return actionElement;
                } else {
                    return null;
                }
            }

            Element newEl = new Element(action.getText());

            if (value instanceof Combo) {
                String cv = ((Combo) value).getText();
                if (!cv.equals("")) { //$NON-NLS-1$
                    if (cv.equals("action")) { //$NON-NLS-1$
                        newEl.setAttribute("type", cv); //$NON-NLS-1$
                    } else if (value2 != null) {
                        newEl.setAttribute("property", cv); //$NON-NLS-1$
                        newEl.setText(((Text) value2).getText());
                    } else {
                        newEl.setText(cv);
                    }
                } else {
                    return null;
                }
            }
            if (value instanceof Text) {
                String text = ((Text) value).getText();
                if (text != null && (!text.equals(""))) { //$NON-NLS-1$
                    newEl.setText(text);
                } else {
                    return null;
                }
            }
            return newEl;
        }

        public void delete() {
            Composite parent = actionGroup.getParent();
            actionGroup.dispose();
            parent.layout();
            parent.redraw();
        }

        public void fill(Element el) {
            String tag = el.getName();
            action.select(action.indexOf(tag));
            action.notifyListeners(SWT.Selection, new Event());

            if (value2 != null) {
                ((Combo) value).select(((Combo) value).indexOf(el.getAttributeValue("property"))); //$NON-NLS-1$
                ((Text) value2).setText(el.getText());
                return;
            }

            if (value instanceof Combo) {
                String val = el.getText();
                if (val != null) {
                    ((Combo) value).select(((Combo) value).indexOf(val));
                } else {
                    ((Combo) value).select(((Combo) value).indexOf(el.getAttributeValue("type"))); //$NON-NLS-1$
                }
                return;
            }
            if (value instanceof Text) {
                Text t = (Text) value;
                t.setText(el.getText());
                return;
            }
            if (value instanceof Button) {
                actionElement = el;
                return;
            }

        }

        public void cleanWidgets() {
            if (this.value != null) {
                value.dispose();
            }
            delete.dispose();
            value = null;
            delete = null;
            actionElement = null;
        }

        public void createDeleteButton() {
            Button db = new Button(this.actionGroup, SWT.PUSH);
            db.setText(Messages.getString("DELETE")); //$NON-NLS-1$
            db.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent e) {
                    groups.remove(ActionGroup.this);
                    delete();
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
            this.delete = db;
        }

        public void makeItemInput() {
            cleanWidgets();
            Combo itemCombo = new Combo(actionGroup, SWT.READ_ONLY);
            itemCombo.setItems(JdomHelpers.getItemNames(element));
            GDKMainWindow.saveSelectFirst(itemCombo);
            value = itemCombo;
            valueLayoutData();
            createDeleteButton();
        }

        public void makeTextInput() {
            cleanWidgets();
            Text textBox = new Text(actionGroup, SWT.BORDER);
            value = textBox;
            valueLayoutData();
            createDeleteButton();
        }

        public void makeActionInput() {
            cleanWidgets();
            Combo actionCombo = new Combo(actionGroup, SWT.READ_ONLY);
            actionCombo.add("moveRandom"); //$NON-NLS-1$
            actionCombo.add("leaveRoom"); //$NON-NLS-1$
            actionCombo.add("deleteItem"); //$NON-NLS-1$
            GDKMainWindow.saveSelectFirst(actionCombo);
            value = actionCombo;
            valueLayoutData();
            createDeleteButton();
        }

        public void makeTargetInput() {
            cleanWidgets();
            Button bt = new Button(actionGroup, SWT.PUSH);
            bt.setText(Messages.getString("EDIT_TARGETS")); //$NON-NLS-1$
            value = bt;
            bt.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent e) {
                    if (actionElement == null) {
                        actionElement = new Element("target"); //$NON-NLS-1$
                        actionElement.addContent(new Element("description")); //$NON-NLS-1$
                    } else {
                        actionElement.detach();
                    }
                    TargetEditor ed = new TargetEditor(new Shell(e.display), SWT.NONE);
                    Element foo = new Element("actions"); //$NON-NLS-1$
                    foo.addContent(actionElement);
                    ed.open(foo);
                    actionElement = foo.getChild("target"); //$NON-NLS-1$
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            valueLayoutData();
            createDeleteButton();
        }

        public void makeNoInput() {
            cleanWidgets();

            createDeleteButton();
        }

        public void makeAlterItemInput() {
            cleanWidgets();
            Combo itemCombo = new Combo(actionGroup, SWT.READ_ONLY);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            itemCombo.setLayoutData(gd);
            itemCombo.add("description"); //$NON-NLS-1$
            GDKMainWindow.saveSelectFirst(itemCombo);
            value = itemCombo;

            Text newDesc = new Text(actionGroup, SWT.BORDER);
            GridData pcgd = new GridData(GridData.FILL_HORIZONTAL);
            newDesc.setLayoutData(pcgd);
            value2 = newDesc;

            createDeleteButton();
        }

        public void valueLayoutData() {
            if (value != null && value instanceof Control) {
                GridData valDat = new GridData(GridData.FILL_HORIZONTAL);
                valDat.horizontalSpan = 2;
                ((Control) value).setLayoutData(valDat);

            }
        }
        


    }

    protected final String[] actions = { "target", "action", "player-say", "npc-say", "inventory-item", "room-item", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
            "alter-item", "delete-item", "random-success" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    protected Stack groups;

    private Button more;

    private Button less;

    private Element element;

    /**
     * @param arg0
     * @param arg1
     */
    public ActionEditorComposite(Composite arg0, int arg1, Element elementWithActions) {
        super(arg0, arg1);
        Arrays.sort(actions);
        groups = new Stack();
        this.element = elementWithActions;

        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.horizontalSpan = 7;
        this.setLayoutData(layoutData);

        this.setLayout(new GridLayout(2, false));

        more = new Button(this, SWT.PUSH);
        more.setText(Messages.getString("MORE")); //$NON-NLS-1$

        GridData buttondat = new GridData();
        buttondat.heightHint = 30;
        buttondat.widthHint = 80;
        more.setLayoutData(buttondat);

        more.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                generateActionGroup();
                layout();
                redraw();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        less = new Button(this, SWT.PUSH);
        less.setText(Messages.getString("LESS")); //$NON-NLS-1$
        GridData buttondat2 = new GridData();
        buttondat2.heightHint = 30;
        buttondat2.widthHint = 80;
        less.setLayoutData(buttondat2);
        less.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                if (groups.isEmpty()) return;
                ActionGroup g = ((ActionGroup) groups.pop());
                g.delete();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

    }

    protected void generateActionGroup() {
        /*
         * an action group is a combo box combined with a label to specify an
         * action
         */
        final Group actionGroup = new Group(this, SWT.NONE);

        GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
        groupData.horizontalSpan = 2;
        actionGroup.setLayoutData(groupData);

        actionGroup.setLayout(new GridLayout(4, false));

        Combo combo = new Combo(actionGroup, SWT.READ_ONLY);
        GridData comboDat = new GridData();
        comboDat.horizontalSpan = 1;
        combo.setLayoutData(comboDat);
        combo.setItems(actions);

        Text text = new Text(actionGroup, SWT.BORDER);
        GridData textDat = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        textDat.horizontalSpan = 2;
        text.setLayoutData(textDat);

        ActionGroup newGroup = new ActionGroup(combo, text, actionGroup);
        newGroup.createDeleteButton();
        combo.select(0);
        combo.notifyListeners(SWT.Selection, new Event());
        groups.push(newGroup);

    }

    public void showActionElements(java.util.List singleActions) {
        if (singleActions == null) return;
        for (Iterator actionIter = singleActions.iterator(); actionIter.hasNext();) {
            Element action = (Element) actionIter.next();
            generateActionGroup();
            ActionGroup last = (ActionGroup) groups.peek();
            last.fill(action);
        }
        this.layout();
        this.redraw();

    }

    public java.util.List getElementList() {
        java.util.List newList = new ArrayList();
        for (Iterator actionGroupIter = groups.iterator(); actionGroupIter.hasNext();) {
            ActionGroup ag = (ActionGroup) actionGroupIter.next();
            Element newEl = ag.toXMLElement();
            if (newEl != null) {
                newEl.detach();
                newList.add(newEl);
            }
            ag.delete();
            actionGroupIter.remove();
        }
        return newList;

    }

    public void setEnabled(boolean enable) {
        more.setEnabled(enable);
        less.setEnabled(enable);
    }

}