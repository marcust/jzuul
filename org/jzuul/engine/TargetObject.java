/*
 * 	CVS: $Id: TargetObject.java,v 1.6 2004/07/22 16:18:27 marcus Exp $
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

package org.jzuul.engine;

import org.jdom.Element;

/**
 * Diese Klasse stellt ein Ziel im Spiel dar.
 * 
 * 
 * @version $Revision: 1.6 $
 * @see org.jzuul.engine.TargetList
 */
public class TargetObject {
	/**
	 * Der Typ dieses TargetObject Objekt
	 */
	private int type;
	/**
	 * Der GameObject Objektname mit dem dieses Target verbunden ist 
	 */
	private String objName;
	/**
	 * Die Beschreibung die bei dem Befehl "todo" ausgegben wird.
	 */
	private String description;
	
	private String creator;

	/**
	 * Dieser Typ legt fest das die erfüllende Aktion dieses TargetObject Objektes
	 * mit dem Befehl "talk" eingeleitet wird. In diesem Fall ist objName eine Character
	 * Objekt Name.
	 */
	public static final int TALK = 0;
	/**
	 * Dieser Typ legt fest das die erfüllende Aktion dieses TargetObject Objektes
	 * mit dem Befehl "give" eingeleitet wird. Da "give" befehle nur erfolgreich sind
	 * wenn das Character Objekt das GameObject Objekt annimmt, findet die Überprüfung
	 * auf Erfolg bei dem Character statt. In diesem Fall ist objName der Name eines GameObject
	 * Objektes.
	 */
	public static final int GIVE = 1;
	/**
	 * Dieser Typ legt fest das die erfüllende Aktion dieses TargetObject Objektes
	 * ein Item ist das der Spieler besitzt. In diesem Fall ist objName der Name eines
	 * GameObject Objektes.
	 */
	public static final int ITEM = 2;
	/**
	 * Dieser Typ zeigt einen Fehler an.
	 */
	public static final int NO_SUCH_TYPE = -1;

	/**
	 * Erstellt ein neues TargetObject Objekt
	 * 
	 * @param type					der Typ dieses TargetObject Objektes
	 * @param objectName	der Name des damit assozierten GameObject Objektes
	 * @param description		die Beschreibung die ausgegeben werden soll
	 */
	public TargetObject(int type, String objectName, String description, String creator) {
		this.type = type;
		this.objName = objectName;
		this.description = description;
		this.creator = creator;
	}

	/**
	 * Zugriff auf die Beschreibung
	 *  
	 * @return die Beschreibung dieses TargetObject Objektes
	 */
	public String toString() {
		return this.description;
	}

	/**
	 * Überprüft ob eine gegebene TargetAction auf dieses Objekt zutrifft.
	 * Es wird überprüfft ob die Parameter mit dem Inhalt der Felder in diesem
	 * Objekt übereinstimmt.
	 * 
	 * @param type	ein TargetObject Objekttyp
	 * @param name	der Name des damit assozieerten GameObject Objektes
	 * @return	true falls dieses TargetObject mit den Parametern übereinstimmt, false sonst.
	 */
	public boolean identify(int type, String name) {
		return ((this.type == type) && (objName.equals(name)));
	}

	/**
	 * Wandelt einen gegebenen GameObject Objekttyp von einem String in eines der Konstanten werte
	 * 
	 * @param type	ein Typname
	 * @return	die dazugehörige Integerzahl
	 */
	public static int actionTypeFromString(String type) {
	    Engine.debug("Type: " + type,1);
		if (type.equals("talk")) {
			return TargetObject.TALK;
		}
		if (type.equals("give")) {
			return TargetObject.GIVE;
		}
		if (type.equals("item")) {
			return TargetObject.ITEM;
		}
		if (type.equals("take")) {
			return TargetObject.ITEM;
		}
		
		return TargetObject.NO_SUCH_TYPE;
	}

	/**
	 * Wandelt den Typen dieses TargetObject Objektes in seinen Namen um
	 * 
	 * @return	der Name des Typen
	 */
	private String actionTypeToString() {
		switch (type) {
			case TargetObject.TALK :
				return "talk";
			case TargetObject.GIVE :
				return "give";
			case TargetObject.ITEM :
				return "item";
			default :
				return "error";
		}
	}

	/**
	 * Wandelt dieses TargetObject in ein JDOM XML Element um
	 * 
	 * @return	die JDOM Element representation dieses Objektes
	 */
	public Element toElement() {
		Element toE = new Element("target");
		toE.addContent(new Element("description").setText(description));
		
		Element condition = new Element("condition");
		condition.setAttribute("type", actionTypeToString());
		condition.setAttribute("value", objName);
		toE.addContent(condition);
		
		if (creator != null && (!creator.equals("initial"))) {
		    toE.addContent(new Element("creator").setAttribute("name",creator));
		}
		
		return toE;
	}

	public void setCreator(String name) {
	    this.creator = name;
	    Engine.debug("creator for target type " + this.type + " (" + description + ") is now " + name,1);
	}
	
	public boolean hasType(int type) {
	    return this.type == type;
	}
	
	public boolean checkGive(String creatorName, String objName) {
	    Engine.debug("checking " + creator + "==" + creatorName + " && " +this.objName + "==" + objName,1);
	    return ((creatorName.equals(creator)) &&  (objName.equals(this.objName)));
	}
	
}