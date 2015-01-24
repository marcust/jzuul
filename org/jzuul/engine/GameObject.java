/*
 * 	CVS: $Id: GameObject.java,v 1.13 2004/07/25 19:07:20 marcus Exp $
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
import java.util.Map;

import org.jdom.Element;
import org.jzuul.engine.exceptions.NoSuchEventException;

/**
 * Die Zentale Oberklasse für Characters und Items
 * 
 * 
 * @version $Revision: 1.13 $
  */
public abstract class GameObject implements EventListener {
	/**
	 * Die Beschreibung des Objektes (für "inspect")
	 */
	protected String desc;
	
	/**
	 * Der Name des Objektes
	 */
	protected String name;
	
	/**
	 * Ist das GameObject nehmbar (für "take")
	 */
	protected boolean takeable;
	
	/**
	 * Kann man das GameObject benutzen (für "use")
	 */
	protected boolean useable;
	
	/**
	 * Den Platz den das GameObject im Inventar einnimmt
	 */
	protected int size;
	
	/**
	 * Soll das GameObject nach dem Benutzen aus dem Inventar entfernt werden 
	 */
	protected boolean deleteOnUse = false;
	
	/**
	 * Targets die das GameObject zuweisen kann
	 */
	protected TargetList targets;
	
	/**
	 * Die EventHandler für dieses GameObject
	 */
	protected EventHandler[] events;

	/**
	 * Der Konstruktor der von Unterklassen aufgerufen wird
	 * 
	 * @param name	der Name des GameObjects
	 */
	public GameObject(String name) {
		this.name = name.toLowerCase();
		this.targets = new TargetList();
		this.events = new EventHandler[Event.COUNT];
	}

	/**
	 * Die Größe (der Platz) den ein Objekt im Inventory einnimmt
	 * 
	 * @return die Größe
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Accessor für den Namen
	 * 
	 * @return	der Name des GameObjects
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Holt die Beschreibung, wenn die GameObject.desc nicht gesetzt ist wird sie aus dem Namen gebildet. 
	 * 
	 * @return die Beschreibung des GameObject
	 */
	public String getDescription() {
		if (this.desc == null) {
		    Object[] formatArgs = { this.getName() };
			return MessageFormat.format(Messages.getString("GAMEOBJECT_DEFAULT_DESCRIPTION"), formatArgs); //$NON-NLS-1$
		} else {
			return this.desc;
		}
	}

	/**
	 * Setzt die Beschreibung
	 * @param desc	die neue Beschreibung des Gegenstands
	 */
	public void setDescription(String desc) {
		if (desc == null) throw new IllegalArgumentException("The description must not be null"); //$NON-NLS-1$
		this.desc = desc;
	}

	/**
	 * Setzt den Namen.
	 * Vorsicht: die Gegenstände werden auch intern über diesen Namen verwaltet (warum eigentlich?) daher
	 * sollte der Name nicht geändert werden. 
	 * 
	 * @param name der neue Name eines Objektes
	 */
	public void setName(String name) {
		if (name == null) { throw new IllegalArgumentException(); }
		this.name = name.toLowerCase();
	}

	/**
	 * Kann der Spieler das GameObject aufheben
	 * 
	 * @return	true wenn man es aufheben kann, false sonst
	 */
	public boolean isTakeable() {
		return this.takeable;
	}

	/**
	 * Setzt die Benutzbarkeit eines GameObjects
	 * 
	 * @param value	true falls man es mit use benutzen kann, false sonst
	 */
	public void setUsability(boolean value) {
		this.useable = value;
	}

	/**
	 * Kann der Spieler das GameObject benutzen?
	 * 
	 * @return true wenn man es benutzen kann, false sonst
	 */
	public boolean isUsable() {
		return this.useable;
	}

	/**
	 * Ist das GameObject ein Character Objekt?
	 * 
	 * @return	true wenn das Objekt eine instanz von Character ist, false sonst
	 */
	public boolean isCharacter() {
		return false;
	}

	/**
	 * Ist das GameObject eine instanz von Item
	 * 
	 * @return	true wenn das Objekt eine Instanz von Item ist, false sonst
	 */
	public boolean isItem() {
		return false;
	}

	/**
	 * Wandelt das Objekt in ein JDOM XML Element
	 * 
	 * @return	ein JDOM XML Element
	 */
	public abstract Element toElement();

	/**
	 * Setzt die Nehmbarkeit des GameObjects
	 * 
	 * @param takeable	true wenn man es aufheben können soll, sonst false
	 */
	public void setTakeable(boolean takeable) {
		this.takeable = takeable;
	}

	/**
	 * Setzt die Größe (den Platz im Inventar) des GameObject
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		if (size < 0) { throw new IllegalArgumentException("Size must be bigger than 0, is " + size); } //$NON-NLS-1$
		this.size = size;
	}
	
	/**
	 * Setzt ob das GameObject nach der Benutzung aus dem Inventar gelöscht werden soll
	 * 
	 * @param delete	soll es nach "use" gelöscht werden?
	 */
	public void setDeleteOnUse(boolean delete) {
		Engine.debug("New state of deleteonuse for " + name + ":" + delete,1); //$NON-NLS-1$ //$NON-NLS-2$
		this.deleteOnUse = delete;
	}

	/**
	 * Setzt eine Eigenschaft über einen Hashkey.
	 * Im Moment wird nur "description" unterstützt, was setDescription aufruft
	 * 
	 * @param properties	eine Map mit den zu änderndern Eigenschaften
	 */
	public void setProperties(Map properties) {
		if (properties.containsKey("description")) { //$NON-NLS-1$
			this.desc = (String)properties.get("description"); //$NON-NLS-1$
		} else {
			Engine.debug("Call to set unsuported property in GameObject",1); //$NON-NLS-1$
		}
	}

	/**
	 * Zugriff auf die TargetList eines GameObjects
	 * 
	 * @return	die mit dem GameObject assozierte TargetList
	 */
	public TargetList getTargetList() {
		return this.targets;
	}

	/**
	 * Führt ein event aus. 
	 * 
	 * @param id	Die Id des Events
	 * @see org.jzuul.engine.Event
	 * @see org.jzuul.engine.EventHandler
	 */
	public void doEvent(int id) {
		if (id >= Event.COUNT || id < 0) throw new IllegalArgumentException("Id is not valid: " + id); //$NON-NLS-1$
		if (id!=0) Engine.debug(getName() + " got event " + id,1); //$NON-NLS-1$
		if (events[id] != null) {
			events[id].execute(this);
		} else {
			if (id!=0) Engine.debug(getName() + " has no event handler for id " + id,1); //$NON-NLS-1$
		}
	}
	
	/**
	 * Setzt einen EventHandler
	 * 
	 * @param eventName	der Name des Events
	 * @param handler			das EventHandler Objekt
	 * @see	org.jzuul.engine.EventHandler
	 * @see	org.jzuul.engine.Event
	 */
	public void setHandler(String eventName, EventHandler handler) {
		if (eventName == null || handler == null) {
		    Engine.debug("Tryed to set a null handler (name=" + eventName + ", handler=" + handler + ")",1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		    return;
		}
		int id = Event.fromString(eventName);
		handler.setOwner(this.getName());
		if (id > -1) {
		events[id] = handler;
		} else {
			throw new NoSuchEventException("The event " + eventName + " is unknown"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
		
	/**
	 * Kopiert die Eigenschaften dieses GameObjects in das übergeben GameObject.
	 * Wird von Character.clone und Item.clone benutzt
	 * 
	 * @param obj	das Objekt dessen eigenschaften mit dem aktuellen gesynct werden sollen
	 */	
	protected void cloneInto(GameObject obj) {
		obj.name = this.name;
		obj.desc = this.desc;
		obj.takeable = this.takeable;
		obj.useable = this.useable;
		obj.size = this.size;
		obj.deleteOnUse = this.deleteOnUse;
		obj.targets = this.targets.copy();
		//NOTE I don't know wether this is correct or not, because events is a array of objects
		obj.events = this.events;
	}

	abstract public GameObject copy();

}
