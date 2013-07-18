package pl.ais.commons.infrastructure.principal.unwrapping;

import static com.google.common.base.Objects.toStringHelper;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import pl.ais.commons.application.feature.FeaturesHolder;
import pl.ais.commons.application.feature.FeaturesManager;
import pl.ais.commons.application.service.PrincipalTransformer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * {@linkplain Principal} to {@linkplain FeaturesHolder} transformer.
 *
 * @author Warlock, AIS.PL
 * @since 1.0
 */
public class PrincipalToFeaturesHolderTransformer implements ApplicationContextAware, InitializingBean,
    PrincipalTransformer<FeaturesHolder> {

    private static final Logger LOG = LoggerFactory.getLogger(PrincipalToFeaturesHolderTransformer.class);

    private transient GrantedAuthority anonymous;

    private transient ApplicationContext context;

    private transient ImmutableMap<GrantedAuthority, Set<Class<?>>> featuresMap = ImmutableMap.of();

    private void addFeature(final FeaturesManager manager, final Class<?> feature) {
        final Map<String, ?> handlersMap = context.getBeansOfType(feature);
        if (handlersMap.isEmpty()) {
            manager.addVirtualFeature(feature);
        } else {
            manager.addFeature(feature, handlersMap.values().iterator().next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws BeanInitializationException {
        if (null == anonymous) {
            throw new BeanInitializationException("Please, provide authority granted for anonymous user.");
        }
        if (null == context) {
            throw new BeanInitializationException("Please, provide the application context.");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Principal -> FeaturesHolder transformer initialized as: " + this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeaturesHolder apply(@Nullable final Principal principal) {
        final FeaturesHolder result;
        if (principal instanceof Authentication) {
            result = toFeaturesHolder(((Authentication) principal).getAuthorities());
        } else {
            result = toFeaturesHolder(ImmutableList.of(anonymous));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends FeaturesHolder> getReturnType() {
        return FeaturesHolder.class;
    }

    /**
     * Defines the authority granted for anonymous user.
     *
     * @param authority the authority to use
     */
    public void setAnonymousAuthority(final GrantedAuthority authority) {
        this.anonymous = authority;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }

    /**
     * Defines the mapping between authority and features.
     *
     * @param featuresMap the mapping to set
     */
    public void setFeaturesMap(final Map<GrantedAuthority, Set<Class<?>>> featuresMap) {
        this.featuresMap = ImmutableMap.copyOf(featuresMap);
    }

    private FeaturesHolder toFeaturesHolder(final Collection<? extends GrantedAuthority> authorities) {
        final FeaturesManager result = new FeaturesManager();
        for (final GrantedAuthority authority : authorities) {
            final Collection<Class<?>> features = featuresMap.get(authority);
            if (null != features) {
                for (final Class<?> feature : features) {
                    if (result.hasFeature(feature)) {
                        continue;
                    }
                    addFeature(result, feature);
                }
            }
        }
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toStringHelper(this).add("anonymous", anonymous).add("featuresMap", featuresMap).toString();
    }

}
