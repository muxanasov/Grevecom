import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;


public class Benchmark {
	
	public static int _event = 0;
	public static Long _mainID;
	public static ThreadMXBean _tmbean = null;
	
	public interface Specimen{
		public Object init(Object[] args);
		public Object update(int groups, int contexts_per_group);
		public long get_value(Object[] args);
	}
	
	public static void main(String[] args) {
		
		_tmbean = ManagementFactory.getThreadMXBean();
		_tmbean.setThreadCpuTimeEnabled(true);

		// Get the standard attribute "VmVendor"
		//String vendor = mxbean.getVmVendor();
		long[] tids = _tmbean.getAllThreadIds();
	    ThreadInfo[] tinfos = _tmbean.getThreadInfo(tids);
	    for (ThreadInfo tinfo:tinfos)
	    	if(tinfo.getThreadName().equals("main")) {
	    		_mainID = tinfo.getThreadId();
	    		break;
	    	}
		
		Specimen specimen = new Specimen(){
			@Override
			public long get_value(Object[] args) {
				long startTime, gen_t;
				startTime = _tmbean.getThreadCpuTime(_mainID); //System.currentTimeMillis();
				((ConesCModelVerifier)args[0]).generateModel("");
				gen_t = _tmbean.getThreadCpuTime(_mainID);
				return gen_t - startTime;
			}
			@Override
			public Object init(Object[] args) {
				ConesCModelVerifier verifier = new ConesCModelVerifier((ContextDiagram)args[0]);
				for(int i=0;i<10;i++)
					verifier.generateModel("");
				return verifier;
			}
			@Override
			public Object update(int groups, int contexts_per_group) {
				ContextDiagram diagram = new ContextDiagram();
				_event = 0;
				for(int i = 0;i<groups;i++){
					List<Context> contexts = new ArrayList<Context>();
					for(int j=0;j<contexts_per_group;j++) {
						Context con = new Context();
						con.setName("context"+i+""+j);
						contexts.add(con);
					}
					diagram.addChild(createGroup(contexts, i));
				}
				return new ConesCModelVerifier(diagram);
			}
		};
		
		int[] groups_slice = new int[]{1,2,3,4,5,6,7,8,9,10};
		int[] contexts_slice = new int[]{5,8,10};
		benchmark(groups_slice, contexts_slice, specimen);
		
		groups_slice = new int[]{2,5,10};
		contexts_slice = new int[]{1,2,3,4,5,6,7,8,9,10};
		benchmark(groups_slice, contexts_slice, specimen);
		
		specimen = new Specimen(){
			ConesCModelVerifier _verifier = null;
			String _result = "";
			@Override
			public long get_value(Object[] args) {
				long startTime, gen_t;
				NuSMVResultParser parser = (NuSMVResultParser)args[0];
				_verifier.generateModel("");
				startTime = _tmbean.getThreadCpuTime(_mainID); // top command get CPU time of the process
				_result = _verifier.verify();
				gen_t = _tmbean.getThreadCpuTime(_mainID);
				_result = parser.parse(_result, _verifier.getSpecifications(), "");
				if (_result.contains("Unreachable"))
					return gen_t - startTime;
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
				_event = 0;
				for(int i = 0;i<groups;i++){
					List<Context> contexts = new ArrayList<Context>();
					for(int j=0;j<contexts_per_group;j++) {
						Context con = new Context();
						con.setName("context"+i+""+j);
						contexts.add(con);
					}
					diagram.addChild(createGroup(contexts, i));
				}
				diagram = makeUnreachable(diagram);
				_verifier = new ConesCModelVerifier(diagram);
				return new NuSMVResultParser();
			}
		};
		
		groups_slice = new int[]{2,3,4,5,6,7,8,9,10};
		contexts_slice = new int[]{2,5,10};
		benchmark(groups_slice, contexts_slice, specimen);
		
		groups_slice = new int[]{2,5,10};
		contexts_slice = new int[]{2,3,4,5,6,7,8,9,10};
		benchmark(groups_slice, contexts_slice, specimen);
	}

	public static ContextDiagram makeUnreachable(ContextDiagram diagram) {
		int groups = diagram.getChildrenArray().size();
		if (groups < 2) return diagram; // no deadlocks with less than 2 groups
		// From the last two groups...
		ContextGroup group_1 = (ContextGroup)diagram.getChildrenArray().get(groups-1);
		ContextGroup group_2 = (ContextGroup)diagram.getChildrenArray().get(groups-2);
		int contexts = group_1.getChildrenArray().size();
		// ...take last contexts...
		Context context_1 = (Context)group_1.getChildrenArray().get(contexts-1);
		Context context_2 = (Context)group_2.getChildrenArray().get(contexts-1);
		// ...and their connections, and put mutual exlusive dependencies
		for(Object obj : context_1.getTargetConnections())
			((Connection)obj).setLabel(((Connection)obj).getLabel()+" iff "+context_2.name);
		for(Object obj : context_2.getTargetConnections())
			((Connection)obj).setLabel(((Connection)obj).getLabel()+" iff "+context_1.name);
		return diagram;
	}

	public static void benchmark(int[] groups_slice, int[] contexts_slice, Specimen specimen) {
		// TODO Auto-generated method stub
		
		_event = 0;
		System.out.print("g\\c\t");
		for(int i=0;i<contexts_slice.length;i++)
			System.out.print(contexts_slice[i]+"\t");
		System.out.println("");
		ContextDiagram diagram = new ContextDiagram();
		_event = 0;
		for(int i = 0;i<groups_slice[groups_slice.length-1];i++){
			List<Context> contexts = new ArrayList<Context>();
			for(int j=0;j<contexts_slice[contexts_slice.length-1];j++) {
				Context con = new Context();
				con.setName("context"+i+""+j);
				contexts.add(con);
			}
			diagram.addChild(createGroup(contexts, i));
		}
		Object obj = specimen.init(new Object[]{diagram});
		
		for(int k=0;k<groups_slice.length;k++){
			int groups = groups_slice[k];
			System.out.print(groups+"\t");
			for(int l=0;l<contexts_slice.length;l++){
				int contexts_per_group = contexts_slice[l];
				
				obj = specimen.update(groups, contexts_per_group);
				
				double error = 1;
				double[] deviation = new double[2];
				while (error > 0.333) {
					int measurments = 10;
					long[] gen_ts = new long[measurments];
					while(measurments>0){
						measurments--;
						long value = specimen.get_value(new Object[]{obj});
						if (value <= 0) {
							measurments++;
							continue;
						}
						gen_ts[measurments] = value;
					}
					deviation = deviation(gen_ts);
					error = deviation[1]/deviation[0];
					System.out.print((float)deviation[0]/1000000+"+-"+error/1000000+"\t");
				}
				System.out.print((float)deviation[0]/1000000+"\t");
			}
			System.out.println("");
		}
	}
	
	public static ContextGroup createGroup(List<Context> contexts, int num) {
		ContextGroup grp = new ContextGroup();
		grp.setName("Group"+num);
		for(Context context:contexts) {
			for(Context other:contexts) {
				if (context == other) continue;
				Connection con = new Connection(context,other);
				con.setLabel("event"+_event);
				_event++;
			}
			grp.addChild(context);
		}
		return grp;
	}
	
	public static double[] deviation(long[] values) {
		double avg = 0;
		for(long val:values) avg += val;
		avg = avg/values.length;
		double mean_quad = 0;
		for(int i=0;i<values.length;i++) mean_quad+=(avg-values[i])*(avg-values[i]);
		mean_quad = Math.sqrt(mean_quad/values.length);
		return new double[]{avg,mean_quad};
	}
}
