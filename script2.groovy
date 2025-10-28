Pipeline

throw exception

import com.sap.gateway.ip.core.customdev.util.Message

def Message processData(Message message) {

    // get exception message
    def propertyMap = message.getProperties()
    String exceptionMessage = propertyMap.get("exceptionMessage");

    throw new Exception(exceptionMessage);

    return message;
}

set userproperty to header 
import com.sap.gateway.ip.core.customdev.util.Message

def Message processData(Message message) {
    // 1. Get the header value
    def headers = message.getHeaders()
    def userPropertiesString = headers.get("UserProperties")
    try {
        if (userPropertiesString instanceof Map) {
            // 2. Iterate over each key-value pair in the parsed Map
            userPropertiesString.each { key, value ->
                // 3. Set each key-value pair as a new message header
                def newHeaderName = key.toString() // Ensure key is a string
                def newHeaderValue = value != null ? value.toString() : "" // Ensure value is a string, handle null

                message.setHeader(newHeaderName, newHeaderValue)
            }
        }
    } catch (Exception e) {
        throw new Exception("Error setting UserProperties to Header")
    }

    return message
}

Set IDOC senderInterface 
import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {
    // headers
    def map = message.getHeaders()

    // Set sender interface name
    message.setHeader("SAP_SenderInterface", map.get("SAP_IDoc_EDIDC_MESTYP") + "." + map.get("SAP_IDoc_EDIDC_IDOCTYP") + (map.get("SAP_IDoc_EDIDC_CIMTYP") ? "." +map.get("SAP_IDoc_EDIDC_CIMTYP") : ""))

    return message;
}

reciver determination
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.pd.PartnerDirectoryService;
import com.sap.it.api.ITApiFactory;

def Message processData(Message message) {
    
    def service = ITApiFactory.getApi(PartnerDirectoryService.class, null); 
    if (service == null){
        throw new IllegalStateException("Partner Directory Service not found");
    }
    
    // get pid
    def headers = message.getHeaders();
    def Pid = headers.get("partnerID");
    if (Pid == null){
        throw new IllegalStateException("Partner ID not found in sent message");   
    }

    // read the extended receiver determination end point from the Partner Directory
    def reuseXRDEndpoint = service.getParameter("ReuseXRDEndpoint", Pid , String.class);
    
    // if the extended receiver determination end point exists, create a new exchange property containing the end point
    // otherwise, the exchange property is not created
    message.setProperty("reuseXRDEndpoint", reuseXRDEndpoint ?: null);
    
    // create a new exchange property with value true or false depending on whether the end point exists
    message.setProperty("reuseXRDBoolean", reuseXRDEndpoint ? 'true' : 'false');
    
    return message;
}


remove leading zero
import com.sap.gateway.ip.core.customdev.util.Message;

def Message processData(Message message) {
    def headers = message.getHeaders()
    def applicationId = headers.get('SAP_ApplicationID')

    if (applicationId != null){
        message.setHeader('SAP_ApplicationID', applicationId.replaceFirst("^0+", ""))
    }
    
    return message
}


import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.pd.PartnerDirectoryService;
import com.sap.it.api.ITApiFactory;
import src.main.resources.script.PipelineLogger;

def Message processData(Message message) {
    
    def service = ITApiFactory.getApi(PartnerDirectoryService.class, null); 
    if (service == null){
        throw new IllegalStateException("Partner Directory Service not found");
    }
    
    // get pid
    def headers = message.getHeaders();
    def Pid = headers.get("partnerID");
    if (Pid == null){
        throw new IllegalStateException("Partner ID not found in sent message");   
    }

    // read retry handling from the Partner Directory
    def maxJMSRetries = service.getParameter("MaxJMSRetries", Pid , String.class);
    
    // if the value exists in the Partner Directory, create a new header holding the max number of retries
    // otherwise, set the header to default which is 5
    message.setHeader("maxJMSRetries", maxJMSRetries ? maxJMSRetries.toInteger() : 5);

	// adding information to audit log using helper PipelineLogger class
	PipelineLogger logger = PipelineLogger.newLogger(message)
	def logEntry = new StringBuilder().append("Partner ID to read the retry handling: ").append(Pid ?: "n/a").toString();
	logger.addEntry("readRetryHandlingFromPD", logEntry);
	logEntry = new StringBuilder().append("Maximum number of retries from the Partner Directory: header maxJMSRetries=").append(message.headers.maxJMSRetries.toString() ?: "n/a").toString();
	logger.addEntry("readRetryHandlingFromPD", logEntry);
	if (logger.getAuditLog()) {
		message.setHeader('auditLogHeader', logger.getAuditLog())
	}
    
    return message;
}

read reciver specific queue 

import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.pd.PartnerDirectoryService;
import com.sap.it.api.ITApiFactory;
import src.main.resources.script.PipelineLogger;

def Message processData(Message message) {
    
    def service = ITApiFactory.getApi(PartnerDirectoryService.class, null); 
    if (service == null){
        throw new IllegalStateException("Partner Directory Service not found");
    }
    
    // get pid (pid equals SAP_ReceiverAlias)
    def headers = message.getHeaders();
    def Pid = headers.get("SAP_ReceiverAlias");
    // throw exception if pid is missing
    if (Pid == null){
        throw new IllegalStateException("Partner ID not found in sent message");   
    }
    
    // read the receiver-specific JMS queue from the Partner Directory
    def receiverSpecificQueue = service.getParameter("ReceiverSpecificQueue", Pid , String.class);
    
    // if the receiver-specific queue exists, create a new exchange property containing the queue name
    // otherwise, the exchange property is not created
    message.setProperty("receiverSpecificQueueName", receiverSpecificQueue ?: null);
    
    // create a new exchange property with value true or false depending on whether the queue exists
    message.setProperty("receiverSpecificQueueBoolean", receiverSpecificQueue ? 'true' : 'false');

	// adding information to audit log using helper PipelineLogger class
	PipelineLogger logger = PipelineLogger.newLogger(message)
	def logEntry = new StringBuilder().append("Header SAP_ReceiverAlias=").append(message.headers.SAP_ReceiverAlias ?: "n/a").toString();
	logger.addEntry("readReceiverSpecificQueueFromPD", logEntry);
	logEntry = new StringBuilder().append("PID to read the receiver-specific JMS queue from the Partner Directory: ").append(Pid ?: "n/a").toString();
	logger.addEntry("readReceiverSpecificQueueFromPD", logEntry);
	logEntry = new StringBuilder().append("Receiver specific queue from the Partner Directory: property receiverSpecificQueueName=").append(message.properties.receiverSpecificQueueName.toString() ?: "n/a").toString();
	logger.addEntry("readReceiverSpecificQueueFromPD", logEntry);
	logEntry = new StringBuilder().append("Receiver specific queue existence from the Partner Directory: property receiverSpecificQueueBoolean=").append(message.properties.receiverSpecificQueueBoolean.toString() ?: "n/a").toString();
	logger.addEntry("readReceiverSpecificQueueFromPD", logEntry);
	if (logger.getAuditLog()) {
		message.setHeader('auditLogHeader', logger.getAuditLog())
	}

    return message;
}


read inbound queue
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.pd.PartnerDirectoryService;
import com.sap.it.api.ITApiFactory;
import src.main.resources.script.PipelineLogger;

def Message processData(Message message) {
    
    def service = ITApiFactory.getApi(PartnerDirectoryService.class, null); 
    if (service == null){
        throw new IllegalStateException("Partner Directory Service not found");
    }
    
    // get pid
    def headers = message.getHeaders();
    def Pid = headers.get("partnerID");
    if (Pid == null){
        throw new IllegalStateException("Partner ID not found in sent message");   
    }

    // read the inbound queue from the Partner Directory
    def inbQueue = service.getParameter("InboundQueue", Pid , String.class);
    
    // if the inbound queue exists, create a new exchange property containing the queue name
    // otherwise, the exchange property is not created
    message.setProperty("inbQueue", inbQueue ?: null);
    
	// adding information to audit log using helper PipelineLogger class
	PipelineLogger logger = PipelineLogger.newLogger(message)
	def logEntry = new StringBuilder().append("Partner ID to read the inbound queue: ").append(Pid ?: "n/a").toString();
	logger.addEntry("readInboundQueueFromPD", logEntry);
	logEntry = new StringBuilder().append("Inbound queue from the Partner Directory: property inbQueue=").append(message.properties.inbQueue.toString() ?: "n/a").toString();
	logger.addEntry("readInboundQueueFromPD", logEntry);
	if (logger.getAuditLog()) {
		message.setHeader('auditLogHeader', logger.getAuditLog())
	}

    return message;
}

read inbound conversion
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.pd.PartnerDirectoryService;
import com.sap.it.api.ITApiFactory;
import src.main.resources.script.PipelineLogger;

def Message processData(Message message) {
    
    def service = ITApiFactory.getApi(PartnerDirectoryService.class, null); 
    if (service == null){
        throw new IllegalStateException("Partner Directory Service not found");
    }
    
    // get pid
    def headers = message.getHeaders();
    def Pid = headers.get("partnerID");
    if (Pid == null){
        throw new IllegalStateException("Partner ID not found in sent message");   
    }

    // read the inbound conversion end point from the Partner Directory
    def inbConvEndpoint = service.getParameter("InboundConversionEndpoint", Pid , String.class);
    
    // if the inbound conversion end point exists, create a new exchange property containing the end point
    // otherwise, the exchange property is not created
    message.setProperty("inbConvEndpoint", inbConvEndpoint ?: null);
    
    // create a new exchange property with value true or false depending on whether the end point exists
    message.setProperty("inbConvBoolean", inbConvEndpoint ? 'true' : 'false');

	// adding information to audit log using helper PipelineLogger class
	PipelineLogger logger = PipelineLogger.newLogger(message)
	def logEntry = new StringBuilder().append("Partner ID to read the inbound conversion: ").append(Pid ?: "n/a").toString();
	logger.addEntry("readInboundConversionFromPD", logEntry);
	logEntry = new StringBuilder().append("Inbound conversion end point from the Partner Directory: property inbConvEndpoint=").append(message.properties.inbConvEndpoint.toString() ?: "n/a").toString();
	logger.addEntry("readInboundConversionFromPD", logEntry);
	logEntry = new StringBuilder().append("Inbound conversion existence from the Partner Directory: property inbConvBoolean=").append(message.properties.inbConvBoolean.toString() ?: "n/a").toString();
	logger.addEntry("readInboundConversionFromPD", logEntry);
	if (logger.getAuditLog()) {
		message.setHeader('auditLogHeader', logger.getAuditLog())
	}

    return message;
}


pipeline logger

package src.main.resources.script

import com.sap.gateway.ip.core.customdev.util.Message
import org.apache.camel.Exchange
import org.apache.camel.builder.SimpleBuilder

class PipelineLogger {
    final List entries
    final String logLevel
	final String iFlow

    static PipelineLogger newLogger(Message message) {
        return new PipelineLogger(message)
    }

    private PipelineLogger() {}

    private PipelineLogger(Message message) {
        this.entries = []
		def headers = message.getHeaders()
		if (headers.find { it.toString().contains('auditLogHeader') }) {
			this.entries.add(headers.get('auditLogHeader'))
		}		
	//	this.logLevel = message.properties.SAP_MessageProcessingLogConfiguration.logLevel.toString()
		Exchange ex = message.exchange
		this.iFlow = SimpleBuilder.simple('${camelId}').evaluate(ex, String)
    }

    void addEntry(String location, String entry) {
    //    if (this.logLevel in ['DEBUG', 'TRACE'] || this.entries.size()) {
			def timeNow=new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSXXX",TimeZone.getTimeZone('GMT'))
	//		this.entries.add("[" + this.logLevel + "]" + timeNow + "|" + this.iFlow + "|" + location + "|" + entry)
			this.entries.add(timeNow + "|" + this.iFlow + "|" + location + "|" + entry)
	//	}
    }
    
    String getAuditLog() {
        StringBuilder sb = new StringBuilder()
		if (this.entries.size()) {
            this.entries.each { sb << it + ";;" }
        }
		sb.toString()
    }
	
    String getAuditLog4Print() {
        this.getAuditLog().replaceAll(';;', '\n')
    }
}


map reciver alias to business system
import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.pd.PartnerDirectoryService;
import com.sap.it.api.ITApiFactory;
import src.main.resources.script.PipelineLogger;

def Message processData(Message message) {
    
    // get headers
    def headers = message.getHeaders();
    
    // Fetch receiver alias
    String ReceiverAlias = headers.get("SAP_ReceiverAlias");

    def service = ITApiFactory.getApi(PartnerDirectoryService.class, null); 
        if (service == null){
            throw new IllegalStateException("Partner Directory Service not found");
    }

    // Map receiver alias to business name if maintained otherwise keep alias    
    String Agency = headers.get("tenantStage") ?: "PRD";
    String Scheme = 'BusinessSystemName';
    String Pid = ReceiverAlias;

    String Receiver = service.getAlternativePartnerId(Agency, Scheme, Pid) ?: Pid;
    message.setHeader("SAP_Receiver", Receiver);
    
    // enhance custom header properties if receiver alias differs from actual receiver
    if (Receiver != ReceiverAlias) {
        String CustomHeaders = headers.get("customHeaderProperties");
        CustomHeaders = CustomHeaders ? CustomHeaders + '|' : '';
        CustomHeaders = CustomHeaders + 'SAP_ReceiverAlias' + ':' + ReceiverAlias;
        message.setHeader("customHeaderProperties", CustomHeaders);
    }

	// adding information to audit log using helper PipelineLogger class
    PipelineLogger logger = PipelineLogger.newLogger(message)
    def logEntry = new StringBuilder().append("Tenant Stage=").append(Agency ?: "n/a").toString();
	logger.addEntry("mapReceiverToBusinessName", logEntry);
    logEntry = new StringBuilder().append("Receiver Alias=").append(Pid ?: "n/a").toString();
	logger.addEntry("mapReceiverToBusinessName", logEntry);
    logEntry = new StringBuilder().append("Business System Name=").append(headers.get("SAP_Receiver") ?: "n/a").toString();
	logger.addEntry("mapReceiverToBusinessName", logEntry);
    if (logger.getAuditLog()) {
	    message.setHeader('auditLogHeader', logger.getAuditLog())
    }

    return message;
}