/*
 * 	CVS: $Id: Drop.java,v 1.5 2004/07/25 21:40:55 marcus Exp $
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
import org.jzuul.engine.Engine;

/**
 * 
  */
public class Drop extends Command {

	public Drop() {
		super();
		this.name = "drop"; //$NON-NLS-1$
		this.arguments = 1;
		this.desc = Messages.getString("DROP_HELP"); //$NON-NLS-1$
	}

	protected boolean action() {
	    Object[] name = { args[0] };
	    if (player.getInv().containsGameObject(args[0])) {
			GameObject obj = player.getInv().popGameObject(args[0]);
			obj.doEvent(Event.DROP);
			currentRoom.getContent().addGameObject( obj  );
			Engine.gui.println(MessageFormat.format(Messages.getString("DROP_SUCCESS"), name)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		} else {
			Engine.gui.println(MessageFormat.format(Messages.getString("DROP_FAILURE"), name)); //$NON-NLS-1$
			return false;
		}
	
	}

}
