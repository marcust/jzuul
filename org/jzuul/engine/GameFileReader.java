/*
 * 	CVS: $Id: GameFileReader.java,v 1.30 2004/07/25 19:07:20 marcus Exp $
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

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.jzuul.engine.exceptions.ConnectAllRoomsFailed;
import org.jzuul.engine.exceptions.NoSuchGameObjectException;
import org.jzuul.engine.exceptions.NoSuchRoomException;
import org.jzuul.engine.rooms.*;

import org.jdom.*;
import org.jdom.input.*;

/**
 * Diese Klasse benutzt JDOM um eine JZuul Gamefile zu parsen.
 * 
 * 
 * @version $Revision: 1.30 $
 */
public class GameFileReader {

    /**
     * Die vordefinierte Größe eines Raum Inventars
     */
    private final static int ROOM_INVENTORY_SIZE = Inventory.UNLIMITED_INVENTORY;

    /**
     * Die vordefinierte Größe des Inventars eines Spielers (10)
     */
    private final static int PLAYER_INVENTORY_SIZE = 10;

    /**
     * Die aus der Datei geladene Karte
     */
    private GameMap map;

    /**
     * Der aus der Datei geladene Spieler
     */
    private Player player;

    /**
     * Die aus der Datei geladene Befehlshistorie
     */
    private List history;

    /**
     * Die Dialog die aus der Datei geladen werden
     */
    private Map dialogs;

    /**
     * Die Character Objekte die aus der Datei geladen werden
     */
    private Map characters;

    /**
     * Die Item Objekte die aus der Datei geladen werden
     */
    private Map items;

    /**
     * Ein hilfs-Stack der Benutzt wird um zu Überprüfen ob alle in der Datei
     * erwähnten GameObjects auch existieren
     */
    private Stack objCheckStack;

    /**
     * Enthällt das description Feld aus dem Toplevel des gamefiles
     */
    private String gameDescription;

    private Element root;

    public GameFileReader() {
        objCheckStack = new Stack();
    }

    /**
     * Erstell einen neues GameFileReader Objekt das auf jeden Fall den DTD File
     * und die Karte "default" lädt.
     * 
     * @param resource
     *            der Stream aus dem gelesen werden soll
     * @throws IOException
     *             bei IO Fehlern
     * @throws JDOMException
     *             bei XML Fehlern
     */
    public GameFileReader(InputStream resource) throws IOException, JDOMException {
        this(resource, "default", true);
    }

    /**
     * Erstellt ein neues GameFileReader Objekt das die Karte "default" lädt
     * 
     * @param resource
     *            der Stream der die XML Datei enthällt
     * @param loaddtd
     *            Soll der DTD File geladen werden?
     * @throws IOException
     *             Bei IO Fehlern
     * @throws JDOMException
     *             Bei XML Fehlern
     */
    public GameFileReader(InputStream resource, boolean loaddtd) throws IOException, JDOMException {
        this(resource, "default", loaddtd);
    }

    /**
     * Erstell ein neues GameFileReader Objekt
     * 
     * @param resource
     *            der Stream der die XML Datei enthällt
     * @param mapname
     *            der Name der Karte die geladen werden soll
     * @param loaddtd
     *            Soll der DTD File geladen werden
     * @throws IOException
     *             Bei IO Fehlern
     * @throws JDOMException
     *             Bei XML Fehlern
     */
    public GameFileReader(InputStream resource, String mapName, boolean loaddtd) throws IOException, JDOMException {
        this();

        // Build the document with SAX and Xerces
        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", false);

        if (!loaddtd) {
            Engine.debug("Dtd loading disabled", 4);
            builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } else {
            Engine.debug("Dtd loading enabled", 4);
        }

        InputStreamReader reader = new InputStreamReader(resource, Charset.forName("UTF-8"));
        try {
            Document doc = builder.build(reader);
            Element e = doc.getRootElement();
            parseTree(e);
        } catch (JDOMException e) {
            throw e;
        }

    }

    public void parseTree(Element e) {
        if (e == null) return;
        this.root = e;
        this.gameDescription = descriptionHandler(e.getChild("description"));

        Element go = e.getChild("gameobjects");
        if (go != null) {
            this.characters = characterHandler(go.getChild("characters"));
            this.items = itemsHandler(go.getChild("items"));
            this.checkObjects();
        }

        this.history = historyHandler(e.getChild("history"));
        this.player = playerHandler(e.getChild("player"));
        this.dialogs = dialogHandler(e.getChild("dialogs"));
    }

    /**
     * Wird benutzt um den Kartenteil der Datei zu parsen.
     * 
     * @param root
     *            das Element <map>aus dem Gamefile
     * @param name
     *            der Name der Karte die geladen werden soll
     * @return ein verifizierte GameMap bei Erfolg, eine leere GameMap bei
     *         fehlern
     */
    protected GameMap mapHandler(Element root, String name) throws NoSuchRoomException, ConnectAllRoomsFailed {

        GameMap map = new GameMap(name);
        map.setStartRoom(root.getAttributeValue("startroom"));

        List rooms = new Vector(root.getChildren("room"));
        for (Iterator iterRooms = rooms.iterator(); iterRooms.hasNext();) {
            //hier bin ich für jeden raum einmal

            Element roomElement = (Element) iterRooms.next();
            String typeOfRoom = roomElement.getAttributeValue("class");
            String nameOfRoom = roomElement.getAttributeValue("name");

            map.addRoom(nameOfRoom, roomElement.getChildText("description"), typeOfRoom);

            // image stream hinzufügen:
            Element imageEl = roomElement.getChild("image");
            if (imageEl != null) {
                String imageName = imageEl.getAttributeValue("file");
                if (imageName != null && !imageName.equals("")) {
                InputStream imageStream = this.getClass().getResourceAsStream(imageName);
                if (imageStream == null) {
                    try {
                        imageStream = new FileInputStream(new File(imageName));
                    } catch (FileNotFoundException e) {
                        Engine.debug("Image name is " + imageName,1);
                        e.printStackTrace();
                    } 
                }
                map.setRoomImageStream(nameOfRoom, imageStream);
                }
            }

            //wege hinzufügen
            List ways = new Vector(roomElement.getChildren("ways"));
            if (!ways.isEmpty()) {
                Element waysElement = (Element) ways.get(0);
                List way = new Vector(waysElement.getChildren());
                for (Iterator iterWay = way.iterator(); iterWay.hasNext();) {
                    Element wayElement = (Element) iterWay.next();
                    String wayDirection = wayElement.getAttributeValue("direction");
                    String wayRoom = wayElement.getAttributeValue("room");

                    //don't create connections here, just enque them for later
                    // (when all rooms are added)
                    map.enqueueWay(wayRoom, Directions.directionToInt(wayDirection), nameOfRoom);
                }

            }

            //views hinzufügen
            List views = new Vector(roomElement.getChildren("views"));
            if (!views.isEmpty()) {
                Element viewsElement = (Element) views.get(0);
                List view = new Vector(viewsElement.getChildren());
                for (Iterator iterView = view.iterator(); iterView.hasNext();) {
                    Element viewElement = (Element) iterView.next();
                    String viewDirection = viewElement.getAttributeValue("direction");
                    map.setWayDescription(nameOfRoom, Directions.directionToInt(viewDirection), viewElement.getText());

                }
            }

            // contents:
            Element content = roomElement.getChild("contents");
            if (content != null) {
                Inventory inv = createInventory(content, ROOM_INVENTORY_SIZE, map.getRoom(nameOfRoom));

                map.addInvToRoom(nameOfRoom, inv);
            }

            // events
            List events = roomElement.getChildren("event");
            setEventListenerEvents(map.getRoom(nameOfRoom), events);

        }
        // get the transition room
        List transrooms = root.getChildren("transitionroom");
        for (Iterator transRoomIter = transrooms.iterator(); transRoomIter.hasNext();) {
            Element transroom = (Element) transRoomIter.next();

            if (transroom != null) {
                String nameOfRoom = transroom.getAttributeValue("name");

                // get preconditions
                List preconditions = transroom.getChildren("precondition");
                String[] objNames = new String[preconditions.size()];
                int i = 0;
                for (Iterator iter = preconditions.iterator(); iter.hasNext();) {
                    Element element = (Element) iter.next();
                    objNames[i++] = element.getAttributeValue("item");
                }

                // get ways
                List ways = new Vector(transroom.getChildren("ways"));
                if (!ways.isEmpty()) {
                    Element waysElement = (Element) ways.get(0);
                    List way = new Vector(waysElement.getChildren());
                    for (Iterator iterWay = way.iterator(); iterWay.hasNext();) {
                        Element wayElement = (Element) iterWay.next();
                        String wayDirection = wayElement.getAttributeValue("direction");
                        String wayRoom = wayElement.getAttributeValue("room");

                        //don't create connections here, just enque them for
                        // later
                        // (when all rooms are added)
                        map.enqueueWay(wayRoom, Directions.directionToInt(wayDirection), nameOfRoom);
                    }

                }
                boolean isFinal = transroom.getAttributeValue("final").equals("true");

                map.addTransRoom(nameOfRoom, objNames, isFinal, transroom.getAttributeValue("target"));
            }
        }
        //new create Connections
        map.processQueue();

        //now verify the map
        //map.verifyMap();
        return map;

    }

    /**
     * Wird benutzt um den history Teil der Datei zu laden
     * 
     * @param root
     *            das <history>Element
     * @return eine Liste von n (0 <=n) Befehlen bei Erfolg, null bei fehlern
     */
    protected List historyHandler(Element root) {
        List history = new Vector();
        if (root == null) return null;
        List historyElements = new Vector(root.getChildren());
        for (Iterator iterHistory = historyElements.iterator(); iterHistory.hasNext();) {
            Element historyElement = (Element) iterHistory.next();
            String command = historyElement.getText();
            history.add(command);
        }
        return history;
    }

    /**
     * Wird benutzt um den Player aus der Datei zu laden
     * 
     * @param root
     *            das <player>Element
     * @return ein Spielerobjekt
     */
    protected Player playerHandler(Element root) {
        Player player = new Player();
        if (map != null) {
            player.setCurrentRoom(map.getRoom(map.getStartRoom()));
            String startraum = root.getAttributeValue("position");
            if (startraum != null) {
                player.setCurrentRoom(map.getRoom(startraum));
            }
            String name = root.getAttributeValue("name");
            if (name != null) {
                player.setName(name);
            }

        }

        //parse contents!
        if (root != null) {
            Element contents = root.getChild("contents");
            if (contents != null) {
                Inventory inv = createInventory(contents, PLAYER_INVENTORY_SIZE, null);
                player.setInv(inv);
            }
            List targets = root.getChildren("target");
            if (!targets.isEmpty()) {
                TargetList targetList = createTargetList(targets);
                player.setTargetList(targetList);
            }

        }
        return player;
    }

    /**
     * Wird benutzt um ein Inventar aus einem <contents>Block zu erstellen
     * 
     * @param contentsElement
     *            der <contents>Block
     * @param size
     *            die Größe des Inventars
     * @param currentRoom
     *            der aktuelle Raum, entweder ein Raum falls ein Rauminventar
     *            aufgebaut wird oder der Raum des Spielers
     * @return ein Inventar der Größe size mit allen in dem Block beschreibenen
     *         Objekten
     */
    protected Inventory createInventory(Element contentsElement, int size, Room currentRoom) {
        if (contentsElement == null)
                throw new IllegalArgumentException("createInventory was called with contentsElement==null");

        Inventory inv = new Inventory(size);
        // items hinzufügen

        List items = new Vector(contentsElement.getChildren("item"));
        for (Iterator iterItems = items.iterator(); iterItems.hasNext();) {
            Element itemElement = (Element) iterItems.next();
            String itemName = itemElement.getAttributeValue("name");
            Engine.debug("Starting with " + itemName, 1);
            inv.addGameObject(((GameObject) this.items.get(itemName)).copy());
        }

        //character hinzufügen
        List characters = new Vector(contentsElement.getChildren("character"));
        for (Iterator iterCharacter = characters.iterator(); iterCharacter.hasNext();) {
            Element characterElement = (Element) iterCharacter.next();
            String characterName = characterElement.getAttributeValue("name");
            String characterDialog = characterElement.getAttributeValue("dialog");
            int dialogNrInt = 1;
            if (characterDialog != null) {
                dialogNrInt = Integer.parseInt(characterDialog);
            }
            Character newChar = (Character) this.characters.get(characterName);
            if (newChar == null) { throw new NoSuchGameObjectException("Could not get character " + characterName); }
            newChar.setDialog(dialogNrInt - 1);
            newChar.setCurrentRoom(currentRoom);
            if ((dialogs != null) && dialogs.containsKey(newChar.getName())) {
                newChar.addDialogs((Vector) dialogs.get(newChar.getName()));
            }
            inv.addGameObject(newChar);
        }
        return inv;

    }

    /**
     * Zugriff auf die geladene History
     * 
     * @return eine Liste von Strings (Befehle)
     */
    public List getHistory() {
        return history;
    }

    /**
     * Zugriff auf das geladene Kartenobjekt
     * 
     * @return ein GameMap Objekt
     */
    public GameMap getMap(String mapname) throws ConnectAllRoomsFailed, NoSuchRoomException {
        List tmp = root.getChildren("map");
        for (Iterator i = tmp.iterator(); i.hasNext();) {
            Element element = (Element) i.next();
            if (element.getAttributeValue("name").equals(mapname)) {
                this.map = mapHandler(element, mapname);
            }
        }
        checkObjects();
        return map;
    }

    /**
     * Zugriff auf den geladenen Spieler
     * 
     * @return ein Player Objekt
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Lädt die Dialoge aus der Datei
     * 
     * @param root
     *            das <dialog>Element
     * @return eine Map die Character Objekt Namen auf Dialog Objekte abbildet
     */
    private Map dialogHandler(Element root) {
        HashMap characterDialogs = new HashMap();
        if (root == null) { return null; }
        List persons = root.getChildren("dialog-owner");
        for (Iterator i = persons.iterator(); i.hasNext();) {
            Element p = (Element) i.next();
            Vector dialogObj = new Vector();
            List dialogs = p.getChildren("dialog");
            for (Iterator i1 = dialogs.iterator(); i1.hasNext();) {
                Element dialogElement = (Element) i1.next();
                Dialog newDialog = new Dialog();
                List precons = dialogElement.getChildren("precondition");
                for (Iterator i2 = precons.iterator(); i2.hasNext();) {
                    Element precon = (Element) i2.next();
                    newDialog.addPrecondition(precon.getAttributeValue("item"));
                }
                List phases = dialogElement.getChildren("phase");
                int phaseCounter = 1;
                for (Iterator i3 = phases.iterator(); i3.hasNext();) {
                    Element phaseElement = (Element) i3.next();
                    List objects = phaseElement.getChildren("object");
                    for (Iterator i4 = objects.iterator(); i4.hasNext();) {
                        Element objElement = (Element) i4.next();
                        String nextPhase = objElement.getAttributeValue("nextphase");
                        int nextPhaseInt = 0;
                        if (nextPhase != null && nextPhase != "") {
                            nextPhaseInt = Integer.parseInt(nextPhase);
                        }
                        newDialog.addDialog(phaseCounter, Integer.parseInt(objElement.getAttributeValue("id")),
                                objElement.getChildText("say"), objElement.getChildText("reply"), Event
                                        .fromString(objElement.getAttributeValue("type")), nextPhaseInt);
                    }
                    List references = phaseElement.getChildren("reference");
                    for (Iterator i5 = references.iterator(); i5.hasNext();) {
                        Element referenceElement = (Element) i5.next();
                        newDialog.addDialog(phaseCounter, Integer.parseInt(referenceElement.getAttributeValue("id")));
                    }
                    phaseCounter++;
                }
                dialogObj.add(newDialog);
            }
            characterDialogs.put(p.getAttributeValue("name"), dialogObj);
        }
        return characterDialogs;
    }

    /**
     * Lädt die Character Objekte aus der Datei
     * 
     * @param characters
     *            das <characters>Element einer Datei
     * @return eine Map die Character Objekt Namen auf Character Objekte
     *         abbildet
     */
    private Map characterHandler(Element characters) {
        HashMap newCharacters = new HashMap();
        if (characters == null) { return null; }
        List persons = characters.getChildren("person");
        for (Iterator personIter = persons.iterator(); personIter.hasNext();) {
            Element personElement = (Element) personIter.next();
            Character newChar = new Character(checkLater(personElement.getAttributeValue("name")));
            newChar.setDescription(personElement.getChildText("description"));
            setGameObjectProperties(newChar, personElement.getChildren("property"));
            setEventListenerEvents(newChar, personElement.getChildren("event"));

            newCharacters.put(newChar.getName(), newChar);
        }
        return newCharacters;
    }

    /**
     * Wird benutzt um den <items>Teil einer Datei zu verarbeiten
     * 
     * @param items
     *            das <items>Element einer Datei
     * @return eine Map die Item Objekt Namen auf Item Objekte abbildet
     */
    private Map itemsHandler(Element items) {
        HashMap newItems = new HashMap();
        if (items == null) { return null; }
        List gameitems = items.getChildren("gameitem");
        for (Iterator gameItemIter = gameitems.iterator(); gameItemIter.hasNext();) {
            Element gameItemElement = (Element) gameItemIter.next();
            Item newItem = new Item(checkLater(gameItemElement.getAttributeValue("name")));
            newItem.setDescription(gameItemElement.getChildText("description"));
            setGameObjectProperties(newItem, gameItemElement.getChildren("property"));
            setEventListenerEvents(newItem, gameItemElement.getChildren("event"));
            Element combinations = gameItemElement.getChild("combinations");

            // this handles the with-object events
            if (combinations != null) {
                List withObjects = combinations.getChildren("with-object");
                for (Iterator withObjectsIter = withObjects.iterator(); withObjectsIter.hasNext();) {
                    Element withObjelement = (Element) withObjectsIter.next();

                    EventHandler newEventHandler = createEventHandlerFrom(withObjelement.getChild("actions"));

                    newItem.addWithObject(withObjelement.getAttributeValue("name"), newEventHandler);
                }

            }
            Engine.debug("Added item " + newItem.getName(), 1);
            newItems.put(newItem.getName(), newItem);
        }
        return newItems;
    }

    /**
     * Wird benutzt um eine Liste von Eigenschafen in einem GameObject Object zu
     * ändern
     * 
     * @param obj
     *            das GameObject Objekt dessen eigenschaften geändert werden
     *            sollen
     * @param properties
     *            die <property>Elemente aus der XML Datei
     */
    private void setGameObjectProperties(GameObject obj, List properties) {
        for (Iterator propertyIter = properties.iterator(); propertyIter.hasNext();) {
            Element propertyElement = (Element) propertyIter.next();
            String name = propertyElement.getAttributeValue("name");
            if (name.equals("takeable")) {
                obj.setTakeable(propertyElement.getAttributeValue("value").equals("true"));
            }
            if (name.equals("useable")) {
                obj.setUsability(propertyElement.getAttributeValue("value").equals("true"));
            }
            if (name.equals("size")) {
                obj.setSize(Integer.parseInt(propertyElement.getAttributeValue("value")));
            }
            if (name.equals("deleteonuse")) {
                obj.setDeleteOnUse(propertyElement.getAttributeValue("value").equals("true"));
            }
        }
    }

    /**
     * Wird verwendet um aus einem <actions>Teil der Datei einen EventHandler zu
     * erzeugen.
     * 
     * @param actionsElement
     *            Ein <actions>Element aus der XML Datei
     * @return einen EventHandler
     * @see org.jzuul.engine.EventHandler
     */
    private EventHandler createEventHandlerFrom(Element actionsElement) {
        EventHandler e = new EventHandler();
        if (actionsElement == null) return null;

        // setup player saying
        List eventSaying = actionsElement.getChildren("player-say");
        for (Iterator sayingIter = eventSaying.iterator(); sayingIter.hasNext();) {
            Element sayingElement = (Element) sayingIter.next();
            e.addPlayerSaying(sayingElement.getText());
        }

        // setup player saying
        List eventNpcSaying = actionsElement.getChildren("npc-say");
        for (Iterator sayingIter = eventNpcSaying.iterator(); sayingIter.hasNext();) {
            Element sayingElement = (Element) sayingIter.next();
            e.addNpcSaying(sayingElement.getText());
        }

        // setup actions
        List eventActions = actionsElement.getChildren("action");
        for (Iterator actionIter = eventActions.iterator(); actionIter.hasNext();) {
            Element element = (Element) actionIter.next();
            e.addAction(element.getAttributeValue("type"));
        }

        List eventTargets = actionsElement.getChildren("target");
        if (eventTargets != null) e.setTargets(createTargetList(eventTargets));

        HashMap properties = new HashMap();

        List alterations = actionsElement.getChildren("alter-item");
        for (Iterator alterationsIter = alterations.iterator(); alterationsIter.hasNext();) {
            Element alterElement = (Element) alterationsIter.next();
            properties.put(alterElement.getAttributeValue("property"), alterElement.getText());
        }
        e.setPropertyChanges(properties);

        List invElements = actionsElement.getChildren("inventory-item");
        for (Iterator invIter = invElements.iterator(); invIter.hasNext();) {
            Element element = (Element) invIter.next();
            e.addInvobject(checkLater(element.getText()));
        }

        List roomElements = actionsElement.getChildren("room-item");
        for (Iterator roomIter = roomElements.iterator(); roomIter.hasNext();) {
            Element element = (Element) roomIter.next();
            e.addRoomobject(checkLater(element.getText()));
        }

        List deleteElements = actionsElement.getChildren("delete-item");
        for (Iterator deleteIter = deleteElements.iterator(); deleteIter.hasNext();) {
            Element element = (Element) deleteIter.next();
            e.addDeletition(checkLater(element.getText()));
        }

        Element randomSuccessElement = actionsElement.getChild("random-success");
        if (randomSuccessElement != null) {
            e.setRandomSuccess(true);
        }
        return e;

    }

    /**
     * Wird verwendet um einem EventListener Objekt einen EventHandler
     * zuzuweisen
     * 
     * @param obj
     *            das EventListener Objekt dem der Handler zugewiesen werden
     *            soll
     * @param events
     *            Eine Liste von <event>Elementen aus der XML Datei
     */
    private void setEventListenerEvents(EventListener obj, List events) {
        for (Iterator eventIter = events.iterator(); eventIter.hasNext();) {
            Element eventElement = (Element) eventIter.next();
            String eventName = eventElement.getAttributeValue("name");
            Engine.debug("Added event handler for event " + eventName + " (" + Event.fromString(eventName) + ")", 1);
            EventHandler e = createEventHandlerFrom(eventElement.getChild("actions"));
            obj.setHandler(eventName, e);
        }
    }

    /**
     * Zugriff auf die Liste aller GameObject Objekte in der Datei
     * 
     * @return eine Liste von GameObject Objekten die aus der Datei gelesen
     *         wurden
     */
    public Map getObjectPool() {
        HashMap objectPool = new HashMap();
        if (this.characters != null) objectPool.putAll(this.characters);
        if (this.items != null) objectPool.putAll(this.items);
        return objectPool;

    }

    /**
     * Enqueued den Namen eines Objektes um später zu überprüfen ob dieses auch
     * erstellt wurde
     * 
     * @param objName
     *            der Name eines Objekte
     * @return das gleiche wie objName um einen einfachen wrapper zu haben
     */
    public String checkLater(String objName) {
        if ((objName == null) || (objName.equals("")))
                throw new IllegalArgumentException("You cannot have empty GameObject names");
        objCheckStack.push(objName.toLowerCase());
        Engine.debug(objName, 1);
        return objName.toLowerCase();
    }

    /**
     * Überprüft ob für alle GameObject Namen in objCheckStack auch ein Objekt
     * existiert
     * 
     * @throws NoSuchGameObjectException
     *             falls ein Name zu einem nicht existierenden Objekt in der
     *             Liste ist
     */
    public void checkObjects() {
        Map pool = getObjectPool();
        for (Iterator iter = objCheckStack.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            if ((element != null) && !pool.containsKey(element))
                    throw new NoSuchGameObjectException("GameObject " + element + " does not exist!");
        }

    }

    /**
     * Läd ein Savegame Diese Methode lädt zusätzlich zu dem schon gelesenen
     * Gamefile eine weiter Datei und überschreibt die Karte und den Spieler mit
     * dem in der Datei vorhandenen. Da in einem Savegame nicht die ganzen
     * Objekte und ihre Eigenschaften gespeichert werden ist dies notwendig,
     * damit nicht nicht existierende Objekte benutzt werden.
     * 
     * @param filename
     *            der Dateiname des Savegames
     * @param loaddtd
     *            soll der DTD File geladen werden?
     * @return true on success, false on failure
     */
    public boolean readFromSavegame(String filename, boolean loaddtd) {
        SAXBuilder builder = new SAXBuilder(true);

        // This only works if xerces is used, read the JDOM faq for the reason
        if (!loaddtd) {
            builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        }

        try {
            FileInputStream is = new FileInputStream(new File(filename));
            if (is != null) {
                InputStreamReader reader = new InputStreamReader(is, Charset.forName("UTF-8"));

                Document doc = builder.build(reader);
                Element e = doc.getRootElement();
                this.root = e;
                this.map = mapHandler(e.getChild("map"), "default");
                this.player = playerHandler(e.getChild("player"));

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Erstellt ein TargetList Objekt aus einer <target>Elementliste in der
     * Datei
     * 
     * @param targetElements
     *            eine Liste von <target>Elementen aus der Datei
     * @return ein TargetList Objekt das die TargetObject Objekte enthällt die
     *         in der Liste definiert sind
     */
    private TargetList createTargetList(List targetElements) {
        TargetList newList = new TargetList();

        for (Iterator targetIter = targetElements.iterator(); targetIter.hasNext();) {
            Element targetElement = (Element) targetIter.next();
            TargetObject newObj = createTargetObject(targetElement);
            if (newObj != null) {
                newList.addTarget(newObj);
            }
        }
        return newList;
    }

    /**
     * Erstell ein TargetObject Objekt aus einem <target>Element in der Liste
     * 
     * @param targetElement
     *            ein <target>Element
     * @return das daraus erstellte TargetObject Objekt
     */
    public TargetObject createTargetObject(Element targetElement) {
        String description = targetElement.getChildText("description");
        Engine.debug("Description is " + description, 1);
        Element condition = targetElement.getChild("condition");
        Element creator = targetElement.getChild("creator");
        String creatorName = "initial";
        if (creator != null) {
            creatorName = creator.getAttributeValue("name");
        }
        if (condition != null) {
            return new TargetObject(TargetObject.actionTypeFromString(condition.getAttributeValue("type")), condition
                    .getAttributeValue("value"), description, creatorName);
        } else {
            return null;
        }
    }

    /**
     * Holt die Spielbeschreibung aus dem Gamefile
     * 
     * @param root
     *            ein <description>Element aus dem Gamefile
     * @return den Inhalt des Elements
     */
    private String descriptionHandler(Element root) {
        if (root != null) { return root.getText(); }
        return null;
    }

    /**
     * Zugriff auf die Spielbeschreibung
     * 
     * @return die Spielbeschreibung aus dem Gamefile
     */
    public String getDescription() {
        return this.gameDescription;
    }

}