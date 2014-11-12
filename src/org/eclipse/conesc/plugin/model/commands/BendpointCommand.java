/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Mikhail Afanasov - refactoring and modifications
 *******************************************************************************/
package org.eclipse.conesc.plugin.model.commands;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

public class BendpointCommand extends Command {

	protected int index;
	protected Point location;
	protected Connection connection;
	private Dimension d1, d2;

	protected Dimension getFirstRelativeDimension() {
		return d1;
	}

	protected Dimension getSecondRelativeDimension() {
		return d2;
	}

	protected int getIndex() {
		return index;
	}

	protected Point getLocation() {
		return location;
	}

	protected Connection getConnection() {
		return connection;
	}

	public void redo() {
		execute();
	}

	public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
		d1 = dim1;
		d2 = dim2;
	}

	public void setIndex(int i) {
		index = i;
	}

	public void setLocation(Point p) {
		location = p;
	}

	public void setConnection(Connection con) {
		connection = con;
	}

}
