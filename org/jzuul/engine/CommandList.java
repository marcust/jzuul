/*
 * 	CVS: $Id: CommandList.java,v 1.7 2004/07/25 18:08:14 marcus Exp $
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jzuul.engine.commands.Command;

/**
 * Verwaltet die Liste der im Engine aktiven Befehle
 * 
 * 
 * @version $Revision: 1.7 $
 */
public class CommandList {
	/**
	 * Enthält alle möglichen Befehle
	 */
	public static String[] COMMANDS = 
		{ "do", "drink", "drop", "eat", "exit", "give", "go", "hallo", "help", "inspect", "load", "save",
			"look", "sleep", "take", "talk", "todo", "use", "view", "whoami"
		};
	
	/**
	 * Enthällt eine Map in der Form Befehlsname => Command Objekte
	 */
	protected Map commandMap;

	/**
	 * Erstellt eine neue leere CommandList
	 */
	public CommandList() {
		super();
		commandMap = new HashMap();
	}

	/**
	 * Fügt ein Command Objekt in das CommandList Objekt ein
	 * 
	 * @param command	ein Command Objekt
	 */
	public void addCommand(Command command) {
		this.commandMap.put(command.getName(), command);
	}
	
	/**
	 * Fügt das Kommand mit dem gegebenen Namen zu der CommandList hinzu 
	 * 
	 * @param commandName	der Name des commands
	 */
	public void addCommand(String commandName) {
		Object object = null;
				 try {
					 Class classDefinition = Class.forName("org.jzuul.engine.commands." + commandName );
					 object = classDefinition.newInstance();
					 Command com = (Command)object;
					 this.addCommand(com);
				 } catch (InstantiationException e) {
					 System.out.println(e);
				 } catch (IllegalAccessException e) {
					 System.out.println(e);
				 } catch (ClassNotFoundException e) {
					 System.out.println(e);
				 }
	}
	
	/**
	 * Fügt einen Kommandoalias zu der CommandList hinzu
	 * 
	 * @param alias							der Name des Alias
	 * @param commandName	der wirkliche Kommandoname
	 * @param commandArguments	die Argumente zu dem Kommand
	 */
	public void addAlias(String alias, String commandName, String commandArguments) {
		commandMap.put(alias, new CommandAlias(alias,(Command)commandMap.get(commandName),commandArguments));
	}
	
	/**
	 * Fügt einen Kommandoalias  für eine Kommando ohne Argumente zu der CommandList hinzu
	 * 
	 * @param alias							der Name des Alias
	 * @param commandName	der wirkliche Kommandoname
	*/
	public void addAlias(String alias, String commandName) {
		commandMap.put(alias, commandMap.get(commandName));
	}
	
	/**
	 * Holt das Command Objekt mit dem gegebenen Namen aus dem CommandList Objekt 
	 * 
	 * @param name	der Name des Command Objekts
	 * @return				das Command Objekt mit dem gegebenen Namen
	 */
	public Command getCommandObject(String name) {
		return (Command)this.commandMap.get(name); 
	}

	/**
	 * Gibt eine "defaul" Liste von Kommandos zurück.
	 * 
	 * @return	eine Liste die alle in COMMANDS spezifizierten Command Objekte enthält
	 */
	public static CommandList defaultList() {
		CommandList com = new CommandList();
		com.initAll();
		com.commonAliases();
		return com;
	}
	
	/**
	 * Erstellt Objekte aus den unter COMMANDS festgelegten Namen
	 */
	protected void initAll() {
		for (int i = 0; i < CommandList.COMMANDS.length; i++) {
			this.addCommand(Helpers.firstToUpper(CommandList.COMMANDS[i]));
		}
	}
	
	/**
	 * Setzt gewisse gebräuchliche Aliase
	 */
	protected void commonAliases() {
		this.addAlias("up", "go", "up");
		this.addAlias("down", "go", "down");
		
		this.addAlias("north", "go", "north");
		this.addAlias("east", "go", "east");
		this.addAlias("south", "go", "south");
		this.addAlias("west", "go", "west");
		
		this.addAlias("inv", "view", "inventory");
		
		this.addAlias("quit", "exit");
		
	}

	/**
	 * Gibt eine Map der Form Kommandoname => Command Objekt zurück
	 * 
	 * @param onlyAppletSave	nur Applet Sandbox sicher Befehle?
	 * @return	eine Map von Commad Objekts
	 */
	public Map getCommands(boolean onlyAppletSave) {
		Map newmap = new HashMap();
		
		for (Iterator commandIter = commandMap.keySet().iterator(); commandIter.hasNext();) {
			String commandName = (String)commandIter.next();
			Command element = (Command)commandMap.get(commandName);
			if (onlyAppletSave) {
				if (element.isAppletSave()) {
					newmap.put(commandName, element);
				}
			} else {
			    newmap.put(commandName, element);
			}
		}
		return newmap;		
	}

}
