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
import org.eclipse.conesc.plugin.model.commands.ContextEditCommand;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

public class ConesCGeneratePolicy extends AbstractEditPolicy {
	
	public Command getCommand(Request req) {
		if(req.getType().equals("generate"))
			return createGenerateCommand(req);
		return null;
	}
	
	protected Command createGenerateCommand(Request req) {
		ConesCGenerateCommand cmd = new ConesCGenerateCommand();
		cmd.setModel(getHost().getModel());
		if (req.getExtendedData().isEmpty()) return cmd;
		cmd.setOutput((String)req.getExtendedData().get("output"));
		return cmd;
	}

}

