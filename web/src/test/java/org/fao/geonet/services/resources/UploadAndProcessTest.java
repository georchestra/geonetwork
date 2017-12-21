package org.fao.geonet.services.resources;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.DataManagerParameter;
import org.fao.geonet.kernel.search.SearchManager;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;

public class UploadAndProcessTest {

    @Test
    public void testGetUploadedFileUrl() throws Exception {
        UploadAndProcess uap = new UploadAndProcess();
        Method m = ReflectionUtils.findMethod(UploadAndProcess.class, "getUploadedFileUrl",
                DataManager.class, ServiceContext.class, String.class, String.class);
        m.setAccessible(true);
        ServiceContext sc = Mockito.mock(ServiceContext.class);
        DataManagerParameter dmp = Mockito.mock(DataManagerParameter.class);
        dmp.context = sc;
        Mockito.when(sc.getAppPath()).thenReturn("/tmp");
        dmp.dbms = Mockito.mock(Dbms.class);
        Mockito.when(dmp.dbms.select(Mockito.anyString())).thenReturn(new Element("response"));
        dmp.searchManager = Mockito.mock(SearchManager.class);
        dmp.settingsManager = Mockito.mock(SettingManager.class);
        Mockito.when(dmp.settingsManager.getValue(Geonet.Settings.SERVER_PROTOCOL)).thenReturn("https");
        Mockito.when(dmp.settingsManager.getValue(Geonet.Settings.SERVER_HOST)).thenReturn("dev.pigma.org");
        Mockito.when(dmp.settingsManager.getValue(Geonet.Settings.SERVER_PORT)).thenReturn("443");
        dmp.baseURL = "/geonetwork";
        DataManager dm = new DataManager(dmp);

        String ret = (String) ReflectionUtils.invokeMethod(m, uap, dm, null, "1235", "myFile.jpg");

        assertTrue(ret.equals("https://dev.pigma.org:443/geonetwork/srv/eng/resources.get?access=private&id=1235&fname=myFile.jpg"));
    }
}
