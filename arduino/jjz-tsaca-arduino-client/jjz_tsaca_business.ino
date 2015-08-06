/*
 * This module contains all business logic.
 * 
 * Example CSV response to parse:
 * STATION,000000,000000,000000,000000,000000,000000,000000,000000,ff00ff,00ff00,00ff00,00ff00,00ff00,dada00,ff0000,ff00ff,dada00,ff0000,ff00ff,00ff00,ff0000,000000,000000,000000,000000,
 */

#define STATION "STATION"
#define COMMA ","
int commaIndex = -1;
int nextCommaIndex = -1;
String hexstring ;
int fieldCounter = 0;

void processHttpContentString(String s){
  if (s.startsWith(STATION)){
    commaIndex = s.indexOf(COMMA);
    nextCommaIndex = -1;
    fieldCounter = -1;
    while(commaIndex > 0 ){
      nextCommaIndex = s.indexOf(COMMA, nextCommaIndex + 1);
      if( nextCommaIndex > commaIndex ){
        hexstring = s.substring(commaIndex + 1,nextCommaIndex);
        setOnePixelNoShowHexString(fieldCounter, hexstring);
      }
      commaIndex = nextCommaIndex;
      fieldCounter++;
    }
    showPixels();
  }
}
