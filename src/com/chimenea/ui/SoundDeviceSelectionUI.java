package com.chimenea.ui;
/*
 * Created on October 2, 2010
 *
 * Creates a java UI that we can use to select a sound device
 *
 */
 

import java.util.List;
import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// Custom events
import com.chimenea.events.CustomPresenceEvent;
import com.chimenea.events.CustomPresenceListener;
import javax.swing.event.EventListenerList;
 
public class SoundDeviceSelectionUI {
	
	private JFrame frame;
	private JLabel lblSelect, lblGain;
	private JComboBox deviceList;
	private JButton btnSave, btnCancel;
	private JPanel panel1, panel2, soundLevelVis, gainSettingsContainer, gainExample;
	private JSlider gainSlider;
	private Container cp;
	
	private List<String> soundDevices;
	private int initialDeviceIndex = 0;
	private boolean Saving = false;
	private boolean readyForUIUpdates = false;
	
		 // --- Custom Events
	 protected EventListenerList listenerList = new EventListenerList();
	 
    public SoundDeviceSelectionUI(List<String> _InputSoundDevices, int _ConfigFileInitialIndex) {
		soundDevices = _InputSoundDevices;
		initialDeviceIndex = _ConfigFileInitialIndex;
		
		setupUI();
    }
	
	public void listenForPresenceEvents(CustomPresenceListener _listener) {
		listenerList.add(CustomPresenceListener.class, _listener);
	}
	
	public void removeEventListener(CustomPresenceListener _listener) {
		listenerList.remove(CustomPresenceListener.class, _listener);
	}
	
	private void sendOutPresenceUpdates(String _FullJIDAndResource, String _NewPresence) {
		Object[] listeners = listenerList.getListenerList(); 
		
		// Empty out the listener list
		// Each listener occupies two elements - the first is the listener class 
		// and the second is the listener instance 
		for (int i=0; i < listeners.length; i+=2) { 
			if (listeners[i]==CustomPresenceListener.class) { 
				((CustomPresenceListener)listeners[i+1]).presenceEventOccurred(new CustomPresenceEvent(this, _FullJIDAndResource, _NewPresence));
			} 
		} 
	}
	
	private void setupUI() {
		
		frame = new JFrame("Select Recording Device");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(500, 300));
		lblSelect = new JLabel("Select your recording device:");
		lblGain = new JLabel("Make sure a red line shows up below indicating the presense of sound:");
		
		deviceList = new JComboBox( soundDevices.toArray() );
		// Set the last index selected in the config file
		if(initialDeviceIndex <= soundDevices.size() ) { 
			deviceList.setSelectedIndex(initialDeviceIndex);
		}
		
		deviceList.addActionListener(new ActionListener() {
               
            public void actionPerformed(ActionEvent e)
            {
				JComboBox cb = (JComboBox)e.getSource();
				String deviceName = (String)cb.getSelectedItem();

                //Execute when button is pressed
                //System.out.println("You changed the drop down list to: " + deviceName);
				
				// Dispatch an event to the parent
				sendOutPresenceUpdates(deviceName,"");
            }
        });  
		
		
		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
               
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                System.out.println("You clicked the save button");
				// Dispatch an event to the parent to save the config file
				if( Saving == false) {
					Saving = true;
					sendOutPresenceUpdates("Save","Save");
				}
            }
        });  
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
               
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                System.out.println("You clicked the cancel button");
				// Tell the java runtime to exit with a normal code: 0
				System.exit(0);
            }
        });  
		
		/*
		gainSlider = new JSlider(JSlider.HORIZONTAL,0,100,50);
		gainSlider.addChangeListener(new ChangeListener() {
               
            public void stateChanged(ChangeEvent e)
            {
				JSlider source = (JSlider)e.getSource();
                //Execute when button is pressed
                System.out.println("You changed the slider to: " + (int)source.getValue() );
				updateSoundLevel( (int)source.getValue()  );
            }
        });
		*/
		
		panel1 = new JPanel();
		panel2 = new JPanel();
		soundLevelVis = new JPanel();
		//soundLevelVis.setBackground(Color.WHITE);
		gainSettingsContainer = new JPanel();
		gainExample = new JPanel();
		gainExample.setBackground(Color.RED);
		
		panel1.add(lblSelect);
		panel1.add(deviceList);
		panel2.add(btnSave);
		panel2.add(btnCancel);
		gainSettingsContainer.add(lblGain);
		gainSettingsContainer.add(gainExample);
		
		cp = frame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		
		cp.add(panel1);
		cp.add(gainSettingsContainer);
		cp.add(soundLevelVis);
		cp.add(panel2);
	}
	
	// _InputLevel will be 0 - ? 0 for nothing
	public void updateSoundLevel(int inputLevel) {
    	System.out.println("Input level: " + inputLevel);
		if(readyForUIUpdates == true) {
			int finalHeight = inputLevel * 2;
			Graphics g = soundLevelVis.getGraphics();

			g.setColor(setBarColor(inputLevel));

			// Clear any current graphics
			g.clearRect(0,0,soundLevelVis.getWidth(),soundLevelVis.getHeight());
			g.fillRect(soundLevelVis.getWidth() / 2,0,finalHeight,50);
		}
		//soundLevelVis.paintComponent(g);
	}
	
	public void display()
	{
		frame.pack();
		frame.setVisible(true);
		readyForUIUpdates = true;
	}

	private Color setBarColor(int inputLevel) {
		if (inputLevel < 30) {
			return Color.green;
		} else { return Color.red;}

	}
}
