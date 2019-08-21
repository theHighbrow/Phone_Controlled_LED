package com.example.iot_led;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        Button buttonON = findViewById(R.id.buttonON);
        Button buttonOFF = findViewById(R.id.buttonOFF);
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect("on");
            }
        });
        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect("off");
            }
        });
    }
    public void connect(final String state)
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
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("file", "onSuccess: ");
                    if (state.equals("on")){
                        publish(client,"0");
                    }
                    if (state.equals("off")) {
                        publish(client, "1");
                    }
                    client.setCallback(new MqttCallback() {
                        TextView textView = findViewById(R.id.textView);
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.e("file", "messageArrived: "+message.toString() );
                            textView.setText("The led is currently "+ message.toString());
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                            Log.e("file","message delivered");
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
