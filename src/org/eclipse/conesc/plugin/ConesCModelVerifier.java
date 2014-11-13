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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.utils.BinarySelector;
import org.eclipse.conesc.plugin.utils.FileManager;
import org.eclipse.conesc.plugin.utils.StringTemplate;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ConesCModelVerifier {
	
	private static final String YOUR_SPECIFICATION = "your specification";
	private static final String TRANSITION_FROM = "transition from";
	private ContextDiagram diagram;
	private HashMap<String, String> generated = new HashMap<String, String>();
	private HashMap<String, String> specifications = new HashMap<String, String>();
	
	private final String applicationModel =
			"MODULE main\n"+
			" VAR\n"+
			"%vars%"+//place for vars
			"%events%"+//place for events
			"%next_vars%"+
			" ASSIGN\n"+
			"%inits%"+//place for inits
			"%nexts%"+//place for nexts
			"%specs%";//place for specs

	public ConesCModelVerifier(ContextDiagram diagram) {
		this.diagram = diagram;
	}

	public String verify(String constraints) {
		generateModel(constraints);
		String result = "";
		for (String spec:specifications.keySet())
			System.out.println(spec+" : "+specifications.get(spec));
		for (String key : generated.keySet()) {
			String model = FileManager.fwrite(key,generated.get(key));
			//System.out.println("Verifying the model:\n"+generated.get(key));
			try {
				Process p = new ProcessBuilder(BinarySelector.getNuSMVBin(),model).start();
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = null;
				String counterexample = "";
				while ((line = br.readLine()) != null) {
					if(line.startsWith("***")||line.isEmpty()) continue;
					System.out.println(line);
					if (!line.startsWith("--")) {
						counterexample += line+"\n";
						continue;
					}
					if (!line.contains("is true") && !line.contains("is false")) continue;
					for (String spec:specifications.keySet()){
						if (!line.contains(spec)) continue;
						if (line.contains("is false")) {
							line = "Violation of " + specifications.get(spec)+".";
							break;
						}
						if (line.contains("is true")) {
							line = "";
							break;
						}
					}
					result += counterexample+line+"\n";
					counterexample = "";
				}
				result += counterexample;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Exception verifying the model:\n"+generated.get(key));
				e.printStackTrace();
			}
			FileManager.delete(model);
		}
		
		
		return result;
	}
	
	private void generateModel(String constraints) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		String vars = "";
		for(Node group:diagram.getChildrenArray()) {
			String g_name = group.getName().replaceAll(" ", "");
			vars += "  " + g_name + "_state : {";
			for(Node ctx:group.getChildrenArray())
				vars += ctx.getName().replaceAll(" ", "") + ", ";
			vars = vars.substring(0, vars.length()-2) + "};\n";
		}
		map.put("vars", vars);
		
		String events = "  event : {";
		for(Node group:diagram.getChildrenArray())
			for(Node child:group.getChildrenArray()) {
				Context ctx = (Context)child;
				for(Object con:ctx.getSourceConnections()) {
					String label = ((Connection)con).getLabel();
					if(!label.contains(" iff "))
						events += label.replaceAll(" ", "_") + ", ";
					else
						events += label.split(" iff ")[0].replaceAll(" ", "_") + ", ";
				}
			}
		events = events.substring(0, events.length()-2) + "};\n";
		map.put("events", events);
		
		String next_vars = "";
		for(Node group:diagram.getChildrenArray()) {
			String g_name = group.getName().replaceAll(" ", "");
			next_vars += "  next_" + g_name + "_state : {";
			for(Node ctx:group.getChildrenArray())
				next_vars += ctx.getName().replaceAll(" ", "") + ", ";
			next_vars = next_vars.substring(0, next_vars.length()-2) + "};\n";
		}
		map.put("next_vars", next_vars);
		
		String inits = "";
		for(Node group:diagram.getChildrenArray())
			for(Node child:group.getChildrenArray()) {
				Context ctx = (Context)child;
				if (ctx.isDefault()) {
					inits += "  init("+group.getName().replaceAll(" ", "") + "_state) := "+
							ctx.getName().replaceAll(" ", "") + ";\n";
					break;
				}
			}
		map.put("inits", inits);
		
		String nexts = "";
		String nexts_triggers = "";
		HashMap<String, List<String> > triggers = new HashMap<String, List<String> >();
		for(Node group:diagram.getChildrenArray()) {
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			nexts += "  next_"+state_var+" :=\n   case\n";
			for(Node child:group.getChildrenArray()) {
				Context ctx = (Context)child;
				String state_name = ctx.getName().replaceAll(" ", "");
				for(Object obj:ctx.getSourceConnections()) {
					Connection con = (Connection)obj;
					String label = con.getLabel();
					String event_condition;
					if(!label.contains(" iff "))
						event_condition = label.replaceAll(" ", "_");
					else
						event_condition = label.split(" iff ")[0].replaceAll(" ", "_");
					nexts += "    "+state_var+" = "+state_name+" & event = "+event_condition+getDependencies(label)+
							 " : "+con.getTarget().getName().replaceAll(" ", "")+";\n";
					// dealing with triggers
					if (ctx.getTriggers().isEmpty()) continue;
					for(String trigger:ctx.getTriggers().split("\\n")){
						if(!trigger.contains("."))continue;
						String group_name = trigger.split("\\.")[0]+"_state";
						if (!triggers.containsKey(group_name)) triggers.put(group_name, new ArrayList<String>());
						triggers.get(group_name).add("    "+state_var+" != "+state_name+" & next_"+state_var+" = "+state_name+" : "+trigger.split("\\.")[1]+";\n");
					}
				}
			}
			nexts += "    TRUE : "+state_var+";\n   esac;\n";
		}
		// still dealing with triggers....
		for(Node group:diagram.getChildrenArray()) {
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			nexts_triggers += "  next("+state_var+") :=\n   case\n";
			if (triggers.containsKey(state_var))
				for(String trigger:triggers.get(state_var))
					nexts_triggers += trigger;
			nexts_triggers += "    TRUE : next_" + state_var + ";\n   esac;\n";
		}
		// DONE!
		map.put("nexts", nexts+nexts_triggers);
		
		String specs = "";
		String[] formulas = constraints.split(";");
		for(int i=0;i<formulas.length;i++) {
			String spesification = translate(formulas[i]);
			specs += "SPEC "+spesification+"\n";
			//specifications.put(spesification, YOUR_SPECIFICATION);
		}
		// checking that all transitions are legal
		for(Node group:diagram.getChildrenArray()){
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			for(Node child:group.getChildrenArray()){
				Context ctx = (Context)child;
				String state_name = ctx.getName().replaceAll(" ", "");
				if (ctx.getSourceConnections().size() == group.getChildrenArray().size() - 1) continue;
				for (Object o:ctx.getSourceConnections()){
					Connection con = (Connection)o;
					String target_name = con.getTarget().getName().replaceAll(" ", "");
					String specification = "AG ("+state_var+" = "+state_name+" -> AX ("+state_var+" = "+state_name+" | "+state_var+" = "+target_name+"))";
					specs += "SPEC "+specification+"\n";
					specifications.put(specification, TRANSITION_FROM+" "+state_name);
				}
			}
		}
				
		map.put("specs", specs);
		
		generated.put("model.smv", StringTemplate.build(applicationModel, map));
	}

	private String getDependencies(String label) {
		String deps = "";
		if(!label.contains(" iff "))
			return deps;
		return " & "+translate(label.split(" iff ")[1]);
	}
	
	private String translate(String input) {
		String output = input;
		for(Node group:diagram.getChildrenArray()){
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			for(Node child:group.getChildrenArray()){
				if(!output.contains(child.getName())) continue;
				output = output.replaceAll(child.getName(), state_var+" = "+child.getName().replaceAll(" ", ""));
			}
		}
		return output;
	}

}
