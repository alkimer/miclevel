/*
 * XMLFileLoader.java
 *
 * Created on Feb 2, 2010
 *
 * This class loads configuration parameters from an xml file and closes the xml file.
 */
package com.technogumbo;

// For loading and dealing with xml
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

 import java.io.File;
 import java.io.FileWriter;
 import java.io.BufferedWriter;
 import java.io.IOException;
 
/**
 *
 * @author Charles Palen
 */

public class XMLFileLoader{
    
	 // DATAMEMBERS
		
		// The relative path to the configuration files location inside Red5
		private String CONFIGPATH = "configuration\\ClientConfiguration.xml";
		
		//////////////////////////
		private String FFMPEGFULLPATH = "D:\\ffmpeg_dynamic_vhook\\ffmpeg.exe";
		private String WATERMARKFULLPATH = "D:\\ffmpeg_dynamic_vhook\\vhook\\watermark.dll";
		// The relative path from the Red5 root to the applications "sreams" directory
		private String RELATIVEVIDPATH = "\\dist\\webapps\\interactive_webcam\\streams\\";
		private String LOCALPORTLIST = "11000,11001";
		private String VALUE5 = "";
		private String VALUE6 = "1000";
		private String VALUE7 = "2000";
		private String VALUE8 = "300";
		private String VALUE9 = "4";
		
	    /** Constructor - Need the path to the config file! */
	    public XMLFileLoader(String _configFilePath) {
			CONFIGPATH = _configFilePath;
	    }
	      
		public String getJServer() {
			return FFMPEGFULLPATH;
		}
		
		public String getJID() {
			return WATERMARKFULLPATH;
		}
		
		public String getJPass() {
			return VALUE5;
		}
		
		public String getJResource() {
			return RELATIVEVIDPATH;
		}
		
		public String getPortList() {
			return LOCALPORTLIST;
		}
		
		public String getSensorRecordingRate() {
			return VALUE6;
		}
		
		public String getChallengeTime() {
			return VALUE7;
		}
		
		public String getSensorTollerance() {
			return VALUE8;
		}
		
		public String getMixerPortIndex() {
			return VALUE9;
		}
		
		public void insertMixerPortIndex(int _InputIndex) {
			VALUE9 = Integer.toString(_InputIndex);
		}
		
	// ## NAME: Load_Configuration_File
	// ## DATE: 2/2/2010
	// ## AUTHOR: Charles Palen
	// ## DESCRIPTION: 
	// ##
	// ## PRECONDITIONS: Need to have the configuration file in the location that this will try and load it from
	// ## POSTCONDITIONS: If successfully, the following global variables will be updated:
	// ## 1. FFMPEGFULLPATH
	// ## 2. WATERMARKFULLPATH
	// ## 3. RELATIVEVIDPATH
       public void Load_Configuration_File() {
			
				try {
		
					Parse_XML();
					System.out.println("Done Parsing  - Device index is:" + VALUE9);
				} catch (Exception e) {
					System.out.println("LoadConfigFile Load_Configuration_File- " + e);
				}
	   }
	 
	// ## NAME: Parse_XML
	// ## DATE: 1/15/2010
	// ## AUTHOR: Charles Palen
	// ## DESCRIPTION: Attempts to load and parse the xml file
	// ##
	// ## REFERENCE: http://www.developerfusion.com/code/2064/a-simple-way-to-read-an-xml-file-in-java/
	// ## PRECONDITIONS: 
	// ## POSTCONDITIONS: 
       private void Parse_XML() {
			
			try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(CONFIGPATH));

            // normalize text representation
            doc.getDocumentElement ().normalize ();

			// This will only be one item
            NodeList ConfigList = doc.getElementsByTagName("config");
            int totalConfig = ConfigList.getLength();

            for(int s=0; s < ConfigList.getLength() ; s++) {

                Node firstConfigNode = ConfigList.item(s);
                if(firstConfigNode.getNodeType() == Node.ELEMENT_NODE){


                    Element firstConfigElement = (Element)firstConfigNode;

                    //------- ffmpeg System path
                    NodeList ffSysPathList = firstConfigElement.getElementsByTagName("jServer");
                    Element ffSysPathElement = (Element)ffSysPathList.item(0);

                    NodeList textFFSysPath = ffSysPathElement.getChildNodes();
					FFMPEGFULLPATH = ((Node)textFFSysPath.item(0)).getNodeValue().trim();
					
                    //------- watermark System Path
                    NodeList wmSysPathList = firstConfigElement.getElementsByTagName("jid");
                    Element wmElement = (Element)wmSysPathList.item(0);

                    NodeList textWM = wmElement.getChildNodes();
                    WATERMARKFULLPATH = ((Node)textWM.item(0)).getNodeValue().trim();

                    //---- streams Relative Path
                    NodeList streamsList = firstConfigElement.getElementsByTagName("jResource");
                    Element streamsElement = (Element)streamsList.item(0);

                    NodeList textStreamsList = streamsElement.getChildNodes();
					RELATIVEVIDPATH = ((Node)textStreamsList.item(0)).getNodeValue().trim();
					
					//---- a comma delimited list of local ports we listen for socket connections on
                    NodeList pList = firstConfigElement.getElementsByTagName("localPortList");
                    Element pElement = (Element)pList.item(0);

                    NodeList pDetailList = pElement.getChildNodes();
					LOCALPORTLIST = ((Node)pDetailList.item(0)).getNodeValue().trim();
					
					//---- 
                    NodeList jList = firstConfigElement.getElementsByTagName("jSecret");
                    Element jElement = (Element)jList.item(0);

                    NodeList jDetailList = jElement.getChildNodes();
					VALUE5 = ((Node)jDetailList.item(0)).getNodeValue().trim();
					
					//---- 
                    NodeList kList = firstConfigElement.getElementsByTagName("sensorRecordRateInMS");
                    Element kElement = (Element)kList.item(0);

                    NodeList kDetailList = kElement.getChildNodes();
					VALUE6 = ((Node)kDetailList.item(0)).getNodeValue().trim();
					
					//---- 
                    NodeList lList = firstConfigElement.getElementsByTagName("challengeTimeAllowedInMS");
                    Element lElement = (Element)lList.item(0);

                    NodeList lDetailList = lElement.getChildNodes();
					VALUE7 = ((Node)lDetailList.item(0)).getNodeValue().trim();
					
					//---- 
                    NodeList mList = firstConfigElement.getElementsByTagName("challengeSensorTollerance");
                    Element mElement = (Element)mList.item(0);

                    NodeList mDetailList = mElement.getChildNodes();
					VALUE8 = ((Node)mDetailList.item(0)).getNodeValue().trim();
					
					//---- 
                    NodeList nList = firstConfigElement.getElementsByTagName("recordDeviceIndex");
                    Element nElement = (Element)nList.item(0);

                    NodeList nDetailList = nElement.getChildNodes();
					VALUE9 = ((Node)nDetailList.item(0)).getNodeValue().trim();
					
                }//end of if clause
            }//end of for loop with s var


        }catch (SAXParseException err) {
			System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
			System.out.println(" " + err.getMessage ());
        }catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();
        }catch (Throwable t) {
			t.printStackTrace ();
        }
				
	   }
	   
	// NAME: SaveConfigurationFile
	// DATE: 10/4/2010
	// AUTHOR: Charles Palen (palen1c@gmail.com)
	// DESCRIPTION:
	// Saves the configuration file back to itself if possible - Trying to make something like this:
	/*
		<config>
		<jServer><![CDATA[192.168.0.1]]></jServer>
		<jid><![CDATA[Client2]]></jid>
		<jSecret><![CDATA[roboticmess]]></jSecret>
		<jResource><![CDATA[spark]]></jResource>
		<localPortList><![CDATA[11000,11001]]></localPortList>
		<sensorRecordRateInMS><![CDATA[250]]></sensorRecordRateInMS>
		<challengeTimeAllowedInMS><![CDATA[2000]]></challengeTimeAllowedInMS>
		<challengeSensorTollerance><![CDATA[400]]></challengeSensorTollerance>
		<recordDeviceIndex><![CDATA[4]]></recordDeviceIndex>
		</config>
	*/
	public void SaveConfigurationFile() {
		try {
			
			File outputFileHandle = new File(CONFIGPATH);
			if( outputFileHandle.exists() ) {
				BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFileHandle));
				outputWriter.write("<config>\r\n");
				outputWriter.write("<jServer><![CDATA[" + FFMPEGFULLPATH + "]]></jServer>\r\n");
				outputWriter.write("<jid><![CDATA[" + WATERMARKFULLPATH + "]]></jid>\r\n");
				outputWriter.write("<jSecret><![CDATA[" + VALUE5 + "]]></jSecret>\r\n");
				outputWriter.write("<jResource><![CDATA[" + RELATIVEVIDPATH + "]]></jResource>\r\n");
				outputWriter.write("<localPortList><![CDATA[" + LOCALPORTLIST + "]]></localPortList>\r\n");
				outputWriter.write("<sensorRecordRateInMS><![CDATA[" + VALUE6 + "]]></sensorRecordRateInMS>\r\n");
				outputWriter.write("<challengeTimeAllowedInMS><![CDATA[" + VALUE7 + "]]></challengeTimeAllowedInMS>\r\n");
				outputWriter.write("<challengeSensorTollerance><![CDATA[" + VALUE8 + "]]></challengeSensorTollerance>\r\n");
				outputWriter.write("<recordDeviceIndex><![CDATA[" + VALUE9 + "]]></recordDeviceIndex>\r\n");
				outputWriter.write("</config>\r\n");
				outputWriter.close();
				outputWriter = null;
				outputFileHandle = null;
			}
			
			// Tell the java runtime to exit with a normal code: 0
			System.exit(0);
				
		} catch (IOException e) {
			System.out.println("XMLFileLoader - SaveConfigurationFile - IOException: " + e);
		}
	}
	
	// ## NAME: destroyInternals
	// ## DATE: 1/14/2010
	// ## AUTHOR: Charles Palen
	// ## DESCRIPTION: Gets rid of/ends everything associated with this class so that it can be released from memory
	// ##
	// ## PRECONDITIONS: 
	// ## POSTCONDITIONS: This class should be all set to null externally
       public void destroyInternals() {
			// Maybe need to stop the ffmpeg process, but not sure how to do that.
	   }
}