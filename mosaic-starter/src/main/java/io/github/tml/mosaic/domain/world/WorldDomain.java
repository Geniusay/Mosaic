package io.github.tml.mosaic.domain.world;

import io.github.tml.mosaic.convert.WorldContainerConvert;
import io.github.tml.mosaic.core.event.DefaultMosaicEventBroadcaster;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.world.event.event.AfterWorldTransitionEvent;
import io.github.tml.mosaic.entity.dto.WorldContainerDTO;
import io.github.tml.mosaic.entity.vo.world.WorldContainerVO;
import io.github.tml.mosaic.world.MosaicWorld;
import io.github.tml.mosaic.world.WorldContainer;
import io.github.tml.mosaic.world.factory.WorldContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorldDomain {
    @Resource
    private MosaicWorld mosaicWorld;

    @Resource
    private ApplicationContext applicationContext;

    private final DefaultMosaicEventBroadcaster broadcaster = DefaultMosaicEventBroadcaster.broadcaster();

    public WorldContainerVO createWorld(WorldContainerDTO worldDTO){
        // 创建新世界容器
        WorldContainer worldContainer = mosaicWorld.createWorldContainer(worldDTO.getName());

        mosaicWorld.registryWorldContainer(worldContainer);

        return WorldContainerConvert.convert2VO(worldContainer);
    }

    public WorldContainerVO traverseWorld(GUID guid){
        // 当前切换的世界是否存在
        if(mosaicWorld.contains(guid)){
            if(!mosaicWorld.isRunningWorld(guid)){
                AfterWorldTransitionEvent event = new AfterWorldTransitionEvent(null);
                event.setOldWorldId(mosaicWorld.getRunningWorldContainer().getId());
                event.setNewWorldId(guid);
                WorldContainer worldContainer = mosaicWorld.getWorldContainer(guid);

                mosaicWorld.traverse(worldContainer);

                broadcaster.broadcastEvent(event);

                return WorldContainerConvert.convert2VO(worldContainer);
            }
            return WorldContainerConvert.convert2VO(mosaicWorld.getRunningWorldContainer());
        }
        return null;
    }

    public WorldContainerVO createQuickCopyWorld(GUID guid){
        WorldContainer worldContainer = mosaicWorld.getWorldContainer(guid);

        WorldContainer newWorldContainer = WorldContainerFactory.createWorldContainer(worldContainer);

        mosaicWorld.registryWorldContainer(newWorldContainer);

        return WorldContainerConvert.convert2VO(newWorldContainer);
    }

    public List<WorldContainerVO> getAllWorlds(){
        return mosaicWorld.getAllWorldContainer().stream().map(WorldContainerConvert::convert2VO).collect(Collectors.toList());
    }

    public WorldContainerVO removeWorld(GUID guid){
        WorldContainer worldContainer = mosaicWorld.getWorldContainer(guid);
        if(mosaicWorld.getOriginalWorldContainer().equals(worldContainer) || mosaicWorld.worldSize() <= 1){
            return null;
        }
        if (mosaicWorld.isRunningWorld(guid)){
            WorldContainer originalWorld = mosaicWorld.getOriginalWorldContainer();

            mosaicWorld.traverse(originalWorld);
        }
        return WorldContainerConvert.convert2VO(mosaicWorld.removeWorldContainer(guid));
    }

    public WorldContainerVO getNowWorld(){
        return WorldContainerConvert.convert2VO(mosaicWorld.getRunningWorldContainer());
    }

    public <E> E getWorldComponent(GUID guid, Class<E> clazz){
        String componentName = mosaicWorld.getWorldContainer(guid).getComponentName(clazz);
        return applicationContext.getBean(componentName, clazz);
    }
}
