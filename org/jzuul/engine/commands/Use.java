/*
 * 	CVS: $Id: Use.java,v 1.6 2004/07/25 21:40:55 marcus Exp $
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
import org.jzuul.engine.GameObject;
import org.jzuul.engine.Item;
import org.jzuul.engine.Engine;

/**
 * 
  */
public class Use extends Command {

	public Use() {
		super();
		this.arguments = Command.VARARG_COMMAND;
		this.name = "use"; //$NON-NLS-1$
		this.desc = Messages.getString("USE_HELP"); //$NON-NLS-1$
	}

	protected boolean action() {
		if (args.length == 1) {
		    Object[] formatArgs = { args[0] };
			// einen Gegenstand im Inventar oder Raumalk to hacker
			GameObject obj = player.findGameObject(args[0]);
			if (obj != null) {
				if (obj.isUsable()) {
					if (obj instanceof Item) {
						((Item) obj).doEvent(Event.USE);
						return true;
					} else {
						player.say(Messages.getString("USE_ERROR_WANT")); //$NON-NLS-1$
					}
				} else {
					player.say(Messages.getString("USE_ERROR_CANT")); //$NON-NLS-1$
					return false;
				}
			} else {
				player.say(MessageFormat.format(Messages.getString("USE_NO_ITEM_0"),formatArgs)); //$NON-NLS-1$
				return false;
			}
		}
		if (args.length == 3) {
		    Object[] formatArgs = { args[0], args[2] };
			// einen Gegenstand im Inventar mit gegenstand im Inventar oder Room
			if (player.getInv().containsGameObject(args[0])) {
				GameObject item1 = player.findGameObject(args[0]);
				if (item1 == null) {
					player.say(MessageFormat.format(Messages.getString("USE_NO_ITEM_0"),formatArgs)); //$NON-NLS-1$
					return false;
				}
				GameObject item2 = player.findGameObject(args[2]);
				if (item2 == null) {
					player.say(MessageFormat.format(Messages.getString("USE_NO_ITEM_1"),formatArgs)); //$NON-NLS-1$
					return false;
				}
				if (item1 instanceof Item) {
					Item item3 = (Item) item1;
					return item3.useWith(item2);
				}
			} else {
				player.say(MessageFormat.format(
					Messages.getString("USE_ERROR_NOT_IN_INVENTORY"),formatArgs)); //$NON-NLS-1$
			}
			return false;
		}
		Object[] nameArray = { this.name };
		Engine.gui.println(MessageFormat.format(Messages.getString("COMMAND_ERROR_PARAMS"), nameArray)); //$NON-NLS-1$
		this.help();
		return false;
	}

	public void help() {
		super.help();
		Engine.gui.println(Messages.getString("USE_HELP_USAGE")); //$NON-NLS-1$
	}

}
