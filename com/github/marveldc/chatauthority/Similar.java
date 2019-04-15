package com.github.marveldc.chatauthority;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Similar {
        private static final float THRESHOLD = (float) 0.75;

        private final Logger logger = Logger.getLogger(Similar.class.getName());

        private String str;
        private Map <Character, Integer> strMap;

        public Similar(String str){
            this.str = str;
            this.strMap = this.generateCharMap(str);
        }

        @Override
        public String toString(){
            return this.str;
        }

        private Map<Character, Integer> generateCharMap(String str){
            Map <Character, Integer> map = new HashMap<>();
            Integer currentChar;
            for(char c: str.toCharArray()){
                currentChar = map.get(c);
                if(currentChar == null){
                    map.put(c, 1);
                } else {
                    map.put(c, currentChar+1);
                }
            }
            return map;
        }

        public String isSimilar(String compareStr) {
            Map<Character, Integer> compareStrMap = this.generateCharMap(compareStr);
            Set<Character> charSet = compareStrMap.keySet();
            int similarChars = 0;
            int totalStrChars = this.str.length();
            float thisThreshold;

            if (totalStrChars < compareStrMap.size()) {
                totalStrChars = compareStr.length();
            }

            Iterator it = charSet.iterator();
            char currentChar;
            Integer currentCountStrMap;
            Integer currentCountCompareStrMap;
            while (it.hasNext()) {
                currentChar = (Character) it.next();
                currentCountStrMap = strMap.get(currentChar);
                if (currentCountStrMap != null) {
                    currentCountCompareStrMap = compareStrMap.get(currentChar);
                    if (currentCountCompareStrMap >= currentCountStrMap) {
                        similarChars += currentCountStrMap;
                    } else {
                        similarChars += currentCountCompareStrMap;
                    }
                }
            }

            thisThreshold = ((float) similarChars) / ((float) totalStrChars);
            if (thisThreshold > THRESHOLD) {
                return "Yes";
            }
            return "No";
        }
}
