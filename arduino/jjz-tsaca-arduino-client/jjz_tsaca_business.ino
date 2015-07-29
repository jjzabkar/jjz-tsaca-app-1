/*
 * This module contains all business logic.
 * 
 */

/*
 * 
 * STATION,000000,000000,000000,000000,000000,000000,000000,000000,ff00ff,00ff00,00ff00,00ff00,00ff00,dada00,ff0000,ff00ff,dada00,ff0000,ff00ff,00ff00,ff0000,000000,000000,000000,000000,
 */


int commaIndex = -1;
int nextCommaIndex = -1;
String hex ;
int fieldCounter = 0;

void processHttpContentString(String s){
  if (s.startsWith("STATION")){
    Serial << "\n  STATION='" << s << "'";
    commaIndex = s.indexOf(',');
    nextCommaIndex = -1;
    fieldCounter = -1;
    while(commaIndex > 0 ){
      Serial << "\n  nextCommaIndex="<< commaIndex << "  fieldCounter=" << fieldCounter;
      nextCommaIndex = s.indexOf(',', nextCommaIndex + 1);
      if( nextCommaIndex > commaIndex ){
        hex = s.substring(commaIndex + 1,nextCommaIndex);
        Serial << "\n  hex=" << hex;
      } else{
        Serial << "\n  commaIndex <= nextCommaIndex (" << commaIndex << "<=" << nextCommaIndex << ")";
      }
      commaIndex = nextCommaIndex;
      fieldCounter++;
    }

  }
}

