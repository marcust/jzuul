/*
 * 	CVS: $Id: NahrungAufnehmen.java,v 1.1 2004/05/13 18:29:21 leh Exp $
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
import java.util.*;

import org.jzuul.engine.*;
import org.jzuul.engine.Engine;

public class NahrungAufnehmen extends Command {
	String[] sayings;

	public NahrungAufnehmen() {
		super();
		this.arguments = Command.VARARG_COMMAND;

	}

	protected boolean action() {
		switch (args.length) {
			case 0 :
				{
					Random rnd = new Random();
					int value = rnd.nextInt(sayings.length);
					Engine.gui.println(sayings[value]);
					return true;
				}
			case 1 :
				{
					return new Use().doAction(player, args);

				}
			default :
				return false;
		}

	}

}
