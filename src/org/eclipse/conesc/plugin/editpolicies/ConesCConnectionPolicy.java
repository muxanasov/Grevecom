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

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.commands.ConnectionCreateCommand;
import org.eclipse.conesc.plugin.model.commands.ConnectionReconnectCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public class ConesCConnectionPolicy extends GraphicalNodeEditPolicy {
	
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		ConnectionCreateCommand cmd = (ConnectionCreateCommand)request.getStartCommand();
		cmd.setTarget((Context) getHost().getModel());
		return cmd;
	}
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Context source = (Context) getHost().getModel();
		int style = ((Integer) request.getNewObjectType()).intValue();
		ConnectionCreateCommand cmd = new ConnectionCreateCommand(source, style);
		request.setStartCommand(cmd);
		return cmd;
	}
	
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		Connection conn = (Connection)request.getConnectionEditPart().getModel();
		Context newSource = (Context) getHost().getModel();
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
		cmd.setNewSource(newSource);
		return cmd;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		Connection conn = (Connection) request.getConnectionEditPart().getModel();
		Context newTarget = (Context) getHost().getModel();
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
		cmd.setNewTarget(newTarget);
		return cmd;
	}

}
