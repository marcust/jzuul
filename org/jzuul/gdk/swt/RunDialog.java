/*
 * 	CVS: $Id: RunDialog.java,v 1.12 2004/07/25 21:40:56 marcus Exp $
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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.jzuul.engine.gui.utils.Util;

/**
 * RunDialog Class, the Dialog in which the options for 
 * runnig jzuul can be spezified (multithreaded npcs and number
 * of players)
 * 
 */
public class RunDialog extends Dialog {

	private Label numbers;
	private Label numOfPlayers_label;

	private Button threadedNPC_button;

	private Slider slider;

	private Button ok;

	private Button cancel;

	private RunValues runvalues;

	/**
	 * @param arg0
	 */
	public RunDialog(Shell shell) {
		super(shell);
		runvalues = new RunValues();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RunDialog(Shell shell, int style) {
		super(shell, style);
		runvalues = new RunValues();
	}

	public void setRunValues(RunValues runValues) {
		this.runvalues = runValues;
	}

	public RunValues open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
	
		shell.setText(Messages.getString("ENGINE_OPTIONS")); //$NON-NLS-1$
		shell.setImage(Util.getImagefromResource(parent.getDisplay(),"etc/artwork/jz.png")); //$NON-NLS-1$
		shell.setLayout(new GridLayout(2, false));

		threadedNPC_button = new Button(shell, SWT.CHECK);
		threadedNPC_button.setSelection(runvalues.threadedNPCs);
		threadedNPC_button.setText(Messages.getString("THREADED_NPCS")); //$NON-NLS-1$
		threadedNPC_button.setToolTipText(Messages.getString("THREADED_TOOLTIP")); //$NON-NLS-1$
		
		final Button askPlayer = new Button(shell, SWT.CHECK);
		askPlayer.setSelection(runvalues.askPlayerName);
		askPlayer.setText(Messages.getString("ASK_PLAYER_NAME")); //$NON-NLS-1$
		askPlayer.setToolTipText(Messages.getString("ASK_TOOLTIP")); //$NON-NLS-1$
		
		numOfPlayers_label = new Label(shell, SWT.NONE);
		numOfPlayers_label.setText(Messages.getString("NUMBER_OF_PLAYERS")); //$NON-NLS-1$
		
		Composite comp = new Composite(shell,SWT.FILL);
		comp.setLayout(new GridLayout(2,false));
		slider = new Slider(comp, SWT.HORIZONTAL);
		//slider.setSelection(runvalues.numOfPlayers);
		slider.setMinimum(1);
		slider.setMaximum(20);
		slider.setPageIncrement(1);
		slider.setIncrement(1);
		slider.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				numbers.setText("" + slider.getSelection()); //$NON-NLS-1$
				numbers.update();
				if (slider.getSelection() == 1) {
				    askPlayer.setSelection(false);
				    askPlayer.setEnabled(true);
				} else {
				    askPlayer.setSelection(true);
				    askPlayer.setEnabled(false);
				}
				shell.update();
				System.err.println("Slider: " + slider.getSelection()); //$NON-NLS-1$
			}

			public void widgetDefaultSelected(SelectionEvent e) {
					//des g'hört so
			}
			
		});
		numbers = new Label(comp,SWT.NONE);
		numbers.setText("  " +slider.getSelection()); //$NON-NLS-1$

		ok = new Button(shell, SWT.NONE);
		ok.setText(Messages.getString("OK")); //$NON-NLS-1$
		ok.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
				runvalues = new RunValues(threadedNPC_button.getSelection(),
						slider.getSelection(),askPlayer.getSelection());
				shell.dispose();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		GridData okDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		okDat.widthHint = 80;
		ok.setLayoutData(okDat);

		cancel = new Button(shell, SWT.NONE);
		cancel.setText(Messages.getString("CANCEL")); //$NON-NLS-1$
		cancel.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent arg0) {
			    shell.dispose();
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		GridData cancelDat = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		cancelDat.widthHint = 80;
		cancel.setLayoutData(cancelDat);

		this.runvalues = null;
		shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return runvalues;
	}
}