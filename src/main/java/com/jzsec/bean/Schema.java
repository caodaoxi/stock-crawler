package com.jzsec.bean;

/**
 * Created by caodaoxi on 16-6-30.
 */
public class Schema {
    private int id;
    private String schemaCoce;
    private String schemaName;
    private String createSchemaSql;
    private int isAlert;

    public Schema() {
    }

    public Schema(int id, String schemaCoce, String schemaName, String createSchemaSql, int isAlert) {
        this.id = id;
        this.schemaCoce = schemaCoce;
        this.schemaName = schemaName;
        this.createSchemaSql = createSchemaSql;
        this.isAlert = isAlert;
    }

    public int getIsAlert() {
        return isAlert;
    }

    public void setIsAlert(int isAlert) {
        this.isAlert = isAlert;
    }

    public Schema(String schemaCoce, String schemaName, String createSchemaSql, int isAlert) {

        this.schemaCoce = schemaCoce;
        this.schemaName = schemaName;
        this.createSchemaSql = createSchemaSql;
        this.isAlert = isAlert;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchemaCoce() {
        return schemaCoce;
    }

    public void setSchemaCoce(String schemaCoce) {
        this.schemaCoce = schemaCoce;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getCreateSchemaSql() {
        return createSchemaSql;
    }

    public void setCreateSchemaSql(String createSchemaSql) {
        this.createSchemaSql = createSchemaSql;
    }
}
