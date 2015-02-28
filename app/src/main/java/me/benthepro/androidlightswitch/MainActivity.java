package me.benthepro.androidlightswitch;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {

    public Socket socket;
    public static final int SERVERPORT = 4444;
    public static final String SERVER_IP = "128.211.230.226";
    public static TextView text;
    public int ledState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text= (TextView)findViewById(R.id.textView);

        final Button button = (Button) findViewById(R.id.buttonLight);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ClientTask clientTask = new ClientTask(SERVER_IP, SERVERPORT);
                clientTask.execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        ClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("led=?");
                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                //byte[] buffer = new byte[1024];

                //int bytesRead;
                InputStream inputStream = socket.getInputStream();

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                //thisshitreads forever
                /*while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                    System.out.println("Here." + response);
                }*/

                //read once and stop task
                if ((ledState = inputStream.read()) == '0') {
                    out.println("led=13H");
                } else {
                    out.println("led=13L");
                }

                //byteArrayOutputStream.write(buffer, 0, bytesRead);
                //response += byteArrayOutputStream.toString("UTF-8");


            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            text.setText(response);
            super.onPostExecute(result);
        }

    }

}


