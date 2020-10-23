package com.chimenea.main;

import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;

import java.io.IOException;
import java.io.File;

import java.util.Random;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Sound device control
import com.chimenea.devices.SoundInputDeviceControl;
import java.util.List;
import java.util.ArrayList;

// UI
import com.chimenea.ui.SoundDeviceSelectionUI;

// Custom events
import com.chimenea.events.CustomPresenceEvent;
import com.chimenea.events.CustomPresenceListener;

class bootstrap {

    // SOUND DEVICE
    private static SoundInputDeviceControl sDeviceControl;
    // UI
    private static SoundDeviceSelectionUI UI;

    public static void main(String args[]) {

        // Have java set the look and feel of the UI to be like the native systems
        // so people dont get wierded out by the java UI scheeme.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println("bootstrap - Main - UnsupportedLookAndFeelException - " + ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        createSoundDeviceControl();
        sDeviceControl.listenForPresenceEvents(handleAudioInputLevelReading);
        List<String> soundDevices = sDeviceControl.ListAudioInputDevices();
        int CurrentConfigFileIndex = sDeviceControl.getConfigFileDeviceIndex();

        UI = new SoundDeviceSelectionUI(soundDevices, CurrentConfigFileIndex);
        UI.listenForPresenceEvents(handleCustomPresenceUpdate);
        UI.display();

    }

    // 10/2/2010
    // CDP
    //
    // For listening to events from the SoundDeviceSelection
    // The drop down box.
    // When the save button is pressed, this also is used to know we need to save the config file.
    static CustomPresenceListener handleCustomPresenceUpdate = new CustomPresenceListener() {
        public void presenceEventOccurred(CustomPresenceEvent evt) {

            if(sDeviceControl != null) {
                if( evt.getNewPresence().equals("Save") ) {
                    sDeviceControl.SaveConfigAndExit();
                } else {
                    sDeviceControl.StartMonitoringLevelsOnMixer( evt.getFullJIDAndResource() );
                }
            }
        }
    };

    // 10/3/2010
    // CDP
    //
    // For listening to events from the SoundInputDeviceControl
    // This changes the graphical indicator on the little red bar in the UI
    static CustomPresenceListener handleAudioInputLevelReading = new CustomPresenceListener() {
        public void presenceEventOccurred(CustomPresenceEvent evt) {

            if(UI != null) {
                UI.updateSoundLevel( Integer.parseInt(evt.getFullJIDAndResource() ) );
            }
        }
    };

    private static int randomFromRange(int _lowest, int _highest) {
        int ReturnValue = 0;

        // Get a psudo random number gen ready
        Random randNumber = new Random();

        long range = (long)_highest - (long)_lowest + 1;
        long fraction = (long)(range * randNumber.nextDouble());

        ReturnValue = (int)(fraction + _lowest);

        randNumber = null;

        return ReturnValue;
    }

    private static void createSoundDeviceControl() {
        if(sDeviceControl == null) {
            sDeviceControl = new SoundInputDeviceControl ();
        } else {
            destroySoundDeviceControl(true);
        }
    }

    private static void destroySoundDeviceControl(boolean _OptionalCallback) {
        if(sDeviceControl != null) {
            sDeviceControl = null;

            if(_OptionalCallback == true) {
                createSoundDeviceControl();
            }
        }
    }

}
