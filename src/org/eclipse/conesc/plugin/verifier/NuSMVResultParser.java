package org.eclipse.conesc.plugin.verifier;

import java.awt.Label;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

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
			addItem(bar, spec, _counterexamples.get(spec));
		}
	}
	
	private void addItem(ExpandBar bar, String title, String content) {
		// do not add existing counterexample
		for (ExpandItem item:bar.getItems())
			if (item.getText().equals(title)) return;
		
		Composite cmp = new Composite (bar, SWT.H_SCROLL);
		cmp.setLayout(new FillLayout());
		
		final Canvas canvas = new Canvas(cmp, SWT.BORDER | SWT.H_SCROLL);
		canvas.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final PaintHelper helper = new PaintHelper(content);
		canvas.addPaintListener(helper);
		
		final ScrollBar hBar = canvas.getHorizontalBar ();
		hBar.addListener(SWT.Selection, new Listener(){
			@Override
			public void handleEvent(Event event) {
				//System.out.println(canvas.getBounds()+";"+hBar.getSelection());
				Rectangle rect = helper.getBounds ();
				int hSelection = hBar.getSelection () * (helper.getBounds().width-canvas.getBounds().width)/80;
				int destX = -hSelection - helper._origin.x;
				canvas.scroll (destX, 0, 0, 0, rect.width, rect.height, false);
				helper._origin.x = -hSelection;
			}
		});
		
		
		ExpandItem item = new ExpandItem (bar, SWT.H_SCROLL, bar.getItemCount());
		item.setText(title);
		item.setHeight(helper.calculateCanvasH());
		item.setControl(cmp);
		item.setImage(bar.getDisplay().getSystemImage(SWT.ICON_WARNING));
	}
	
	private class PaintHelper implements PaintListener {
		
		private int _canvas_h = 0;
		private String _content;
		private State _first_state;
		private final static int STR_H = 13; // ok, this is hardcode, sorry :(
		private final Point _origin = new Point(0,0);
		private int _canvas_w = 0;

		public PaintHelper(String content) {
			_content = content;
		}
		
		public Rectangle getBounds() {
			return new Rectangle(0,0,_canvas_w , _canvas_h);
		}

		@Override
		public void paintControl(PaintEvent e) {
			if (_canvas_h == 0) calculateCanvasH();
			
			Font bold = new Font(e.display, "Arrial",11, SWT.BOLD);
			Font norm = new Font(e.display, "Arrial",11, SWT.NORMAL);
			
			int new_xmargin = _origin.x+10;
			int new_ymargin = _origin.y+10;
			
			State state = _first_state;
			int state_n = 0;
			int canvas_w = _canvas_w;
			while((state=state.nextState()) != null) {
				state_n++;
				String[] lines = state.toString().split("\\n");
				if (lines.length == 0) continue;
				
				Point pt = e.gc.stringExtent(lines[0]);
				Point new_pt = null;
				for(String line:lines)
					pt = ((new_pt = e.gc.stringExtent(line)).x < pt.x) ? pt : new_pt;
				
				e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
				e.gc.setBackground(new Color(e.display,255, 255, 206));
				
				Rectangle header = new Rectangle(new_xmargin, new_ymargin, pt.x+1, pt.y+1);
				e.gc.drawRectangle(header);
				e.gc.fillRectangle(header.x+1, header.y+1, header.width-1, header.height-1);
				e.gc.setFont(bold);
				e.gc.drawText("State "+state_n,header.x+1,header.y+1);
				
				Rectangle label = new Rectangle(header.x, header.y+header.height, header.width, (header.height-1)*lines.length+1);
				e.gc.drawRectangle(label);
				e.gc.setFont(norm);
				e.gc.drawText(state.toString(), label.x+1, label.y+1);
				
				canvas_w = label.x+label.width+10;
				
				String event = state.getEvent();
				if (event.isEmpty()) continue;
				
				Point evt_pt = e.gc.stringExtent(event);
				Point line_start = new Point(label.x+label.width, label.y+label.height/2);
				Point line_stop = new Point(label.x+label.width+evt_pt.x+2*10, label.y+label.height/2);
				
				e.gc.drawLine(line_start.x, line_start.y, line_stop.x, line_stop.y);
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.drawText(event, line_start.x+10, line_start.y-evt_pt.y);
				
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
				e.gc.fillPolygon(new int[]{line_stop.x,line_stop.y,
										   line_stop.x-5,line_stop.y-5,
										   line_stop.x-5,line_stop.y+5});
				new_xmargin = line_stop.x;
			}
			_canvas_w = (_canvas_w == 0)?canvas_w:_canvas_w;
			//System.out.println(_canvas_w);
		}
		
		public int calculateCanvasH() {
			// parsing counterexample and calculating canvas_h
			State state = new State();
			_first_state = state;
			int lines = 0;
			for(String line:_content.split("\\n")) {
				if (line.contains("next_")) continue;
				if (line.startsWith("->")) {
					state = state.newState();
					lines = (state.lines()<lines)?lines:state.lines();
					continue;
				}
				if (line.contains("event = ")) {
					state.setEvent(line.split("event = ")[1]);
				}
				if (!line.contains("_state = ")) continue;
				// substring because we have 2 redundant spaces :/
				state.put(line.substring(2,line.length()).split("_state = ")[0], line.split("_state = ")[1]);
			}
			System.out.println(lines);
			state = _first_state;
			//System.out.println(state);
			while((state = state.nextState()) != null);
				//System.out.println(state + state.getEvent()+"\n");
			_canvas_h = 27+STR_H*(1+lines);
			return _canvas_h;
		}
	}
	
	private class State {
		private HashMap<String,String> _label;
		private State _next = null;
		private String _event = null;
		public State(){
			_label = new HashMap<String, String>();
		}
		public State(HashMap<String, String> label){
			_label = label;
		}
		public void put(String key, String value) {
			_label.put(key, value);
		}
		public State nextState() {
			return _next;
		}
		public String getEvent() {
			return (_next == null)? "" : _event;
		}
		public int lines(){
			return _label.size();
		}
		public void setEvent(String event) {
			_event = event;
		}
		public State newState() {
			if (_label == null) return null;
			_next = new State((HashMap<String, String>) _label.clone());
			return _next;
		}
		public String toString() {
			String string = "";
			if (_label == null) return string;
			for (String key:_label.keySet())
				string += key+"."+_label.get(key)+"\n";
			return string.substring(0, string.length()-1);
		}
	}

}
