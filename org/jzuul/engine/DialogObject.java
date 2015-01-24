/*
 * 	CVS: $Id: DialogObject.java,v 1.4 2004/07/16 16:22:33 marcus Exp $
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
 * Ein DialogObject ist ein Element eines gesamten Dialoges
 * 
 * 
 * @version $Revision: 1.4 $
  */
public class DialogObject {
	/**
	 * Der Satz den der Spieler zur Auswahl bekommt
	 */
	protected String playerSentence;
	/**
	 * Die Antwort die ein Character Objekt zu der Auswahl des Spielers gibt
	 */
	protected String npcAnswer;
	/**
	 * Die Phase zu der dieses DialogObject Objekt gehört
	 */
	int phase;
	/**
	 * Der Typ den dieses DialogObject als Rückgabewert liefert
	 */
	int type;
	/**
	 * Ein Verweis auf die nächste Dialogphase, default ist 0 
	 */
	int nextPhase;
	/**
	 * Die eindeutige Id dieses DialogObject Objekt
	 */
	int id;

	/**
	 * Erstellt ein neues DialogObject Objekt
	 * 
	 * @param phase	die Phase zu der dieses DialogObject gehört
	 * @param id			die eindeutige Id des DialogObject Objektes
	 * @param playerSentence	die Aussage die der Spieler zur Auswahl hat
	 * @param npcAnswer				die Antwort die ein Character Objekt darauf gibt
	 * @param nextPhase				die nächste Phase für einen Success, sonst 0
	 * @param type							der Typ dieses DialogObject Objektes, wird als Event ausgelöst
	 */
	public DialogObject(int phase, int id, String playerSentence, String npcAnswer, int nextPhase, int type) {
		this.phase = phase;
		this.playerSentence = playerSentence;
		this.npcAnswer = npcAnswer;
		this.type = type;
		this.nextPhase = nextPhase;
		this.id = id;
	}

	/**
	 * Zugriff auf die ID des Objektes
	 * 
	 * @return	Die ID des Objektes
	 */
	public int getId() {
		return id;
	}

	/**
	 * Zugriff auf den Antwortsatz eines Charakters
	 * 
	 * @return	Die Antwort des Charactes auf einen Satz des Spielers
	 */
	public String getNpcAnswer() {
		return npcAnswer;
	}

	/**
	 * Zugriff auf die Phase dieses DialogObject Objektes
	 * 
	 * @return	Die Phase zu der dieses DialogObject gehört (nur die initiale, aliase werden nicht berücksichtigt)
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * Zugriff auf die Aussage des Spielers
	 * 
	 * @return	Die Aussage des Spielers
	 */
	public String getPlayerSentence() {
		return playerSentence;
	}

	/**
	 * Zugriff auf den Typen des Objektes. Wird als Event ausgelöst.
	 * 
	 * @return	Den Typen des Objektes
	 * @see		org.jzuul.engine.Event
	 */
	public int getType() {
		return type;
	}

	/**
	 * Die Phase auf die dieses DialogObject verweist.
	 * 
	 * @return Die darauffolgende Phase
	 */
	public int getNextPhase() {
		return this.nextPhase;
	}
}
