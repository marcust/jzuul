/*
 * 	CVS: $Id: InputDialog.java,v 1.17 2004/07/25 21:40:56 marcus Exp $
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jzuul.engine.gui.utils.Util;

public class InputDialog extends Dialog {
    Object result;
    Label message;    
    Text input;
    String msgtext=""; //$NON-NLS-1$
    String def=""; //$NON-NLS-1$
    int style = SWT.BORDER;
    Button ok;
    
    public InputDialog (Shell parent, int style) {
            super (parent, style);
    }
    public InputDialog (Shell parent) {
            this (parent, 0); // your default style bits go here (not the Shell's style bits)
    }
    public String open () {
            Shell parent = getParent();
            final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            shell.setText(getText());
            shell.setSize(400,150);
            shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$
            shell.setLayout(new GridLayout(2,true));
            
            String text = ""; //$NON-NLS-1$
            
            message = new Label((Composite)shell,SWT.NONE);
            message.setText(msgtext);
            
            
            
            input = new Text((Composite)shell,style);
            input.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
            input.setText("\n\n\n"); //$NON-NLS-1$
            input.addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent arg0) {}

                public void keyReleased(KeyEvent arg0) {
                    if (arg0.keyCode == SWT.CR && (style & SWT.MULTI)==0) {
                        ok.notifyListeners(SWT.Selection, new Event());
                    }

                }
            });
            
            Composite buttonComp = new Composite((Composite)shell,SWT.NONE);
            GridData buttonCompDat = new GridData(GridData.FILL_HORIZONTAL);
            buttonCompDat.horizontalSpan = 2;
            buttonComp.setLayoutData(buttonCompDat);
            
            buttonComp.setLayout(new GridLayout(2,true));
            
            ok = new Button(buttonComp,SWT.NONE);
            ok.setText(Messages.getString("OK")); //$NON-NLS-1$
            ok.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    if (input.getText().length() > 0) {
                        shell.dispose();
                    }
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });
            GridData okDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
            okDat.widthHint = 80;
            ok.setLayoutData(okDat);
            
            Button cancel = new Button(buttonComp,SWT.NONE);
            cancel.setText(Messages.getString("CANCEL")); //$NON-NLS-1$
            cancel.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent arg0) {
                    input.setText(""); //$NON-NLS-1$
                    shell.dispose();

                }

                public void widgetDefaultSelected(SelectionEvent arg0) {}
            });
            GridData cancelDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
            cancelDat.widthHint = 80;
            cancel.setLayoutData(cancelDat);
            
            	shell.pack();
                input.setText(def);

            shell.open();
            Display display = parent.getDisplay();
            input.setFocus();
            while (!shell.isDisposed()) {
                	text =  input.getText();
                    if (!display.readAndDispatch()) display.sleep();
            }
            if (text.matches("\\s*")) { //$NON-NLS-1$
                return null;
            } else {
                return text;
            }
    }
    
    public void setMessage(String message) {
        msgtext = message;
    }
    
    public void setDefaultValue(String value) {
        def = value;
    }
    
    public void setStyle(int style) {
        this.style = style;
    }
    
    public String openNoWhitespace() {
    	String retVal = open();
    	if (retVal == null) return null;
    	String newVal = retVal.replaceAll("\\s+","").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
    	if (retVal.length() != newVal.length()) {
    		MessageBox mb = new MessageBox(this.getParent(),SWT.ICON_INFORMATION);
    	
    		mb.setMessage(Messages.getString("WHITESPACE_TRUNC")); //$NON-NLS-1$
    		mb.open();
    	}
    	return newVal;
    }
    
 
}
