package Benchmark.SynthesizedSpecimens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.utils.BinarySelector;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;

import Benchmark.BenchmarkUtils;
import Benchmark.Specimen;

public class VerificationSpecimen extends Specimen{

	@Override
	public String sayHello(){
		String cx = _dcx?"out":"";
		return "Verification time with"+cx+" counterexample generation.";
	}
	
	@Override
	public boolean resultIsCorrect(){
		return _result.contains("No errors");
	}
	
	@Override
	public void throwException() throws Exception{
		throw new Exception("The model is not verifyable!\n"+_result);
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
		diagram = BenchmarkUtils.putDependencies(diagram);
		diagram = BenchmarkUtils.putTrigger(diagram);
		_verifier = new ConesCModelVerifier(diagram);
		return new NuSMVResultParser();
	}

}