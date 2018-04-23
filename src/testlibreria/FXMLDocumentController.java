/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.*;
import java.io.File;
import java.io.IOException;
import javafx.scene.image.Image;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import modelo.Alumno;
import modelo.Curso;
import modelo.Dias;
import modelo.Matricula;

/**
 *
 * @author icemole
 */
public class FXMLDocumentController implements Initializable {
    
    // Variables that will hold what the table is currently showing
    private final int STUDENTS = 0;
    private final int COURSES  = 1;
    
    // Variable to check the table mode and avoid useless table updates
    private byte tableMode = -1;
    
    // Variable that will hold the database access
    private AccesoaBD abd;
    
    // Relevant elements from the FXML
    @FXML private TableView<Object> tableView;  // With Object as parametric class because
                                                // an Object can display Curso and Alumno
    @FXML private BorderPane borderPane;

    @FXML private ToggleGroup displayable;
    @FXML private ToggleGroup theme;
    @FXML private ToggleGroup font;
    
    @FXML private Button addButtonMain;
    @FXML private Button modifyButtonMain;
    @FXML private Button deleteButtonMain;
    @FXML private Label control;
    @FXML private Label tableTag;
    @FXML private Button showCourses;

    // Menu: Display
    // Sets the elements to the TableView to Students or courses
    @FXML private RadioMenuItem tableStudents;
    @FXML private RadioMenuItem tableCourses;
    
    // Menu: Settings -> Theme
    // Sets a lighter or darker theme
    @FXML private RadioMenuItem lightTheme;
    @FXML private RadioMenuItem darkTheme;
    
    // Menu: Settings -> Font size
    // Sets a smaller or bigger font size
    @FXML private RadioMenuItem smallFontSize;
    @FXML private RadioMenuItem normalFontSize;
    @FXML private RadioMenuItem bigFontSize;
    
    // Other actions in the menu
    @FXML private MenuItem about;
    @FXML private MenuItem close;
    
    private ObservableList<Alumno>    obsListAlumno;
    private ObservableList<Curso>     obsListCurso;
    private ObservableList<Matricula> obsListMatricula;
    boolean aCursos = false;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        abd = new AccesoaBD();
        
        obsListAlumno    = FXCollections.observableList( abd.getAlumnos() );
        obsListCurso     = FXCollections.observableList( abd.getCursos() );
        obsListMatricula = FXCollections.observableList( abd.getMatriculas() );
        
        tableView.setColumnResizePolicy( CONSTRAINED_RESIZE_POLICY );
        setTableToStudents();
        
        deleteButtonMain.disableProperty().bind(Bindings.equal(-1, tableView.getSelectionModel().selectedIndexProperty()));
        modifyButtonMain.disableProperty().bind(Bindings.equal(-1, tableView.getSelectionModel().selectedIndexProperty()));
        tableStudents.setSelected( true );
        lightTheme.setSelected( true );
        normalFontSize.setSelected( true );
    }
    
    /**
     * We are sure that the objects to manipulate (cellData)
     * are from the class Alumno, therefore we can safely cast it.
     * 
     * The TableColumn instances were <Alumno, ?>, changed to <Object, ?>
     * in order to change the table 
     */
    @FXML
    private void setTableToStudents()
    {
        if( tableMode == STUDENTS )
        {
            tableStudents.setSelected( true );
            return;
        }
        this.showCourses.setText("Courses");
        this.aCursos = false;
        // Change the table display to students
        tableMode = STUDENTS;
        tableTag.setText( "Students" );
        control.setText( "Select a student..." );
                
        TableColumn<Object, String>  name            = new TableColumn<>("Name");
        TableColumn<Object, String>  dni             = new TableColumn<>("DNI");
        TableColumn<Object, Integer> age             = new TableColumn<>("Age");
        TableColumn<Object, String>  address         = new TableColumn<>("Address");
        TableColumn<Object, String>  startDate       = new TableColumn<>("Enrollment date");
        // The next TableColumn objects do not need parameters
        TableColumn<Object, Button> enrollments = new TableColumn<>( "Enrollments" );
        TableColumn<Object, Button> image       = new TableColumn<>( "Image" );
        
        name.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Alumno) cellData.getValue() ).getNombre() ) ); 
        dni.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Alumno) cellData.getValue() ).getDni()));
        // asObject porque trabajamos con enteros.
        age.setCellValueFactory( cellData -> new SimpleIntegerProperty( ( (Alumno) cellData.getValue() ).getEdad() ).asObject() );
        address.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Alumno) cellData.getValue() ).getDireccion()) ); 
        startDate.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Alumno) cellData.getValue() ).getFechadealta().toString()) ); 
        
        /*This was added from my mind*/
        enrollments.setCellValueFactory( cellData -> new SimpleObjectProperty<Button>( new Button( "Show" ) ) );
        Callback<TableColumn<Object, Button>, TableCell<Object, Button>> cellFactoryEnrollment
                = //
                new Callback<TableColumn<Object, Button>, TableCell<Object, Button>>() {
            @Override
            public TableCell call(final TableColumn<Object, Button> param) {
                final TableCell<Object, Button> cell = new TableCell<Object, Button>() {

                    final Button btn = new Button( "Modify" );

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Alumno person = (Alumno) getTableView().getItems().get(getIndex());
                                /**
                                 * SHOW A DIALOG WITH THE ENROLLMENTS OF THE STUDENT
                                 */
                                showEnrollmentsOnNewStage( person );
                            });
                            btn.setPrefWidth(80.0);
                            btn.setFocusTraversable(false);
                           
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        enrollments.setCellFactory(cellFactoryEnrollment);
        
        image.setCellValueFactory( cellData -> new SimpleObjectProperty<Button>( new Button( "Show" ) ) );
        Callback<TableColumn<Object, Button>, TableCell<Object, Button>> cellFactoryImage
                = //
                new Callback<TableColumn<Object, Button>, TableCell<Object, Button>>() {
            @Override
            public TableCell call(final TableColumn<Object, Button> param) {
                final TableCell<Object, Button> cell = new TableCell<Object, Button>() {

                    final Button btn = new Button( "Show" );

                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Alumno person = (Alumno) getTableView().getItems().get(getIndex());
                                
                                // Show a dialog with the student photo
                                showPhotoOnNewStage( person.getFoto() );
                            });
                            btn.setFocusTraversable(false);
                            btn.setPrefWidth(80.0);
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        
        
        image.setCellFactory(cellFactoryImage);
        
        ObservableList<TableColumn<Object, ?>> auxTables = 
            (ObservableList)FXCollections.observableArrayList( name, dni, age, address, startDate, image, enrollments );
        
        tableView.getColumns().setAll( auxTables );
        tableView.setItems( (ObservableList) obsListAlumno  );
    }
    
    /**
     * We are sure that the objects to manipulate (cellData)
     * are from the class Curso, therefore we can safely cast it.
     */
    @FXML
    private void setTableToCourses()
    {
        if( tableMode == COURSES )
        {
            tableCourses.setSelected( true );
            return;
        }
        
          this.showCourses.setText("Students");

        this.aCursos = true;
        // Change the table display to courses
        tableMode = COURSES;
        tableTag.setText( "Courses" );
        control.setText( "Select a course..." );

        TableColumn<Object, String> name              = new TableColumn<>("Course");
        TableColumn<Object, String> assignedProfessor = new TableColumn<>("Professor");
        TableColumn<Object, Integer> maxStudents      = new TableColumn<>("Max students");
        TableColumn<Object, String> startDate         = new TableColumn<>("Start date");
        TableColumn<Object, String> endDate           = new TableColumn<>("End date");
        TableColumn<Object, String> time              = new TableColumn<>("Starting time");
        TableColumn<Object, String> days              = new TableColumn<>("Days");
        TableColumn<Object, String> clase             = new TableColumn<>("Classroom");
        TableColumn<Object, Button> enrollments = new TableColumn<>( "Enrollments" );
        
        name.setCellValueFactory( cellData -> new SimpleStringProperty( ((Curso)cellData.getValue() ).getTitulodelcurso() ));
        assignedProfessor.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Curso) cellData.getValue() ).getProfesorAsignado() ));
        maxStudents.setCellValueFactory( cellData -> new SimpleIntegerProperty( ( (Curso) cellData.getValue() ).getNumeroMaximodeAlumnos()).asObject());
        startDate.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Curso) cellData.getValue() ).getFechainicio().toString() ));
        endDate.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Curso) cellData.getValue() ).getFechafin().toString()));
        time.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Curso) cellData.getValue() ).getHora().toString()));
        days.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Curso) cellData.getValue() ).getDiasimparte().toString().substring(1,( (Curso) cellData.getValue() ).getDiasimparte().toString().length()-1)));
        clase.setCellValueFactory( cellData -> new SimpleStringProperty( ( (Curso) cellData.getValue() ).getAula()));
        enrollments.setCellValueFactory( cellData -> new SimpleObjectProperty<Button>( new Button( "Show" ) ) );
        // Show a button with label "show enrollments"
        // on the Curso TableView
        
        enrollments.setCellValueFactory( cellData -> new SimpleObjectProperty<Button>( new Button( "Show" ) ) );
        Callback<TableColumn<Object, Button>, TableCell<Object, Button>> cellFactoryEnrollment
                = //
                new Callback<TableColumn<Object, Button>, TableCell<Object, Button>>() {
            @Override
            public TableCell call(final TableColumn<Object, Button> param) {
                final TableCell<Object, Button> cell = new TableCell<Object, Button>() {

                    final Button btn = new Button( "Modify" );
                     
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                                Curso curso = (Curso) getTableView().getItems().get(getIndex());
                                
                                /*
                                 * SHOW A DIALOG WITH THE ENROLLMENTS OF THE STUDENT
                                 */
                                showStudentsOnNewStage( curso );
                            });
                            btn.setFocusTraversable(false);
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        enrollments.setCellFactory(cellFactoryEnrollment);
        ObservableList<TableColumn<Object, ?>> auxTables =
            (ObservableList)FXCollections.observableArrayList( name, assignedProfessor, maxStudents,
                                               startDate, endDate, time, days, clase,enrollments );
        
        tableView.getColumns().setAll( auxTables );
        tableView.setItems( (ObservableList) obsListCurso );
    }
    
    private Pane parentLoader(Pane root, FXMLLoader myLoader) {
         try {
            root = (Pane) myLoader.load();
        } catch( IOException e ) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error occurred");
            alert.setHeaderText("Oops! That was a mistake");
            alert.setContentText("An error has occurred while loading a new window."
                    + "\n\n" + "Please contact the support staff."
                    + "\n\n" + "The application will now exit.");
            alert.showAndWait();
            System.exit(-1);
        }
         

        root.getStylesheets().add(getClass().getResource("table-view.css").toExternalForm());
        
         return root;
    }
    
    private void goToScene(ActionEvent event, int index)
    {
        Pane root = null;
        // Get the controller of the UI
        if( this.tableStudents.isSelected() ){
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLDocumentAddStudent.fxml"));
            root = parentLoader(root, myLoader);
            FXMLDocumentAddStudentController addStudentController = myLoader.<FXMLDocumentAddStudentController>getController();
            if( event.getSource() == this.addButtonMain )
            {
                addStudentController.initializeDataAdd( this.obsListAlumno, this.abd, this.control);
            } else if( event.getSource() == this.modifyButtonMain )
            {
                addStudentController.initializeDataModify( this.obsListAlumno, index, this.abd, this.control );
            }
        } else {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLDocumentAddCourse.fxml"));
            root = parentLoader(root, myLoader);
            FXMLDocumentAddCourseController addCourseController = myLoader.<FXMLDocumentAddCourseController>getController();
            if( event.getSource() == this.addButtonMain )
            {
                addCourseController.initializeDataAdd( this.obsListCurso, this.abd, this.control);
            }
            else if( event.getSource() == this.modifyButtonMain )
            {
                addCourseController.initializeDataModify( this.obsListCurso, index, this.abd, this.control );
            }
        }
        
        Scene scene = new Scene (root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Student data");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
    
    private void showPhotoOnNewStage( Image photo )
    {
        Pane root = null;
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource("FXMLDocumentShowPhoto.fxml"));
        //root = parentLoader(root, myLoader);
         try {
            root = (Pane) myLoader.load();
        } catch( IOException e ) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error occurred");
            alert.setHeaderText("Oops! That was a mistake");
            alert.setContentText("An error has occurred while loading a new window."
                    + "\n\n" + "Please contact the support staff."
                    + "\n\n" + "The application will now exit.");
            alert.showAndWait();
            System.exit(-1);
        }
         
        FXMLDocumentShowPhotoController showPhoto = myLoader.<FXMLDocumentShowPhotoController>getController();
        
        if( photo == null ) {
            photo = new Image( "images/nophoto.png" );
        }
        showPhoto.initializePhoto( photo );
        
        Scene scene = new Scene (root, photo.getWidth(), photo.getHeight() );
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle( "Student photo" );
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.show();
    }
    
    private void showEnrollmentsOnNewStage( Alumno a )
    {
        Pane root = null;
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource( "FXMLDocumentShowEnrollments.fxml" ));
        root = parentLoader(root, myLoader);
        FXMLDocumentShowEnrollmentsController showEnrollments = myLoader.<FXMLDocumentShowEnrollmentsController>getController();
        
        AlumnoEquals e = new AlumnoEquals( a, abd );
        
        showEnrollments.initializeData( e, obsListCurso , this.abd, obsListMatricula);
        
        Scene scene = new Scene( root );
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle( a.getNombre() + "'s enrollments" );
        stage.initModality( Modality.APPLICATION_MODAL );
        stage.setResizable( false );
        stage.showAndWait();
    }
    private void showStudentsOnNewStage( Curso c ){
        Pane root = null;
        FXMLLoader myLoader = new FXMLLoader(getClass().getResource( "FXMLDocumentModifyEnrollments.fxml" ));
        root = parentLoader(root, myLoader);
        FXMLDocumentModifyEnrollmentsController showEnrollments = myLoader.<FXMLDocumentModifyEnrollmentsController>getController();
        CursoEquals e = new CursoEquals( c, abd );
        System.out.println("DOCUMENTO CONTROLLER ANTES DE LLAMAR A LA NUEVA VENTANA DE CURSOS      " + this.abd.getMatriculasDeCurso(c).size());

        showEnrollments.initializeData( c, e, obsListMatricula , this.abd, obsListAlumno);
        
        Scene scene = new Scene( root );
        Stage stage = new Stage();
        stage.setScene(scene);
        //stage.setTitle( a.getNombre() + "'s enrollments" );
        stage.initModality( Modality.APPLICATION_MODAL );
        stage.setResizable( false );
        stage.showAndWait();
    }

    /**
     * Actions for when an element wants to be added.
     * 
     * @param event 
     */
    @FXML
    private void addHandler(ActionEvent event) {
        goToScene( event, -1 );  // The add method does not need an index
    }

    /**
     * Actions to perform when an element is modified.
     * 
     * @param event 
     */
    @FXML
    private void modifyHandler(ActionEvent event) {
        int index = tableView.getSelectionModel().getSelectedIndex();
        if( index >= 0 ) {
            goToScene( event, index );
        }
    }
            
    /**
     * Action to do when some element is removed.
     * 
     * @param event 
     */
    @FXML
    private void deleteHandler(ActionEvent event) {
        Alert delete = new Alert( AlertType.CONFIRMATION );
        delete.setTitle( "Delete confirmation" );
        delete.setHeaderText( "Really delete?" );
        boolean deleteStudent = this.tableStudents.isSelected();
        
        if( deleteStudent ) {
            delete.setContentText( "Do you really want to delete the selected student?" );
        } else {  // tableCourses is selected
            delete.setContentText( "Do you really want to delete the selected course?" );
        }
        
        Optional<ButtonType> result = delete.showAndWait();
        if( result.isPresent() && result.get() == ButtonType.OK )
        {
            int index = tableView.getSelectionModel().getSelectedIndex();
            if( deleteStudent ) {
                
                control.setText( "Student " + obsListAlumno.get( index ).getNombre() + " was removed.");
                obsListAlumno.remove( index );
            } else {
            
                control.setText( "Course " + obsListCurso.get( index ).getTitulodelcurso() + " was removed.");
                obsListCurso.remove( index );
            }
        }
        
        abd.salvar();
    }

    /**
     * Changes the theme of the application according to the menu button pressed.
     * 
     * @param event Light or dark, depending on what the user chose.
     */
    @FXML
    private void changeTheme(ActionEvent event) {
    }

    /**
     * Changes the font size according to the corresponding menu option pressed.
     * 
     * @param event 50%, 100% or 125%, depending on what the user clicked.
     */
    @FXML
    private void changeFontSize(ActionEvent event) {
    }

    @FXML
    private void aboutHandler(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeight(500);
        alert.setWidth(500);
        alert.setTitle("About");
        alert.setHeaderText("About Pangea Corporation");
        alert.setContentText( "This deliverable was made by Imaddin Ahmed Mohamed and Nahuel Unai Roselló Beneitez."
                + "\n\n" + "If you find some bugs, please let us know at fakemail@fakedomain.org"
                + "\n\n" + "We hope you enjoy our application :)" );
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        // Was here some fascist text?
        /*alert.setContentText("This company arose from the need for a coup in 1936 \n\n"
                + "after our CEO Don Francisco Franco Bahamonte alias the caudillo\n"
                + "perpetrated before a society of queers and lazy.");
        */
        alert.showAndWait();
    }

    @FXML
    private void showCourses(ActionEvent event) {
        if(this.aCursos == false){
            setTableToCourses();
            
            
        }else {
            setTableToStudents();
           
        }
    }
}
