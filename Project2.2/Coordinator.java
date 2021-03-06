import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.sql.Timestamp;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

//My addition
import java.util.concurrent.BlockingQueue;


public class Coordinator extends Verticle {

	//Default mode: Strongly consistent. Possible values are "strong" and "causal"
	private static String consistencyType = "strong";

	/**
	 * TODO: Set the values of the following variables to the DNS names of your
	 * three dataCenter instances
	 */
	private static final String dataCenter1 = "<DNS-OF-DATACENTER-1>";
	private static final String dataCenter2 = "<DNS-OF-DATACENTER-2>";
	private static final String dataCenter3 = "<DNS-OF-DATACENTER-3>";

	BlockingQueue putreq = new ArrayBlockingQueue(1000);
	BlockingQueue getreq = new ArrayBlockingQueue(1000);
	Map<string, List<String>> putrequests = new HashMap<string, List<String>>();   

	@Override
	public void start() {
		//DO NOT MODIFY THIS
		KeyValueLib.dataCenters.put(dataCenter1, 1);
		KeyValueLib.dataCenters.put(dataCenter2, 2);
		KeyValueLib.dataCenters.put(dataCenter3, 3);
		final RouteMatcher routeMatcher = new RouteMatcher();
		final HttpServer server = vertx.createHttpServer();
		server.setAcceptBacklog(32767);
		server.setUsePooledBuffers(true);
		server.setReceiveBufferSize(4 * 1024);

		routeMatcher.get("/put", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String value = map.get("value");
				//You may use the following timestamp for ordering requests
                                final String timestamp = new Timestamp(System.currentTimeMillis() 
                                                                + TimeZone.getTimeZone("EST").getRawOffset()).toString();
				Thread t = new Thread(new Runnable() {
					public void run() {
						//TODO: Write code for PUT operation here.
						//Each PUT operation is handled in a different thread.
						//Highly recommended that you make use of helper functions.
						if(consistencyType.equals("strong")
						{
							//Acquire a lock before executing this
							//update some kind of data structure to block any get request

							putdata(dataCenter1, key,value);
							putdata(dataCenter2, key,value);
							putdata(dataCenter3, key,value);

						}
						if(consistencyType.equals("causal"))
						{
							//No lock but ensure all the put operation are executed
							putdata(dataCenter1, key,value);
							putdata(dataCenter2, key,value);
							putdata(dataCenter3, key,value);
						}
					}
				});
				t.start();
				req.response().end(); //Do not remove this
			}
		});

		routeMatcher.get("/get", new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				MultiMap map = req.params();
				final String key = map.get("key");
				final String loc = map.get("loc");
				//You may use the following timestamp for ordering requests
				final String timestamp = new Timestamp(System.currentTimeMillis() 
								+ TimeZone.getTimeZone("EST").getRawOffset()).toString();
				Thread t = new Thread(new Runnable() {
					public void run() {
						//TODO: Write code for GET operation here.
                                                //Each GET operation is handled in a different thread.
                                                //Highly recommended that you make use of helper functions.
						if(consistencyType.equals("strong")
							//block on the key if put operation is underway
							int value=getdata(dataCenter1, key);
						if(consistencyType.equals("causal"))
							//Return the lates value for the datcenter specified
							int value=getdata(dataCenter1, key);
						req.response().end(value.String()); //Praveen
					}
				});
				t.start();
			}
		});

		routeMatcher.get("/consistency", new Handler<HttpServerRequest>() {
                        @Override
                        public void handle(final HttpServerRequest req) {
                                MultiMap map = req.params();
                                consistencyType = map.get("consistency");

                                //This endpoint will be used by the auto-grader to set the 
				//consistency type that your key-value store has to support.
                                //You can initialize/re-initialize the required data structures here
                                req.response().end();
                        }
                });

		routeMatcher.noMatch(new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest req) {
				req.response().putHeader("Content-Type", "text/html");
				String response = "Not found.";
				req.response().putHeader("Content-Length",
						String.valueOf(response.length()));
				req.response().end(response);
				req.response().close();
			}
		});
		server.requestHandler(routeMatcher);
		server.listen(8080);
	}

	void putdata(String datacenterDNS, String key, String value)
	{
		KeyValueLib.PUT(String datacenterDNS, String key, String value) throws IOException

	}
	int getdata(String datacenterDNS, String key)
	{
		int value =KeyValueLib.GET(String datacenterDNS, String key) throws IOException
		return value;

	}
}
