/*
 * 	CVS: $Id: Eat.java,v 1.5 2004/07/25 21:40:55 marcus Exp $
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

package org.jzuul.engine.commands;

/**
 * 
 */
public class Eat extends NahrungAufnehmen {

	String[] eatsayings = { Messages.getString("EAT_SAYING_1"), //$NON-NLS-1$
									Messages.getString("EAT_SAYING_2"), //$NON-NLS-1$
									Messages.getString("EAT_SAYING_3") //$NON-NLS-1$
	};

	public Eat() {
		super();
		this.name = "eat"; //$NON-NLS-1$
		this.sayings = eatsayings;
		this.desc = Messages.getString("EAT_HELP"); //$NON-NLS-1$
	}

}
