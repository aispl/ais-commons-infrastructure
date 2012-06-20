/*
 * Copyright (c) 2012, AIS.PL
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pl.ais.commons.infrastructure.security.permission.evaluators;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * Chained {@link PermissionEvaluator} which denies all access.
 *
 * @author Warlock, AIS.PL
 * @version $Revision:$
 */
public class DenyAllChainedPermissionEvaluator extends AbstractChainedPermissionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DenyAllChainedPermissionEvaluator.class.getName());

    /**
     * @see gov.ehawaii.dohtb.infrastructure.security.permissions.AbstractChainedPermissionEvaluator#isPermissionGranted(org.springframework.security.core.Authentication, java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isPermissionGranted(
        final Authentication authentication, final Object target, final Object permission) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Denying access for " + authentication + " to " + target + "(permission: " + permission + ")");
        }
        return false;
    }

    /**
     * @see gov.ehawaii.dohtb.infrastructure.security.permissions.AbstractChainedPermissionEvaluator#isPermissionGranted(org.springframework.security.core.Authentication, java.io.Serializable, java.lang.String, java.lang.Object)
     */
    @Override
    protected boolean isPermissionGranted(
        final Authentication authentication, final Serializable targetId, final String targetType,
        final Object permission) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Denying access for " + authentication + " to " + targetType + " having id: " + targetId
                + "(permission: " + permission + ")");
        }
        return false;
    }

    /**
     * @see gov.ehawaii.dohtb.infrastructure.security.permissions.AbstractChainedPermissionEvaluator#supportsTarget(org.springframework.security.core.Authentication, java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean supportsTarget(final Authentication authentication, final Object target, final Object permission) {
        return true;
    }

    /**
     * @see gov.ehawaii.dohtb.infrastructure.security.permissions.AbstractChainedPermissionEvaluator#supportsTargetType(org.springframework.security.core.Authentication, java.io.Serializable, java.lang.String, java.lang.Object)
     */
    @Override
    protected boolean supportsTargetType(
        final Authentication authentication, final Serializable targetId, final String targetType,
        final Object permission) {
        return true;
    }

}
