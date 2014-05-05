/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.ericsson.ngin.support.jenkins.builders;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Computer;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;

import com.ericsson.ngin.support.jenkins.util.MonitorUtils;
import com.ericsson.ngin.support.jenkins.util.Pair;
import com.ericsson.ngin.support.jenkins.util.ServerStatus;

public class SlaveOfflineStatusScanningBuilder extends Builder {

	// Fields in config.jelly must match the parameter names in the
	// "DataBoundConstructor"
	@DataBoundConstructor
	public SlaveOfflineStatusScanningBuilder(boolean generateCsv) {
		this.generateCsv = generateCsv;
	}

	public final boolean generateCsv;

	private static String CSV_TITLE_LINE = "Slave, Status, Label, Contact, LastestActivity, RecentFailureNumber, Reason\n";
	@Override
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) {
		PrintStream log = listener.getLogger();
		// This is what will be executed when the job is build.
		// This also shows how you can use listener and build.

		// Will be seen in the Jenkins Console output
		log(log, "Start scanning...");

		HashMap<String, Pair<ServerStatus, String>> map = new HashMap<String, Pair<ServerStatus, String>>();
		StringBuffer csvContent = new StringBuffer(CSV_TITLE_LINE);
		for (Computer computer : Jenkins.getInstance().getComputers()) {
			if (computer.getNode() instanceof Jenkins) {
				continue;// ignore master
			}
			String slaveName = computer.getName();
			boolean isOff = computer.isOffline();
			log(log, "Determining " + slaveName);
			ServerStatus serverStatus = MonitorUtils.getServerStatus(computer);
			String offlinedBy = MonitorUtils.getOfflineBy(computer);
			String label = MonitorUtils.getLable(computer);
			log(log,
					slaveName
							+ " had no activity for "
							+ serverStatus.getOffDuration()
							+ ". Recent build failed for "
							+ serverStatus.getRecentFailures()
							+ " times."
							+ (offlinedBy.isEmpty() ? "" : (" Offlined by "
									+ offlinedBy + ".")));
			Pair<ServerStatus, String> pair = new Pair<ServerStatus, String>(
					serverStatus, offlinedBy);
			map.put(slaveName, pair);
			csvContent.append(slaveName + ',' + (isOff ? "OFF" : "ON") + ','
					+ label + ',' + offlinedBy + ','
					+ serverStatus.getOffDuration().replace(',', ' ') + ','
					+ serverStatus.getRecentFailures() + ','
					+ computer.getOfflineCause() + '\n');
		}
		log(log, "Scanning finished successfully.");

		generateReport(build, log, csvContent);
		return true;
	}

	private void generateReport(AbstractBuild build, PrintStream log,
			StringBuffer csvContent) {
		if (!isGenerateCsv()) {
			return;
		}
		log(log, "Going to generate report file.");
		FilePath fp = build.getWorkspace().child("report.csv");
		try {
			if (fp.exists()) {
				fp.delete();
			}
			fp.write(csvContent.toString(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			log(log,
					"Failed to save to csv file due to "
							+ e.getLocalizedMessage());
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
			log(log,
					"Failed to save to csv file due to "
							+ e.getLocalizedMessage());
			return;
		}
		log(log, "Report file generated successfully. ");
	}

	private static void log(PrintStream log, String s) {
		log.println(new Date() + " : " + s);
	}

	public boolean isGenerateCsv() {
		return generateCsv;
	}

	// Overridden for better type safety.
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	// This indicates to Jenkins that this is an implementation of an extension
	// point.
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Builder> {
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project
			// types
			return true;
		}

		// This human readable name is used in the configuration screen.
		@Override
		public String getDisplayName() {
			return "OfflinedSlavesScanner";
		}
	}

}
