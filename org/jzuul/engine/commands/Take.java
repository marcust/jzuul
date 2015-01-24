/*
 * 	CVS: $Id: Take.java,v 1.7 2004/07/25 21:40:55 marcus Exp $
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

/**
 * 
  */
public class Take extends Command {

	public Take() {
		super();
		this.name = "take"; //$NON-NLS-1$
		this.arguments = 1;
		this.desc = Messages.getString("TAKE_HELP"); //$NON-NLS-1$

	}

	protected boolean action() {
	    Object[] formatArgs = {  Helpers.firstToUpper(args[0]) };
		if (currentRoom.getContent().containsGameObject(args[0])) {
			GameObject obj = currentRoom.getContent().getGameObject(args[0]);
			if (obj.isTakeable()) {
				currentRoom.getContent().deleteGameObject(args[0]);
				if (player.getInv().addGameObject(obj)) {
				    this.objectName = obj.getName();
					player.say(MessageFormat.format(Messages.getString("TAKE_SUCCESS"),formatArgs)); //$NON-NLS-1$
					obj.doEvent(Event.TAKEUP);
					return true;
				} else {
					Engine.gui.println(Messages.getString("TAKE_INVENTORY_FULL")); //$NON-NLS-1$
					currentRoom.getContent().addGameObject(obj);
					return false;
				}
			} else {
				player.say(Messages.getString("TAKE_UNTAKEABLE")); //$NON-NLS-1$
				return false;
			}
		} else {
			player.say(MessageFormat.format(Messages.getString("TAKE_NO_ITEM"), formatArgs)); //$NON-NLS-1$
			return false;
		}
	}
	public void help() {
		super.help();
		Engine.gui.println(Messages.getString("TAKE_HELP_USAGE")); //$NON-NLS-1$
	}

}
