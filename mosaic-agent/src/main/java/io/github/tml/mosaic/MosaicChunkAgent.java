package io.github.tml.mosaic;

import io.github.tml.mosaic.server.MosaicAgentSocketServer;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;


/**
 * @author welsir
 * @description :
 * @date 2025/5/27
 */

@Slf4j
public class MosaicChunkAgent {

    private static Instrumentation instrumentation;
    public static void agentmain(String args, Instrumentation inst) {

        log.info("run mosaic agent...");
        instrumentation = inst;
        int port = Integer.parseInt(args.trim());
        new Thread(() -> {
            try {
                new MosaicAgentSocketServer().start(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}