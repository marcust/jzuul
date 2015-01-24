/*
 * 	CVS: $Id: BeamRoom.java,v 1.5 2004/07/25 19:07:20 marcus Exp $
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

package org.jzuul.engine.rooms;

import org.jzuul.engine.Engine;
import org.jzuul.engine.Event;
import org.jzuul.engine.Messages;

/**
 * Ein BeamRoom ist ein Raum der den Player beim betreten in einen
 * zufälligen anderen Raum befördert
 * 
 * 
 * @version $Revision: 1.5 $
 */
public class BeamRoom extends Room {

	/**
	 * Erstellt ein neues BeamRoom Objekt
	 * 
	 * @param name				der Name des Raumes
	 * @param beschreibung	die Beschreibung des Raumes
	 */
	public BeamRoom(String name, String beschreibung) {
		super(name, beschreibung);
	}

	/**
	 * Der defaultHandler für BeamRoom Objekte.
	 * Das Event.PLAYERENTER wird benutzt um den Spieler in einen anderen Raum
	 * zu bewegen.
	 * 
	 * @param eventId	eine eventId
	 * @see org.jzuul.engine.Event
	 */
	protected void defaultHandler(int eventId) {
		if (eventId == Event.PLAYERENTER) {

			this.printBeschreibung();
			Engine.player.resetLastRoom();
			Room nextRoom = Engine.map.getRandomRoom();

			for (int i = 0; i < 5; i++) {
				Engine.gui.print("."); //$NON-NLS-1$
				Engine.delay(1000);
			}
			Engine.gui.println();
			Engine.gui.println(Messages.getString("BEAMROOM_MESSAGE")); //$NON-NLS-1$
			Engine.player.setCurrentRoom(nextRoom);
		}
	}

}
