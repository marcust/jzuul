/*
 * 	CVS: $Id: CommandParser.java,v 1.3 2004/07/16 16:22:33 marcus Exp $
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
 * 	Initially based on an example by Michael Kolling and David J. Barnes
 * 
 */
 
package org.jzuul.engine;

/**
 * Dieser Parser liest Benutzereingaben und wandelt sie in
 * Befehle für das Adventure-Game um. Bei jedem Aufruf
 * liest er eine Zeile von der Konsole und versucht, diese als
 * einen Befehl aus bis zu zwei Wörtern zu interpretieren. Er
 * liefert den Befehl als ein Objekt der Klasse Befehl zurück.
 * 
 * Der Parser verfügt über einen Satz an bekannten Befehlen. Er
 * vergleicht die Eingabe mit diesen Befehlen. Wenn die Eingabe
 * keinen bekannten Befehl enthält, dann liefert der Parser ein als 
 * unbekannter Befehl gekennzeichnetes Objekt zurück.
 * 
 * 
 * @version $Revision: 1.3 $
 */

public class CommandParser {

	/**
	 * Erstellt aus dem gegebenen String ein CommandContainer Objekt
	 * 
	 * @param eingabezeile	ein einzeiliger String mit einer Eingabe
	 * @return	ein CommandContainer Objekt oder null bei einem Fehler
	 */
	public CommandContainer liefereBefehl(String eingabezeile) {
		
		CommandContainer retval;
			if (eingabezeile == null) {
				System.exit(0);
			}

			String[] commandline = eingabezeile.split(" ");

			if ((commandline != null) && (commandline.length > 0)) {
				retval = new CommandContainer(commandline[0]);
				String[] arguments = new String[commandline.length - 1];
				for (int i = 0; i < commandline.length - 1; i++) {
					arguments[i] = (commandline[i + 1]).toLowerCase();
				}
				retval.setArgs(arguments);
				return retval;
			}
		return null;
	}
}
