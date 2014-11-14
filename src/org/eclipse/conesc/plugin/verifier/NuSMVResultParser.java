package org.eclipse.conesc.plugin.verifier;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

public class NuSMVResultParser {
	
	private ConesCModelVerifier _verifier;
	private String _constraints;
	private HashMap<String, String> _counterexamples = null;
	
	public NuSMVResultParser(ConesCModelVerifier verifier, String constraints){
		_verifier = verifier;
		_constraints = constraints;
	}
	
	public String parse() {
		_counterexamples = new HashMap<String, String>();
		String output = "";
		String current_spec = "";
		String counterexample = "";
		for(String line:_verifier.verify(_constraints).split("\\n")){
			if(line.startsWith("***")||line.isEmpty()) continue;
			//System.out.println(line);
			if (!line.startsWith("--")) {
				counterexample += line+"\n";
				continue;
			}
			if (!current_spec.isEmpty()&&!counterexample.isEmpty())
				_counterexamples.put(current_spec,counterexample);
			if (!line.contains("is true") && !line.contains("is false")) continue;
			current_spec = line.replaceAll("-- specification ", "").replaceAll("_state = ", ".");
			for (String spec:_verifier.getSpecifications().keySet()){
				if (!line.contains(spec)) continue;
				if (line.contains("is false")) {
					current_spec = "Violation of " + _verifier.getSpecifications().get(spec);
					break;
				}
				if (line.contains("is true")) {
					current_spec = "";
					counterexample = "";
					break;
				}
			}
			if (current_spec.isEmpty()) continue;
			output += current_spec+"\n";
		}
		if (!current_spec.isEmpty()&&!counterexample.isEmpty())
			_counterexamples.put(current_spec,counterexample);
		return output;
	}
	
	public void displayResultsOn(ExpandBar bar){
		for(String spec:_counterexamples.keySet()) {
			System.out.println(spec + " : " + _counterexamples.get(spec));
			Composite cmp = new Composite (bar, SWT.NONE);
			cmp.setLayout(new FillLayout());
			addItem(bar, cmp, spec, drawCounterexample(_counterexamples.get(spec), cmp));
		}
	}
	
	private void addItem(ExpandBar bar, Composite cmp, String specs, int height) {
		ExpandItem item = new ExpandItem (bar, SWT.NONE, bar.getItemCount());
		item.setText(specs);
		item.setHeight(height);
		item.setControl(cmp);
		Image image = bar.getDisplay().getSystemImage(SWT.ICON_WARNING);
		item.setImage(image);
	}
	
	private int drawCounterexample(String counterexample, Composite cmp) {
		Canvas canvas = new Canvas(cmp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL);
		canvas.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		canvas.addPaintListener(new PaintListener(){
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_DARK_RED));
				e.gc.drawRectangle(10, 20, 100, 200);
			}
		});
		return 300;
	}

}
