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

import org.eclipse.conesc.plugin.model.commands.ContextEditCommand;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

public class ContextEditPolicy extends AbstractEditPolicy {
	
	public Command getCommand(Request req) {
		if(req.getType().equals("edit"))
			return createConetxtEditCommand(req);
		return null;
	}
	
	protected Command createConetxtEditCommand(Request req) {
		ContextEditCommand cmd = new ContextEditCommand();
		cmd.setModel(getHost().getModel());
		if (req.getExtendedData().isEmpty()) return cmd;
		cmd.setNewName((String)req.getExtendedData().get("newName"));
		cmd.setNewOnEnter((String)req.getExtendedData().get("newOnEnter"));
		cmd.setNewOnActive((String)req.getExtendedData().get("newOnActive"));
		cmd.setNewOnLeave((String)req.getExtendedData().get("newOnLeave"));
		cmd.setDefault(((Boolean)req.getExtendedData().get("isDefault")).booleanValue());
		cmd.setError(((Boolean)req.getExtendedData().get("isError")).booleanValue());
		cmd.setTriggers((String)req.getExtendedData().get("triggers"));
		return cmd;
	}

}
