/*
 * 	CVS: $Id: DefaultRoomPropertyComposite.java,v 1.10 2004/07/25 21:40:56 marcus Exp $
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

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

/**
 * 
 * Created on Jul 14, 2004
 * 
 * 
 * @version $Revision: 1.10 $
 */

public class DefaultRoomPropertyComposite extends RoomPropertyComposite {
	Combo northRoomCombo, eastRoomCombo, southRoomCombo, westRoomCombo,
			upRoomCombo, downRoomCombo;

	Text northRoomDescription, eastRoomDescription, southRoomDescription,
			westRoomDescription, upRoomDescription, downRoomDescription;

	Element room;

	String map;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DefaultRoomPropertyComposite(Composite arg0, int arg1,
			Element roomElement, String mapName) {
		super(arg0, arg1);
		room = roomElement;
		map = mapName;
		// second group: ways and views
		GridData wvDat = new GridData(GridData.FILL_BOTH);
		wvDat.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		this.setLayoutData(wvDat);
		this.setLayout(new GridLayout(5, false));

		//north
		{
			Label nLabel = new Label(this, SWT.NONE);
			nLabel.setText(Messages.getString("NORTH")); //$NON-NLS-1$

			northRoomCombo = new Combo(this, SWT.READ_ONLY);
			GridData nrcData = new GridData(GridData.FILL_HORIZONTAL);
			nrcData.horizontalSpan = 2;
			northRoomCombo.setLayoutData(nrcData);

			northRoomDescription = new Text(this, SWT.BORDER);
			GridData nrdData = new GridData(GridData.FILL_HORIZONTAL);
			nrdData.horizontalSpan = 2;
			northRoomDescription.setLayoutData(nrdData);
			northRoomDescription.setToolTipText(Messages.getString("VIEW_TOOLTIP")); //$NON-NLS-1$
		}
		//east
		{
			Label eLabel = new Label(this, SWT.NONE);
			eLabel.setText(Messages.getString("EAST")); //$NON-NLS-1$

			eastRoomCombo = new Combo(this, SWT.READ_ONLY);
			GridData ercData = new GridData(GridData.FILL_HORIZONTAL);
			ercData.horizontalSpan = 2;
			eastRoomCombo.setLayoutData(ercData);

			eastRoomDescription = new Text(this, SWT.BORDER);
			GridData erdData = new GridData(GridData.FILL_HORIZONTAL);
			erdData.horizontalSpan = 2;
			eastRoomDescription.setLayoutData(erdData);
			eastRoomDescription.setToolTipText(Messages.getString("VIEW_TOOLTIP")); //$NON-NLS-1$
		}
		//south
		{
			Label sLabel = new Label(this, SWT.NONE);
			sLabel.setText(Messages.getString("SOUTH")); //$NON-NLS-1$

			southRoomCombo = new Combo(this, SWT.READ_ONLY);
			GridData srcData = new GridData(GridData.FILL_HORIZONTAL);
			srcData.horizontalSpan = 2;
			southRoomCombo.setLayoutData(srcData);

			southRoomDescription = new Text(this, SWT.BORDER);
			GridData srdData = new GridData(GridData.FILL_HORIZONTAL);
			srdData.horizontalSpan = 2;
			southRoomDescription.setLayoutData(srdData);
			southRoomDescription.setToolTipText(Messages.getString("VIEW_TOOLTIP")); //$NON-NLS-1$
		}
		//west
		{
			Label wLabel = new Label(this, SWT.NONE);
			wLabel.setText(Messages.getString("WEST")); //$NON-NLS-1$

			westRoomCombo = new Combo(this, SWT.READ_ONLY);
			GridData wrcData = new GridData(GridData.FILL_HORIZONTAL);
			wrcData.horizontalSpan = 2;
			westRoomCombo.setLayoutData(wrcData);

			westRoomDescription = new Text(this, SWT.BORDER);
			GridData wrdData = new GridData(GridData.FILL_HORIZONTAL);
			wrdData.horizontalSpan = 2;
			westRoomDescription.setLayoutData(wrdData);
			westRoomDescription.setToolTipText(Messages.getString("VIEW_TOOLTIP")); //$NON-NLS-1$
		}
		//up
		{
			Label uLabel = new Label(this, SWT.NONE);
			uLabel.setText(Messages.getString("UP")); //$NON-NLS-1$

			upRoomCombo = new Combo(this, SWT.READ_ONLY);
			GridData upcData = new GridData(GridData.FILL_HORIZONTAL);
			upcData.horizontalSpan = 2;
			upRoomCombo.setLayoutData(upcData);

			upRoomDescription = new Text(this, SWT.BORDER);
			GridData updData = new GridData(GridData.FILL_HORIZONTAL);
			updData.horizontalSpan = 2;
			upRoomDescription.setLayoutData(updData);
			upRoomDescription.setToolTipText(Messages.getString("VIEW_TOOLTIP")); //$NON-NLS-1$
		}
		//down
		{
			Label dLabel = new Label(this, SWT.NONE);
			dLabel.setText(Messages.getString("DOWN")); //$NON-NLS-1$

			downRoomCombo = new Combo(this, SWT.READ_ONLY);
			GridData docData = new GridData(GridData.FILL_HORIZONTAL);
			docData.horizontalSpan = 2;
			downRoomCombo.setLayoutData(docData);

			downRoomDescription = new Text(this, SWT.BORDER);
			GridData dodData = new GridData(GridData.FILL_HORIZONTAL);
			dodData.horizontalSpan = 2;
			downRoomDescription.setLayoutData(dodData);
			downRoomDescription.setToolTipText(Messages.getString("VIEW_TOOLTIP")); //$NON-NLS-1$
		}

		Button contents = new Button(this, SWT.PUSH);
		contents.setText(Messages.getString("EDIT_CONTENTS")); //$NON-NLS-1$
		contents.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				ContentsEditorDialog ed = new ContentsEditorDialog(new Shell(
						arg0.display), SWT.NONE);
				ed.open(room);
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});

		Button events = new Button(this, SWT.PUSH);
		events.setText(Messages.getString("EDIT_EVENTS")); //$NON-NLS-1$
		events.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				String[] roomEvents = { "playerenter", "playerleave", "timer" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				EventEditorDialog ed = new EventEditorDialog(new Shell(
						arg0.display), SWT.NONE);
				ed.setEvents(roomEvents);
				ed.open(room.getChildren("event")); //$NON-NLS-1$

			}

			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});

	}

	public void showRoomProperties() {
		String[] rooms = JdomHelpers.getRoomListForMap(room, map);
		
		northRoomCombo.setItems(rooms);
		northRoomCombo.add(""); //$NON-NLS-1$
		northRoomCombo.remove(room.getAttributeValue("name")); //$NON-NLS-1$
		
		eastRoomCombo.setItems(rooms);
		eastRoomCombo.add(""); //$NON-NLS-1$
		eastRoomCombo.remove(room.getAttributeValue("name")); //$NON-NLS-1$
				
		southRoomCombo.setItems(rooms);
		southRoomCombo.add(""); //$NON-NLS-1$
		southRoomCombo.remove(room.getAttributeValue("name")); //$NON-NLS-1$
		
		westRoomCombo.setItems(rooms);
		westRoomCombo.add(""); //$NON-NLS-1$
		westRoomCombo.remove(room.getAttributeValue("name")); //$NON-NLS-1$
		
		upRoomCombo.setItems(rooms);
		upRoomCombo.add(""); //$NON-NLS-1$
		upRoomCombo.remove(room.getAttributeValue("name")); //$NON-NLS-1$
		
		downRoomCombo.setItems(rooms);
		downRoomCombo.add(""); //$NON-NLS-1$
		downRoomCombo.remove(room.getAttributeValue("name")); //$NON-NLS-1$
		
		Element description = room.getChild("views"); //$NON-NLS-1$
		if (description != null) {
			List views = description.getChildren("view"); //$NON-NLS-1$
			for (Iterator viewIter = views.iterator(); viewIter.hasNext();) {
				Element viewElement = (Element) viewIter.next();
				if (viewElement.getAttributeValue("direction").equals("north")) { //$NON-NLS-1$ //$NON-NLS-2$
					northRoomDescription.setText(viewElement.getText());
				}
				if (viewElement.getAttributeValue("direction").equals("east")) { //$NON-NLS-1$ //$NON-NLS-2$
					eastRoomDescription.setText(viewElement.getText());
				}
				if (viewElement.getAttributeValue("direction").equals("south")) { //$NON-NLS-1$ //$NON-NLS-2$
					southRoomDescription.setText(viewElement.getText());
				}
				if (viewElement.getAttributeValue("direction").equals("west")) { //$NON-NLS-1$ //$NON-NLS-2$
					westRoomDescription.setText(viewElement.getText());
				}
				if (viewElement.getAttributeValue("direction").equals("above")) { //$NON-NLS-1$ //$NON-NLS-2$
					upRoomDescription.setText(viewElement.getText());
				}
				if (viewElement.getAttributeValue("direction").equals("below")) { //$NON-NLS-1$ //$NON-NLS-2$
					downRoomDescription.setText(viewElement.getText());
				}
			}

		}
		Element ways = room.getChild("ways"); //$NON-NLS-1$
		if (ways != null) {
			List wayList = ways.getChildren("way"); //$NON-NLS-1$
			for (Iterator wayIter = wayList.iterator(); wayIter.hasNext();) {
				Element wayElement = (Element) wayIter.next();
				if (wayElement.getAttributeValue("direction").equals("north")) { //$NON-NLS-1$ //$NON-NLS-2$
					northRoomCombo.select(northRoomCombo.indexOf(wayElement
							.getAttributeValue("room"))); //$NON-NLS-1$
				}
				if (wayElement.getAttributeValue("direction").equals("east")) { //$NON-NLS-1$ //$NON-NLS-2$
					eastRoomCombo.select(eastRoomCombo.indexOf(wayElement
							.getAttributeValue("room"))); //$NON-NLS-1$
				}
				if (wayElement.getAttributeValue("direction").equals("south")) { //$NON-NLS-1$ //$NON-NLS-2$
					southRoomCombo.select(southRoomCombo.indexOf(wayElement
							.getAttributeValue("room"))); //$NON-NLS-1$
				}
				if (wayElement.getAttributeValue("direction").equals("west")) { //$NON-NLS-1$ //$NON-NLS-2$
					westRoomCombo.select(westRoomCombo.indexOf(wayElement
							.getAttributeValue("room"))); //$NON-NLS-1$
				}
				if (wayElement.getAttributeValue("direction").equals("above")) { //$NON-NLS-1$ //$NON-NLS-2$
					upRoomCombo.select(upRoomCombo.indexOf(wayElement
							.getAttributeValue("room"))); //$NON-NLS-1$
				}
				if (wayElement.getAttributeValue("direction").equals("below")) { //$NON-NLS-1$ //$NON-NLS-2$
					downRoomCombo.select(downRoomCombo.indexOf(wayElement
							.getAttributeValue("room"))); //$NON-NLS-1$
				}
			}

		}
	}

	public void clear() {
		northRoomCombo.removeAll();
		northRoomDescription.setText(""); //$NON-NLS-1$

		eastRoomCombo.removeAll();
		eastRoomDescription.setText(""); //$NON-NLS-1$

		southRoomCombo.removeAll();
		southRoomDescription.setText(""); //$NON-NLS-1$

		westRoomCombo.removeAll();
		westRoomDescription.setText(""); //$NON-NLS-1$

		upRoomCombo.removeAll();
		upRoomDescription.setText(""); //$NON-NLS-1$

		downRoomCombo.removeAll();
		downRoomDescription.setText(""); //$NON-NLS-1$

		room = null;
	}

	public void dispose() {
		if (room != null) {

			Element views = new Element("views"); //$NON-NLS-1$
			Element ways = new Element("ways"); //$NON-NLS-1$

			if (!northRoomCombo.getText().equals("")) { //$NON-NLS-1$
				Element way = new Element("way"); //$NON-NLS-1$
				way.setAttribute("direction", "north"); //$NON-NLS-1$ //$NON-NLS-2$
				way.setAttribute("room", northRoomCombo.getText()); //$NON-NLS-1$
				ways.addContent(way);
			}
			if (!northRoomDescription.getText().equals("")) { //$NON-NLS-1$
				Element view = new Element("view"); //$NON-NLS-1$
				view.setAttribute("direction", "north"); //$NON-NLS-1$ //$NON-NLS-2$
				view.setText(northRoomDescription.getText());
				views.addContent(view);
			}

			if (!eastRoomCombo.getText().equals("")) { //$NON-NLS-1$
				Element way = new Element("way"); //$NON-NLS-1$
				way.setAttribute("direction", "east"); //$NON-NLS-1$ //$NON-NLS-2$
				way.setAttribute("room", eastRoomCombo.getText()); //$NON-NLS-1$
				ways.addContent(way);
			}
			if (!eastRoomDescription.getText().equals("")) { //$NON-NLS-1$
				Element view = new Element("view"); //$NON-NLS-1$
				view.setAttribute("direction", "east"); //$NON-NLS-1$ //$NON-NLS-2$
				view.setText(eastRoomDescription.getText());
				views.addContent(view);
			}

			if (!southRoomCombo.getText().equals("")) { //$NON-NLS-1$
				Element way = new Element("way"); //$NON-NLS-1$
				way.setAttribute("direction", "south"); //$NON-NLS-1$ //$NON-NLS-2$
				way.setAttribute("room", southRoomCombo.getText()); //$NON-NLS-1$
				ways.addContent(way);
			}
			if (!southRoomDescription.getText().equals("")) { //$NON-NLS-1$
				Element view = new Element("view"); //$NON-NLS-1$
				view.setAttribute("direction", "south"); //$NON-NLS-1$ //$NON-NLS-2$
				view.setText(southRoomDescription.getText());
				views.addContent(view);
			}

			if (!westRoomCombo.getText().equals("")) { //$NON-NLS-1$
				Element way = new Element("way"); //$NON-NLS-1$
				way.setAttribute("direction", "west"); //$NON-NLS-1$ //$NON-NLS-2$
				way.setAttribute("room", westRoomCombo.getText()); //$NON-NLS-1$
				ways.addContent(way);
			}
			if (!westRoomDescription.getText().equals("")) { //$NON-NLS-1$
				Element view = new Element("view"); //$NON-NLS-1$
				view.setAttribute("direction", "west"); //$NON-NLS-1$ //$NON-NLS-2$
				view.setText(westRoomDescription.getText());
				views.addContent(view);
			}
			if (!upRoomCombo.getText().equals("")) { //$NON-NLS-1$
				Element way = new Element("way"); //$NON-NLS-1$
				way.setAttribute("direction", "above"); //$NON-NLS-1$ //$NON-NLS-2$
				way.setAttribute("room", upRoomCombo.getText()); //$NON-NLS-1$
				ways.addContent(way);
			}
			if (!upRoomDescription.getText().equals("")) { //$NON-NLS-1$
				Element view = new Element("view"); //$NON-NLS-1$
				view.setAttribute("direction", "above"); //$NON-NLS-1$ //$NON-NLS-2$
				view.setText(upRoomDescription.getText());
				views.addContent(view);
			}
			if (!downRoomCombo.getText().equals("")) { //$NON-NLS-1$
				Element way = new Element("way"); //$NON-NLS-1$
				way.setAttribute("direction", "below"); //$NON-NLS-1$ //$NON-NLS-2$
				way.setAttribute("room", downRoomCombo.getText()); //$NON-NLS-1$
				ways.addContent(way);
			}
			if (!downRoomDescription.getText().equals("")) { //$NON-NLS-1$
				Element view = new Element("view"); //$NON-NLS-1$
				view.setAttribute("direction", "below"); //$NON-NLS-1$ //$NON-NLS-2$
				view.setText(downRoomDescription.getText());
				views.addContent(view);
			}

			room.removeChildren("views"); //$NON-NLS-1$
			room.removeChildren("ways"); //$NON-NLS-1$

			if (ways.getChildren().size() > 0) {
			    room.addContent(ways);
			}
			if (views.getChildren().size() > 0) {
			    room.addContent(views);
			}
		}

		super.dispose();
	}

}