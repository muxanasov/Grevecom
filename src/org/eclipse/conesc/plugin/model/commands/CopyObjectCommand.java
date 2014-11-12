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
import java.util.Iterator;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

public class CopyObjectCommand extends Command {
	private ArrayList<Object> list = new ArrayList<Object>();
	public boolean addElement(Object obj) {
		if (!list.contains(obj)) {
			return list.add(obj);
		}
		return false;
	}
	@Override
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		Iterator<Object> it = list.iterator();
		while (it.hasNext()) {
			if (!isCopyable(it.next()))
				return false;
		}
		return true;
	}
	@Override
	public void execute() {
		if (canExecute())
			Clipboard.getDefault().setContents(list);
	}
	@Override
	public boolean canUndo() {
		return false;
	}
	public boolean isCopyable(Object o) {
		if (o instanceof Node || o instanceof Connection)
			return true;
		return false;
	}
}
