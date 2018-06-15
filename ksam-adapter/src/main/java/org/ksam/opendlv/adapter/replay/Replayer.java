package org.ksam.opendlv.adapter.replay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.ksam.opendlv.adapter.server.AdapterServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Replayer {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    private final AdapterServer adapterServer;
    private final ScheduledExecutorService schedulerV = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService schedulerA = Executors.newSingleThreadScheduledExecutor();
    // private final ScheduledExecutorService schedulerC =
    // Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService schedulerCamera = Executors.newSingleThreadScheduledExecutor();
    private Iterator<String> linesVStartAzimuth;
    private Iterator<String> linesVEndAzimuth;
    private Iterator<String> linesVFrontalD;
    private Iterator<String> linesVRearD;
    private Iterator<String> linesVRigthD;
    private Iterator<String> linesVLeftD;
    private Iterator<String> linesALatitude;
    private Iterator<String> linesALongitude;
    private Iterator<String> linesASpeed;
    private Iterator<String> linesCameraImgSize;
    private Iterator<String> linesCameraFrontal;
    // private Iterator<String> linesCSpeed;
    private Map<String, Boolean> monitorsState;

    public Replayer(AdapterServer adapterServer) {
	this.adapterServer = adapterServer;

	try {
	    linesVStartAzimuth = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-startAzimuth_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesVEndAzimuth = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-endAzimuth_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesVFrontalD = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-frontaldistance_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesVRearD = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-reardistance_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesVRigthD = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-rightdistance_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesVLeftD = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-leftdistance_replay.txt"))
		    .collect(Collectors.toList()).iterator();

	    linesALatitude = Files.lines(Paths.get("/tmp/weka/applanixGps-latitude_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesALongitude = Files.lines(Paths.get("/tmp/weka/applanixGps-longitude_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesASpeed = Files.lines(Paths.get("/tmp/weka/applanixGps-speed_replay.txt")).collect(Collectors.toList())
		    .iterator();

	    linesCameraImgSize = Files.lines(Paths.get("/tmp/weka/axiscamera-imgSize_replay.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesCameraFrontal = Files.lines(Paths.get("/tmp/weka/axiscamera-frontaldistance_replay.txt"))
		    .collect(Collectors.toList()).iterator();

	    // linesCSpeed =
	    // Files.lines(Paths.get("can-speedNoNormalized.txt")).collect(Collectors.toList()).iterator();
	    monitorsState = new HashMap<>();
	    monitorsState.put("velodyne32Lidar", true);
	    monitorsState.put("applanixGps", true);
	    monitorsState.put("axiscamera", false);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// TODO Refactor CONSTRUCT JSON code, substitute for an ObjectMapper instead for
	// constructing the json manually (error prone solution)
	/** Velodyne **/
	Runnable taskV = new Runnable() {
	    int i = 0;

	    @Override
	    public void run() {
		// READ FILE
		if (monitorsState.get("velodyne32Lidar")) {
		    String startAzimuth = linesVStartAzimuth.hasNext() ? linesVStartAzimuth.next().split(" ")[1] : "0";
		    String endAzimuth = linesVEndAzimuth.hasNext() ? linesVEndAzimuth.next().split(" ")[1] : "360";
		    String frontalD = "";

		    if (i <= 10) {
			frontalD = linesVFrontalD.hasNext() ? linesVFrontalD.next().split(" ")[1] : "0";
		    } else {
			frontalD = "-1.0";
		    }
		    i++;

		    String rearD = linesVRearD.hasNext() ? linesVRearD.next().split(" ")[1] : "0";
		    String rightD = linesVRigthD.hasNext() ? linesVRigthD.next().split(" ")[1] : "0";
		    String leftD = linesVLeftD.hasNext() ? linesVLeftD.next().split(" ")[1] : "0";

		    // CONSTRUCT JSON
		    String json = "{'systemId' : 'openDlvMonitorv0','timeStamp':'"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "',"
			    + "'context': [{'services': ['laneFollower']}],'monitors': [{'monitorId':'velodyne32Lidar','measurements': [{'varId':'startAzimuth','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + startAzimuth
			    + "'}]},{'varId':'endAzimuth','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + endAzimuth
			    + "'}]},{'varId':'frontaldistance','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + frontalD
			    + "'}]},{'varId':'rightdistance','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + rightD
			    + "'}]},{'varId':'leftdistance','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + leftD
			    + "'}]},{'varId':'reardistance','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + rearD + "'}]}]}]}";
		    // POST DATA
		    adapterServer.forwardData(json);
		}
	    }
	};
	schedulerV.scheduleAtFixedRate(taskV, 10000, 100, TimeUnit.MILLISECONDS);

	/*************************************/

	/** Applanix **/
	Runnable taskA = new Runnable() {
	    @Override
	    public void run() {
		// READ FILE
		if (monitorsState.get("applanixGps")) {
		    String latitude = linesALatitude.hasNext() ? linesALatitude.next().split(" ")[1] : "57.773239";
		    String longitude = linesALongitude.hasNext() ? linesALongitude.next().split(" ")[1] : "12.771125";
		    String speed = linesASpeed.hasNext()
			    ? String.valueOf(Double.valueOf(linesASpeed.next().split(" ")[1]) * 3600 / 1000)
			    : "0"; // speed is translated into km/h (speed*3600/1000)

		    // CONSTRUCT JSON
		    String json = "{'systemId' : 'openDlvMonitorv0','timeStamp':'"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "',"
			    + "'context': [{'services': ['laneFollower']}],'monitors': [{'monitorId':'applanixGps','measurements': [{'varId':'latitude','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + latitude
			    + "'}]},{'varId':'longitude','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + longitude
			    + "'}]},{'varId':'speed','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + speed + "'}]}]}]}";
		    // POST DATA
		    adapterServer.forwardData(json);
		}
	    }
	};
	schedulerA.scheduleAtFixedRate(taskA, 10000, 20, TimeUnit.MILLISECONDS);
	/*************************************/

	// /** CAN **/
	// Runnable taskC = new Runnable() {
	// @Override
	// public void run() {
	// // READ FILE
	// String speed = "20"; // read from file when have it
	// // CONSTRUCT JSON
	// String json = "{'systemId' : 'openDlvMonitorv0','timeStamp':'"
	// + new Timestamp(System.currentTimeMillis()).getTime() + "',"
	// + "'context': [{'services': ['laneFollower']}],'monitors':
	// [{'monitorId':'can','measurements': [{'varId':'speed','measures':
	// [{'mTimeStamp': '"
	// + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + speed
	// + "'}]}]}]}";
	// // POST DATA
	// adapterServer.forwardData(json);
	// }
	// };
	// schedulerC.scheduleAtFixedRate(taskC, 10000, 200, TimeUnit.MILLISECONDS);
	/*************************************/

	/** AxisCamera **/
	Runnable taskCamera = new Runnable() {
	    @Override
	    public void run() {
		if (monitorsState.get("axiscamera")) {
		    // READ FILE
		    String imgSize = linesCameraImgSize.hasNext() ? linesCameraImgSize.next().split(" ")[1] : "0";
		    String frontal = linesCameraFrontal.hasNext() ? linesCameraFrontal.next().split(" ")[1] : "0";
		    // CONSTRUCT JSON

		    String json = "{'systemId' : 'openDlvMonitorv0','timeStamp':'"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "',"
			    + "'context': [{'services': ['laneFollower']}],'monitors': [{'monitorId':'axiscamera','measurements': [{'varId':'frontaldistance','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + frontal
			    + "'}]},{'varId':'imgSize','measures': [{'mTimeStamp': '"
			    + new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + imgSize
			    + "'}]}]}]}";
		    // POST DATA
		    adapterServer.forwardData(json);
		}
	    }
	};
	schedulerCamera.scheduleAtFixedRate(taskCamera, 10000, 50,
		TimeUnit.MILLISECONDS);/*************************************/

    }

    public boolean adaptMonitors(String adaptation) {
	ObjectMapper mapper = new ObjectMapper();
	try {
	    MonitorAdaptation adapt = mapper.readValue(adaptation, MonitorAdaptation.class);
	    adapt.getMonitorsToAdd().forEach(m -> {
		monitorsState.put(m, true);
		LOGGER.info("Add monitor: " + m);
	    });
	    adapt.getMonitorsToRemove().forEach(m -> {
		LOGGER.info("Remove monitor: " + m);
		monitorsState.put(m, false);
	    });
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return true;
    }

}
