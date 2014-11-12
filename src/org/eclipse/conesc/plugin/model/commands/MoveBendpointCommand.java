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

import org.eclipse.conesc.plugin.model.ConnectionBendpoint;
import org.eclipse.draw2d.Bendpoint;

public class MoveBendpointCommand extends BendpointCommand {

	private ConnectionBendpoint oldBendpoint;

	public void execute() {
		ConnectionBendpoint bp = new ConnectionBendpoint();
		bp.setRelativeDimensions(getFirstRelativeDimension(),getSecondRelativeDimension());
		setOldBendpoint((ConnectionBendpoint) getConnection().getBendpoints().get(getIndex()));
		getConnection().setBendpoint(getIndex(), bp);
		super.execute();
	}

	protected ConnectionBendpoint getOldBendpoint() {
		return oldBendpoint;
	}

	public void setOldBendpoint(ConnectionBendpoint bp) {
		oldBendpoint = bp;
	}

	public void undo() {
		super.undo();
		getConnection().setBendpoint(getIndex(), getOldBendpoint());
	}

}
