/*
 * 	CVS: $Id: Dialog.java,v 1.8 2004/07/25 19:07:20 marcus Exp $
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Dialog bildet einen Dialog mit einem Character Objekt ab
 * 
 * Ein Dialog ist in Phasen aufgeteilt, wobei für jede Phase eine Anzahl von Aussagen
 * des Players vorhanden sein muss und eine Anzahl von Antworten auf die Aussage.
 * über die Methode talk wird ein Dialog initiert.
 * 
 * 
 * @version $Revision: 1.8 $
  */
public class Dialog {
	/**
	 * Ein ActionListener der Benutzt wird um die Auswahl der Dialoge 
	 * festzustellen und an den Charakter zu übergeben.
	 */
	private class AuswahlListener implements ActionListener {
	    /**
	     * Die Methode die von dem Gui aufgerufen wird um
	     * die Auswahl zu übergeben.
	     * 
	     * @param e ein AWT ActionEvent
	     */
		public void actionPerformed(ActionEvent e) {
			Vector v = getPhaseVector(currentPhase);
			DialogObject retval = null;
			Engine.gui.resetInput();
			try {
				int auswahl = Integer.parseInt(e.getActionCommand());
				auswahl--;
				if ((auswahl >= v.size()) || (auswahl < 0)) {
					Engine.gui.println((auswahl + 1) + Messages.getString("DIALOG_NO_VALID_NUMBER")); //$NON-NLS-1$
				} else {
					retval = (DialogObject) v.get(auswahl);
				}
			} catch (NumberFormatException ex) {
				Engine.gui.println(Messages.getString("DIALOG_HAVE_TO_ENTER_NUMBER")); //$NON-NLS-1$
			}
					if (retval != null) {
				talk(retval);
			}
		}
	}

	/**
	 * Die Verschiedenen Dialogphasen. Der Vector enthällt pro Phase wiederum einen Vector von
	 * DialogObjects
	 */
	Vector phases;
	
	/**
	 * Dieser Vector enthällt alle DialogObjects unter ihrere Id
	 */
	Vector dialogs;
	
	/**
	 * Die aktuelle Phase des Dialoges (default ist 1)
	 */
	int currentPhase;
	
	/**
	 * Das Character Objekt der diesen Dialog füht
	 */
	Character character;
	
	/**
	 * Eine Liste von GameObject Namen die als Vorbedingungen 
	 * für diesen Dialog gelten
	 */
	List preconditions;

	/**
	 * Konstruktor
	 *
	 */
	public Dialog() {
		this.phases = new Vector(10);
		this.dialogs = new Vector(10);
		this.preconditions = new Vector();
		currentPhase = 1;
	}

	/**
	 * Fügt ein neues DialogObject in den Dialog ein
	 * 
	 * @param phase	Die Dialogphase für diesen Dialog
	 * @param id			Eine eindeutige ID für das DialogObject
	 * @param playerSentence	Die Aussage des Spielers
	 * @param npcAnswer			Die Antwort des Characters
	 * @param type						den DialogObject type
	 * @param nextPhase			Die nächste Phase, falls type DialogObject.DIALOG_CONTINUE ist, 0 sonst.
	 * @see org.jzuul.engine.DialogObject
	 */
	public void addDialog(int phase, int id, String playerSentence, String npcAnswer, int type, int nextPhase) {
		DialogObject dialog = new DialogObject(phase, id, playerSentence, npcAnswer, nextPhase, type);
		this.addToPhase(phase, dialog);
		if (id > dialogs.size()) {
			dialogs.setSize(id);
		}
		this.dialogs.add(id, dialog);
	}

	/**
	 * Fügt ein existierendes DialogObject in eine andere Phase
	 * 
	 * @param phase	Die Dialogphase dem das DialogObject hinzugefügt werden soll
	 * @param id			Die id des existierenden DialogObjects
	 */
	public void addDialog(int phase, int id) {
		this.addToPhase(phase, (DialogObject) dialogs.get(id));
	}

	/**
	 * Fügt ein DialogObject in die Phase phase ein
	 * 
	 * @param phase	Die Dialogphase für diesen Dialog
	 * @param dialog	Das DialogObject
	 */
	protected void addToPhase(int phase, DialogObject dialog) {
		this.getPhaseVector(phase).add(dialog);
	}

	/**
	 * Zugriff auf alle DialogObject Objekte einer Phase
	 * 
	 * @param phase	Die Phase aus der die Objekte geholte werden sollen
	 * @return	Einen Vector aus den DialogObject Objekten einer Phase
	 */
	protected Vector getPhaseVector(int phase) {
		if (this.phases.size() < phase) {
			this.phases.setSize(phase+1);
		}

		if (this.phases.get(phase) != null) {
			return (Vector) this.phases.get(phase);
		} else {
			Vector newVec = new Vector();
			this.phases.add(phase, newVec);
			return newVec;
		}
	}

	/**
	 * Die Hauptmethode, wird von dem Befehlt "talk" aufgerufen.
	 * Sie setzt dem Engine.gui eine AuswahlListener als neuen
	 * ActionListener und gibt die Auswahl möglicher Sätze aus. 
	 * 
	 * @return	bei nicht kritischen fehlern true
	 */
	public boolean talk() {
	    if (this.dialogs.size() > 0 &&  this.phases.size() > 0 ) {
	        this.printAuswahl();
			Engine.gui.setActionListener(new AuswahlListener());
			return true;
	    } else {
	        return false;
	    }
	}

	/**
	 * Diese Methode wird von dem AuswahlListener Objekt aufgerufen
	 * nachdem der Spieler seine Auswahl getätigt hat.
	 * 
	 * @param selected	das zu dem ausgewählten Satz gehörende DialogObject
	 */
	public void talk(DialogObject selected) {
		if (selected == null)
			throw new IllegalArgumentException("Character.talk called with selected==null"); //$NON-NLS-1$

		Engine.player.say(selected.getPlayerSentence());
		character.say(selected.getNpcAnswer());
		if (selected.getType() == Event.DIALOG_CONTINUE) {
			this.currentPhase = selected.getNextPhase();
		}
		this.character.pushAnswer(selected.getType());
	}

	/**
	 * Gibt die Auswahl der möglichen PlayerSentences einer Phase aus.
	 */
	public void printAuswahl() {
		Vector v = getPhaseVector(this.currentPhase);
		int index = 1;
		Engine.gui.println("-----------------------------------------------"); //$NON-NLS-1$
		for (Iterator i = v.iterator(); i.hasNext();) {
			DialogObject dialog = (DialogObject) i.next();
			Engine.gui.println(index + ") " + dialog.getPlayerSentence()); //$NON-NLS-1$
			index++;
		}
		Engine.gui.println("-----------------------------------------------"); //$NON-NLS-1$
	}

	/**
	 * Fügt einen GameObject Namen als Vorbedingung hinzu
	 * 
	 * @param objName	der Name eines GameObjects das Vorbedingung zu diesem Dialog ist
	 */
	public void addPrecondition(String objName) {
		this.preconditions.add(objName);
	}
	
	/**
	 * Die Liste der Vorbedingungen
	 * 
	 * @return	List of Strings (die Namen der GameObjects)
	 */
	public List getPreconditions() {
		return this.preconditions;
	}
	
	/**
	 * Setzt das Character Objekt das diesen Dialog führt
	 * 
	 * @param character	das Character Objekt
	 */
	public void setCharacter(Character character) {
		this.character = character;
	}
	
	/**
	 * Setzt die Dialogphase auf 1 zurück
	 */
	public void resetPhase() {
		this.currentPhase = 1;
	}

}
