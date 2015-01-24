/*
 * 	CVS: $Id: TargetEditor.java,v 1.16 2004/07/25 21:40:56 marcus Exp $
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;
import org.jzuul.engine.gui.utils.Util;


/**
 * 
 * Created on Jul 15, 2004
 * 
 * 
 * @version $Revision: 1.16 $
 */
public class TargetEditor extends Dialog {
    private class TargetGroup {
        protected class ConditionGroup {
            private Combo type, value, value2;
            private Group group;
            public ConditionGroup(Group group, Combo type, Combo value) {
                this.group = group;
                this.type = type;
                this.value = value;
                this.value2 = value2;
            }
            public void delete() {
                group.dispose();
            }
            public void fill(Element e) {
                type.select(type.indexOf(e.getAttributeValue("type"))); //$NON-NLS-1$
                type.notifyListeners(SWT.Selection, new Event());
                value.select(value.indexOf(e.getAttributeValue("value"))); //$NON-NLS-1$
                
            }
            public Element toXMLElement() {
                Element newEl = new Element("condition"); //$NON-NLS-1$
                System.err.println("toXMLELement: " + this); //$NON-NLS-1$
                newEl.setAttribute("type", type.getText()); //$NON-NLS-1$
                String val = value.getText();
                if (val != null && (!val.equals(""))) { //$NON-NLS-1$
                    newEl.setAttribute("value", val); //$NON-NLS-1$
                    return newEl;
                } else {
                    return null;
                }
            }
            
        }
        
        private Group group;
        private Text description;
        private Combo creator;
        private Stack conditionGroups;
        
        public TargetGroup(Group targetGroup, Text desc) {
            this.group = targetGroup;
            this.description = desc;
            conditionGroups = new Stack();
        }
        
        public void delete() {
            this.group.dispose();
        }
        
        public void deleteConditionGroup(Object cg) {
        	conditionGroups.remove(cg);
        	((ConditionGroup)cg).delete();
            redraw();
        }
        
        public void deleteConditionGroup() {
            ((ConditionGroup)conditionGroups.pop()).delete();
            redraw();
        }
        
        public ConditionGroup addConditionGroup(Group group, Combo type, Combo value, Combo value2) {
            ConditionGroup cg = new ConditionGroup(group,type,value);
            conditionGroups.push(cg);
            this.creator = value2;
            return cg;
            
        }
        
        public void redraw() {
            group.layout();
            group.redraw();
            group.getParent().layout();
            group.getParent().redraw();
        }
        
        public void fill(Element el) {
            String desc = el.getChildText("description"); //$NON-NLS-1$
            if (desc != null) {
                this.description.setText(desc);
            }
            String creatorName = el.getChildText("creator"); //$NON-NLS-1$
            if (creatorName != null) {
                creator.select(creator.indexOf(creatorName));
            }
            
        }
        
        public void fillLastCondition(Element el) {
            ConditionGroup lc = (ConditionGroup)conditionGroups.peek();
            lc.fill(el);
        }
        
        public Element toXMLElement() {
            Element newEl = new Element("target"); //$NON-NLS-1$
            newEl.addContent(new Element("description").setText(description.getText())); //$NON-NLS-1$
            
            for (Iterator iter = conditionGroups.iterator(); iter.hasNext();) {
                ConditionGroup cg = (ConditionGroup) iter.next();
                Element newCond = cg.toXMLElement();
                if (newCond != null) {
                    newEl.addContent(newCond);
                }
            }
            if (creator.getVisible() && (!creator.getText().equals(""))) { //$NON-NLS-1$
                newEl.addContent(new Element("creator").setAttribute("name",creator.getText())); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return newEl;
        }
        
    }
    
    private Element element;
    private Stack groups;
    private Composite mainComposite;
    
    /**
     * @param arg0
     */
    public TargetEditor(Shell arg0) {
        super(arg0);
        groups = new Stack();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public TargetEditor(Shell arg0, int arg1) {
        super(arg0, arg1);
        groups = new Stack();
    }
    
    public void open(Element elementWithTargets) {
        element = elementWithTargets;
        Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.BORDER | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.setText(Messages.getString("EDIT_TARGETS")); //$NON-NLS-1$
        shell.setLayout(new FillLayout());
        shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$
        shell.addShellListener(new ShellAdapter() {

            public void shellClosed(ShellEvent e) {
                cleanUpChanges();
            }
        });
        
        mainComposite = new Composite(shell, SWT.NONE);
        mainComposite.setLayout(new GridLayout(4,true));

        Button button = new Button(mainComposite, SWT.PUSH);
        button.setText(Messages.getString("MORE")); //$NON-NLS-1$

        GridData buttondat = new GridData();
        buttondat.heightHint = 30;
        buttondat.widthHint = 80;
        button.setLayoutData(buttondat);

        button.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                generateTargetGroup();
                mainComposite.layout();
                mainComposite.redraw();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Button button2 = new Button(mainComposite, SWT.PUSH);
        button2.setText(Messages.getString("LESS")); //$NON-NLS-1$
        GridData buttondat2 = new GridData();
        buttondat2.heightHint = 30;
        buttondat2.widthHint = 80;
        button2.setLayoutData(buttondat2);
        button2.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                if (groups.isEmpty()) return;
                TargetGroup g = ((TargetGroup) groups.pop());
                g.delete();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Button button3 = new Button(mainComposite, SWT.PUSH);
        button3.setText(Messages.getString("OK")); //$NON-NLS-1$
        GridData buttondat3 = new GridData(GridData.HORIZONTAL_ALIGN_END);
        buttondat3.heightHint = 30;
        buttondat3.widthHint = 80;
        button3.setLayoutData(buttondat3);
        button3.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                cleanUpChanges();
                shell.dispose();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
        
        
        showTargetGroups();
        shell.setSize(640,480);
        shell.open();

        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }

    }
    
    protected void generateTargetGroup() {
        final Group targetGroup = new Group(mainComposite,SWT.NONE);
        GridData tgDat = new GridData(GridData.FILL_HORIZONTAL);
        tgDat.horizontalSpan = 4;
        targetGroup.setLayoutData(tgDat);
        
        targetGroup.setLayout(new GridLayout(5, false));
        
        Label dLabel = new Label(targetGroup, SWT.NONE);
        dLabel.setText(Messages.getString("DESCRIPTION")); //$NON-NLS-1$
        
        Text description = new Text(targetGroup, SWT.BORDER);
        GridData dDat = new GridData(GridData.FILL_HORIZONTAL);
        description.setLayoutData(dDat);
        
        final TargetGroup ntg = new TargetGroup(targetGroup, description);
        
        /*
         * This code is for targets with multiple conditions
         * which is not supported in the engine anyway
         * remove the comments and you'll have multiple conditions 
        Button p = new Button(targetGroup, SWT.PUSH);
        p.setText("+");
        p.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                generateConditionGroup(ntg);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        Button m = new Button(targetGroup, SWT.PUSH);
        m.setText("-");
        m.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                ntg.deleteConditionGroup();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        */
        Button delete = new Button(targetGroup, SWT.PUSH);
        delete.setText(Messages.getString("DELETE")); //$NON-NLS-1$
        delete.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                ntg.delete();
                mainComposite.layout();
                mainComposite.redraw();
                groups.remove(ntg);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        
        generateConditionGroup(ntg);
        mainComposite.layout();
        mainComposite.redraw();
        
        groups.push(ntg);
        
        
    }
    
    protected void generateConditionGroup(final TargetGroup tg) {
        Group cg = new Group(tg.group, SWT.NONE);
        GridData cgDat = new GridData(GridData.FILL_HORIZONTAL);
        cgDat.horizontalSpan = 5;
        cg.setLayoutData(cgDat);
        
        cg.setLayout(new GridLayout(5, false));
        
        Label tLabel = new Label(cg, SWT.NONE);
        tLabel.setText(Messages.getString("TYPE")); //$NON-NLS-1$
        
        
        final Combo type = new Combo(cg, SWT.READ_ONLY);
        GridData tDat = new GridData(GridData.FILL_HORIZONTAL);
        type.setLayoutData(tDat);
        type.add("item"); //$NON-NLS-1$
        type.add("talk"); //$NON-NLS-1$
        type.add("give"); //$NON-NLS-1$
        
        Label vLabel = new Label(cg, SWT.NONE);
        vLabel.setText(Messages.getString("VALUE")); //$NON-NLS-1$
        
        final Combo value = new Combo(cg, SWT.READ_ONLY);
                
        GridData vDat = new GridData(GridData.FILL_HORIZONTAL);
        value.setLayoutData(vDat);
        
      
        final Combo value2 = new Combo(cg, SWT.READ_ONLY);
        
        type.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                if (type.getText().equals("give")) { //$NON-NLS-1$
                    String[] items = JdomHelpers.getItemNames(element);
                    if (items != null) value.setItems(items);
                    value2.setVisible(true);
                }
                if (type.getText().equals("item")) { //$NON-NLS-1$
                    String[] items = JdomHelpers.getItemNames(element);
                    if (items != null) value.setItems(items);
                    value2.setVisible(false);
                }
                if (type.getText().equals("talk")) { //$NON-NLS-1$
                    String[] chars = JdomHelpers.getCharacterNames(element);
                    if (chars != null) value.setItems(chars);
                    value2.setVisible(false);
                }
                GDKMainWindow.saveSelectFirst(value);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
  
        GridData vDat2 = new GridData(GridData.FILL_HORIZONTAL);
        value2.setLayoutData(vDat2);
        String[] items = JdomHelpers.getCharacterNames(element);
        if (items != null) value2.setItems(items);
        
        GDKMainWindow.saveSelectFirst(type);
        GDKMainWindow.saveSelectFirst(value2);
        
        final Object ncg = tg.addConditionGroup(cg,type,value,value2);
        
        /*Button delete = new Button(cg, SWT.PUSH);
        delete.setText("delete");
        delete.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                tg.deleteConditionGroup(ncg);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {}
        });
        */
        tg.redraw();
    }
    
    
    public void cleanUpChanges() {
        element.removeChildren("target"); //$NON-NLS-1$
        for (Iterator groupIter = groups.iterator(); groupIter.hasNext();) {
            TargetGroup targetGroup = (TargetGroup) groupIter.next();
            Element newEl = targetGroup.toXMLElement();
            if (newEl != null) {
            	element.addContent(newEl);
            }
        }
    }
    
    public void showTargetGroups() {
        if (element != null) {
        java.util.List targets = element.getChildren("target"); //$NON-NLS-1$
        for (Iterator targetIter = targets.iterator(); targetIter.hasNext();) {
            Element targetElement = (Element) targetIter.next();
            generateTargetGroup();
            TargetGroup currentTg = (TargetGroup)groups.peek();
            currentTg.fill(targetElement);
            java.util.List conditions = targetElement.getChildren("condition"); //$NON-NLS-1$
            for (Iterator conditionIter = conditions.iterator(); conditionIter.hasNext();) {
                Element conditionElement = (Element) conditionIter.next();
                //generateConditionGroup(currentTg);
                currentTg.fillLastCondition(conditionElement);
            }
        }
        }
    }
}
