/*
 * 	CVS: $Id: ObjectCache.java,v 1.3 2004/07/16 16:22:33 marcus Exp $
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
package org.jzuul.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Diese Klasse implementiert einen Cache für Objekte der nur eine bestimmte Anzahl
 * an Objekten enthält. Bei einem vollen Cache werden "wertlose" Objekte gelöscht.
 * 
 * Dabei wird der Wert eines Objektes aus dem Alter in Millisekunden geteilt durch die Anzahl der
 * Zugriffe berechnet. Somit haben alte Objekte die auf die Häufig zugegriffen wird die gleiche
 * Chance im Cache zu bleiben wir junge Objekte, auf die selten zugegriffen wird.
 * 
 * Created on Jun 2, 2004
 * 
 * 
 * @version $Revision: 1.3 $
 */
public class ObjectCache {
    /**
     * Diese Klasse ist ein Wrapper um die hinzugefügten Objekte.
     * Es speichert zusätzlich noch die Zeit zu der das Objekt hinzugefügt wurde
     * und die Anzahl der Zugriffe
     */
    private class CacheObject {
        /**
         * Die Zeit in Millisekunden zu der das Objekt in den Cache eingefügt wurde
         */
        long created;
        /**
         * Die Anzahl der Zugriffe auf dieses Objekt, beginnend bei 1
         */
        long accessCount;
        /**
         * Das eigentliche Objekt
         */
        Object obj;

        /**
         * Erstellt ein neues CacheObject Objekt mit dem Inhalt obj
         * @param obj	das Objekt um den ein CacheObject erstellt werden soll
         */
        public CacheObject(Object obj) {
            this.created = System.currentTimeMillis();
            this.accessCount = 1;
            this.obj = obj;
        }
        
        /**
         * Gibt das eigentliche Objekt zurück
         * 
         * @return	das Objekt das dem Konstruktor übergeben wurden
         */
        public Object value() {
            return obj;
        }
        
        /**
         * Gibt den aktuellen Wert des Objektes zurück. Ein höherer Wert ist dabei schlechter.
         * 
         * @return	den relativen Wert des Objektes im Cache.
         */
        public long getObjectWorth() {
            return ((System.currentTimeMillis() - created) / accessCount);
        }
        
    }
    
    /**
     * Der interne Speicher für die Objekte, eine Map von Key auf CacheObjects
     */
    private Map objectMap;
    /**
     * Die Größe des Caches 
     */
    private int size;
    /**
     * Der Füllstatus
     */
    private int fillStat = 0;
    
    /**
     * Erstellt eine ObjectCache Objekt das die angegebene Anzahl an 
     * Elementen enthalten soll.
     * 
     * @param size	die Anzahl der im Cache zu haltenden Elemente
     */
    public ObjectCache(int size) {
        this.objectMap = new HashMap(size);
        this.size = size;
    }
    
    /**
     * Fragt nach ob ein Objekt zu key in dem Cache ist.
     * Da der Cache Objekte löscht kann es passieren das ein Element
     * nicht mehr in dem Cache ist obwohl es hinzugefügt wurde. Daher
     * muss die aufrufende Methode damit Rechnen das ein Objekt verschwindet.
     * 
     * @param key	der Key zu dem ein Objekt gefunden werden soll
     * @return	true wenn ein Objekt zu dem key existiert, false sonst
     */
    public boolean containsKey(Object key) {
        return objectMap.containsKey(key);
    }

    public boolean containsKey(String key) {
        return objectMap.containsKey(key);
    }
    
    /**
     * Holt das zu dem key zugehörige Objekt
     * 
     * @param key	ein Key der auf ein Objekt zeigt
     * @return	das Objekt aus dem Cache, null falls das Objekt nicht enthalten ist
     */
    public Object get(Object key) {
        if (objectMap.containsKey(key)) {
            ((CacheObject)objectMap.get(key)).accessCount++;
            return ((CacheObject)objectMap.get(key)).value();
        } else {
            return null;
        }
    }
    
    /**
     * Fügt ein Key Value Paar in den ObjectCache ein.
     * Diese Methode ist auch für das entfernen von Objekten aus dem Cache zuständig.
     * 
     * @param key	ein Key für das Value
     * @param value	das dazugehörige Value
     */
    public void put(Object key, Object value) {
        if (this.fillStat == size) {
            long maxValue = -1;
            Object maxValueKey = null;
            for (Iterator objIter = objectMap.keySet().iterator(); objIter.hasNext();) {
                Object oldkey = objIter.next();
                CacheObject co = (CacheObject)objectMap.get(oldkey);
                if ( (maxValue == -1) || ( maxValue < co.getObjectWorth() ) ) {
                    maxValueKey = oldkey;
                    maxValue = co.getObjectWorth();
                }
            }
            objectMap.remove(maxValueKey);
            fillStat--;
        }
        objectMap.put(key,new CacheObject(value));
        fillStat++;
    }
    

}
