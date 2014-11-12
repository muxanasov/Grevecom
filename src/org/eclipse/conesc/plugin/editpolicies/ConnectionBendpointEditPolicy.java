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
package org.eclipse.conesc.plugin.editpolicies;

import org.eclipse.conesc.plugin.model.commands.BendpointCommand;
import org.eclipse.conesc.plugin.model.commands.CreateBendpointCommand;
import org.eclipse.conesc.plugin.model.commands.DeleteBendpointCommand;
import org.eclipse.conesc.plugin.model.commands.MoveBendpointCommand;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;

public class ConnectionBendpointEditPolicy extends BendpointEditPolicy {

	protected Command getCreateBendpointCommand(BendpointRequest request) {
		CreateBendpointCommand com = new CreateBendpointCommand();
		Point p = request.getLocation();
		Connection conn = getConnection();

		conn.translateToRelative(p);

		com.setLocation(p);
		Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
		Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

		conn.translateToRelative(ref1);
		conn.translateToRelative(ref2);

		com.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
		com.setConnection((org.eclipse.conesc.plugin.model.Connection) request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

	protected Command getMoveBendpointCommand(BendpointRequest request) {
		MoveBendpointCommand com = new MoveBendpointCommand();
		Point p = request.getLocation();
		Connection conn = getConnection();

		conn.translateToRelative(p);

		com.setLocation(p);

		Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
		Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

		conn.translateToRelative(ref1);
		conn.translateToRelative(ref2);

		com.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
		com.setConnection((org.eclipse.conesc.plugin.model.Connection) request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		BendpointCommand com = new DeleteBendpointCommand();
		Point p = request.getLocation();
		com.setLocation(p);
		com.setConnection((org.eclipse.conesc.plugin.model.Connection) request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

}
