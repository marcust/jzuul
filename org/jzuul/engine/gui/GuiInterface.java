/*
 * 	CVS: $Id: GuiInterface.java,v 1.10 2004/07/16 16:22:34 marcus Exp $
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

package org.jzuul.engine.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.InputStream;

/**
 * Dieses Interface wird von dem Engine für seine Ein/Ausgabe benutzt.
 * Die print Methoden müssen so weit wie möglich den erwartungen Entsprechen.
 * Der KeyListener muss nicht unbedingt implementiert werden, der ActionListener
 * jedoch muss die Eingabe an das Engine weiterreichen.
 * 
 * 
 * @version $Revision: 1.10 $
 */
public interface GuiInterface {
	/**
	 * Definiert die Farbe Rot in einem int[3] in RGB Werten
	 */
	int[] RED = { 255,0,0 };
	/**
	 * Definiert die Farbe Grün in einem int[3] in RGB Werten
	 */
	int[] GREEN = { 0,255,0 };
	/**
	 * Definiert die Farbe Blau in einem int[3] in RGB Werten
	 */
	int[] BLUE = { 0, 0,255 };
	/**
	 * Definiert die Farbe Orange in einem int[3] in RGB Werten
	 */
	int[] ORANGE = { 255, 177 , 177 };	
	/**
	 * Definiert die Farbe Black in einem int[3] in RGB Werten
	 */
	int[] BLACK = { 0, 0 , 0 };	
	
	
	/**
	 * Erwartet die Ausgaben von out mit anschließendem Zeilenumbruch
	 * in der Farbe color, die in RGB in einem int[3] übergeben wird.
	 * 
	 * @param out			der String der Ausgegeben werden soll
	 * @param color		die Farbe in RGB
	 */
	void println(String out, int[] color );
	
	/**
	 * Erwartet die Ausgabe von out ohne Zeilenumbruch
	 * 
	 * @param out	der String der Ausgegeben werden soll
	 */
	 void print(String out);
	 
	 /**
	  * Erwartet die Ausgabe mit anschließendem Zeilenumbruch
	  * 
	  * @param out	der String der Ausgegben wird
	  */
	 void println(String out);
	 
	 /**
	  * Erwartet eine Leerzeile in der Ausgabe
	  */
	 void println();
	 
	 /**
	  * Erwartet die Ausgabe von out unterstrichen, falls das Ausgabesystem
	  * es unterstützt
	  * 
	  * @param out	der String der ausgegebn werden soll
	  */
	 void printU(String out);
	 
	 /**
	  * Erwartet die Ausgabe von out in Fett (Bold), falls das Ausgabesystem es 
	  * unterstützt plus einen Zeilenumbruch
	  * 
	  * @param out der String der Ausgegeben werden soll
	  */
	 void printlnB(String out);
	 
	 /**
	  * Erwartet die Ausgabe von out in Fett (Bold) in der in color spezifizierten
	  * Farbe, falls das Ausgabesystem es unterstützt plus einen Zeilenumbruch.
	  * 
	  * @param out		der String der Ausgegeben werden soll
	  * @param color	die RGB Farben in einem int[3]
	  */
	 void printlnB(String out, int[] color);
	 
	 /**
	  * Erwartet die Ausgabe von out in Kursiv (Italic) plus einen Zeilenumbruch
	  * 
	  * @param out 	der String der Ausgegeben werden soll.
	  */
	 void printlnI(String out);
	 
	 /**
	  * Erwartet die Ausgabe von out in Kursiv(Italic) in der in color spezifizierten 
	  * Farbe, falls das Ausgabesystem dies unterstützt plus einen Zeilenumbruch.
	  * 
	  * @param out			der String der ausgegeben werden soll
	  * @param color		die Farbe in RGB in einem int[3]
	  */
	 void printlnI(String out, int[] color);
	 
	 /**
	  * Setzt den ActionListener der von dem GUI Objekt bedient werden muss. 
	  * 
	  * @param al	der ActionListener
	  */
	 void setActionListener(ActionListener al);
	 
	 /**
	  * Setzt einen default ActionListener der mit setDefaultActionListener()
	  * wieder gesetzt werden kann.
	  * 
	  * @param defaultListener	der default ActionListener
	  * @see #setDefaultActionListener()
	  */
	void setDefaultActionListener(ActionListener defaultListener);
	
	/**
	 * Setzt den ActionListener der mit setDefaultActionListener(defaultListener)
	 * gesetzt worden ist wieder aktuell.
	 *
	 *@see #setDefaultActionListener(ActionListener defaultListener)
	 */
	 void setDefaultActionListener();
	 
	 /**
	  * Setzt einen KeyListener, der einzelne Tastendrücke mitgeteilt bekommt,
	  * falls das Ausgabesystem dies unterstützt.
	  * 
	  * @param list	der KeyListener.
	  */
	 void setKeyListener(KeyListener list);
	 
	 /**
	  * Gibt den aktuell aktiven ActionListener zurück
	  * 
	  * @return den ActionListener, der aktuell von der Gui bedient wird
	  */
	 ActionListener getActionListener();
	 
	 /**
	  * Schaltet das Input ein, der gesetzte ActionListener wird mit Events bedient.
	  */
	 void enableInput();

	/**
	 * Schaltet das Input ab, der gesetzte ActionListener darf nicht mehr mit
	 * Events bedient werden.
	 */
	 void disableInput();
	 
	 /**
	  * Fordert das neuzeichnen der Ausgabe, alle vorher aufgerufenen print
	  * methoden müssen dargestellt werden.
	  */
	 void redraw();

	/**
	 *Übergibt dem GUI die Kontrolle
	 */
	 void start();
	 
	 /**
	  * Das GUI muss die Inputzeile bereinigen, falls dies möglich ist
	  */
	 void resetInput();
	 
	 /**
	  * Fragt das GUI ob es in einer Applet Sandbox läuft
	  * 
	  * @return	true wenn das der Fall is, false sonst
	  */
	 boolean isApplet();
	 
	 /**
	  * Setzt die Eingabezeile auf newline
	  * 
	  * @param newline	der neue Text der Eingabezeile
	  */
	 void setInput(String newline);
	 
	 /**
	  * Holt den aktuellen Wert der Eingabezeile
	  * @return den aktuellen Wert der Eingabezeile
	  */
	 String getInput() ;

	
	/**
	 * Startet den Splashscreen (Fenster was den Ladevorgang illustriert)
	 * @param numOfSteps wieviele Schritte ausgeführt werden
	 */
	void splash_start(int numOfSteps);
	
	/**
	 * stellt aufdem Splashscreen den Fortschritt um einen weiteren Task da
	 * @param message Nachricht die für den Fortschritt ausgegeben werden soll
	 */
	void splash_next(String message);
	
	/**
	 * Beendet und schliesst das UserInterface
	 */
	void close();
	
	/**
	 * Zeigt ein Bild an, wenn das Gui dieses Unterstützt
	 * 
	 * @param imageStream
	 */
	public void showImage(InputStream imageStream);
	 
}