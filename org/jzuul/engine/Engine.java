/*
 * 	CVS: $Id: Engine.java,v 1.42 2004/08/16 12:15:30 marcus Exp $
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

package org.jzuul.engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.jzuul.engine.commands.Command;
import org.jzuul.engine.exceptions.ConnectAllRoomsFailed;
import org.jzuul.engine.exceptions.EngineRuntimeException;
import org.jzuul.engine.exceptions.NoSuchRoomException;
import org.jzuul.engine.gui.*;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Die Hauptklasse des JZuul Engines
 * 
 * 
 * @version $Revision: 1.42 $
 */
public class Engine {

    private static Engine instance;

    /**
     * Die Namen aller aktiven Befehle
     */
    private String[] commandNames;

    /**
     * Eine Liste von Character Objekten
     */
    private List NPCs;

    /**
     * Die aktuelle Position in der Befehlshistory
     */
    private int historyPos;

    /**
     * Befehle die für die Befehlscompletition verwendet werden
     */
    private Vector completions;

    /**
     * Der Thread der für NPCs Benutzt wird, falls dies aktiviert ist, null
     * sonst
     */
    private static NPCThread npcRunner;

    /**
     * Sollen die NPCs von einem Thread gesteuert werden?
     */
    private boolean threadedNpcs;

    /**
     * Den GameFileReader den das Engine benutzt
     */
    private GameFileReader reader;

    /**
     * Der Timer der die Timer Events auslöst
     * 
     * @see org.jzuul.engine.Event
     */
    private static Timer timer;

    /**
     * Ein RingVector von Player Objekten, falls das Spiel im Multiplayer läuft,
     * sonst null
     */
    public static RingVector players;

    /**
     * Die Globale Version des Engines
     */
    public static final String VERSION = "1.0 RC 1"; //$NON-NLS-1$

    /**
     * Eine Liste der eingegebenen Befehle, bereinigt von nicht bekannten.
     */
    private static List commandHistory;

    /**
     * Das GUI des Engines
     */
    public static GuiInterface gui;

    /**
     * Die aktuelle Spielkarte
     */
    public static GameMap map;

    /**
     * Das Spielerobjekt des Spieles. Bei Multiplayerspielen wird das Objekt pro
     * Runde ausgetauscht
     */
    public static Player player;

    /**
     * Der Dateiname der Spieldatei
     */
    public static String gamefile;

    /**
     * Der globale ObjektPool, enthällt alle GameObjects die im Spiel enthalten
     * sind
     */
    public static Map objectPool;

    /**
     * Eine Map die ein Mapping von befehl auf Command Objekt enthällt
     */
    public static Map commands;

    /**
     * Der Debug flag des Engines
     */
    public static int DEBUG = 1;

    /**
     * Der maximal erlaubte Debug wert
     */
    private final static int MAX_DEBUG = 10;

    /**
     * Der default ActionListener des spieles, behandelt die normalen Befehle
     * des spieles.
     * 
     *  
     */
    private class BefehlListener implements ActionListener {

        /**
         * Das CommandParser Objekt das dieser ActionListener benutzt
         */
        private CommandParser p;

        /**
         * Erstellt ein neues BefehlListener Objekt
         *  
         */
        public BefehlListener() {
            p = new CommandParser();
        }

        /**
         * Die Methode die das eigentliche Parsen übernimmt, wird von dem Gui
         * aufgerufen. Hier wird der Befehl auf gültigkeit überprüft,
         * ausgeführt, sein Rückgabewert überprüft und daraufhin die TargetList
         * Verarbeitung aufgerufen. Außerdem werden gültige Befehle zur History
         * hinzugefügt. Bei Rundenbasierten NPCs werden diese mit dem
         * Event.DEFAULT benachrichtigt.
         * 
         * @param e
         *            Ein Action Event
         */
        public void actionPerformed(ActionEvent e) {
            Engine.gui.resetInput();
            CommandContainer befehl = p.liefereBefehl(e.getActionCommand());

            if (!Engine.commands.containsKey(befehl.getAction())) {
                Object[] formatArgs = { befehl.getAction() };
                Engine.gui.printlnB(MessageFormat.format(Messages.getString("ENGINE_UNKNOWN_COMMAND"), formatArgs), //$NON-NLS-1$
                        GuiInterface.RED);
            } else {
                // I know this is ugly :-)
                Engine.gui.printlnB("> " + e.getActionCommand()); //$NON-NLS-1$
                Engine.commandHistory.add(e.getActionCommand());
                historyPos = commandHistory.size();
                Command currentAction = (Command) commands.get(befehl.getAction());
                if (currentAction.doAction(player, befehl.getArgs())) {
                    if (befehl.isTargetAction()) {
                        Engine.player.getTargetList().targetAction(befehl.getTargetNumber(),
                                currentAction.getObjectName());
                    }
                }
                if (currentAction.isGameAction()) {
                    doNPCaction();
                    if (players != null) {
                        Engine.player = (Player) players.next();
                        Object[] playerName = { Engine.player.getName() };
                        Engine.gui.printlnB(
                                MessageFormat.format(Messages.getString("ENGINE_PLAYERS_TURN"), playerName), //$NON-NLS-1$
                                GuiInterface.ORANGE);
                    }
                }
            }
        }
    }

    /**
     * KeyListener für die Spezialtasten.
     * 
     * Diese Klasse behandelt die Tabcompletition und die Historydurchläufe
     * 
     *  
     */
    private class SpecialKeyListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
            //Engine.gui.println("KeyTyped: " + e.toString());
        }

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {

            case (38): // Key_UP
            case (16777217): // SWT.KeyUp
                if (historyPos > 0) {
                    Engine.gui.setInput((String) commandHistory.get(--historyPos));
                } else {
                    Engine.gui.setInput(""); //$NON-NLS-1$
                }
                break;
            case (40): // Key_DOWN
            case (16777218): // SWT.KeyDown
                if (historyPos < commandHistory.size()) {
                    Engine.gui.setInput((String) commandHistory.get(historyPos++));
                } else {
                    Engine.gui.setInput(""); //$NON-NLS-1$
                }
                break;
            case (9): //TAB
                String input = Engine.gui.getInput();
                if (hasUniqueCompletition(input)) {
                    Engine.gui.setInput(getUniqueCompletition(input));
                } else {
                    printCompletitions(input);
                }
                break;

            default:
                Engine.debug("Key pressed: " + e.getKeyCode(), 5); //$NON-NLS-1$
            }
        }

        public void keyReleased(KeyEvent e) {
            //		Engine.gui.println("KeyReleased: " + e.toString());
        }
    }

    /**
     * Diese Klasse ist für den Ablauf der History zuständig.
     * 
     *  
     */
    protected class MyHistRun extends Thread {

        /**
         * Die Verwendete Liste von Befehlen
         */
        protected List history;

        /**
         * Erstellt ein neues MyHistRun objekt
         * 
         * @param name
         *            der Name des Threads
         * @param hist
         *            die Liste von Befehlen die abgearbeitet werden sollen
         */
        public MyHistRun(String name, List hist) {
            super(name);
            this.history = hist;
        }

        /**
         * Die implementierung eines Historydurchlaufs
         */
        public void run() {
            Engine.gui.disableInput();
            Engine.gui.println(Messages.getString("ENGINE_DEMO_START"), GuiInterface.RED); //$NON-NLS-1$
            int delay = 1000;
            for (Iterator commandIter = this.history.iterator(); commandIter.hasNext();) {
                String element = (String) commandIter.next();
                ActionListener al = Engine.gui.getActionListener();
                ActionEvent event = new ActionEvent(this, 0, element);
                al.actionPerformed(event);
                Engine.delay(delay);
            }
            Engine.gui.println(Messages.getString("ENGINE_DEMO_END"), GuiInterface.RED); //$NON-NLS-1$
            Engine.gui.setKeyListener(new SpecialKeyListener());
            Engine.gui.enableInput();
        }

    }

    /**
     * Diese Klasse wird verwendet um die NPCs in einem Thread laufen zu lassen
     * 
     *  
     */
    private class NPCThread extends Thread {

        /**
         * Die Liste der zu signalisierenden NPCs
         */
        private List npcList;

        protected boolean runs;

        protected boolean firstTime;

        /**
         * Erstellt einen neuen NPCThread
         * 
         * @param npcList
         *            die Liste der zu signalisierenden NPCs
         */
        public NPCThread(List npcList) {
            this.npcList = npcList;
            runs = true;
        }

        /**
         * Sendet alle 10 Sekunden an alle NPCs ein Event.DEFAULT
         * 
         * @see org.jzuul.engine.Event
         */
        public void run() {
            while (this.runs) {
                if (!firstTime) {
                    for (Iterator i = npcList.iterator(); i.hasNext();) {
                        ((Character) i.next()).doEvent(Event.DEFAULT);
                    }
                } else {
                    firstTime = false;
                }
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    // Silent catch
                }
            }
        }
    }

    /**
     * Diese Klasse wird verwendet um bei einem Spiel mit Spielernamen den
     * Spielernamen zu ermitteln
     * 
     *  
     */
    private class PlayerNameListener implements ActionListener {

        /**
         * Diese Methode verarbeitet einen eingegebenen Spielernamen und weist
         * diesen den Player Objekten zu.
         * 
         * @param e
         *            Ein Action Event
         */
        public void actionPerformed(ActionEvent e) {
            if (!e.getActionCommand().equals("")) { //$NON-NLS-1$
                Engine.player.setName(e.getActionCommand());
                // auto save and load works only in singleplayer games
                if (!Engine.gui.isApplet() && (players == null)) {
                    Engine.loadFromFile(e.getActionCommand());
                }
                Object[] formatArgs = { Engine.player.getName() };
                Engine.gui.printlnB(MessageFormat.format(Messages.getString("ENGINE_GREET_PLAYER"), formatArgs), //$NON-NLS-1$
                        GuiInterface.ORANGE);
                Engine.gui.setDefaultActionListener();
                Engine.gui.resetInput();
                if (players == null) {
                    run(false);
                } else {
                    run(true);
                }
            }
        }
    }

    /**
     * Diese Klasse wird verwendet um das Event.TIMER auszulösen.
     * 
     *  
     */
    private class TimerEventNotifier extends TimerTask {

        /**
         * Ruft Engine.notifyAll(Event.TIMER) bei jedem Timerlauf auf
         * 
         * @see org.jzuul.engine.Event
         */
        public void run() {
            Engine.notifyAll(Event.TIMER);
        }
    }

    /**
     * Diese Klasse wird verwendet um alle 5 Minuten zu speichern
     * 
     *  
     */
    private class SaveTimer extends TimerTask {

        /**
         * Ruft Engine.saveToFile() auf.
         * 
         * @see org.jzuul.engine.Event
         */
        public void run() {
            Engine.debug("Autosave called", 5); //$NON-NLS-1$
            if (!Engine.gui.isApplet()) Engine.saveToFile("autosave"); //$NON-NLS-1$
        }
    }

    /**
     * Diese Klasse implementiert einen ringförmigen Vector
     * 
     *  
     */
    private class RingVector extends Vector {

        /**
         * Die Position für die next einen Wert zurückgibt
         */
        int pos;

        /**
         * Erstellt ein neues RingVector Objekt
         *  
         */
        public RingVector() {
            super();
        }

        /**
         * Erstellt ein neues RingVector Objekt mit der initialen Größe size
         * 
         * @param size
         *            die Initiale größe für diesen RingVector
         */
        public RingVector(int size) {
            super(size);
        }

        /**
         * Gibt, beginnend von Position 0 immer das nächste Objekt im Vector
         * zurück. Am Ende des Vectors wird wieder das erste Objekt
         * zurückgegeben
         * 
         * @return das "nächste" Objekt im RingVector
         */
        public Object next() {
            if (pos == this.size()) {
                pos = 0;
            }
            return get(pos++);
        }
    }

    /**
     * Die zentrale Debugmethode für das JZuul Engine. Der Nachricht wird
     * automatisch die Aufrufende Methode vorangestellt.
     * 
     * @param message
     *            die Nachricht, die ausgegeben werden soll
     * @param level
     *            das Debuglevel, bei dem diese Nachricht erscheinen soll
     * @throws IllegalArgumentException
     *             wenn das level > MAX_DEBUG
     */
    public static void debug(String message, int level) {
        if (level > Engine.MAX_DEBUG)
                throw new IllegalArgumentException(
                        "You shouldn't set a debug value which is bigger than Engine.MAX_DEBUG which is currently " //$NON-NLS-1$
                                + Engine.DEBUG);
        if (Engine.DEBUG >= level) {
            String pre = "DEBUG(" + level + "): "; //$NON-NLS-1$ //$NON-NLS-2$
            Throwable t = new Throwable();
            StackTraceElement[] trace = t.getStackTrace();

            if (trace.length > 1) {
                String name = trace[1].getClassName();
                name = name.replaceFirst(".+\\.", ""); //$NON-NLS-1$ //$NON-NLS-2$
                pre += name;

                pre += "."; //$NON-NLS-1$
                pre += trace[1].getMethodName();
                pre += ": "; //$NON-NLS-1$
            }

            System.out.println(pre + message);
        }
    }

    /**
     * Compatibility creator
     * 
     * @param gamefile
     *            der Dateinamen mit dem Spielfile
     * @param commands
     *            die in dem Spiel erlaubten Befehle
     * @param gui
     *            das GUI
     */
    public Engine(String gamefile, CommandList commands, GuiInterface gui) {
        this(gamefile, commands, gui, false);
    }

    /**
     * Compatibility creator
     * 
     * @param gamefile
     *            der Dateinamen mit dem Spielfile
     * @param commands
     *            die in dem Spiel erlaubten Befehle
     * @param gui
     *            das GUI
     * @param numOfPlayers
     *            die Anzahl der Spieler
     */
    public Engine(String gamefile, CommandList commands, GuiInterface gui, int numOfPlayers) {
        this(gamefile, commands, gui, false, numOfPlayers);
    }

    /**
     * Erstellt ein neues Engine Objekt
     * 
     * @param gamefile
     *            der Dateiname der Spieldatei
     * @param commands
     *            die in dem Spiel erlaubten Befehle
     * @param gui
     *            das GUI
     * @param threadedNpc
     *            Sollen die NPCs in einem eigenen Thread laufen?
     */
    public Engine(String gamefile, CommandList commands, GuiInterface gui, boolean threadedNpc) {
        this(gamefile, commands, gui, threadedNpc, 1);
    }

    /**
     * Erstellt ein neues Engine Objekt
     * 
     * @param gamefile
     *            der Dateinamen der Spieldatei
     * @param commands
     *            die in dem Spiel erlaubten Befehle
     * @param gui
     *            das GUI
     * @param threadedNpc
     *            Sollen die NPCs in einem eigenen Thread laufen?
     * @param numOfPlayers
     *            die Anzahl der Spieler
     */
    public Engine(String gamefile, CommandList commands, GuiInterface gui, boolean threadedNpc, int numOfPlayers) {
        if (Engine.instance != null) {
            Engine.debug("There is another instance of myself", 1); //$NON-NLS-1$
            Engine.exit(0);
        }
        Engine.instance = this;

        Engine.debug("Current default locale is: " + Locale.getDefault(), 1); //$NON-NLS-1$

        this.threadedNpcs = threadedNpc;
        Engine.timer = new Timer();
        timer.schedule(new TimerEventNotifier(), 60 * 1000, 60 * 1000);
        timer.schedule(new SaveTimer(), 60 * 5 * 1000, 60 * 5 * 1000);

        Engine.gamefile = gamefile;
        if (gamefile == null) { throw new EngineRuntimeException("No gamefile set"); //$NON-NLS-1$
        }
        Engine.gui = gui;
        if (numOfPlayers < 2) {
            Engine.gui.splash_start(3);
        } else {
            Engine.gui.splash_start(4);
        }
        Engine.commands = commands.getCommands(gui.isApplet());
        this.commandNames = commandNames(Engine.commands);

        Engine.gui.printlnB("JZuul Engine Version " + Engine.VERSION + " (www.jzuul.org)"); //$NON-NLS-1$ //$NON-NLS-2$
        Engine.gui.splash_next(Messages.getString("ENGINE_STATING_MSG")); //$NON-NLS-1$
        Engine.player = new Player();

        this.historyPos = 0;
        this.completions = new Vector();

        Engine.debug(Engine.gamefile, 1);
        InputStream stream = Engine.getFileStream(Engine.gamefile);

        if (stream != null) {
            Engine.debug("Got input stream!", 1); //$NON-NLS-1$
        } else {
            Object[] formatArgs = { Engine.gamefile };
            Engine.gui.println(MessageFormat.format(Messages.getString("ENGINE_FILE_ERROR"),formatArgs)); //$NON-NLS-1$
            Engine.exit(1);
        }
        try {
            Engine.gui.splash_next(Messages.getString("ENGINE_PARSING_FILE_MSG")); //$NON-NLS-1$
            reader = new GameFileReader(stream, !Engine.gui.isApplet());
            try {
                Engine.map = reader.getMap("default"); //$NON-NLS-1$
            } catch (ConnectAllRoomsFailed e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (NoSuchRoomException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Engine.objectPool = reader.getObjectPool();
            if (reader.getPlayer() != null) {
                Engine.player = reader.getPlayer();
            }
            Engine.debug("Object Pool contains " //$NON-NLS-1$
                    + Engine.objectPool.keySet().size() + " Objects", //$NON-NLS-1$
                    1);
        } catch (JDOMException e) {
            System.err.println("JDOMException:\n" + e.getMessage()); //$NON-NLS-1$
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException:\n" + e.getMessage()); //$NON-NLS-1$
            System.exit(1);
        }

        if (threadedNpcs) {
            Engine.npcRunner = new NPCThread(map.getNpcList());
        }

        if (map != null) {
            Engine.player.setCurrentRoom(map.getStartRoomObj());
            this.NPCs = Engine.map.getNpcList();
        }

        if (numOfPlayers > 1) {
            Engine.debug("Creating Player Objects for numOfPlayers=" + numOfPlayers, 1); //$NON-NLS-1$
            Engine.gui.splash_next(Messages.getString("ENGINE_CREATING_PLAYERS_MSG")); //$NON-NLS-1$
            Engine.players = new RingVector(numOfPlayers);
            for (int i = 0; i < numOfPlayers; i++) {
                Player newPlayer = Engine.player.copy();
                newPlayer.setNumber(i + 1);
                players.add(newPlayer);
            }
            Engine.debug("Created " + numOfPlayers + " Player objects", 2); //$NON-NLS-1$ //$NON-NLS-2$
        }

        Engine.commandHistory = new Stack();
        Engine.gui.splash_next(Messages.getString("ENGINE_READY")); //$NON-NLS-1$

        if (reader.getDescription() != null) {
            Engine.gui.println(reader.getDescription());
        }
        reader = null; // we dont want the XML Stuff in our mem;
    }

    /**
     * Started das Spiel. Zuerst werden die wichtigen Spielvariablen abgefragt
     * (playr,gui,map und gamefile), dann wir der Spielername gefragt wenn
     * gefordert und dann dem Gui der Start des Spieles signalisiert.
     * 
     * @param askPlayerName
     *            sollen Spielernamen abgefragt werden?
     * @throws EngineRuntimeException
     *             Falls eine der oben genannten Variablen null ist.
     */
    public void run(boolean askPlayerName) {
        if (Engine.gui == null) { throw new EngineRuntimeException("You can't start the Engine without a GUI"); //$NON-NLS-1$
        }
        if (Engine.player == null) { throw new EngineRuntimeException("You can't start the Engine without a Player\n"); //$NON-NLS-1$
        }
        if (Engine.map == null) { throw new EngineRuntimeException("You can't start the Engine without a Map\n"); //$NON-NLS-1$
        }
        if (Engine.gamefile == null) { throw new EngineRuntimeException(
                "You can't start the Engine without a Initial Gamefile\n"); //$NON-NLS-1$
        }

        if (!askPlayerName) {
            Engine.gui.setDefaultActionListener(new BefehlListener());
            Engine.gui.setKeyListener(new SpecialKeyListener());
            Engine.player.getCurrentRoom().printBeschreibung();
            addToCompletition(this.commandNames);
            if (this.threadedNpcs) {
                this.npcRunner.start();
            }

        } else {
            if (players != null) {
                Engine.player = (Player) players.next();
                if (player.getName().equals("Player")) { //$NON-NLS-1$
                    this.getPlayerName();
                } else {
                    Object[] formatArgs = { Engine.player.getName() };
                    Engine.gui.printlnB(MessageFormat.format(Messages.getString("ENGINE_PLAYERS_TURN"), formatArgs), //$NON-NLS-1$
                            GuiInterface.ORANGE);
                    run(false);
                }
            } else {
                getPlayerName();
            }
        }

        if (Engine.gui != null) {
            Engine.gui.start();
        }
    }

    /**
     * Liefert ein Array der Befehlsnamen zurück (Benötigt für die
     * Autocompletion)
     * 
     * @param commands
     *            eine Map von Kommandos, der Keys die Kommandonamen sind
     * @return String[] of Action Names
     */
    private String[] commandNames(Map commands) {
        Iterator iter = commands.keySet().iterator();
        String[] actionNames = new String[commands.size()];
        for (int i = 0; i < actionNames.length && iter.hasNext(); i++) {
            actionNames[i] = (String) iter.next();
        }
        return actionNames;
    }

    /**
     * Wird benötigt um für jeden Character die doEvent(Event.DEFAULT) methode
     * aufzurufen
     * 
     * @see org.jzuul.engine.Event
     */
    private void doNPCaction() {
        if (!this.threadedNpcs) {
            for (Iterator i = NPCs.iterator(); i.hasNext();) {
                ((Character) i.next()).doEvent(Event.DEFAULT);
            }
        }
    }

    /**
     * Fügt einen Befehl (ein Wort) zu der Liste der möglichen Befehle für die
     * Tabcompletition hinzu
     * 
     * @param command
     *            das hinzuzufügende Wort
     */
    protected void addToCompletition(String command) {
        this.completions.add(command);
    }

    /**
     * Fügt eine Liste von Befehlen zu der Liste für die Tabcompletition hinzu
     * 
     * @param commands
     *            die Befehle die Hinzugefügt werden sollen
     */
    protected void addToCompletition(Vector commands) {
        this.completions.addAll(commands);
    }

    /**
     * @see #addToCompletition(Vector commands)
     * 
     * @param commands
     *            Eine String Array von Befehlen das hinzugefügt werden soll
     */
    protected void addToCompletition(String[] commands) {
        for (int i = 0; i < commands.length; i++) {
            this.completions.add(commands[i]);
        }
    }

    /**
     * Überpüft ob es für einen Teilstring eine eindeutige Vervollständigung
     * gibt.
     * 
     * @param input
     *            der Teilstring der überprüft werden soll
     * @return true falls es eine eindeutige Vervollständigung gibt, false
     *         sonst.
     */
    protected boolean hasUniqueCompletition(String input) {
        int count = 0;
        for (Iterator i = completions.iterator(); i.hasNext();) {
            if (((String) i.next()).startsWith(input)) {
                count++;
            }
            if (count > 1) { return false; }
        }
        if (count == 0) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Gibt die möglichen Vervollständigungen für einen Inputsting aus.
     * 
     * @param input
     *            der Teilstring für den die Vervollständigungen ausgegeben
     *            werden sollen
     */
    protected void printCompletitions(String input) {
        int count = 0;
        String result = ""; //$NON-NLS-1$
        for (Iterator i = completions.iterator(); i.hasNext();) {
            String value = (String) i.next();
            if (value.startsWith(input)) {
                count++;
                result += " " + value; //$NON-NLS-1$
            }
        }
        if (count > 0) {
            Engine.gui.printU(Messages.getString("ENGINE_COMMANDS")); //$NON-NLS-1$
            Engine.gui.println(" " + result); //$NON-NLS-1$
        }
    }

    /**
     * Holt für einen gegebenen Inputstring die einzige mögliche
     * Vervollständigung.
     * 
     * @param input
     *            ein Teilstring für den die Vervollständigung geholt werden
     *            soll
     * @return die Vervollständigung wenn eindeutig, null sonst
     */
    protected String getUniqueCompletition(String input) {
        int count = 0;
        String retval = null;
        for (Iterator i = completions.iterator(); i.hasNext();) {
            String value = (String) i.next();
            if (value.startsWith(input)) {
                retval = value;
                count++;
            }
        }
        if (count == 1) {
            return retval + " "; //$NON-NLS-1$
        } else {
            return null;
        }
    }

    /**
     * Wandelt die Befehlshistory in ein JDOM XML Element um. Die Befehlshistory
     * enthällt nur gültige Befehle
     * 
     * @return ein JDOM XML Element das die History wiederspiegelt
     */
    public static Element historyToElement() {
        Element histE = new Element("history"); //$NON-NLS-1$
        for (Iterator histIter = commandHistory.iterator(); histIter.hasNext();) {
            String element = (String) histIter.next();
            Element e = new Element("command"); //$NON-NLS-1$
            e.setText(element);
        }
        return histE;
    }

    /**
     * Wandelt den stand des Engines in ein JDOM XML Element um. Diese Methode
     * verwandelt den aktullen Stand des Engines in ein JDOM XML Element indem
     * es nacheinander die Methoden toElement() der Karte, des Spielers und der
     * History aufruft.
     * 
     * @return ein JDOM XML Element das den aktuellen Stand des Engines
     *         wiederspiegelt
     */
    public static Element toElement() {
        Element root = new Element("gamefile"); //$NON-NLS-1$
        root.setAttribute("version", Engine.VERSION); //$NON-NLS-1$
        root.addContent(Engine.map.toElement());
        root.addContent(Engine.player.toElement());
        root.addContent(historyToElement());

        return root;
    }

    /**
     * Diese Methode speichert Spielstände. Diese Methode ruft Engine.toElement
     * auf und speichert das Ergebnis in dem Übergebenen Namen an das sie ".xml"
     * anhängt.
     * 
     * @param filename
     *            der Dateiname, ohne Endung
     * @return true bei Erfolg, false bei Fehlern
     */
    public static boolean saveToFile(String filename) {
        try {
            DocType type = new DocType("gamefile", //$NON-NLS-1$
                    "file:org/jzuul/engine/dtd/gamefile.dtd"); //$NON-NLS-1$
            Document doc = new Document(Engine.toElement(), type);

            FileOutputStream out = new FileOutputStream(new File(filename + ".xml"), false); //$NON-NLS-1$
            OutputStreamWriter ow = new OutputStreamWriter(out, Charset.forName("UTF-8")); //$NON-NLS-1$

            Format format = Format.getPrettyFormat();
            format.setEncoding("UTF-8"); //$NON-NLS-1$
            XMLOutputter fmt = new XMLOutputter(format);

            fmt.output(doc, ow);
            ow.flush();
            ow.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

    }

    /**
     * Diese Methode lädt Spielstände.
     * 
     * Die Methode wird von dem Befehl "load" sowie von der Named Game
     * Verwaltung aufgerufen um einen Spielstand zu laden. Da ein Spielstand nur
     * den aktuellen Zustand und nicht die gesamten Objektbeschreibungen
     * enthällt, wird zuerst die Spieldatei und dann das Savegame geladen.
     * 
     * @param filename
     *            der Name der Savegamedatei
     * @return true bei Erfolg, false bei fehlern
     */
    public static boolean loadFromFile(String filename) {
        // ok, the logic here is to load the environment from the gamefile and
        // the player and map from the savegame
        InputStream stream = Engine.class.getResourceAsStream(Engine.gamefile);
        if (stream == null) {
            System.err.println("An error occured getting the inital map file: " //$NON-NLS-1$
                    + Engine.gamefile + "! Trying again\n"); //$NON-NLS-1$
            try {
                //mmm, this didn't work... try again
                stream = new FileInputStream(new File(Engine.gamefile));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.exit(1);
            }

        }
        try {
            GameFileReader reader = new GameFileReader(stream, !Engine.gui.isApplet());
            Engine.objectPool = reader.getObjectPool();
            // this is mainly why we do this
            Engine.debug("Object Pool contains " //$NON-NLS-1$
                    + Engine.objectPool.keySet().size() + " Objects", //$NON-NLS-1$
                    1);

            if (reader.readFromSavegame(filename + ".xml", !Engine.gui.isApplet())) { //$NON-NLS-1$
                try {
                    Engine.map = reader.getMap("default"); //$NON-NLS-1$
                } catch (ConnectAllRoomsFailed e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (NoSuchRoomException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Engine.player = reader.getPlayer();
                List hist = reader.getHistory();
                if (hist != null) {
                    Engine.commandHistory = hist;
                }
                return true;
            }
        } catch (JDOMException e) {
            System.err.println("JDOMException:\n" + e.getMessage()); //$NON-NLS-1$
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException:\n" + e.getMessage()); //$NON-NLS-1$
            System.exit(1);
        }
        return false;
    }

    /**
     * Die Methode runHistory verarbeitet eine {@link List}von Befehlen
     * innerhalb des Engines ab.
     * 
     * @param filename
     *            eine JZuul XML Datei, deren <history>Teil abegearbeitet
     *            werden soll
     */
    public void runHistory(String filename) {
        Engine.gui.setDefaultActionListener(new BefehlListener());
        Engine.gui.setKeyListener(new SpecialKeyListener());
        List hist = null;
        InputStream resource = this.getClass().getResourceAsStream(filename);
        try {
            GameFileReader r = new GameFileReader(resource, !Engine.gui.isApplet());
            hist = r.getHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyHistRun histThread = new MyHistRun("history", hist); //$NON-NLS-1$
        Engine.player.getCurrentRoom().printBeschreibung();
        histThread.start();
        Engine.gui.start();
    }

    /**
     * Unterbricht die Ausführung für eine Gegebene Zeit. Diese Methode versucht
     * relativ genau eine gewisse Zeit zu schlafen, indem sie die übergebene
     * Zeit aufteilt und dann die tatsächlich geschlafene Zeit überprüft.
     * 
     * @param ms
     *            Millisekunden die geschlafen werden sollen
     */
    public static void delay(int ms) {
        if (ms < 20) {
            ms = 20;
        }
        for (int i = 0; i < 20; i++) {
            int part = ms / 20;
            Engine.debug("Should sleep a unit of " + part + " msecs", 5); //$NON-NLS-1$ //$NON-NLS-2$
            try {
                long begin = System.currentTimeMillis();
                Thread.sleep(part);
                long ende = System.currentTimeMillis();
                Engine.debug("Slept: " + (ende - begin), 5); //$NON-NLS-1$
                ms -= (ende - begin);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Fordert den Spieler zur Eingabe eines Namens auf. Diese Methode setzt dem
     * Gui als ActionListener eine PlayerNameListener Objekt welches dann die
     * Eingabe des Namens verarbeitet und gibt eine Aufforderung zur Eingabe des
     * Names aus.
     */
    public void getPlayerName() {
        Engine.gui.setActionListener(new PlayerNameListener());
        if (Engine.players == null) {
            Engine.gui.printlnB(Messages.getString("ENGINE_ENTER_USERNAME"), //$NON-NLS-1$
                    GuiInterface.ORANGE);
        } else {
            Object[] formatArgs = { new Integer(Engine.player.getNumber()) };
            Engine.gui.printlnB(MessageFormat.format(Messages.getString("ENGINE_ENTER_USERNAME_NUMBER"), formatArgs), //$NON-NLS-1$
                    GuiInterface.ORANGE);
        }
    }

    /**
     * Schicke Event id an alle EventListener im Spiel (an die Räume und die
     * Objekte)
     * 
     * @param id
     *            die Event id
     * @see org.jzuul.engine.Event
     */
    public static void notifyAll(int id) {
        if (map != null) {
            map.notifyRooms(id);
        }
        for (Iterator gameObjectIter = objectPool.values().iterator(); gameObjectIter.hasNext();) {
            EventListener element = (EventListener) gameObjectIter.next();
            element.doEvent(id);
        }

    }

    public static void exit(int status) {
        Engine.debug("Exit called!", 1); //$NON-NLS-1$

        if ((players == null) && (!Engine.player.getName().equals("Player"))) { //$NON-NLS-1$
            if (!Engine.gui.isApplet()) Engine.saveToFile(Engine.player.getName());
        }
        
        if (Engine.gui != null) {
            Engine.gui.disableInput();
            Engine.gui.printlnB(Messages.getString("ENGINE_GOING_DOWN")); //$NON-NLS-1$
        }
            
        if (Engine.npcRunner != null) {
            Engine.npcRunner.runs = false;
            Engine.debug("Joining NPC thread", 1); //$NON-NLS-1$
            Engine.npcRunner.interrupt();
            try {
                Engine.npcRunner.join();
            } catch (InterruptedException e) {
                Engine.debug("InterruptedException during join", 1); //$NON-NLS-1$
            }
            Engine.debug("NPCThread died", 1); //$NON-NLS-1$
            Engine.npcRunner = null;
        }

        if (Engine.gui != null) {
            Engine.gui.close();
            Engine.gui = null;
        }

        if (Engine.timer != null) {
            Engine.timer.cancel();
            Engine.timer = null;
        }

        Engine.player = null;
        Engine.map = null;

        Engine.commandHistory = null;

        Engine.commands = null;

        Engine.instance = null;

    }

    protected void finalize() {
        Engine.debug("Engine finalize called", 1); //$NON-NLS-1$
    }

    public static InputStream getFileStream(String filename) {
        InputStream stream = null;
        String[] elements = filename.split("(/|\\\\)"); //$NON-NLS-1$ //$NON-NLS-2$
        String dir, name;
        dir = ""; //$NON-NLS-1$
        name = ""; //$NON-NLS-1$
        for (int i = 0; i < elements.length - 1; i++) {
            dir += elements[i] + File.separator;
        }
        name = elements[elements.length - 1];
        name = name.replaceAll("\\.xml", ""); //$NON-NLS-1$ //$NON-NLS-2$
        Engine.debug("Filename seems to be " + name + ".xml, dir seems to be " + dir, 1); //$NON-NLS-1$ //$NON-NLS-2$
        Locale loc = Locale.getDefault();

        Stack possFilenames = new Stack();
        if (!loc.getVariant().equals("")) { //$NON-NLS-1$
            possFilenames.push(name + "_" + loc.getLanguage() + "_" + loc.getCountry() + "_" + loc.getVariant() //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + ".xml"); //$NON-NLS-1$
        }
        possFilenames.push(name + "_" + loc.getLanguage() + "_" + loc.getCountry() + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        possFilenames.push(name + "_" + loc.getLanguage() + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
        possFilenames.push(name + ".xml"); //$NON-NLS-1$

        // first as a stream, because we don't want to crash applets prematurely
        for (Iterator iter = possFilenames.iterator(); iter.hasNext();) {
            String fname = (String) iter.next();
            Engine.debug("Trying " + dir + fname, 1); //$NON-NLS-1$
            stream = Engine.class.getResourceAsStream(dir + fname);
            if (stream == null) {
                String tmpdir = dir.replaceFirst("/", ""); //$NON-NLS-1$
                Engine.debug("Trying " + tmpdir + fname, 1); //$NON-NLS-1$
                stream = Engine.class.getResourceAsStream(dir + fname);
            }

            if (stream != null) {
                Engine.debug("Running with file " + dir + fname + " (stream)", 1); //$NON-NLS-1$ //$NON-NLS-2$
                return stream;
            }
        }
        // try the original name
        stream = Engine.class.getResourceAsStream(filename);
        if (stream != null) {
            Engine.debug("Running with file " + filename + " (stream-fallback)", 1); //$NON-NLS-1$ //$NON-NLS-2$
            return stream;
        }

        if (!Engine.gui.isApplet()) {
            // now as file:
            for (Iterator iter = possFilenames.iterator(); iter.hasNext();) {
                String fname = (String) iter.next();
                File f = new File(dir + fname);
                if (f.exists()) {
                    try {
                        Engine.debug("Running with file " + dir + fname + " (file)", 1); //$NON-NLS-1$ //$NON-NLS-2$
                        return new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        System.err.println("File.exists lied to us :-(:"); //$NON-NLS-1$
                        e.printStackTrace();
                    }
                }

            }
            try {
                //try the original name
                return new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                System.err.println("Somehow we didn't find any gamefile"); //$NON-NLS-1$
                e.printStackTrace();
            }
        }
        return null;
    }
}