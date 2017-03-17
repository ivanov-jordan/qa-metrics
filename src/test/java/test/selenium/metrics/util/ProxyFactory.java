package test.selenium.metrics.util;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.CaptureType;

public class ProxyFactory {

    public static BrowserMobProxy getInstance() {
        // start the proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);

        // enable more detailed HAR capture, if desired (see CaptureType for the
        // complete list)
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);

        return proxy;
    }

}
