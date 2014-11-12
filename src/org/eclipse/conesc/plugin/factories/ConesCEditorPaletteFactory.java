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
package org.eclipse.conesc.plugin.factories;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.conesc.plugin.ConesCPlugin;
import org.eclipse.conesc.plugin.model.BehaviorControl;
import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.ContentProvider;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.ContextualActuator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;

/**
 * Utility class that can create a GEF Palette.
 * 
 * @see #createPalette()
 * @author Elias Volanakis
 */
public final class ConesCEditorPaletteFactory {

	/** Create the "Shapes" drawer. */
	private static PaletteContainer createShapesDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Components");

		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
				"Context", "Create new context", Context.class,
				new ComponentsFactory(Context.class),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW)),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW)));
		componentsDrawer.add(component);

		component = new CombinedTemplateCreationEntry("Context Group",
				"Create a new context group", ContextGroup.class,
				new ComponentsFactory(ContextGroup.class),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)));
		componentsDrawer.add(component);

		return componentsDrawer;
	}
	
	/** Create the "Shapes" drawer. */
	private static PaletteContainer createPatternsDrawer() {
		PaletteDrawer componentsDrawer = new PaletteDrawer("Patterns");

		CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry("Behavior Control",
				"Create a behavior control group", BehaviorControl.class,
				new ComponentsFactory(BehaviorControl.class),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)));
		componentsDrawer.add(component);
		
		component = new CombinedTemplateCreationEntry("Content Provider",
				"Create a content generator group", ContentProvider.class,
				new ComponentsFactory(ContentProvider.class),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)));
		componentsDrawer.add(component);
		
		component = new CombinedTemplateCreationEntry("Contextual Actuator",
				"Create a contextual actuator group", ContextualActuator.class,
				new ComponentsFactory(ContextualActuator.class),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)),
				ImageDescriptor.createFromImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT)));
		componentsDrawer.add(component);

		return componentsDrawer;
	}

	/**
	 * Creates the PaletteRoot and adds all palette elements. Use this factory
	 * method to create a new palette for your graphical editor.
	 * 
	 * @return a new PaletteRoot
	 */
	public static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createShapesDrawer());
		palette.add(createPatternsDrawer());
		return palette;
	}

	/** Create the "Tools" group. */
	private static PaletteContainer createToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");

		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);

		// Add a marquee tool to the group
		toolbar.add(new MarqueeToolEntry());

		// Add (solid-line) connection tool
		tool = new ConnectionCreationToolEntry("Solid connection",
				"Create a solid-line connection", new CreationFactory() {
					@Override
					public Object getNewObject() {
						return null;
					}

					// see ShapeEditPart#createEditPolicies()
					// this is abused to transmit the desired line style
					@Override
					public Object getObjectType() {
						return new Integer(Graphics.LINE_SOLID);
					}
				}, ImageDescriptor.createFromFile(ConesCPlugin.class,
						"icons/connection_s16.gif"),
				ImageDescriptor.createFromFile(ConesCPlugin.class,
						"icons/connection_s24.gif"));
		toolbar.add(tool);

		return toolbar;
	}

	/** Utility class. */
	private ConesCEditorPaletteFactory() {
		// Utility class
	}

}