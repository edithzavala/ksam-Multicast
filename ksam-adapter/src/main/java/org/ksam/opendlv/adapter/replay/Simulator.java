package org.ksam.opendlv.adapter.replay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.ksam.opendlv.adapter.server.AdapterServer;

public class Simulator {
    private final AdapterServer adapterServer;
    private final ScheduledExecutorService schedulerV = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService schedulerA = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService schedulerC = Executors.newSingleThreadScheduledExecutor();
    private Iterator<String> linesVStartAzimuth;
    private Iterator<String> linesVEndAzimuth;
    private Iterator<String> linesALatitude;
    private Iterator<String> linesALongitude;
    // private Iterator<String> linesCSpeed;

    public Simulator(AdapterServer adapterServer) {
	this.adapterServer = adapterServer;

	try {
	    linesVStartAzimuth = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-startAzimuth.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesVEndAzimuth = Files.lines(Paths.get("/tmp/weka/velodyne32Lidar-endAzimuth.txt"))
		    .collect(Collectors.toList()).iterator();
	    linesALatitude = Files.lines(Paths.get("/tmp/weka/applanixGps-latitude.txt")).collect(Collectors.toList())
		    .iterator();
	    linesALongitude = Files.lines(Paths.get("/tmp/weka/applanixGps-longitude.txt")).collect(Collectors.toList())
		    .iterator();
	    // linesCSpeed =
	    // Files.lines(Paths.get("can-speedNoNormalized.txt")).collect(Collectors.toList()).iterator();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	/** Velodyne **/
	Runnable taskV = new Runnable() {
	    @Override
	    public void run() {
		// READ FILE
		String startAzimuth = linesVStartAzimuth.hasNext() ? linesVStartAzimuth.next().split(" ")[1] : "0";
		String endAzimuth = linesVEndAzimuth.hasNext() ? linesVEndAzimuth.next().split(" ")[1] : "360";
		// CONSTRUCT JSON
		String json = "{'systemId' : 'openDlvMonitorv0','timeStamp':'"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "',"
			+ "'context': [{'services': ['laneFollower']}],'monitors': [{'monitorId':'velodyne32Lidar','measurements': [{'varId':'startAzimuth','measures': [{'mTimeStamp': '"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + startAzimuth
			+ "'}]},{'varId':'endAzimuth','measures': [{'mTimeStamp': '"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + endAzimuth + "'}]}]}]}";
		// POST DATA
		adapterServer.forwardData(json);
	    }
	};
	schedulerV.scheduleAtFixedRate(taskV, 10000, 100, TimeUnit.MILLISECONDS);
	/*************************************/

	/** Applanix **/
	Runnable taskA = new Runnable() {
	    @Override
	    public void run() {
		// READ FILE
		String latitude = linesALatitude.hasNext() ? linesALatitude.next().split(" ")[1] : "57.770000";
		String longitude = linesALongitude.hasNext() ? linesALongitude.next().split(" ")[1] : "12.768000";
		// CONSTRUCT JSON
		String json = "{'systemId' : 'openDlvMonitorv0','timeStamp':'"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "',"
			+ "'context': [{'services': ['laneFollower']}],'monitors': [{'monitorId':'applanixGps','measurements': [{'varId':'latitude','measures': [{'mTimeStamp': '"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + latitude
			+ "'}]},{'varId':'longitude','measures': [{'mTimeStamp': '"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + longitude + "'}]}]}]}";
		// POST DATA
		adapterServer.forwardData(json);
	    }
	};
	schedulerA.scheduleAtFixedRate(taskA, 10000, 20, TimeUnit.MILLISECONDS);
	/*************************************/

	/** CAN **/
	Runnable taskC = new Runnable() {
	    @Override
	    public void run() {
		// READ FILE
		String speed = "20"; // read from file when have it
		// CONSTRUCT JSON
		String json = "{'systemId' : 'openDlvMonitorv0','timeStamp':'"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "',"
			+ "'context': [{'services': ['laneFollower']}],'monitors': [{'monitorId':'can','measurements': [{'varId':'speed','measures': [{'mTimeStamp': '"
			+ new Timestamp(System.currentTimeMillis()).getTime() + "','value':'" + speed + "'}]}]}]}";
		// POST DATA
		adapterServer.forwardData(json);
	    }
	};
	schedulerC.scheduleAtFixedRate(taskC, 10000, 200, TimeUnit.MILLISECONDS);
	/*************************************/

    }

}
