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

package org.eclipse.conesc.plugin.verifier;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
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

import Benchmark.BenchmarkUtils;
import Benchmark.PIDHelper;
import Benchmark.Times;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessInfo;

public class ConesCModelVerifier {
	
	private static final String YOUR_SPECIFICATION = "Violation of your specification";
	private static final String TRANSITION_FROM = "Violation of transition from context";
	private static final String REACHABILITY_OF = "Unreachable context";
	private static final String DEADLOCK_AT = "Deadlock at context";
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
	
	public HashMap<String, String> getSpecifications() {
		return specifications;
	}

	public String verify() {
		String result = "";
		//System.out.println("Verify!");
		for (String key : generated.keySet()) {
			String model = FileManager.fwrite(key,generated.get(key));
			//System.out.println("Verifying the model:\n"+generated.get(key));
			try {
				//System.out.println(BinarySelector.getNuSMVBin());
				//JavaSysMon monitor =   new JavaSysMon();
				//System.out.print(monitor.osName());
				//ProcessInfo nusmv = null;
				//long stCPU, stUser, stSys, gtCPU, gtUser, gtSys;
				//stCPU = Times.getCpuTime();
				//stUser = Times.getUserTime();
				//stSys = Times.getSystemTime();
				Process p = new ProcessBuilder(BinarySelector.getNuSMVBin(),model).start();
				
				//gtCPU = Times.getCpuTime();
				//gtUser = Times.getUserTime();
				//gtSys = Times.getSystemTime();
				/*
				long pid = PIDHelper.getPID(p);
				for (ProcessInfo pinfo:monitor.processTable()){
					if(pinfo.getPid() == pid){
						System.out.print(" "+pinfo.getPid()+":"+pid+":"+pinfo.getUserMillis()+" ");
						nusmv = pinfo;
						break;
					}
				}
				*/
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String line = null;
				while ((line = br.readLine()) != null) {
					result += line+"\n";
				}
				p.waitFor();
				p.destroy();
				//System.out.println(" cpu:"+(gtCPU-stCPU)+" user:"+(gtUser-stUser)+" sys:"+(gtSys-stSys));
			} catch (IOException | InterruptedException  e) {
				System.err.println("Exception verifying the model:\n"+generated.get(key));
				e.printStackTrace();
			}
			FileManager.delete(model);
		}
		//System.out.println(result);
		return result;
	}
	
	public void generateModel(String constraints) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		String vars = "";
		String events = "  event : {";
		String next_vars = "";
		String inits = "";
		String nexts = "";
		String nexts_triggers = "";
		HashMap<String, List<String> > triggers = new HashMap<String, List<String> >();
		
		String specs = "";
		String[] formulas = constraints.split(";");
		String spesification = "";
		for(int i=0;i<formulas.length;i++) {
			if ((spesification = translate(formulas[i])).isEmpty()) continue;
			specs += "SPEC "+spesification+"\n";
		}
		
		for(Node group:diagram.getChildrenArray()) {
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			vars += "  " + state_var + " : {";
			next_vars += "  next_" + state_var + " : {";
			nexts += "  next_"+state_var+" :=\n   case\n";
			for(Node child:group.getChildrenArray()) {
				Context ctx = (Context)child;
				String state = child.getName().replaceAll(" ", "");
				// dealing with vars
				vars += state + ", ";
				next_vars += state + ", ";
				
				// checking that all transitions are legal
				boolean transitions = true;
				if (ctx.getSourceConnections().size() == 0) {
					String specification = "AG ("+state_var+" = "+state+" -> AX "+state_var+" = "+state+")";
					specs += "SPEC "+specification+"\n";
					specifications.put(specification, TRANSITION_FROM+" "+group.getName().replaceAll(" ", "")+"."+state);
					transitions = false;
				}
				if (ctx.getSourceConnections().size() == group.getChildrenArray().size() - 1) transitions = false;
				
				// checking that all contexts are reachable
				String specification = "AG (EF "+state_var+" = "+state+")";
				specs += "SPEC "+specification+"\n";
				specifications.put(specification, REACHABILITY_OF+" "+group.getName().replaceAll(" ", "")+"."+state);
				
				// checking deadloacks
				specification = "AG ("+state_var+" = "+state+" -> EF ";
				if (group.getChildrenArray().size() > 2) specification += "(";
				for (Node o_child:group.getChildrenArray()){
					if (o_child.equals(child)) continue;
					String target_name = o_child.getName().replaceAll(" ", "");
					specification += state_var+" = "+target_name+" | ";
				}
				specification = specification.substring(0, specification.length()-3)+")";
				if (group.getChildrenArray().size() > 2) specification += ")";
				specs += "SPEC "+specification+"\n";
				specifications.put(specification, DEADLOCK_AT+" "+group.getName().replaceAll(" ", "")+"."+state);
				
				// dealing with triggers
				if (!ctx.getTriggers().isEmpty()) {
					for(String trigger:ctx.getTriggers().split("\\n")){
						if(!trigger.contains("."))continue;
						String group_name = trigger.split("\\.")[0]+"_state";
						if (!triggers.containsKey(group_name)) triggers.put(group_name, new ArrayList<String>());
						triggers.get(group_name).add("    "+state_var+" != "+state+" & next_"+state_var+" = "+state+" : "+trigger.split("\\.")[1]+";\n");
					}
				}
				
				for(Object obj:ctx.getSourceConnections()) {
					// dealing with events
					Connection con = (Connection)obj;
					String label = con.getLabel();
					if(!label.contains(" iff "))
						events += label.replaceAll(" ", "_") + ", ";
					else
						events += label.split(" iff ")[0].replaceAll(" ", "_") + ", ";
					// dealing with triggers
					String event_condition;
					if(!label.contains(" iff "))
						event_condition = label.replaceAll(" ", "_");
					else
						event_condition = label.split(" iff ")[0].replaceAll(" ", "_");
					nexts += "    "+state_var+" = "+state+" & event = "+event_condition+getDependencies(label)+
							 " : "+con.getTarget().getName().replaceAll(" ", "")+";\n";
					
					// checking if all transitions are legal
					if (transitions) {
						String target_name = con.getTarget().getName().replaceAll(" ", "");
						specification = "AG ("+state_var+" = "+state+" -> AX ("+state_var+" = "+state+" | "+state_var+" = "+target_name+"))";
						specs += "SPEC "+specification+"\n";
						specifications.put(specification, TRANSITION_FROM+" "+group.getName().replaceAll(" ", "")+"."+state);
					}
				}
				// dealing with inits
				if (ctx.isDefault()) {
					inits += "  init("+state_var+") := "+state+";\n";
				}
			}
			vars = vars.substring(0, vars.length()-2) + "};\n";
			next_vars = next_vars.substring(0, next_vars.length()-2) + "};\n";
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
		
		map.put("vars", vars);
		events = events.substring(0, events.length()-2) + "};\n";
		map.put("events", events);
		map.put("next_vars", next_vars);
		map.put("inits", inits);
		map.put("nexts", nexts+nexts_triggers);
		map.put("specs", specs);
		
		generated.put("model.smv", StringTemplate.build(applicationModel, map));
	}
	
	public void generateModel2(String constraints) {
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
		String spesification = "";
		for(int i=0;i<formulas.length;i++) {
			if ((spesification = translate(formulas[i])).isEmpty()) continue;
			specs += "SPEC "+spesification+"\n";
		}
		// checking that all transitions are legal
		for(Node group:diagram.getChildrenArray()){
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			for(Node child:group.getChildrenArray()){
				Context ctx = (Context)child;
				String state_name = ctx.getName().replaceAll(" ", "");
				if (ctx.getSourceConnections().size() == 0) {
					String specification = "AG ("+state_var+" = "+state_name+" -> AX "+state_var+" = "+state_name+")";
					specs += "SPEC "+specification+"\n";
					specifications.put(specification, TRANSITION_FROM+" "+group.getName().replaceAll(" ", "")+"."+state_name);
					continue;
				}
				if (ctx.getSourceConnections().size() == group.getChildrenArray().size() - 1) continue;
				for (Object o:ctx.getSourceConnections()){
					Connection con = (Connection)o;
					String target_name = con.getTarget().getName().replaceAll(" ", "");
					String specification = "AG ("+state_var+" = "+state_name+" -> AX ("+state_var+" = "+state_name+" | "+state_var+" = "+target_name+"))";
					specs += "SPEC "+specification+"\n";
					specifications.put(specification, TRANSITION_FROM+" "+group.getName().replaceAll(" ", "")+"."+state_name);
				}
			}
		}
		
		// checking that all contexts are reachable
		for(Node group:diagram.getChildrenArray()){
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			for(Node child:group.getChildrenArray()){
				String state_name = child.getName().replaceAll(" ", "");
				String specification = "AG (EF "+state_var+" = "+state_name+")";
				specs += "SPEC "+specification+"\n";
				specifications.put(specification, REACHABILITY_OF+" "+group.getName().replaceAll(" ", "")+"."+state_name);
			}
		}
		
		// checking that there are no deadlocks
		for(Node group:diagram.getChildrenArray()){
			String state_var = group.getName().replaceAll(" ", "")+"_state";
			for(Node child:group.getChildrenArray()){
				Context ctx = (Context)child;
				String state_name = ctx.getName().replaceAll(" ", "");
				String specification = "AG ("+state_var+" = "+state_name+" -> EF ";
				if (group.getChildrenArray().size() > 2) specification += "(";
				for (Node o_child:group.getChildrenArray()){
					if (o_child.equals(child)) continue;
					String target_name = o_child.getName().replaceAll(" ", "");
					specification += state_var+" = "+target_name+" | ";
				}
				specification = specification.substring(0, specification.length()-3)+")";
				if (group.getChildrenArray().size() > 2) specification += ")";
				specs += "SPEC "+specification+"\n";
				specifications.put(specification, DEADLOCK_AT+" "+group.getName().replaceAll(" ", "")+"."+state_name);
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
