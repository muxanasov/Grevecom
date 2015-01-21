package Benchmark;
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
	
	//public static int _event = 0;
	//public static Long _mainID;
	//public static ThreadMXBean _tmbean = null;
	
	public static void main(String[] args) {
		/*
		_tmbean = ManagementFactory.getThreadMXBean();
		_tmbean.setThreadCpuTimeEnabled(true);

		// Get the standard attribute "VmVendor"
		//String vendor = mxbean.getVmVendor();
		long[] tids = _tmbean.getAllThreadIds();
	    ThreadInfo[] tinfos = _tmbean.getThreadInfo(tids);
	    for (ThreadInfo tinfo:tinfos){
	    	//System.out.println(tinfo.getThreadName());
	    	if(tinfo.getThreadName().equals("main")) {
	    		_mainID = tinfo.getThreadId();
	    		break;
	    	}
	    }
		*/
		int[] groups_slice = new int[]{1,2,3,4,5,6,7,8,9,10};
		int[] contexts_slice = new int[]{5,8,10};
		//BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new GenerationTimeSpecimen());
		
		groups_slice = new int[]{2,5,10};
		contexts_slice = new int[]{1,2,3,4,5,6,7,8,9,10};
		//BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new GenerationTimeSpecimen());
		
		groups_slice = new int[]{5,3,4,5,6,7,8,9,10};
		contexts_slice = new int[]{10,5,10};
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new DeadlockSpecimen());
		
		groups_slice = new int[]{2,5,10};
		contexts_slice = new int[]{2,3,4,5,6,7,8,9,10};
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new DeadlockSpecimen());
	}
}
