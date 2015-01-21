package Benchmark;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;


public class GenerationTimeSpecimen extends Specimen{
	
	@Override
	public long get_value(Object[] args) {
		long stCPU, stUser, stSys, gtCPU, gtUser, gtSys, st, gt;
		//stCPU = Times.getCpuTime();
		//stUser = Times.getUserTime();
		//stSys = Times.getSystemTime();
		st = System.currentTimeMillis();//_tmbean.getThreadCpuTime(_mainID);
		((ConesCModelVerifier)args[0]).generateModel("");
		//gtCPU = Times.getCpuTime();
		//gtUser = Times.getUserTime();
		//gtSys = Times.getSystemTime();
		gt = System.currentTimeMillis();//_tmbean.getThreadCpuTime(_mainID);
		return gt - st;
	}
	@Override
	public Object init(Object[] args) {
		ConesCModelVerifier verifier = new ConesCModelVerifier((ContextDiagram)args[0]);
		for(int i=0;i<10;i++) {
			verifier.generateModel("");
			//verifier.verify();
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
