package com.jzsec.bean;

/**
 * Created by caodaoxi on 16-6-30.
 */
public class Epl {
    private int id;
    private String eplName;
    private String buySql;
    private String saleSql;
    private int alertType;
    private String phone;
    private String email;
    private String alertTemplate;


    public Epl() {
    }

    public Epl(int id, String eplName, String buySql, String saleSql, int alertType, String phone, String email, String alertTemplate) {
        this.id = id;
        this.eplName = eplName;
        this.buySql = buySql;
        this.saleSql = saleSql;
        this.alertType = alertType;
        this.phone = phone;
        this.email = email;
        this.alertTemplate = alertTemplate;
    }

    public Epl(String eplName, String buySql, String saleSql, int alertType, String phone, String email, String alertTemplate) {
        this.eplName = eplName;
        this.buySql = buySql;
        this.saleSql = saleSql;
        this.alertType = alertType;
        this.phone = phone;
        this.email = email;
        this.alertTemplate = alertTemplate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEplName() {
        return eplName;
    }

    public void setEplName(String eplName) {
        this.eplName = eplName;
    }

    public String getBuySql() {
        return buySql;
    }

    public void setBuySql(String buySql) {
        this.buySql = buySql;
    }

    public String getSaleSql() {
        return saleSql;
    }

    public void setSaleSql(String saleSql) {
        this.saleSql = saleSql;
    }

    public int getAlertType() {
        return alertType;
    }

    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlertTemplate() {
        return alertTemplate;
    }

    public void setAlertTemplate(String alertTemplate) {
        this.alertTemplate = alertTemplate;
    }

}
