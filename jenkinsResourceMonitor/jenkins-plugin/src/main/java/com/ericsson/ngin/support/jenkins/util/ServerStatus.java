/*------------------------------------------------------------------------------
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *----------------------------------------------------------------------------*/
package com.ericsson.ngin.support.jenkins.util;

import groovy.time.TimeCategory;
import groovy.time.TimeDuration;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class ServerStatus {

	private Date offTime;
	private int recentFailures;
	private String stringRepresent = null;

	public static ServerStatus NEVER_BUILT = new ServerStatus(
			"Never built.");
	public static ServerStatus UNKNOWN = new ServerStatus("Unknown.");

	public ServerStatus(Date offTime, int recentFailures) {
		this.offTime = offTime;
		this.recentFailures = recentFailures;
	}

	private ServerStatus(String s) {
		stringRepresent = s;
	}

	public Date getOffTime() {
		return offTime;
	}

	public String getOffDuration() {
		return toShortFormat(new Date(), getOffTime());
	}
	public int getRecentFailures() {
		return recentFailures;
	}

	@Override
	public String toString() {
		if (stringRepresent != null) {
			return stringRepresent;
		}
		return toShortFormat(new Date(), getOffTime()) + ", RecentFailures:"
				+ getRecentFailures();
	}

	public static String toShortFormat(Date date, Date offTime) {
		if ((null == date) || (null == offTime)) {
			return UNKNOWN.toString();
		}
		String ret = StringUtils.leftPad(
				String.valueOf((date.getTime() - offTime.getTime()) / 1000), 8,
				'0');
		ret += "s ~ ";
		TimeDuration minus = TimeCategory.minus(date, offTime);
		ret += new TimeDuration(minus.getDays(), minus.getHours(),
				minus.getDays() > 0 ? 0 : minus.getMinutes(), 0, 0).toString();
		return ret;
	}
}