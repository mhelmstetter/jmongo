package com.mongodb.util;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class RandomUtils {
    
    Random rand = new Random();

    public static int getRandomInt(int min, int max) {
        int ret;
        ret = (int)(Math.random() * (max - min + 1));
        ret += min;
        return ret;
    }
    
    public static String getRandomNumeric(int length) {
    	StringBuilder sb = new StringBuilder();
    	Random r = new Random();
    	for (int i = 0; i < length; i++) {
    		sb.append(r.nextInt(9));
    	}
    	return sb.toString();
    }
    
    public long getRandomLong() {
        return rand.nextLong();
    }
 
    public static Object getRandomElement(List list) {
        int index = getRandomInt(0, list.size()-1);
        return list.get(index);
    }

    public static double getRandomDouble(int min, int max) {
        return (double) Math.random() * max + min;
    }

    public static double getRandomDouble(double min, double max) {
        return (double) Math.random() * max + min;
    }

    public static Calendar getRandomDate(int daysFromCurrentMin, int daysFromCurrentMax) {
        Calendar c = Calendar.getInstance();
        int daysToAdd = getRandomInt(daysFromCurrentMin, daysFromCurrentMax);
        c.add(Calendar.DATE, daysToAdd);
        c.set(Calendar.HOUR, getRandomInt(0, 23));
        return c;
    }

    public static char getRandomChar() {
        return (char)getRandomInt(65, 90);
    }

    public static boolean getRandomBoolean() {
        int randomBit = getRandomInt(0, 10) % 2;
        return (randomBit == 0);
    }

    public static String getRandomString(int stringLength) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stringLength; i++) {
            sb.append(getRandomChar());
        }
        return sb.toString();
    }

    public static String getRandomString(String format, int stringLength) {
        return MessageFormat.format(format, new Object[] {getRandomString(stringLength)});
    }

    public static Object randomType(Class class1) throws Exception {
        Method valuesMethod = class1.getMethod("values", new Class[] {});
        Object values = valuesMethod.invoke(class1, new Object[] {});
        Object[] valuesArray = (Object[]) values;
        int i = getRandomInt(0, valuesArray.length);
        return valuesArray[i];
    }
    
    public void getRandomLatitude() {
    	double latitude = (Math.random()*180);
//    	if(latitude > 90)
//			lat = (int)(latitude-90) + "\u00B0 "+ (int)((latitude-90)*60)%60+"' N";
//		else
//			lat = (int)(-(latitude-90)) + "\u00B0 "+ (int)((latitude-90)*-60)%60+"' S";
    }
    
    public String quotedList(Iterable<Object> list) {
        if (list == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Iterator<Object> i = list.iterator(); i.hasNext();) {
            Object obj = i.next();
            sb.append("\"");
            sb.append(obj);
            sb.append("\"");
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    public String quotedList(Object[] list) {
        if (list == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Object obj : list) {
            sb.append("\"");
            sb.append(obj);
            sb.append("\"");
            if (i < list.length-1) {
                sb.append(",");
            }
            i++;
        }
        return sb.toString();
    }

}
