/*******************************************************************************
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mikhail Afanasov - initial API and implementation
?*******************************************************************************/

package org.eclipse.conesc.plugin.editpolicies;

import org.eclipse.conesc.plugin.model.commands.ConesCGenerateCommand;
import org.eclipse.conesc.plugin.model.commands.ConesCVerifyCommand;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

public class ConesCVerifyPolicy extends AbstractEditPolicy {
	
	public Command getCommand(Request req) {
		if(req.getType().equals("verify"))
			return createVerifyCommand(req);
		return null;
	}
	
	protected Command createVerifyCommand(Request req) {
		ConesCVerifyCommand cmd = new ConesCVerifyCommand();
		cmd.setModel(getHost().getModel());
		if (req.getExtendedData().isEmpty()) return cmd;
		cmd.setConstraints((String)req.getExtendedData().get("constraints"));
		return cmd;
	}

}

