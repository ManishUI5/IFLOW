Package like 

1. Common Library 
2. File transfer Framework(Inbound file movement , JMS to ESB , Lookupservice ,sharedfiletransfer
3.Non SAP Integration 
4. SAP Integration 
5. Garunteed delivery 
6. MODULEwise/Systemwise package SAP EAM , SAPTOARIBA ex.


1. XSLT Mapping
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"> 
<xsl:output method="xml" indent="yes" omit-xml-declaration="no" />  
	
	<xsl:template match="Message">
	<payload>  
	<Number><xsl:value-of select="Payload/structure/Number"/></Number>
	<CreationDate><xsl:value-of select="Payload/structure/CreationDate"/></CreationDate>
	<UpdateDate><xsl:value-of select="Payload/structure/UpdateDate"/></UpdateDate>
	<ResolutionDate><xsl:value-of select="Payload/structure/ResolutionDate"/></ResolutionDate>
	<ShortDescription><xsl:value-of select="Payload/structure/ShortDescription"/></ShortDescription>
	<Description><xsl:value-of select="Payload/structure/Description"/></Description>
	<State><xsl:value-of select="Payload/structure/State"/></State>
	<Priority><xsl:value-of select="Payload/structure/Priority"/></Priority>
	<ConfigurationItem><xsl:value-of select="Payload/structure/ConfigurationItem"/></ConfigurationItem>
	<AssignmentGroup><xsl:value-of select="Payload/structure/AssignmentGroup"/></AssignmentGroup>
	<AssignedTo><xsl:value-of select="Payload/structure/AssignedTo"/></AssignedTo>
	<CorrelationId><xsl:value-of select="Payload/structure/CorrelationId"/></CorrelationId>
	<ManagedBy><xsl:value-of select="Payload/structure/ManagedBy"/></ManagedBy>
	<SupportGroup><xsl:value-of select="Payload/structure/SupportGroup"/></SupportGroup>
	<BusinessService><xsl:value-of select="Payload/structure/BusinessService"/></BusinessService> 
  </payload>
  </xsl:template>
  
</xsl:stylesheet>

/*
Script from https://blogs.sap.com/2020/09/13/sap-cpi-a-guide-to-mpl-search/
*/

Looging 

import com.sap.gateway.ip.core.customdev.util.Message;

def Message logInitStart(Message message) {
   logCustomHdrProp(message, "RICEF", "SV073");
   logCustomHdrProp(message, "_interface_log", "step-000, interface started via incoming SOAP call from SNOW");
   return message;
}

def Message logQuery(Message message) {
   logCustomHdrProp(message, "_interface_log", "step-010, message payload before mapping");
   return message;
}

def Message logPostEnd(Message message) {
   logCustomHdrProp(message, "_interface_log", "step-999, interface finished processing");
   logCustomHdrProp(message, "_interface_log", "step-020, Proxy request submitted to Solman");
   
   return message;
}

def Message logCustomHdrProp(Message message, String propName, String propValue) {
   def messageLog = messageLogFactory.getMessageLog(message);
   messageLog.addCustomHeaderProperty(propName, propValue);
   return message;
}


replace namespace 
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import java.util.*;


def Message processData(Message message)
{
    def body = message.getBody(java.lang.String) as String;
    def trim1 = body.replaceAll(/ns0:/,"");
    def trim2 = trim1.replaceAll(/ns1:/,"");

    message.setBody(trim2);
    return message;
}

File logging
import com.sap.gateway.ip.core.customdev.util.Message;

def Message logInitStart(Message message) {
    //def idNumber = message.getHeader("id", String)
    logCustomHdrProp(message, "_interface_log", "Starting: CMS message processing for File Outbound Shared Interface")
    return message
}
/*
def Message logSFTPprocess(Message message) {
    logCustomHdrProp(message, "_interface_log", "step-010, Poll files on the SFTP server dynamically")
    return message
}*/

def Message logSFTPdetails(Message message) {
    logCustomHdrProp(message, "_interface_log", "Looking for FILENAME : " +  message.getProperty("FileNames") + " -DIRECTORY : "+ message.getProperty("DirectoryNames"))
       // logCustomHdrProp(message, "_interface_log", "step-030, Directory Count = "+ message.getProperty("TotalFolders") + " File Count = "+  message.getProperty("TotalFiles"))
    return message
}

/*
def Message logmoveFile(Message message) {
    logCustomHdrProp(message, "_interface_log", "step-077, File contains *.move extension:" + message.getProperty("File_Contains_.move"))
    return message
}*/

/*
def Message logMappingStart(Message message) {
    logCustomHdrProp(message, "_interface_log", "step-040, Starting mapping process")
    return message
}

def Message logMappingEnd(Message message) {
    logCustomHdrProp(message, "_interface_log", "step-050, End of mapping process")
    return message
}*/

def Message logidid(Message message) {
    logCustomHdrProp(message, "_interface_log", "id_ID :" + message.getProperty("id_ID") + " is currently being processed")
    return message
}


def Message logSendStart(Message message) {
    logCustomHdrProp(message, "_interface_log", "Send data to ESB on SOAP Channel")
    return message
}

def Message logESBSendStart(Message message) {
    logCustomHdrProp(message, "_interface_log", "Prepare message to ESB on JMS Queue for id ID: "+ message.getProperty("id_ID")+"; FileName: "+message.getProperty("FileName")+"; File Directory: "+message.getProperty("DirectoryNames"))
    return message
}

def Message logESBSendEndJMS(Message message) {
    logCustomHdrProp(message, "_interface_log", "Message to ESB JMS Queue Completed for: "+ message.getProperty("id_ID")+"; FileName: "+message.getProperty("FileName")+"; File Directory: "+message.getProperty("DirectoryNames"))
    return message
}

def Message logESBSendEndSOAP(Message message) {
    logCustomHdrProp(message, "_interface_log", "Message to ESB SOAP Call Completed")
    return message
}

/*
def Message logEnd(Message message) {
    logCustomHdrProp(message, "_interface_log", "step-999, Ending: CMS message processing for File Outbound Shared Interface")
    return message
}*/

def Message logFileNotFound(Message message) {
    logCustomHdrProp(message, "_interface_log", "FILENAME : " + message.getProperty("FileNames") + " NOT FOUND IN - " + message.getProperty("DirectoryNames"))
    return message
}

def Message logFileFound(Message message) {
    logCustomHdrProp(message, "_interface_log", "FILENAME : " + message.getProperty("processedFileName") + " FOUND IN - " + message.getProperty("DirectoryNames"))
    return message
}

def Message logException(Message message) {
    def messageLog = messageLogFactory.getMessageLog(message) // Correct way to get Message Log
    def ex = message.getProperty("CamelExceptionCaught")
    
    if (ex) {
        def errorMessage = ex.getMessage()
        logCustomHdrProp(message, "ErrorMessage", errorMessage)

        def errorCause = ex.getCause()?.toString()
        if (errorCause) {
            logCustomHdrProp(message, "ErrorCause", errorCause)
            if (messageLog) {
                messageLog.addAttachmentAsString("ErrorCause", errorCause, "text/plain")
            }
        }
    }
    
    logCustomHdrProp(message, "_interface_log", "step-X, message exception caught")
    return message
}

def Message logCustomHdrProp(Message message, String propName, String propValue) {
    def messageLog = messageLogFactory.getMessageLog(message) // Correct method usage
    if (messageLog) {
        messageLog.addCustomHeaderProperty(propName, propValue)
    }
    return message
}


Logging
import com.sap.gateway.ip.core.customdev.util.Message
import com.sap.it.api.ITApiFactory
import com.sap.it.api.mapping.ValueMappingApi



def Message buildTargetAddressFromSyncProxy(Message message) {
    def interfaceName = getRequiredHeader(message, 'SapInterfaceName')
    def interfaceNamespace = getRequiredHeader(message, 'SapInterfaceNamespace')
    def senderService = getRequiredHeader(message, 'SapSenderService')
    def proxyMessageID = getRequiredHeader(message, 'SapMessageIdEx')

    // lookup from proxy message headers
    def idNumber = getRequiredLookupValue('SAP', 'SenderProxySyncInterface', "${interfaceNamespace}|${interfaceName}", '', 'id')
    def messageType = getRequiredLookupValue('SAP', 'SenderProxySyncInterface', "${interfaceNamespace}|${interfaceName}", '', 'MessageType')
    def sender = getRequiredLookupValue('SAP', 'SyncProxyBusinessSystem', senderService, 'BTP', 'SystemName')
    setRequiredHeader(message, 'SAP_Sender', sender)
    setRequiredHeader(message, 'SAP_MessageType', messageType)
    setRequiredHeader(message, 'id', idNumber)
    setRequiredHeader(message, 'ProxyMessageID', proxyMessageID)

    def targetProcessDirectAddress = "/${sender}/${messageType}"
    message.setHeader("TargetProcessDirectAddress", targetProcessDirectAddress)
    
    return message
}

def String getRequiredHeader(Message message, String headerName) {
    def header = message.getHeader(headerName, String)
    if(!header)
        throw new Exception("Required Header ${headerName} not defined or not passed by Sender IFlow")

    return header
}

garuntee delivery
import com.sap.gateway.ip.core.customdev.util.Message
import com.sap.it.api.ITApiFactory
import com.sap.it.api.mapping.ValueMappingApi
import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult
import com.sap.it.api.securestore.SecureStoreService
import com.sap.it.api.securestore.UserCredential
import com.sap.it.api.securestore.exception.SecureStoreException

def Message buildTargetAddressFromIdoc(Message message) {
    def idocMessageType = getRequiredProperty(message, 'IdocMessageType')
    def idocType = getRequiredProperty(message, 'IdocType')
    def idocExtension = message.getProperty('IdocExtension')
    def idocSenderPartner = getRequiredProperty(message, 'IdocSenderPartner')
    def idocNumber = getRequiredProperty(message, 'IdocNumber')
    
    def idocLookup = "${idocMessageType}|${idocType}"
    if (idocExtension)  idocLookup += "|${idocExtension}"

    // lookup from idoc properties
    def brmNumber = getRequiredLookupValue('SAP', 'IdocType', idocLookup, '', 'BRM')
    def messageType = getRequiredLookupValue('SAP', 'IdocType', idocLookup, '', 'MessageType')
    def sender = getRequiredLookupValue('SAP', 'IdocPartner', idocSenderPartner, 'BTP', 'SystemName')
    setRequiredHeader(message, 'SAP_Sender', sender)
    setRequiredHeader(message, 'SAP_MessageType', messageType)
    setRequiredHeader(message, 'BRM', brmNumber)
    setRequiredHeader(message, 'IdocNumber', removeLeadingZeros(idocNumber))

    def targetProcessDirectAddress = "/${sender}/${messageType}"
    message.setHeader("TargetProcessDirectAddress", targetProcessDirectAddress)

    return message
}

def Message buildTargetAddressFromProxy(Message message) {
    def interfaceName = getRequiredHeader(message, 'SapInterfaceName')
    def interfaceNamespace = getRequiredHeader(message, 'SapInterfaceNamespace')
    def senderService = getRequiredHeader(message, 'SapSenderService')
    def proxyMessageID = getRequiredHeader(message, 'SapMessageIdEx')

    // lookup from proxy message headers
    def brmNumber = getRequiredLookupValue('SAP', 'SenderProxyInterface', "${interfaceNamespace}|${interfaceName}", '', 'BRM')
    def messageType = getRequiredLookupValue('SAP', 'SenderProxyInterface', "${interfaceNamespace}|${interfaceName}", '', 'MessageType')
    def sender = getRequiredLookupValue('SAP', 'ProxyBusinessSystem', senderService, 'BTP', 'SystemName')
    setRequiredHeader(message, 'SAP_Sender', sender)
    setRequiredHeader(message, 'SAP_MessageType', messageType)
    setRequiredHeader(message, 'BRM', brmNumber)
    setRequiredHeader(message, 'ProxyMessageID', proxyMessageID)

    def targetProcessDirectAddress = "/${sender}/${messageType}"
    message.setHeader("TargetProcessDirectAddress", targetProcessDirectAddress)
    
    return message
}




//update by Om Heerani -  Date: 16th oct 2025
// To redirect the message from AEM common flow to BRM Business flow
def Message buildTargetAddressFromAEMTopic(Message message) {
    
    def destination = getRequiredHeader(message, 'Destination')
    def subscriber = getRequiredHeader(message, 'Subscriber')
    
    // lookup from Value mapping - ValueMapping_AEM
  
    def  targetProcessDirectAddress  = getRequiredLookupValue( subscriber, 'Destination', destination , '', 'BRM')
    
    def brmNumber =  targetProcessDirectAddress.substring( 0 , targetProcessDirectAddress.indexOf("_"))
    def messageType = targetProcessDirectAddress.substring(targetProcessDirectAddress.indexOf("_") + 1)
    
    setRequiredHeader(message, 'BRM', brmNumber)
    setRequiredHeader(message, 'SAP_MessageType', messageType)
    message.setHeader("TargetProcessDirectAddress", targetProcessDirectAddress)
    
    return message
}


def Message buildTargetAddressFromSender(Message message) {
    // read sender and message type from headers
    def messageType = getRequiredHeader(message, 'SAP_MessageType')
    def sender = getRequiredHeader(message, 'SAP_Sender')

    def targetProcessDirectAddress = "/${sender}/${messageType}"
    message.setHeader("TargetProcessDirectAddress", targetProcessDirectAddress)
    
    return message
}

def Message buildTargetProxyFromMessage(Message message) {
    def brmNumber = getRequiredHeader(message, 'BRM')
    def messageType = getRequiredHeader(message, 'SAP_MessageType')
    def sender = getRequiredHeader(message, 'SAP_Sender')
    def receiver = getRequiredHeader(message, 'SAP_Receiver')
    
    // lookup for receiver proxy properties
    def senderSystem = getRequiredLookupValue('BTP', 'SystemName', sender, 'SAP', 'ProxyBusinessSystem')
    def receiverSystem = getRequiredLookupValue('BTP', 'SystemName', receiver, 'SAP', 'ProxyBusinessSystem')
    def proxyInterface = getOptionalLookupValue('', 'BRM', brmNumber, 'SAP', 'ReceiverProxyInterface')
    if (!proxyInterface) { // further lookup with BRM with MessageType
        def brmMessage = "${brmNumber}|${messageType}"
        proxyInterface = getRequiredLookupValue('', 'BRMMessage', brmMessage, 'SAP', 'ReceiverProxyInterface')
    }
    //def targetProxyAddress = getRequiredLookupValue('SAP', 'ProxyBusinessSystem', receiverSystem, 'SAP', 'ProxyAddress')
    // proxy interface is {interfaceName}|{interfaceNamespace} format 
    def (interfaceNamespace, interfaceName) = proxyInterface.tokenize('|')
    
    setRequiredProperty(message, 'SenderComponent', senderSystem)
    setRequiredProperty(message, 'ReceiverComponent', receiverSystem)
    setRequiredProperty(message, 'InterfaceName', interfaceName)
    setRequiredProperty(message, 'InterfaceNamespace', interfaceNamespace)
    //setRequiredProperty(message, 'TargetProxyAddress', targetProxyAddress)
    
    return message
}

def Message buildTargetAddressFromA2SBridge(Message message) {
    def a2sBridge = getRequiredHeader(message, 'A2SBridge')
    logCustomHdrProp(message, "A2SBridge", a2sBridge as String)
    
    def brmNumber = getRequiredHeader(message, 'BRM')
    def sender = getRequiredHeader(message, 'SAP_Sender')
    def messageType = getRequiredHeader(message, 'SAP_MessageType')
    def receiver = getRequiredHeader(message, 'SAP_Receiver')
    def targetProcessDirectAddress = "/${receiver}/${messageType}/to/${sender}"
    message.setHeader("TargetProcessDirectAddress", targetProcessDirectAddress)

    return message
}

def Message buildTargetAddressFromReceiver(Message message) {
    def receiver = getRequiredHeader(message, 'SAP_Receiver')
    def targetProcessDirectAddress = getRequiredHeader(message, 'TargetProcessDirectAddress')
    // update TargetProcessDirectAddress append /to/${receeiver}
    message.setHeader("TargetProcessDirectAddress", "${targetProcessDirectAddress}/to/${receiver}")
    
    return message
}

def Message buildTargetAddressFromESBSender(Message message) {
    def brmNumber = getRequiredHeader(message, 'BRM')
    def Noun = getRequiredHeader(message, 'Noun')
    def Verb = getRequiredHeader(message, 'Verb')
    def sender = getRequiredHeader(message, 'SAP_Sender')
    def receiver = getRequiredHeader(message, 'SAP_Receiver')
    
    // lookup for generating esb consumer flow process direct address
	if (!brmNumber)throw new Exception ("BRM ID is missing in the incoming payload")
    def messageType = getOptionalLookupValue('ESB', 'BRMMessage', brmNumber, '', 'MessageType')
    if (!messageType){
        cmslookup = brmNumber + "|" + Noun
        messageType = getOptionalLookupValue('ESB', 'BRMMessage', cmslookup, '', 'MessageType')   
    }
    if (!messageType){
        cmslookup += "|" + Verb
        messageType = getRequiredLookupValue('ESB', 'BRMMessage', cmslookup, '', 'MessageType')
    }

    def targetProcessDirectAddress = "/${sender}/${messageType}"
    message.setHeader("TargetProcessDirectAddress", targetProcessDirectAddress)
    
    return message;
}


def Message retryExaustedDetermination(Message message) {
    // attemptNum is zero based
    int attemptNum = (message.getHeader('attemptNum', String) ?: 0) as Integer
    int maxRetries = message.getHeader('maxRetries', String) as Integer
    if(attemptNum >= maxRetries)
        message.setProperty('retryExausted', 'true')

    return message
}

def Message buildTargetUrlToESB(Message message) {
    def esbReceiver = getRequiredHeader(message, 'SAP_Receiver')
    def esbServiceName = getRequiredHeader(message, 'ESBServiceName')
    def esbSystemService = "${esbReceiver}|${esbServiceName}";

    // lookup from ESB Receiver
    def esbHost = getRequiredLookupValue('ESB', 'SystemName', esbReceiver, 'ESB', 'Host');
    def esbPort = getRequiredLookupValue('ESB', 'SystemService', esbSystemService, 'ESB', 'Port');
    def esbPath = getRequiredLookupValue('ESB', 'ServiceName', esbServiceName, 'ESB', 'Path');
    // always use http for connection via Cloud Connector
    def targetUrl = "http://${esbHost}:${esbPort}/${esbPath}";
    message.setProperty("TargetUrl", targetUrl);

    return message
}

def Message buildReceiverErrorDataStoreProperties(Message message) {
    def jmsDestination = message.getHeader('JMSDestination', String)
    if (jmsDestination) message.setHeader('TargetJMSQueue', jmsDestination)
    // restart data store name ${brm}-${receiver}-RCV-ERR
    def brm = message.getHeader('BRM', String)
    def receiver = message.getHeader('SAP_Receiver', String)
    def dataStoreName = "${brm}-${receiver}-RCV-ERR"
    message.setProperty("RestartDataStoreName", dataStoreName)
    // data store entry ID ${brm}_${messageType}_${keyValue1}_${applicationId}
    def messageType = message.getHeader('SAP_MessageType', String)
    def keyValue1 = removeLeadingZeros(message.getHeader('KeyValue1', String))
    if (!keyValue1) keyValue1 = "null"
    def applicationId = message.getHeader('SAP_ApplicationID', String)
    //applicationId = applicationId.replaceAll("-", "").toUpperCase()
    def entryId = "${brm}_${messageType}_${keyValue1}_${applicationId}"
    message.setProperty("RestartDataStoreEntryID", entryId)

    return message
}

def Message buildSenderErrorDataStoreProperties(Message message) {
    // save source JMS queue name as the restart target queue 
    def jmsDestination = message.getHeader('JMSDestination', String)
    if (jmsDestination) message.setHeader('TargetJMSQueue', jmsDestination)
    // restart data store name ${brm}-${sender}-SND-ERR
    def brm = message.getHeader('BRM', String)
    def sender = message.getHeader('SAP_Sender', String)
    def dataStoreName = "${brm}-${sender}-SND-ERR"
    message.setProperty("RestartDataStoreName", dataStoreName)
    // data store entry ID ${brm}_${messageType}_${keyValue1}_${applicationId}
    def messageType = message.getHeader('SAP_MessageType', String)
    def keyValue1 = removeLeadingZeros(message.getHeader('KeyValue1', String))
    if (!keyValue1) keyValue1 = "null"
    def applicationId = message.getHeader('SAP_ApplicationID', String)
    def entryId = "${brm}_${messageType}_${keyValue1}_${applicationId}"
    message.setProperty("RestartDataStoreEntryID", entryId)

    return message
}

def Message buildIntegrationErrorDataStoreProperties(Message message) {
    // save source JMS queue name as the restart target queue 
    def jmsDestination = message.getHeader('JMSDestination', String)
    if (jmsDestination) message.setHeader('TargetJMSQueue', jmsDestination)
    // restart data store name ${brm}-${sender}-INT-ERR
    def brm = message.getHeader('BRM', String)
    def sender = message.getHeader('SAP_Sender', String)
    def dataStoreName = "${brm}-${sender}-INT-ERR"
    message.setProperty("RestartDataStoreName", dataStoreName)
    // data store entry ID ${brm}_${messageType}_${keyValue1}_${applicationId}
    def messageType = message.getHeader('SAP_MessageType', String)
    def keyValue1 = removeLeadingZeros(message.getHeader('KeyValue1', String))
    if (!keyValue1) keyValue1 = "null"
    def applicationId = message.getHeader('SAP_ApplicationID', String)
    def entryId = "${brm}_${messageType}_${keyValue1}_${applicationId}"
    message.setProperty("RestartDataStoreEntryID", entryId)

    return message
}

def Message parseUnifierResponseStatus(Message message) {
    Reader reader = message.getBody(Reader)
    GPathResult response = new XmlSlurper().parse(reader)
    GPathResult statusCode = response.return.statusCode
    GPathResult errorStatus = response.return.errorStatus
    
    if (statusCode.text() != '200') {
        throw new Exception("UnifierWebServices error: ${statusCode.text()} - ${errorStatus.text()}");
    }
    return message
}

def Message readUnifierAuthCode(Message message) {
    def apikey_alias = message.getProperty("UnifierAuthCodeAlias")
    def secureStorageService =  ITApiFactory.getService(SecureStoreService.class, null)
    try{
        def secureParameter = secureStorageService.getUserCredential(apikey_alias)
        def apikey = secureParameter.getPassword().toString()
        message.setProperty("AuthCode", apikey)
    } catch(Exception e){
        throw new SecureStoreException("Secure Parameter not available")
    }
    return message
}

def Message generateRandomIntegrationQueue(Message message) {
    int integrationQueueNumber = getRequiredProperty(message, 'JMS_IntegrationQueueNumber') as Integer
    def integrationQueueName = getRequiredProperty(message, 'JMS_IntegrationQueueName')

    // generate a random queue number within [1, integrationQueueNumber] range
    def integrationQueue = Math.abs(new Random().nextInt() % [integrationQueueNumber]) + 1 as String
    def targetJMSQueue = "${integrationQueueName}_${integrationQueue}";
    message.setHeader("TargetJMSQueue", targetJMSQueue);

    return message
}

def String getPropertyWithDefaultValue(Message message, String propertyName, String defaultValue) {
    def property = message.getProperty(propertyName) as String
    if(!property)
        property = defaultValue

    return property
}

def String getRequiredProperty(Message message, String propertyName) {
    def property = message.getProperty(propertyName) as String
    if(!property)
        throw new Exception("Required Property ${propertyName} not defined")

    return property
}

def String getRequiredHeader(Message message, String headerName) {
    def header = message.getHeader(headerName, String)
    if(!header)
        throw new Exception("Required Header ${headerName} not defined or not passed by Sender IFlow")

    return header
}

def String getHeaderWithDefault(Message message, String headerName, String defaultValue) {
    def header = message.getHeader(headerName, String)
    if(!header)
        header = defaultValue

    return header
}

def Message setRequiredProperty(Message message, String propertyName, String propertyValue) {
    if (!propertyValue)
        throw new Exception("Required Property ${propertyName} has no value defined")

    message.setProperty(propertyName, propertyValue)
    return message
}

def Message setRequiredHeader(Message message, String headerName, String headerValue) {
    if (!headerValue)
        throw new Exception("Required Header ${headerName} has no value defined or no lookup value returned from value map")

    message.setHeader(headerName, headerValue)
    return message
}

def String getRequiredLookupValue(String sourceAgency, String sourceIdentifier, String lookupValue, String targetAgency, String targetIdentifier) {
    def valueMapApi = ITApiFactory.getApi(ValueMappingApi.class, null)
    def lookupResult = valueMapApi.getMappedValue(sourceAgency, sourceIdentifier, lookupValue, targetAgency, targetIdentifier)
    if (!lookupResult) 
        throw new Exception("No lookup result returned from ValueMap(${sourceAgency}, ${sourceIdentifier}, ${lookupValue}, ${targetAgency}, ${targetIdentifier})")
        
    return lookupResult
}

def String getOptionalLookupValue(String sourceAgency, String sourceIdentifier, String lookupValue, String targetAgency, String targetIdentifier) {
    def valueMapApi = ITApiFactory.getApi(ValueMappingApi.class, null)
    def lookupResult = valueMapApi.getMappedValue(sourceAgency, sourceIdentifier, lookupValue, targetAgency, targetIdentifier)
    return lookupResult
}

def Message logCustomHdrProp(Message message, String propName, String propValue) {
	def messageLog = messageLogFactory.getMessageLog(message)
	messageLog.addCustomHeaderProperty(propName, propValue)
	
	return message
}

def String removeLeadingZeros(String value) {
    if (!value)
        return null

    def pattern = ~/^0+(?!$)/
    return value.replaceAll(pattern, "")
}

logging
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import com.sap.it.api.mapping.ValueMappingApi;

def Message logCustomHeaders(Message message) {
    def brmNumber = message.getHeader('BRM', String)
    def idocNumber = message.getHeader('IdocNumber', String)
    def proxyMessageID = message.getHeader('ProxyMessageID', String)
    def esbSessionID = message.getHeader('ESBSessionID', String)
    def esbCorrelationID = message.getHeader('ESBCorrelationID', String)
    def keyValue1 = getHeaderWithDefaultValue(message, 'KeyValue1', "")
    def keyValue2 = getHeaderWithDefaultValue(message, 'KeyValue2', "")
    def keyValue3 = getHeaderWithDefaultValue(message, 'KeyValue3', "")
    def keyValue4 = getHeaderWithDefaultValue(message, 'KeyValue4', "")
    if (keyValue1)  logCustomHdrProp(message, "KeyValue1", removeLeadingZeros(keyValue1))
    if (keyValue2)  logCustomHdrProp(message, "KeyValue2", removeLeadingZeros(keyValue2))
    if (keyValue3)  logCustomHdrProp(message, "KeyValue3", removeLeadingZeros(keyValue3))
    if (keyValue4)  logCustomHdrProp(message, "KeyValue4", removeLeadingZeros(keyValue4))
    if (brmNumber)  logCustomHdrProp(message, "BRM", brmNumber)
    if (idocNumber)  logCustomHdrProp(message, "IdocNumber", idocNumber)
    if (proxyMessageID)  logCustomHdrProp(message, "ProxyMessageID", proxyMessageID)
    if (esbSessionID)  logCustomHdrProp(message, "ESBSessionID", esbSessionID)
    if (esbCorrelationID)  logCustomHdrProp(message, "ESBCorrelationID", esbCorrelationID)
    // log retry headers and set custom status 'Retried'
    def applicationId = getHeaderWithDefaultValue(message, 'SAP_ApplicationID', "")
    def maxRetries = getHeaderWithDefaultValue(message, 'maxRetries', "0")
    def attemptNum = message.getHeader('SAPJMSRetries', String)
    if (attemptNum != null) {
        message.setHeader("attemptNum", attemptNum as String)
        message.setProperty("SAP_MessageProcessingLogCustomStatus", "Retried")
        logCustomHdrProp(message, "Retry_AttemptNum", attemptNum as String)
        logCustomHdrProp(message, "Retry_MaxRetries", maxRetries as String)
        logCustomHdrProp(message, "Retry_CorrelationId", applicationId as String)
    }
    // log restart header and set custom status 'Restarted'
    def restarted = message.getHeader('Restarted', String)
    if (restarted) {
        message.setProperty("SAP_MessageProcessingLogCustomStatus", "Restarted")
        logCustomHdrProp(message, "ManualRestarted", restarted as String)
    }
    
    return message
}

def Message copyRestartHeaders(Message message) {
	// when manual restart SAP headers are not saved to Data Store therefore we need to save them to custom  headers 
	copyHeader(message, 'SAP_MessageType', '_MessageType')
    copyHeader(message, 'SAP_ApplicationID', '_ApplicationID')
    copyHeader(message, 'SAP_Sender', '_Sender')
    copyHeader(message, 'SAP_Receiver', '_Receiver')
    
    return message
}

def Message copyHeader(Message message, String sapHeaderName, String HeaderName) {
    def sapHeader = message.getHeader(sapHeaderName, String)
    def Header = message.getHeader(HeaderName, String)
    if (!sapHeader) {
        message.setHeader(sapHeaderName, Header)
    }
    if (!Header) {
        message.setHeader(HeaderName, sapHeader)
    }
    
    return message
}

def String getRequiredHeader(Message message, String headerName) {
    def header = message.getHeader(headerName, String)
    if(!header)
        throw new Exception("Required Header ${headerName} not defined or not passed by Sender IFlow")

    return header
}

def String getHeaderWithDefaultValue(Message message, String headerName, String defaultValue) {
	def header = message.getHeader(headerName, String)
    if(!header)
        header = defaultValue

    return header
}

def Message logCustomHdrProp(Message message, String propName, String propValue) {
	def messageLog = messageLogFactory.getMessageLog(message)
	messageLog.addCustomHeaderProperty(propName, propValue)
	
	return message
}

def String removeLeadingZeros(String value) {
	if (!value)
		return null
              
    def pattern = ~/^0+(?!$)/
    return value.replaceAll(pattern, "")
}



def String getRequiredLookupValue(String sourceAgency, String sourceIdentifier, String lookupValue, String targetAgency, String targetIdentifier) {
    def valueMapApi = ITApiFactory.getApi(ValueMappingApi.class, null)
    def lookupResult = valueMapApi.getMappedValue(sourceAgency, sourceIdentifier, lookupValue, targetAgency, targetIdentifier)
    if (!lookupResult) 
        throw new Exception("No lookup result returned from ValueMap(${sourceAgency}, ${sourceIdentifier}, ${lookupValue}, ${targetAgency}, ${targetIdentifier})")
        
    return lookupResult
}

def Message setRequiredHeader(Message message, String headerName, String headerValue) {
    if (!headerValue)
        throw new Exception("Required Header ${headerName} has no value defined or no lookup value returned from value map")

    message.setHeader(headerName, headerValue)
    return message
}


DATE time
package urn.ydro.com_PI_FunctionLibrary;

import com.sap.aii.mapping.api.*;
import com.sap.aii.mapping.lookup.*;
import com.sap.aii.mappingtool.tf7.rt.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.sap.aii.mappingtool.functionlibrary.*;

public class DateTime extends AFunctionLibrary {

    public static final String WSDL_DTTM_FRMT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String IDOC_DTTM_FRMT = "yyyyMMddHHmmss";

    //private static final String IDOC_DT_FRMT = "yyyyMMdd";
    public static final String DEFAULT_TIME = "000000";

    public static final String TZ_UTC = "UTC";

    public static final String TZ_LOCAL = "America/Vancouver";

    //private static final int ERROR_MODE_FAIL = -1;
    public static final int ERROR_MODE_NULL = 0;

    public static final int ERROR_MODE_CURRENT = 1;

    private static boolean isEmptyDateString(String dateS) {
        return (isEmpty(dateS) || dateS.startsWith("0"));
    }

    private static boolean isEmptyTimeString(String timeS) {
        return isEmpty(timeS);
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    private static Date parseDate(DateFormat df, String value, int errorMode) throws StreamTransformationException {
        try {
            return df.parse(value);
        } catch (ParseException e) {
            //most used first
            if (errorMode == ERROR_MODE_CURRENT)
                return new Date();
            if (ERROR_MODE_NULL == errorMode)
                return null;
            throw new StreamTransformationException("Error parsing date string " + value + " with format " + df.toString());
        }
    }

    private static String safeFormat(DateFormat df, Date date) {
        try {
            return df.format(date);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static String convertDateTime(String value, int errorMode, String sourceFmt, String sourceTZ, String targetFmt, String targetTZ) throws StreamTransformationException {
        DateFormat df = getDateFormat(sourceFmt, sourceTZ);
        Date date = parseDate(df, value, errorMode);
        df = getDateFormat(targetFmt, targetTZ);
        return safeFormat(df, date);
    }

    private static DateFormat getDateFormat(String format, String timezone) {
        DateFormat df = new SimpleDateFormat(format);
        if (!isEmpty(timezone))
            df.setTimeZone(TimeZone.getTimeZone(timezone));
        return df;
    }

    @Init
    public void init(GlobalContainer container) throws StreamTransformationException {
    }

    @CleanUp
    public void cleanUp(GlobalContainer container) throws StreamTransformationException {
    }

    //The key property of the UDFs should not be changed. If it is changed it cannot be used
    //in the Message Mappings which are imported from ESR system.
    @FunctionLibraryMethod(category = "_DateTime", title = "CurrentDateTime", executionType = "SINGLE_VALUE", key = "calculate1222")
    public String CurrentDateTime(@UDFParam(paramCategory = "Argument", title = "DateTime Format") String format, @UDFParam(paramCategory = "Argument", title = "Timezone. Blank for default") String timezone, Container container) throws StreamTransformationException {
        return getDateFormat(format, timezone).format(new Date());
    }

    @FunctionLibraryMethod(category = "_DateTime", title = "ConvertDateTime", executionType = "SINGLE_VALUE", key = "calculate12222")
    public String ConvertDateTime(@UDFParam(paramCategory = "Argument", title = "") String value, @UDFParam(paramCategory = "Argument", title = "Java DateTime format") String sourceFmt, @UDFParam(paramCategory = "Argument", title = "empty for local timezone") String sourceTZ, @UDFParam(paramCategory = "Argument", title = "Java DateTime format") String targetFmt, @UDFParam(paramCategory = "Argument", title = "empty for local timezone") String targetTZ, @UDFParam(paramCategory = "Argument", title = "-1=fail; 0=null; 1=current") int errorMode, Container container) throws StreamTransformationException {
        return convertDateTime(value, errorMode, sourceFmt, sourceTZ, targetFmt, targetTZ);
    }

    @FunctionLibraryMethod(category = "_DateTime", title = "ConcatAndConvertDateAndTime", executionType = "SINGLE_VALUE", key = "calculate122222")
    public String ConcatAndConvertDateAndTime(@UDFParam(paramCategory = "Argument", title = "") String dateS, @UDFParam(paramCategory = "Argument", title = "") String timeS, @UDFParam(paramCategory = "Argument", title = "java SimpleDateFormat") String sourceFmt, @UDFParam(paramCategory = "Argument", title = "empty for local timezone") String sourceTZ, @UDFParam(paramCategory = "Argument", title = "java SimpleDateFormat") String targetFmt, @UDFParam(paramCategory = "Argument", title = "empty for local timezone") String targetTZ, @UDFParam(paramCategory = "Argument", title = "-1=fail; 0=null; 1=current") int errorMode, Container container) throws StreamTransformationException {
        if (isEmptyDateString(dateS)) {
            return null;
        }
        if (isEmptyTimeString(timeS))
            timeS = DEFAULT_TIME;
        return convertDateTime(dateS + timeS, errorMode, sourceFmt, sourceTZ, targetFmt, targetTZ);
    }

    @FunctionLibraryMethod(category = "_DateTime", title = "ConvertIdocDateAndTime_to_UtcWsdlDateTime", executionType = "SINGLE_VALUE", key = "calculate1")
    public String ConvertIdocDateAndTime_to_UtcWsdlDateTime(@UDFParam(paramCategory = "Argument", title = "Date in format yyyyMMdd") String dateS, @UDFParam(paramCategory = "Argument", title = "Time in format HHmmdd") String timeS, Container container) throws StreamTransformationException {
        return ConcatAndConvertDateAndTime(dateS, timeS, IDOC_DTTM_FRMT, TZ_LOCAL, WSDL_DTTM_FRMT, TZ_UTC, ERROR_MODE_CURRENT, container);
    }

    @FunctionLibraryMethod(category = "_DateTime", title = "ConvertIdocDate_to_WsdlDateTime", executionType = "SINGLE_VALUE", key = "calculate12")
    public String ConvertIdocDate_to_WsdlDateTime(@UDFParam(paramCategory = "Argument", title = "Date in format yyyyMMdd") String dateS, Container container) throws StreamTransformationException {
        return ConcatAndConvertDateAndTime(dateS, DEFAULT_TIME, IDOC_DTTM_FRMT, TZ_LOCAL, WSDL_DTTM_FRMT, TZ_LOCAL, ERROR_MODE_CURRENT, container);
    }

    @FunctionLibraryMethod(category = "_DateTime", title = "ConvertIdocDate_to_UtcWsdlDateTime", executionType = "SINGLE_VALUE", key = "871768c3-5344-11ee-cf31-00059a3c7a00")
    public String ConvertIdocDate_to_UtcWsdlDateTime(@UDFParam(paramCategory = "Argument", title = "Date in format yyyyMMdd") String dateS, Container container) throws StreamTransformationException {
        return ConcatAndConvertDateAndTime(dateS, DEFAULT_TIME, IDOC_DTTM_FRMT, TZ_LOCAL, WSDL_DTTM_FRMT, TZ_UTC, ERROR_MODE_CURRENT, container);
    }
}



Structural java
package urn.ydro.com_PI_FunctionLibrary;

import com.sap.aii.mapping.api.*;
import com.sap.aii.mapping.lookup.*;
import com.sap.aii.mappingtool.tf7.rt.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;
import com.sap.aii.mappingtool.functionlibrary.*;

public class Structural extends AFunctionLibrary {

    public static final String DEFAULT_HASH_NAME = "USER_HASH";

    private boolean isContextChange(String entry) {
        return ResultList.CC.equals(entry);
    }

    /**
     * Combines an array of string into 1 string using a delimiter
     */
    private String combine(String[] arr, String delimiter) {
        if (arr.length == 0) {
            return ResultList.SUPPRESS;
        }
        StringBuilder sb = new StringBuilder();
        //add the first one before delimiter
        sb.append(arr[0]);
        for (int i = 1; i < arr.length; ++i) {
            sb.append(delimiter).append(arr[i]);
        }
        return sb.toString();
    }

    private String getArrValueSafelyWithDefault(String[] arr, int ind, String defalt) {
        return (ind < arr.length) ? arr[ind] : defalt;
    }

    /**
     * Filter a list of names value pairs using a regex. Include or Exclude option.
     */
    private void filterNameValuePairsHelper(String regex, int mode, int context, String[] names, String[] values, ResultList namesResult, ResultList valuesResult, Container container) {
        boolean keepContext = (context == 0) ? true : false;
        boolean exclude = (mode == 0) ? false : true;
        //pre-compile the pattern before the loop
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        AbstractTrace trace = container.getTrace();
        //	trace.addInfo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //	trace.addInfo("namesLength:" + names.length);
        //	trace.addInfo("valuesLength:" + values.length);
        //	trace.addInfo("regex:" + regex);
        //	int index = 0; //for debugging
        int valueIndex = 0;
        for (String name : names) {
            String value = getArrValueSafelyWithDefault(values, valueIndex++, null);
            //		trace.addInfo("-------------");
            //		trace.addInfo("index:" + ++index);
            //		trace.addInfo("name:" + name);
            //		trace.addInfo("valueIndex:" + valueIndex);
            //		trace.addInfo("value:" + value);
            if (isContextChange(name)) {
                if (keepContext) {
                    if (namesResult != null)
                        namesResult.addContextChange();
                    valuesResult.addContextChange();
                }
                if (!isContextChange(value)) {
                    trace.addInfo("SearchNameValuePairsWithHash: Adjusting value array offset. Discovered empty 'name' between context changes whe value is " + value);
                    //Empty 'name' between context changes. Adjusting value array offset.
                    valueIndex++;
                }
            } else {
                if (isContextChange(value)) {
                    trace.addInfo("SearchNameValuePairsWithHash: Adjusting value array offset. Discovered empty 'value' between context changes for name=" + name);
                    //Empty 'value' between context changes. Adjusting value array offset.
                    valueIndex--;
                    value = null;
                }
                boolean matches = pattern.matcher(name).matches();
                if (matches ^ exclude) {
                    if (namesResult != null)
                        namesResult.addValue(name);
                    valuesResult.addValue(value);
                }
            }
        }
    }

    @Init
    public void init(GlobalContainer container) throws StreamTransformationException {
    }

    @CleanUp
    public void cleanUp(GlobalContainer container) throws StreamTransformationException {
    }

    //The key property of the UDFs should not be changed. If it is changed it cannot be used
    //in the Message Mappings which are imported from ESR system.
    @FunctionLibraryMethod(category = "_Structural", title = "FlattenContext", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate12")
    public void FlattenContext(@UDFParam(paramCategory = "Argument", title = "") String[] delimiter, @UDFParam(paramCategory = "Argument", title = "") String[] values, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        /**
         * @author Ryan Murphy
         * @date 2012-06-21
         * @description flattens a context into a single string with delimiter
         */
        if (values.length == 0) {
            return;
        }
        String delimiterValue;
        if (delimiter.length == 0) {
            delimiterValue = "";
        } else {
            delimiterValue = delimiter[0];
        }
        if (delimiterValue.contains("LF"))
            delimiterValue = delimiterValue.replace("LF", "\n");
        if (delimiterValue.contains("CR"))
            delimiterValue = delimiterValue.replace("CR", "\r");
        if (delimiterValue.contains("TAB"))
            delimiterValue = delimiterValue.replace("TAB", "\t");
        result.addValue(combine(values, delimiterValue));
    }

    @FunctionLibraryMethod(category = "_Structural", title = "searchNameValuePair", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate1222222222")
    public void searchNameValuePair(@UDFParam(paramCategory = "Argument", title = "") String[] pairName, @UDFParam(paramCategory = "Argument", title = "") String[] pairValue, @UDFParam(paramCategory = "Argument", title = "") String[] searchName, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        /**
         * @author Kenn Wu
         * @date 2014-02-19
         * @description Searches name value pairs for specific name and returns all hits
         */
        for (int i = 0; i < pairName.length; i++) {
            if (searchName.equals(pairName[i])) {
                result.addValue(pairValue[i]);
            }
        }
    }

    @FunctionLibraryMethod(category = "_Structural", title = "SearchNameValuePairsWithHash", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate122")
    public void SearchNameValuePairsWithHash(@UDFParam(paramCategory = "Argument", title = "") String[] pairNames, @UDFParam(paramCategory = "Argument", title = "") String[] pairValues, @UDFParam(paramCategory = "Argument", title = "") String[] searchName, @UDFParam(paramCategory = "Argument", title = "") String[] hashName, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        /**
         * @author Ryan Murphy
         * @date 2012-06-21
         * @description Builds up a HashMap of name/value pairs and returns value for a specific name.
         * 	Stores the HashMap for efficient retrieval of the next value, if needed.
         * 	Handles missing values in the name or value context lists which cause unbalanced arrays.
         * 	Always returns 1 element in order to avoid mapping errors. May be null.
         */
        String hashNameValue;
        if (hashName.length == 0) {
            hashNameValue = DEFAULT_HASH_NAME;
        } else {
            hashNameValue = hashName[0];
        }
        String searchNameValue = searchName[0];
        AbstractTrace trace = container.getTrace();
        Map<String, String> map = null;
        Object mapObj = container.getGlobalContainer().getParameter(hashNameValue);
        //if map is initialised, use it and return
        if (mapObj != null) {
            map = (Map<String, String>) mapObj;
            result.addValue(map.get(searchNameValue));
            return;
        } else {
            trace.addInfo("SearchNameValuePairsWithHash: Creating new HashMap - " + hashNameValue);
            //assume the input array is at least half context changes, so no need to buffer the map size
            map = new HashMap<String, String>(pairNames.length);
            container.getGlobalContainer().setParameter(hashNameValue, map);
        }
        int valueInd = 0;
        for (String name : pairNames) {
            String value = pairValues[valueInd++];
            boolean nameContextChange = ResultList.CC.equals(name);
            boolean valueContextChange = ResultList.CC.equals(value);
            if (nameContextChange) {
                if (valueContextChange) {
                    continue;
                } else {
                    trace.addInfo("SearchNameValuePairsWithHash: Adjusting value array offset. Discovered empty name between context changes whe value is " + value);
                    valueInd++;
                }
            } else {
                if (valueContextChange) {
                    trace.addInfo("SearchNameValuePairsWithHash: Adjusting value array offset. Discovered empty value between context changes for name=" + name);
                    value = null;
                    valueInd--;
                }
                map.put(name, value);
            }
        }
        String val = map.get(searchNameValue);
        if (val == null) {
            val = ResultList.SUPPRESS;
        }
        result.addValue(val);
    }

    @FunctionLibraryMethod(category = "_Structural", title = "FilterNameValuePairs", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate122222")
    public void FilterNameValuePairs(@UDFParam(paramCategory = "Argument", title = "") String[] regex, @UDFParam(paramCategory = "Argument", title = "Mode: 0=include, -1=exclude") int[] mode, @UDFParam(paramCategory = "Argument", title = "Context: 0=keep, -1=remove") int[] context, @UDFParam(paramCategory = "Argument", title = "") String[] names, @UDFParam(paramCategory = "Argument", title = "") String[] values, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        String regexValue = regex[0];
        int modeValue = mode[0];
        int contextValue = context[0];
        filterNameValuePairsHelper(regexValue, modeValue, contextValue, names, values, null, result, container);
    }

    @FunctionLibraryMethod(category = "_Structural", title = "HashNameValuePairs", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate1222")
    public void HashNameValuePairs(@UDFParam(paramCategory = "Argument", title = "") String[] names, @UDFParam(paramCategory = "Argument", title = "") String[] values, @UDFParam(paramCategory = "Argument", title = "") String[] hashName, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        /**
         * @author Ryan Murphy
         * @date 2012-06-21
         * @description puts key/value pairs into a hashmap for efficient lookups and stores in global container
         * @see GetValueFromHash to get the values out
         */
        String[] searchName = new String[] { "" };
        SearchNameValuePairsWithHash(names, values, searchName, hashName, result, container);
        result.clear();
    }

    @FunctionLibraryMethod(category = "_Structural", title = "GetValueFromHash", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate12222")
    public void GetValueFromHash(@UDFParam(paramCategory = "Argument", title = "") String[] searchName, @UDFParam(paramCategory = "Argument", title = "") String[] hashName, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        /**
         * @author Ryan Murphy
         * @date 2012-06-21
         * @description gets key/value pairs from a Hash that must be populated earlier in the mapping
         * 	Used for efficient retrieval for large data sets to avoid inefficient O(n*n)
         * 	Uses ResultList instead of String return in case the value is not in the hash
         */
        String hashNameValue;
        if (hashName.length == 0) {
            hashNameValue = DEFAULT_HASH_NAME;
        } else {
            hashNameValue = hashName[0];
        }
        String searchNameValue = searchName[0];
        Map<String, String> map = (Map<String, String>) container.getGlobalContainer().getParameter(hashNameValue);
        result.addValue(map.get(searchNameValue));
    }

    @FunctionLibraryMethod(category = "_Structural", title = "ContextIndex", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate12222222")
    public void ContextIndex(@UDFParam(paramCategory = "Argument", title = "mode: 0=no errors, 1=errors") int[] errorMode, @UDFParam(paramCategory = "Argument", title = "") String[] context, @UDFParam(paramCategory = "Argument", title = "base 0 index") int[] ind, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        int errorModeValue = errorMode[0];
        if (ind.length == 0) {
            if (errorModeValue == 0) {
                return;
            }
            throw new StreamTransformationException("ContextIndex UDF: No Index Specified");
        }
        int index = ind[0];
        if (context.length <= index) {
            if (errorModeValue == 0) {
                return;
            }
            throw new StreamTransformationException("ContextIndex UDF: Index value " + index + " is greater than Context Length " + context.length);
        }
        result.addValue(context[index]);
    }

    @FunctionLibraryMethod(category = "_Structural", title = "ContexAfterIndex", executionType = "ALL_VALUES_OF_CONTEXT", key = "1c42f180-6fec-11e8-b53e-00059a3c7a00")
    public void ContexAfterIndex(@UDFParam(paramCategory = "Argument", title = "mode: 0=no errors, 1=errors") int[] errorMode, @UDFParam(paramCategory = "Argument", title = "") String[] context, @UDFParam(paramCategory = "Argument", title = "base 0 index") int[] ind, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        int errorModeValue = errorMode[0];
        if (ind.length == 0) {
            if (errorModeValue == 0) {
                return;
            }
            throw new StreamTransformationException("ContextAfterIndex UDF: No Index Specified");
        }
        //get the last index from ind queue
        int index = ind[0];
        for (int i = 0; i < ind.length; i++) {
            if (ind[i] > index) {
                index = ind[i];
            }
        }
        if (context.length <= index) {
            if (errorModeValue == 0) {
                return;
            }
            throw new StreamTransformationException("ContextAfterIndex UDF: Index value " + index + " is greater than Context Length " + context.length);
        }
        for (int i = 0; i < context.length; i++) {
            if (i > index) {
                result.addValue(context[i]);
            }
        }
    }

    @FunctionLibraryMethod(category = "_Structural", title = "ContexBetweenIndex", executionType = "ALL_VALUES_OF_CONTEXT", key = "6b32052e-6fec-11e8-c802-00059a3c7a00")
    public void ContexBetweenIndex(@UDFParam(paramCategory = "Argument", title = "mode: 0=no errors, 1=errors") int[] errorMode, @UDFParam(paramCategory = "Argument", title = "") String[] context, @UDFParam(paramCategory = "Argument", title = "base 0 index") int[] ind, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        int errorModeValue = errorMode[0];
        if (ind.length == 0) {
            if (errorModeValue == 0) {
                return;
            }
            throw new StreamTransformationException("ContextBetweenIndex UDF: No Index Specified");
        }
        //get the last index from ind queue
        int index = ind[0];
        for (int i = 0; i < ind.length; i++) {
            if (ind[i] > index) {
                index = ind[i];
            }
        }
        if (context.length <= index) {
            if (errorModeValue == 0) {
                return;
            }
            throw new StreamTransformationException("ContextBetweenIndex UDF: Index value " + index + " is greater than Context Length " + context.length);
        }
        for (int i = 0; i < ind.length; i++) {
            int startIndex = ind[i];
            int endIndex = i + 1 == ind.length ? ind[i] : ind[i + 1];
            if (startIndex == endIndex)
                break;
            for (int j = startIndex + 1; j < endIndex; j++) {
                result.addValue(context[j]);
            }
            result.addValue(ResultList.CC);
        }
    }
}


Text 
package urn.ydro.com_PI_FunctionLibrary;

import com.sap.aii.mapping.api.*;
import com.sap.aii.mapping.lookup.*;
import com.sap.aii.mappingtool.tf7.rt.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.StringBuffer;
import com.sap.aii.mappingtool.functionlibrary.*;

public class Text extends AFunctionLibrary {

    private static final String REGEX_XML_TAG_CHARS = "(<)|(&lt;)|(&#60;)|(>)|(&gt;)|(&#62;)";

    //only matches on either end
    private static final String REGEX_XML_TAG_WRAPPERS = "^(" + REGEX_XML_TAG_CHARS + ")|(" + REGEX_XML_TAG_CHARS + ")$";

    public static final java.util.regex.Pattern PATTERN_XMLTAGWRAPPERS = java.util.regex.Pattern.compile(REGEX_XML_TAG_WRAPPERS);

    private static String removeXmlTagWrappers(String value) {
        java.util.regex.Matcher m = PATTERN_XMLTAGWRAPPERS.matcher(value);
        return m.replaceAll("");
    }

    private static String replaceDelimitier(String delimiter) {
        if (delimiter == null)
            delimiter = "";
        if (delimiter.contains("LF"))
            delimiter = delimiter.replace("LF", "\n");
        if (delimiter.contains("CR"))
            delimiter = delimiter.replace("CR", "\r");
        if (delimiter.contains("TAB"))
            delimiter = delimiter.replace("TAB", "\t");
        return delimiter;
    }

    /**
     * Combines an array of strings into 1 string using a delimiter *
     */
    private String combine(String[] arr, String delimiter) {
        if (arr.length == 0) {
            return ResultList.SUPPRESS;
        }
        StringBuilder sb = new StringBuilder();
        //add the first one before delimiter
        sb.append(arr[0]);
        for (int i = 1; i < arr.length; ++i) {
            sb.append(delimiter).append(arr[i]);
        }
        return sb.toString();
    }

    @Init
    public void init(GlobalContainer container) throws StreamTransformationException {
    }

    @CleanUp
    public void cleanUp(GlobalContainer container) throws StreamTransformationException {
    }

    //The key property of the UDFs should not be changed. If it is changed it cannot be used
    //in the Message Mappings which are imported from ESR system.
    @FunctionLibraryMethod(category = "_Text", title = "padZerosIfPositiveInteger", executionType = "SINGLE_VALUE", key = "072ebca1-c714-11e6-a762-843a4b33d018")
    public String padZerosIfPositiveInteger(@UDFParam(paramCategory = "Argument", title = "") String value, @UDFParam(paramCategory = "Argument", title = "") String length, Container container) throws StreamTransformationException {
        String format = "%0" + length + "d";
        try {
            int returnValue = Integer.parseInt(value);
            if (returnValue >= 0) {
                return String.format(format, returnValue);
            } else {
                return value;
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }

    @FunctionLibraryMethod(category = "_Text", title = "isEmpty", executionType = "SINGLE_VALUE", key = "calculate12222222")
    public String isEmpty(@UDFParam(paramCategory = "Argument", title = "") String value, Container container) throws StreamTransformationException {
        return Boolean.toString(value == null || value.length() == 0);
    }

    @FunctionLibraryMethod(category = "_Text", title = "Substring", executionType = "SINGLE_VALUE", key = "calculate12")
    public String Substring(@UDFParam(paramCategory = "Argument", title = "") int numChars, @UDFParam(paramCategory = "Argument", title = "") String inputString, Container container) throws StreamTransformationException {
        /**
         * 		@author Kenn Wu
         * 		@date 2011-02-23
         * 		@description null safe and length safe substring
         * 			this function will read from start of input to...
         * 			parameter value; or end of string, whichever comes first
         *
         * 		- 2012-06-15: Handle null input string
         * *
         */
        if (inputString != null && (inputString.length() > numChars)) {
            return inputString.substring(0, numChars);
        } else {
            return inputString;
        }
    }

    @FunctionLibraryMethod(category = "_Text", title = "SubstringMid", executionType = "SINGLE_VALUE", key = "calculate122")
    public String SubstringMid(@UDFParam(paramCategory = "Argument", title = "") int start, @UDFParam(paramCategory = "Argument", title = "") int num, @UDFParam(paramCategory = "Argument", title = "") String value, Container container) throws StreamTransformationException {
        if (value == null || value.length() < start) {
            return null;
        }
        return value.substring(start, Math.min(start + num, value.length()));
    }

    @FunctionLibraryMethod(category = "_Text", title = "SubstringRight", executionType = "SINGLE_VALUE", key = "calculate1222")
    public String SubstringRight(@UDFParam(paramCategory = "Argument", title = "") int num, @UDFParam(paramCategory = "Argument", title = "") String value, Container container) throws StreamTransformationException {
        if (value == null) {
            return null;
        }
        int length = value.length();
        if (length < num) {
            return value;
        }
        return value.substring(length - num);
    }

    @FunctionLibraryMethod(category = "_Text", title = "ConcatIf", executionType = "SINGLE_VALUE", key = "calculate12222")
    public String ConcatIf(@UDFParam(paramCategory = "Argument", title = "") String delimiter, @UDFParam(paramCategory = "Argument", title = "") String var1, @UDFParam(paramCategory = "Argument", title = "") String var2, Container container) throws StreamTransformationException {
        if (var1 == null || var1.length() == 0) {
            return var2;
        }
        if (var2 == null || var2.length() == 0) {
            return var1;
        }
        return var1 + replaceDelimitier(delimiter) + var2;
    }

    @FunctionLibraryMethod(category = "_Text", title = "ReplaceRegExp", executionType = "SINGLE_VALUE", key = "calculate1")
    public String ReplaceRegExp(@UDFParam(paramCategory = "Argument", title = "") String regExp, @UDFParam(paramCategory = "Argument", title = "") String source, @UDFParam(paramCategory = "Argument", title = "") String replacement, Container container) throws StreamTransformationException {
        /**
         * Author: Kenn Wu
         * Date: 2011-03-25
         * Description:
         * - reads Java supported regular expression
         * - reads source string
         * - reads replacement string
         * - replaces instances of regular expression in source string with replacement string
         *
         * *
         */
        AbstractTrace trace = container.getTrace();
        // Create Pattern based on input string
        Pattern p = Pattern.compile(regExp);
        // Create Matcher based on source string
        Matcher m = p.matcher(source);
        // Create StringBuffer to manipulate source string
        StringBuffer sb = new StringBuffer(999);
        // Replace instances of regular expression with replacement string
        while (m.find()) {
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        // Return result
        return sb.toString();
    }

    @FunctionLibraryMethod(category = "_Text", title = "FlattenContext", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate122222")
    public void FlattenContext(@UDFParam(paramCategory = "Argument", title = "") String[] delimiter, @UDFParam(paramCategory = "Argument", title = "") String[] values, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        if (values.length == 0) {
            return;
        }
        result.addValue(combine(values, replaceDelimitier(delimiter[0])));
    }

    @FunctionLibraryMethod(category = "_Text", title = "ParseFieldFromXmlString", executionType = "SINGLE_VALUE", key = "calculate1222222")
    public String ParseFieldFromXmlString(@UDFParam(paramCategory = "Argument", title = "") String name, @UDFParam(paramCategory = "Argument", title = "") String encoded, Container container) throws StreamTransformationException {
        if (encoded == null) {
            return null;
        }
        int start = encoded.indexOf(name);
        if (start < 0) {
            return "";
        }
        int end1 = encoded.indexOf(name, start + 1);
        //handle ns prefix too
        int end = encoded.lastIndexOf("/", end1);
        if (end < 0) {
            return "";
        }
        encoded = encoded.substring(start + name.length(), end);
        return removeXmlTagWrappers(encoded);
    }

    @FunctionLibraryMethod(category = "_Text", title = "GetDataAfterIdentifier", executionType = "SINGLE_VALUE", key = "calculate122222222")
    public String GetDataAfterIdentifier(@UDFParam(paramCategory = "Argument", title = "") String inputValue, @UDFParam(paramCategory = "Argument", title = "") String identifier, Container container) throws StreamTransformationException {
        int pos = inputValue.indexOf(identifier);
        if (pos != -1) {
            return inputValue.substring(pos + 1);
        } else {
            return null;
        }
    }

    @FunctionLibraryMethod(category = "_Text", title = "GetDataBeforeIdentifier", executionType = "SINGLE_VALUE", key = "calculate1222222222")
    public String GetDataBeforeIdentifier(@UDFParam(paramCategory = "Argument", title = "") String inputValue, @UDFParam(paramCategory = "Argument", title = "") String identifier, Container container) throws StreamTransformationException {
        int pos = inputValue.indexOf(identifier);
        if (pos != -1) {
            return inputValue.substring(0, pos);
        } else {
            return inputValue;
        }
    }

    @FunctionLibraryMethod(category = "_Text", title = "GetDataWithinIdentifier", executionType = "SINGLE_VALUE", key = "c59e8fb6-9cca-11e8-b681-88b1111eae6f")
    public String GetDataWithinIdentifier(@UDFParam(paramCategory = "Argument", title = "") String value, @UDFParam(paramCategory = "Argument", title = "") String identifier, @UDFParam(paramCategory = "Argument", title = "") int position, Container container) throws StreamTransformationException {
        /**
         * @description returns the substring between the found delimiters, as specified by the position.
         * 	position should be index 1 (as opposed to 0)
         * 	returns the original string if no delimiter found and the position is 1, or empty string for another position
         */
        if (value == null)
            return null;
        if (position < 1)
            return "";
        //convert from base 1 to base 0 index
        position--;
        if (identifier.length() == 1)
            identifier = "\\" + identifier;
        String[] arr = value.split(identifier);
        if (arr.length <= position) {
            return "";
        }
        return arr[position];
    }

    @FunctionLibraryMethod(category = "_Text", title = "RemoveLeadingZeroes", executionType = "SINGLE_VALUE", key = "calculate12222222222")
    public String RemoveLeadingZeroes(@UDFParam(paramCategory = "Argument", title = "") String value, Container container) throws StreamTransformationException {
        return value.replaceAll("^0*", "");
    }

    @FunctionLibraryMethod(category = "_Text", title = "SplitByLength", executionType = "ALL_VALUES_OF_CONTEXT", key = "calculate122222222222")
    public void SplitByLength(@UDFParam(paramCategory = "Argument", title = "") int[] length, @UDFParam(paramCategory = "Argument", title = "") String[] values, @UDFParam(paramCategory = "ResultList", title = "") ResultList result, Container container) throws StreamTransformationException {
        if (values == null || values.length == 0) {
            return;
        }
        if (values.length > 1) {
            throw new StreamTransformationException("Multi values received in single context. Can only split 1");
        }
        int lengthValue = length[0];
        if (lengthValue == 0) {
            throw new StreamTransformationException("Length parameter can not be 0");
        }
        String value = values[0];
        while (value.length() > lengthValue) {
            String tmp = value.substring(0, lengthValue);
            result.addValue(tmp);
            value = value.substring(lengthValue);
        }
        if (value.length() > 0) {
            result.addValue(value);
        }
    }
}


Header Mapping
import com.sap.it.api.mapping.*;

/*Add MappingContext parameter to read or set headers and properties
def String customFunc1(String P1,String P2,MappingContext context) {
         String value1 = context.getHeader(P1);
         String value2 = context.getProperty(P2);
         return value1+value2;
}

Add Output parameter to assign the output value.
def void custFunc2(String[] is,String[] ps, Output output, MappingContext context) {
        String value1 = context.getHeader(is[0]);
        String value2 = context.getProperty(ps[0]);
        output.addValue(value1);
        output.addValue(value2);
}*/

def String setHeader(String name, String value, MappingContext context) {
    
    if (value) {
        context.setHeader(name, value)
    }
    return value
}

def String getHeaderWithDefault(String name, String defaultValue, MappingContext context) {
    
    def value = context.getHeader(name) as String
    if (value) {
        return value
    }
    return defaultValue
}

def String getProperty(String name, MappingContext context) {
    
    def value = context.getProperty(name) as String
    if (value) {
        return value
    }
    return ""
}

def String setProperty(String name, String value, MappingContext context) {
    
    context.setProperty(name, value)
    return value
}

def String readFileName(MappingContext context) {
	
	def value = context.getProperty('FileName') as String
    if (value) {
        return value
    }
    return ""
	
}

def String writeFileName(String fileName, MappingContext context) {
    
    context.setProperty('FileName', fileName)
    return fileName
}

