/*
 * 	CVS: $Id: TargetList.java,v 1.9 2004/07/25 19:07:20 marcus Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jzuul.engine.gui.GuiInterface;

/**
 * Diese Klasse implementiert Ziellisten im Spiel.
 * 
 * 
 * @version $Revision: 1.9 $
 * @see org.jzuul.engine.TargetObject
 */
public class TargetList {
	/**
	 * enthällt die TargetObject Objekte
	 */
	private List targets;
	
	/**
	 * Erstellt eine neues TargetList Objekt
	 *
	 */
	public TargetList() {
		this.targets = new Vector();
	}

	/**
	 * Fügt ein Ziel zu dem TargetList Objekt hinzu
	 * 
	 * @param target	ein TargetObject objekt das das neue Ziel abbildet
	 */
	public void addTarget(TargetObject target) {
		this.targets.add(target);
	}

	/**
	 * Füg ein Ziel zu dem TargetList Objekt hinzu
	 * 
	 * @param type					der Name des Typs des neuen TargetObject Objekt
	 * @param objName			der Name des damit assozierten GameObject Objektes
	 * @param description		die Beschreibung
	 * @see org.jzuul.engine.TargetObject
	 */
	public void addTarget(String type, String objName,String description, String creator) {
				addTarget(TargetObject.actionTypeFromString(type),objName,description,creator);
	}
	
	/**
	 * @param type		der Type des Targets
	 * @param objName	der Name des mit dem Ziel verbundenen Objektes
	 * @param description	die Beschreibung des Ziels
	 * @see #addTarget(String type, String objName, String description) 
	 */
	public void addTarget(int type, String objName,String description, String creator) {
				if (description == null) throw new IllegalArgumentException("description is not set"); //$NON-NLS-1$
				Engine.debug("Added a new Target Object",1); //$NON-NLS-1$
				targets.add(new TargetObject(type,objName,description,creator));
	}
	
	/**
	 * Iterator über die TargetObject Objekte in dieser Liste
	 * 
	 * @return	Iterator über TargetObject Objekte
	 */
	public Iterator targetObjectIterator() {
		return this.targets.iterator();
	}

	/**
	 * Gibt die Beschreibungen der einzelnen TargetObject Objekte in dieser Liste aus.
	 *
	 */
	public void print() {
		if (!targets.isEmpty()) {
		Engine.gui.println(Messages.getString("TARGET_ARE")); //$NON-NLS-1$
		for (Iterator targetIter = targets.iterator(); targetIter.hasNext();) {
		TargetObject element = (TargetObject) targetIter.next();
			Engine.gui.println(" - " + element); //$NON-NLS-1$
		}
		
	} else {
			Engine.gui.println(Messages.getString("TARGET_NONE")); //$NON-NLS-1$
		}
	}
	
	/**
	 * Überpüft ob ein Ziel erfüllt ist.
	 * Wird vom Engine nach einem Befehl der die targetAction Eigenschaft besitzt aufgerufen
	 * um zu überprüfen ob die Aktion zum Erfüllen eines Zieles geführt hat.
	 * Wenn die Aktion ein Ziel erfüllt hat dann wirde eine Meldung ausgegeben und dieses TargetObject
	 * Objekt aus der Liste gelöscht.
	 * 
	 * @param type					der Typ der Aktion
	 * @param objectName	der Name des Objektes für das die Aktion ausgeführt wurde.
	 */	
	public void targetAction(int type, String objectName) {
			Engine.debug("targetType is " + type + " object is " + objectName,1); //$NON-NLS-1$ //$NON-NLS-2$
			for (Iterator targetIter = targets.iterator(); targetIter.hasNext();) {
				TargetObject element = (TargetObject) targetIter.next();
				if (element.identify(type,objectName)) {
					Engine.gui.println(Messages.getString("TARGET_FULLFILLED"),GuiInterface.GREEN); //$NON-NLS-1$
					targetIter.remove();
				}
			}
	}
	
	/**
	 * Fügt dieser TargetList neue Targets hinzu
	 * 
	 * @param targets	eine TargetList deren TargetObject Objekte übernommen werden sollen
	 */
	public void addTargetListAll(TargetList targets) {
		for (Iterator targetItet = targets.targetObjectIterator(); targetItet.hasNext();) {
			TargetObject element = (TargetObject) targetItet.next();
			this.addTarget(element);
		}		
	}

	/**
	 * Fügt ein TargetObject Objekt hinzu und gibt eine Meldung aus.
	 * 
	 * @param target	das hinzuzufügende TargetObject
	 */
	public void addTargetVerbose(TargetObject target) {
			Engine.gui.println(Messages.getString("TARGET_NEW") + target,GuiInterface.GREEN); //$NON-NLS-1$
			this.addTarget(target);
		}

	/**
	 * Wandelt das Objekt in eine Liste von JDOM XML Elementen um.
	 * 
	 * @return	die einzelnen TargetObject Objekte in einer List von JDOM XML Elementen
	 */
	public List toElementList() {
		List targetListE = new Vector();
		for (Iterator tOIter = targets.iterator(); tOIter.hasNext();) {
			TargetObject element = (TargetObject) tOIter.next();
			targetListE.add(element.toElement());
		}
		return targetListE;
	}
	
	/**
	 * Macht eine 1 zu 1 kopie von diesem Objekt
	 * 
	 * @return	ein neues Objekt mit den gleichen TargetObjects wie die ursprüngliche Liste
	 */
	public TargetList copy( ) {
		TargetList newList = new TargetList();
		newList.targets = new Vector(this.targets);
		return newList;
	}
	
	public void setCreator(String name) {
	    for (Iterator targetIter = targets.iterator(); targetIter.hasNext();) {
            TargetObject tarObj = (TargetObject) targetIter.next();
            tarObj.setCreator(name);
        }
	}
	
	public boolean isGiveTarget(String characterName, String objectName) {
	    Engine.debug("checking if " + characterName + " takes " + objectName,1); //$NON-NLS-1$ //$NON-NLS-2$
	    List giveTargets = getTargetsByType(TargetObject.GIVE);
	    for (Iterator targetIter = giveTargets.iterator(); targetIter.hasNext();) {
            TargetObject element = (TargetObject) targetIter.next();
            if (element.checkGive(characterName,objectName)) {
                Engine.debug("true" ,1); //$NON-NLS-1$
                return true;
            }
        }
	    Engine.debug("false" ,1); //$NON-NLS-1$
	    return false;
	}
	
	public List getTargetsByType(int type) {
	    List retval = new ArrayList();
	    for (Iterator targetIter = targets.iterator(); targetIter.hasNext();) {
            TargetObject tarObj = (TargetObject) targetIter.next();
            if (tarObj.hasType(type)) {
                retval.add(tarObj);
            }
        }
	    Engine.debug(this + "Having " + retval.size() + " targets of type " + type ,1); //$NON-NLS-1$ //$NON-NLS-2$
	    return retval;
	}
	
	
}
