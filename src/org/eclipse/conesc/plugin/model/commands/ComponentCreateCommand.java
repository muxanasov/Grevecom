/*******************************************************************************
 * Copyright (c) 2004, 2010 Elias Volanakis and others.
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *    Mikhail Afanasov - refactoring and modifications
 *******************************************************************************/
package org.eclipse.conesc.plugin.model.commands;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * A command to add a Shape to a ShapeDiagram. The command can be undone or
 * redone.
 * 
 * @author Elias Volanakis
 */
public class ComponentCreateCommand extends Command {

	/** The new shape. */
	private Node newNode;
	/** ShapeDiagram to add to. */
	private final Node parent;
	/** The bounds of the new Shape. */
	private Rectangle bounds;

	/**
	 * Create a command that will add a new Shape to a ShapesDiagram.
	 * 
	 * @param newShape
	 *            the new Shape that is to be added
	 * @param parent
	 *            the ShapesDiagram that will hold the new element
	 * @param bounds
	 *            the bounds of the new shape; the size can be (-1, -1) if not
	 *            known
	 * @throws IllegalArgumentException
	 *             if any parameter is null, or the request does not provide a
	 *             new Shape instance
	 */
	public ComponentCreateCommand(Node newNode, Node parent,
			Rectangle bounds) {
		this.newNode = newNode;
		this.parent = parent;
		this.bounds = bounds;
		setLabel("component creation");
	}

	/**
	 * Can execute if all the necessary information has been provided.
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
		if (newNode == null || parent == null || bounds == null)
			return false;
		return  (parent instanceof ContextDiagram && newNode instanceof ContextGroup)||
				(parent instanceof ContextGroup && newNode instanceof Context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		if ((parent instanceof ContextDiagram && newNode instanceof ContextGroup)||
			(parent instanceof ContextGroup && newNode instanceof Context)) {
			newNode.setLayout(bounds);
			parent.addChild(newNode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		parent.addChild(newNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		parent.removeChild(newNode);
	}

}