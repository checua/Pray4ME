package com.example.base2021a;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Register extends Activity {

    private EditText etNombre;
    private EditText etTelefono;
    private EditText etCorreo;
    private EditText etConfirCorreo;
    private EditText etContra;
    private Button btnReg;

    private TextView txtResultado;

    public String res;

    public String strNombre;
    public String strTelefono;
    public String strCorreo;
    public String strConfirCorreo;
    public String strContra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNombre = (EditText)findViewById(R.id.etNombre);
        etTelefono = (EditText)findViewById(R.id.etTel);
        etCorreo = (EditText)findViewById(R.id.etCorreo);
        etConfirCorreo = (EditText)findViewById(R.id.etConfirCorreo);
        etContra = (EditText)findViewById(R.id.etContra);
        btnReg = (Button)findViewById(R.id.btnReg);
        txtResultado = (TextView)findViewById(R.id.tvResult);



        btnReg.setOnClickListener(new View.OnClickListener () {

            @Override
            public void onClick(View v) {
                if((etNombre.getText().length() > 0) && (etTelefono.getText().length() > 0) && (etContra.getText().length() > 0) && (etCorreo.getText().length() > 0) && (etConfirCorreo.getText().length() > 0)) {

                    strNombre = etNombre.getText().toString();
                    strTelefono = etTelefono.getText().toString();
                    strCorreo = etCorreo.getText().toString();
                    strConfirCorreo = etConfirCorreo.getText().toString();
                    strContra = etContra.getText().toString();

                    if(strCorreo.equals(strConfirCorreo)){
                        txtResultado.setText("");
                        btnReg.setEnabled(false);
                        TareaWSInsercion1 tarea = new TareaWSInsercion1();
                        tarea.execute();
                    }
                    else
                    {
                        txtResultado.setText("Los correos no coinciden");
                    }


                }
                else
                {
                    txtResultado.setText("Faltan datos, no se pudo registrar!");
                }
            }
        });

    }
    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
    */

    private class TareaWSInsercion1 extends AsyncTask<String,Integer,Boolean> {

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://microsoft.com/webservices/";
            final String URL = "https://webservicex.azurewebsites.net/WebServices/WebService1.asmx";
            final String METHOD_NAME = "NuevoUsuarioSimple2";
            final String SOAP_ACTION = "http://microsoft.com/webservices/NuevoUsuarioSimple2";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("nombres", strNombre);
            request.addProperty("telefono", strTelefono);
            request.addProperty("correo", strCorreo);
            request.addProperty("contra", strContra);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope( SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml = (SoapPrimitive) envelope.getResponse();
                res = resultado_xml.toString();

                if (res != null)
                    resul = true;
            } catch (Exception e) {
                resul = false;
            }



            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result) {

                //initialize it with a name to your pref-file and it's mode, public/private
                SharedPreferences sharedpreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);

                //get and initialise an instance of SharedPreferences's Editor
                SharedPreferences.Editor editor = sharedpreferences.edit();

                //put desired string to the editor with a key to reference.
                editor.putString("user1", etCorreo.getText().toString());
                editor.putString("user2", etContra.getText().toString());
                //apply your changes
                editor.apply();

                txtResultado.setText("Insertado OK");

                /*
                String[] parts = res.split("\\|");
                String part1 = parts[0]; // idAsesor
                String part2 = parts[1]; // Nombres
                String part3 = parts[2]; // Teléfono
                */

                Intent intentReg = new Intent(Register.this, Default.class);
                /*intentReg.putExtra("idasesor", parts[0]);
                intentReg.putExtra("nombres", parts[1]);
                intentReg.putExtra("tel_asesor", parts[2]);
                 */
                Register.this.startActivity(intentReg);

            }
            else
                txtResultado.setText("Error, no se pudo registrar!");
        }
    }
}

/*
[WebMethod]
        public int NuevoUsuarioSimple2(string nombres, string telefono, string correo, string contra)
        {
            SqlConnection con =
                new SqlConnection(
                    @"data source = dpa6ecad46.database.windows.net; initial catalog = mapsMarkers; user id = misiones@dpa6ecad46; password = Alopedev7mision");

            con.Open();

            //string sql = "INSERT INTO Clientes (Nombre, Telefono) VALUES (@nombre, @telefono)";
            string sql = "INSERT INTO USUARIOS(nombres, apaterno, amaterno, nick, telefono, correo, contra, foto, obs, dob) OUTPUT INSERTED.idAsesor values (@nombres, 'x', 'y', 'nick', @telefono, @correo, @contra, 'foto1.jpg', 'no obs', '2017-07-03') SELECT SCOPE_IDENTITY()";

            SqlCommand cmd = new SqlCommand(sql, con);

            //cmd.Parameters.Add("@idasesor", System.Data.SqlDbType.Int).Value = idasesor;
            cmd.Parameters.Add("@nombres", System.Data.SqlDbType.NVarChar).Value = nombres;
            cmd.Parameters.Add("@telefono", System.Data.SqlDbType.NVarChar).Value = telefono;

            cmd.Parameters.Add("@correo", System.Data.SqlDbType.NVarChar).Value = correo;
            cmd.Parameters.Add("@contra", System.Data.SqlDbType.NVarChar).Value = contra;

            //int res = cmd.ExecuteNonQuery();
            int res = (int)cmd.ExecuteScalar();

            con.Close();

            return res;
        }

 */