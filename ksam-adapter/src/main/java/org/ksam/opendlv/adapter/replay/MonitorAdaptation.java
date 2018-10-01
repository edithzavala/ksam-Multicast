package org.ksam.opendlv.adapter.replay;

import java.util.List;
import java.util.Map;

public class MonitorAdaptation {
    private String adaptId;
    private List<String> monitorsToAdd;
    private List<String> monitorsToRemove;
    private Map<String, String> paramsToAdapt;

    public String getAdaptId() {
	return adaptId;
    }

    public void setAdaptId(String adaptId) {
	this.adaptId = adaptId;
    }

    public List<String> getMonitorsToAdd() {
	return monitorsToAdd;
    }

    public void setMonitorsToAdd(List<String> monitorsToAdd) {
	this.monitorsToAdd = monitorsToAdd;
    }

    public List<String> getMonitorsToRemove() {
	return monitorsToRemove;
    }

    public void setMonitorsToRemove(List<String> monitorsToRemove) {
	this.monitorsToRemove = monitorsToRemove;
    }

    public Map<String, String> getParamsToAdapt() {
	return paramsToAdapt;
    }

    public void setParamsToAdapt(Map<String, String> paramsToAdapt) {
	this.paramsToAdapt = paramsToAdapt;
    }

}
