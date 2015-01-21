package Benchmark;
import java.lang.reflect.Field;
import com.sun.jna.Pointer;


public class PIDHelper {
	
	public static long getPID(Process process) {
		if(process.getClass().getName().equals("java.lang.UNIXProcess")) {
			/* get the PID on unix/linux systems */
			try {
				Field f = process.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				return f.getInt(process);
			} catch (Throwable e) {
			}
		}
		if (process.getClass().getName().equals("java.lang.Win32Process") ||
				process.getClass().getName().equals("java.lang.ProcessImpl")) {
			/* determine the pid on windows plattforms */
			try {
				Field f = process.getClass().getDeclaredField("handle");
				f.setAccessible(true);				
				long handl = f.getLong(process);
			    
				Kernel32 kernel = Kernel32.INSTANCE;
				W32API.HANDLE handle = new W32API.HANDLE();
				handle.setPointer(Pointer.createConstant(handl));
				return kernel.GetProcessId(handle);
			  } catch (Throwable e) {				
			  }
		}
		return 0L;
	}

}
