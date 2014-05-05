package com.ericsson.ngin.support.jenkins;

import hudson.model.Computer;
import hudson.node_monitors.AbstractNodeMonitorDescriptor;
import hudson.node_monitors.NodeMonitor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import com.ericsson.ngin.support.jenkins.util.MonitorUtils;

public class LastActiveMonitor extends NodeMonitor {

	@Override
	public Object data(Computer c) {
		return MonitorUtils.getServerStatus(c);
	}

	// @Extension
	public static final AbstractNodeMonitorDescriptor<String> DESCRIPTOR = new AbstractNodeMonitorDescriptor<String>() {
		@Override
		protected String monitor(Computer c) {
			return MonitorUtils.getServerStatus(c).toString();
		}

		@Override
		public String getDisplayName() {
			return "Last active";
		}

		@Override
		public LastActiveMonitor newInstance(StaplerRequest req,
				JSONObject formData) {
			return new LastActiveMonitor();
		}
	};

}
