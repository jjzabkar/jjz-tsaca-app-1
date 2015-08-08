/*
 * This module contains all Wifi-related functions
 * for the CC 3000 chip.
 * 
 */
 
#include <Adafruit_CC3000.h>
#include <ccspi.h>
#include <SPI.h>
#include <string.h>
//#include "utility/debug.h"

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   3  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins.  On an UNO, SCK = 13, MISO = 12, and MOSI = 11
Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT,
                                         SPI_CLOCK_DIVIDER); // you can change this clock speed but DI
Adafruit_CC3000_Client www;

#define WLAN_SSID       "ZabkarN"        // cannot be longer than 32 characters!
#define WLAN_PASS       "echo2009zzzzz"
#define WLAN_SECURITY   WLAN_SEC_WPA // Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2
#define IDLE_TIMEOUT_MS  10000      // Amount of time to wait (in milliseconds) with no data received before closing the connection.
#define WEBSITE      "jjztsacaapp1.cfapps.io" // What page to grab!
#define WEBSITE_PORT 80
#define WEBPAGE      "/arrivals/csv"
#define NEWLINE      '\n'
#define STATION_PFX  "STATION"
uint32_t ip = 0;
//#define WEBSITE      "192.168.1.12"
//#define WEBSITE_PORT   8080
//#define WEBPAGE      "/arrivals/test"
#define DASHES       "\n---\n"

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Adafruit_CC3000_Client &obj, T arg) { obj.fastrprint(arg); return obj; }

char httpContent[200]; // buffer array for data recieve over serial port
unsigned int contentLength = 0; // MAX= 32,767 (2^15 -1 )
unsigned int charsRead = 0; 
unsigned long lastRead;


void checkConnectedToWifiNetwork(void){
  if(!cc3000.checkConnected()){
    connectToWifiNetwork();
    printFreeMemory();
  }
}


void connectToWebSite(unsigned long loopCounter, unsigned long lastRequestMillis){
  /* Try connecting to the website.  Note: HTTP/1.1 protocol is used 
     to keep the server from closing the connection before all data is read.   */
  if(Serial) Serial.println(F("Connecting..."));
  printFreeMemory();
  www = cc3000.connectTCP(ip, WEBSITE_PORT);
//  Serial << "GET http://"  << WEBSITE << WEBPAGE << "\n";
  if (www.connected()) {
      if(Serial) Serial.println(F(" ...Connected."));
      printFreeMemory();
//    www << "GET " << WEBPAGE << " HTTP/1.1\r\nHost: " << WEBSITE << "\r\n\r\n";
    www << F("GET ") << WEBPAGE << F(" HTTP/1.1\r\nHost: ") << WEBSITE << F("\r\n\r\n");
    www.println();
  } else {
    if(Serial) Serial.println(F("Connection failed\n"));
    return;
  }
  setOnePixel(0, 0, 255, 0 ); //green
  contentLength = 0; // reset!
  charsRead = 0;
  if(Serial) Serial.println(F("Reading data..."));
  printFreeMemory();
  lastRead = millis();
  boolean doContinue = true ; 
  /* Read data until either the connection is closed, or the idle timeout is reached. */ 
  while (doContinue && www.connected() && (millis() - lastRead < IDLE_TIMEOUT_MS)) {
    while (doContinue && www.available()) {
      char c = www.read();
      charsRead++;
      if(c == NEWLINE){
        // printFreeMemory();
        String s;
        s = String(httpContent);
        if (s.startsWith(STATION_PFX)){
          processHttpContentString(s);
          // printFreeMemory();
          // pre-emptively close the stream to see if we can beat 10500ms (falls to ~680ms)
          // PROBLEM: When pre-emptively calling 'www.close()', Arduino stops responding after 1 hour 
          // www.close();
          doContinue = false ;
        }
        // reset httpContent to null char
        for(int j = 0; j < contentLength; j++){
          httpContent[j]='\0';  
        }
        contentLength=0;
      }else{
        httpContent[contentLength++] = c;
      }
      lastRead = millis();
    }
  }
  if( Serial && (millis() - lastRead) > IDLE_TIMEOUT_MS ){
    Serial.println(F("lastRead timed out"));
      // printFreeMemory();
  }
  // printFreeMemory();
  Serial.println(F("\nclosing..."));
  // printFreeMemory();
  www.close();
  Serial.println(F(" ...closed."));
  // printFreeMemory();
//  Serial << "\nDone Reading data." << DASHES;
}


void doWebClientTest(unsigned long loopCounter, unsigned long lastRequestMillis){
  if ((loopCounter % 20) == 0){
    ip = 0;
    if(Serial) Serial.println(F("Try looking up the website's IP address by Host Name"));
  }
//  Serial << WEBSITE << " -> ";
  while (ip == 0) {
    if (! cc3000.getHostByName(WEBSITE, &ip)) {
      if(Serial) Serial.println(F("Couldn't resolve!"));
    }
    delay(500);
  }
  //  cc3000.printIPdotsRev(ip);

  connectToWebSite(loopCounter, lastRequestMillis);
}


void disconnectFromWifiNetwork(void){
  /* You need to make sure to clean up after yourself or the CC3000 can freak out */
  /* the next time your try to connect ... */
  if(Serial) Serial.println(F("disconnect"));
  cc3000.disconnect();
}


void connectToWifiNetwork(void){
  if(Serial) Serial.println(F("connectToWifiNetwork"));
  // printFreeMemory();
  char *ssid = WLAN_SSID;             /* Max 32 chars */
//  Serial << "\nAttempting to connect to " << ssid << "... ";
  unsigned long time1 = millis();
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY)) {
    while(1);
  }
  while (!cc3000.checkDHCP())
  {
    delay(100); // ToDo: Insert a DHCP timeout!
  }  
  if(Serial) Serial << F("Connected! (") << ( millis() - time1 ) << ("ms)\n");
  // printFreeMemory();

}


void initializeWifi(void){
  if(Serial) Serial.println(F("\nInitialising the CC3000 ... "));
  if (!cc3000.begin())
  // 'begin(boolean,boolean)' is for re-using the stored Wifi config.
//  if (!cc3000.begin(false, true))
  {
    while(1);
  }
  if(Serial) Serial.println(F(" ...done.\n"));
}


void setStaticIpAddress(void){
  uint32_t ipAddress = cc3000.IP2U32(192, 168, 1, 19);
  uint32_t netMask = cc3000.IP2U32(255, 255, 255, 0);
  uint32_t defaultGateway = cc3000.IP2U32(192, 168, 1, 1);
  uint32_t dns = cc3000.IP2U32(8, 8, 4, 4);
  if (!cc3000.setStaticIPAddress(ipAddress, netMask, defaultGateway, dns)) {
    while(1);
  }
//  Serial << "\nSet Static IP Address to 192.168.1.19";
}


