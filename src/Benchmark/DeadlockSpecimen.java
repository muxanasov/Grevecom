package Benchmark;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;


public class DeadlockSpecimen extends Specimen{
	
	ConesCModelVerifier _verifier = null;
	String _result = "";
	@Override
	public long get_value(Object[] args) {
		long stCPU, stUser, stSys, gtCPU, gtUser, gtSys, st, gt;
		NuSMVResultParser parser = (NuSMVResultParser)args[0];
		_verifier.generateModel("");
		//ProcessInfo nusmv = BenchmarkUtils.getNuSMVProcessInfo();
		//stUser = nusmv.getUserMillis();
		_result = _verifier.verify();
		//ProcessInfo nusmv = BenchmarkUtils.getNuSMVProcessInfo();
		//gtUser = nusmv.getSystemMillis();
		_result = parser.parse(_result, _verifier.getSpecifications(), "");
		if (_result.contains("Unreachable"))
			return 0;//nusmv.getUserMillis();//gtCPU - stCPU;
		System.err.println("Model has no unreachables!\n"+_result);
		return -1;
	}
	@Override
	public Object init(Object[] args) {
		_verifier = new ConesCModelVerifier((ContextDiagram)args[0]);
		for(int i=0;i<10;i++)
			_verifier.generateModel("");
		return new NuSMVResultParser();
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
		diagram = BenchmarkUtils.makeUnreachable(diagram);
		_verifier = new ConesCModelVerifier(diagram);
		return new NuSMVResultParser();
	}

}
