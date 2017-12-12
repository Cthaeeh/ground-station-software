
void setup() {
  Serial.begin(115200);
}

int counter = 0;
int counter2 = 0;
int counter3 = 0;
int counter4 = 0;
int counter5 = 0;
int counter6 = 0;

void loop()
{
  byte packet[13];
  delay(1000);

  packet[0] = 12; //start byte
  packet[1] = 13; //start byte
  
  packet[2] = 0;  // Id
  packet[3] = 0;  // lat
  packet[4] = (counter++)%50;  // 
  packet[5] = 0;  // lon
  packet[6] = (counter2++)%500;  // 
  packet[7] = 3;  // height 
  packet[8] = 42;  // sats
  packet[9] = 4;  // quality
  packet[10] = 33;  // time
  packet[11] = 12; // stop byte
  packet[12] = 22; // stop byte
  
  Serial.write(packet, 13);

  packet[0] = 12; //start byte
  packet[1] = 13; //start byte
  
  packet[2] = 1;  // Id
  packet[3] = 0;  // lat
  packet[4] = (counter3++)%150;  // 
  packet[5] = 0;  // lon
  packet[6] = (counter4++)%150;  // 
  packet[7] = 3;  // height 
  packet[8] = 42;  // sats
  packet[9] = 4;  // quality
  packet[10] = 33;  // time
  packet[11] = 12; // stop byte
  packet[12] = 22; // stop byte
  
  Serial.write(packet, 13);

  packet[0] = 12; //start byte
  packet[1] = 13; //start byte
  
  packet[2] = 2;  // Id
  packet[3] = 0;  // lat
  packet[4] = (counter5++)%10;  // 
  packet[5] = 0;  // lon
  packet[6] = (counter6++)%10;  // 
  packet[7] = 3;  // height 
  packet[8] = 42;  // sats
  packet[9] = 4;  // quality
  packet[10] = 33;  // time
  packet[11] = 12; // stop byte
  packet[12] = 22; // stop byte
  
  Serial.write(packet, 13);
}





