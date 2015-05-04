package Benchmark.OtherSpecimens;

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

public class UserSpecsSpecimen extends Specimen{

	List<String> _specs_list = new ArrayList<String>();
	
	@Override
	public String sayHello(){
		String cx = _dcx?"out":"";
		return "User-defined satisfied specifications time with"+cx+" counterexample generation.";
	}
	
	@Override
	public boolean resultIsCorrect() {
		return !_result.contains("is false");
	}
	
	@Override
	public void throwException() throws Exception {
		throw new Exception("Model is not verifyable!\n"+_result);
	}
	
	@Override
	public Object init(Object[] args) {
		_specs_list.add("EF (Day&Normal);");
		_specs_list.add("EF (Day&Fire);");
		_specs_list.add("EF (Day&Housebreaking);");
		_specs_list.add("EF (Night&Normal);");
		_specs_list.add("EF (Night&Fire);");
		_specs_list.add("EF (Night&Housebreaking);");
		_specs_list.add("EF (Weekend&Normal);");
		_specs_list.add("EF (Weekend&Fire);");
		_specs_list.add("EF (Weekend&Housebreaking);");
		_specs_list.add("EF (Day&Bright);");
		_specs_list.add("EF (Day&Dark);");
		
		return super.init(args);
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
		
		new Connection(night, "day", day);
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
		
		new Connection(t_normal, "cold", low);
		new Connection(low, "ok", t_normal);
		new Connection(t_normal, "warm", high);
		new Connection(high, "ok", t_normal);
		
		_verifier = new ConesCModelVerifier(diagram);
		_specification += _specs_list.size()>0?_specs_list.remove(0):"";
		return new NuSMVResultParser();
	}

}