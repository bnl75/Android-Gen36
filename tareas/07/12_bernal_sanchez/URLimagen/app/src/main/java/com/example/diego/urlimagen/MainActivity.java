package com.example.diego.urlimagen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imagen;
    private Button boton;
    private ProgressBar barraProgreso;
    private String miURL = "http://m.memegen.com/m1xsca.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagen = (ImageView) findViewById(R.id.imagenCargada);
        barraProgreso = (ProgressBar) findViewById(R.id.barra);
        boton = (Button) findViewById(R.id.botonCarga);

        barraProgreso.setProgress(0);

        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                barraProgreso.setClickable(false);
                new CargarImagenExterna(imagen).execute(miURL);
            }
        });

    }


    class CargarImagenExterna extends AsyncTask<String, Integer, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private int progreso;

        public CargarImagenExterna(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }


        protected void onProgressUpdate(Integer... values) {
            barraProgreso.setProgress(values[0]);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                while (progreso < barraProgreso.getMax()) {
                    progreso ++;
                    publishProgress(progreso);
                    SystemClock.sleep(20);
                }

                return downloadBitmap(params[0]);
            } catch (Exception e) {
                Log.e("CargarImagenExterna", "doInBackground() " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }

        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.e("CargarImagenExterna", "Descargando imagen desde url: " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

}
