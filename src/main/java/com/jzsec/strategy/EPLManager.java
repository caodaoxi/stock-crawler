package com.jzsec.strategy;

import com.espertech.esper.client.*;
import com.jzsec.bean.Epl;
import com.jzsec.bean.Schema;
import com.stock.quota.utils.DBUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by caodaoxi on 16-6-30.
 */
public class EPLManager implements Serializable {

    private EPAdministrator admin = null;
    private Map<String, List<Schema>> allSchema = null;
    private Map<Integer, Epl> allEpls = null;
    private UpdateListener buylistener = null;
    private UpdateListener salelistener = null;
    private EPRuntime runtime = null;
    private EPServiceProvider epService = null;

    public EPLManager(UpdateListener buylistener, UpdateListener salelistener) {
        if(buylistener != null) this.buylistener = buylistener;
        if(salelistener != null) this.salelistener = salelistener;
        Configuration configuration = new Configuration();

        this.epService = EPServiceProviderManager.getDefaultProvider(configuration);
        this.admin = epService.getEPAdministrator();

        this.addSchemas();
        this.addEpls();
        this.start();
    }

    public EPLManager addSchemas() {
        this.allSchema = DBUtils.qetAllSchema();
        Collection<List<Schema>> schemas = allSchema.values();
        for(List<Schema> schema : schemas) {
            for(Schema s : schema) {
                this.admin.createEPL(s.getCreateSchemaSql());
            }
        }
        return this;
    }

    public EPLManager addEpls() {
        this.allEpls = DBUtils.qetAllEpls();
        Collection<Epl> epls = allEpls.values();
        EPStatement buyState = null;
        EPStatement saleState = null;
        String buyEplSql = null;
        String saleEplSql = null;
        for(Epl epl : epls) {
            buyEplSql = epl.getBuySql();
            buyState = this.admin.createEPL(buyEplSql);
            buyState.addListener(buylistener);

            saleEplSql = epl.getSaleSql();
            saleState = this.admin.createEPL(saleEplSql);
            saleState.addListener(salelistener);
        }
        return this;
    }

    public EPLManager start() {
        this.runtime = epService.getEPRuntime();
        return this;
    }
    public EPRuntime getEPRuntime() {
        return runtime;
    }
}
