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

package org.eclipse.conesc.plugin.utils;

import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.widgets.Control;

public class HelperAssistant {
	
	public static void applyAssistentTo(Control control, Node diagram){
	
		try {
			ContentProposalAdapter adapter = new ContentProposalAdapter(
					control, new TextContentAdapter(), 
					new SimpleContentProposalProvider(getAllProposals(diagram)),
					KeyStroke.getInstance("Ctrl+Space"), new char[]{'(','|','&','>'});
			adapter.setFilterStyle(ContentProposalAdapter.FILTER_CHARACTER);
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static String[] getAllProposals(Node diagram) {
		String result = "";
		for(Node group:diagram.getChildrenArray())
			for(Node ctx:group.getChildrenArray())
				result += ctx.getName().replaceAll(" ", "")+",";
		if(result.isEmpty()) return new String[]{};
		return result.substring(0, result.length()-1).split(",");
	}

}
