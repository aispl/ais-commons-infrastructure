package pl.ais.commons.infrastructure.principal.unwrapping;

import java.beans.Introspector;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ClassUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Utility class for assembling {@linkplain PrincipalToFeaturesHolderTransformer}.
 *
 * @author Warlock, AIS.PL
 * @since 1.0.2
 */
public final class PrincipalToFeaturesHolderTransformerAssembler {

    private static final ThreadLocal<PrincipalToFeaturesHolderTransformerAssembler> STORAGE = new ThreadLocal<>();

    /**
     * Provides assembler instance usable for given transformer.
     *
     * @param transformer the transformer
     * @return assembler instance usable for given transformer
     */
    public static PrincipalToFeaturesHolderTransformerAssembler given(
        final PrincipalToFeaturesHolderTransformer transformer) {

        // Create the assembler instance, ...
        final PrincipalToFeaturesHolderTransformerAssembler assembler = new PrincipalToFeaturesHolderTransformerAssembler(
            transformer);

        // ... put it into the storage for future reference, and return to the caller.
        STORAGE.set(assembler);
        return assembler;
    }

    /**
     * Finalizes assembly of given {@linkplain PrincipalToFeaturesHolderTransformer}.
     *
     * @return assembled transformer instance
     */
    public static PrincipalToFeaturesHolderTransformer when() {

        // Fetch the assembler from the storage, and verify method requirements, ...
        final PrincipalToFeaturesHolderTransformerAssembler assembler = STORAGE.get();
        if (null == assembler) {
            throw new IllegalStateException(
                "There is no assembler for current thread, have you called 'given' method first?");
        }

        // ... remove the assembler from the storage, finalize assembling, and return assembled instance.
        STORAGE.remove();
        return assembler.assemble();
    }

    private transient GrantedAuthority anonymous;

    private transient ApplicationContext context;

    private transient final ImmutableMap.Builder<GrantedAuthority, Set<Class<?>>> featuresBuilder = ImmutableMap
        .builder();

    private transient final PrincipalToFeaturesHolderTransformer transformer;

    /**
     * Constructs new instance.
     */
    private PrincipalToFeaturesHolderTransformerAssembler(final PrincipalToFeaturesHolderTransformer transformer) {
        super();
        this.context = new StaticApplicationContext();
        this.transformer = transformer;
    }

    private PrincipalToFeaturesHolderTransformer assemble() {
        transformer.setAnonymousAuthority(anonymous);
        transformer.setApplicationContext(context);
        transformer.setFeaturesMap(featuresBuilder.build());
        transformer.afterPropertiesSet();
        return transformer;
    }

    /**
     * Defines the authority granted for anonymous user.
     *
     * @see PrincipalToFeaturesHolderTransformer#setAnonymousAuthority(GrantedAuthority)
     * @param authority the anonymous authority to use
     * @return assembler instance (for method invocation chaining)
     */
    public PrincipalToFeaturesHolderTransformerAssembler withAnonymousAuthority(final GrantedAuthority authority) {
        this.anonymous = authority;
        return this;
    }

    /**
     * Defines the application context that the assembled transformer runs in.
     *
     * @see PrincipalToFeaturesHolderTransformer#setApplicationContext(ApplicationContext)
     * @param context the application context
     * @return assembler instance (for method invocation chaining)
     */

    public PrincipalToFeaturesHolderTransformerAssembler withApplicationContext(
        @SuppressWarnings("hiding") final ApplicationContext context) {
        this.context = context;
        return this;
    }

    /**
     * Defines the application features corresponding to specified authority.
     *
     * @see PrincipalToFeaturesHolderTransformer#setFeaturesMap(java.util.Map)
     * @param authority the authority
     * @param features the features available for the authority
     * @return assembler instance (for method invocation chaining)
     */
    public PrincipalToFeaturesHolderTransformerAssembler withAuthorityFeatures(
        final GrantedAuthority authority, final Class<?>... features) {
        featuresBuilder.put(authority, ImmutableSet.copyOf(features));
        return this;
    }

    /**
     * Provides the command handlers which should be registered in application context.
     *
     * @see PrincipalToFeaturesHolderTransformer#setApplicationContext(ApplicationContext)
     * @param commandHandlers the command handlers to register
     * @return assembler instance (for method invocation chaining)
     */
    public PrincipalToFeaturesHolderTransformerAssembler withCommandHandlers(final Class<?>... commandHandlers) {
        final StaticApplicationContext staticContext = new StaticApplicationContext();
        for (final Class<?> handlerClass : commandHandlers) {
            final String className = Introspector.decapitalize(ClassUtils.getShortName(handlerClass));
            staticContext.registerSingleton(className, handlerClass);
        }
        this.context = staticContext;
        return this;
    }
}
