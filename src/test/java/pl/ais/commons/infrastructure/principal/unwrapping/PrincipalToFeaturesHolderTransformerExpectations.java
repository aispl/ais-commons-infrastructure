package pl.ais.commons.infrastructure.principal.unwrapping;

import static pl.ais.commons.application.feature.FeaturesHolderAssert.then;
import static pl.ais.commons.infrastructure.principal.unwrapping.PrincipalToFeaturesHolderTransformerAssembler.given;
import static pl.ais.commons.infrastructure.principal.unwrapping.PrincipalToFeaturesHolderTransformerAssembler.when;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import pl.ais.commons.application.feature.FeaturesHolder;
import pl.ais.commons.infrastructure.test.feature.smile.BeNiceFeature;

/**
 * Verifies {@linkplain PrincipalToFeaturesHolderTransformer} expectations.
 *
 * @author Warlock, AIS.PL
 * @since 1.0.2
 */
public class PrincipalToFeaturesHolderTransformerExpectations {

    private static final GrantedAuthority ANONYMOUS = new SimpleGrantedAuthority("anonymous");

    /**
     * Verifies if anonymous features are used for not authorized user.
     */
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    @Test
    public void shouldUseAnonymousFeaturesForNotAuthorizedUser() {
        final PrincipalToFeaturesHolderTransformer transformer = new PrincipalToFeaturesHolderTransformer();

        given(transformer).withAnonymousAuthority(ANONYMOUS).withAuthorityFeatures(ANONYMOUS, BeNiceFeature.class);

        final FeaturesHolder featuresHolder = when().apply(null);

        then(featuresHolder).shouldHaveFeature(BeNiceFeature.class);
    }

}
