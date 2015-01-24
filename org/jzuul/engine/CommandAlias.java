/*
 * 	CVS: $Id: CommandAlias.java,v 1.7 2004/07/25 18:08:14 marcus Exp $
 * 
 *  This file is part of Zuul.
 *
 *  Zuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Zuul is distributed in the hope that it will be useful,
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
 
package org.jzuul.engine;

import org.jzuul.engine.commands.Command;

/**
 * Diese Klasse bietet eine einfache Möglichkeite, einen Alias auf ein anderes
 * Kommando inklusive Argumenten zu setzen.
 * 
 * 
 * @version $Revision: 1.7 $
 */
public class CommandAlias extends Command {
	/**
	 * Das Command Objekt, für das dieser Alias gesetzt ist
	 */
	Command command;

	/**
	 * Die Parameter, die dem Command Objekt als Befehlsparameter übergeben werden
	 */
	String parameters;

	/**
	 * Erstellt einen neuen Alias
	 * 
	 * @param alias					der Alias (z.B. inv)
	 * @param command		das eigentliche Kommando (z.B. inspect)
	 * @param parameters	die Parameter zu diesem Kommando (z.B. inventory)
	 */
	public CommandAlias(String alias, Command command, String parameters) {
		super();
		this.name = alias;
		this.parameters = parameters;
		this.command = command;
		this.gameAction = command.isGameAction();
		this.isAppletSave = command.isAppletSave();
	}

	/**
	 * @see org.jzuul.engine.commands.Command#action()
	 */
	protected boolean action() {
		command.doAction(Engine.player, parameters.split(" "));
		return false;
	}

	/**
	 * @see org.jzuul.engine.commands.Command#help()
	 */	
	public void help() {
		command.help();
	}

}
