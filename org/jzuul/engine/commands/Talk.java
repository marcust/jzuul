/*
 * 	CVS: $Id: Talk.java,v 1.6 2004/07/25 21:40:55 marcus Exp $
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
import org.jzuul.engine.Character;
import org.jzuul.engine.Engine;

/**
 * 
  */
public class Talk extends Command {

	/**
	 * 
	 */
	public Talk() {
		super();
		this.name = "talk"; //$NON-NLS-1$
		this.arguments = -1;
		this.desc = Messages.getString("TALK_HELP"); //$NON-NLS-1$
	}

	protected boolean action() {
		String name;
		switch (args.length) {
			case 2: 	name = args[1];
							break;
			case 1:  name = args[0];
							break;
			default: Object[] nameArray = { this.name };
					 Engine.gui.println(MessageFormat.format(Messages.getString("COMMAND_ERROR_PARAMS"), nameArray)); //$NON-NLS-1$
							this.help();
							return false;
		}
		this.objectName = name;
		if (currentRoom.getContent().containsGameObject(name)) {
				GameObject obj = currentRoom.getContent().getGameObject(name);
				if (obj instanceof Character) {
					Character person = (Character)obj;
					Engine.debug("Called talk to for " + person.getName(), 1); //$NON-NLS-1$
					return person.talkTo();
				} else {
					player.say(Messages.getString("TALK_ERROR")); //$NON-NLS-1$
					return false;
				}
		} else {
		    Object[] formatArgs = { name };
			player.say(MessageFormat.format(Messages.getString("TALK_NO_CHARACTER"),formatArgs)); //$NON-NLS-1$
			return false;
		}
	}

}
