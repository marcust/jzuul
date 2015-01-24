/*
 * 	CVS: $Id: Splash.java,v 1.9 2004/07/16 16:22:33 marcus Exp $
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


package org.jzuul.gdk.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * SplashScreen
 * Der SplashScreen öffnet ein Fenster und stellt die Abarbeitung von Aufgaben grafisch dar.
 * 
 * 
 * @version $Revision: 1.9 $
 */
public class Splash {
	private int percentPerTask;
	private int numberOfTasks;
	private int percent;
	private Display display;
	private Shell shell;
	private Label taskName;
	private ProgressBar bar;
	private int currentCount = 0;

	public Splash(Display display) {
		this.percent = 0;
		this.display = display;
	}

	/**
	 * öffnet den splashscreen
	 *
	 * @param numberOfTasks anzahl der tasks die abgearbeitet werden sollen
	 * @param firstTask Name des ersten Tasks
	 */
	public void show(int numberOfTasks, String firstTask) {
		setNumberOfTasks(numberOfTasks);
		shell = new Shell(display, SWT.ON_TOP);
		shell.setText("Jzuul GDK");

		GridLayout layout = new GridLayout();
		layout.numColumns= 1;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		

		shell.setLayout(layout);

		Label label = new Label(shell, SWT.NONE);
		Image image =Util.getImagefromResource(display, "etc/artwork/logo-big.png");
		if (image != null) label.setImage(image);

		this.taskName = new Label(shell, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		this.taskName.setLayoutData(gridData);

		this.bar = new ProgressBar(shell, SWT.HORIZONTAL);
		this.bar.setMinimum(0);
		this.bar.setMaximum(100);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		this.bar.setLayoutData(gridData);
		taskName.setText(firstTask);
		shell.pack();
		Util.centerWindow(shell);
		shell.open();

	}
	/**
	 * setzt die Anzahl der tasks
	 * @param number anzahl der tasks
	 */
	private void setNumberOfTasks(int number) {
		this.numberOfTasks = number;
		percentPerTask = Math.round(100f / number);
	}

	/**
	 * nächster task darstellen, also progressbar und tasknamen ändern
	 * @param task Beschreibung des gerade verarbeiteten tasks
	 */
	public void nextTask(String task) {
		percent += percentPerTask;
		taskName.setText(task);
		this.bar.setSelection(percent);
		currentCount++;

		taskName.update();
		this.bar.update();
		if (numberOfTasks == currentCount) {
			this.close(); 
		}
		
		}
		
	/**
	 * Schliesst den Splashscreen
	 *
	 */
	public void close() {
		if(shell != null && !shell.isDisposed())
		  shell.dispose();

	}
}
