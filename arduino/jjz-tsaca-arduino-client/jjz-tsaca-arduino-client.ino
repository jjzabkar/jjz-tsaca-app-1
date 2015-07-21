/*
 * This module contains all business logic, setup(), and loop().
 * 
 */

const unsigned long DEFAULT_LOOP_TIME_MILLIS = 60000;
const unsigned long MIN_LOOP_TIME_MILLIS = 5000;
unsigned long loopCount = 0;

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Print &obj, T arg) { obj.print(arg); return obj; }


void setup(void)
{
  Serial.begin(115200);
  configureRGBOutputPins();
  setColorRGB(254,0,0); //red
  initializeWifi();
  setColorRGB(254,140,0); //orange
  setStaticIpAddress();
  setColorRGB(255,200,0); // yellow
}


void loop(void){
  unsigned long startLoopMillis = millis();
  setColorRGB(0,254,0); // green
  connectToWifiNetwork();
  setColorRGB(255,200,0); // yellow
  doWebClientTest();
  setColorRGB(0,254,0); // green
  disconnectFromWifiNetwork();
  delay(5000);
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  unsigned long sleepTime = min(max(MIN_LOOP_TIME_MILLIS, DEFAULT_LOOP_TIME_MILLIS - elapsedLoopMillis), DEFAULT_LOOP_TIME_MILLIS);
  Serial << "*** Sleep for " << sleepTime << "ms ****\r\n"; 
}

