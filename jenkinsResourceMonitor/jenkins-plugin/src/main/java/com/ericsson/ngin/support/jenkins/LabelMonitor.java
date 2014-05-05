package com.ericsson.ngin.support.jenkins;

import hudson.Extension;
import hudson.model.Computer;
import hudson.node_monitors.AbstractNodeMonitorDescriptor;
import hudson.node_monitors.NodeMonitor;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import com.ericsson.ngin.support.jenkins.util.MonitorUtils;

public class LabelMonitor extends NodeMonitor {

	@Override
	public Object data(Computer c) {
		return MonitorUtils.getLable(c);
	}

	@Extension
	public static final AbstractNodeMonitorDescriptor<String> DESCRIPTOR = new AbstractNodeMonitorDescriptor<String>() {
		@Override
		protected String monitor(Computer c) throws IOException,
				InterruptedException {
			return MonitorUtils.getLable(c);
		}

		@Override
		public String getDisplayName() {
			return "Label";
		}

		@Override
		public LabelMonitor newInstance(StaplerRequest req,
				JSONObject formData) {
			return new LabelMonitor();
		}
	};

}
