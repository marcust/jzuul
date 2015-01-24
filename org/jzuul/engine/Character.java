/*
 * 	CVS: $Id: Character.java,v 1.22 2004/07/25 19:07:20 marcus Exp $
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
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jdom.Element;
import org.jzuul.engine.gui.GuiInterface;
import org.jzuul.engine.rooms.*;

/**
 * Ein Character ist ein interaktives GameObject im Sinne eines Lebewesens
 * 
 * 
 * @version $Revision: 1.22 $
 */
public class Character extends GameObject {

    /**
     * Enthält die Dialoge die ein Charakter führen kann
     */
    private List dialogs;

    /**
     * Die Nummer des aktuellen Dialogs
     */
    private int currentDialog = 0;

    /**
     * Das Inventar des Charakters
     */
    private Inventory inv;

    /**
     * enthält den aktuellen Room in dem sich der Character aufhält
     */
    protected Room currentRoom;

    /**
     * enthält das aktuell aktive Dialog Objekt
     */
    protected Dialog dialog;

    /**
     * für diverse Zwecke ein Random Object
     */
    private Random r;

    /**
     * Instanzieiert einen character
     * 
     * @param name
     *            Der Name des Charakters
     */
    public Character(String name) {
        super(name);
        r = new Random();
        inv = new Inventory(Inventory.UNLIMITED_INVENTORY);
        this.takeable = false;
        this.useable = false;
    }

    /**
     * Führt eine Aktion aus (z.B moveRandom, leaveRoom) Die Namen für die
     * Aktionen sind im gamefile.dtd definiert
     * 
     * @param actionName
     *            der Name der Aktion
     */
    public void doAction(String actionName) {
        Engine.debug(this.getName() + ": " + actionName, 2); //$NON-NLS-1$
        if (actionName.equals("moveRandom")) { //$NON-NLS-1$
            moveRandom();
        }
        if (actionName.equals("leaveRoom")) { //$NON-NLS-1$
            this.leaveRoom();
        }
    }

    /**
     * Führt eine Bewegung eines Characters in eine mögliche Himmelsrichtung in
     * Abhängigkeit von einer Zufallsvariable aus.
     */
    protected void moveRandom() {
        if (r.nextBoolean()) { return; }
        this.leaveRoom();
    }

    /**
     * Sorgt dafür das der Character den Raum verlässt.
     *  
     */
    protected void leaveRoom() {
        if (this.currentRoom == null) { return; }

        List rooms = currentRoom.getWays();
        Object[] formatArgs = { Helpers.firstToUpper(this.getName()) };
        if (inRoomWithPlayer()) {
            Engine.gui.printlnI(MessageFormat.format(Messages.getString("CHARACTER_LEAVES_ROOM"),formatArgs)); //$NON-NLS-1$
        }
        currentRoom.getContent().deleteGameObject(this.getName());
        this.currentRoom = (Room) rooms.get(new Random().nextInt(rooms.size()));
        currentRoom.getContent().addGameObject(this);
        if (inRoomWithPlayer()) {
            Engine.gui.printlnI(MessageFormat.format(Messages.getString("CHARACTER_ENTERS_ROOM"), formatArgs)); //$NON-NLS-1$
        }
    }

    /**
     * Sagt etwas im Namen des Characters wenn der Charakter sich im selben Raum
     * wie der Spieler befindet.
     * 
     * @param something
     *            Das was gesagt werden soll
     */
    public void say(String something) {
        if ((inRoomWithPlayer() || (this.currentRoom == null)) && (something != null)& (!something.equals("")) ) { //$NON-NLS-1$
            Engine.gui.println(Helpers.firstToUpper(this.getName()) + ": " + something, GuiInterface.BLUE); //$NON-NLS-1$
        } else {
            Engine.debug(this.getName() + " won't say something", 1); //$NON-NLS-1$
        }
    }

    /**
     * Stellt fest ob der Character sich im selben Room wie der Spieler befindet
     * 
     * @return true wenn der Character im selben Room wie der Spieler ist, sonst
     *         false
     */
    public boolean inRoomWithPlayer() {
        if (this.currentRoom == null) {
            // I'm taken!
            return false;
        }
        return this.currentRoom.getName().equals(Engine.player.getCurrentRoom().getName());
    }

    /**
     * Setzt den aktiven Room für den Character
     * 
     * @param newRoom
     *            der Room in dem sich der Player ist
     */
    public void setCurrentRoom(Room newRoom) {
        this.currentRoom = newRoom;
    }

    /**
     * Wird von dem Befehl "talk" aufgerufen uns sollte zu einem Dialog führen
     * 
     * @return true bei erfolg des Dialoges, false otherwise
     */
    public boolean talkTo() {
        if (this.dialog == null) {
            if ((this.dialogs != null) && (this.dialogs.get(currentDialog) != null)
                    && checkPreconditions(currentDialog)) {
                this.dialog = (Dialog) dialogs.get(currentDialog);
                if (this.dialog.talk()) {
                    return true;
                } else {
                    this.say(Messages.getString("CHARACTER_WONT_TALK")); //$NON-NLS-1$
                    return false;
                }
            } else {
                this.say(Messages.getString("CHARACTER_WONT_TALK")); //$NON-NLS-1$
                return false;
            }
        } else {
            return this.dialog.talk();
        }
    }

    /**
     * Fordert den Character auf etwas zu nehmen. Die aufrufende Methode ist
     * dafür zuständig den Gegenstand zu entfernen
     * 
     * @param obj
     *            Das GameObject was genommen werden soll
     * @return true falls der Gegenstand genommen wurde, false sonst
     */
    public boolean take(GameObject obj) {
        if (isPrecondition(obj.getName()) 
                || Engine.player.getTargetList().isGiveTarget(this.getName(), obj.getName())) {
            this.say(Messages.getString("CHARACTER_THANKS")); //$NON-NLS-1$
            inv.addGameObject(obj);
            Engine.player.findAndDeleteGameObject(obj.getName());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gibt das Objekt als JDOM Element XML zurück.
     * 
     * @return den Character als JDOM XML Element
     */
    public Element toElement() {
        Element characterElement = new Element("character"); //$NON-NLS-1$
        characterElement.setAttribute("name", this.getName()); //$NON-NLS-1$
        characterElement.setAttribute("dialog", String.valueOf(this.currentDialog + 1)); //$NON-NLS-1$
        return characterElement;
    }

    /**
     * Weist dem Charakter seine Dialoge zu
     * 
     * @param dialogs
     *            eine Liste von Dialog Objekten
     */
    public void addDialogs(List dialogs) {
        for (Iterator i = dialogs.iterator(); i.hasNext();) {
            Dialog element = (Dialog) i.next();
            element.setCharacter(this);
        }
        this.dialogs = dialogs;
    }

    /**
     * Wird von dem AuswahlListener aufgerufen nachdem der Spieler eine
     * DialogObject ausgewählt hat
     * 
     * @param type
     *            Ein Event.DIALOG_ wert
     */
    public void pushAnswer(int type) {
        this.doEvent(type);
        Object[] formatArgs = { Helpers.firstToUpper(this.name) };
        switch (type) {
        case Event.DIALOG_CONTINUE:
            talkTo();
            break;
        case Event.DIALOG_END_FAILURE:
            Engine.gui.setDefaultActionListener();
            Engine.gui.println(MessageFormat.format(Messages.getString("CHARACTER_ENDS_DIALOG"),formatArgs)); //$NON-NLS-1$
            this.dialog.resetPhase();
            break;
        // das ist absicht:
        case Event.DIALOG_END_SUCCESS:
        case Event.DIALOG_CUSTOM_RESULT_1:
        case Event.DIALOG_CUSTOM_RESULT_2:
        case Event.DIALOG_CUSTOM_RESULT_3:
            Engine.gui.setDefaultActionListener();
            Engine.gui.println(MessageFormat.format(Messages.getString("CHARACTER_ENDS_DIALOG"),formatArgs)); //$NON-NLS-1$
            this.dialog = null;
            this.currentDialog++;
            break;
        default:
            Engine.gui.setDefaultActionListener();
            Engine.gui.println(MessageFormat.format(Messages.getString("CHARACTER_ENDS_DIALOG"),formatArgs)); //$NON-NLS-1$
        }
    }

    /**
     * Überprüft die Vorbedingungen für einen Dialog
     * 
     * @param dialogNr
     *            die Dialognummer für den die Vorbedingungen überpüft werden
     *            sollen
     * @return true wenn die Vorbedingungen erfüllt sind, false otherwise
     */
    protected boolean checkPreconditions(int dialogNr) {
        if (dialogs.size() >= dialogNr) {
            Dialog d = (Dialog) dialogs.get(dialogNr);
            List precons = d.getPreconditions();
            if (precons == null) { return true; }
            for (Iterator iter = precons.iterator(); iter.hasNext();) {
                String element = (String) iter.next();
                if (!inv.containsGameObject(element)) { return false; }
            }
        }
        return true;
    }

    /**
     * Überprüft ob das GameObject mit dem Namen objName eine Dialogvorbedingung
     * ist.
     * 
     * @param objName
     *            der Name des Objekts das überpüft werden
     * @return true wenn das spezifizierte Objekt eine Vorbedingung darstellt,
     *         false otherwise
     */
    protected boolean isPrecondition(String objName) {
        if ((dialogs != null) && (dialogs.size() > currentDialog)) {
            Dialog d = (Dialog) dialogs.get(currentDialog);
            List precons = d.getPreconditions();
            if (precons == null) { return true; }
            return precons.contains(objName);
        } else {
            return false;
        }

    }

    /**
     * Setzt die aktuelle Dialognummer
     * 
     * @param currentDialog
     *            die neue Dialognummer
     */
    public void setDialog(int currentDialog) {
        this.currentDialog = currentDialog;
    }

    /**
     * Führt das event mit der Id eventId aus. Spezifiziert folgende Defaults:
     * Event.TAKEUP: setzt den aktullen Raum auf null Event.DROP: setzt den
     * aktuellen Raum auf den aktuellen Raum des Spielers
     * 
     * @param eventId
     *            eine Event Id
     */
    public void doEvent(int eventId) {
        super.doEvent(eventId);
        switch (eventId) {
        case Event.TAKEUP:
            this.setCurrentRoom(null);
            break;
        case Event.DROP:
            this.setCurrentRoom(Engine.player.getCurrentRoom());
            break;
        }

    }

    /**
     * Erstellt eine 1 zu 1 Kopie des Character Objektes
     * 
     * @return eine Kopie des Character Objekt
     */
    public GameObject copy() {
        Character newChar = new Character(this.getName());
        super.cloneInto(newChar);
        newChar.inv = this.inv.copy();
        newChar.currentDialog = this.currentDialog;
        // NOTE Again, not really save:
        newChar.dialogs = this.dialogs;
        newChar.currentRoom = this.currentRoom;
        newChar.dialog = this.dialog;
        return newChar;
    }
    
    public boolean isCharacter() {
        return true;
    }

}