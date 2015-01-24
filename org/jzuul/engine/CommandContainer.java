/*
 * 	CVS: $Id: CommandContainer.java,v 1.6 2004/07/16 16:22:33 marcus Exp $
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

package org.jzuul.engine;

/**
 * 
 * @version $Revision: 1.6 $
 */
public class CommandContainer {
	/**
	 * die Aktion, also der Name des Befehls
	 */
	private String action;

	/**
	 * die Argumente des Befehls
	 */	
	private String[] args;
	
	/**
	 * hat das Kommando Parameter gehabt
	 */
	private boolean hasArgs = false;

	//---------------------------------------

	/**
	 * tja, der konstruktor für einen Befehl :-D
	 * 
	 * @param action ein gültiges Befehlswort
	 */
	public CommandContainer(String action) {
		this.action = action;
	
	}
	
	/**
	 * Setzt die Argumente
	 * 
	 * @param args	neue Liste von Argumenten
	 */
	public void setArgs(String[] args) {
		this.hasArgs = true;
		this.args = args;
	}

	/**
	 * Gibt die Argumenteliste des Command Objektes zurück
	 * 
	 * @return	die Liste der Argument die der Befehl hat
	 */
	public String[] getArgs() {
		return args;
	}


	/**
	 * Diese Methode setzt das Hauptbefehlsowort eines Befehls Objekt und sollte nur beim 
	 * initialisieren des Objektes benutzt werden.
	 * 
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Gibt das eigentliche Befehlswort zurück
	 * 
	 * @return	das Befehlswort
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Hatte die Befehlszeile Argumente?
	 * 
	 * @return	true wenn dem so war, false otherwise
	 */
	public boolean hasArgs() {
		return hasArgs;
	}
	
	/**
	 * Ist der Befehl ein Kommando das zum Erfüllen eines Targets 
	 * benutzt werden kann
	 * 
	 * @return	true wenn dem so ist, false otherwise
	 */
	public boolean isTargetAction() {
		return (TargetObject.actionTypeFromString(this.action) != -1);
	}

	/**
	 * Gibt die action Type Nummer des aktuellen Befehls zurück 
	 * 
	 * @return die ActionType nummer
	 */
	public int getTargetNumber() {
		return TargetObject.actionTypeFromString(this.action);
	}
}
