/*******************************************************************************
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mikhail Afanasov - initial API and implementation
?*******************************************************************************/

package org.eclipse.conesc.plugin.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class BinarySelector {
	
	public static final String MACOS = "Mac";
	public static final String LINUX = "Linux";
	public static final String WINDOWS = "Windows";
	public static final String OTHER = "Other";
	
	public static String osCheck() {
		String os = System.getProperty("os.name");
		if (os.startsWith(MACOS)) return MACOS;
		if (os.startsWith(LINUX)) return LINUX;
		if (os.startsWith(WINDOWS)) return WINDOWS;
		return OTHER;
	}
	
	public static String getNuSMVBin() throws IOException {
		Bundle bundle = Platform.getBundle("org.eclipse.conesc.plugin");
		Path path = null;
		String result = "";
		if (bundle == null)
			result = BinarySelector.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (osCheck().equals(MACOS)) path = new Path("NuSMV-2.5.4-linux-mac/bin_mac/nusmv");
		if (osCheck().equals(LINUX)) path = new Path("NuSMV-2.5.4-linux-mac/bin_linux/nusmv");
		if (path == null) throw new IOException("I do not have NuSMV binary for your "+osCheck()+" system.");
		if (bundle == null)
			return result+"../"+path.toString();
		URL relative_path = FileLocator.find(bundle,path,Collections.EMPTY_MAP);
		URL absolute_path = FileLocator.resolve(relative_path);
		return absolute_path.getPath();
	}

}
