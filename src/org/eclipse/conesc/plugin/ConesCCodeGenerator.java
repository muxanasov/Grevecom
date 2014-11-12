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

package org.eclipse.conesc.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.utils.FileManager;
import org.eclipse.conesc.plugin.utils.StringTemplate;

public class ConesCCodeGenerator {
	
	private ContextDiagram diagram;
	private HashMap<String, String> generated = new HashMap<String, String>();
	
	//template usage print(template, name, components, name, name, wirings)
	private final String mainAppConfTemplate = 
			"configuration %name%MainAppC {}\n"+//place for app name
			"implementation {\n"+
			"  components\n"+
			"%components%"+//place for ContextGroup components
			"    MainC,\n"+
			"    %name%MainC;"+//place for app name
			"\n"+
			"  %name%MainC.Boot -> MainC;\n"+//place for app name
			"%wirings%"+//place for ContextGroup wirings
			"}";
	private final String mainAppTemplate =
			"module %name%MainC {\n"+//place for application name
			"%uses%"+//place for "uses context group"
			"  uses interface Boot;\n"+
			"}implementation {\n"+
			"  event void Boot.booted() {\n"+
			"  }\n"+
			"%events%"+//place for event void ContextGroup.coontextChanged(context_t con)
			"}";
	private final String contextGroupTemplate =
			"context group %name% {\n"+// place for name
			"%layered%"+//place for suggested layered functions
			"} implementation {\n"+
			" components\n"+
			"%contexts%"+//place for contexts
			"}";
	private final String contextTemplate =
			"context %name% {\n"+// place for name
			"%tnt%"+//place for transitions and triggers
			"} implementation {\n"+
			"%impl%"+//place for implementation
			"}";
	private final String makefileTemplate =
			"COMPONENT = %name%MainAppC\n"+//place for app name
			"include $(MAKERULES)";
	
	public ConesCCodeGenerator(ContextDiagram diagram) {
		this.diagram = diagram;
	}
	
	public void generate(String output) {
		generateMakefile();
		generateAppConfig();
		generateAppMain();
		generateContextGroups();
		generateContexts();
		for (String key : generated.keySet())
			FileManager.fwrite(output+key,generated.get(key));
	}

	private void generateMakefile() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", diagram.getName().replaceAll(" ", ""));
		generated.put("Makefile",StringTemplate.build(makefileTemplate, map));
	}
	
	private void generateAppConfig() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		String components = "";
		String wirings = "";
		for (Node child : diagram.getChildrenArray()) {
			components += "    "+child.getName().replaceAll(" ", "")+",\n";
			wirings += "  %name%MainC."+child.getName().replaceAll(" ", "")+
					   " -> "+child.getName().replaceAll(" ", "")+";\n";
		}
		
		map.put("name", diagram.getName().replaceAll(" ", ""));
		map.put("components", components);
		map.put("wirings", wirings);
		generated.put(diagram.getName().replaceAll(" ", "")+"MainAppC.cnc",StringTemplate.build(mainAppConfTemplate, map));
	}
	
	private void generateAppMain() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		String uses = "";
		String events = "";
		for (Node child : diagram.getChildrenArray()) {
			uses += "  uses context group "+child.getName().replaceAll(" ", "")+";\n";
			events += "  event void "+child.getName().replaceAll(" ", "")+".contextChanged(context_t con) {\n  }\n";
		}
		
		map.put("name", diagram.getName().replaceAll(" ", ""));
		map.put("uses", uses);
		map.put("events", events);
		generated.put(diagram.getName().replaceAll(" ", "")+"MainC.cnc",StringTemplate.build(mainAppTemplate, map));
	}
	
	private void generateContextGroups() {
		for (Node group : diagram.getChildrenArray()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
		
			String name = group.getName().replaceAll(" ", "");
			String contexts = "";
			// for layered functions suggestion
			List<String> onActives = new ArrayList<String>();
			String comment = "";
			for (Node ctx : group.getChildrenArray()) {
				contexts += "    "+ctx.getName().replaceAll(" ", "");
				if (((Context)ctx).isDefault()) contexts += " is default";
				else if (((Context)ctx).isError()) contexts += " is error";
				contexts += ",\n";
				onActives.add(((Context)ctx).getOnActive());
				comment += ctx.getName().replaceAll(" ", "")+".cnc ";
			}
			
			// layered function suggestion
			String suggestions = suggestLayeredFunctions(onActives);
			((ContextGroup)group).setSuggestedFunctions(suggestions);
			if (!suggestions.isEmpty())
				suggestions = "  // suggested layered functions\n  // change the implementation at "+comment+"\n"+suggestions;
			
			contexts = contexts.substring(0, contexts.length()-2) + ";\n";
			map.put("name", name);
			map.put("contexts", contexts);
			map.put("layered", suggestions);
			generated.put(name+".cnc",StringTemplate.build(contextGroupTemplate, map));
		}
	}
	
	private String suggestLayeredFunctions(List<String> list) {
		String functions = "";
		if(list.size() == 0) return functions;
		if(list.size() == 1) return generateSignature(list.get(0));
		String commonWords = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			commonWords = commonWords(commonWords, list.get(i));
			if (commonWords.isEmpty()) commonWords = list.get(i);
		}
		functions = generateSignature(commonWords);
		return functions;
	}
	
	private String commonWords(String str1, String str2) {
		String result = "";
		String[] words = str1.split(" ");
		for (int i = 0; i < words.length; i++)
			if(str2.contains(words[i]))result+=" "+words[i];
		return result;
	}
	
	private String generateSignature(String str) {
		String signature = "";
		String[] words = str.split(" ");
		for(int i = 0; i < words.length; i++){
			if (signature.isEmpty()) {
				signature += words[i];
				continue;
			}
			if (words[i].isEmpty()) continue;
			if (words[i].length() == 1){
				signature += words[i].toUpperCase();
				continue;
			}
			signature += words[i].substring(0, 1).toUpperCase()+words[i].substring(1, words[i].length());
		}
		if (!signature.isEmpty())
			signature = "  layered void "+signature.replaceAll("[^\\w\\s\\-_]", "")+"();\n";
		return signature;
	}

	private void generateContexts() {
		for (Node group : diagram.getChildrenArray()) {
			for (Node child : group.getChildrenArray()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				String name = child.getName().replaceAll(" ", "");
				String tnt = "";
				String impl = "";
				
				Context ctx = (Context)child;
				if (!ctx.getOnEnter().isEmpty()) impl += "  event void activated(){\n"+
														 "    //"+ctx.getOnEnter()+"\n"+
														 "  }\n";
				if (!ctx.getOnLeave().isEmpty()) impl += "  event void deactivated(){\n"+
						 								 "    //"+ctx.getOnLeave()+"\n"+
						 								 "  }\n";
				
				
				
				if (ctx.getSourceConnections().size() != 0) {
					tnt = "  transitions";
					for (Object obj : ctx.getSourceConnections()) {
						Connection con = (Connection)obj;
						tnt += " "+con.getTarget().getName().replaceAll(" ", "")+getDependencies(con.getLabel())+",";
						// TODO add iff (preserved in connection)
					}
					tnt = tnt.substring(0, tnt.length()-1) + ";\n";
				}
				
				if (ctx.getTriggers() != null && !ctx.getTriggers().isEmpty()) {
					tnt += "  triggers";
					String[] triggers = ctx.getTriggers().split("\\n");
					for(int i=0;i<triggers.length;i++)
						tnt += " "+triggers[i]+",";
					tnt = tnt.substring(0, tnt.length()-1) + ";\n";
				}
				
				// layered functions
				String suggestions = ((ContextGroup)ctx.getParent()).getSuggestions();
				if (!suggestions.isEmpty()) {
					impl += "  // suggested layered functions\n  // change the signature at "+group.getName().replaceAll(" ", "")+".cnc\n";
					String[] functions = suggestions.split("\\n");
					for (int i=0;i<functions.length;i++)
						impl += functions[i].substring(0, functions[i].length()-1)+"{\n"+
								"    // "+ctx.getOnActive()+"\n"+
								"  }\n";
				}
				
				map.put("name", name);
				map.put("tnt", tnt);
				map.put("impl", impl);
				generated.put(name+".cnc",StringTemplate.build(contextTemplate, map));
			}
		}
	}
	private String getDependencies(String label) {
		String deps = "";
		if(!label.contains(" iff "))
			return deps;
		return " iff "+translate(label.split(" iff ")[1]);
	}
	
	private String translate(String input) {
		String output = input;
		for(Node group:diagram.getChildrenArray()){
			String state_var = group.getName().replaceAll(" ", "")+".";
			for(Node child:group.getChildrenArray()){
				if(!output.contains(child.getName())) continue;
				output = output.replaceAll(child.getName(), state_var+child.getName().replaceAll(" ", ""));
			}
		}
		return output.replaceAll("\\|", "\\|\\|").replaceAll("\\&", "\\&\\&");
	}

}
