#include <Adafruit_CC3000.h>
#include <ccspi.h>
#include <SPI.h>
#include <string.h>
#include "utility/debug.h"

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins
// On an UNO, SCK = 13, MISO = 12, and MOSI = 11
Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed but DI

#define WLAN_SSID       "ZabkarN"        // cannot be longer than 32 characters!
#define WLAN_PASS       "echo2009zzzzz"
// Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define WLAN_SECURITY   WLAN_SEC_WPA
#define IDLE_TIMEOUT_MS  3000      // Amount of time to wait (in milliseconds) with no data received before closing the connection.


// What page to grab!
#define WEBSITE      "jjztsacaapp1.cfapps.io"
#define WEBPAGE      "/arrivals/csv"
uint32_t ip = 0;
unsigned long DEFAULT_LOOP_TIME_MILLIS = 60000;
unsigned long MIN_LOOP_TIME_MILLIS = 5000;
unsigned long loopCount = 0;

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Print &obj, T arg) { obj.print(arg); return obj; }

// LED leads connected to PWM pins
// Used by Wifi: 3,4,5,9,10,11,12,13
const int RED_LED_PIN = 6;  
const int GREEN_LED_PIN = 7; 
const int BLUE_LED_PIN = 8; 
unsigned char httpContent[1024]; // buffer array for data recieve over serial port
int contentLength = 0; // MAX= 32,767 (2^15 -1 )

//On the CC3000 shield, we use the following pin connections
//SCK - #13
//MISO #12
//MOSI #11
//CS for CC3000 #10
//VBAT_EN #5
//CS for SD Card #4
//IRQ #3

// Used to store the current intensity level of the individual LEDs
int redIntensity = 0;
int greenIntensity = 0;
int blueIntensity = 0;

// Length of time we spend showing each color
const int DISPLAY_TIME = 60; // In milliseconds


void setup(void)
{
  Serial.begin(115200);
  configureRGBOutputPins();
  setColorRGB(255,255,255);
  setColorRGB(254,0,0); //red
  initializeWifi();
  setColorRGB(254,140,0); //orange
  setStaticIpAddress();
  setColorRGB(255,200,0); // yellow
}


void loop(void){
  doLedLoop();
  unsigned long startLoopMillis = millis();
  setColorRGB(255,255,255); // white
  connectToWifiNetwork();
  //doArduinoPingTest();
  setColorRGB(255,200,0); // yellow
  doWebClientTest();
  setColorRGB(0,254,0); // green
  disconnectFromWifiNetwork();
  delay(5000);
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  unsigned long sleepTime = min(max(MIN_LOOP_TIME_MILLIS, DEFAULT_LOOP_TIME_MILLIS - elapsedLoopMillis), DEFAULT_LOOP_TIME_MILLIS);
  Serial << "*** Sleep for " << sleepTime << "ms ****\r\n"; 
//  delay(sleepTime);

}



// Read HTTP response data to String, per: http://stackoverflow.com/a/12439969/237225
String content = "";

void connectToWebSite(void){

  /* Try connecting to the website.
     Note: HTTP/1.1 protocol is used to keep the server from closing the connection before all data is read.
  */
  Serial.println(F("\r\n-------------------------------------"));
  Serial << "GET " << WEBPAGE << WEBSITE << "\r\n";

  Adafruit_CC3000_Client www = cc3000.connectTCP(ip, 80);
  if (www.connected()) {
    www.fastrprint(F("GET "));
    www.fastrprint(WEBPAGE);
    www.fastrprint(F(" HTTP/1.1\r\n"));
    www.fastrprint(F("Host: ")); www.fastrprint(WEBSITE); www.fastrprint(F("\r\n"));
    www.fastrprint(F("\r\n"));
    www.println();
  } else {
    Serial.println(F("Connection failed"));    
    return;
  }

  Serial.println(F("\r\n-------------------------------------"));

  content = "";
//  contentLength = 0; // reset!
  /* Read data until either the connection is closed, or the idle timeout is reached. */ 
  unsigned long lastRead = millis();
  while (www.connected() && (millis() - lastRead < IDLE_TIMEOUT_MS)) {
    while (www.available()) {
      char c = www.read();
      content.concat(c);
//      httpContent[contentLength] = c;
//      contentLength++;
      lastRead = millis();
    }
  }
  www.close();
  Serial.println(content);
//  String str(httpContent);
//  Serial.println(str);
  Serial.println(F("-------------------------------------"));
}

void doWebClientTest(void){
  if ((loopCount % 20) == 0){
    ip = 0;
    Serial.println(F("Try looking up the website's IP address by Host Name"));
  }
  Serial << WEBSITE << " -> ";
  while (ip == 0) {
    if (! cc3000.getHostByName(WEBSITE, &ip)) {
      //Serial.println(F("Couldn't resolve!"));
    }
    delay(500);
  }
  cc3000.printIPdotsRev(ip);

  connectToWebSite();

}


void disconnectFromWifiNetwork(void){
  Serial.println(F("\n\nDisconnecting"));
  /* You need to make sure to clean up after yourself or the CC3000 can freak out */
  /* the next time your try to connect ... */
  cc3000.disconnect();
}


void doArduinoPingTest(){

  uint32_t ip = 0;
  Serial.println(F("Lookup Host for www.adafruit.com"));
  long delayed = 200 ;
  while  (ip  ==  0)  {
    if  (!  cc3000.getHostByName("www.adafruit.com", &ip))  {
      Serial << "    Couldn't resolve 'getHostByName'!   ip=" << ip << "\r\n";
    }
    delay(delayed);
    delayed = delayed + 200;
  }  
  cc3000.printIPdotsRev(ip);

  for(int x = 0; x < 10; x = x + 1){
      Serial.print(F("\n\r**** Ping Test ")); Serial.print(x); Serial.print("***");
      Serial.print(F("\n\r    Do a quick ping test on adafruit.com"));
      Serial.print(F("\n\r    Pinging ")); cc3000.printIPdotsRev(ip); Serial.print("...");  
      uint8_t replies = cc3000.ping(ip, 5);
      Serial.print(replies); Serial.println(F(" replies"));
      if (replies){
        Serial.println(F("\n\r    Ping successful!"));
        x = 10 ;
      }else{
        Serial.println(F("\n\r    Ping failed.  Wait 500ms."));
      }
      delay(500);
  }

  
}

void connectToWifiNetwork(void){
  char *ssid = WLAN_SSID;             /* Max 32 chars */
  Serial << "\nAttempting to connect to " << ssid;
  unsigned long time1 = millis();
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY)) {
    Serial.println(F("Failed!"));
    while(1);
  }
  Serial.println(F("Request DHCP"));
  while (!cc3000.checkDHCP())
  {
    Serial.print(F("."));
    delay(100); // ToDo: Insert a DHCP timeout!
  }  
  unsigned long time2 = ( millis() - time1 );
  Serial << "Connected! (" << time2 << "ms)";
}


void initializeWifi(void){
  Serial.println(F("\nInitialising the CC3000 ..."));
  if (!cc3000.begin())
  {
    Serial.println(F("Unable to initialise the CC3000! Check your wiring?"));
    while(1);
  }
  Serial.println(F("Initialized the CC3000"));
}


void setStaticIpAddress(void){
  uint32_t ipAddress = cc3000.IP2U32(192, 168, 1, 19);
  uint32_t netMask = cc3000.IP2U32(255, 255, 255, 0);
  uint32_t defaultGateway = cc3000.IP2U32(192, 168, 1, 1);
  uint32_t dns = cc3000.IP2U32(8, 8, 4, 4);
  if (!cc3000.setStaticIPAddress(ipAddress, netMask, defaultGateway, dns)) {
    Serial.println(F("Failed to set static IP!"));
    while(1);
  }
  Serial.print(F("Set Static IP Address to 192.168.1.19"));
}


void doLedLoop(){
  Serial << "\ndoLedLoop()";
  // Cycle color from red through to green
  // (In this loop we move from 100% red, 0% green to 0% red, 100% green)
  analogWrite(BLUE_LED_PIN, 255);
  Serial << "\ndoLedLoop(): red/green";
  for (greenIntensity = 0; greenIntensity <= 255; greenIntensity+=5) {
        redIntensity = 255-greenIntensity;
        analogWrite(GREEN_LED_PIN, greenIntensity);
        analogWrite(RED_LED_PIN, redIntensity);
        delay(DISPLAY_TIME);
  }

  // Cycle color from green through to blue
  // (In this loop we move from 100% green, 0% blue to 0% green, 100% blue)  
  Serial << "\ndoLedLoop(): green/blue";
  analogWrite(RED_LED_PIN, 255);
  for (blueIntensity = 0; blueIntensity <= 255; blueIntensity+=5) {
        greenIntensity = 255-blueIntensity;
        analogWrite(BLUE_LED_PIN, blueIntensity);
        analogWrite(GREEN_LED_PIN, greenIntensity);
        delay(DISPLAY_TIME);
  }

  // Cycle cycle from blue through to red
  // (In this loop we move from 100% blue, 0% red to 0% blue, 100% red)    
  analogWrite(GREEN_LED_PIN, 255);
  Serial << "\ndoLedLoop(): blue/red";
  for (redIntensity = 0; redIntensity <= 255; redIntensity+=5) {
        blueIntensity = 255-redIntensity;
        analogWrite(RED_LED_PIN, redIntensity);
        analogWrite(BLUE_LED_PIN, blueIntensity);
        delay(DISPLAY_TIME);
  }

}


void configureRGBOutputPins(void){
  Serial << "\n  Configure RGB pins ("<< RED_LED_PIN << ","<< GREEN_LED_PIN << ","<< BLUE_LED_PIN << ") as OUTPUT.\n";
  pinMode(RED_LED_PIN, OUTPUT);
  pinMode(GREEN_LED_PIN, OUTPUT);
  pinMode(BLUE_LED_PIN, OUTPUT);
}


void setColorRGB (int r, int g, int b){
  Serial << "\n  Set LED to RGB (" << r << ","<< g << ","<< b << ")  pins ("<< RED_LED_PIN << ","<< GREEN_LED_PIN << ","<< BLUE_LED_PIN << ")\n";
  redIntensity= 255 - r;
  analogWrite(RED_LED_PIN, redIntensity);
  greenIntensity=255 - g;
  analogWrite(GREEN_LED_PIN, greenIntensity);
  blueIntensity=255 - b;
  analogWrite(BLUE_LED_PIN, blueIntensity);  
  delay(500);
}

