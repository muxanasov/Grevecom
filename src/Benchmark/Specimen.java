package Benchmark;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.utils.BinarySelector;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;


public class Specimen {
	
	protected ConesCModelVerifier _verifier = null;
	protected String _result = "";
	protected String _specification = "";
	protected boolean _dcx = false;
	
	public Specimen(){}
	
	public Specimen generateCx(boolean c){
		_dcx = !c;
		return this;
	}
	
	public Specimen withSpecs(String specs){
		_specification = specs;
		return this;
	}
	
	public Specimen withSpecsNcx(String specs, boolean c){
		_specification = specs;
		_dcx = !c;
		return this;
	}
	
	public long get_value(Object[] args) throws Exception {
		NuSMVResultParser parser = (NuSMVResultParser)args[0];
		_verifier.generateModel(_specification);
		
		_result = _dcx?_verifier.verify("-dcx"):_verifier.verify();
		//System.out.println(_result);
		Matcher matcher = Pattern.compile("(\\d+).(\\d+) user").matcher(_result);
		long time = 0L;
		if (matcher.find())
			time = BenchmarkUtils.millisFrom2(matcher, BinarySelector.osCheck());
		_result = parser.parse(_result, _verifier.getSpecifications(), _specification);
		if (resultIsCorrect()){
			return time;
		}
		throwException();
		return -1;
	}
	
	public void throwException() throws Exception{
		throw new Exception("This is an abstract class! Redefine the function Specimen.throwException()");
	}
	
	public boolean resultIsCorrect(){
		return false;
	}

	public Object init(Object[] args) {
		System.out.println(sayHello()+"\n");
		_verifier = new ConesCModelVerifier((ContextDiagram)args[0]);
		for(int i=0;i<10;i++)
			_verifier.generateModel("");
		return new NuSMVResultParser();
	}
	
	public String sayHello(){
		return "This is an abstract class, redefine the method Specimen.sayHello()!";
	}
	
	public Object update(int groups, int contexts_per_group) {
		return null;
	}

}
