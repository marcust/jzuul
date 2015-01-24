/*
 * 	CVS: $Id: TransitionRoom.java,v 1.12 2004/07/25 21:40:56 marcus Exp $
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

package org.jzuul.engine.rooms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jzuul.engine.Engine;
import org.jzuul.engine.Event;
import org.jzuul.engine.GameFileReader;
import org.jzuul.engine.Messages;
import org.jzuul.engine.exceptions.ConnectAllRoomsFailed;
import org.jzuul.engine.exceptions.NoSuchRoomException;
import org.jzuul.engine.gui.GuiInterface;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Ein Übergangsraum
 * 
 * Übergangsräumer Überprüfen den Spieler auf eine Liste von Vorbedingungen 
 * und laden bei Erfolg eine neue Karte in den sie den Spieler postionieren.
 * 
 * 
 * @version $Revision: 1.12 $
 */
public class TransitionRoom extends Room {
	/**
	 * Eine Liste von Vorbedingungen also die Namen von GameObjects die der Spieler
	 * in seinem Inventar haben muss. 
	 */
	String[] preconditions;
	
	/**
	 * Ist das das Ende des Spieles?
	 */
	boolean isFinal = false;
	
	/**
	 * Der Name der Karte die das Ziel dieses Übergangsraumes ist
	 */
	String targetMap;

	/**
	 * Erstellt einen neuen Übergangsraum
	 * 
	 * @param name	der Name dieses Raumes
	 * @param preconditions	die Vorbedingungen für den Übergang
	 * @param isFinal	ist dies das Ende des Spieles
	 * @param targetMap	die Karte zu der der Spieler übergehen soll
	 */
	public TransitionRoom(String name, String[] preconditions, boolean isFinal, String targetMap) {
		super(name);
		this.preconditions = preconditions;
		this.isFinal = isFinal;
		this.targetMap = targetMap;
	}
	
	/**
	 * Wandelt diesen Raum in ein JDOM XML Element um
	 * 
	 * @return das JDOM XML Element das diesen Raum beschreibt
	 */
	public Element toElement() {
		Element trE = new Element("transitionroom"); //$NON-NLS-1$
		trE.setAttribute("name", getName()); //$NON-NLS-1$
		trE.setAttribute("final", String.valueOf(isFinal)); //$NON-NLS-1$
		trE.setAttribute("target", targetMap); //$NON-NLS-1$

		for (int i = 0; i < this.preconditions.length; i++) {
			Element preconE = new Element("precondition"); //$NON-NLS-1$
			preconE.setAttribute("item", preconditions[i]); //$NON-NLS-1$
			trE.addContent(preconE);
		}
		trE.addContent(super.waysToElement());
		return trE;
	}

	/**
	 * Der defaultHandler für Events.
	 * 
	 * Dieser Handler führt den eigentlichen Übergang aus, d.h. er überprüft die Vorbedingungen,
	 * lädt die Karte und positioniert den Spieler. Er wird von Room.doEvent() aufgerufen.
	 * 
	 * @see org.jzuul.engine.Event
	 * @see org.jzuul.engine.EventHandler
	 * @param eventId eine EventId 
	 */
	public void defaultHandler(int eventId) {
		if (eventId == Event.PLAYERENTER) {
			Engine.debug(this.getName() + " onEnterCalled, target is " + this.targetMap, 1); //$NON-NLS-1$
			if (Engine.player.getInv().contains(this.preconditions)) {
				if (!this.isFinal) {
					InputStream map2 = Engine.getFileStream(Engine.gamefile);
					try {
						GameFileReader reader = new GameFileReader(map2, !Engine.gui.isApplet());
						try {
							Engine.map = reader.getMap(this.targetMap);
						} catch (ConnectAllRoomsFailed e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchRoomException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Engine.objectPool = reader.getObjectPool();
						Engine.player.setCurrentRoom(Engine.map.getRoom(Engine.map.getStartRoom()));
						// OK, in the usual case we just unreferenced about 50 Objects
						System.gc();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JDOMException e) {
						e.printStackTrace();
					}
				} else {
					Engine.gui.println(Messages.getString("TRANSITION_ROOM_GAME_END"), GuiInterface.GREEN); //$NON-NLS-1$
					Engine.gui.disableInput();
				}

			} else {
				Engine.player.say(Messages.getString("TRANSITION_ROOM_NOT_REACHABLE")); //$NON-NLS-1$
				Engine.player.setCurrentRoom(Engine.player.getLastRoom());
			}
		}
	}

}
