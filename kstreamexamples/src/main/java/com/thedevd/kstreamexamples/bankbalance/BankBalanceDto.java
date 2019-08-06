package com.thedevd.kstreamexamples.bankbalance;

import java.time.Instant;

public class BankBalanceDto {

	private Integer balanceAmt = 0;
	private String lastUpdatedTime = Instant.ofEpochMilli(0L).toString();
	private Integer txnCount = 0;
	
	
	public Integer getBalanceAmt()
	{
		return balanceAmt;
	}
	
	public void setBalanceAmt( Integer balanceAmt )
	{
		this.balanceAmt = balanceAmt;
	}
	
	public String getLastUpdatedTime()
	{
		return lastUpdatedTime;
	}
	
	public void setLastUpdatedTime( String lastUpdatedTime )
	{
		this.lastUpdatedTime = lastUpdatedTime;
	}
	
	public Integer getTxnCount()
	{
		return txnCount;
	}
	
	public void setTxnCount( Integer txnCount )
	{
		this.txnCount = txnCount;
	}
	
	
}
