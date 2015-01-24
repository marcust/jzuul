/*
 * 	CVS: $Id: Inspect.java,v 1.4 2004/07/25 21:40:55 marcus Exp $
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
public class Inspect extends Command {

	/**
	 * 
	 */
	public Inspect() {
		super();
		this.arguments = 1;
		this.name = "inspect"; //$NON-NLS-1$
		this.desc = Messages.getString("INSPECT_HELP"); //$NON-NLS-1$
	}

	protected boolean action() {
		String itemName = args[0];
		Object[] formatArgs = { args[0] };
		if (player.getInv().containsGameObject(itemName)) {
			player.say(player.getInv().getGameObject(itemName).getDescription());
			return true;
		}
		if (currentRoom.getContent().containsGameObject(itemName)) {
			player.say(currentRoom.getContent().getGameObject(itemName).getDescription());
			return true;
		}
		player.say(MessageFormat.format(Messages.getString("INSPECT_NO_ITEM_ERROR"), formatArgs)); //$NON-NLS-1$
		return false;
	}

	public void help() {
		super.help();
		Engine.gui.println(Messages.getString("INSPECT_HELP_USAGE")); //$NON-NLS-1$
	}

}
