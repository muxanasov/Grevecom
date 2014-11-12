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

package org.eclipse.conesc.plugin.factories;

import java.io.Serializable;

import org.eclipse.conesc.plugin.model.BehaviorControl;
import org.eclipse.conesc.plugin.model.ContentProvider;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.ContextualActuator;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.requests.SimpleFactory;

public class ComponentsFactory extends SimpleFactory{
	
	private Class<?> template;
	
	public ComponentsFactory(Class<?> cls) {
		super(cls);
		template = cls;
	}
	
	@Override
	public Object getNewObject() {
		if (template == Context.class) {
			Context ctx = new Context();
			ctx.setName("New Context");
			ctx.setOnEnter("TODO");
			ctx.setOnActive("TODO");
			ctx.setOnLeave("TODO");
			return ctx;
		}
		if (template == ContextGroup.class) {
			ContextGroup ctxgrp = new ContextGroup();
			ctxgrp.setName("New Context Group");
			return ctxgrp;
		}
		if (template == ContextDiagram.class) {
			ContextDiagram diagram = new ContextDiagram();
			diagram.setName("New Application");
			return diagram;
		}
		if (template == BehaviorControl.class) {
			return new BehaviorControl().init();
		}
		if (template == ContentProvider.class) {
			return new ContentProvider().init();
		}
		if (template == ContextualActuator.class) {
			return new ContextualActuator().init();
		}
		return null;
	}

}
