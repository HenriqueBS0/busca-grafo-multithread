import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GrafoView<T> extends JFrame {

    private static final long serialVersionUID = 1L;
    private final mxGraph mxGraph;
    private final Object parent;
    private final Map<T, Object> vertexMap;
    private final mxGraphComponent graphComponent;

    public GrafoView(Grafo<T> grafo) {
        super("Visualização do Grafo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        mxGraph = new mxGraph();
        parent = mxGraph.getDefaultParent();
        vertexMap = new HashMap<>();

        initializeGraph(grafo);
        applyGraphLayout(); // Layout circular

        graphComponent = new mxGraphComponent(mxGraph);
        setupUIComponents();
    }

    private void initializeGraph(Grafo<T> grafo) {
        mxGraph.getModel().beginUpdate();
        try {
            addVertices(grafo);
            addEdges(grafo);
        } finally {
            mxGraph.getModel().endUpdate();
        }
    }

    private void addVertices(Grafo<T> grafo) {
        for (int i = 0; i < grafo.size(); i++) {
            T valor = grafo.getValorVertice(i);
            Object vertex = mxGraph.insertVertex(parent, null, valor, 100, 100, 50, 50);
            vertexMap.put(valor, vertex);
        }
    }

    private void addEdges(Grafo<T> grafo) {
        for (int i = 0; i < grafo.size(); i++) {
            ArrayList<Integer> adjacentes = grafo.getVerticesAdjacentes(i);
            T origem = grafo.getValorVertice(i);
            for (Integer adjacente : adjacentes) {
                T destino = grafo.getValorVertice(adjacente);
                mxGraph.insertEdge(parent, null, "", vertexMap.get(origem), vertexMap.get(destino));
            }
        }
    }

    private void applyGraphLayout() {
        // Aplicando layout circular
        mxCircleLayout layout = new mxCircleLayout(mxGraph);
        layout.execute(mxGraph.getDefaultParent());
    }

    private void setupUIComponents() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(graphComponent, BorderLayout.CENTER);
        getContentPane().add(createToolBar(), BorderLayout.NORTH);
    }

    public void highlightPath(Stack<T> path) {
        if (path.isEmpty()) return;

        mxGraph.getModel().beginUpdate();
        try {
            T previous = path.pop();
            while (!path.isEmpty()) {
                T current = path.pop();
                if (vertexMap.containsKey(current) && vertexMap.containsKey(previous)) {
                    Object edge = mxGraph.insertEdge(parent, null, "", vertexMap.get(current), vertexMap.get(previous));
                    mxGraph.setCellStyle(createHighlightedEdgeStyle(), new Object[]{edge});
                }
                previous = current;
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }
    }

    private String createHighlightedEdgeStyle() {
        return mxConstants.STYLE_STROKECOLOR + "=#FF0000;" +
               mxConstants.STYLE_STROKEWIDTH + "=3";
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();

        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(e -> graphComponent.zoomIn());

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(e -> graphComponent.zoomOut());

        JButton resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.addActionListener(e -> graphComponent.zoomActual());

        toolBar.add(zoomInButton);
        toolBar.add(zoomOutButton);
        toolBar.add(resetZoomButton);

        return toolBar;
    }
}
