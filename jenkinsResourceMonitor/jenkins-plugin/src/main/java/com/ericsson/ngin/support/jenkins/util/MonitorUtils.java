/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.ericsson.ngin.support.jenkins.util;

import hudson.model.Result;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.labels.LabelAtom;

import java.util.Date;

public class MonitorUtils {

	private static Cache<String> offlineStatusCache = new AbstractCache<String>() {
		@Override
		public String getNew(Computer c) {
			StringBuffer ret = new StringBuffer();
			if (c.isOffline()) {
				if (c.isTemporarilyOffline()) {
					// show reason
					if (c.getOfflineCause() != null) {
						String cause = c.getOfflineCause().toString();
						if (cause.startsWith("Disc")) {
							ret.append(cause.substring(16, 23));
						} else {
							ret.append("Auto down");
						}
					}
				} else {
					// show the warning of broken.
					ret.append("server is down.");
				}
			}
			return ret.toString();
		}

	};

	private static Cache<String> labelCache = new AbstractCache<String>() {
		@Override
		public String getNew(Computer c) {
			Node node = c.getNode();
			String ret = "No Label";
			for (LabelAtom label : node.getAssignedLabels()) {
				if (label.getDisplayName().contains("ecnsh")) {
					continue;
				}
				ret = label.getDisplayName();
			}
			return ret;
		}
	};

	private static Cache<ServerStatus> offDurationCache = new AbstractCache<ServerStatus>() {
		@Override
		public ServerStatus getNew(Computer c) {
			if ((c.getBuilds() == null) || (c.getBuilds().size() == 0)
					|| (c.getBuilds().getLastBuild() == null)) {
				return ServerStatus.NEVER_BUILT;
			}
			try {
				Date offTime = c.getBuilds().getFirstBuild().getTime();
				int recentFailures = 0;
				for (int i = 0; i < c.getBuilds().size(); ++i) {
					Run r = (Run) c.getBuilds().get(i);
					if ((r.getResult() != null)
							&& r.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
						offTime = r.getTime();
						recentFailures = i;
						break;
					}
				}
				return new ServerStatus(offTime, recentFailures);
			} catch (RuntimeException ex) {
				return ServerStatus.UNKNOWN;
			}
		}
	};


	public static String getOfflineBy(Computer c) {
		return offlineStatusCache.getNew(c);
	}

	public static String getLable(Computer c) {
		return labelCache.getNew(c);
	}

	public static ServerStatus getServerStatus(Computer c) {
		return offDurationCache.getValue(c);
	}

	public void clear() {
		offlineStatusCache.clear();
		labelCache.clear();
		offDurationCache.clear();
	}

}
