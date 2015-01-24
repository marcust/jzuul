/*
 * 	CVS: $Id: Go.java,v 1.6 2004/07/25 21:40:55 marcus Exp $
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
import org.jzuul.engine.rooms.*;

/**
 * 
 */
public class Go extends Command {

	public Go() {
		super();
		this.name = "go"; //$NON-NLS-1$
		this.arguments = 1;
		this.desc =
			Messages.getString("GO_HELP"); //$NON-NLS-1$
	}

	protected boolean action() {
		Room myCurrentRoom = player.getCurrentRoom();
		String direction = args[0];
		Object[] formatArgs = { args[0] };
		if (!(direction.equals("north") //$NON-NLS-1$
			|| direction.equals("south") //$NON-NLS-1$
			|| direction.equals("west") //$NON-NLS-1$
			|| direction.equals("east") //$NON-NLS-1$
			|| direction.equals("down") //$NON-NLS-1$
			|| direction.equals("up") //$NON-NLS-1$
			|| direction.equals("back"))) { //$NON-NLS-1$
			player.say(MessageFormat.format(Messages.getString("GO_DIRECTION_ERROR"), formatArgs)); //$NON-NLS-1$
			return false;
		}
			if (args[0].equals("back")) { //$NON-NLS-1$
			Room nextRoom = player.getLastRoom();
			if (nextRoom == null) {
				player.say(Messages.getString("GO_BACK_ERROR")); //$NON-NLS-1$
				return false;
			}
			player.setCurrentRoom(nextRoom);
			return true;
		}

		if (myCurrentRoom.getRoomByOrientation(args[0]) != null) {
			Room nextRoom = myCurrentRoom.getRoomByOrientation(args[0]);
			player.setCurrentRoom(nextRoom);
			return true;
		} else {
			player.say(MessageFormat.format(Messages.getString("GO_WAY_ERROR"),formatArgs)); //$NON-NLS-1$
			return false;
		}
	}

	public void help() {
		super.help();
		Engine.gui.println(Messages.getString("GO_HELP_USAGE")); //$NON-NLS-1$
	}
}
