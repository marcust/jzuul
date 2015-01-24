/*
 * 	CVS: $Id: Main.java,v 1.15 2004/07/25 21:40:56 marcus Exp $
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

package org.jzuul.gdk;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jzuul.gdk.swt.GDKMainWindow;
import org.jzuul.gdk.swt.Messages;
import org.jzuul.gdk.swt.Splash;

/**
 * 
 * 
 *  
 */
public class Main {

    public static void main(String[] args) {
        String filename = null;
        if (args.length > 0) {
            filename = args[0];
        }
        Main main = new Main(filename);
    }

    private Main(String filename) {
        Display display = new Display();
        Splash s = new Splash(display);
        int steps = 3;
        if (filename != null) steps++;
        s.show(steps, Messages.getString("SPLASH_STARTING")); //$NON-NLS-1$

        GDKMainWindow main = new GDKMainWindow();

        try {
            s.nextTask(Messages.getString("SPLASH_CREATING_GUI")); //$NON-NLS-1$
            main.createGui(display);

            if (filename != null) {
                s.nextTask(Messages.getString("SPLASH_LOADING")); //$NON-NLS-1$
                main.openFile(filename);
            }

            s.nextTask(Messages.getString("SPLASH_FINISHED")); //$NON-NLS-1$
            s.close();

            main.open(display);

        } catch (Exception e) {
            main.saveFile();
            
            MessageBox mb = new MessageBox(new Shell(display), SWT.ICON_ERROR);
            mb.setText(Messages.getString("FATAL_ERROR")); //$NON-NLS-1$
            mb.setMessage(Messages.getString("FATAL_ERROR_OCCURED") + e.getMessage() + Messages.getString("FATAL_ERROR_DATA")); //$NON-NLS-1$ //$NON-NLS-2$
            
            mb.open();
            e.printStackTrace();
        }
    }
}