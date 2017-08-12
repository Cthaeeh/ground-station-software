package command;

import data.params.Parameter;
import gui_elements.NumberTextField;
import javafx.beans.value.ObservableValue;
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
    private CheckBox checkBox;
    private ChoiceBox<String>choiceBox;
    private NumberTextField numberTextField;


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
                    displayFlagParam(param);
                    break;
                case STATE:
                    displayStateParam(param);
                    break;
                case INTEGER:
                    displayIntegerParam(param);
                    break;
                default:
                    box.getChildren().add(new Label("unknown param type"));
            }

        }
        setGraphic(box);
    }

    private void displayFlagParam(Parameter param) {
        if(checkBox == null){
            checkBox = new CheckBox("ON or OFF");
            checkBox.selectedProperty().addListener(e-> param.setFlag(checkBox.isSelected()));
            box.getChildren().add(checkBox);
        }
    }

    private void displayStateParam(Parameter param) {
        if(choiceBox == null){
            choiceBox = new ChoiceBox<>();
            choiceBox.getItems().setAll(param.getStateMap().keySet());
            choiceBox.getSelectionModel().selectFirst();
            choiceBox.getSelectionModel()
                     .selectedItemProperty()
                     .addListener( (ObservableValue<? extends String> observable,String oldValue, String newValue) -> param.setState(newValue));
            box.getChildren().add(choiceBox);
        }
    }

    private void displayIntegerParam(Parameter param) {
        if(numberTextField == null){
            numberTextField = new NumberTextField();
            numberTextField.textProperty().addListener(e-> param.setInteger(numberTextField.getInteger()));
            box.getChildren().add(numberTextField);
        }
    }

}
