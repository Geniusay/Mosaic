package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.Cube;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述: 第四步：配置处理之后的cube初始化操作
 * 职责：专门负责Cube的初始化调用和结果处理
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public abstract class AbstractAutowireInitCubeFactory extends AbstractAutowireConfigCubeFactory {

    @Override
    protected Cube executeInitializationPhase(Cube cube, CubeDefinition cubeDefinition, Object[] args) throws CubeException {
        try {
            log.debug("开始初始化阶段 | CubeId: {}", cube.getCubeId());

            // 执行前置初始化处理
            preInitialization(cube, cubeDefinition, args);

            // 执行核心初始化
            if (cube.init()) {
                log.info("✓ Cube初始化成功 | CubeId: {}, CubeName: {}", cube.getCubeId(), cube.getMetaData().getName());

                // 执行后置初始化处理
                postInitialization(cube, cubeDefinition, args);
                return cube;
            } else {
                String errorMsg = String.format("Cube初始化返回false | CubeId: %s", cube.getCubeId());
                log.error("✗ {}", errorMsg);
                throw new CubeException(errorMsg);
            }

        } catch (CubeException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Cube初始化异常 | CubeId: %s", cube.getCubeId());
            log.error("✗ {}: {}", errorMsg, e.getMessage(), e);
            throw new CubeException(errorMsg + ": " + e.getMessage(), e);
        }
    }

    /**
     * 前置初始化处理
     */
    protected void preInitialization(Cube cube, CubeDefinition cubeDefinition, Object[] args) throws CubeException {

    }

    /**
     * 后置初始化处理
     */
    protected void postInitialization(Cube cube, CubeDefinition cubeDefinition, Object[] args) throws CubeException {

    }
}