/*
 * 	CVS: $Id: EventHandler.java,v 1.12 2004/07/23 14:55:18 marcus Exp $
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
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.jzuul.engine.rooms.Room;

/**
 * Ein EventHandler behandelt {@link Event}. 
 * EventHandler können einer Klasse, die {@link EventListener} implementiert über die 
 * Methode setHandler() zugewiesen werden.
 * 
 * Es gibt drei Einstiegspunkte für die Behandlung von Events:
 * 	execute(GameObject) wird von Character Objekten und Item Objekten aufgerufen;
 * 	execute(Room) wird von Room Objekten aufgerufen;
 * 	execute()	wird von execute(Room) aufgerufen;
 * 
 * Die Methoden rufen sich untereinander selber auf, d.h. von oben nach unten werden
 * immer weniger echte Aktionen ausgeführt.
 * 
 * 
 * @version $Revision: 1.12 $
 */
public class EventHandler {
	/**
	 * Eine Liste von Strings die bei dem Event vom Spieler ausgegben werden sollen.
	 */
	protected List playerSayings;
	/**
	 * Eine Liste von Strings die bei dem Event vom beteiligten NPC ausgegben werden sollen.
	 */
	protected List npcSayings;
	/**
	 * Eine Liste von actions, die bei diesem Event ausgeführt werden sollen.
	 */
	protected List actions;
	/**
	 * TargetObjects die dem Spieler bei diesem Event zugewiesen werden.
	 */
	protected TargetList targets;
	/**
	 * Liste von GameObject Namen, die bei diesem Event dem Player übergeben werden.
	 * 
	 */
	protected List invobjects;
	/**
	 * Liste von GameObject Namen, die bei diesem Event dem Raum hinzugefügt werden
	 * 
	 */
	protected List roomobjects;
	/**
	 * Liste von GameObject Namen, die bei diesem Event gelöscht werden sollen.
	 * Hierbei wird sowohl im Raum als auch im Inventar des Spielers nachgeschaut.
	 * 
	 */
	protected List delete;
	/**
	 * Map von properties, die bei diesem Event geändert werden sollen.
	 */
	protected Map properties;
	/**
	 * Legt fest ob das Event RandomSuccess haben soll.
	 * Steuert den Rückgabewert der execute Methode.
	 */
	protected boolean randomSuccess;
	
	private String owner;

	/**
	 * Erstellt einen leeren EventHandler
	 *
	 */
	public EventHandler() {
		this.playerSayings = new Vector();
		this.npcSayings = new Vector();
		this.actions = new Vector();
		this.invobjects = new Vector();
		this.roomobjects = new Vector();
		this.delete = new Vector();
		this.randomSuccess = false;
	}

	/**
	 * Fügt einen Satz zu den bei dem Event ausgegbenen Sätzen hinzu
	 * 
	 * @param sentence	der neue Satz
	 */
	public void addPlayerSaying(String sentence) {
		this.playerSayings.add(sentence);
	}

	/**
	 * Fügt einen Satz zu den bei dem Event ausgegbenen NPC Sätzen hinzu
	 * 
	 * @param sentence	der neue Satz
	 */
	public void addNpcSaying(String sentence) {
	    Engine.debug("added npc saying: " + sentence, 1);
		this.npcSayings.add(sentence);
	}
	
	/**
	 * Fügt den Namen einer Aktion zu den bei dem Event ausgeführten Aktionen hinzu
	 * 
	 * @param actionName	der Name einer aktion (z.B. moveRandom)
	 */
	public void addAction(String actionName) {
		this.actions.add(actionName);
	}

	/**
	 * Setzt die TargetList die die TargetObjects enthällt die bei der Aktion dem Spieler hinzugefüht werden
	 * 
	 * @param targets	eine TargetList mit neuen Targets
	 */
	public void setTargets(TargetList targets) {
		this.targets = targets;
	}


	/**
	 * Fügt den namen eines GameObjects hinzu, das bei dem Event dem Spieler übergeben werden soll
	 * 
	 * @param objName	der Name eines existierenden GameObjects
	 */
	public void addInvobject(String objName) {
		this.invobjects.add(objName);
	}

	/**
	 * Fügt den Namen eines GameObjects hinzu, das bei dem Event dem aktuellen Raum hinzugefügt werden
	 * soll.
	 * 
	 * @param objName der Name eines existierenden GameObjects
	 */
	public void addRoomobject(String objName) {
		this.roomobjects.add(objName);
	}

	/**
	 * Fügt den Namen eines GameObjects hinzu, das bei dem Event gelöscht werden soll.
	 * 
	 * @param objName	der Name eines existierenden GameObjects
	 */
	public void addDeletition(String objName) {
		this.delete.add(objName);
	}

	/**
	 * Setzt die Map in der zu ändernde Eigenschaften eines GameObjects definiert werden
	 * 
	 * @param properties	eine Map von Eigenschaftsnamen und neuen Werten
	 */
	public void setPropertyChanges(Map properties) {
		this.properties = properties;
	}

	/**
	 * Definiert ob die auslösende Aktion zufällig ist.
	 * 
	 * @param isRandomSuccess	true wenn dem so ist, false sonst
	 */
	public void setRandomSuccess(boolean isRandomSuccess) {
		this.randomSuccess = isRandomSuccess;
	}

	/**
	 * Die Haupmethode für GameObjects.
	 * Wird von den doEvent() Methoden von GameObject aufgerufen, die sich selber 
	 * übergeben.
	 * Falls das übergebene GameObject ein Character Objekt ist wird die Action für
	 * den Charakter ausgeführt und die Sätze von dem Character gesagt. Da Characters
	 * überprüfen ob sie sich im selben Raum wie der Spieler befinden bevor sie etwas sagen
	 * kann hier die Ausgabe auch ins leere verlaufen.
	 * Properties werden in dem übergebenen GameObject geändert.
	 * Danach wird die Methode excecute(Room) mit dem aktuellen Raum des Spielers
	 * aufgerufen.
	 * 
	 * @param obj das GameObject das das Event bekommen hat
	 * @return	true be erfolg, false otherwise
	 */
	public boolean execute(GameObject obj) {
		// say and actions like moveRandom etc...
	    if (isCharacter(obj)) {
			Character cobj = (Character) obj;
			for (Iterator actionIter = actions.iterator(); actionIter.hasNext();) {
				String element = (String) actionIter.next();
				cobj.doAction(element);
			}
			for (Iterator sayingIter = npcSayings.iterator(); sayingIter.hasNext();) {
				String element = (String) sayingIter.next();
				cobj.say(element);
			}
		}
		if ((this.properties != null) && (properties.size() > 0)) {
			Engine.player.findGameObject(obj.getName()).setProperties(properties);
		}
		return execute(Engine.player.getCurrentRoom());
	}

	/**
	 * Execute methode ohne Parameter
	 * In dieser Methode wird die TargetList dem Spieler hinzugefügt und
	 * die Objekte die ins Inventar kommen und gelöscht werden bearbeitet,
	 * Die Methode wird von execute(Room) aufgerufen.
	 * 
	 * @return always true
	 */
	public boolean execute() {
	    // target lists
		for (Iterator targetIter = targets.targetObjectIterator(); targetIter.hasNext();) {
			TargetObject element = (TargetObject) targetIter.next();
			Engine.player.getTargetList().addTargetVerbose(element);
		}

		//handle game object stuff like roomitems, invitems, delete and properties
		if (this.invobjects != null) {
			for (Iterator objIter = invobjects.iterator(); objIter.hasNext();) {
				String element = (String) objIter.next();
				Engine.player.getFromPool(element);
			}
		}

		if (this.delete != null) {
			for (Iterator deleteIter = delete.iterator(); deleteIter.hasNext();) {
				String element = (String) deleteIter.next();
				Engine.player.findAndDeleteGameObject(element,false);
			}

		}
		return true;

	}

	/**
     * Methode für Räume Hier werden Sätze im Namen des Players gesagt. Die
     * excute(GameObject) methode löscht gesagt Sätze falls ein Character
     * übergeben wurde, daher werden hier nur Sätze in Verbindung mit Items
     * gesagt. Die Sätze werden nur ausgegeben wenn der übergebene Raum auch der
     * aktuelle Raum des Spielers ist. Danach werden dem Übergebenen Raumobjekt
     * die eingestellten GameObjects hinzugefügt. Zuletzt wird execute()
     * aufgerufen, danach entscheidet das Random Success ob die Methode true
     * oder false zurückgibt. Die Methode wird von der doEvent() Methode von
     * Räumen direkt aufgerufen.
     * 
     * @param r
     *            ein Room Objekt
     * @return true if RandomSuccess == false, else random true or false
     */
    public boolean execute(Room r) {
        if (this.roomobjects != null) {
            for (Iterator roomIter = roomobjects.iterator(); roomIter.hasNext();) {
                String element = (String) roomIter.next();
                r.getFromPool(element);
            }
        }

        /*
         * actually, there are two ways this method could be called, either by a GameObject
         * event handler or by a room event handler. 
         * If it is called by a GameObject handler it is called with the current room of the
         * player, otherwise it is called with the room the event is handled in, so the
         * player sayings belong here:
         */
        
        for (Iterator sayingIter = playerSayings.iterator(); sayingIter.hasNext();) {
            String element = (String) sayingIter.next();
            if (Engine.player.getCurrentRoom().equals(r)) {
                Engine.player.say(element);
            }
        }

        execute();
        
        if (this.randomSuccess) {
            if (new Random().nextBoolean()) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }

    }

	/**
	 * Interne Methode zum überprüfen auf Null
	 * @param  foo ein Object
	 * @return	foo != null
	 */
	public boolean notNull(Object foo) {
		return foo != null;
	}
	
	/**
	 * Interne Methode zum überprüfen ob ein GameObject ein Character Objekt ist
	 * 
	 * @param foo	ein GameObject
	 * @return			true falls es ein CharacterObject ist, false sonst
	 */
	public boolean isCharacter(GameObject foo) {
		return foo.isCharacter();
	}
	
	public void setOwner(String name) {
	    this.owner = name;
	    if (targets != null) {
	        targets.setCreator(name);
	    }
	}
	
}
