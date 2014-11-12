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

package org.eclipse.conesc.plugin.model;

import org.eclipse.draw2d.geometry.Rectangle;

public class ContextualActuator extends ContextGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2287749323789025589L;

	public ContextualActuator(){
	}
	
	public ContextualActuator init() {
		setName("New Trigger Group");
		Context ctx1 = new Context();
		ctx1.setName("Trigger 1");
		ctx1.setOnActive("");
		ctx1.setOnEnter("enable X");
		ctx1.setOnLeave("disable X");
		ctx1.setLayout(new Rectangle(10,40,100,75));
		addChild(ctx1);
		Context ctx2 = new Context();
		ctx2.setName("Trigger 2");
		ctx2.setOnActive("");
		ctx2.setOnEnter("disable Y");
		ctx2.setOnLeave("enable Y");
		ctx2.setLayout(new Rectangle(120, 40, 100, 75));
		addChild(ctx2);
		getLayout().setHeight(200);
		return this;
	}
}