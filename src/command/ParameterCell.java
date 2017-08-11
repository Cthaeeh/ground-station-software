package command;

import data.params.Parameter;
import gui_elements.NumberTextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

/**
 * Created by Kai on 11.08.2017.
 */
public class ParameterCell extends ListCell<Parameter> {

    private Label name = new Label();
    private VBox box = new VBox();

    public ParameterCell(){
        box.getChildren().addAll(name);
        name.setStyle("-fx-font-size:18;");
    }

    @Override
    protected void updateItem(Parameter param, boolean empty){
        super.updateItem(param,empty);
        if(empty || param == null){
            //TODO set all UI elements null
        }else{
            name.setText(param.getName());
            switch (param.getType()){
                case FLAG:
                    box.getChildren().add(new CheckBox("ON or OFF"));
                    break;
                case STATE:
                    box.getChildren().add(new ChoiceBox<String>());
                    break;
                case INTEGER:
                    box.getChildren().add(new NumberTextField());
                    break;
                default:
                    box.getChildren().add(new Label("unknown param type"));
            }

        }
        setGraphic(box);
    }

}
