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

public class BehaviorControl extends ContextGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8508717665089635987L;

	public BehaviorControl(){
	}
	
	public BehaviorControl init() {
		setName("New Behavior Control");
		Context ctx1 = new Context();
		ctx1.setName("Variation 1");
		ctx1.setOnActive("perform action 1");
		ctx1.setOnEnter("");
		ctx1.setOnLeave("");
		ctx1.setLayout(new Rectangle(10,40,100,50));
		addChild(ctx1);
		Context ctx2 = new Context();
		ctx2.setName("Variation N");
		ctx2.setOnActive("perform action N");
		ctx2.setOnEnter("");
		ctx2.setOnLeave("");
		ctx2.setLayout(new Rectangle(120, 40, 100, 50));
		addChild(ctx2);
		getLayout().setHeight(200);
		return this;
	}
}