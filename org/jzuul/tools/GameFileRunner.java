/*
 * 	CVS: $Id: GameFileRunner.java,v 1.5 2004/07/16 16:22:35 marcus Exp $
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

package org.jzuul.tools;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jzuul.engine.CommandList;
import org.jzuul.engine.Engine;
import org.jzuul.engine.exceptions.GuiNotInitializedException;
import org.jzuul.engine.gui.GuiInterface;

/**
 * The GameFileRunner is a general purpose Engine Excecutable which is usable
 * for any feature of  the JZuul Engine. 
 * 
 * 
 * @version $Revision: 1.5 $
 */
public class GameFileRunner {
	/**
	 * The Base where to find GuiInterface implementations
	 */
	private static final String guiBase = "org.jzuul.engine.gui";

	/**
	 * The filename of the inital gamefile
	 */
	private String filename;
	/**
	 * Soll bei dem Spiel der Spielername erfragt werden?
	 */
	private boolean withPlayerName = true;
	/**
	 * Soll ein History Lauf gemacht werden
	 */
	private boolean historyRun = false;
	/**
	 * NPCs werden von eigenem Thread bedient
	 */
	private boolean threaded = false;
	/**
	 * Anzahl der Spieler
	 */
	private int numberOfPlayers = 0;
	/**
	 * Der Name des History File Names
	 */
	private String histFileName;
	/**
	 * Das zu benutzende Gui (default ist TextUI)
	 */
	private String guiName = "TextUi";

	/**
	 * Das Engine Objekt das von dem GameFileRunner Benutzt wird
	 */
	private Engine engine;

	/**
	 * Startet den GameFileRunner mit den gegebenen Kommandozeilenargumenten
	 * 
	 * @param args	Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			GameFileRunner.usage();
		}
		Vector v = new Vector(args.length);
		for (int i = 0; i < args.length; i++) {
			v.add(args[i].replaceFirst("-+", ""));
		}

		GameFileRunner runner = new GameFileRunner(v);
		runner.run();
	}

	/**
	 * Erstellt ein GameFileRunner Objekt
	 * 
	 * @param arguments	Argumente
	 */
	public GameFileRunner(List arguments) {
		// handle the arguments
		for (Iterator argIter = arguments.iterator(); argIter.hasNext();) {
			String element = (String) argIter.next();
			if (element.endsWith(".xml") && filename == null) {
				if (element.startsWith("/")) {
					filename = element;
				} else {
					filename = "/" + element;
				}
				argIter.remove();
			}
			if (element.equals("players")) {
				argIter.remove();
				numberOfPlayers = Integer.parseInt((String) argIter.next());
				argIter.remove();
			}
			if (element.equals("history")) {
				historyRun = true;
				argIter.remove();
				histFileName = (String) argIter.next();
				argIter.remove();
				if (!histFileName.startsWith("/")) {
					histFileName = "/" + histFileName;
				}
			}
			if (element.equals("gui")) {
				argIter.remove();
				guiName = (String) argIter.next();
				argIter.remove();
			}
			if (element.equals("debug")) {
				argIter.remove();
				Engine.DEBUG = Integer.parseInt((String) argIter.next());
				argIter.remove();
			}
		}
		withPlayerName = arguments.contains("name");
		arguments.remove("name");
		threaded = arguments.contains("threaded");
		arguments.remove("threaded");
		
		if (!arguments.isEmpty()) {
			System.out.println();
			for (Iterator argIter = arguments.iterator(); argIter.hasNext();) {
				String element = (String) argIter.next();
				System.out.println("ERROR: No such argument: " + element);
			}
			System.out.println();
			usage();
		}
		
		GuiInterface gui = createGui();
		this.engine = new Engine(filename, CommandList.defaultList(), gui, threaded, numberOfPlayers);
	}

	/**
	 * Versucht ein GuiInterface Objekt aus den angegeben Namen zu erstellen
	 * 
	 * @return	ein GuiInterface Objekt
	 */
	protected GuiInterface createGui() {
		Object guiObj = null;
		try {
			Class classDefinition = Class.forName(guiBase + "." + guiName);
			guiObj = classDefinition.newInstance();
		} catch (InstantiationException e) {
			System.out.println(e);
		} catch (IllegalAccessException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: There is no " + guiName + " GUI");
			System.exit(2);
		}

		if (guiObj != null) {
			return (GuiInterface) guiObj;
		} else {
			throw new GuiNotInitializedException();
		}
	}

	/**
	 * Lässt das Engine laufen.
	 *
	 */
	protected void run() {
		if (historyRun) {
			engine.runHistory(histFileName);
		} else {
			engine.run(withPlayerName);
		}
	}

	/**
	 * Gibt eine Hilfsmeldung aus
	 */
	public static void usage() {
		System.out.println(" This is the JZuul Gamefile runner $Revision: 1.5 $ ");
		System.out.println();
		System.out.println(" JZuul is free software, you can distribute and modify ");
		System.out.println(" it under the terms of the GPL (see LICENSE)");
		System.out.println();
		System.out.println(
			" Usage:  [java] GameFileRunner gamefile.xml [-name] [-history <file.xml>]");
		System.out.println("\t [-players <number>] [-threaded] [-gui GuiImplementation] ");
		System.out.println("\t [-debug value]");
		System.out.println();
		System.out.println("\t-name\t\tdoes do a named Player game");
		System.out.println("\t-history\tdoes a history run on the given file");
		System.out.println("\t-players\tcreates a game with number of players");
		System.out.println("\t-threaded\tlet the NPCs actions run in an own Thread");
		System.out.println("\t-debug\t\tsets the Engine debug level to Value");
		System.out.println();
		System.exit(1);
	}

}
