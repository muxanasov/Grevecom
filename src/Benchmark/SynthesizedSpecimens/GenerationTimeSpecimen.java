package Benchmark.SynthesizedSpecimens;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;

import Benchmark.BenchmarkUtils;
import Benchmark.Specimen;
import Benchmark.Times;


public class GenerationTimeSpecimen extends Specimen{
	
	@Override
	public long get_value(Object[] args) {
		long stUser, gtUser;
		stUser = Times.getUserTime();
		((ConesCModelVerifier)args[0]).generateModel("");
		gtUser = Times.getUserTime();
		return gtUser - stUser;
	}
	@Override
	public Object init(Object[] args) {
		ConesCModelVerifier verifier = new ConesCModelVerifier((ContextDiagram)args[0]);
		for(int i=0;i<10;i++) {
			verifier.generateModel("");
		}
		return verifier;
	}
	@Override
	public Object update(int groups, int contexts_per_group) {
		ContextDiagram diagram = new ContextDiagram();
		BenchmarkUtils._event = 0;
		for(int i = 0;i<groups;i++){
			List<Context> contexts = new ArrayList<Context>();
			for(int j=0;j<contexts_per_group;j++) {
				Context con = new Context();
				con.setName("context"+i+""+j);
				contexts.add(con);
			}
			diagram.addChild(BenchmarkUtils.createGroup(contexts, i));
		}
		return new ConesCModelVerifier(diagram);
	}

}
