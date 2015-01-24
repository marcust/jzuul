/*
 * 	CVS: $Id: Timer.java,v 1.2 2004/07/16 16:22:35 marcus Exp $
 * 
 *  This file is part of zuul.
 *
 *  zuul is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  zuul is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with zuul; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Copyrigth 2004 by marcus, leh
 * 
 */

package org.jzuul.tools;

public class Timer {
    String name;
    long[] values;
    int pos;
    long start;
    long stop;

    public Timer(String name) {
	this.name = name;
	pos = 0;
	values = new long [100];
    }

    public void start() {
	this.start = System.currentTimeMillis();
    }

    public void stop() {
	this.stop = System.currentTimeMillis();

	if (pos >= values.length) { 
	    long [] newvals = new long [values.length*2];
	    for (int i = 0; i < pos; i++)
		newvals[i] = values[i];
	    values = newvals;
	}
	values[pos] = this.stop - this.start;
	pos++;
    }

    public String toString() {
    if (pos == 0) return "";
	String retval;

	retval = new String(name + "\t" + pos); 

	int helper = 0;
	for (int i = 0; i < pos; i++) {
	    helper += this.values [i ];
	}
	float avrg = helper / (pos);
	retval += "\t" + avrg + "ms";
	return retval;
    }

}








