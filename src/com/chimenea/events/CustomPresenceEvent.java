package com.chimenea.events;
/*
 * Created on 7 6, 2010
 * 
 * My second custom event in Java.  Referencing
 * http://www.exampledepot.com/egs/java.util/custevent.html
 */
 import java.util.EventObject;
 
public class CustomPresenceEvent extends EventObject {
	
	public String FullJIDAndResource = "";
	public String NewPresence = "";
    public CustomPresenceEvent(Object source, String _FullJIDAndResource, String _NewPresence) {
		super(source);
		FullJIDAndResource = _FullJIDAndResource;
		NewPresence = _NewPresence;
    }
	
	public String getFullJIDAndResource() {
		return FullJIDAndResource;
	}
	
	public String getNewPresence() {
		return NewPresence;
	}
}
