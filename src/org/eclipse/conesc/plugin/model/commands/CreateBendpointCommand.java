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

public class CreateBendpointCommand extends BendpointCommand {

	public void execute() {
		ConnectionBendpoint wbp = new ConnectionBendpoint();
		wbp.setRelativeDimensions(getFirstRelativeDimension(),
				getSecondRelativeDimension());
		getConnection().insertBendpoint(getIndex(), wbp);
		super.execute();
	}

	public void undo() {
		super.undo();
		getConnection().removeBendpoint(getIndex());
	}

}
