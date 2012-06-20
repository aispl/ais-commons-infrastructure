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
