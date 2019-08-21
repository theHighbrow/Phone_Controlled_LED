package com.example.iot_led;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       connect();
    }
    public void connect()
    {
        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(),"tcp://soldier.cloudmqtt.com:16923",clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setCleanSession(false);
        options.setUserName("uuknfjep");
        options.setPassword("t0wCgMnr8KEt".toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                ImageButton ion = findViewById(R.id.imageButtonON);
                ImageButton ioff = findViewById(R.id.imageButtonOFF);
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("file", "onSuccess: ");
                    ion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            publish(client,"1");
                            ion.setVisibility(View.GONE);
                            ioff.setVisibility(View.VISIBLE);
                        }
                    });
                    ioff.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            publish(client, "0");
                            ioff.setVisibility(View.GONE);
                            ion.setVisibility(View.VISIBLE);
                        }
                    });


                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.e("file", "messageArrived: "+message.toString() );

                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                            try {
                                Log.e("file","message delivered "+token.getMessage().toString());
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("file","onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public  void publish(MqttAndroidClient client, String payload)
    {
        String topic = "led_control";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic,message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
}
