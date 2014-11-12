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
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.ConnectionBendpoint;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

public class PasteObjectCommand extends Command {
	private HashMap<Object, Object> list = new HashMap<Object, Object>();
	@Override
	public boolean canExecute() {
		ArrayList<Object> bList = (ArrayList<Object>) Clipboard.getDefault().getContents();
		if (bList == null || bList.isEmpty())
			return false;
		Iterator<Object> it = bList.iterator();
		while (it.hasNext()) {
			Object o = (Object)it.next();
			if (isPastable(o)) {
				list.put(o, null);
			}
		}
		return true;
	}
	@Override
	public void execute() {
		if (!canExecute())
			return ;
		Iterator<Object> it = list.keySet().iterator();
		while (it.hasNext()) {
			Object o = (Object)it.next();
			try {
				if (o instanceof Context) {
					Context ctx = (Context) o;
					// if its orphan then clone
					// it will be cloned otherwise in a new cloned parent
					if (!list.containsKey(ctx.getParent())) {
						Context clone = (Context) ctx.clone();
						list.put(o, clone);
					}
				}
				else if (o instanceof ContextGroup) {
					ContextGroup ctxgrp = (ContextGroup) o;
					ContextGroup clone = (ContextGroup) ctxgrp.clone();
					for (Node child : ctxgrp.getChildrenArray()) {
						// if the child is in the list, clone it and attach to the new parent
						if (list.containsKey(child)) {
							Context ctxclone = (Context)((Context)child).clone();
							clone.addChild((Node)ctxclone);
							list.put(child, ctxclone);
						}
					}
					list.put(o, clone);
				}
			}catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		// we need to deal with the connections only afterwards, when all contexts are cloned
		// so... another round
		it = list.keySet().iterator();
		while (it.hasNext()) {
			Object o = (Object)it.next();
			if (o instanceof Context) {
				Context ctx = (Context)o;
				// check if there is a context in outgoing connections
				// has to be cloned as well - we need to reconnect them later
				for (Object obj : ctx.getSourceConnections()) {
					Connection con = (Connection)obj;
					if (!list.containsKey(con.getTarget()))
						continue;
					Context source = (Context)list.get(con.getSource());
					Context target = (Context)list.get(con.getTarget());
					// we don't need to clone, since the contexts are new
					// we just connect newly cloned contexts
					Connection clone = new Connection(source,target);
					clone.setLabel(con.getLabel());
					clone.getBendpoints().addAll(con.getBendpoints());
				}
			}
		}
		redo();
	}
	@Override
	public void redo() {
		Iterator<Object> it = list.values().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (isPastable(o)) {
				if (o instanceof Node)
					((Node)o).getParent().addChild((Node)o);
			}
		}
	}
	@Override
	public boolean canUndo() {
		return !(list.isEmpty());
	}
	@Override
	public void undo() {
		Iterator<Object> it = list.values().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			if (isPastable(o)) {
				if(o instanceof Node) {
					((Node)o).getParent().removeChild((Node)o);
				}
			}
		}
	}
	public boolean isPastable(Object o) {
		if (o instanceof Node || o instanceof Connection)
			return true;
		return false;
	}
}
