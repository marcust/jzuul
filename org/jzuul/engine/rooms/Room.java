/*
 * 	CVS: $Id: Room.java,v 1.17 2004/07/25 19:07:20 marcus Exp $
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

package org.jzuul.engine.rooms;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jzuul.engine.Directions;
import org.jzuul.engine.Engine;
import org.jzuul.engine.Event;
import org.jzuul.engine.EventHandler;
import org.jzuul.engine.EventListener;
import org.jzuul.engine.GameObject;
import org.jzuul.engine.Helpers;
import org.jzuul.engine.Inventory;
import org.jzuul.engine.Messages;
import org.jzuul.engine.exceptions.GuiNotInitializedException;
import org.jzuul.engine.exceptions.NoSuchEventException;

/**
 * Ein normaler Raum
 * 
 * 
 * @version $Revision: 1.17 $
 **/
public class Room implements EventListener {
	/**
	 * enthält die Beschriebung des Raumes
	 */
	protected String beschreibung;

	/**
	 * die Beschreibungen für "look"
	 */
	protected String[] blickrichtungen = new String[Directions.NUMBER_OF_DIRECTIONS];

	/**
	 * Die Raum Objekte die an diesen Raum grenzen
	 */
	protected Room[] wege = new Room[Directions.NUMBER_OF_DIRECTIONS];

	/**
	 * Der Name des Raumes
	 */
	protected String name;

	/**
	 * Die EventHandler für diesen Raum
	 */
	protected EventHandler[] eventHandlers = new EventHandler[Event.COUNT];

	/**
	 * Der Inhalt des Raumes
	 */
	protected Inventory contents;
	
	/**
	 * Enthällt den Stream auf das für den Raum definierte Bild
	 */
	protected InputStream imageStream;

	/**
	 * Konstruktor, erstellt einen Raum OHNE Beschreibung
	 * 
	 * @param name der Name des Raumes, ermöglicht den Zugriff innerhalb einer GameMap
	 */
	public Room(String name) {
		this(name, ""); //$NON-NLS-1$
	}

	/**
	 * Erzeuge eine Room Objekt mit einer Beschreibung. Ein Raum
	 * hat anfangs keine Ausgänge.
	 * @param name	der Name des Raumes, ermöglicht den Zugriff innerhalb einer GameMap
	 * @param beschreibung enthält eine Beschreibung in der Form
	 *        "in einer Küche" oder "auf einem Sportplatz".
	 */
	public Room(String name, String beschreibung) {
		this.name = name;
		this.beschreibung = beschreibung;
		contents = new Inventory(Inventory.UNLIMITED_INVENTORY);
	}

	/**
	 * Ermöglicht den Zugriff auf das "Inventar" des Raumes, ein Room Objekt kann unendlich viele Dinge enthalten
	 * 
	 * @return das Inventory Objekt des Raumes
	 */
	public Inventory getContent() {
		return this.contents;
	}

	/** 
	* Zugriff auf das Room Objekt unter diesem Room Objekt
	 * 
	 * @return das Room Objekt unter dem aktuellen Raum
	 */
	public Room getDownway() {
		return getRoomByOrientation(Directions.BELOW);
	}

	/** 
	* Zugriff auf das Room Objekt über diesem Room Objekt
	 * 
	 * @return das Room Objekt under dem aktuellen Room Objekt
	 */

	public Room getUpway() {
		return getRoomByOrientation(Directions.TOP);
	}

	/**
	 * Gibt ein Room Objekt in einer gegebenen Himmelsrichtung zurück
	 * 
	 * @param direction	die Himmelsrichtung
	 * @return	den Raum in dieser Himmelsrichtung, null falls dort keiner ist
	 * @see org.jzuul.engine.Directions
	 */
	public Room getRoomByOrientation(int direction) {
		if (wege[direction] != null) {
			return wege[direction];
		} else {
			return null;
		}
	}

	/**
	 * Gibt due Beschreibung für eine Himmelrichtung zurück (für "look")
	 * 
	 * Für d Directions.TOP und Directions.BELOW wird ein default Wert zurückgegeben,
	 * 
	 * @param direction Eine mögliche Richtung wie north,east,south und west oder up and down
	 * @return die Beschreibung in dieser Richtung, null wenn es keine Beschreibung gibt.
	 * @see org.jzuul.engine.Directions
	 */
	public String getWayDescriptionByDirection(int direction) {
	    // watch out, this is the INT version of this method
	    // which will be called by the STRING version of this method
	    Engine.debug("Called for direction " + direction, 1); //$NON-NLS-1$
		if (Directions.getDirectionName(direction) == null) {
			return null;
		}
		String desc = blickrichtungen[direction];
		if ((desc != null) && (!desc.equals(""))) { //$NON-NLS-1$
			return desc;
		}
		if (direction == Directions.TOP) {
			return Messages.getString("ROOM_ROOF"); //$NON-NLS-1$
		}
		if (direction == Directions.BELOW) {
			return Messages.getString("ROOM_BOTTOM"); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Liefere die Beschreibung dieses Raums (die dem Konstruktor
	 * übergeben wurde).
	 * @return die Beschreibeung des Raumes
	 */
	public String getDescription() {
	    if (beschreibung != null) {
	        return beschreibung;
	    } else {
	        return Messages.getString("ROOM_DEFAULT") + Helpers.firstToUpper(getName()); //$NON-NLS-1$
	    }
	}

	/**
	 * Führt ein Event aus und ruft den defaulHandler auf
	 * 
	 * @param id	die Event ID
	 * @see org.jzuul.engine.Event
	 */
	public void doEvent(int id) {
		if (id >= Event.COUNT || id < 0)
			throw new IllegalArgumentException("Id is not valid: " + id); //$NON-NLS-1$
		if (eventHandlers[id] != null) {
			eventHandlers[id].execute(this);
		}
		defaultHandler(id);
	}

	/**
	 * Default EventHandler für die Fälle Event.PLAYERENTER und Event.PLAYERLEAVE.
	 * 
	 * Der Raum gibt diese Events an sein Inventory weiter. Bei einem PLAYERENTER
	 * wird zusätzlich die Beschreibung ausgegeben
	 * 
	 * @param eventId die EventId
	 * @see org.jzuul.engine.Event
	 * @see org.jzuul.engine.EventHandler
	 */
	protected void defaultHandler(int eventId) {
		//		these are the default event handlers: (do we want them overrideable?)
		switch (eventId) {
			case Event.PLAYERENTER :
				this.printBeschreibung();
				notifyContained(Event.PLAYERENTER);
				break;
			case Event.PLAYERLEAVE :
				notifyContained(Event.PLAYERLEAVE);
				break;
		}
	}

	/**
	 * Gibt die Beschreibung aus
	 *
	 *	@throws GuiNotInitializedException wenn Engine.gui == null
	 */
	public void printBeschreibung() throws GuiNotInitializedException {
		if (Engine.gui == null) {
			throw new GuiNotInitializedException("Engine.gui is undefined"); //$NON-NLS-1$
		}

		Engine.gui.showImage(this.imageStream);
		Engine.gui.println();
		Engine.gui.println(Messages.getString("ROOM_YOU_ARE") + this.getDescription()); //$NON-NLS-1$
		Engine.gui.println();
		String ausgaenge = getWegeAsString();

		if (ausgaenge.length() > 0) {
			Engine.gui.printU(Messages.getString("ROOM_WAYS")); //$NON-NLS-1$
			Engine.gui.println(" " + ausgaenge); //$NON-NLS-1$
		}

		if (this.getUpway() != null) {
			Engine.gui.println(Messages.getString("ROOM_STAIRS_UP")); //$NON-NLS-1$
		}
		if (this.getDownway() != null) {
			Engine.gui.println(Messages.getString("ROOM_STAIRS_DOWN")); //$NON-NLS-1$
		}

		String inhalt = new String();
		for (Iterator i = contents.gameObjectNamesIterator(); i.hasNext();) {
			String objName = (String) i.next();
			inhalt += Helpers.firstToUpper(objName) + " "; //$NON-NLS-1$
			if (contents.getNumberOfObject(objName) > 1) {
				inhalt += "(" + contents.getNumberOfObject(objName) + ") "; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		if (inhalt.length() > 0) {
			Engine.gui.printU(Messages.getString("ROOM_INTERESTING_THINGS")); //$NON-NLS-1$
			Engine.gui.println(" " + inhalt); //$NON-NLS-1$
		}

		Engine.gui.println();
	}

	/**
	 * Wandelt die möglichen Wege, also Himmelsrichtungen für die ein
	 * Room Objekt existiert in eine Beschreibung um
	 * 
	 * @return die Namen der Himmelsrichtungen für die Room Objekte existieren
	 */
	public String getWegeAsString() {
		String retval = new String();

		if (getRoomByOrientation(Directions.NORTH) != null) {
			retval = "north "; //$NON-NLS-1$
		}
		if (getRoomByOrientation(Directions.EAST) != null) {
			retval += "east "; //$NON-NLS-1$
		}
		if (getRoomByOrientation(Directions.SOUTH) != null) {
			retval += "south "; //$NON-NLS-1$
		}
		if (getRoomByOrientation(Directions.WEST) != null) {
			retval += "west "; //$NON-NLS-1$
		}
		return retval;

	}

	/**
	 * Setzt die Beschreibungen für oben und unten
	 * 
	 * @param up	die neue Beschreibung für Oben
	 * @param down	die neue Beschreibung für Unten
	 */
	public void setDescriptionsUpDown(String up, String down) {
		blickrichtungen[Directions.TOP] = up;
		blickrichtungen[Directions.BELOW] = down;
	}

	/**
	 * Zugriffe auf alle an ein Room Objekt grenzenden Räume
	 * 
	 * @return	eine Liste von Room Objekten die an den aktuellen Raum grenzen
	 */
	public List getWays() {
		Vector v = new Vector();
		for (int i = 0; i < wege.length; i++) {
			if (wege[i] != null) {
				v.add(wege[i]);
			}
		}
		return v;
	}

	/**
	 * Weist dem Room Objekt eine neues benachbartes Objekt zu 
	 * 
	 * @param direction	die Himmelrichtung
	 * @param raum		das Room Objekt
	 * @see org.jzuul.engine.Directions
	 */
	public void setExitByDirection(int direction, Room raum) {
		if (wege[direction] == null) {
			wege[direction] = raum;
		} else {
			/*throw new RoomOverwriteException(
				"Der Room "
					+ this.getName()
					+ " hat schon einen Room in Richtung "
					+ Directions.getDirectionName(direction));
					*/
		}
	}

	/**
	 * Setzt die Beschreibung für eine Himmelsrichtung die mit "look" 
	 * eingesehen werden kann.
	 * 
	 * @param direction	die Richtung
	 * @param description	die neue Beschreibung
	 * @see org.jzuul.engine.Directions
	 */
	public void setWayDescription(int direction, String description) {
		blickrichtungen[direction] = description;
	}

	/**
	 * Gibt das Room Objekt in einer Himmelsrichtung zurück
	 * 
	 * @param orientation	der Name eine Himmelsrichtung
	 * @return	das Room Objekt in dieser Richtung, null falls keines existiert
	 * @see org.jzuul.engine.Directions
	 */
	public Room getRoomByOrientation(String orientation) {
		int dir = Directions.directionToInt(orientation);
		if (dir > -1) {
			return wege[dir];
		} else {
			return null;
		}
	}

	/**
	 * Gibt die Beschreibung einer Himmelsrichtung zurück
	 * 
	 * @param direction	der Name einer Himmelrichtung
	 * @return	die Beschreibung für diese Himmelsrichtung
	 * @see org.jzuul.engine.Directions
	 */
	public String getWayDescriptionByDirection(String direction) {
		int dir = Directions.directionToInt(direction);
		if (dir != Directions.DIRECTION_ERROR) {
			return getWayDescriptionByDirection(dir);
		} else {
			return null;
		}
	}

	/**
	 * Zugriff auf den Namen eines Raumes
	 * 
	 * @return	der Name des Raumes
	 */
	public String getName() {
		return name;
	}

	/**
	 * Wandelt einen Raum und die enthaltenen Objekte in ein JDOM XML Element um
	 * 
	 * @return das JDOM XML Element das diesen Raum beschreibt
	 */
	public Element toElement() {
		Element roomE = new Element("room"); //$NON-NLS-1$

		roomE.setAttribute("class", this.getClass().toString().substring(6)); //$NON-NLS-1$
		roomE.setAttribute("name", getName()); //$NON-NLS-1$
		roomE.addContent(new Element("description").setText(getDescription())); //$NON-NLS-1$

		roomE.addContent(getContent().toElement());

		Element w = waysToElement();
		if (w.getChildren().size() > 0)
			roomE.addContent(w);

		Element v = viewsToElement();
		if (v.getChildren().size() > 0)
			roomE.addContent(v);

		return roomE;
	}

	/**
	 * Wandelt die Wege (angrenzende Room Objekte) in ein JDOM XML Element um
	 * 
	 * @return	ein JDOM XML Element das die Namen der Räume beschreibt, die an diesen Raum grenzen
	 */
	protected Element waysToElement() {
		Element waysE = new Element("ways"); //$NON-NLS-1$
		for (int i = 0; i < Directions.NUMBER_OF_DIRECTIONS; i++) {
			if (wege[i] != null) {
				Element wayE = new Element("way"); //$NON-NLS-1$
				wayE.setAttribute("direction", Directions.getDirectionName(i)); //$NON-NLS-1$
				wayE.setAttribute("room", wege[i].getName()); //$NON-NLS-1$
				waysE.addContent(wayE);
			}
		}
		return waysE;
	}

	/**
	 * Wandelt die Wege (Blickrichtungsbeschreibungen) in ein JDOM XML Element um
	 * 
	 * @return	ein JDOM XML Element das die Wegbeschreibungen enthällt
	 */
	protected Element viewsToElement() {
		Element viewsE = new Element("views"); //$NON-NLS-1$

		int count = 0;
		for (int i = 0; i < Directions.NUMBER_OF_DIRECTIONS; i++) {
			if (blickrichtungen[i] != null) {
				Element viewE = new Element("view"); //$NON-NLS-1$
				viewE.setAttribute("direction", Directions.getDirectionName(i)); //$NON-NLS-1$
				viewE.setText(blickrichtungen[i]);
				viewsE.addContent(viewE);
				count++;
			}
		}
		return viewsE;
	}

	/**
	 * Weist dem Raum ein Inventar zu
	 * 
	 * @param newInv das neue Inventar
	 */
	public void setInv(Inventory newInv) {
		this.contents = newInv;
	}

	/**
	 * Weist den Raum an, ein GameObject aus dem globalen ObjectPool in sein Inventar zu übernehmen
	 * 
	 * @param objName	der Name des GameObjects das geholt werden soll 
	 */
	public void getFromPool(String objName) {
		Engine.debug(objName, 1);
		GameObject obj = (GameObject) Engine.objectPool.get(objName);
		this.contents.addGameObject(obj.copy());
	}

	/**
	 * Leitet ein Event an die enthaltenen GameObjects weiter
	 * 
	 * @param id die Id des Events
	 * @see org.jzuul.engine.Event
	 */
	public void notifyContained(int id) {
		Vector v = (Vector) this.getContent().getCharacterObjects();
		for (Iterator i = v.iterator(); i.hasNext();) {
			((EventListener) i.next()).doEvent(id);
		}
	}

	/**
	 * Setzt den EventHandler für eine Event.
	 * 
	 * @param name der Name des Events
	 * @param handler das EventHandler Objekt für dieses Event
	 * @see org.jzuul.engine.EventHandler
	 */
	public void setHandler(String name, EventHandler handler) {
		if (name == null || handler == null)
			throw new IllegalArgumentException("You can not set a handler with a null value"); //$NON-NLS-1$
		int id = Event.fromString(name);
		if (id > -1) {
			this.eventHandlers[id] = handler;
		} else {
			throw new NoSuchEventException("The event " + name + " is unknown"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Setzt dem Raum den auf das Bild verweisenden Stream
	 * 
	 * @param imageStream	ein InputStream der ein Bild enthällt
	 */
	public void setImageStream(InputStream imageStream) {
		this.imageStream = imageStream;
	}

}
