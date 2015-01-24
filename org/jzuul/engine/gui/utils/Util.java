/*
 * 	CVS: $Id: Util.java,v 1.3 2004/07/16 16:22:34 marcus Exp $
 * 
 *  This file is part of JZuul.
 *
 *  JZuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  JZuul is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Zuul; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Copyrigth 2004 by marcus, leh
 * 
 */
package org.jzuul.engine.gui.utils;


import java.awt.Color;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * This class provides some tool functions for the gui, as centering the window
 * 
 */
public class Util {
	
	public static void centerWindow(Shell shell){
		Rectangle absoluteRect = shell.getMonitor().getClientArea();
		Rectangle shellRect = shell.getBounds();
	
		shell.setLocation( ( (absoluteRect.width - shellRect.width) / 2), ((absoluteRect.height - shellRect.height) / 2));
	}
	
	public static Image getImagefromResource(Display display,String resName){
		InputStream is = Util.class.getClassLoader().getResourceAsStream(resName);
		
		if (is == null) {
		    System.err.println("is (stream) was null");
		    return null;
		    
		}
		Image image = null;
		if (is != null) { 
			image = new Image(display,is);
		} else {
			image = new Image(display,320,320); 
		}
	
		return image;
	}
	
	public static RGB awtColorToRGB(Color color){
		return new RGB(color.getRed(),color.getGreen(),color.getBlue());
	}

}
