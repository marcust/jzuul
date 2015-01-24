/*
 * 	CVS: $Id: Command.java,v 1.1 2004/07/25 18:08:13 marcus Exp $
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

import org.jzuul.engine.Engine;
import org.jzuul.engine.Player;
import org.jzuul.engine.rooms.*;

/**
  * Die Klasse Command dient als Oberklase für alle Befehle, die verarbeitet werden können.
  * 
  * Wenn ein neuer Befehl abgeleitet wird, müssen die Attribute name, arguments und desc gesetzt werden und
  * die Methode action überschrieben werden. 
  * 
  *  
  * @version $Revision: 1.1 $
  */
public abstract class Command {
	/**
	 * Normalerweise valiediert doAction die Anzahl der Argumente die in arguments definiert wurde
	 * wenn man arguments auf VARARG_COMMAND setzt, ist eine variable Anzahl von Argumenten möglich.
	 * 
	 * Ein Beispiel ist {@link org.jzuul.engine.commands.Use}
	 * 
	 */
	public final static int VARARG_COMMAND = -1; 
	
	/**
	 * Die Anzahl der Argumente zu diesem Befehl. 
	 * Default ist 0.
	 */
	protected int arguments = 0;
	
	/**
	 * Der Name des Befehls, sollte mit dem Klassennamen übereinstimmen
	 */
	protected String name;
	
	/**
	 * Enthällt die Argument, die dem Befehl übergeben wurden
	 */
	protected String[] args;
	
	/**
	 * Enthält das aktulle Playerobjekt
	 */
	protected Player player;
	
	/**
	 * Enthält den aktuellen Room des Players
	 */
	protected Room currentRoom;
	
	/**
	 * Die Beschreibung des Befhels, wird von help() verwendet.
	 */
	protected String desc;
	
	/**
	 * Legt fest ob es sich um eine GameAction, also eine Interaktion im Spiel (give,go) oder
	 * eine off Game action handelt (help,save aber auch inv oder look). Nach gameActions werden NPC Aktionen ausgeführt, 
	 * nach off Game Actions nicht.
	 */
	protected boolean gameAction;
	
	/**
	 * Legt fest ob dieser Befehl auch in der Applet Sandbox ausgeführt werden kann.
	 */
	protected boolean isAppletSave;
	
	/**
	 * Sollte von einem Befehl der in der Form "befehl <objekt>" ist auf das eigentlich Objekt der Aktion gesetzt
	 * werden. Wird von dem Event System genutzt um dem Objekt das Event der Aktion mitzuteilen (z.B. Event.USE)
	 */
	protected String objectName = null;
	
	/**
	 * Konstruktor
	 * Default Werte:
	 * 	gameAction = true
	 * 	isAppletSave = true
	 */
	public Command() {
		this.gameAction = true;
		isAppletSave = true;
	}

	/**
	 * Zugriff auf die Anzahl der Argumente
	 * 
	 * @return die Anzahl der Argumente zu diesem Befehl
	 */
	public int getNumArguments() {
			return arguments;
	}
	
	/**
	 * Wird vom {@link org.jzuul.engine.Engine} aufgerufen, nachdem der Befehl identifiziert wurde. 
	 * 
	 * Übergabeparameter wie der Spieler und die Anzahl der Argumente werden validiert. 
	 * 
	 * @param player	Der aktuelle Spieler
	 * @param args	Die auf der Kommandozeile angegebenen 
	 * @return	true wenn der Befehl erfolgreich ausgeführt wurde, false otherwise
	 */
	public final boolean doAction(Player player, String[] args) {
	    Object[] nameArray = { this.name };
		if ( ( (args == null) && (arguments > 0) ) ||
			( (args != null ) && (args.length != arguments) && (arguments != VARARG_COMMAND) ) ) {
			Engine.gui.println(MessageFormat.format(Messages.getString("COMMAND_ERROR_PARAMS"), nameArray)); //$NON-NLS-1$ //$NON-NLS-2$
			this.help();
			return false;
		}
		if (player == null ) {
			Engine.gui.println(MessageFormat.format(Messages.getString("COMMAND_ERROR_PLAYER"), nameArray)); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
		this.currentRoom = player.getCurrentRoom();
		this.player = player;
		this.args = args;

		return this.action();
	}
	
	/**
	 * Diese Methode muss von jedem Befehl überschrieben werden und sollte
	 * die eigentlich Aktion implementieren
	 * 
	 * @return true wenn der Befehl erfolgreich ausgeführt wurde
	 */
	abstract protected boolean action();

	/**
	 * Der help handler, der von dem Befehl help aufgerufen wird und bei einem 
	 * Aufruf mit falschen Parametern.
	 * Kann überschrieben werden um eigen Hilfetexte auszugeben. Defaults to
	 * "Mit diesem Befehl" + this.desc
	 *
	 */
	public void help() {
	    Object[] nameArray = { this.name };
		if (this.desc != null) Engine.gui.println(Messages.getString("COMMAND_HELP_START") + this.desc); //$NON-NLS-1$
		if (this.arguments == 0) {
		Engine.gui.println(MessageFormat.format(Messages.getString("COMMAND_ERROR_ARGS"), nameArray)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Zugriff auf die Eigenschaft gameAction. Wird von dem Engine aufgerufen um festzustellen ob
	 * nach dem Ausführen NPC Aktionen durchgeführt werden sollen oder nicht.
	 * 
	 * @return	true if it is a gameAction, false otherwise
	 */
	public boolean isGameAction() {
		return this.gameAction;
	}
	
	/**
	 * Gibt den Namen des Objektes (im sprachlichen Sinne) des Befehls zurück
	 * 
	 * @return	ein GameObject Name
	 */
	public String getObjectName() {
		return this.objectName;
	}
	
	/**
	 * Ist der Behl in der Applet Sandbox ausführbar?
	 * 
	 * @return true wenns so ist, false otherwise
	 */
	public boolean isAppletSave() {
		return this.isAppletSave;
	}
	
	/**
	 * Gibt den Name, also den eigentlichen Befehl des Commands zurück
	 * 
	 * @return	der Name des Befehls
	 */
	public String getName() {
		return this.name;
	}
	
}
