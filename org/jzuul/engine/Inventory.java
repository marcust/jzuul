/*
 * 	CVS: $Id: Inventory.java,v 1.12 2004/07/26 10:09:12 marcus Exp $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

/**
 * Das Inventar eines Spielers oder eines Raums.
 * Hier werden die aufgenommen GameObjects gehalten.
 * Das Inventar hat eine Grösse. Jedes aufgenomme GameObject belegt Platz im Inventar,
 * so dass nur eine gewisse Anzahl an GameObjects aufgenommen werden kann.
 * 
 * 
 * @version $Revision: 1.12 $
  */

public class Inventory {
	/**
	 * Interner Datentyp zum halten der GameObjects
	 * HashMap of Stacks
	 */
	protected Map gameObjectMap;
	
	/**
	 * Größe des Inventars in Einheiten
	 */
	int size = 10;
	
	/**
	 * Füllstatus des Inventars
	 */
	int fillStat = 0;
	
	/**
	 * Bildet size == UNLIMITED_INVENTORY ab
	 */
	boolean isUnlimited;
	
	/**
	 * Größenwert für ein unendlich großes Inventar
	 */
	public static int UNLIMITED_INVENTORY = -1;

	//----------------------------------------------------------------------------

	/**
	 * Konstruktor
	 * @param size - Die größe des Inventar
	 */
	public Inventory(int size) {
		this.gameObjectMap = new HashMap();
		this.size = size;
		this.isUnlimited = (size == Inventory.UNLIMITED_INVENTORY);
	}

	/**
	 * Fügt ein neues Item in das Inventar ein
	 * @param item das Aufgenommen werden soll
	 * @return Erfolgsmeldung... konnte das Item aufgenommen werden true, bei Fehlern wie z.B. volles Inventar false
	 */
	public boolean addGameObject(GameObject item) {
		if ((!this.isUnlimited) && ((fillStat + item.getSize()) > this.size)) {
			return false;
		}
		if (gameObjectMap.containsKey(item.getName())) {
			((Stack) gameObjectMap.get(item.getName())).push(item);
		} else {
			Stack s = new Stack();
			s.push(item);
			gameObjectMap.put(item.getName(), s);
		}

		fillStat += item.getSize();
		return true;
	}

	/**
	 * @param item das Item das hinzugefügt werden soll
	 * @return	true wenn das Item aufgenommen wurde, false sonst
	 * @see #addGameObject 
	 */
	public boolean pushGameObject(GameObject item) {
		return this.addGameObject(item);
	}

	/**
	 * Get a GameObject by name
	 * @param name	Name des GameObjects das geholt werden soll
	 * @return				Das korespondierende GameObject, null wenn es nicht existiert
	 */
	public GameObject getGameObject(String name) {
		if (gameObjectMap.containsKey(name)) {
			Stack itemStack = (Stack) gameObjectMap.get(name);
			return (GameObject) itemStack.peek();
		}
		return null;
	}

	/**
	 * Liefert Iterator über die Namen der Objekt in dem Inventars 
	 * 
	 * @return Iterator über die GameObject Namen
	 */
	public Iterator gameObjectNamesIterator() {
		return this.gameObjectMap.keySet().iterator();
	}

	/**
	 * Fragt ob das Iventory das GameObject mit dem Namen name enthält
	 * 
	 * @param name	der Name des GameObject
	 * @return				true falls das Inventar mindest ein GameObject mit dem Namen enthält, false otherwise
	 */
	public boolean containsGameObject(String name) {
		return this.gameObjectMap.containsKey(name);
	}

	/**
	 * Löscht das Item mit dem Namen name
	 * @param name	der Name des zu löschenden Objektes
	 * @return always true ?
	 */
	public boolean deleteGameObject(String name) {
		Stack objStack = (Stack) gameObjectMap.get(name);
		fillStat -= ((GameObject) objStack.peek()).getSize();
		objStack.pop();
		if (objStack.isEmpty()) {
			gameObjectMap.remove(name);
		}
		return true;
	}

	/**
	 * popItem gibt das GameObject mit dem Namen name zurück und entfernt dieses aus dem Inventar
	 *
	 * @param name
	 * @return GameObject on success and null on failure
	 */
	public GameObject popGameObject(String name) {
		Stack objStack = (Stack) gameObjectMap.get(name);
		if (objStack == null) {
			return null;
		}
		GameObject obj = (GameObject) objStack.pop();
		fillStat -= obj.getSize();
		if (objStack.isEmpty()) {
			gameObjectMap.remove(name);
		}
		return obj;
	}

	/**
	 * Gibt eine textuelle Beschreibung des Inventars zurück
	 * 
	 * @return	einen multiline String der den Inhalt des Inventars beschreibt
	 */
	public String toString() {
		if (this.gameObjectMap.isEmpty()) {
		    Object[] formatArgs = { new Integer(size) };
			return MessageFormat.format(Messages.getString("INVENTORY_EMPTY"),formatArgs); //$NON-NLS-1$
		}
		String inhalt = new String(Messages.getString("INVENTORY_CONTAINS")); //$NON-NLS-1$
		for (Iterator i = gameObjectMap.keySet().iterator(); i.hasNext();) {
			String objName = (String) i.next();
			inhalt += Helpers.firstToUpper(objName);
			// tell how many:
			if (getNumberOfObject(objName) > 1) {
				inhalt += " (" + getNumberOfObject(objName) + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				inhalt += "\n"; //$NON-NLS-1$
			}
		}
		inhalt += "\n"; //$NON-NLS-1$
		Object[] formatArgs = {  new Integer(size - fillStat) };
		inhalt += MessageFormat.format(Messages.getString("INVENTORY_SPACE_LEFT"),formatArgs); //$NON-NLS-1$
		return inhalt;
	}

	/**
	 * Sucht alle Character im Inventar
	 * 
	 * @return	Eine Liste aller Character Objekte im Inventar
	 */
	public List getCharacterObjects() {
		Vector v = new Vector();
		for (Iterator i = gameObjectMap.values().iterator(); i.hasNext();) {
			Stack s = (Stack) i.next();
			if (!s.isEmpty() && (s.peek() instanceof Character)) {
				for (Iterator stackIter = s.iterator(); stackIter.hasNext();) {
					Character o = (Character) stackIter.next();
					v.add(o);
				}
			}
		}
		return v;
	}

	/**
	 * Gibt alle GameObjects im Inventar zurück
	 * 
	 * @return	Iterator über die GameObjects Objekte
	 */
	public Iterator getGameObjects() {
		Vector v = new Vector();
		for (Iterator i = gameObjectMap.values().iterator(); i.hasNext();) {
			Stack s = (Stack) i.next();
			for (Iterator stackIter = s.iterator(); stackIter.hasNext();) {
				GameObject o = (GameObject) stackIter.next();
				v.add(o);
			}
		}
		return v.iterator();
	}

	/**
	 * Wandelt das Inventar in ein JDOM XML Element um.
	 * 
	 * @return	ein JDOM Element Objekt das den Inhalt des Inventars beschreibt
	 */
	public Element toElement() {
		Element contentE = new Element("contents"); //$NON-NLS-1$
		for (Iterator i = this.getGameObjects(); i.hasNext();) {
			contentE.addContent(((GameObject) i.next()).toElement());
		}
		return contentE;
	}

	/**
	 * Fragt nach ob das Inventar die GameObjects mit den gegebenen Namen enthält
	 * 
	 * @param objects	Die Namen der zu überprüfenden GameObjects
	 * @return					true wenn das Inventar für jeden Namen mindestens ein Objekt enthält, false otherwise
	 */
	public boolean contains(String[] objects) {
		Vector v = new Vector(objects.length);
		for (int i = 0; i < objects.length; i++) {
			v.add(objects[i]);
		}
		return contains(v);
	}


	/**
	 * Fragt nach der Anzahl der Objekte mit dem gegebenen Namen
	 * 
	 * @param name	ein Name eines GameObjects
	 * @return				die Anzahl der GameObjects in dem Inventar mit diesem Namen
	 */
	public int getNumberOfObject(String name) {
		if (gameObjectMap.containsKey(name)) {
			Stack s = (Stack) gameObjectMap.get(name);
			return s.size();
		} else {
			return 0;
		}
	}

	/**
	 * @param objects eine Liste von Objekten die überprüft werden sollen
	 * @return True wenn die Objekte in dem Invetar sind, false sonst
	 * @see #contains(String[] objects)
	 */
	 public boolean contains(List objects) {
		Inventory tmp = this.copy();
		for (Iterator objIter = objects.iterator(); objIter.hasNext();) {
			String objName = (String) objIter.next();
			if (tmp.containsGameObject(objName)) {
				tmp.deleteGameObject(objName);
			} else {
				return false;
			}

		}
		return true;
	}

	/**
	 * Erstellt eine depp copy des aktuellen Inventars
	 * 
	 * @return	ein neues Inventar objekt mit dem gleichen Inhalt wie das aktuelle Inventar
	 */
	public Inventory copy() {
		Map copy = new HashMap();
		
		for (Iterator mapIter = gameObjectMap.keySet().iterator(); mapIter.hasNext();) {
			String element = (String) mapIter.next();
			Stack oldStack = (Stack) gameObjectMap.get(element);
			Stack newStack = new Stack();
			newStack.addAll(oldStack);
			copy.put(element, newStack);
		}

		Inventory newInv =  new Inventory(this.size);
		newInv.gameObjectMap = copy;
		newInv.fillStat = this.fillStat;
		return newInv;
	}

}
