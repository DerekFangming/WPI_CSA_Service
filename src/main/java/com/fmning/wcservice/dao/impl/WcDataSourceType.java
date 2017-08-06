package com.fmning.wcservice.dao.impl;

import com.fmning.service.dao.DataSourceType;

public enum WcDataSourceType implements DataSourceType {


    WC_SERVICE();

    WcDataSourceType() {
        System.out.println("In WcDataSourceType");
    }

    @Override
    public String getNickname() {
        return this.toString();
    }
}