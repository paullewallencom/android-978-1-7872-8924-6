#include "Adafruit_Sensor.h"
#include <DHT.h>
#include <ESP8266WiFi.h>
#include <Adafruit_BMP280.h>
#include <PubSubClient.h>

const char* ssid = "your_SSID";
const char* pwd = "your WiFi password";

#define DHTPIN D5
#define DHTTYPE DHT11
#define TEMTPIN A0

WiFiClient client;
DHT dht(DHTPIN, DHTTYPE);
Adafruit_BMP280 bme;

// Setup MQTT client
PubSubClient mqttClient(client);

IPAddress mqtt_server(192, 168, 1, 3); 
char *topic = "channel1";

void setup() {
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
 
  dht.begin();
  WiFi.begin(ssid, pwd);
  Serial.println("Connecting to Wifi...");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.println("..");
    delay(400);
  }

  initMQTT(); 
  bme.begin();
  Serial.println("Wifi connected.");
}

void loop() {

    digitalWrite(LED_BUILTIN, LOW); // Led ON
  
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  float press = bme.readPressure() / 100;
    
  int lightVal = analogRead(TEMTPIN) * 0.9765625;
  Serial.println("Temperature: " + String(t));
  Serial.println("Humidity: " + String(h));
  Serial.println("TEMT:" + String(lightVal));
  Serial.println("Pressure: " + String(press));

  // Connect to MQTT server
  if (!client.connected()) {
    connect();
  }

  String payload="{\"temp\":\"" + String(t) + "\", \"hum\":\"" + String(h) + "\", \"press\": \"" + String(press) + "\", \"light\":\"" + String(lightVal) + "\"}";

 
  mqttClient.publish(topic, payload.c_str());
 
  digitalWrite(LED_BUILTIN, HIGH);
  delay(30000);
  
}

void initMQTT() {
   mqttClient.setServer(mqtt_server, 1883);
  
}

void connect() {
   while (!mqttClient.connected()) {
      Serial.println("Connecting to MQTT Server....");
      boolean result = mqttClient.connect("euryeury23429");
      if (result) {
        Serial.println("Client connected");
      }
      else {
        Serial.println("Client not connected. Next attempt in 5 secs");
        delay(5000);
      }
   }
}

