/*
 * 	CVS: $Id: Look.java,v 1.5 2004/07/25 21:40:55 marcus Exp $
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

import org.jzuul.engine.exceptions.GuiNotInitializedException;
import org.jzuul.engine.*;
import org.jzuul.engine.Engine;

/**
 * 
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Look extends Command {

	/**
	 * 
	 */
	public Look() {
		super();
		this.name = "look"; //$NON-NLS-1$
		this.arguments = Command.VARARG_COMMAND;
		this.gameAction = false;
		this.desc = Messages.getString("LOOK_HELP"); //$NON-NLS-1$
	}

	protected boolean action() {
		if (args.length == 0) {
			try {
			currentRoom.printBeschreibung();
			} catch (GuiNotInitializedException e) {
				System.err.println(e.getMessage());
			}
			return true;
		}
		if (args.length == 1) {
			String desc = currentRoom.getWayDescriptionByDirection(args[0]);
			if (desc == null) {
				//maybe an object?
				if (player.findGameObject(args[0]) != null) {
					return new Inspect().doAction(player,args);	
				} else {
				player.say(Messages.getString("LOOK_NOTHING")); //$NON-NLS-1$
				return false;
				}
				
			} else {
				player.say(Messages.getString("LOOK_YOU_SEE") + desc); //$NON-NLS-1$
				return true;
			}
		}
		Engine.gui.println(Messages.getString("LOOK_ERROR")); //$NON-NLS-1$
		this.help();
		return false;
	}

	public void help() {
		super.help();
		Engine.gui.println(Messages.getString("LOOK_HELP_USAGE")); //$NON-NLS-1$
	}

}
