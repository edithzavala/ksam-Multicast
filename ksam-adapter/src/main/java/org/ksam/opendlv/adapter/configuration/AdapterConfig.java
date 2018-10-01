package org.ksam.opendlv.adapter.configuration;

public class AdapterConfig {

    private int portOpenDLV;
    private int porKsam;
    private String systemId;
    private String experimentType;
    private boolean replay;
    private String hostKsam;

    public int getPortOpenDLV() {
	return portOpenDLV;
    }

    public void setPortOpenDLV(int portOpenDLV) {
	this.portOpenDLV = portOpenDLV;
    }

    public int getPorKsam() {
	return porKsam;
    }

    public void setPorKsam(int porKsam) {
	this.porKsam = porKsam;
    }

    public String getSystemId() {
	return systemId;
    }

    public void setSystemId(String systemId) {
	this.systemId = systemId;
    }

    public String getExperimentType() {
	return experimentType;
    }

    public void setExperimentType(String experimentType) {
	this.experimentType = experimentType;
    }

    public boolean isReplay() {
	return replay;
    }

    public void setReplay(boolean replay) {
	this.replay = replay;
    }

    public String getHostKsam() {
	return hostKsam;
    }

    public void setHostKsam(String hostKsam) {
	this.hostKsam = hostKsam;
    }

}
