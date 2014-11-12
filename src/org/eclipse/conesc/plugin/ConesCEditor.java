/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Elias Volanakis - initial API and implementation
 * Mikhail Afanasov - refactoring and modifications
?*******************************************************************************/
package org.eclipse.conesc.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EventObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.conesc.plugin.factories.ComponentsFactory;
import org.eclipse.conesc.plugin.factories.ConesCEditPartFactory;
import org.eclipse.conesc.plugin.factories.ConesCEditorPaletteFactory;
import org.eclipse.conesc.plugin.factories.ConesCTreeEditPartFactory;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.model.actions.ConesCGenerateAction;
import org.eclipse.conesc.plugin.model.actions.ConesCVerifyAction;
import org.eclipse.conesc.plugin.model.actions.ContextEditAction;
import org.eclipse.conesc.plugin.model.actions.CopyObjectAction;
import org.eclipse.conesc.plugin.model.actions.PasteObjectAction;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.conesc.plugin.ConesCEditorContextMenuProvider;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.TreeViewer;

/**
 * A graphical editor with flyout palette that can edit .shapes files. The
 * binding between the .shapes file extension and this editor is done in
 * plugin.xml
 * 
 * @author Elias Volanakis
 */
public class ConesCEditor extends GraphicalEditorWithFlyoutPalette {

	/** This is the root of the editor's model. */
	private ContextDiagram model;
	/** Palette component, holding the tools and shapes. */
	private static PaletteRoot PALETTE_MODEL;

	/** Create a new ConesCEditor instance. This is called by the Workspace. */
	public ConesCEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	/**
	 * Configure the graphical viewer before it receives contents.
	 * <p>
	 * This is the place to choose an appropriate RootEditPart and
	 * EditPartFactory for your editor. The RootEditPart determines the behavior
	 * of the editor's "work-area". For example, GEF includes zoomable and
	 * scrollable root edit parts. The EditPartFactory maps model elements to
	 * edit parts (controllers).
	 * </p>
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new ConesCEditPartFactory());
		
		ScalableRootEditPart root = new ScalableRootEditPart();
		viewer.setRootEditPart(root);
		
		ZoomManager manager = root.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));
		
		double[] zoomLevels = new double[]{0.25,0.5,0.75,1.0,1.5,2.0,2.5,3.0,4.0,5.0,10.0,20.0};
		manager.setZoomLevels(zoomLevels);

		ArrayList<String> zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);
		
		KeyHandler keyHandler = new KeyHandler();
		
		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127,0),
					   getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		keyHandler.put(KeyStroke.getPressed(SWT.BS, 127,0),
				   getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		keyHandler.put(KeyStroke.getPressed('+',SWT.KEYPAD_ADD,0),
				   	   getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
		keyHandler.put(KeyStroke.getPressed('-',SWT.KEYPAD_ADD,0),
			   	   	   getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
		
		viewer.setKeyHandler(keyHandler);
		
		ContextMenuProvider provider = new ConesCEditorContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(provider);
		
		
		/*
		 * super.configureGraphicalViewer();
		 * 
		 * GraphicalViewer viewer = getGraphicalViewer();
		 * viewer.setEditPartFactory(new ShapesEditPartFactory());
		 * viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		 * viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
		 * 
		 * // configure the context menu provider ContextMenuProvider cmProvider
		 * = new ConesCEditorContextMenuProvider( viewer, getActionRegistry());
		 * viewer.setContextMenu(cmProvider);
		 * getSite().registerContextMenu(cmProvider, viewer);
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util
	 * .EventObject)
	 */
	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}

	private void createOutputStream(OutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(getModel());
		oos.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#
	 * createPaletteViewerProvider()
	 */
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			@Override
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener,
				// this will enable
				// model element creation by dragging a
				// CombinatedTemplateCreationEntries
				// from the palette into the editor
				// @see ConesCEditor#createTransferDropTargetListener()
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(
						viewer));
			}
		};
	}

	/**
	 * Create a transfer drop target listener. When using a
	 * CombinedTemplateCreationEntry tool in the palette, this will enable model
	 * element creation by dragging from the palette.
	 * 
	 * @see #createPaletteViewerProvider()
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			@Override
			protected CreationFactory getFactory(Object template) {
				return new ComponentsFactory((Class) template);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor
	 * )
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			createOutputStream(out);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true, // keep
																				// saving,
																				// even
																				// if
																				// IFile
																				// is
																				// out
																				// of
																				// sync
																				// with
																				// the
																				// Workspace
					false, // dont keep history
					monitor); // progress monitor
			getCommandStack().markSaveLocation();
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// Show a SaveAs dialog
		Shell shell = getSite().getWorkbenchWindow().getShell();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();

		IPath path = dialog.getResult();
		if (path != null) {
			// try to save the editor's contents under a different file name
			final IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);
			try {
				new ProgressMonitorDialog(shell).run(false, // don't fork
						false, // not cancelable
						new WorkspaceModifyOperation() { // run this operation
							@Override
							public void execute(final IProgressMonitor monitor) {
								try {
									ByteArrayOutputStream out = new ByteArrayOutputStream();
									createOutputStream(out);
									file.create(
											new ByteArrayInputStream(out
													.toByteArray()), // contents
											true, // keep saving, even if IFile
													// is out of sync with the
													// Workspace
											monitor); // progress monitor
								} catch (CoreException ce) {
									ce.printStackTrace();
								} catch (IOException ioe) {
									ioe.printStackTrace();
								}
							}
						});
				// set input to the new file
				setInput(new FileEditorInput(file));
				getCommandStack().markSaveLocation();
			} catch (InterruptedException ie) {
				// should not happen, since the monitor dialog is not cancelable
				ie.printStackTrace();
			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
			}
		}
	}

	@Override
	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class)
			return new ConesCOutlinePage(new TreeViewer());
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();
		return super.getAdapter(type);
	}

	ContextDiagram getModel() {
		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot
	 * ()
	 */
	@Override
	protected PaletteRoot getPaletteRoot() {
		if (PALETTE_MODEL == null)
			PALETTE_MODEL = ConesCEditorPaletteFactory.createPalette();
		return PALETTE_MODEL;
	}

	private void handleLoadException(Exception e) {
		System.err.println("** Load failed. Using default model. **");
		e.printStackTrace();
		model = createContextDiagram();
	}

	/**
	 * Set up the editor's inital content (after creation).
	 * 
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		//super.initializeGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(model);
		viewer.addDropTargetListener(createTransferDropTargetListener());
		/*
		 * GraphicalViewer viewer = getGraphicalViewer();
		 * viewer.setContents(getModel()); // set the contents of this editor
		 * 
		 * // listen for dropped parts
		 * viewer.addDropTargetListener(createTransferDropTargetListener());
		 */
	}
	
	public void createActions() {
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action = new ContextEditAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		action = new CopyObjectAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		action = new PasteObjectAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		action = new ConesCGenerateAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
		action = new ConesCVerifyAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}

	public ContextDiagram createContextDiagram() {
		ContextDiagram diagram = new ContextDiagram();
		diagram.setName("New Application");
		
		return diagram;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		try {
			IFile file = ((IFileEditorInput) input).getFile();
			ObjectInputStream in = new ObjectInputStream(file.getContents());
			model = (ContextDiagram) in.readObject();
			in.close();
			setPartName(file.getName());
		} catch (IOException e) {
			handleLoadException(e);
		} catch (CoreException e) {
			handleLoadException(e);
		} catch (ClassNotFoundException e) {
			handleLoadException(e);
		}
	}

	/**
	 * Creates an outline pagebook for this editor.
	 */
	protected class ConesCOutlinePage extends ContentOutlinePage {
		
		public ConesCOutlinePage(EditPartViewer viewer) {
			super(viewer);
		}
		
		public void createControl(Composite parent) {
			// create outline viewer page
			getViewer().createControl(parent);
			// configure outline viewer
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new ConesCTreeEditPartFactory());
			// hook outline viewer
			getSelectionSynchronizer().addViewer(getViewer());
			// initialize outline viewer with model
			getViewer().setContents(model);
			// show outline viewer
			
			
		}
		
		public void init(IPageSite pageSite) {
			super.init(pageSite);
			
			IActionBars bars = getSite().getActionBars();
			bars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
										getActionRegistry().getAction(ActionFactory.UNDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.REDO.getId(),
										getActionRegistry().getAction(ActionFactory.REDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
										getActionRegistry().getAction(ActionFactory.DELETE.getId()));
			bars.setGlobalActionHandler(ActionFactory.COPY.getId(), 
										getActionRegistry().getAction(ActionFactory.COPY.getId()));
			bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), 
										getActionRegistry().getAction(ActionFactory.PASTE.getId()));

			
			ContextMenuProvider provider = new ConesCEditorContextMenuProvider(getViewer(), getActionRegistry());
			getViewer().setContextMenu(provider);
			
		}
		
		public Control getControl() {
			return getViewer().getControl();
		}
		
		public void dispose() {
			getSelectionSynchronizer().removeViewer(getViewer());
			super.dispose();
		}
		
	}

}