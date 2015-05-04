package Benchmark.AdaptiveProtocolStackSpecimens;

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

public class AdaptiveStackSpecimen extends Specimen{
	
	@Override
	public String sayHello(){
		String cx = _dcx?"out":"";
		return "Adaptive Stack time with"+cx+" counterexample generation.";
	}
	
	@Override
	public boolean resultIsCorrect(){
		return _result.contains("No errors");
	}
	
	@Override
	public void throwException() throws Exception{
		throw new Exception("The model not verifyable!\n"+_result);
	}
	
	@Override
	public Object update(int groups, int contexts_per_group) {
		ContextDiagram diagram = new ContextDiagram();
		
		ContextGroup ppg = new ContextGroup("Protocol Parameters Group");
		Context lifetime = new Context("Lifetime");
		Context bandwidth = new Context("Bandwidth");
		Context link = new Context("Link");
		ppg.addChildren(new Node[]{lifetime, bandwidth, link});
		
		ContextGroup ptg = new ContextGroup("Protocol Type Group");
		Context ctp = new Context("CTP");
		Context gossip = new Context("Gossip");
		ptg.addChildren(new Node[]{ctp, gossip});
		
		diagram.addChildren(new Node[]{ppg, ptg});
		
		gossip.setTriggers(ppg.getName().replaceAll(" ", "")+"."+bandwidth.getName().replaceAll(" ", ""));
		
		new Connection(lifetime, "low link quality", link);
		//Connection bandwidth2link = new Connection(bandwidth, "low link quality", link);
		new Connection(link, "low load iff CTP", lifetime);
		new Connection(link, "high load", bandwidth);
		new Connection(lifetime, "high load", bandwidth);
		new Connection(bandwidth, "low load iff CTP", lifetime);
		
		new Connection(ctp, "modile", gossip);
		new Connection(gossip, "static iff Bandwidth", ctp);
		
		_verifier = new ConesCModelVerifier(diagram);
		return new NuSMVResultParser();
	}

}