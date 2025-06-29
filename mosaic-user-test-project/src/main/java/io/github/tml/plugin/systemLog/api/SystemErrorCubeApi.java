package io.github.tml.plugin.systemLog.api;

import io.github.tml.mosaic.cube.external.MExtension;
import io.github.tml.mosaic.cube.external.MExtensionPackage;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.plugin.systemLog.cube.SystemLogCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID;

@MExtensionPackage(
        name = "系统日志错误输出包",
        cubeId = PLUGIN_ID)
public class SystemErrorCubeApi extends MosaicExtPackage<SystemLogCube> {

    @MExtension(extPointId = "error", name = "错误输出", priority = 1)
    public void error(String msg) {
        mosaicCube.error(msg);
    }

    @Override
    public String extPackageId() {
        return "system.error";
    }
}
