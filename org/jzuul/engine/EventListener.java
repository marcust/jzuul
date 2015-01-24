/*
 * 	CVS: $Id: EventListener.java,v 1.4 2004/07/16 16:22:33 marcus Exp $
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

/**
 * Dieses Interface definiert die Methoden die eine JZuul Klasse implementieren muss um
 * von dem Event System benutzt werden zu können 
 * 
 * 
 * @version $Revision: 1.4 $
 * @see org.jzuul.engine.Event
 * @see org.jzuul.engine.EventHandler
 */
public interface EventListener {
	/**
	 * Wird von auslösenden Objekten aufgerufen um einem Objekt ein Event mitzuteilen
	 * 
	 * @param id	die Id des Events
	 * @see org.jzuul.engine.Event
	 */
	void doEvent(int id);
	
	/**
	 * Wird von dem Event System benutzt um einem Objekt einen {@link EventHandler} zuzuweisen.
	 * 
	 * @param eventName	der Name des Events
	 * @param handler			das EventHandler Objekt für dieses Event
	 * @see org.jzuul.engine.Event
	 * @see org.jzuul.engine.EventHandler
	 */
	 void setHandler(String eventName, EventHandler handler);

}
