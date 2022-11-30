package com.example.peliculas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText jetcodigo,jetnombre;
    RadioButton jrbaccion,jrbfantasia,jrbsuspenso;
    CheckBox jcbencartelera;
    String codigo,nombre,genero;
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
        jcbencartelera=findViewById(R.id.cbencartelera);
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
            pelicula.put("En cartelera", "si");

// Add a new document with a generated ID
            db.collection("Funciones")
                    .add(pelicula)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(MainActivity.this, "Película añadida", Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
                            Toast.makeText(MainActivity.this, "Error añadiendo película", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void Limpiar_campos(){
        jetnombre.setText("");
        jetcodigo.setText("");
        jrbaccion.setChecked(true);
        jcbencartelera.setChecked(false);
        jetcodigo.requestFocus();
    }


}