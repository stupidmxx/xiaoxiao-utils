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

import hudson.model.Computer;

public interface Cache<T> {
	T getValue(Computer c);

	T getNew(Computer c);

	void clear();
}
