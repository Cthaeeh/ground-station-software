
void setup() {
  Serial.begin(115200);
}

int counter = 0;

void loop()
{
  byte packet[13];
  delay(500);

  packet[0] = 12; //start byte
  packet[1] = 13; //start byte
  
  packet[2] = 0;  // Id
  packet[3] = 0;  // lat
  packet[4] = 50+(counter++)%10;  // 
  packet[5] = 0;  // lon
  packet[6] = 10;  // 
  packet[7] = 3;  // height 
  packet[8] = 42;  // sats
  packet[9] = 4;  // quality
  packet[10] = 33;  // time
  packet[11] = 12; // stop byte
  packet[12] = 22; // stop byte
  
  Serial.write(packet, 13);
}





