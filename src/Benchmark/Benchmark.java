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

import Benchmark.AdaptiveProtocolStackSpecimens.AdaptiveDeadlockSpecimen;
import Benchmark.AdaptiveProtocolStackSpecimens.AdaptiveIllegalSpecimen;
import Benchmark.AdaptiveProtocolStackSpecimens.AdaptiveStackSpecimen;
import Benchmark.AdaptiveProtocolStackSpecimens.AdaptiveUserSpecimen;
import Benchmark.SmartHomeSpecimens.SmartDeadlockSpecimen;
import Benchmark.SmartHomeSpecimens.SmartHomeSpecimen;
import Benchmark.SmartHomeSpecimens.SmartIllegalSpecimen;
import Benchmark.SmartHomeSpecimens.SmartUserSpecimen;
import Benchmark.SynthesizedSpecimens.DeadlockSpecimen;
import Benchmark.SynthesizedSpecimens.GenerationTimeSpecimen;
import Benchmark.SynthesizedSpecimens.IllegalTransitionsSpecimen;
import Benchmark.SynthesizedSpecimens.VerificationSpecimen;
import Benchmark.WildlifeTrackingSpecimens.WildlifeDeadlockSpecimen;
import Benchmark.WildlifeTrackingSpecimens.WildlifeIllegalSpecimen;
import Benchmark.WildlifeTrackingSpecimens.WildlifeTrackingSpecimen;
import Benchmark.WildlifeTrackingSpecimens.WildlifeUserSpecimen;


public class Benchmark {
	
	public static void main(String[] args) throws Exception {
		
		int[] groups_slice = new int[]{1,2,3,4,5,6,7,8,9,10};
		int[] contexts_slice = new int[]{5,8,10};
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new GenerationTimeSpecimen());
		
		groups_slice = new int[]{2,5,10};
		contexts_slice = new int[]{1,2,3,4,5,6,7,8,9,10};
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new GenerationTimeSpecimen());
		
		groups_slice = new int[]{2,3,4};
		contexts_slice = new int[]{5};
		/*
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new VerificationSpecimen());
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new DeadlockSpecimen());
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new IllegalTransitionsSpecimen());
		
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new VerificationSpecimen().generateCx(false));
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new DeadlockSpecimen().generateCx(false));
		BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new IllegalTransitionsSpecimen().generateCx(false));
		*/
		/*
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new WildlifeTrackingSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new WildlifeDeadlockSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new WildlifeIllegalSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new WildlifeUserSpecimen().withSpecs("AG !(Running&Diseased)"));
		
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new SmartHomeSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new SmartDeadlockSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new SmartIllegalSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new SmartUserSpecimen().withSpecs("AG !(Fire&Low)"));
		
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new AdaptiveStackSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new AdaptiveDeadlockSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new AdaptiveIllegalSpecimen());
		BenchmarkUtils.runBenchmark(new int[]{0}, new int[]{0}, new AdaptiveUserSpecimen().withSpecs("AG Lifetime"));
		*/
		//BenchmarkUtils.runBenchmark(new int[]{1,2,3,4,5,6,7,8,9,10}, new int[]{1}, new UserSpecsSpecimen());
		//BenchmarkUtils.runBenchmark(new int[]{1,2,3,4,5,6,7,8,9,10}, new int[]{1}, new UserUnSpecsSpecimen());
		
		groups_slice = new int[]{6};
		contexts_slice = new int[]{2,3,4,5,6,7,8,9,10};
		//BenchmarkUtils.runBenchmark(groups_slice, contexts_slice, new DeadlockSpecimen());
	}
}
