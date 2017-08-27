#include <dht.h>

dht DHT;

#define DHT11_PIN 7

//returns one telemetry package
void getMeasurements(byte packet[])
{
  int chk = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
  int temp1 = DHT.temperature;      // For simulation purpose use temp twice.
  int temp2 = DHT.temperature;
  int humd1 = DHT.humidity;
  int humd2 = DHT.humidity;

  //Lets asume we want to send 64 bits

  packet[0] = highByte(temp1);
  packet[1] = lowByte(temp1);

  packet[2] = highByte(temp2);
  packet[3] = lowByte(temp2);

  packet[4] = highByte(humd1);
  packet[5] = lowByte(humd1);

  packet[6] = highByte(humd2);
  packet[7] = lowByte(humd2);

  packet[8] = 42;
  packet[9] = 0;
}


void getPacket1(byte packet[]) {
  int chk = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
  int humidity = 10; // DHT.humidity;

  packet[0] = 5; //start bytes
  packet[1] = 10;

  packet[2] = 1;
  
  packet[3] = lowByte(humidity);
  packet[4] = highByte(humidity);

  packet[5] = 0;
  packet[6] = 0;

  packet[7] = 5; //stop bytes
  packet[8] = 20;
}

void getPacket2(byte packet[]) {
  int chk = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
  int temp = 55;
  
  packet[0] = 5; //start bytes
  packet[1] = 10;

  packet[2] = 2;

  packet[3] = lowByte(temp);
  packet[4] = highByte(temp);

  packet[5] = 0;

  packet[6] = 5; //stop bytes
  packet[7] = 20;
}

int counter = 0;
void getPacket3(byte packet[]) {

  int demoAccel = ((counter ++) % 100);
  if(counter == 1000){
    counter = 0;
  }
  
  packet[0] = 5; //start bytes
  packet[1] = 10;

  packet[2] = 3;

  packet[3] = 3;

  packet[4] = lowByte(demoAccel);
  packet[5] = highByte(demoAccel);

  packet[6] = 5; //stop bytes
  packet[7] = 20;
}

void getPacket4(byte packet[]) {
  int a = 2;
  int b = 5;
  
  packet[0] = 12; //start bytes
  packet[1] = 13;

  packet[2] = 1;
  packet[3] = 1;
  packet[4] = 1;
  
  packet[5] = 12;
  packet[6] = 22;
}

void setup() {
  Serial.begin(38400);
}

void loop()
{
  byte packet[4];
  delay(3000);

  packet[0] = 12; //start byte

  packet[1] = 0;  // Id
  packet[2] = 1;  // some value
  packet[3] = 13; // stop byte
  
  Serial.write(packet, 4);
}




