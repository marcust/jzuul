/*
 * 	CVS: $Id: PropertyComposite.java,v 1.8 2004/07/25 21:40:56 marcus Exp $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

/**
 * 
 * Created on Jul 12, 2004
 * 
 * 
 * @version $Revision: 1.8 $
 */

public class PropertyComposite extends Composite {
	protected Button useable, takeable, deleteonuse;

	protected Text size;

	protected List propertyElements;

	protected final static String SIZE_TIP = Messages.getString("SIZE_TIP"); //$NON-NLS-1$

	protected final static String USEABLE_TIP = Messages.getString("USEABLE_TIP"); //$NON-NLS-1$

	protected final static String TAKEABLE_TIP = Messages.getString("TAKEABLE_TIP"); //$NON-NLS-1$

	protected final static String DELETONUSE_TIP = Messages.getString("DELETEONUSE_TIP"); //$NON-NLS-1$

	protected class CheckInteger implements ModifyListener {
		public void modifyText(ModifyEvent arg0) {
			if (size.getText().length() > 0) {
				try {
					int value = Integer.parseInt(size.getText());
					System.err.println("value is " + value); //$NON-NLS-1$
				} catch (NumberFormatException e) {
					MessageBox mb = new MessageBox(new Shell(arg0.display),
							SWT.ICON_ERROR | SWT.OK);
					mb.setText(Messages.getString("GAMEDITOR_ERROR")); //$NON-NLS-1$
					mb
							.setMessage(Messages.getString("SIZE_ERROR")); //$NON-NLS-1$
					mb.open();
					size.setText("0"); //$NON-NLS-1$
				}
				System.err.println("ModifyEvent: " + arg0.toString()); //$NON-NLS-1$
			}
		}

	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PropertyComposite(Composite arg0, int arg1) {
		super(arg0, arg1);

		//this.setText("Properties:");
		GridData propGridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL);
		this.setLayoutData(propGridData);
		GridLayout thisLayout = new GridLayout(3, false);
		this.setLayout(thisLayout);

		Label sizel = new Label((Composite) this, SWT.NONE);
		sizel.setText(Messages.getString("SIZE")); //$NON-NLS-1$
		sizel.setToolTipText(PropertyComposite.SIZE_TIP);
		size = new Text((Composite) this, SWT.BORDER);
		GridData sizeLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		sizeLayoutData.horizontalSpan = 2;
		size.setLayoutData(sizeLayoutData);
		size.setText("0"); //$NON-NLS-1$
		size.addModifyListener(new CheckInteger());
		size.setToolTipText(PropertyComposite.SIZE_TIP);

		useable = new Button((Composite) this, SWT.CHECK);
		useable.setToolTipText(PropertyComposite.USEABLE_TIP);
		useable.setText(Messages.getString("USEABLE")); //$NON-NLS-1$
		useable.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (useable.getSelection()) {
					deleteonuse.setEnabled(true);
				} else {
					deleteonuse.setEnabled(false);
				}

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// das soll leer sein

			}

		});

		deleteonuse = new Button((Composite) this, SWT.CHECK);
		deleteonuse.setToolTipText(PropertyComposite.DELETONUSE_TIP);
		deleteonuse.setText(Messages.getString("DELETE_ON_USE")); //$NON-NLS-1$
		deleteonuse.setEnabled(false);
		
		takeable = new Button((Composite) this, SWT.CHECK);
		takeable.setToolTipText(PropertyComposite.TAKEABLE_TIP);
		takeable.setText(Messages.getString("TAKEABLE")); //$NON-NLS-1$

	}

	public void showProperties(List propertyElements) {
		this.propertyElements = propertyElements;
		if (propertyElements == null) {
			this.clear();
			return;
		}
		setEnabled(true);
		String sizeText = getPropertyValue("size"); //$NON-NLS-1$
		if (sizeText == null || sizeText == "") { sizeText = "0"; } //$NON-NLS-1$ //$NON-NLS-2$
		size.setText(sizeText);
		useable.setSelection(getBoolProperty("useable")); //$NON-NLS-1$
		takeable.setSelection(getBoolProperty("takeable")); //$NON-NLS-1$
		if (!getBoolProperty("useable")) { //$NON-NLS-1$
			deleteonuse.setEnabled(false);
		} else {
			deleteonuse.setEnabled(true);
			deleteonuse.setSelection(getBoolProperty("deleteonuse")); //$NON-NLS-1$
		}

	}

	public String getPropertyValue(String property) {
		for (Iterator propIter = propertyElements.iterator(); propIter
				.hasNext();) {
			Element element = (Element) propIter.next();
			if (element.getAttributeValue("name").equals(property)) //$NON-NLS-1$
				return element.getAttributeValue("value"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	public boolean getBoolProperty(String property) {
		String val = getPropertyValue(property);
		if (val != null) {
			return val.equals("true"); //$NON-NLS-1$
		} else {
			return false;
		}
	}

	public List getElementList() {
		List retlist = new ArrayList();
		retlist.add(getButtonProperty("useable", useable)); //$NON-NLS-1$
		retlist.add(getButtonProperty("takeable", takeable)); //$NON-NLS-1$
		retlist.add(getButtonProperty("deleteonuse", deleteonuse)); //$NON-NLS-1$
		Element sizeEl = new Element("property"); //$NON-NLS-1$
		sizeEl.setAttribute("name", "size"); //$NON-NLS-1$ //$NON-NLS-2$
		if (size.getText().length() > 0) {
			sizeEl.setAttribute("value", size.getText()); //$NON-NLS-1$
		} else {
			sizeEl.setAttribute("value", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		retlist.add(sizeEl);
		return retlist;
	}

	protected Element getButtonProperty(String name, Button button) {
		Element retval = new Element("property"); //$NON-NLS-1$
		retval.setAttribute("name", name); //$NON-NLS-1$
		if (button.getSelection()) {
			retval.setAttribute("value", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			retval.setAttribute("value", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return retval;
	}

	public void clear() {
		this.useable.setSelection(false);
		this.takeable.setSelection(false);
		this.deleteonuse.setSelection(false);
		this.size.setText("0"); //$NON-NLS-1$
		this.propertyElements = null;
		setEnabled(false);
	}

	public void setEnabled(boolean enabled) {
		this.useable.setEnabled(enabled);
		this.takeable.setEnabled(enabled);
		this.size.setEnabled(enabled);

	}

}