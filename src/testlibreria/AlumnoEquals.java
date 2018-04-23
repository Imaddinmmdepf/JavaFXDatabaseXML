/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import modelo.Alumno;
import modelo.Matricula;

/**
 *
 * @author icemole
 */
public class AlumnoEquals extends Alumno {
    
    private List<Matricula> enrollmentsStudent;
    
    public AlumnoEquals( Alumno a, AccesoaBD abd )
    {
        super( a.getDni(), a.getNombre(), a.getEdad(), a.getDireccion(), a.getFechadealta(), a.getFoto() );
        
        enrollmentsStudent = new ArrayList<>();
        for( Matricula m : abd.getMatriculas() )
        {
            if( this.equals( m.getAlumno() ) )  // Quite sure it will work
            {
                enrollmentsStudent.add( m );
            }
        }
    }
    
    public List<Matricula> getEnrollmentsStudent()
    {
        for( Matricula m : enrollmentsStudent ) { System.out.println( m.getCurso().getTitulodelcurso() ); }
        return enrollmentsStudent;
    }
    public List<Matricula> setAllEnrollments(ObservableList l){
        this.enrollmentsStudent = l;
        return this.enrollmentsStudent;
    }
    public void setEnrollmentsStudent(Matricula matricula){
         this.enrollmentsStudent.add(matricula);
    }
    @Override
    public boolean equals( Object o )
    {
        if( o instanceof AlumnoEquals )
        {
            return ( ( AlumnoEquals ) o ).getDni().equals( this.getDni() );
        }
        else if( o instanceof Alumno )
        {
            return ( ( Alumno ) o ).getDni().equals( this.getDni() );
        }
        return false;
    }
}
