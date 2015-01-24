/*
 * 	CVS: $Id: Event.java,v 1.6 2004/07/19 15:52:35 marcus Exp $
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

/**
 * Diese Klasse definiert die Eventtypen in JZuul
 * 
 * 
 * @version $Revision: 1.6 $
 */
public abstract class Event {
	/**
	 * Das Default Event, wird vom Engine ausgelöst.
	 * Falls das Engine Threaded NPCs benutzt wird das Event von
	 * dem Thread ausgelöst, ansonsten nach jeder Runde.
	 */
	public static final int DEFAULT = 0;
	
	/**
	 * Das Drop Event.
	 * Wird von dem Befehl "drop" ausgelöst.
	 */
	public static final int DROP = 1;
	
	/**
	 * Das Takeup Event.
	 * Wird von dem Befehl "take" ausgelöst.
	 */
	public static final int TAKEUP = 2;
	
	/**
	 * Das Playerenter Event.
	 * Wird von einem Room Objekt ausgelöst wenn der Spieler
	 * mit "go" in diesen Raum wechselt.
	 */
	public static final int PLAYERENTER = 3;
	
	/**
	 * Das Playerleave Event.
	 * Wird von einem Room Objekt ausgelöst wenn der Spieler
	 * mit "go" den Raum verlässt.
	 */
	public static final int PLAYERLEAVE = 4;
	
	/**
	 * Das Use Event.
	 * Wird von dem Befehl "use" ausgelöst.
	 */
	public static final int USE = 5;

	/**
	 * Ein Dialog Event.
	 * Wird von dem Dialogsystem aufgerufen wenn ein
	 * Dialog erfolgreich beendet wurde.
	 */
	public final static int DIALOG_END_SUCCESS = 6;
	
	/**
	 * Ein Dialog Event.
	 * Wird von dem Dialogsystem ausgelöst wenn ein
	 * Dialog abgebrochen wird
	 */
	public final static int DIALOG_END_FAILURE = 7;
	
	/**
	 * Ein Dialog Event.
	 * Wird von dem Dialogsystem ausgelöst wenn ein
	 * Satz zur nächsten Phase des Dialoges führt.
	 */
	public final static int DIALOG_CONTINUE = 8;
	
	/**
	 * Ein Dialog Event.
	 * Wird vom Dialogsystem ausgelöst wenn das ausgewählte
	 * DialogObject zu einer aktion des Character Objekt führen soll.
	 */
	public final static int DIALOG_NPC_GIVE = 9;
	
	/**
	 * Ein Dialog Event.
	 * 
	 * Wird vom Dialogsystem ausgelöst wenn das ausgewählte
	 * DialogObject dazu führen soll, das der Spieler dem Character
	 * Objekt etwas übergibt.
	 */
	public final static int DIALOG_NPC_TAKE = 10;

	/**
	 * Ein Dialog Event.
	 * 
	 * Kann in den XML Definitionen für eigene Aktionen definiert werden
	 */
	public final static int DIALOG_CUSTOM_RESULT_1 = 11;

	/**
	 * Ein Dialog Event.
	 * 
	 * Kann in den XML Definitionen für eigene Aktionen definiert werden
	 */
	public final static int DIALOG_CUSTOM_RESULT_2 = 12;

	/**
	 * Ein Dialog Event.
	 * 
	 * Kann in den XML Definitionen für eigene Aktionen definiert werden
	 */
	public final static int DIALOG_CUSTOM_RESULT_3 = 13;

	/**
	 * Das Use_Success Event.
	 * Wird ausgelöst wenn ein "use" oder "use with" Befehl zu einem
	 * positiven ergebnis geführt hat.
	 */
	public static final int USE_SUCCESS = 14;
	
	/**
	 * Das Use_failure Event.
	 * Wird ausgelöst wenn ein "use" oder "use with" Befehlt fehlgeschlagen
	 * ist.
	 */
	public static final int USE_FAILURE = 15;
	
	/**
	 * Das Timer event.
	 * Wird von dem Timer alle 3 Minuten ausgelöst.
	 */
	public static final int TIMER = 16;
	
	public static final int DIALOG_ERROR = 17;
	/**
	 * Die Anzahl der Events
	 */
	public static final int COUNT=18;
	
	
	
	/**
	 * Wandelt einen Event Namen in die Event Nummer um
	 * 
	 * @param event	der Name des events
	 * @return	die Nummer des Events, -1 bei unbekanntem Eventname.
	 */
	public static int fromString(String event) {
		if (event == null) throw new IllegalArgumentException("You can not get an id from a null value!");
		if (event.equals("default")) return Event.DEFAULT;
		if (event.equals("drop")) return Event.DROP;
		if (event.equals("takeup")) return Event.TAKEUP;
		if (event.equals("playerenter")) return Event.PLAYERENTER;
		if (event.equals("playerleave")) return Event.PLAYERLEAVE;
		if (event.equals("use")) return Event.USE;
		
		if (event.equals("dialog_end_failure")) return Event.DIALOG_END_FAILURE;
		if (event.equals("dialog_end_success")) return Event.DIALOG_END_SUCCESS;
		if (event.equals("dialog_continue")) return Event.DIALOG_CONTINUE;
		if (event.equals("dialog_npc_give")) return Event.DIALOG_NPC_GIVE;
		if (event.equals("dialog_npc_take")) return Event.DIALOG_NPC_TAKE;
		if (event.equals("dialog_custom_result_1")) return Event.DIALOG_CUSTOM_RESULT_1;
		if (event.equals("dialog_custom_result_2")) return Event.DIALOG_CUSTOM_RESULT_2;
		if (event.equals("dialog_custom_result_3")) return Event.DIALOG_CUSTOM_RESULT_3;
		if (event.equals("dialog_error")) return Event.DIALOG_ERROR;
		
		if (event.equals("use_success")) return Event.USE_SUCCESS;
		if (event.equals("use_failure")) return Event.USE_FAILURE;
		
		if (event.equals("timer")) return Event.TIMER;
		Engine.debug("Unimplemented event " + event + " fromString called, did you add it?",1);
		return -1;
	}
}
