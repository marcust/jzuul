/*
 * 	CVS: $Id: GameMap.java,v 1.12 2004/07/25 19:07:20 marcus Exp $
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

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.jdom.Element;
import org.jzuul.engine.exceptions.ConnectAllRoomsFailed;
import org.jzuul.engine.exceptions.NoSuchRoomException;
import org.jzuul.engine.rooms.*;

/**
 * Diese Klasse bildet die Karte (das Raumgeflecht in dem sich der Spieler bewegen kann)
 * in einem Spiel ab.
 * Da durch die Struktur der Spieldateien es zulässt, das Wege ohne Room Objekte angelegt werden
 * werden die Wege in einer Queue ähnlichen Struktur gehalten und erst zum schluss angelegt.
 * 
 * 
 * @version $Revision: 1.12 $
 */
public class GameMap {
	/**
	 * Diese Klasse implementiert einen in der Queue gehaltenen Weg.
	 * 
	 * 
	 */
	private class QueueItem {
		/**
		 * der Name des Raumes zu dem dieser Weg führt.
		 */
		public String ofName;
		/**
		 * die Richtung, ausgehend von dem Quellraum, in der der Raum liegt
		 */
		int direction;

		/**
		 * Erstellt einen neuen Weg für die Queue
		 * 
		 * @param direction	die Richtung in der dieser Raum liegt
		 * @param ofName	der Name des Raumes
		 */
		public QueueItem(int direction, String ofName) {
			this.direction = direction;
			this.ofName = ofName;
		}
	}

	/**
	 * Der interne speicher für die Karte, enthällt Raumname auf Room Objekt mappings.
	 */
	protected Map gameMap;
	/**
	 * Der Name des Startraumes für diese Karte
	 */
	protected String startRoom;
	/**
	 * Die Queue der Wege die noch angelegt werden müssen. Enthällt Ausgangsraum auf QueueItem
	 * Mappings.
	 */
	protected Map connectQueue;
	/**
	 * Die Liste der NPCs die sich in den Räumen aufhalten.
	 */
	protected List npcList;
	/**
	 * Der Name dieser Karte
	 */
	protected String name;

	/**
	 * Erstell ein neues GameMap Objekt
	 * 
	 * @param name	der Name dieser Karte
	 */
	public GameMap(String name) {
		this.name = name;
		gameMap = new HashMap();
		connectQueue = new HashMap();
		npcList = new Vector();
	}

	/**
	 * Fügt einen neuen Raum und einen Weg in die Karte ein.
	 * 
	 * @param name				der Name des neuen Raumes
	 * @param description		die Beschreibung des neuen Raumes
	 * @param direction			ein Richtung in der ein anderer Raum liegt
	 * @param ofRoom			der Name des Raumes der in dieser Richtung liegt
	 * @see	org.jzuul.engine.Directions
	 */
	public void addRoom(String name, String description, int direction, String ofRoom) {
		this.addRoom(name, description);
		this.enqueueWay(name, direction, ofRoom);
	}

	/**
	 * Fügt ein neues Room Objekt der Karte hinzu.
	 * Für diesen Raum müssen später noch Wege angelegt werden!
	 * 
	 * @param raum	ein Room Objekt das der Karte hinzugefügt werden soll.
	 */
	public void addRoom(Room raum) {
		gameMap.put(raum.getName(), raum);
	}

	/**
	 * Fügt einen Room Objekt und einen Weg der Karte hinzu
	 * 
	 * @param raum			das Room Objekt das hinzugefügt werden soll
	 * @param direction		die Richtung in der ein anderer Raum liegt
	 * @param ofRoom		der Name des Raumes der in dieser Richtung liegt
	 * @see org.jzuul.engine.Directions
	 */
	public void addRoom(Room raum, int direction, String ofRoom) {
		this.addRoom(raum);
		this.enqueueWay(raum.getName(), direction, ofRoom);
	}

	/**
	 * Fügt einen Raum der Karte hinzu.
	 * Für diesen Raum müssen später noch Wege angelegt werden!
	 * 
	 * @param name				der Name des neuen Raums
	 * @param description		die Beschreibung des neuen Raums
	 */
	public void addRoom(String name, String description) {
		addRoom(new Room(name, description));
	}

	/**
	 * Fügt einen neuen typisierten Raum zu der Karte hinzu.
	 * Für diesen Raum müssen später noch Wege angelegt werden!
	 * 
	 * @param name					der Name des neuen Raumes
	 * @param description			die Beschreibung des neuen Raumes
	 * @param typeOfRoom		der Typ (die Klasse) des neuen Raumes
	 * @see org.jzuul.engine.rooms
	 */
	public void addRoom(String name, String description, String typeOfRoom) {
		try {
			Class classDefinition = Class.forName(typeOfRoom);
			Class[] types = new Class[] { String.class, String.class };
			Constructor cons = classDefinition.getConstructor(types);
			Object[] args = new Object[] { name, description };

			gameMap.put(name, cons.newInstance(args));
		} catch (InstantiationException e) {
			System.out.println(e);
		} catch (IllegalAccessException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Zugriff auf einzelne Room Objekte
	 * 
	 * @param name	der Name eines Raumes
	 * @return das Room Objekt falls der Raum existiert, null sonst
	 */
	public Room getRoom(String name) {
		if (gameMap.containsKey(name)) {
			return (Room) gameMap.get(name);
		} else {
			return null;
		}
	}

	/**
	 * Überpüft ob schon ein Weg zwischen Raum 1 und Raum 2 in der Queue existiert.
	 * 
	 * @param room1	der Name eines Raumes
	 * @param room2	der Name eines Raumes
	 * @return true wenn es einen Weg in der Queue von room1 nach room2 gibt, false sonst.
	 */
	private boolean connectionExists(String room1, String room2) {
		if (connectQueue.containsKey(room1)) {
			Vector v = (Vector) connectQueue.get(room1);
			for (Iterator i = v.iterator(); i.hasNext();) {
				if (((QueueItem) i.next()).ofName.equals(room2)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Überprüft bidirektional ob schon ein Weg zwischen beiden Räumen in der Queue existiert.
	 * 
	 * @param room1	der Name eines Raumes
	 * @param room2	der Name eines Raumes
	 * @return	true wenn es einen Weg zwischen den beiden Räumen in der Queue gibt, false sonst
	 */
	private boolean wayExists(String room1, String room2) {
		return connectionExists(room1, room2) || connectionExists(room2, room1);
	}

	/**
	 * Fügt einen Weg in die Wegequeue ein falls dieser noch nicht existiert.
	 * 
	 * @param name					der Name des Ausgangsraumes
	 * @param direction				die Richtung in der der Weg liegt
	 * @param ofRoomName	der Name des Zielraumes.
	 */
	public void enqueueWay(String name, int direction, String ofRoomName) {
		if (wayExists(name, ofRoomName)) {
			return;
		}

		if (connectQueue.containsKey(name)) {
			((Vector) connectQueue.get(name)).add(new QueueItem(direction, ofRoomName));
		} else {
			Vector v = new Vector();
			v.add(new QueueItem(direction, ofRoomName));
			connectQueue.put(name, v);
		}
	}

	/**
	 * Erstellt alle Verbindungen für den gegebenen Raum.
	 * Die Räume für die Verbindungen erstellt wurden werden aus der Liste gelöscht.
	 * 
	 * @param name	der Name des Raumes bei dem begonnen werden soll
	 * @param seen	die Liste der noch nicht besuchten Räume
	 */
	protected void processQueue(String name, List seen) {
		if (connectQueue.containsKey(name)) {
			Vector queueItems = (Vector) connectQueue.get(name);
			if (seen.contains(name)) {
				seen.remove(name);
				for (Iterator i = queueItems.iterator(); i.hasNext();) {
					QueueItem qi = (QueueItem) i.next();
					connect(name, qi.direction, qi.ofName);
					processQueue(qi.ofName, seen);
				}
			}
			connectQueue.remove(name);
		}
	}

	/**
	 * Macht einen Verbindungsdurchlauf.
	 * Alle in der Wegequeue vorhandenen Wege werden angelegt.
	 *
	 */
	public void processQueue() {
		List seen = new Vector(gameMap.keySet());
		for (Iterator i = this.gameMap.keySet().iterator(); i.hasNext();) {
			this.processQueue((String) i.next(), seen);
		}
	}

	/**
	 * Erstellt eine bidirektionale Verbindung zwischen zwei Räumen.
	 * 
	 * @param name			der Name des ersten Raumes
	 * @param direction		die Richtung in der der Weg liegt
	 * @param ofName		der Name des zweiten Raumes
	 * @see org.jzuul.engine.Directions
	 */
	protected void connect(String name, int direction, String ofName) {
		Room room = getRoom(name);
		Room ofRoom = getRoom(ofName);

		if (ofRoom == null) {
			return;
		}
		if (room == null) {
			return;
		}

		switch (direction) {
			case Directions.NORTH_OF :
				room.setExitByDirection(Directions.SOUTH, ofRoom);
				ofRoom.setExitByDirection(Directions.NORTH, room);
				break;
			case Directions.EAST_OF :
				room.setExitByDirection(Directions.WEST, ofRoom);
				ofRoom.setExitByDirection(Directions.EAST, room);
				break;
			case Directions.SOUTH_OF :
				room.setExitByDirection(Directions.NORTH, ofRoom);
				ofRoom.setExitByDirection(Directions.SOUTH, room);
				break;
			case Directions.WEST_OF :
				room.setExitByDirection(Directions.EAST, ofRoom);
				ofRoom.setExitByDirection(Directions.WEST, room);
				break;
			case Directions.TOP_OF :
				room.setExitByDirection(Directions.BELOW, ofRoom);
				ofRoom.setExitByDirection(Directions.TOP, room);
				break;
			case Directions.BELOW_OF :
				room.setExitByDirection(Directions.TOP, ofRoom);
				ofRoom.setExitByDirection(Directions.BELOW, room);
				break;
		}
	}

	/**
	 * Fügt ein GameObject Objekt in einen Raum ein.
	 * 
	 * @param name	der Name des Raumes
	 * @param object	das GameObject Objekt das in den Raum eingefügt werden soll.
	 */
	public void addItemToRoom(String name, GameObject object) throws NoSuchRoomException {
		if (gameMap.containsKey(name)) {
			((Room) gameMap.get(name)).getContent().addGameObject(object);
		} else {
			throw new NoSuchRoomException("Raum " + name + " existiert nicht!");
		}

	}

	/**
	 * Fügt ein Item Objekt in einen Raum ein
	 * 
	 * @param name				der Name des Raumes
	 * @param itemName		der Name des Item Objektes
	 * @deprecated Sollte nicht mehr funktionieren, da die Methode versucht das Objekt zu instanzieeren
	 */
	public void addItemToRoom(String name, String itemName) throws NoSuchRoomException {
		if (gameMap.containsKey(name)) {
			Object object = null;
			try {
				Class classDefinition = Class.forName(itemName);
				object = classDefinition.newInstance();
				addItemToRoom(name, (GameObject) object);
			} catch (InstantiationException e) {
				System.out.println(e);
			} catch (IllegalAccessException e) {
				System.out.println(e);
			} catch (ClassNotFoundException e) {
				System.out.println(e);
			}
		} else {
			throw new NoSuchRoomException("Raum " + name + " does not exist!");
		}

	}

	/**
	 * Fügt eine Beschreibung für eine Himmelsrichtung einem Raum hinzu.
	 * 
	 * @param name	der Name des Raumes
	 * @param direction	die Himmelrichtung
	 * @param description	die Beschreibung
	 * @see org.jzuul.engine.Directions
	 */
	public void setWayDescription(String name, int direction, String description) {
		Room r = getRoom(name);

		r.setWayDescription(direction, description);

	}
	
	/**
	 * Setzt die Beschreibung für alle Himmelrichtungen in einem bestimmten Raum
	 * 
	 * @param name	der Name des Raumes
	 * @param north	die Beschreibung für Directions.NORTH
	 * @param east		die Beschreibung für Directions.EAST
	 * @param south	die Beschreibung für Directions.SOUTH
	 * @param west		die Beschreibung für Directions.WEST
	 * @deprecated Benutzen wir das irgendwo?
	 */
	public void setWayDescription(String name, String north, String east, String south, String west) {
		Room r = getRoom(name);
		r.setWayDescription(Directions.NORTH, north);
		r.setWayDescription(Directions.EAST, east);
		r.setWayDescription(Directions.SOUTH, south);
		r.setWayDescription(Directions.WEST, west);

	}

	/**
	 * Testet übergänge von einem Raum in andere.
	 * Die Räume für die Übergange existieren werden aus der Liste gelöscht.
	 * 
	 * @param raum	der Raum von dem aus die Übergange getestet werden sollen
	 * @param seen	eine Liste von noch nicht besuchten Räumen.
	 */
	public void verifyMap(Room raum, List seen) {
		List ways = raum.getWays();
		seen.remove(raum);
		for (Iterator i = ways.iterator(); i.hasNext();) {
			Room r = (Room) i.next();
			if (seen.contains(r)) {
				verifyMap(r, seen);
			}
		}
	}

	/**
	 * Versucht einen kompletten Durchlauf durch die Karte und überpüft ob alle
	 * Räume erreicht werden können.
	 * 
	 * @return	True falls alle Räume erreicht werden können, false sonst
	 * @throws NoSuchRoomException falls ein Raum besucht werden soll der nicht existiert
	 */
	public boolean verifyMap() throws NoSuchRoomException, ConnectAllRoomsFailed{
		Vector seen = new Vector(gameMap.values());
		if (this.startRoom == null) {
			Room roomObj = getRandomRoom();
			if (roomObj == null) {
				throw new NoSuchRoomException("Can not get any Room for map " + name + ". Does this Map have rooms?");
			}
			startRoom = roomObj.getName();
		}
		
		this.processQueue();
		Room sraum = this.getRoom(startRoom);
		if (sraum == null)
			throw new NoSuchRoomException("Raum " + startRoom + " does not exist!");
		verifyMap(sraum, seen);
		if (!this.connectQueue.isEmpty()) {
			String roomNames = "";
			for (Iterator i = connectQueue.keySet().iterator(); i.hasNext();) {
				roomNames += ((String) i.next()) + "\n";
			}
			throw new NoSuchRoomException("Missing Rooms in Map " + name + ":\n" + roomNames);
		}

		if (seen.isEmpty()) {
			return true;
		} else {
		    String error = "Non reachable rooms in map " + name + ":\n";
			for (Iterator i = seen.iterator(); i.hasNext();) {
				error += (((Room) i.next()).getName()) + "\n";
			}
			throw new ConnectAllRoomsFailed(error);
			
		}
	}

	/**
	 * Setzt den Startraum für diese Karte
	 * 
	 * @param name	der Name des Startraumes
	 */
	public void setStartRoom(String name) {
		this.startRoom = name;
	}

	/**
	 * Wandelt die Karte in ein JDOM XML Element um.
	 * 
	 * @return	ein JDOM XML Element das den aktuellen Stand der Karte wiederspiegelt
	 */
	public Element toElement() {
		Element map = new Element("map");
		map.setAttribute("name", getName());
		map.setAttribute("startroom", getStartRoom());

		for (Iterator i = this.gameMap.values().iterator(); i.hasNext();) {
			map.addContent(((Room) i.next()).toElement());
		}

		return map;
	}

	/**
	 * Gibt einen beliebigen Raum aus der Karte zurück
	 * 
	 * @return	ein beliebiger Raum aus der Karte
	 */
	public Room getRandomRoom() {
		Vector v = new Vector(this.gameMap.values());
		if (this.gameMap.size()>0) {
			return (Room) v.get(new Random().nextInt(this.gameMap.size()));
		} else {
			return null;
		}
	}

	/**
	 * Weist einem Raum eine Inventory Objekt zu.
	 * 
	 * @param raum	der Name des Raumes
	 * @param inv		das Inventory Objekt das dem Raum gesetzt werden soll
	 */
	public void addInvToRoom(String raum, Inventory inv) throws NoSuchRoomException {
		this.npcList.addAll(inv.getCharacterObjects());
		if (getRoom(raum) == null) {
			throw new NoSuchRoomException("The room " + raum + " does not exist");
		} else {
			getRoom(raum).setInv(inv);
		}
	}

	/**
	 * Gibt eine Liste der Character Objekte die sich in den Räumen befinden zurück
	 * 
	 * @return	Liste von Character Objekten
	 */
	public List getNpcList() {
		return this.npcList;
	}

	/**
	 * Fügt einen {@link org.jzuul.engine.rooms.TransitionRoom} in die Karte ein.
	 * 
	 * @param name					der Name des Raumes
	 * @param preconditions	die Namen von GameObject Objekten die Vorbedingungen für einen Übergang sind
	 * @param isFinal					ist das das Ende des Spieles?
	 * @param target					der Name der Zielkarte
	 */
	public void addTransRoom(String name, String[] preconditions, boolean isFinal, String target) {
		TransitionRoom newRoom = new TransitionRoom(name, preconditions, isFinal, target);
		this.addRoom(newRoom);
	}

	/**
	 * Zugriff auf den Namen der Karte.
	 * 
	 * @return	der Name der Karte
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Zugriff auf den Namen des Startraumes der Karte
	 * 
	 * @return der Name des Startraumes.
	 */
	public String getStartRoom() {
		return this.startRoom;
	}

	/**
	 * Zugriff auf das Room Objekt des Startraumes
	 * 
	 * @return	das Room Objekt des Startraume, null wenn kein Startraum gesetzt ist
	 */
	public Room getStartRoomObj() {
		return this.getRoom(this.startRoom);
	}

	/**
	 * Teilt allen Räumen in der Karte ein Event mit
	 * 
	 * @param eventId	eine Id eines Events
	 * @see org.jzuul.engine.Event
	 */
	public void notifyRooms(int eventId) {
		for (Iterator roomIter = gameMap.values().iterator(); roomIter.hasNext();) {
			EventListener element = (EventListener) roomIter.next();
			element.doEvent(eventId);
		}
	}

	/**
	 * Setzt einem Raum einen EventHandler
	 * 
	 * @param room					der Name des Raumes für den EventHandler
	 * @param eventName		der Name des Events
	 * @param eventHandler		das EventHandler Objekt das das Event behandelt
	 * @see org.jzuul.engine.Event
	 */
	public void setRoomEventHandler(String room, String eventName, EventHandler eventHandler) {
		Room r = getRoom(room);
		r.setHandler(eventName, eventHandler);
	}
	
	/**
	 * Weist einem Raum einen auf ein Bild verweisenden Stream zu
	 * 
	 * @param roomName	der Name des Raumes dem der Stream zugewiesen werden soll
	 * @param imageStream	der Stream der auf das Bild verweist.
	 */
	public void setRoomImageStream(String roomName, InputStream imageStream) {
		getRoom(roomName).setImageStream(imageStream);
	}

}
