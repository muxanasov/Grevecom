import java.util.ArrayList;
import java.util.List;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;


public class Benchmark {
	
	public static int _event = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int max_groups = 20;
		int max_contexts_per_group = 20;
		_event = 0;
		System.out.print("g\\c\t");
		for(int contexts_per_group=2;contexts_per_group<=max_contexts_per_group;contexts_per_group++)
			System.out.print(contexts_per_group+"\t");
		System.out.println("");
		ContextDiagram diagram = new ContextDiagram();
		for(int i = 0;i<max_groups;i++){
			List<Context> contexts = new ArrayList<Context>();
			for(int j=0;j<max_contexts_per_group;j++) {
				Context con = new Context();
				con.setName("context"+i+""+j);
				contexts.add(con);
			}
			diagram.addChild(createGroup(contexts, i));
		}
		ConesCModelVerifier verifier = new ConesCModelVerifier(diagram);
		NuSMVResultParser parser = new NuSMVResultParser();
		for(int i=0;i<10;i++)
			verifier.generateModel("");
		
		for(int groups=1;groups<=max_groups;groups++){
			System.out.print(groups+"\t");
			for(int contexts_per_group=2;contexts_per_group<=max_contexts_per_group;contexts_per_group++){
				diagram = new ContextDiagram();
				for(int i = 0;i<groups;i++){
					List<Context> contexts = new ArrayList<Context>();
					for(int j=0;j<contexts_per_group;j++) {
						Context con = new Context();
						con.setName("context"+i+""+j);
						contexts.add(con);
					}
					diagram.addChild(createGroup(contexts, i));
				}
				verifier = new ConesCModelVerifier(diagram);
				parser = new NuSMVResultParser();
				double error = 1;
				double new_error = 0;
				double[] deviation = new double[2];
				while (error > 0.333 || error == 0) {
					int measurments = 10;
					long startTime = 0;
					long gen_t = 0;
					long ver_t = 1;
					long[] gen_ts = new long[measurments+1];
					long[] ver_ts = new long[measurments+1];
					String result = "";
					while(measurments>=0){
						startTime = System.currentTimeMillis();
						verifier.generateModel("");
						gen_t = System.currentTimeMillis();
						gen_t -= startTime;
						//startTime = System.currentTimeMillis();
						//result = verifier.verify();
						//ver_t = System.currentTimeMillis() - startTime - gen_t;
						if (ver_t <= 0 || gen_t <=0) continue;
						//if (!parser.parse(result, verifier.getSpecifications(), "").contains("No errors.")) {
						//	System.err.println("Model is not verifyable!\n"+result);
						//	return;
						//}
						gen_ts[measurments] = gen_t;
						//ver_ts[measurments] = ver_t;
						measurments--;
					}
					deviation = deviation(gen_ts);
					error = deviation[1]/deviation[0];
					//System.out.print(deviation[0]+"+-"+deviation[1]+" "+error+"\t");
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
