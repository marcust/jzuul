/*
 * 	CVS: $Id: Hallo.java,v 1.4 2004/07/25 21:40:55 marcus Exp $
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

import org.jzuul.engine.*;

/**
 * 
 */
public class Hallo extends Command {

	/**
	 * 
	 */
	public Hallo() {
		super();
		this.name = "hallo"; //$NON-NLS-1$
		this.arguments = 0;
		this.desc = Messages.getString("HELLO_HELP"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see BefehlAction#action()
	 */
	protected boolean action() {
		player.say(Messages.getString("HELLO_HELLO")); //$NON-NLS-1$
		return true;
	}

}
