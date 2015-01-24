/*
 * 	CVS: $Id: RoomDetailsComposite.java,v 1.13 2004/07/25 21:40:56 marcus Exp $
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
package org.jzuul.gdk.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.13 $
 */
public class RoomDetailsComposite extends Composite {

    protected RoomPropertyComposite roomProperties;

    protected Element room;

    private Text description;

    private Text image;

    private Combo type;

    private Button findButton;

    private String map;

    /**
     * @param arg0
     * @param arg1
     */
    public RoomDetailsComposite(Composite arg0, int arg1) {
        super(arg0, arg1);
        GridData myDat = new GridData(GridData.FILL_BOTH);
        myDat.horizontalSpan = 4;
        this.setLayoutData(myDat);
        this.setLayout(new GridLayout(1, false));

        // first group, description and image
        Group roomProperty = new Group(this, SWT.NONE);
        GridData rpDat = new GridData(GridData.FILL_HORIZONTAL);
        rpDat.horizontalSpan = 1;
        roomProperty.setLayoutData(rpDat);
        roomProperty.setLayout(new GridLayout(4, false));

        Label dLabel = new Label(roomProperty, SWT.NONE);
        GridData dlData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        dlData.horizontalSpan = 1;
        dLabel.setLayoutData(dlData);
        dLabel.setText(Messages.getString("ROOM_DESCRIPTION")); //$NON-NLS-1$

        description = new Text(roomProperty, SWT.BORDER);
        GridData dData = new GridData(GridData.FILL_HORIZONTAL);
        dData.horizontalSpan = 3;
        description.setLayoutData(dData);

        Label iLabel = new Label(roomProperty, SWT.NONE);
        GridData ilData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        ilData.horizontalSpan = 1;
        iLabel.setLayoutData(ilData);
        iLabel.setText(Messages.getString("ROOM_IMAGE")); //$NON-NLS-1$

        image = new Text(roomProperty, SWT.BORDER);
        GridData iData = new GridData(GridData.FILL_HORIZONTAL);
        iData.horizontalSpan = 2;
        image.setLayoutData(iData);

        findButton = new Button(roomProperty, SWT.PUSH);
        GridData fbData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        fbData.horizontalSpan = 1;
        findButton.setLayoutData(fbData);
        findButton.setText(Messages.getString("SELECT")); //$NON-NLS-1$
        findButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                FileDialog fd = new FileDialog(new Shell(arg0.display), SWT.OPEN);
                fd.setText(Messages.getString("PICK_IMAGE")); //$NON-NLS-1$
                fd.setFileName(image.getText());
                String newName = fd.open();
                if (newName != null) {
                    image.setText(newName);
                }

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label tLabel = new Label(roomProperty, SWT.NONE);
        GridData tlData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        tlData.horizontalSpan = 1;
        tLabel.setLayoutData(tlData);
        tLabel.setText(Messages.getString("TYPE_ROOM")); //$NON-NLS-1$

        type = new Combo(roomProperty, SWT.READ_ONLY);
        GridData tcData = new GridData(GridData.FILL_HORIZONTAL);
        tcData.horizontalSpan = 1;
        type.setLayoutData(tcData);
        type.add("room"); //$NON-NLS-1$
        type.add("transitionroom"); //$NON-NLS-1$
        type.add("beamroom"); //$NON-NLS-1$
        type.select(0);
        type.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent arg0) {
                if (roomProperties != null) roomProperties.dispose();
                if (type.getText().equals("room")) { //$NON-NLS-1$
                    roomProperties = new DefaultRoomPropertyComposite((Composite) RoomDetailsComposite.this,
                            SWT.NONE, room, map);
                    if (room != null) {
                        room.setName("room"); //$NON-NLS-1$
                        room.removeAttribute("final"); //$NON-NLS-1$
                        room.removeAttribute("target"); //$NON-NLS-1$
                        room.setAttribute("class", "org.jzuul.engine.rooms.Room"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                if (type.getText().equals("transitionroom")) { //$NON-NLS-1$
                    roomProperties = new TransitionRoomPropertyComposite((Composite) RoomDetailsComposite.this,
                            SWT.NONE, room, map);
                    if (room != null) {
                        room.removeAttribute("class"); //$NON-NLS-1$
                        room.setName("transitionroom"); //$NON-NLS-1$
                    }
                }
                if (type.getText().equals("beamroom")) { //$NON-NLS-1$
                    roomProperties = new BeamRoomPropertyComposite((Composite) RoomDetailsComposite.this, SWT.NONE,
                            room, map);
                    if (room != null) {
                        room.setName("room"); //$NON-NLS-1$
                        room.removeAttribute("final"); //$NON-NLS-1$
                        room.removeAttribute("target"); //$NON-NLS-1$
                        room.setAttribute("class", "org.jzuul.engine.rooms.BeamRoom"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                setEnabled(!type.getText().equals("transitionroom")); //$NON-NLS-1$
                if (type.getText().equals("transitionroom")) { //$NON-NLS-1$
                    type.setEnabled(true);
                }
                RoomDetailsComposite.this.layout();
                RoomDetailsComposite.this.redraw();

            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
    }

    protected void showRoomDetails(Element roomElement, String mapName) {
        this.cleanUpData();
        this.map = mapName;
        this.room = roomElement;
        String desc = roomElement.getChildText("description"); //$NON-NLS-1$
        if (desc != null && !desc.equals("")) { //$NON-NLS-1$
            this.description.setText(desc);
        } else {
            this.description.setText(""); //$NON-NLS-1$
            this.description.setFocus();
        }
        Element imageEl = roomElement.getChild("image"); //$NON-NLS-1$
        if (imageEl != null) {
            this.image.setText(imageEl.getAttributeValue("file")); //$NON-NLS-1$
        } else {
            this.image.setText(""); //$NON-NLS-1$
        }
        String typeName = getRoomType(roomElement);
        setEnabled(!typeName.equals("transitionroom")); //$NON-NLS-1$
        type.select(type.indexOf(typeName));
        type.notifyListeners(SWT.Selection, new Event());
        type.setEnabled(true);
        roomProperties.showRoomProperties();
    }

    protected String getRoomType(Element roomElement) {
        if (roomElement.getName().equals("transitionroom")) { return "transitionroom"; } //$NON-NLS-1$ //$NON-NLS-2$
        String roomClass = roomElement.getAttributeValue("class"); //$NON-NLS-1$
        if (roomClass == null) return "room"; //$NON-NLS-1$
        roomClass = roomClass.replaceAll(".+\\.", ""); //$NON-NLS-1$ //$NON-NLS-2$
        System.err.println("Room class is " + roomClass); //$NON-NLS-1$
        return roomClass.toLowerCase();
    }

    protected void clear() {
        setEnabled(false);
        if (roomProperties != null) {
            roomProperties.dispose();
            roomProperties = null;
        }
        
        room = null;
        description.setText(""); //$NON-NLS-1$
        image.setText(""); //$NON-NLS-1$
        type.select(0);

    }

    protected void cleanUpData() {
        if (room != null) {
            room.removeChildren("description"); //$NON-NLS-1$
            if (!description.getText().equals("")) { //$NON-NLS-1$
                room.addContent(new Element("description").setText(description.getText())); //$NON-NLS-1$
            }

            room.removeChildren("image"); //$NON-NLS-1$
            if (!image.getText().equals("")) { //$NON-NLS-1$
                room.addContent(new Element("image").setAttribute("file", image.getText())); //$NON-NLS-1$ //$NON-NLS-2$
            }

          
        }

    }

    public void setEnabled(boolean enabled) {
        description.setEnabled(enabled);
        image.setEnabled(enabled);
        type.setEnabled(enabled);
        findButton.setEnabled(enabled);
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

}