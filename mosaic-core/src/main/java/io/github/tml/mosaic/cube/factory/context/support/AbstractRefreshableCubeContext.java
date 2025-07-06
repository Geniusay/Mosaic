package io.github.tml.mosaic.cube.factory.context.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.context.ConfigurableCubeContext;
import io.github.tml.mosaic.cube.factory.support.DefaultDefinitionListableCubeFactory;
import io.github.tml.mosaic.cube.factory.support.ListableCubeFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract implementation for a refreshable Cube context.
 * Manages configuration and Cube factory lifecycle.
 *
 * Author: suifeng
 * Date: 2025/6/7
 */
@Slf4j
public abstract class AbstractRefreshableCubeContext extends AbstractCubeContext implements ConfigurableCubeContext {

    /** Singleton Cube factory instance */
    private static final DefaultDefinitionListableCubeFactory cubeFactory;

    static {
        cubeFactory = new DefaultDefinitionListableCubeFactory();
    }

    /** Constructor: initializes Cube factory */
    public AbstractRefreshableCubeContext() {
        log.info("[Cube][CubeContext] cubeContext initialized with cubeFactory: {}", cubeFactory.getClass().getSimpleName());
    }

    /**
     * Refreshes the context: loads configuration and pre-instantiates beans.
     *
     * @throws CubeException if refresh fails
     */
    @Override
    public void refresh() throws CubeException {
        log.info("[Cube][CubeContext] Starting cubeContext refresh...");

        try {
            // 1. Get CubeFactory
            ListableCubeFactory cubeFactory = getBeanFactory();

            // 2. Refresh configuration resources
            refreshConfigurationResources();
            log.info("[Cube][CubeContext] cube Configuration resources has refreshed.");

            // 3. Pre-instantiate singleton beans
            preInstantiateSingletons();
            log.info("[Cube][CubeContext] Singleton cubeBeans pre-instantiated.");

        } catch (Exception e) {
            log.error("[Cube][CubeContext] cubeContext refresh failed.", e);
            throw new CubeException("Context refresh failed: " + e.getMessage(), e);
        }

        log.info("[Cube][CubeContext] cubeContext refresh complete.");
    }

    /**
     * Abstract method: refresh configuration resources.
     * Subclasses must implement loading logic.
     *
     * @throws CubeException if configuration refresh fails
     */
    protected abstract void refreshConfigurationResources() throws CubeException;

    /**
     * Creates a new Cube factory instance.
     *
     * @return DefaultDefinitionListableCubeFactory instance
     */
    private DefaultDefinitionListableCubeFactory createCubeFactory() {
        return new DefaultDefinitionListableCubeFactory();
    }

    /**
     * Returns the Cube factory instance.
     *
     * @return ListableCubeFactory instance
     */
    @Override
    protected ListableCubeFactory getBeanFactory() {
        return cubeFactory;
    }
}