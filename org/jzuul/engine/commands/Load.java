/*
 * 	CVS: $Id: Load.java,v 1.5 2004/07/25 21:40:55 marcus Exp $
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

package org.jzuul.engine.commands;

import java.text.MessageFormat;

import org.jzuul.engine.*;
import org.jzuul.engine.Engine;

/**
 * 
 */
public class Load extends Command {

	/**
	 * 
	 */
	public Load() {
		super();
		this.name = "load"; //$NON-NLS-1$
		this.desc = Messages.getString("LOAD_HELP"); //$NON-NLS-1$
		this.arguments = 1;
		this.gameAction = false;
		this.isAppletSave = false;
	}

	/* (non-Javadoc)
	 * @see commands.BefehlAction#action()
	 */
	protected boolean action() {
	    Object[] formatArgs = {args[0]};
	    
		if (Engine.loadFromFile(args[0])) {
			Engine.gui.println(MessageFormat.format(Messages.getString("LOAD_SUCCESS"),formatArgs)); //$NON-NLS-1$
			return true;
		} else {
			Engine.gui.println(MessageFormat.format(Messages.getString("LOAD_ERROR"),formatArgs)); //$NON-NLS-1$
			return false;	
		}
	}

}
