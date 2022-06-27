
/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.PremierLeague;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Model;
import it.polito.tdp.PremierLeague.model.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnGiocatoreMigliore"
    private Button btnGiocatoreMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="cmbMatch"
    private ComboBox<Match> cmbMatch; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	Match m = this.cmbMatch.getValue();
    	if(m == null) {
    		this.txtResult.setText("Devi prima selezionare un match dall'apposito menù a tendina");
    		return;
    	}
    	//se sono qua posso proseguire
    	this.model.creaGrafo(m);
    	this.txtResult.setText("Grafo creato\n");
    	this.txtResult.appendText("#VERTICI: "+this.model.nVertices()+"\n");
    	this.txtResult.appendText("#ARCHI: "+this.model.nArchi()+"\n");
    }

    @FXML
    void doGiocatoreMigliore(ActionEvent event) {    	
    	this.txtResult.clear();
    	Match m = this.cmbMatch.getValue();
    	if(m == null) {
    		this.txtResult.setText("Devi prima selezionare un match dall'apposito menù a tendina");
    		return;
    	}
    	if(!this.model.isGraphCreated()) {
    		this.txtResult.setText("Devi prima creare il grafo");
    		return;
    	}
    	Player migliore = this.model.getGiocatoreMigliore();
    	this.txtResult.setText("Giocatore migliore:\n");
    	this.txtResult.appendText(migliore+", delta efficienza = "+this.model.deltaMax()+"\n");
    }
    
    @FXML
    void doSimula(ActionEvent event) {
    	this.txtResult.clear();
    	Match m = this.cmbMatch.getValue();
    	if(m == null) {
    		this.txtResult.setText("Devi prima selezionare un match dall'apposito menù a tendina");
    		return;
    	}
    	if(!this.model.isGraphCreated()) {
    		this.txtResult.setText("Devi prima creare il grafo");
    		return;
    	}
    	int N;
    	try {
    		N = Integer.parseInt(this.txtN.getText());
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("Devi inserire un valore numerico intero");
    		return;
    	}
    	this.model.simulazione(N, m);
    	for(int i = 0; i < this.model.getSquadreMatch(m).size(); i++) {
    		String s = this.model.getSquadreMatch(m).get(i);
    		this.txtResult.appendText(s+" ha fatto "+this.model.getGoal().get(i)+" goal, con "+this.model.getGoal().get(i)+" espulsi\n");
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnGiocatoreMigliore != null : "fx:id=\"btnGiocatoreMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbMatch != null : "fx:id=\"cmbMatch\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbMatch.getItems().clear();
    	List<Match> all = this.model.getMatches();
    	Collections.sort(all);
    	this.cmbMatch.getItems().addAll(all);
    }
}
