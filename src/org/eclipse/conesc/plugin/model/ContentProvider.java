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

public class ContentProvider extends ContextGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7284264867302458124L;

	public ContentProvider(){
	}
	
	public ContentProvider init() {
		setName("New Content Provider");
		Context ctx1 = new Context();
		ctx1.setName("Context Data 1");
		ctx1.setOnActive("generate data 1");
		ctx1.setOnEnter("");
		ctx1.setOnLeave("");
		ctx1.setLayout(new Rectangle(10,40,100,50));
		addChild(ctx1);
		Context ctx2 = new Context();
		ctx2.setName("Context Data N");
		ctx2.setOnActive("generate data N");
		ctx2.setOnEnter("");
		ctx2.setOnLeave("");
		ctx2.setLayout(new Rectangle(120, 40, 100, 50));
		addChild(ctx2);
		getLayout().setHeight(200);
		return this;
	}
}