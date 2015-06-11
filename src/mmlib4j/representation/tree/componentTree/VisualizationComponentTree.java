package mmlib4j.representation.tree.componentTree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mmlib4j.gui.WindowImages;
import mmlib4j.images.GrayScaleImage;
import mmlib4j.representation.tree.InfoPrunedTree;
import mmlib4j.utils.AdjacencyRelation;
import mmlib4j.utils.ImageBuilder;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;


/**
 * MMorph4J - Mathematical Morphology Library for Java 
 * @author Wonder Alexandre Luz Alves
 *
 */
public class VisualizationComponentTree extends JPanel {

	DelegateTree<NodeCT, Integer> graph;
	VisualizationViewer<NodeCT, Integer> vv;
	TreeLayout<NodeCT, Integer> treeLayout;
	ComponentTree compTree;
	BufferedImage img;
	boolean isMaxtree;
	private int id = 1;
	
	public VisualizationComponentTree(ComponentTree tree, boolean map1[], boolean map2[]) {
		super.setLayout(new BorderLayout());
		this.compTree = tree;
		this.isMaxtree = tree.isMaxtree();
		
		this.img = ImageBuilder.convertToImage(tree.getInputImage());
		
		graph = new DelegateTree<NodeCT, Integer>();

		graph.setRoot(tree.getRoot());
		createTree(tree.getRoot());

		treeLayout = new TreeLayout<NodeCT, Integer>(graph);
		vv = new VisualizationViewer<NodeCT, Integer>(treeLayout);
		vv.setBackground(Color.white);
		// vv.getRenderContext().setEdgeDrawPaintTransformer(edgeDrawPaintTransformer);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		vv.getRenderContext().setVertexLabelTransformer(new LabelNodes());
		vv.getRenderContext().setVertexFillPaintTransformer(new NodeShape(map1, map2));
		// add a listener for ToolTips
		vv.setVertexToolTipTransformer(new LabelNodes());
		vv.getRenderContext().setArrowFillPaintTransformer(
				new ConstantTransformer(Color.lightGray));

		// Container content = getContentPane();

		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(graphMouse);
		vv.addGraphMouseListener(new GraphMouseListener<NodeCT>() {
			public void graphReleased(NodeCT v, MouseEvent me) {
			}

			public void graphPressed(NodeCT v, MouseEvent me) {
			}

			public void graphClicked(NodeCT v, MouseEvent me) {

			}
		});

		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
		scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
		scaleGrid.add(plus);
		scaleGrid.add(minus);

		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);

		super.add(panel, BorderLayout.CENTER);
		super.add(scaleGrid, BorderLayout.SOUTH);

	}
	
	public VisualizationComponentTree(final InfoPrunedTree prunedTree, boolean map1[], boolean map2[]) {
		this.compTree = (ComponentTree) prunedTree.getTree();
		super.setLayout(new BorderLayout());
		this.isMaxtree = compTree.isMaxtree();

		graph = new DelegateTree<NodeCT, Integer>();

		graph.setRoot(compTree.getRoot());
		createPrunedTree(prunedTree.getRoot());

		treeLayout = new TreeLayout<NodeCT, Integer>(graph);
		vv = new VisualizationViewer<NodeCT, Integer>(treeLayout);
		vv.setBackground(Color.white);
		// vv.getRenderContext().setEdgeDrawPaintTransformer(edgeDrawPaintTransformer);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		vv.getRenderContext().setVertexLabelTransformer(new LabelNodes(prunedTree));
		vv.getRenderContext().setVertexFillPaintTransformer(new NodeShape(map1, map2));
		// add a listener for ToolTips
		vv.setVertexToolTipTransformer(new LabelNodes());
		vv.getRenderContext().setArrowFillPaintTransformer(
				new ConstantTransformer(Color.lightGray));

		// Container content = getContentPane();

		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(graphMouse);
		vv.addGraphMouseListener(new GraphMouseListener<NodeCT>() {
			public void graphReleased(NodeCT v, MouseEvent me) {}
			public void graphPressed(NodeCT v, MouseEvent me) {	}
			public void graphClicked(NodeCT v, MouseEvent me) {
				if(prunedTree.getNodeOfPrunedTree(v).isLeaf())
					WindowImages.show(v.createImageSC(255), "Node:" + v.getLevel() + "; Attribute value: " + v.getAttributeValue(prunedTree.getAttributeType()));
			}
		});

		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
		scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
		scaleGrid.add(plus);
		scaleGrid.add(minus);

		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);

		super.add(panel, BorderLayout.CENTER);
		super.add(scaleGrid, BorderLayout.SOUTH);

	}

	private void createTree(NodeCT node) {
		if (node != compTree.getRoot()) {
			graph.addChild(id, node.getParent(), node);
			id++;
		}
		for (NodeCT son : node.getChildren()) {
			createTree(son);
		}
	}
	
	private void createPrunedTree(InfoPrunedTree.NodePrunedTree node) {
		if (node.getInfo() != compTree.getRoot()) {
			
			
			Collection<NodeCT> vertices = graph.getVertices();
			if(vertices.contains((NodeCT) node.getParent().getInfo()) == false) {
				throw new IllegalArgumentException("Tree must already contain parent "+((NodeCT) node.getParent().getInfo()).getId());
			}
			if(vertices.contains((NodeCT) node.getInfo())) {
				throw new IllegalArgumentException("Tree must not already contain child "+((NodeCT) node.getInfo()).getId());
			}
		
			graph.addChild(id, (NodeCT) node.getParent().getInfo(), (NodeCT) node.getInfo());
			id++;
		}
		for (InfoPrunedTree.NodePrunedTree son : node.getChildren()) {
			createPrunedTree(son);
		}
	}

	public static JFrame getInstance(ComponentTree tree) {
		return getInstance(tree, null, null);
	}
	
	public static JFrame getInstance(ComponentTree tree, boolean map1[], boolean map2[]) {
		JFrame frame;
		if (tree.isMaxtree())
			frame = new JFrame("Component tree  - maxtree");
		else
			frame = new JFrame("Component tree  - mintree");
		Container content = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		content.add(new VisualizationComponentTree(tree, map1, map2));
		frame.pack();
		return frame;
	}

	public static JFrame getInstance(InfoPrunedTree prunedTree, boolean map1[], boolean map2[]) {
		JFrame frame;
		ComponentTree tree = (ComponentTree) prunedTree.getTree();
		if (tree.isMaxtree())
			frame = new JFrame("Component tree  - maxtree");
		else
			frame = new JFrame("Component tree  - mintree");
		Container content = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		content.add(new VisualizationComponentTree(prunedTree,map1, map2));
		frame.pack();
		return frame;
	}
	

	public static void main(String args[]) {
		GrayScaleImage imgInput = ImageBuilder.openGrayImage();
		ComponentTree tree = new ComponentTree(imgInput,
				AdjacencyRelation.getCircular(1.5), true);
		tree.extendedTree();
		getInstance(tree).setVisible(true);
		;
	}

}

class LabelNodes implements Transformer<NodeCT, String> {
	InfoPrunedTree prunedTree = null;
	public LabelNodes(){}
	public LabelNodes(InfoPrunedTree prunedTree){
		this.prunedTree = prunedTree;
	}
	
	public String transform(NodeCT v) {
		//if(true)
		//	return String.valueOf(v.getLevel());
		if(prunedTree == null)
			return String.valueOf(v.getArea());		
		else
			return String.valueOf(v.getAttributeValue(prunedTree.getAttributeType()));
			//return String.valueOf(v.getLevel() + " : " + v.getAttributeValue(prunedTree.getAttributeType()));
			
	}
}

class NodeShape implements Transformer<NodeCT, Paint> {
	
	boolean selected[];
	boolean selected2[];
	
	NodeShape(){ }
	
	NodeShape(boolean map[]){
		selected = map;
	}
	
	NodeShape(boolean map1[], boolean map2[]){
		selected = map1;
		selected2 = map2;
	}
	
	Transformer<NodeCT, Paint> t1 = new ConstantTransformer(Color.RED);
	Transformer<NodeCT, Paint> t1Leaf = new ConstantTransformer(Color.decode("0X990000"));
	Transformer<NodeCT, Paint> t1Selected = new ConstantTransformer(Color.GREEN);

	Transformer<NodeCT, Paint> t2 = new ConstantTransformer(Color.BLUE);
	Transformer<NodeCT, Paint> t2Leaf = new ConstantTransformer(Color.decode("0X000099"));
	Transformer<NodeCT, Paint> t2Selected = new ConstantTransformer(Color.GREEN);

	public Paint transform(NodeCT node) {
		if (node.isNodeMaxtree()) {
			if (selected != null && selected[node.hashCode()]) {
				return t1Selected.transform(node);

			} 
			else if (selected2 != null && selected2[node.hashCode()]) {
				return t1Leaf.transform(node);
			} 
			else {
				if (!node.isClone())
					return t1Leaf.transform(node);
				else
					return t1.transform(node);
			}
		} else {
			if (selected != null && selected[node.hashCode()]) {
				return t2Selected.transform(node);
			} 
			else if (selected2 != null && selected2[node.hashCode()]) {
				return t2Leaf.transform(node);
			} 
			else {
				if (!node.isClone())
					return t2Leaf.transform(node);
				else
					return t2.transform(node);
			}
		}
	}

}
