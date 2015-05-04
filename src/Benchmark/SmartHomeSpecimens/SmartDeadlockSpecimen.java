package Benchmark.SmartHomeSpecimens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.utils.BinarySelector;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;

import Benchmark.Specimen;

public class SmartDeadlockSpecimen extends Specimen{
	
	@Override
	public String sayHello(){
		String cx = _dcx?"out":"";
		return "Smart Deadlock time with"+cx+" counterexample generation.";
	}
	
	@Override
	public boolean resultIsCorrect(){
		return _result.contains("Unreachable");
	}
	
	@Override
	public void throwException() throws Exception{
		throw new Exception("The model has no deadlock!\n"+_result);
	}
	
	@Override
	public Object update(int groups, int contexts_per_group) {
		ContextDiagram diagram = new ContextDiagram();
		
		ContextGroup pg = new ContextGroup("Preferences Group");
		Context day = new Context("Day");
		Context night = new Context("Night");
		Context weekend = new Context("Weekend");
		pg.addChildren(new Node[]{day,night,weekend});
				
		ContextGroup eg = new ContextGroup("Emergency Group");
		Context normal = new Context("Normal");
		Context hb = new Context("Housebreaking");
		Context fire = new Context("Fire");
		eg.addChildren(new Node[]{normal,fire,hb});
		
		ContextGroup lig = new ContextGroup("Light Intencity Group");
		Context dark = new Context("Dark");
		Context bright = new Context("Bright");
		lig.addChildren(new Node[]{dark,bright});
		
		ContextGroup tg = new ContextGroup("Temperature Group");
		Context t_normal = new Context("NormalT");
		Context high = new Context("High");
		Context low = new Context("Low");
		tg.addChildren(new Node[]{t_normal,high,low});
		
		diagram.addChildren(new Node[]{pg,eg,lig,tg});
		
		bright.setTriggers(pg.getName().replaceAll(" ", "")+"."+night.getName().replaceAll(" ", ""));
		
		new Connection(night, "day iff Low", day);
		new Connection(day, "night", night);
		new Connection(night, "weekend", weekend);
		new Connection(weekend, "noweekend", night);
		
		new Connection(normal, "thief", hb);
		new Connection(hb, "normal", normal);
		new Connection(normal, "fire", fire);
		new Connection(fire, "normal", normal);
		new Connection(hb, "fire", fire);
		
		new Connection(dark, "bright", bright);
		new Connection(bright, "dark", dark);
		
		new Connection(t_normal, "cold iff Day", low);
		new Connection(low, "ok", t_normal);
		new Connection(t_normal, "warm", high);
		new Connection(high, "ok", t_normal);
		
		_verifier = new ConesCModelVerifier(diagram);
		return new NuSMVResultParser();
	}

}