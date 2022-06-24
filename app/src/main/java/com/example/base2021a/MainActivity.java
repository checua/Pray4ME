package com.example.base2021a;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    private TextView tvRegistrar;
    private EditText etUsuario;
    private EditText etContra;
    private Button btnLogin;

    public String strUsuario;
    public String strContra;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finishActivity(0);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsuario = (EditText) findViewById(R.id.etUsuario);
        etContra = (EditText) findViewById(R.id.etContra);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegistrar = (TextView) findViewById(R.id.tvRegistrar);

        strUsuario = etUsuario.toString();
        strContra = etContra.toString();

        SharedPreferences sharedpreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        //get your string with default string in case referred key is not found
        String str1 = sharedpreferences.getString("user1",  null);
        String str2 = sharedpreferences.getString("user2",  null);

        if (str1 != null && str2 != null) {
            //etUsuario.setText(str1);
            //etContra.setText(str2);
/*
            etUsuario.setEnabled ( false );
            etContra.setEnabled ( false );
            btnLogin.setEnabled ( false );
*/
            etUsuario.setVisibility (View.INVISIBLE);
            etContra.setVisibility (View.INVISIBLE);
            btnLogin.setVisibility (View.INVISIBLE);

            TareaWSConsulta tarea = new TareaWSConsulta();
            tarea.execute();
        }

    }


    @Override
    protected void onStart()
    {
        super.onStart();
        //Toast.makeText(getApplicationContext(),"Now onStart() calls", Toast.LENGTH_LONG).show(); //onStart Called
        tvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReg = new Intent(MainActivity.this, Register.class);
                MainActivity.this.startActivity(intentReg);
            }
        });

        btnLogin.setOnClickListener( v -> {
            if ((etUsuario.getText().length() > 0) && (etContra.getText().length() > 0)) {

                //initialize it with a name to your pref-file and it's mode, public/private
                SharedPreferences sharedpreferences1 = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

                //get and initialise an instance of SharedPreferences's Editor
                SharedPreferences.Editor editor = sharedpreferences1.edit();

                //put desired string to the editor with a key to reference.
                editor.putString("user1", etUsuario.getText().toString());
                editor.putString("user2", etContra.getText().toString());
                //apply your changes
                editor.apply();

                TareaWSConsulta tarea = new TareaWSConsulta();
                tarea.execute();
            } else {
                tvRegistrar.setText("Por favor ingrese datos válidos!");
                etUsuario.setText("");
                etContra.setText("");
            }
        } );
    }


    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    @SuppressWarnings("deprecation")
    private class TareaWSConsulta extends AsyncTask<String, Integer, Integer> {

        protected Integer doInBackground(String... params) {

            int resul = 0;
            final String NAMESPACE = "http://microsoft.com/webservices/";
            final String URL = "https://webservicex.azurewebsites.net/WebServices/WebService1.asmx";
            final String METHOD_NAME = "LogUsuReturnId";
            final String SOAP_ACTION = "http://microsoft.com/webservices/LogUsuReturnId";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            SharedPreferences sharedpreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            //get your string with default string in case referred key is not found
            String str1 = sharedpreferences.getString("user1",  null);
            String str2 = sharedpreferences.getString("user2",  null);

            if (str1 != null && str2 != null) {
                request.addProperty("usuario", str1);
                request.addProperty("contra", str2);
            }
            else {
                request.addProperty("usuario", strUsuario);
                request.addProperty("contra", strContra);
            }
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION, envelope);
                /*SoapObject resSoap = (SoapObject) envelope.getResponse();*/
                SoapPrimitive resSoap = (SoapPrimitive)envelope.getResponse();
                resul = Integer.parseInt(resSoap.toString());
            } catch (Exception e) {
                resul = 0;
            }


            return resul;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
            btnLogin.setEnabled(false);
        }

        @SuppressLint("WrongConstant")
        protected void onPostExecute(Integer result) {


            if (result != 0) {


                tvRegistrar.setText ( "Acceso concedido" );


                Intent intentReg = new Intent ( MainActivity.this, Main2Activity.class );
                /*intentReg.putExtra ( "idasesor", String.valueOf ( this.listaUsuarios[0].idAsesor ) );
                intentReg.putExtra ( "nombres", String.valueOf ( this.listaUsuarios[0].Nombres ) );
                intentReg.putExtra ( "tel_asesor", String.valueOf ( this.listaUsuarios[0].Telefono ) );
*/
                MainActivity.this.startActivity ( intentReg );
            } else {
                tvRegistrar.setText ( "Acceso denegado" );

                etUsuario.setVisibility (View.VISIBLE);
                etContra.setVisibility (View.VISIBLE);
                btnLogin.setVisibility (View.VISIBLE);

                etUsuario.setText ( "" );
                etContra.setText ( "" );
            }



        }
    }
}