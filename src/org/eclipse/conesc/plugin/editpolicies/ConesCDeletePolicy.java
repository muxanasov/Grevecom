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

import org.eclipse.conesc.plugin.model.commands.DeleteCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class ConesCDeletePolicy extends ComponentEditPolicy {
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		DeleteCommand com = new DeleteCommand();
		com.setModel(getHost().getModel());
		com.setParentModel(getHost().getParent().getModel());
		return com;
	}
}
