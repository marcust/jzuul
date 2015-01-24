/*
 * 	CVS: $Id: Help.java,v 1.7 2004/07/25 21:40:55 marcus Exp $
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
public class Help extends Command {

	public Help() {
		super();
		this.name = "help"; //$NON-NLS-1$
		this.arguments = -1;
		this.desc = Messages.getString("HELP_HELP"); //$NON-NLS-1$
		this.gameAction = false;
	}

	protected boolean action() {
		if (args.length == 0) {
		  Engine.gui.println(Messages.getString("HELP_START_MSG")); //$NON-NLS-1$
		  Engine.gui.println();
		  Engine.gui.println(Messages.getString("HELP_COMMAND_LIST_MSG")); //$NON-NLS-1$
		  Engine.gui.println("   " + Engine.commands.keySet().toString()); //$NON-NLS-1$
		  Engine.gui.println();
		  Engine.gui.println(Messages.getString("HELP_DETAIL_MSG")); //$NON-NLS-1$
		  return true;
		}
		if (args.length == 1) {
		    Object[] formatArgs = { args[0] };
			if (Engine.commands.containsKey(args[0])) {
				((Command)Engine.commands.get(args[0])).help();
			} else {
				Engine.gui.println(MessageFormat.format(Messages.getString("HELP_ERROR_MSG"),formatArgs)); //$NON-NLS-1$
			}
			return true;
		}
		this.help();
		return false;
		}
		
		public void help() {
			super.help();
			Engine.gui.println(Messages.getString("HELP_HELP_USAGE")); //$NON-NLS-1$
		}
		

}
