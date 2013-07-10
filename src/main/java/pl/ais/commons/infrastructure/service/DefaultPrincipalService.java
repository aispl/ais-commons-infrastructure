package pl.ais.commons.infrastructure.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ListableBeanFactory;

import pl.ais.commons.application.service.PrincipalService;
import pl.ais.commons.application.service.PrincipalTransformer;
import pl.ais.commons.application.stereotype.ApplicationService;

/**
 * Default {@link PrincipalService} implementation.
 *
 * @author Warlock, AIS.PL
 * @since 1.0.3
 */
@ApplicationService
public class DefaultPrincipalService implements BeanFactoryAware, PrincipalService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPrincipalService.class);

    private transient ListableBeanFactory owningFactory;

    private transient Map<Class<?>, PrincipalTransformer<?>> transformers;

    /**
     * Provides possibility of automatic service initialization.
     */
    @SuppressWarnings("rawtypes")
    @PostConstruct
    protected void initialize() {
        if (null == this.transformers) {
            final Map<String, PrincipalTransformer> beanMapping = owningFactory
                .getBeansOfType(PrincipalTransformer.class);
            this.transformers = new HashMap<>(beanMapping.size());
            for (final PrincipalTransformer<?> transformer : beanMapping.values()) {
                transformers.put(transformer.getReturnType(), transformer);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Service initialized with transformers: " + transformers);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanFactory(@Nonnull final BeanFactory owningFactory) throws BeansException {

        // Owning factory can be set only once, ...
        if (null != this.owningFactory) {
            throw new BeanInitializationException("Bean factory already initialized.");
        }

        // ... we do require that the factory has to be at least of type ListableBeanFactory
        if (owningFactory instanceof ListableBeanFactory) {
            this.owningFactory = (ListableBeanFactory) owningFactory;
        } else {
            throw new BeanInitializationException(
                "At least ListableBeanFactory is required for proper functioning of this service.");
        }
    }

    /**
     * Determines the mapping between the type being principal representation, and the principal transformer which will
     * be used for converting principal to this type.
     *
     * @param transformers the transformers to set
     */
    public void setTransformers(@Nonnull final Map<Class<?>, PrincipalTransformer<?>> transformers) {
        if (null == transformers) {
            throw new IllegalArgumentException("Transformer map cannot be null");
        }
        this.transformers = new HashMap<>(transformers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(final Principal principal, final Class<T> asClass) {

        // Verify if we have appropriate transformer for the desired class, ...
        final PrincipalTransformer<?> transformer = transformers.get(asClass);
        if (null == transformer) {
            throw new IllegalArgumentException("Unable to transform given principal: " + principal + " into '"
                + asClass + "' - please provide method for transforming it.");
        }

        // ... apply transformation to the principal, ...
        final T result = (T) transformer.apply(principal);

        // ... verify and return the result.
        if (null == result) {
            throw new IllegalArgumentException("Transformation of given principal: " + principal + " into '" + asClass
                + "' led to null result.");
        }
        return result;
    }

}
