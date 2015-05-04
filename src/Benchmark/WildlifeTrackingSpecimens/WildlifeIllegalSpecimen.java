package Benchmark.WildlifeTrackingSpecimens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.utils.BinarySelector;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;

import Benchmark.Specimen;

public class WildlifeIllegalSpecimen extends Specimen{
	
	@Override
	public String sayHello(){
		String cx = _dcx?"out":"";
		return "Wildlife Transition time with"+cx+" counterexample generation.";
	}
	
	@Override
	public boolean resultIsCorrect(){
		return _result.contains("Violation of transition");
	}
	
	@Override
	public void throwException() throws Exception{
		throw new Exception("The model has no illegal transitions!\n"+_result);
	}
	
	@Override
	public Object update(int groups, int contexts_per_group) {
		ContextDiagram diagram = new ContextDiagram();
		
		ContextGroup bg = new ContextGroup();
		bg.setName("Battery Group");
		ContextGroup bsg = new ContextGroup();
		bsg.setName("Base Station Group");
		ContextGroup hcg = new ContextGroup();
		hcg.setName("Health Conditions Group");
		ContextGroup ag = new ContextGroup();
		ag.setName("Activity Group");
		
		Context low = new Context();
		low.setName("Low");
		Context normal = new Context();
		normal.setName("Normal");
		bg.addChild(low);
		bg.addChild(normal);
		normal.setDefault(true);
		Connection low2normal = new Connection(low, normal);
		low2normal.setLabel("normal power");
		Connection normal2low = new Connection(normal, low);
		normal2low.setLabel("low power");
		
		Context reachable = new Context();
		reachable.setName("Reachable");
		Context unreachable = new Context();
		unreachable.setName("Unreachable");
		unreachable.setTriggers("ActivityGroup.Running");
		bsg.addChild(reachable);
		bsg.addChild(unreachable);
		unreachable.setDefault(true);
		Connection re2un = new Connection(reachable, unreachable);
		re2un.setLabel("timeout");
		Connection un2re = new Connection(unreachable, reachable);
		un2re.setLabel("beacon");
		
		Context healthy = new Context();
		healthy.setName("Healthy");
		Context diseased = new Context();
		diseased.setName("Diseased");
		hcg.addChild(healthy);
		hcg.addChild(diseased);
		healthy.setDefault(true);
		Connection h2d = new Connection(healthy, diseased);
		h2d.setLabel("abnormal body temperature iff (Resting|Moving)");
		Connection d2h = new Connection(diseased, healthy);
		d2h.setLabel("normal body temperature iff Moving");
		
		Context run = new Context();
		run.setName("Running");
		Context move = new Context();
		move.setName("Moving");
		Context rest = new Context();
		rest.setName("Resting");
		ag.addChild(run);
		ag.addChild(move);
		ag.addChild(rest);
		rest.setDefault(true);
		Connection re2m = new Connection(rest, move);
		re2m.setLabel("acceleration");
		Connection m2re = new Connection(move, rest);
		m2re.setLabel("hegligible GPS difference");
		Connection ru2m = new Connection(run, move);
		ru2m.setLabel("small GPS difference");
		Connection m2ru = new Connection(move, run);
		m2ru.setLabel("large GPS difference iff Healthy");
		
		diagram.addChild(bg);
		diagram.addChild(bsg);
		diagram.addChild(hcg);
		diagram.addChild(ag);
		
		_verifier = new ConesCModelVerifier(diagram);
		return new NuSMVResultParser();
	}

}