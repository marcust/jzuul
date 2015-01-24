/*
 * 	CVS: $Id: Give.java,v 1.6 2004/07/25 21:40:55 marcus Exp $
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
import org.jzuul.engine.Character;
import org.jzuul.engine.GameObject;
import org.jzuul.engine.Engine;


/**
 * 
  */
public class Give extends Command {
	/**
	 * 
	 */
	public Give() {
		super();
		this.name = "give"; //$NON-NLS-1$
		this.arguments = 3;
		this.desc = Messages.getString("GIVE_HELP"); //$NON-NLS-1$
	}

	protected boolean action() {
		String objName = args[0];
		String charName = args[2];
		Object[] formatArgs = { objName, charName };
		GameObject obj = player.findGameObject(objName);
		if (obj == null) return false;
		this.objectName = obj.getName();
		Character npc;
		GameObject NPCobj = player.findGameObject(charName);
		if (NPCobj==null) return false;
		if (!(NPCobj instanceof Character)) {
			player.say(MessageFormat.format(Messages.getString("GIVE_ERROR"),formatArgs)); //$NON-NLS-1$
			return false;
		} else {
			npc = (Character)NPCobj;
		}
		return npc.take(obj); 
	}

	public void help() {
		super.help();
		Engine.gui.println(Messages.getString("GIVE_HELP_USAGE")); //$NON-NLS-1$
		
	}

}

