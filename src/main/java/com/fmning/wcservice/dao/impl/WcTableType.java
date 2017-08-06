package com.fmning.wcservice.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.fmning.service.dao.DataSourceRegistry;
import com.fmning.service.dao.SchemaTable;
import com.fmning.service.dao.DataSourceType;
import com.fmning.service.dao.SdkDataSource;
import com.fmning.util.Pair;

public enum WcTableType implements SchemaTable {
	;

	private String dsNickname;

	private SdkDataSource dataSource;
  
	private String tableName;
  
	private List<Pair<Enum<?>, String>> columnDefns;
  
	private List<String> columnNames = new ArrayList<String>();

  
	private String pkName;

  
	WcTableType(DataSourceType database, List<Pair<Enum<?>, String>> columnDefns) {
		this.dsNickname = database.getNickname();
		this.columnDefns = columnDefns;
		this.tableName = this.name().toLowerCase();
		this.columnNames = SchemaTable.getColumnNames(this.columnDefns);
		this.pkName = SchemaTable.getPkName(this.columnDefns);
	}
  
	@Override
  
	public void init(DataSourceRegistry dsr) {  
		this.dataSource = dsr.getDataSource(this.dsNickname);
  
		// Keep this at the end so that "this" is fully populated before adding it.
		// In particular addTable() requires that this.tableName have been set
		this.dataSource.addTable(this);
	}
  
	@Override
	public List<Pair<Enum<?>, String>> getColumnDefns( ) {    
		return this.columnDefns;  
	}

	//@Override
	public String getDataSourceType( ) {
		return dsNickname;
	}
  
	@Override
	public SdkDataSource getDataSource( ) {
		return this.dataSource;
	}
  
	@Override
	public String getTableName( ) {
		return this.tableName;
	}
  
	@Override
	public String getPrimaryKeyName( ) {
		return this.pkName;
	}

	@Override
	public List<String> getColumnNames( ) {
		return this.columnNames;
	}
  
	@Override
	public boolean isExactFieldCountRequired( ) {
		return true;
	}

}