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
  int humidity = DHT.humidity;

  packet[0] = 5; //start bytes
  packet[1] = 10;

  packet[2] = 1;
  
  packet[3] = highByte(humidity);
  packet[4] = lowByte(humidity);

  packet[5] = 0;
}

void getPacket2(byte packet[]) {
  int chk = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
  int temp = DHT.temperature;
  
  packet[0] = 5; //start bytes
  packet[1] = 10;

  packet[2] = 2;

  packet[3] = highByte(temp);
  packet[4] = lowByte(temp);

  packet[5] = 0;
}

int counter = 0;
void getPacket3(byte packet[]) {

  int demoAccel = ((counter ++)%1000); 
  
  packet[0] = 5; //start bytes
  packet[1] = 10;

  packet[2] = 3;

  packet[3] = 3;

  packet[4] = highByte(demoAccel);
  packet[5] = lowByte(demoAccel);
}


void setup() {
  Serial.begin(38400);
}

void loop()
{
  byte packet[6];
  getPacket1(packet);
  Serial.write(packet, 6);
  delay(10);
  getPacket2(packet);
  Serial.write(packet, 6);
  delay(10);
  getPacket3(packet);
  Serial.write(packet, 6);
  delay(10);


  
//    int chk  = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
//  Serial.print("Temperature = ");
//  Serial.println(DHT.temperature);
//  Serial.print("Humidity = ");
//  Serial.println(DHT.humidity);
}




