/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testlibreria;

import accesoaBD.AccesoaBD;
import modelo.Matricula;

/**
 *
 * @author icemole
 */
public class MatriculaEquals extends Matricula {
    
    private AccesoaBD abd;
    public MatriculaEquals(Matricula m){
        super(m.getFecha(),m.getCurso(),m.getAlumno());
    }
    public MatriculaEquals( Matricula m, AccesoaBD abd ) {
        super( m.getFecha(), m.getCurso(), m.getAlumno() );
        this.abd = abd;
    }
    
    @Override
    public boolean equals( Object o )
    {
        if( o instanceof MatriculaEquals )
        {
            MatriculaEquals me = (MatriculaEquals) o;
            return new AlumnoEquals( this.getAlumno(), this.abd ).equals( new AlumnoEquals( me.getAlumno(), this.abd ) )
                    && new CursoEquals( this.getCurso() ).equals( new CursoEquals( me.getCurso() ) )
                    && this.getFecha().equals( me.getFecha() );
        }
        else if( o instanceof Matricula )
        {
            Matricula me = (Matricula) o;
            System.out.println("Equaaaaaaaaaaaaaaaaaaaaals");
            System.out.println(new CursoEquals( this.getCurso() ).equals( new CursoEquals( me.getCurso())));

            return this.getAlumno().getDni().equals( me.getAlumno().getDni() )
                    && new CursoEquals( this.getCurso() ).equals( new CursoEquals( me.getCurso() ) )
                    && this.getFecha().equals( me.getFecha() );
        }
        return false;
    }
}
