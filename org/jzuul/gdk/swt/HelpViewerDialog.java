/*
 * 	CVS: $Id: HelpViewerDialog.java,v 1.2 2004/07/25 21:40:56 marcus Exp $
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


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jzuul.engine.gui.utils.Util;

public class HelpViewerDialog extends Dialog {
    
    public HelpViewerDialog (Shell parent, int style) {
            super (parent, style);
    }
    public HelpViewerDialog (Shell parent) {
            this (parent, 0); // your default style bits go here (not the Shell's style bits)
    }
    public void open() {
            Shell parent = getParent();
            final Shell shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.MIN  | SWT.RESIZE );
            shell.setText(Messages.getString("HELP_TITLE")); //$NON-NLS-1$
            shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$
            shell.setLayout(new GridLayout(1,true));

            Browser b = new Browser(shell, SWT.NONE);
            
            GridData bDat = new GridData(GridData.FILL_BOTH);
            b.setLayoutData(bDat);
            
            b.setUrl("http://www.jzuul.org/gdkdoc/gdk_documentation.html"); //$NON-NLS-1$
            
            shell.pack();
            shell.setSize(900,600);
            
            shell.open();
            Display display = parent.getDisplay();
            
            while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) display.sleep();
            }
            
    }
 
 
}
 