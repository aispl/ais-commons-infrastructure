package pl.ais.commons.infrastructure.test.feature.smile;

import pl.ais.commons.application.feature.ApplicationFeature;

/**
 * Defines the API contract for <em>be nice</em> feature.
 *
 * @author Warlock, AIS.PL
 * @since 1.0.2
 */
@ApplicationFeature
public interface BeNiceFeature {

    /**
     * Smile, as wide as possible.
     */
    void smile();

}
