/*
 * This module contains all LED-related functions.
 * 
 * https://github.com/adafruit/Adafruit-WS2801-Library/blob/master/examples/strandtest/strandtest.pde
 */
 
#include <Adafruit_WS2801.h>
#include <stdlib.h>
//#include <SPI.h> // included in jjz_tsaca_wifi
#ifdef __AVR_ATtiny85__
 #include <avr/power.h>
#endif

uint8_t dataPin  = 6;    // Yellow wire on Adafruit Pixels
uint8_t clockPin = 7;    // Green wire on Adafruit Pixels

// Don't forget to connect the ground wire to Arduino ground,
// and the +5V wire to a +5V supply

// Set the first variable to the NUMBER of pixels. 25 = 25 pixels in a row
Adafruit_WS2801 strip = Adafruit_WS2801(25, dataPin, clockPin);

// Optional: leave off pin numbers to use hardware SPI
// (pinout is then specific to each board and can't be changed)
//Adafruit_WS2801 strip = Adafruit_WS2801(25);

// For 36mm LED pixels: these pixels internally represent color in a
// different format.  Either of the above constructors can accept an
// optional extra parameter: WS2801_RGB is 'conventional' RGB order
// WS2801_GRB is the GRB order required by the 36mm pixels.  Other
// than this parameter, your code does not need to do anything different;
// the library will handle the format change.  Examples:
//Adafruit_WS2801 strip = Adafruit_WS2801(25, dataPin, clockPin, WS2801_GRB);
//Adafruit_WS2801 strip = Adafruit_WS2801(25, WS2801_GRB);


void ledSetup() {
#if defined(__AVR_ATtiny85__) && (F_CPU == 16000000L)
  clock_prescale_set(clock_div_1); // Enable 16 MHz on Trinket
#endif

  strip.begin();

  // Update LED contents, to start they are all 'off'
  strip.show();
}


// Helper functions

void setOnePixel(int pixel, byte r, byte g, byte b){
  strip.setPixelColor(pixel, Color(r,g,b) );
  strip.show();
}

void setOnePixelNoShow(int pixel, int r, int g, int b){
  strip.setPixelColor(pixel, Color(r, g, b) );
}


long numberr ;
int rr, gg, bb;
void setOnePixelNoShowHexString(int pixel, String hexstring){
  numberr = strtol( &hexstring[0], NULL, 16); // http://stackoverflow.com/a/23577019/237225
  rr = numberr >> 16;
  gg = numberr >> 8 & 0xFF;
  bb = numberr & 0xFF;

// Serial << "\nset pixel " << pixel << " to " << hexstring << "  " << numberr;
  
  strip.setPixelColor(pixel, Color( rr, gg, bb) );
}


void showPixels(void){
  strip.show();
}


// Create a 24 bit color value from R,G,B
uint32_t Color(byte r, byte g, byte b)
{
  uint32_t c;
  c = r;
  c <<= 8;
  c |= g;
  c <<= 8;
  c |= b;
  return c;
}


