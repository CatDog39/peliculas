package com.example.peliculas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText jetcodigo,jetnombre;
    RadioButton jrbaccion,jrbfantasia,jrbsuspenso;
    CheckBox jcbactivo;
    String codigo,nombre,genero,identPelicula;
    boolean respuesta;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        jetcodigo=findViewById(R.id.etcodigo);
        jetnombre=findViewById(R.id.etnombre);
        jrbaccion=findViewById(R.id.rbaccion);
        jrbfantasia=findViewById(R.id.rbfantasia);
        jrbsuspenso=findViewById(R.id.rbsuspenso);
        jcbactivo=findViewById(R.id.cbactivo);
    }

    private Map<String,Object> contenido(String codigo, String nombre, String genero, String activo){
        Map<String, Object> pelicula = new HashMap<>();
        pelicula.put("Codigo", codigo);
        pelicula.put("Nombre", nombre);
        pelicula.put("Genero", genero);
        pelicula.put("Activo", activo);

        return pelicula;
    }

    private String generoPelicula(){
        if (jrbaccion.isChecked())
            genero="Accion";
        else {
            if (jrbfantasia.isChecked())
                genero = "Fantasia";
            else {
                genero = "Suspenso";
            }
        }
        return genero;

    }

    private void peliculasBaseDatos(String nombreBaseDatos, Map<String,Object> documentoPelicula, String cambioActivo){
        if (cambioActivo.equalsIgnoreCase("add")){
            db.collection(nombreBaseDatos)
                    .add(documentoPelicula)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            mensajeUsuario("Pelicula añadida");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
                            //System.out.println("errorrrrrrrrrrrrrrrr" + e);
                            mensajeUsuario("Error al añadir pelicula");
                        }
                    });
        }
        else {
            db.collection(nombreBaseDatos)
                    .document(identPelicula)
                    .set(documentoPelicula)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            mensajeUsuario("Documento anulada con exito");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
                            //System.out.println("errorrrrrrrrrrrrrrrr" + e);
                            mensajeUsuario("Error al anular documento");
                        }
                    });

        }



    }

    public void Adicionar(View view){
        codigo=jetcodigo.getText().toString();
        nombre=jetnombre.getText().toString();
        if (codigo.isEmpty() || nombre.isEmpty()){
            mensajeUsuario("Los campos son obligatorios");
        }
        else{
            String genero = generoPelicula();

            // Create a new user with a first and last name

            Map<String, Object> pelicula = contenido(codigo,nombre,genero,"si");
            System.out.println(pelicula);

            // Add a new document with a generated ID
            peliculasBaseDatos("Funciones", pelicula, "add");
            Limpiar_campos();
        }
    }
    ///error en el boton pue a buscar me saca de la app
    public void Consultar(View view){
        codigo=jetcodigo.getText().toString();
        if (codigo.isEmpty()) {
            mensajeUsuario("Es necesario el código");
            jetcodigo.requestFocus();
        }
        else {
            db.collection("Funciones")
                    .whereEqualTo("Codigo",codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document: task.getResult()) {
                                    if (document.getString("Activo").equalsIgnoreCase("no")){
                                        mensajeUsuario("El registro existe pero está inactivo");
                                    }
                                    else {
                                        identPelicula = document.getId();
                                        jetnombre.setText(document.getString("Nombre"));
                                        if (document.getString("Genero").equalsIgnoreCase("Accion")){
                                            jrbaccion.setChecked(true);
                                        }
                                        else {
                                            if (document.getString("Genero").equalsIgnoreCase("Fantasia")){
                                                jrbfantasia.setChecked(true);
                                            }
                                            else {
                                                jrbsuspenso.setChecked(true);

                                            }
                                        }
                                        if (document.getString("Activo").equalsIgnoreCase("si")){
                                            jcbactivo.setChecked(true);
                                        }
                                        else {
                                            jcbactivo.setChecked(false);
                                        }
                                    }
                                    mensajeUsuario("La busqueda fue exitosa");
                                    }

                            }
                            else {
                                mensajeUsuario("No se encuentra registro");
                            }
                        }
                    });
        }

    }

    public void Anular(View view){
        codigo=jetcodigo.getText().toString();
        nombre=jetnombre.getText().toString();

        if (codigo.isEmpty() || nombre.isEmpty()){
            mensajeUsuario("Todos los campos son requeridos");
        }
        else {
            genero = generoPelicula();

            //Crear
            Map<String, Object> pelicula = contenido(codigo,nombre,genero,"no");

            //Anular
            peliculasBaseDatos("Funciones", pelicula, "set");
            Limpiar_campos();

        }
    }

    public void Cancelar(View view){
        Limpiar_campos();
    }

    private void Limpiar_campos() {
        jetnombre.setText("");
        jetcodigo.setText("");
        jrbaccion.setChecked(true);
        jrbfantasia.setChecked(false);
        jrbsuspenso.setChecked(false);
        jcbactivo.setChecked(false);
        respuesta=false;
        jetcodigo.requestFocus();
    }

    private void mensajeUsuario(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}