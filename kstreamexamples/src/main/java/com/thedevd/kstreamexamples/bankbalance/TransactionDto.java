package com.thedevd.kstreamexamples.bankbalance;

public class TransactionDto {

	private String accountNo;
	private Integer txnAmt;
	private String txnTime;
	
	public String getAccountNo()
	{
		return accountNo;
	}
	
	public void setAccountNo( String accountNo )
	{
		this.accountNo = accountNo;
	}
	
	public Integer getTxnAmt()
	{
		return txnAmt;
	}
	
	public void setTxnAmt( Integer txnAmt )
	{
		this.txnAmt = txnAmt;
	}
	
	public String getTxnTime()
	{
		return txnTime;
	}
	
	public void setTxnTime( String txnTime )
	{
		this.txnTime = txnTime;
	}
	
	
}
