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

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * Base class for permission evaluators which can be chained together.
 *
 * @author Warlock, AIS.PL
 * @since 1.0
 * @see <a href="http://en.wikipedia.org/wiki/Chain-of-responsibility_pattern">Chain-of-responsibility pattern</a>
 */
public abstract class AbstractChainedPermissionEvaluator implements PermissionEvaluator {

    private transient PermissionEvaluator nextEvaluator;

    /**
     * @see org.springframework.security.access.PermissionEvaluator#hasPermission(org.springframework.security.core.Authentication, java.lang.Object, java.lang.Object)
     */
    @Override
    public final boolean hasPermission(final Authentication authentication, final Object target, final Object permission) {
        boolean result = false;
        if (supportsTarget(authentication, target, permission)) {
            result = isPermissionGranted(authentication, target, permission);
        } else if (null != nextEvaluator) {
            result = nextEvaluator.hasPermission(authentication, target, permission);
        }
        return result;
    }

    /**
     * @see org.springframework.security.access.PermissionEvaluator#hasPermission(org.springframework.security.core.Authentication, java.io.Serializable, java.lang.String, java.lang.Object)
     */
    @Override
    public final boolean hasPermission(
        final Authentication authentication, final Serializable targetId, final String targetType,
        final Object permission) {
        boolean result = false;
        if (supportsTargetType(authentication, targetId, targetType, permission)) {
            result = isPermissionGranted(authentication, targetId, targetType, permission);
        } else if (null != nextEvaluator) {
            result = nextEvaluator.hasPermission(authentication, targetId, targetType, permission);
        }
        return result;
    }

    protected abstract boolean isPermissionGranted(
        final Authentication authentication, final Object target, final Object permission);

    protected abstract boolean isPermissionGranted(
        final Authentication authentication, final Serializable targetId, final String targetType,
        final Object permission);

    /**
     * @param nextEvaluator the nextEvaluator to set
     */
    public void setNextEvaluator(final PermissionEvaluator nextEvaluator) {
        Assert.notNull(nextEvaluator, "Given evaluator cannot be null");
        this.nextEvaluator = nextEvaluator;
    }

    protected abstract boolean supportsTarget(Authentication authentication, Object target, Object permission);

    protected abstract boolean supportsTargetType(
        Authentication authentication, Serializable targetId, String targetType, Object permission);

}
