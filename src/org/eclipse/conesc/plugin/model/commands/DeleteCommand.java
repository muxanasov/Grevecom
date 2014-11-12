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

package org.eclipse.conesc.plugin.model.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.gef.commands.Command;

public class DeleteCommand extends Command {
	private Node model;
	private Node parentModel;
	private List<Connection> connections = null;
	
	public void execute() {
		if (model instanceof Context) {
			Context ctx = (Context)model;
			connections = new ArrayList<Connection>();
			connections.addAll(ctx.getSourceConnections());
			connections.addAll(ctx.getTargetConnections());
			for (Connection con : connections)
				con.disconnect();
		}
		this.parentModel.removeChild(model);
	}
	public void setModel(Object model) {
		this.model = (Node)model;
	}
	public void setParentModel(Object model) {
		this.parentModel = (Node)model;
	}
	public void undo() {
		this.parentModel.addChild(model);
		if (connections == null) return;
		if (connections.isEmpty()) return;
		for (Connection con : connections)
			con.reconnect();
	}
}
