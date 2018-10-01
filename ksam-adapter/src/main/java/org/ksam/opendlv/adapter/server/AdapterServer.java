package org.ksam.opendlv.adapter.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.ksam.opendlv.adapter.configuration.AdapterConfig;
import org.ksam.opendlv.adapter.replay.Replayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.reactivex.Observable;

public class AdapterServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdapterServer.class);
    private final String configPath = "/tmp/config/";
    private AdapterConfig config;

    private int PORT_OPENDLV;// = 8083;
    private int PORT_KSAM;// = 8090;
    private String SYSTEM_ID;// = "openDlvMonitorv0";
    private String URL_KSAM;// = "http://localhost:" + PORT_KSAM;
    private String expFile;// = "/" + SYSTEM_ID + ".json"; // /tmp/config/[CONFIG]/[SYSTEM_ID].json
    private boolean replay;// = false;

    private Replayer replayer;

    public AdapterServer(String configFile) {
	ObjectMapper mapper = new ObjectMapper();
	try {
	    String dataConfig = new String(Files.readAllBytes(Paths.get(this.configPath + configFile)));
	    this.config = mapper.readValue(dataConfig, AdapterConfig.class);
	    this.PORT_OPENDLV = this.config.getPortOpenDLV();
	    this.PORT_KSAM = this.config.getPorKsam();
	    this.SYSTEM_ID = this.config.getSystemId();
	    this.URL_KSAM = "http://" + this.config.getHostKsam() + ":" + this.PORT_KSAM;
	    this.expFile = this.configPath + this.config.getExperimentType() + "/" + this.SYSTEM_ID + ".json";
	    this.replay = this.config.isReplay();

	    String dataExp = new String(Files.readAllBytes(Paths.get(this.expFile)));
	    LOGGER.info("Susbcribe openDlvMonitor");
	    postData(dataExp, this.URL_KSAM + "/managedElement");

	    if (!this.replay) {
		(new Thread(this)).start();
	    } else {
		replayer = new Replayer(this);
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
	//
	// InputStream is = AdapterServer.class.getResourceAsStream(this.expType);
	// StringBuilder json = new StringBuilder();
	// try (Reader reader = new BufferedReader(
	// new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {
	// int c = 0;
	// while ((c = reader.read()) != -1) {
	// json.append((char) c);
	// }
	// } catch (IOException e) {
	// LOGGER.error(e.getMessage());
	// }
    }

    private void postData(String jsonData, String url) {
	HttpHeaders httpHeaders = new HttpHeaders();
	httpHeaders.set("Content-Type", "application/json");
	HttpEntity<String> httpEntity = new HttpEntity<String>(jsonData, httpHeaders);
	RestTemplate restTemplate = new RestTemplate();
	restTemplate.postForObject(url, httpEntity, String.class);
    }

    public void forwardData(String s) {
	String sJsonFormmat = s.replace('\'', '\"');
	// LOGGER.info("Forward JSON: " + sJsonFormmat);
	postData(sJsonFormmat, URL_KSAM + "/" + SYSTEM_ID + "/monitoringData");
    }

    @Override
    public void run() {
	LOGGER.info("KSAM Adapter server up");
	ServerSocket server = null;
	try {
	    server = new ServerSocket(PORT_OPENDLV);
	} catch (IOException e1) {
	    LOGGER.error(e1.getMessage());
	}
	while (true) {
	    try {
		Socket socket = server.accept();

		DataInputStream dis = new DataInputStream(socket.getInputStream());
		byte[] data = new byte[2048];
		dis.read(data);
		String s = new String(data);
		// LOGGER.info(s);

		Observable.just(s).subscribe(t -> forwardData(t));

		dis.close();
		socket.close();
	    } catch (IOException e1) {
		LOGGER.error(e1.getMessage());
	    }
	}
    }

    public Replayer getReplayer() {
	return this.replayer;
    }

    // public static void main(String args[]) throws IOException,
    // ClassNotFoundException {
    // new AdapterServer();
    // }
}
