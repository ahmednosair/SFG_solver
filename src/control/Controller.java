package control;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.DirectEdge;
import model.DirectGraph;
import model.MasonFormula;

import java.util.HashSet;
import java.util.Set;


public class Controller {
    @FXML
    private Pane mainPane;
    @FXML
    private Button add, remove, execute;
    @FXML
    private TextField sinkIndex, srcNode, destNode, weight, srcIndex, result;
    @FXML
    private TableView<Carrier> table;
    @FXML
    private TableColumn<Carrier, Integer> srcCol, destCol;
    @FXML
    private TableColumn<Carrier, String> weightCol;
    private MasonFormula calculator;
    private Stage graphStage;

    @FXML
    public void initialize() {
        srcNode.textProperty().addListener((observable, oldValue, newValue) -> srcTxtHandle());
        destNode.textProperty().addListener((observable, oldValue, newValue) -> destTxtHandle());
        weight.textProperty().addListener((observable, oldValue, newValue) -> weightTxtHandle());
        srcIndex.textProperty().addListener((observable, oldValue, newValue) -> srcIndexHandle());
        sinkIndex.textProperty().addListener((observable, oldValue, newValue) -> sinkIndexHandle());
        srcCol.setCellValueFactory(new PropertyValueFactory<>("srcNode"));
        destCol.setCellValueFactory(new PropertyValueFactory<>("destNode"));
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        calculator = new MasonFormula();

    }

    @FXML
    public void add() {
        table.getItems().add(new Carrier(Integer.parseInt(srcNode.getText()), Integer.parseInt(destNode.getText()), Double.parseDouble(weight.getText())));
        enableRemoveButton();
    }

    @FXML
    public void remove() {
        table.getItems().remove(table.getItems().size() - 1);
        enableRemoveButton();
    }

    private void emptyTxt() {
        sinkIndex.setText("");
        srcNode.setText("");
        destNode.setText("");
        weight.setText("");
        srcIndex.setText("");
        result.setText("");
    }

    @FXML
    public void reset() {
        table.getItems().clear();
        emptyTxt();
        enableAddButton();
        enableRemoveButton();
        enableExecutionButton();
        if(graphStage!=null){
            graphStage.close();
        }
    }


    @FXML
    public void srcTxtHandle() {
        enableAddButton();
        enableExecutionButton();
    }

    @FXML
    public void destTxtHandle() {
        enableAddButton();
        enableExecutionButton();

    }

    @FXML
    public void weightTxtHandle() {
        enableAddButton();
        enableExecutionButton();

    }

    @FXML
    public void srcIndexHandle() {
        enableExecutionButton();
    }

    @FXML
    public void sinkIndexHandle() {
        enableExecutionButton();
    }

    private void enableAddButton() {
        if (isValidTriple()) {
            add.setDisable(false);
        } else {
            add.setDisable(true);
        }
    }

    private void enableRemoveButton() {
        if (table.getItems().isEmpty()) {
            remove.setDisable(true);
        } else {
            remove.setDisable(false);
        }
    }

    private void enableExecutionButton() {
        if (isValidExec()) {
            execute.setDisable(false);
        } else {
            execute.setDisable(true);
        }
    }

    private boolean isValidExec() {
        return isInt(srcIndex.getText()) && isInt(sinkIndex.getText());
    }

    private boolean isValidTriple() {
        return isInt(srcNode.getText()) && isInt(destNode.getText()) && isDouble(weight.getText());
    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    public void executePr() {
        try {
            if(graphStage==null){
                mainPane.getScene().getWindow().setOnHiding( event -> graphStage.close());
                graphStage = new Stage(StageStyle.DECORATED);
                graphStage.setTitle("Signal Flow Graph");
            }
            ObservableList<Carrier> list = table.getItems();
            DirectGraph g = new DirectGraph();
            Set<Integer> vertices = new HashSet<>();
            for (Carrier carrier : list) {
                g.addEdge(carrier.srcNode, new DirectEdge(carrier.destNode, carrier.weight));
                vertices.add(carrier.srcNode);
                vertices.add(carrier.destNode);
            }
            calculator.setGraph(g);
            double fn = calculator.evaluateTransFn(Integer.parseInt(srcIndex.getText()), Integer.parseInt(sinkIndex.getText()));
            Digraph<Integer, String> graph = new DigraphEdgeList<>();

            for (Integer v : vertices) {
                graph.insertVertex(v);
            }
            StringBuilder deli = new StringBuilder(" ");
            for (Carrier carrier : list) {
                graph.insertEdge(carrier.srcNode, carrier.destNode, carrier.weight + deli.toString());
                deli.append(" ");
            }
            SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
            SmartGraphPanel<Integer, String> graphView = new SmartGraphPanel<>(graph, strategy);
            Scene scene = new Scene(graphView, 550, 550);
            graphStage.setScene(scene);
            graphStage.setX(mainPane.getScene().getWindow().getX()+mainPane.getScene().getWidth()+10);
            graphStage.setY(mainPane.getScene().getWindow().getY());
            graphStage.show();
            graphStage.resizableProperty().setValue(false);
            graphView.init();
            result.setText(Double.toString(fn));
        } catch (Throwable e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid input!");
            alert.setContentText("Please verify the input data!");
            alert.showAndWait();
        }

    }
}
