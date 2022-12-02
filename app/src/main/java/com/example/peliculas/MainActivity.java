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
    String codigo,nombre,genero,num_pel;
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

    public void Adicionar(View view){
        codigo=jetcodigo.getText().toString();
        nombre=jetnombre.getText().toString();
        if (codigo.isEmpty() || nombre.isEmpty()){
            Toast.makeText(this, "Los campos son obligatorios", Toast.LENGTH_SHORT).show();
        }
        else{
            if (jrbaccion.isChecked())
                genero="Accion";
            else {
                if (jrbfantasia.isChecked())
                    genero="Fantasia";
                else {
                    genero="Suspenso";
                }
            }

            // Create a new user with a first and last name
            Map<String, Object> pelicula = new HashMap<>();
            pelicula.put("Codigo", codigo);
            pelicula.put("Nombre", nombre);
            pelicula.put("Activo", "si");

// Add a new document with a generated ID
            db.collection("Funciones")
                    .add(pelicula)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "Pelicula añadida", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
                            //System.out.println("errorrrrrrrrrrrrrrrr" + e);
                            Toast.makeText(MainActivity.this, "Error al añadir pelicula", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    ///error en el boton pue a buscar me saca de la app
    public void Consultar(View view){
        codigo=jetcodigo.getText().toString();
        if (codigo.isEmpty()) {
            Toast.makeText(this, "Es necesario el código", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else{
            db.collection("Funciones")
                    .whereEqualTo("Codigo",codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                respuesta = true;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    num_pel=document.getId();
                                    jetnombre.setText(document.getString("Nombre"));
                                    if(document.getString("Genero").equals("Accion"))
                                        jrbaccion.setChecked(true);
                                    else
                                    if(document.getString("Genero").equals("Fantasia"))
                                        jrbfantasia.setChecked(true);
                                    else
                                        jrbsuspenso.setChecked(true);
                                    if(document.getString("Activo").equals("si"))
                                        jcbactivo.setChecked(true);
                                    else
                                        jcbactivo.setChecked(false);
                                    // Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                //Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }

    }

    public void Anular(View view){
        codigo=jetcodigo.getText().toString();
        nombre=jetnombre.getText().toString();
        if (codigo.isEmpty() || nombre.isEmpty()){
            Toast.makeText(this, " Los campos son requeridos", Toast.LENGTH_SHORT).show();
            jetcodigo.requestFocus();
        }
        else{
            if (respuesta == true) {
                if (jrbaccion.isChecked())
                    genero = "Accion";
                else if (jrbfantasia.isChecked())
                    genero = "Fantasia";
                else
                    genero = "Suspenso";

                Map<String, Object> pelicula = new HashMap<>();
                pelicula.put("Codigo", codigo);
                pelicula.put("Nombre", nombre);
                pelicula.put("Proximamente", "si");

// Add a new document with a generated ID
            db.collection("Funciones")
                    .add(pelicula)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "Pelicula fuera de cartelera", Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
                            Toast.makeText(MainActivity.this, "Error sacando película de cartelera", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }



    }
    public void Cancelar(View view){
        Limpiar_campos();
    }

    private void Limpiar_campos() {
        jetnombre.setText("");
        jetcodigo.setText("");
        jrbaccion.setChecked(true);
        jcbactivo.setChecked(false);
        respuesta=false;
        jetcodigo.requestFocus();
    }
}