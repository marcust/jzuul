/*
 * 	CVS: $Id: Item.java,v 1.12 2004/07/25 19:07:20 marcus Exp $
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

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;

/**
 * Diese Klasse stellt statische GameObjects (Gegenstände) zur Verfügung
 * 
 * 
 * @version $Revision: 1.12 $
*/
public class Item extends GameObject {
	/**
	 * Enthällt mappings von GameObject namen auf EventHandler, um "use with" Befehle zu implementieren,
	 */
	Map withObjects;

	/**
	 * Erstellt ein neues GameObject.
	 * 
	 * @param name	der Name des GameObject
	 */
	public Item(String name) {
		super(name);
		this.takeable = true;
		this.useable = true;
		withObjects = new HashMap();
	}

	/**
	 * Implementiert den Befehl "use with".
	 * Der Befehl "use with" Benutzt das Eventsystem um die daraus resultierenden
	 * Aktionen zu implementieren. Dafür wird dem GameObject nach dem "use" eine
	 * Event Map angelegt das mappings von Namen auf EventHandler hat. Diese EventHandler
	 * werden dann mit execute(this) aufgerufen,
	 * 
	 * @param item	das GameObject das nach dem "with" steht
	 * @return	true on success, false on failure
	 * @see 		org.jzuul.engine.EventHandler
	 */
	public boolean useWith(GameObject item) {
		if (withObjects.containsKey(item.getName())) {
			Engine.debug(getName() + " executing EventHandler for \"use with " + item.getName() + "\"",1); //$NON-NLS-1$ //$NON-NLS-2$
			if (((EventHandler) withObjects.get(item.getName())).execute(item))  {
				item.doEvent(Event.USE_SUCCESS);
				return true;
			} else {
				item.doEvent(Event.USE_FAILURE);
				return false;
			}
		} else {
			Engine.player.say(Messages.getString("ITEM_COMBINATION_ERROR")); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Verarbeitet ein Event.
	 * Der einzige Default handler der von Item Objekten implementiert wird
	 * ist für den Befehl "use". Falls das Item true für deleteOnUse hat wird
	 * es nach dem Event.USE gelösct.
	 * 
	 * @param eventId	die Id des Events
	 * @see	org.jzuul.engine.Event
	 */
	public void doEvent(int eventId) {
		super.doEvent(eventId);
		switch (eventId) {
			case Event.USE :
				if (deleteOnUse)
					Engine.player.findAndDeleteGameObject(this.name);
				break;
		}

	}

	/**
	 * Wandelt das Item Objekt in ein JDOM XML Element um.
	 *
	 *@return eine JDOM XML Element representations des Objekts
	 */
	public Element toElement() {
		Element itemE = new Element("item"); //$NON-NLS-1$
		itemE.setAttribute("name", this.getName()); //$NON-NLS-1$
		return itemE;
	}

	/**
	 * Fügt einen "withObject" EventHandler hinzu.
	 * Im Endeffekt das gleiche wie die normalen EventHandler, nur das die Namen
	 * der Events hier richtige GameObject Objektnamen sind 
	 * 
	 * @param objName	der Name des withObjects
	 * @param handler	der dazugehörige EventHandler
	 */
	public void addWithObject(String objName, EventHandler handler) {
		Engine.debug("Added use " + this.getName() + " with " + objName, 1); //$NON-NLS-1$ //$NON-NLS-2$
		withObjects.put(objName, handler);
	}

	public GameObject copy() {
		Item newItem = new Item(this.getName());
		super.cloneInto(newItem);
		newItem.withObjects = this.withObjects;
			
		return newItem;
	}
	
	public boolean isItem() {
	    return true;
	}

}
