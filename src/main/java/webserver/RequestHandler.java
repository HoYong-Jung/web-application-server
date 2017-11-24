package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.HttpData;
import model.User;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    
    private HttpData httpData;
    
    private User memberInfo; 
    
    private int statusCode = 200;
    
    private Map<String, String> httpParamMap;
    
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void inputStreamHandler(InputStream in, InputStreamReader isr, BufferedReader br) {
        this.httpData = new HttpData(in, isr, br);
    }
    
    public void outputStreamHandler(OutputStream out) {
        try {
        	DataOutputStream dos = new DataOutputStream(out);
        	
        	byte[] body = getViewPage(this.httpData.getUrl());
        	 
            setHeader(dos, body.length, this.httpData);
            setBody(dos, body);
            
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void setHeader(DataOutputStream dos, int lengthOfBodyContent, HttpData httpData) {
    	try {
            dos.writeBytes("HTTP/1.1 " + String.valueOf(this.statusCode) + " OK \r\n");
            dos.writeBytes(setContentType(httpData));
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    public void setBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    public String setContentType(HttpData httpdata) {
    	switch ( httpdata.getExtention() ) {
    		case "css":
    			return "Content-Type: text/css;charset=utf-8\r\n";
    		case "js":
    			return "Content-Type: text/javascript;charset=utf-8\r\n";
    		default:
    			return "Content-Type: text/html;charset=utf-8\r\n";
    	}
    }
    
    public void run() {
        log.debug("New Client Connect! Time : {}, Connected IP : {}, Port : {}", WebServer.getTime(), connection.getInetAddress(), connection.getPort());

        InputStreamReader isr = null;
        BufferedReader br = null;
        
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

        	inputStreamHandler(in, isr, br);
        	
        	log.debug("-----------------------------------");
            log.debug("http data :" + httpData.toString());
            log.debug("-----------------------------------");
            
            outputStreamHandler(out);
            
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
			if (br != null) try { br.close(); } catch(Exception e) {log.error(e.getMessage());}
			if (isr != null) try { isr.close(); } catch(Exception e) {log.error(e.getMessage());}
		}
    }

    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
    
    public byte[] defaultPage() {
    	return "Hotshot World".getBytes();
    }
  
    public byte[] getViewPage(String request) {
    	
    	if (isJoinMember(request)) {
    		
    		setQueryString(request);
    		this.statusCode = 302;
    		
    		return getByteArrayOfViewPage("/index.html");
    	} else {
    		return getByteArrayOfViewPage(request);
    	}
    }
    
    public byte[] getByteArrayOfViewPage(String request) {
    	
    	File file = new File("webapp" + request);
		FileInputStream fis = null;
		byte[] bytesArray = null;
		try {
			fis = new FileInputStream(file);
			
			//init array with file length
			bytesArray = new byte[(int) file.length()];
			//read file into bytes[]
			fis.read(bytesArray); 
			fis.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return bytesArray;
    }
    
    private void setQueryString (String request) {
    	
    	log.debug("request : " + request);
    	
    	String[] values = request.split("\\?");
    	if (values.length > 2) {
    		for (int i = 2; i < values.length; i++)
    			values[1] += values[i];
    	}
    	parserQueryString(values[1]);
    }
    
    private void parserQueryString(String queryString) {
    	if(queryString.contains("&"))
    		setHttpParams(queryString.split("&"));
    }
    
    private void getHttpParams(String query) {
    	if ( query.contains("=")) {
    		httpParamMap = new HashMap<String, String>();
    		int delimiter = query.indexOf("=");
    		httpParamMap.put(query.substring(0, (delimiter)), 
    						 query.substring((delimiter+1), query.length()));
    	}
    }
    
    private void setHttpParams(String[] querys) {
    	for(int i = 0; i < querys.length; i++) {
    		getHttpParams(querys[i]);
		}
    	
    	this.memberInfo = new User(httpParamMap.get("userId"),
    			httpParamMap.get("password"),
    			httpParamMap.get("name"),
    			httpParamMap.get("email"));
    	
    	log.debug(this.memberInfo.toString());
    }
    
    private boolean isJoinMember(String request) {
    	return request.contains("/user/create?userId=");
    }
}
