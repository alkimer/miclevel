package com.chimenea.devices;
/*
 * Created on October 2, 2010
 *
 * This helps us deal with operations concerning input sound devices
 * Special Thanks for Dick Baldwins excellent turorials on Java Sound.
 * http://www.developer.com/java/other/article.php/1579071/Java-Sound-Getting-Started-Part-2-Capture-Using-Specified-Mixer.htm
 *
 * Also thanks to this article that has helpful information about determining port capabilities:
 * http://www.vsj.co.uk/java/display.asp?id=370
 *
 */
 
import java.io.*;
import javax.sound.sampled.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashSet;

// Custom events
import com.chimenea.events.CustomPresenceEvent;
import com.chimenea.events.CustomPresenceListener;
import javax.swing.event.EventListenerList;

 // Config File
import com.technogumbo.XMLFileLoader;

public class SoundInputDeviceControl {
	
	// Save the array of Mixers for when the user
	// is trying to determine which one they  want to
	// use for recording
	private Mixer.Info[] mixerInfo;
	
	// We need all these to help us determine capabilities of lines
	private Line.Info sourceDLInfo = new Line.Info(SourceDataLine.class);
	private Line.Info targetDLInfo = new Line.Info(TargetDataLine.class);
	private Line.Info clipInfo = new Line.Info(Clip.class);
	private Line.Info portInfo = new Line.Info(Port.class);

	// For opening data lines
	TargetDataLine targetRecordLine;
	Port targetRecordPort;
	
	// For controlling the inner classes thread
	boolean stopCapture = false;
	boolean threadEnded = true;
	
	// --- Custom Events
	 protected EventListenerList listenerList = new EventListenerList();
	 
	// CONFIG FILE
	private  XMLFileLoader configLoader;
	
	// Saves our valid list of recording devices
	private List<String> SavedValidRecordingDevList;
	private int SavedMixerUIIndex = 0;
	
    public SoundInputDeviceControl() {
		// Load the xml config file
		createConfigurationLoader();
		configLoader.Load_Configuration_File();
    }
	
	public void listenForPresenceEvents(CustomPresenceListener _listener) {
		listenerList.add(CustomPresenceListener.class, _listener);
	}
	
	public void removeEventListener(CustomPresenceListener _listener) {
		listenerList.remove(CustomPresenceListener.class, _listener);
	}
	
	private void sendOutPresenceUpdates(int _FullJIDAndResource, String _NewPresence) {
		Object[] listeners = listenerList.getListenerList(); 
		
		Integer inputValue = _FullJIDAndResource;
		String convertedValue = inputValue.toString();
		
		// Empty out the listener list
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i < listeners.length; i+=2) { 
			if (listeners[i]==CustomPresenceListener.class) { 
				((CustomPresenceListener)listeners[i+1]).presenceEventOccurred(new CustomPresenceEvent(this, convertedValue, _NewPresence));
			} 
		} 
	}
	
	public int getConfigFileDeviceIndex() {
		int returnValue = 0;
		if(configLoader != null) {
			returnValue = Integer.parseInt(configLoader.getMixerPortIndex());
			SavedMixerUIIndex = returnValue;
		}
		
		return returnValue;
	}
	
	public void SaveConfigAndExit() {
		// Get the index of the current mixer
		if(configLoader != null) {
			configLoader.insertMixerPortIndex(SavedMixerUIIndex);
			configLoader.SaveConfigurationFile();
		}
	}
	
	private void DetermineConfigFileIndexOfMixer(String _MixerName) {
		
		int returnIndex = 0;
		int counter = 0;
		boolean found = false;
		
		Iterator it = SavedValidRecordingDevList.iterator();
		
        while(it.hasNext() && found == false)
        {
          String value=(String)it.next();
	      if(value.equals(_MixerName)) {
			  found = true;
			  returnIndex = counter;
		  }

		  counter += 1;
        }

		SavedMixerUIIndex = returnIndex;
	}
	
	public List<String> ListAudioInputDevices() {
		  List<String> returnList = new ArrayList<String>();
		  
		  mixerInfo = AudioSystem.getMixerInfo();
		  System.out.println("Available mixers:");
		  for(int cnt = 0; cnt < mixerInfo.length; cnt++) {
			Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
			
			// Because this is for a recording application, we only care about audio INPUT so we just
			// care if TargetDataLine is supported.
			// currentMixer.isLineSupported(targetDLInfo) && currentMixer.isLineSupported(portInfo)
			if( currentMixer.isLineSupported(targetDLInfo) ) {
				//if( currentMixer.isLineSupported(portInfo) ) {
					System.out.println("mixer name: " + mixerInfo[cnt].getName() + " index:" + cnt);
					returnList.add( mixerInfo[cnt].getName() );
				//}
			}
		  }

		  // Save the valid list of mixers
		  SavedValidRecordingDevList = returnList;
		  // Automatically start listening on the device read for the config file
		  // or default to the first item
		  if(returnList.size() >= Integer.parseInt(configLoader.getMixerPortIndex()) ) {
			StartMonitoringLevelsOnMixer( returnList.get( Integer.parseInt(configLoader.getMixerPortIndex()) ) );
		  } else {
			StartMonitoringLevelsOnMixer( returnList.get(0) );
		  }
		  

		  return returnList;
	}
	
	public void StartMonitoringLevelsOnMixer(String _MixerName) {
		
		DetermineConfigFileIndexOfMixer(_MixerName);
		
		// Locate the target mixer
		  for(int cnt = 0; cnt < mixerInfo.length; cnt++) {
			
			  // Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
			
			// Because this is for a recording application, we only care about audio INPUT so we just
			// care if TargetDataLine is supported.
			if( mixerInfo[cnt].getName().equals(_MixerName) ) {
            System.out.println("SoundInputDeviceControl - found target mixer: " + _MixerName);
				
				// This may freeze the UI but oh well, we have to wait for the inner thread to stop from the
				// previous selection
				stopCapture = true;
				while( threadEnded == false ) {
					//this.sleep(100);
				}

				Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
				
				/*
				HashSet<AudioFormat> inputFormats = new HashSet<AudioFormat>();
				HashSet<AudioFormat> outputFormats = new HashSet<AudioFormat>();
				 
				// Collect the input formats
				for (Line.Info lineInfo : currentMixer.getSourceLineInfo()) {
					for (AudioFormat newFormat : ((DataLine.Info)lineInfo).getFormats())
						inputFormats.add(newFormat);
				}
				 
				// Collect the output formats
				for (Line.Info lineInfo : currentMixer.getTargetLineInfo()) {
					for (AudioFormat newFormat : ((DataLine.Info)lineInfo).getFormats())
						outputFormats.add(newFormat);
				}
				
				// List all supported I/O types for the target mixer
				System.out.println("------------ Supported Input Formats --------------");
				Iterator iterator = inputFormats.iterator();
				while (iterator.hasNext()){
					System.out.println(iterator.next() + " ");  
				}
				
				// List all supported I/O types for the target mixer
				System.out.println("------------ Supported Output Formats --------------");
				iterator = outputFormats.iterator();
				while (iterator.hasNext()){
					System.out.println(iterator.next() + " ");  
				}
				*/
				
				try {
					targetRecordLine = (TargetDataLine)currentMixer.getLine(targetDLInfo);
					targetRecordLine.open( getAudioFormat() );
					
					// Get and store this lines control for gain
					/*
					try {
						ArrayList<Control> ctrls = new ArrayList<Control>( Arrays.asList(targetRecordLine.getControls()));
						System.out.println("Number of available controls: " + ctrls.size());
						for (Control ctrl: ctrls) {
								System.out.println( ctrl.toString());
								if (ctrl instanceof CompoundControl) {
									CompoundControl cc = ((CompoundControl) ctrl);
									ArrayList<Control> ictrls = new ArrayList<Control>(Arrays.asList(cc.getMemberControls()));
									for(Control ictrl : ictrls)
										System.out.println( ictrl.toString());
									} // of if (ctrl instanceof)
								} // of for(Control ctrl)

						
						//targetRecordLineGainControl = (FloatControl)(Line)targetRecordLine.getControl( FloatControl.Type.MASTER_GAIN );
					} catch (IllegalArgumentException e) {
						System.out.println("SoundInputDeviceControl - StartMonitorlingLevelsOnMixer - getting gain control: " + e);
					}
					*/
					
					targetRecordLine.start();
					Thread captureThread = new CaptureThread();
					captureThread.start();
				} catch (LineUnavailableException e) {
					System.out.println("SoundInputDeviceControl - StartMonitorlingLevelsOnMixer -" + e);
				}
				
				//DataLine.Info dataLineInfo = new DataLine.Info( TargetDataLine.class, getAudioFormat() );
				
				//targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
			}
		  }
	}
	
	  // This method creates and returns an AudioFormat object for a given set of format parameters.
	  // In order to do a direct conversion for the audio bytes for displaying sound level we need
	  // http://forums.sun.com/thread.jspa?threadID=5433582
	  // signed 8-bit big-endian linear PCM encoding
	  private AudioFormat getAudioFormat(){
		float sampleRate = 8000.0F;
		//8000,11025,16000,22050,44100
		int sampleSizeInBits = 8;
		//8,16
		int channels = 1;
		//1,2
		boolean signed = true;
		//true,false
		boolean bigEndian = true;
		//true,false
		return new AudioFormat(
						  sampleRate,
						  sampleSizeInBits,
						  channels,
						  signed,
						  bigEndian);
	  }//end getAudioFormat
	
	  
	// NAME: createConfigurationLoader
	// DATE: 2/2/2010
	// AUTHOR: Charles Palen (palen1c@gmail.com)
	// DESCRIPTION:
	// Creates an instance of the configuration loader
	private void createConfigurationLoader() {
		if(configLoader == null) {
			configLoader = new XMLFileLoader("configuration" + File.separatorChar + "ClientConfiguration.xml");
		} else {
			destroyConfigurationLoader(true);
		}
	}
	
	// NAME: destroyConfigurationLoader
	// DATE: 12/04/2010
	// AUTHOR: Charles Palen (palen1c@gmail.com)
	// DESCRIPTION:
	// Destroys an instance of the configuration loader
	private void destroyConfigurationLoader(boolean _OptionalCallback) {
		if(configLoader != null) {
		
			configLoader.destroyInternals();
			configLoader = null;
			
			if(_OptionalCallback == true) {
				createConfigurationLoader();
			}
		}
	}
	
	  // Calculate the level of the audio
	  // http://forums.sun.com/thread.jspa?threadID=5433582
	  //
	  private int calculateRMSLevel(byte[] audioData)
	  { 
		// audioData might be buffered data read from a data line
        long lSum = 0;
        for(int i=0; i<audioData.length; i++)
            lSum = lSum + audioData[i];
 
        double dAvg = lSum / audioData.length;
 
        double sumMeanSquare = 0d;
        for(int j=0; j<audioData.length; j++)
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);
 
        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
     }

	
	//Inner class to capture data from the selected input mixer
	class CaptureThread extends Thread{

	  //An arbitrary-size temporary holding buffer
	  byte tempBuffer[] = new byte[500];
	  public void run(){
System.out.println("SoundInputDeviceControl - Starting thread");
		threadEnded = false;
		stopCapture = false;
		try{
		  while(!stopCapture){
			//Read data from the internal buffer of
			// the data line.
			int cnt = targetRecordLine.read(tempBuffer, 0, tempBuffer.length);
			if(cnt > 0){
				sendOutPresenceUpdates(calculateRMSLevel(tempBuffer), "");
			}//end if
		  }//end while

		  targetRecordLine.close();
		  threadEnded = true;
		}catch (Exception e) {
		  System.out.println(e);
		  System.exit(0);
		}//end catch
	  }//end run
	}//end inner class CaptureThread

	
}
