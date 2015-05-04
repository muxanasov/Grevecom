package Benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.eclipse.conesc.plugin.utils.BinarySelector;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.ProcessInfo;


public class Times {
	
	/** Get CPU time in nanoseconds. */
	public static long getCpuTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadCpuTime( ) : 0L;
	}
	 
	/** Get user time in nanoseconds. */
	public static long getUserTime( ) {
		//if (BinarySelector.osCheck().startsWith(BinarySelector.WINDOWS))
		//	return System.nanoTime();
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        bean.getCurrentThreadUserTime( ) : 0L;
	}

	/** Get system time in nanoseconds. */
	public static long getSystemTime( ) {
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	        (bean.getCurrentThreadCpuTime( ) - bean.getCurrentThreadUserTime( )) : 0L;
	}

}
