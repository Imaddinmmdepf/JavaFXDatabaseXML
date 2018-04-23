/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author icemole
 */
public class TestLibreria extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
       
        root.getStylesheets().add(getClass().getResource("table-view.css").toExternalForm());
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
              public void handle(WindowEvent we) {
                  Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                  alert.setTitle("Quit Confirmation");
                  alert.setHeaderText("Close the application");
                  alert.setContentText("Do you really want to quit?/n/n"
                          + "Contents will not be saved if you do not save before exiting" );
                  ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
                  ButtonType saveEx = new ButtonType("Save and exit");
                  ButtonType noSaving = new ButtonType("Exit without saving");
                  
                  alert.getButtonTypes().setAll(buttonTypeCancel,saveEx,noSaving);
                  
                  Optional<ButtonType> result = alert.showAndWait();
                  if(result.isPresent()){
                      if(result.get() == buttonTypeCancel){
                          we.consume();
                      }else if(result.get() == saveEx){
                         AccesoaBD db = new AccesoaBD();
                         db.salvar();
                      }
                   }
              }
         }); 


        stage.setTitle( "Pangea" );
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
