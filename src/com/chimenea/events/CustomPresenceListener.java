package com.chimenea.events;
/*
 * Created on 7/6/2010
 * 
 * My second custom event in Java.  Referencing
 * http://www.exampledepot.com/egs/java.util/custevent.html
 */
 import java.util.EventListener;
 
public interface CustomPresenceListener extends EventListener {
	
    public void presenceEventOccurred(CustomPresenceEvent evt);
}
