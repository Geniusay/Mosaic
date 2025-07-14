package io.github.tml.mosaic.actuator;

import io.github.tml.mosaic.core.NamedThreadFactory;
import io.github.tml.mosaic.core.execption.ActuatorException;
import io.github.tml.mosaic.cube.external.AngelCube;
import io.github.tml.mosaic.cube.external.MosaicCube;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class AngelCubeActuator extends AbstractCubeActuator{

    private final Map<String, AngelCubeWorker> angelCubeWorkerMap = new ConcurrentHashMap<>();

    private final ExecutorService angelCubeExecutor;

    /**
     * 清道夫线程池，负责清理stop还未结束的cube
     */
    private final ExecutorService scavenger;

    /**
     * 临终关怀任务队列
     * 等待stop关闭的cube
     */
    private final BlockingQueue<AngelCubeWorker> deathbedCareList = new LinkedBlockingQueue<>();

    public AngelCubeActuator() {
        angelCubeExecutor = new ThreadPoolExecutor(0 ,Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("angel-cube-thread")
        );
        scavenger = Executors.newSingleThreadExecutor(new NamedThreadFactory("angel-cube-scavenger"));

        runScavenger();
    }

    /**
     * 清道夫线程启动
     * 负责对正在运行stop的天使线程进行清道夫处理
     * 如果在2s内未完成stop操作则强制清理
     */
    private void runScavenger() {
        scavenger.execute(()->{
            for(;;){
                AngelCubeWorker worker = deathbedCareList.poll();
                if (worker != null){
                    log.info("scavenger ready handle angle cube {} stop", worker.angelCube.cubeId());
                    Future<?> stopFuture = worker.getStopFuture();
                    if (stopFuture != null && !stopFuture.isDone()){
                        try {
                            stopFuture.get(2, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            log.error("scavenger handle angle cube {} stop get error:{}", worker.angelCube.cubeId(), e.getMessage());
                        }
                        log.error("angle cube:{} stop time over 2s, force stop", worker.angelCube.cubeId());
                        stopFuture.cancel(true);
                    }
                }
            }
        });
    }

    @Override
    public <T> T execute(ExecuteContext executeContext) {
        MosaicCube cube = executeContext.getCube();
        if (!(cube instanceof AngelCube)) {
            throw new ActuatorException("{} is not an AngelCube.");
        }
        String cubeId = cube.cubeId();
        synchronized (cubeId){
            AngelCubeWorker worker = angelCubeWorkerMap.getOrDefault(cubeId, new AngelCubeWorker((AngelCube) cube));
            if (!angelCubeWorkerMap.containsKey(cubeId)) {
                worker.start();
                angelCubeWorkerMap.put(cubeId, worker);
            }else{
                worker.stop();
            }
        }
        return null;
    }

    public class AngelCubeWorker{

        private final AngelCube angelCube;

        private final String name;

        private Future<?> startFuture;

        @Getter
        private Future<?> stopFuture;

        private static final String workerNamePrefix = "angel-cube-worker-";

        public AngelCubeWorker(AngelCube angelCube) {
            this.angelCube = angelCube;
            this.name = workerNamePrefix+angelCube.cubeId();
        }

        public void start() {
            startFuture = angelCubeExecutor.submit(()->{
                log.info("Starting AngelCubeWorker: {}", name);
                angelCube.start();
            });
        }

        public void stop(){
            stopFuture = angelCubeExecutor.submit(() -> {
                log.info("Stopping AngelCubeWorker: {}", name);
                angelCube.stop();
                if (!startFuture.isDone()) {
                    startFuture.cancel(true);
                }
            });
        }
    }

}
