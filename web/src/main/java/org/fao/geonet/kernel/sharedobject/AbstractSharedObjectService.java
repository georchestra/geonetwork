package org.fao.geonet.kernel.sharedobject;

import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.sharedobject.support.DAO;

public abstract class AbstractSharedObjectService implements Service {
    protected String appPath;

    public AbstractSharedObjectService() {
        super();
    }

    public void init(String appPath, ServiceConfig config) throws Exception {
        this.appPath = appPath;
    }

    protected DAO dao(ServiceContext context) throws Exception {
        Dbms dbms = (Dbms) context.getResourceManager().open (Geonet.Res.MAIN_DB);

        return DAO.get(appPath,dbms);
    }

}