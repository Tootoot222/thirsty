package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import lib.TextFormatterFactory;
import model.ReportManager;
import model.User;
import model.WaterType;
import model.WaterCondition;
import model.WaterReport;

/**
 * FXML Controller class
 *
 * @author tybrown
 */
public class WaterSourceReportScreenController implements Initializable {

    @FXML
    private Label latErrorLabel;
    @FXML
    private Label longErrorLabel;
    @FXML
    private Label submitErrorLabel;

    @FXML
    private TextField latTextField;
    @FXML
    private TextField longTextField;

    @FXML
    private Slider latSlider;
    @FXML
    private Slider longSlider;

    @FXML
    private ComboBox<WaterType> typeComboBox;
    @FXML
    private ComboBox<WaterCondition> conditionComboBox;

    private static final WaterType WATER_TYPE_DEFAULT = WaterType.OTHER;
    private static final WaterCondition WATER_CONDITION_DEFAULT = WaterCondition.MUDDY;

    private User activeUser;


    /**
     * Greets the user with a welcome message
     * @param activeUser The user who is being greeted
     */
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    /**
     * Returns the text to put on the tab containing this screen
     * @return The String text to put on the tab
     */
    public String getTabText() {
        return ("Submit Water Report");
    }

    /**
     * Resets error fields
     */
    private void resetErrors() {
        latErrorLabel.setText("");
        longErrorLabel.setText("");
        submitErrorLabel.setText("");
    }

    /**
     * Resets all fields
     */
    private void resetFields() {
        resetErrors();
        latTextField.setText("");
        longTextField.setText("");
        typeComboBox.setValue(WATER_TYPE_DEFAULT);
        conditionComboBox.setValue(WATER_CONDITION_DEFAULT);
        latSlider.setValue(0.0);
        longSlider.setValue(0.0);
    }

    /**
     * Sets up the fields with data from elsewhere
     * @param lat The Latitude to stick into the box
     * @param lng The Longitude to stick into the box
     */
    public void populateReport(double lat, double lng) {
        resetFields();
        latTextField.setText(String.format("%.5f", lat));
        longTextField.setText(String.format("%.5f", lng));
    }

    /**
     * Handles canceling a water report submission
     * @param event Button push that triggers the code
     */
    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        resetFields();
    }


    /**
     * Handles saving a water report
     * @param event Button push that triggers the code
     */
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        resetErrors();
        double latD;
        double longD;
        String latS = latTextField.getText();
        String longS = longTextField.getText();
        
        if (!latS.isEmpty()) {
            double mult = (Double.compare(latSlider.getValue(), -1.0) == 0) ? -1.0 : 1.0;
            latD = Double.parseDouble(latS) * mult;
            final int maxLat = 90;
            if (Math.abs(latD) > maxLat) {
                latErrorLabel.setText("Invalid lattitude!");
                return;
            }
        } else {
            latErrorLabel.setText("Cannot be blank!");
            return;
        }
        if (!longS.isEmpty()) {
            double mult = (Double.compare(longSlider.getValue(), -1.0) == 0) ? -1.0 : 1.0;
            longD = Double.parseDouble(longS) * mult;
            final int maxLong = 180;
            if (Math.abs(longD) > maxLong) {
                longErrorLabel.setText("Invalid longitude!");
                return;
            }
        } else {
            longErrorLabel.setText("Cannot be blank!");
            return;
        }

        WaterReport r = ReportManager.createWaterReport(latD, longD, typeComboBox.getValue(),
                conditionComboBox.getValue(), activeUser.getUsername());
        if (r == null) {
            submitErrorLabel.setText("Error during report creation!");
        } else {
            MasterSingleton.updateReportScreen();
            resetFields();
            submitErrorLabel.setText(String.format("Report #%d created successfully!", r.getReportNum()));
        }
    }

    /**
     * Initializes controller class
     * @param url the url
     * @param rb the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        typeComboBox.setItems(WaterType.getAllObservableList());
        conditionComboBox.setItems(WaterCondition.getAllObservableList());

        latTextField.setTextFormatter(TextFormatterFactory.decimalOnlyTextFormatter());
        longTextField.setTextFormatter(TextFormatterFactory.decimalOnlyTextFormatter());

        resetFields();
    }    
    
}
