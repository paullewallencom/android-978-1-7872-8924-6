
#include <Adafruit_NeoPixel.h>
#include <Ethernet.h>
#include "aREST.h"


#define PIN 5
#define LED_NUMBER 60

#define FORWARD 1
#define REVERSE 2

// Server definition
#define SERVER_PORT 80


byte mac[] = { 0xDD, 0xAB, 0xCE, 0xFF, 0xEE, 0xED };
IPAddress ip(192,168,1,14);

struct data {
  String color;
  int wait;
  int dir;
  int func;

  int r;
  int g;
  int b;
} ;

Adafruit_NeoPixel strip = 
          Adafruit_NeoPixel(LED_NUMBER, PIN, NEO_GRB + NEO_KHZ800);

// create the server
EthernetServer server(SERVER_PORT);

// Create aREST
aREST rest = aREST();
    
void setup() {
  Serial.begin(9600);
  Serial.println("Arduino Strip RGB Led");
  strip.begin();
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    Ethernet.begin(mac, ip);
  }
  Serial.println("Connection ok");
  server.begin();
  rest.function("fill", setStripColor);
  rest.function("clear", setClearStrip);
  rest.function("rainbow", setRainbow);
}

void loop() {
  EthernetClient client = server.available();
  rest.handle(client);
  //clearStrip(5, REVERSE);
 // rainbow(5, FORWARD);
 // fillStrip(strip.Color(40,190,49), 10, FORWARD);
}

void rainbow(int wait, int direction) {
   int first, last;
   setDirection(&first, &last, direction);
   byte color[3];
   byte count, a0, a1, a2;
   color[count]=random(256);
   a0=count+random(1)+1;
   color[a0%3]=random(256-color[count]);
   color[(a0+1)%3]=255-color[a0%3]-color[count];

  count+=random(15); // to avoid repeating patterns
  count%=3;  
  fillStrip(strip.Color(color[0], color[1], color[2]), wait, direction);
   
}

void fillStrip(uint32_t color, int wait, int direction) {
   int first, last;
   setDirection(&first, &last, direction);

  
  for (int p = first; p <= last; p++) {
    strip.setPixelColor(abs(p), color);
    strip.show();
    delay(wait);
  }
}



void clearStrip(int wait, int direction) {
  int first, last;
  setDirection(&first, &last, direction);

  
  for (int p = first; p <= last; p++) {
    strip.setPixelColor(abs(p), 0);
    strip.show();
    delay(wait);
  }
  
}

void setDirection(int *first, int *last,int direction) {
  
  if (direction == FORWARD) {
    *first = 1;
    *last = LED_NUMBER;
  }
  else {
    *first = -LED_NUMBER;
    *last = -1;
  }
 
}

// AABBDD Dir(1) Wait(2) 
struct data parseCommand(String command) {
  struct data parsedData;

  parsedData.color = command.substring(1,7);
  String p = command.substring(0,1);
  parsedData.dir = p.toInt();
  p = command.substring(7,9);
  parsedData.wait = p.toInt();

  parsedData.func = command.substring(9).toInt();

  long tmpColor = strtol( &("#" + parsedData.color)[1], NULL, 16);

  
  parsedData.r = tmpColor >> 16;
  parsedData.g = tmpColor >> 8 & 0xFF;
  parsedData.b = tmpColor & 0xFF;
  
  return parsedData;
}


// Function exposed
int setStripColor(String command) {
  Serial.println("Color strip function...");
  struct data value = parseCommand(command);
  debugData(value);
  fillStrip(strip.Color(value.r,value.g,value.b), value.wait, value.dir);
  return 1;
}


int setClearStrip(String command) {
  Serial.println("Clear strip function...");
  struct data value = parseCommand(command);
  debugData(value);
  clearStrip(value.wait, value.dir);
  return 1;
}

int setRainbow(String command) {
  Serial.println("Rainbow strip function...");
  struct data value = parseCommand(command);
  rainbow(value.wait, value.dir);
  debugData(value);
}

void debugData(struct data value) {
  Serial.println("=========================");
  Serial.println("Color ["+value.color+"]");
  Serial.println("Direction ["+String(value.dir)+"]");
  Serial.println("Wait ["+String(value.wait)+"]");
  Serial.println("Func ["+String(value.func)+"]");
  Serial.println("R ["+String(value.r)+"]");
  Serial.println("G ["+String(value.g)+"]");
  Serial.println("B ["+String(value.b)+"]");
  Serial.println("=========================");

}




