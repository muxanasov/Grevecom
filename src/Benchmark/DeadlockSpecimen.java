package Benchmark;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
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
				// TODO Auto-generated method stub
				String time = "none";
				while(_isRunning){
					time = BenchmarkUtils.getNuSMVUserTime();
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				synchronized(_time){
					_time = time;
					//System.out.println(_time);
				}
			}
		};
	}
	private static Pattern pattern = Pattern.compile("(\\d{1}):(\\d{2}).(\\d{2})");
	public static long dateParseRegExp(String period) {
	    Matcher matcher = pattern.matcher(period);
	    if (matcher.matches()) {
	        return Long.parseLong(matcher.group(1)) * 60000 
	            + Long.parseLong(matcher.group(2)) * 1000 
	            + Long.parseLong(matcher.group(3)); 
	    } else {
	        //System.out.print("Invalid format " + period);
	    }
	    return 0;
	}
	
	ConesCModelVerifier _verifier = null;
	String _result = "";
	@Override
	public long get_value(Object[] args) {
		long stCPU, stUser, stSys, gtCPU, gtUser, gtSys, st, gt;
		NuSMVResultParser parser = (NuSMVResultParser)args[0];
		_verifier.generateModel("");
		//ProcessInfo nusmv = BenchmarkUtils.getNuSMVProcessInfo();
		stUser = Times.getUserTime();
		
		Thread monitor = new Thread(getNuSMVProcessMonitor());
		monitor.start();
		
		stUser = Times.getUserTime();
		_result = _verifier.verify();
		gtUser = Times.getUserTime();
		_isRunning = false;
		try {
			monitor.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ProcessInfo nusmv = BenchmarkUtils.getNuSMVProcessInfo();
		
		_result = parser.parse(_result, _verifier.getSpecifications(), "");
		if (_result.contains("Unreachable")){
			synchronized(_time){
				long pTime = dateParseRegExp(_time);
				return pTime == 0 ? pTime : pTime+(gtUser-stUser)/1000000;//gtUser-stUser;//nusmv.getUserMillis();//gtCPU - stCPU;
			}
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
