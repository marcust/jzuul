/*
 * 	CVS: $Id: Zuul.java,v 1.9 2004/07/21 17:02:07 marcus Exp $
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

package org.jzuul.games.zuul;

import org.jzuul.tools.JZuulApplet;

/**
 * Starterklasse für das supertolle ZuulSpiel
*
* 
* @version $Revision: 1.9 $
*/

public class Zuul extends JZuulApplet {
    	static {
    	    JZuulApplet.gamefile = "/org/jzuul/games/zuul/initial.xml";
    	    JZuulApplet.demo = "/org/jzuul/games/zuul/demo.xml";
    	}
    
    	public void init() {
    	    JZuulApplet.gamefile = "/org/jzuul/games/zuul/initial.xml";
    	    JZuulApplet.demo = "/org/jzuul/games/zuul/demo.xml";
    	    super.init();
    	}
}
