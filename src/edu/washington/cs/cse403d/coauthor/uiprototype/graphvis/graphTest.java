package edu.washington.cs.cse403d.coauthor.uiprototype.graphvis;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class graphTest// implements Graph
{
	public static void main(String[] args)
	{
		// add any other author names you want here
		ArrayList<String> authors = new ArrayList<String>();
		authors.add("Steve");
		authors.add("Mark");
		authors.add("David");
		authors.add("James");
		authors.add("Evan");
		
		// Graph<V, E> where V is the type of the vertices and E is the type of the edges
		Graph<String, Integer> g = new SparseMultigraph<String, Integer>();
		
		for (int i = 0; i < authors.size(); i++)
			g.addVertex(authors.get(i));
		
		for (int i = 1; i < authors.size(); i++)
			g.addEdge(i-1, authors.get(0), authors.get(i));
		Layout<String, Integer> layout = new CircleLayout(g);
		layout.setSize(new Dimension(300,300));
		layout.setSize(new Dimension(300,300));
		VisualizationViewer<String, Integer> vv = new VisualizationViewer<String, Integer>(layout);
		vv.setPreferredSize(new Dimension(350,350));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(gm);
		
		Transformer<String,Paint> vertexPaint = new Transformer<String,Paint>() {
			public Paint transform(String i) {
				return Color.GREEN;
		}
		};
		float dash[] = {10};
		final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER);//, 10.0f, dash, 0.0f);
		Transformer<Integer, Stroke> edgeStrokeTransformer = new Transformer<Integer, Stroke>() {
			public Stroke transform(Integer i) {
				return edgeStroke;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		JFrame frame = new JFrame("Simple Graph View 2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
}