/*
 * 	CVS: $Id: Player.java,v 1.14 2004/07/25 19:07:20 marcus Exp $
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

import java.text.MessageFormat;

import org.jdom.Element;
import org.jzuul.engine.exceptions.NoSuchEventException;
import org.jzuul.engine.gui.GuiInterface;
import org.jzuul.engine.rooms.*;

/**
 * Die Playerklasse, die den Container für den Spieler darstellt. Ein Spieler speichert nicht nur seine gegenwärtige Position
 * sondern auch alle Dinge die aufgehoben worden sind.
 * 
 * 
 * @version $Revision: 1.14 $
 */
public class Player implements EventListener {
	/**
	 * Der Raum in dem sich der Spieler aufhält
	 */
	private Room currentRoom;
	
	/**
	 * Das Inventory Objekt des Spielers
	 */
	private Inventory inv;
	
	/**
	 * Der Raum in dem sich der Spieler vorher aufgehalten hat (für "go back")
	 */
	private Room lastRoom;
	
	/**
	 * Der Name des Spielers, default ist Player
	 */
	private String name = Messages.getString("PLAYER_DEFAULT_NAME"); //$NON-NLS-1$
	
	/**
	 * Die TargetList des Spielers (für "todo")
	 */
	private TargetList targets;
	
	/**
	 * Die EventHandler des Spielers
	 */
	private EventHandler[] eventHandlers = new EventHandler[Event.COUNT];
	
	/**
	 * Die Nummer des Spielers für Multiplayer, default ist 0
	 */
	private int number = 0;

	/**
	 * Konstruktor
	 */
	public Player() {
		inv = new Inventory(10);
		targets = new TargetList();
	}
	
	/**
	 * Erstellt einen Spieler mit einer Nummer (multiplayer)
	 * 
	 * @param number	die Nummer des Spielers
	 */
	public Player(int number) {
		this();
		this.number = number;
	}
	
	public boolean findAndDeleteGameObject(String itemName) {
	    return findAndDeleteGameObject(itemName,true);
	}

	/**
	 * Diese Methode sucht das Item itemName in der Reihenfolge Inventar, Room und löscht dieses.
	 * 
	 * @param itemName
	 * @return true on success and false on failure
	 */
	public boolean findAndDeleteGameObject(String itemName,boolean verbose) {
		Engine.debug("Find and delete game Object:" + itemName,1); //$NON-NLS-1$
		if (this.getInv().containsGameObject(itemName)) {
			return this.getInv().deleteGameObject(itemName);
		}
		else
			if (currentRoom.getContent().containsGameObject(itemName)) {
				return currentRoom.getContent().deleteGameObject(itemName);
			}
			else {
			    if (verbose) {
			        Object[] formatArgs = { itemName };
			        this.say(MessageFormat.format(Messages.getString("PLAYER_FIND_FAILED"),formatArgs)); //$NON-NLS-1$
			    }
				return false;
			}
	}
	
	/**
	 * Diese Methode sucht das GameObject objName in der Reihenfolge Inventar, Room und gibt  das GameObject
	 * zurück. Wenn das GameObject nicht gefunden wird gibt es null zurück 
	 * 
	 * @param objName
	 * @return GameObject with objName on success, null on failure
	 */
	public GameObject findGameObject(String objName) {
		if (this.getInv().containsGameObject(objName)) {
			return this.getInv().getGameObject(objName);
		}
		if (currentRoom.getContent().containsGameObject(objName)) {
			return currentRoom.getContent().getGameObject(objName);
		}
		return null;
	}

	/**
	 * Gibt den aktuellen Room zurück
	 * @return Room in dem sich der Spieler aufhält
	 */
	public Room getCurrentRoom() {
		return currentRoom;
	}

	/**
	 * Gibt das Inventar des Spielers zurück
	 * @return Inventar
	 */
	public Inventory getInv() {
		return inv;
	}
	
	/**
	 * Setzt das Inventar eines Spielers
	 * 
	 * @param inv Das neuen Inventar des Spielers
	 */
	public void setInv(Inventory inv) {
			this.inv=inv;
	}

	/**
	 * Diese Methode verwandelt einen Gegenstand in einen anderen. Da Gegenstände
	 * über ihre Namen identifiziert werden muss man einen neuen Namen einsetzen.  
	 * 
	 * @param item
	 * @param newName
	 * @return true on success and false on failure
	 * @deprecated	Wird im Moment nicht mehr benutzt
	 */
	public boolean mutateItem(Item item, String newName) {
		this.findAndDeleteGameObject(item.getName());
		item.setName(newName);
		this.getInv().addGameObject(item);
		return true;
	}

	/**
	 * Ausgabe...
	 * @param something 	was ausgegeben werden soll
	 */
	public void say(String something) {
		Engine.gui.println(name + ": " + something, GuiInterface.RED); //$NON-NLS-1$
	}

	/**
	 * Setzt den Room in dem sich der Player befindet und löst die Events
	 * Event.PLAYERLEAVE und Event.PLAYERENTER aus
	 * @param currentRoom - tja... 
	 */
	public void setCurrentRoom(Room currentRoom) {
		// first time ?
		if (this.currentRoom == null) {
			this.currentRoom = currentRoom;
			return;
		}
		this.currentRoom.doEvent(Event.PLAYERLEAVE);
		this.saveLastRoom();
		this.currentRoom = currentRoom;
		currentRoom.doEvent(Event.PLAYERENTER);
	}

	/**
	 * Macht ein Backup des aktuellen Raumes
	 */
	public void saveLastRoom() {
		this.lastRoom = currentRoom;
	}
	
	/**
	 * Setzt den letzten Room auf null zurück
	 */
	public void resetLastRoom() {
		this.lastRoom = null;
	}
	
	
	/**
	 * Zugriff auf das das Backup das mit saveLastRoom genacht wurde
	 * 
	 * @return der Room in dem man vorher war
	 */
	public Room getLastRoom() {
		return this.lastRoom;
	}

	/**
	 * Wandelt den Spieler und sein Inventar in ein JDOM Element um
	 * 
	 * @return	das JDOM XML ELement das den Spieler beschreibt
	 */
	public Element toElement() {
		Element playerE = new Element("player"); //$NON-NLS-1$
		playerE.setAttribute("position", currentRoom.getName()); //$NON-NLS-1$
		playerE.setAttribute("map", Engine.map.getName()); //$NON-NLS-1$
		playerE.setAttribute("name", getName()); //$NON-NLS-1$
		
		playerE.addContent(inv.toElement());
		playerE.addContent(targets.toElementList());
		return playerE;
	}

	/**
	 * Fügt in das Inventar des Spielers eine Objekt aus dem globalen ObjectPool ein
	 * 
	 * @param objName	der Name des Objektes das geholt werden soll
	 */
	public void getFromPool(String objName) {
		GameObject obj =  (GameObject)Engine.objectPool.get(objName);
		this.inv.addGameObject(obj.copy());
	}
	
	/**
	 * Setzt den Namen des Spielers
	 * 
	 * @param name	der neuen Name des Spielers
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Zugriff auf den Namen des Spielers
	 * 
	 * @return	der aktuelle Name des Spielers
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Weist dem Spieler eine neue TargetList zu
	 * 
	 * @param targets	die neue TargetList für den Spieler
	 */
	public void setTargetList(TargetList targets) {
		this.targets = targets;
	}

	/**
	 * Zugriff auf die TargetList des Spielers
	 * 
	 * @return	die aktuelle TargetList des Spielers
	 */
	public TargetList getTargetList() {
			return this.targets;
	}
	
	/**
	 * Setzt einen EventHandler für ein Event
	 * 
	 * @param name	der Name des Events
	 * @param handler 	der dazugehörige EventHandler
	 * @see org.jzuul.engine.Event
	 * @see org.jzuul.engine.EventHandler
	 */
	public void setHandler(String name, EventHandler handler) {
		if (name == null || handler == null) throw new IllegalArgumentException("You can not set a handler with a null value"); //$NON-NLS-1$
		int id = Event.fromString(name);
		if (id > -1) {
			this.eventHandlers[id] = handler;
		} else {
			  throw new NoSuchEventException("The event " + name + " is unknown"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Führt ein Event aus
	 * 
	 * @param eventId 	die Id des Events
	 * @see org.jzuul.engine.Event
	 * @see org.jzuul.engine.EventHandler
	 */
	public void doEvent(int eventId) {
		if (eventId >= Event.COUNT || eventId < 0) throw new IllegalArgumentException("Id is not valid: " + eventId); //$NON-NLS-1$
		if (eventHandlers[eventId] != null) {
			eventHandlers[eventId].execute();
		}
	}
	
	/**
	 * Gibt die Nummer des Spielers zurück
	 * 
	 * @return	die Nummer des spielers
	 */
	public int getNumber() {
		return this.number;
	}
	
	/**
	 * Setzt die Nummer des Spielers
	 * 
	 * @param number	die neue Nummer für den Spieler
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
	/**
	 * Erstellt eine 1 zu 1 Kopie des Spielers inklusive aller enthaltenen Objekte.
	 * 
	 * @return	eine unabhängige Kopie des Spielers
	 */
	public Player copy() {
		Player newPlayer = new Player();
		newPlayer.setCurrentRoom(this.getCurrentRoom());
		newPlayer.setName(this.getName());
		newPlayer.setInv(this.inv.copy());
		newPlayer.eventHandlers = this.eventHandlers;
		newPlayer.setTargetList(this.targets.copy());
		newPlayer.setNumber(this.getNumber());
		return newPlayer;
		
	}
	
	
}
