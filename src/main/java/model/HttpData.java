package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.omg.CORBA.portable.ValueOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webserver.RequestHandler;

public class HttpData {
	
	private static final Logger log = LoggerFactory.getLogger(HttpData.class);
	
	private String uriData = null;
	private String transType = null;
	private String url = null;
	private String extention = null;
	private String host = null;
	private String connection = null;
	private String cacheControl = null;
	private String userAgent = null;
	private String upgradeInsecureRequest = null;
	private String accept = null;
	private String acceptEncoding = null;
	private String acceptLanguage = null;
	
	public HttpData() {
		// TODO Auto-generated constructor stub
	}
	
	public HttpData(InputStream in, InputStreamReader isr, BufferedReader br) {
        try {
			
			isr = new InputStreamReader(in);
            br = new BufferedReader(isr);

            setUriData(br.readLine());
            log.debug("Set URI : {}", this.uriData);
            
            while( br.ready() ) {
            	setHttpData(br.readLine());
            }
            
            setUrlData(getUriData());
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void setUrlData(String urlData) {
		if (urlData.contains(" ")) {
			String[] urlDatas = urlData.split(" ");
			if (urlDatas.length == 3) {
				setTransType(urlDatas[0].trim());
				setUrl(urlDatas[1].trim());
				setExtention(urlDatas[1].trim());
			}
		}
	}
	
	public void setHttpData(String data) {
		
		log.debug("setHttpData : {}", data);
		
		
		String[] cases = {"Host:", "Connection:", "Cache-Control:","User-Agent:", "Upgrade-Insecure-Requests:",
				"Accept:", "Accept-Encoding:", "Accept-Language:", };

		int i;
		for(i = 0; i < cases.length; i++)
		    if(data.contains(cases[i])) break;

		switch(i) {
		    case 0: 
		    	setHost(dataParser(data)[1]);
		    	break;
		    case 1: 
		    	setConnection(dataParser(data)[1]);
		    	break;
		    case 2: 
		    	setCacheControl(dataParser(data)[1]);
		    	break;
		    case 3: 
		    	setUserAgent(dataParser(data)[1]);
		    	break;
		    case 4: 
		    	setUpgradeInsecureRequest(dataParser(data)[1]);
		    	break;
		    case 5: 
		    	setAccept(dataParser(data)[1]);
		    	break;
		    case 6: 
		    	setAcceptEncoding(dataParser(data)[1]);
		    	break;
		    case 7: 
		    	setAcceptLanguage(dataParser(data)[1]);
		    	break;
		}
	}
	
	private String[] dataParser(String data) {
		
		String[] result = new String[2];
		String[] values = data.split(":");
		
		if ( values.length == 2 ) {
			for ( int i = 0; i < values.length; i++) {
				result[i] = values[i].trim();
			}
		}
		else {
			for ( int i = 0; i < values.length; i++) {
				if( i == 0)
					result[0] = values[i].trim();
				else
					result[1] += ":" + values[i].trim();
			}
		}
		
		return result;
	}

	public String getUriData() {
		return uriData;
	}

	public void setUriData(String uriData) {
		this.uriData = uriData;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getExtention() {
		return extention;
	}

	public void setExtention(String url) {
		if(url.contains(".")) {
			String[] values = url.split("\\.");
			log.debug( String.valueOf(values.length));
			this.extention = values[values.length - 1].trim();
		}
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getConnection() {
		return connection;
	}
	
	public void setConnection(String connection) {
		this.connection = connection;
	}
	
	public String getCacheControl() {
		return cacheControl;
	}
	
	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getUpgradeInsecureRequest() {
		return upgradeInsecureRequest;
	}
	
	public void setUpgradeInsecureRequest(String upgradeInsecureRequest) {
		this.upgradeInsecureRequest = upgradeInsecureRequest;
	}
	
	public String getAccept() {
		return accept;
	}
	
	public void setAccept(String accept) {
		this.accept = accept;
	}
	
	public String getAcceptEncoding() {
		return acceptEncoding;
	}
	
	public void setAcceptEncoding(String acceptEncoding) {
		this.acceptEncoding = acceptEncoding;
	}
	
	public String getAcceptLanguage() {
		return acceptLanguage;
	}
	
	public void setAcceptLanguage(String acceptLanguage) {
		this.acceptLanguage = acceptLanguage;
	}	
	
	public String toString() {
		return "UriData : " + getUriData() + "\n" +
				"Transfer Type : " + getTransType() + "\n" +
				"URL : " + getUrl() + "\n" +
				"Extention : " + getExtention() + "\n" +
				"Host : " + getHost() + "\n" +
				"Connection : " + getConnection() + "\n" +
				"Cache-Control : " + getCacheControl() + "\n" +
				"User-Agent : " + getUserAgent() + "\n" +
				"Upgrade-Insecure-Requests : " + getUpgradeInsecureRequest() + "\n" +
				"Accept : " + getAccept() + "\n" +
				"Accept-Encoding : " + getAcceptEncoding() + "\n" +
				"Accept-Language : " + getAcceptLanguage();
	}
}
