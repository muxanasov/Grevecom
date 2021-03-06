package Benchmark;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.utils.BinarySelector;
import org.eclipse.core.runtime.Path;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;


public class BenchmarkUtils {
	
	public static int _event = 0;
	public static Pattern _pattern = Pattern.compile("(\\d+):(\\d+).(\\d+)");
	public static Pattern _win_pattern = Pattern.compile("(\\d+):(\\d+):(\\d+)");
	
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
	public static ContextDiagram putDependencies(ContextDiagram diagram) {
		int groups = diagram.getChildrenArray().size();
		if (groups < 2) return diagram; // no deadlocks with less than 2 groups
		// From the last two groups...
		ContextGroup group_1 = (ContextGroup)diagram.getChildrenArray().get(groups-1);
		ContextGroup group_2 = (ContextGroup)diagram.getChildrenArray().get(groups-2);
		int contexts = group_1.getChildrenArray().size();
		// ...take last contexts...
		Context context_1 = (Context)group_1.getChildrenArray().get(contexts-1);
		Context context_2 = (Context)group_2.getChildrenArray().get(contexts-1);
		// ...and their connections, and put dependencies
		for(Object obj : context_1.getSourceConnections())
			((Connection)obj).setLabel(((Connection)obj).getLabel()+" iff "+context_2.name);
		for(Object obj : context_2.getSourceConnections())
			((Connection)obj).setLabel(((Connection)obj).getLabel()+" iff "+context_1.name);
		return diagram;
	}
	public static ContextDiagram putTrigger(ContextDiagram diagram) {
		int groups = diagram.getChildrenArray().size();
		if (groups < 2) return diagram; // no deadlocks with less than 2 groups
		// From the last two groups...
		ContextGroup group_1 = (ContextGroup)diagram.getChildrenArray().get(groups-1);
		ContextGroup group_2 = (ContextGroup)diagram.getChildrenArray().get(groups-2);
		int contexts = group_1.getChildrenArray().size();
		// ...take last contexts...
		Context context_1 = (Context)group_1.getChildrenArray().get(0);//contexts-1);
		Context context_2 = (Context)group_2.getChildrenArray().get(0);//contexts-1);
		// ...and their connections, and put a trigger
		context_1.setTriggers(group_2.name.replaceAll(" ","")+"."+context_2.name.replaceAll(" ", ""));
		int connections = context_2.getSourceConnections().size();
		// remove one connection
		((Connection)(context_2.getSourceConnections().get(connections-1))).disconnect();
		return diagram;
	}
	
	public static ContextDiagram putIllegalTransition(ContextDiagram diagram) {
		int groups = diagram.getChildrenArray().size();
		if (groups < 2) return diagram; // no deadlocks with less than 2 groups
		// From the last two groups...
		ContextGroup group_1 = (ContextGroup)diagram.getChildrenArray().get(groups-1);
		ContextGroup group_2 = (ContextGroup)diagram.getChildrenArray().get(groups-2);
		int contexts = group_1.getChildrenArray().size();
		// ...take last contexts...
		Context context_1 = (Context)group_1.getChildrenArray().get(0);//contexts-1);
		Context context_2 = (Context)group_2.getChildrenArray().get(0);//contexts-1);
		// ...and their connections, and put a trigger
		context_1.setTriggers(group_2.name.replaceAll(" ","")+"."+context_2.name.replaceAll(" ", ""));
		int connections = context_2.getSourceConnections().size();
		// remove one connection
		((Connection)(context_2.getTargetConnections().get(connections-1))).disconnect();
		return diagram;
	}

	public static void runBenchmark(int[] groups_slice, int[] contexts_slice, Specimen specimen) throws Exception {
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
				while (error > 0.05) {
					int measurments = 10;
					long[] gen_ts = new long[measurments];
					while(measurments>0){
						measurments--;
						//System.out.print(" s:"+measurments+" ");
						long value = specimen.get_value(new Object[]{obj});
						if (value <= 0) {
							measurments++;
							continue;
						}
						gen_ts[measurments] = value;
					}
					deviation = deviation(gen_ts);
					error = deviation[1]/deviation[0];
					//System.out.print((float)deviation[0]/1000000+"+-"+error/1000000+"\t");
				}
				System.out.print((float)deviation[0]+"\t");
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
	
	public static long getNuSMVUserTime(long prev_time){
		String line;
		String os = BinarySelector.osCheck();
		if (os.equals(BinarySelector.MACOS)){
			try {
				Process p = Runtime.getRuntime().exec("ps -e");
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				//p.waitFor();
				while((line=input.readLine()) != null){
					//System.out.println(line);
					if (line.toLowerCase().contains("nusmv") && !line.toLowerCase().contains("grep")) {
						//System.out.println(line);
						Matcher matcher = _pattern.matcher(line);
						if (matcher.find()) {
							long time = millisFrom(matcher,os);
						    return time > prev_time?time:prev_time;
						}
					}
				}
			} catch (IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (os.equals(BinarySelector.WINDOWS64)){
			try {
				Process p = Runtime.getRuntime().exec("tasklist.exe /v /fi \"imagename eq NuSMV.exe\"");
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				//p.waitFor();
				while((line=input.readLine()) != null){
					//System.out.println(line);
					if (line.contains("NuSMV.exe")) {
						//System.out.println(line);
						Matcher matcher = _win_pattern.matcher(line);
						if (matcher.find()) {
							//System.out.println(matcher.group(0));
							long time = millisFrom(matcher,os);
						    return time > prev_time?time:prev_time;
						}
					}
				}
			} catch (IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public static long millisFrom(Matcher matcher, String os) {
		long result = 0;
	    if (os.equals(BinarySelector.MACOS)){
	    	result += Long.parseLong(matcher.group(1)) * 60000 
			        + Long.parseLong(matcher.group(2)) * 1000 
			        + Long.parseLong(matcher.group(3)) * 10; 
	    	return result == 0 ? result+1 : result;
	    }
	    if (os.startsWith(BinarySelector.WINDOWS)){
	    	result += Long.parseLong(matcher.group(1)) * 60*60000 
			        + Long.parseLong(matcher.group(2)) * 60000 
			        + Long.parseLong(matcher.group(3)) * 1000;
	    	return result == 0 ? result+1 : result;
	    }
		return result;
	}
	
	public static long millisFrom2(Matcher matcher, String os) {
		long result = 0;
	    if (os.equals(BinarySelector.MACOS)){
	    	result += Long.parseLong(matcher.group(1)) * 1000 
			        + Long.parseLong(matcher.group(2)) * 10; 
	    	return result == 0 ? result+1 : result;
	    }
		return result;
	}

}
