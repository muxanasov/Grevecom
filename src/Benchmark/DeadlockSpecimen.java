package Benchmark;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.utils.BinarySelector;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;


public class DeadlockSpecimen extends Specimen{
	
	private boolean _isRunning = true;
	private volatile String _time = "-1";
	
	private Runnable getNuSMVProcessMonitor(){
		_isRunning = true;
		return new Runnable(){
			@Override
			public void run() {
				String time = "none";
				while(_isRunning){
					time = BenchmarkUtils.getNuSMVUserTime();
				}
				_time = time;
			}
		};
	}
	
	ConesCModelVerifier _verifier = null;
	String _result = "";
	@Override
	public long get_value(Object[] args) {
		
		NuSMVResultParser parser = (NuSMVResultParser)args[0];
		_verifier.generateModel("");
		
		Thread monitor = new Thread(getNuSMVProcessMonitor());
		monitor.start();
		_result = _verifier.verify();
		_isRunning = false;
		
		_result = parser.parse(_result, _verifier.getSpecifications(), "");
		if (_result.contains("Unreachable")){
			try {
				monitor.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(" "+_time+" ");
			return BenchmarkUtils.millisFrom(_time);
		}
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
