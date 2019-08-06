package com.thedevd.kstreamexamples.bankbalance;

import java.io.IOException;
import java.time.Instant;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BankBalanceExactlyOnceWithoutSerde {

	static ObjectMapper mapper = new ObjectMapper();

	public static void main( String[] args ) throws JsonProcessingException
	{
		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "BankBalanceStreaming");
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.105.27:9092");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
		config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

		// Exactly once processing!!
		config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

		StreamsBuilder builder = new StreamsBuilder();

		String initialBalanceSheet = mapper.writeValueAsString(new BalanceSheet(0, 0));

		KStream<String, String> txnDetails = builder.stream("bank-txn-data1",
				Consumed.with(Serdes.String(), Serdes.String()));
		KTable<String, String> aggregateTable = txnDetails.groupByKey().aggregate(() -> initialBalanceSheet,
				( k, newV, aggV ) -> updateBalance(newV, aggV), Materialized.with(Serdes.String(), Serdes.String()));

		aggregateTable.toStream().to("bank-balance-data", Produced.with(Serdes.String(), Serdes.String()));

		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp(); // dont do this on prod
		streams.start();

		// print the topology
		System.out.println(streams.toString());

		// shutdown hook to correctly close the streams application
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

	private static String updateBalance( String newV, String aggV )
	{
		try
		{
			TransactionDto newTxn = mapper.readValue(newV, TransactionDto.class);
			BalanceSheet oldBalance = mapper.readValue(aggV, BalanceSheet.class);

			BalanceSheet newBalance = new BalanceSheet(oldBalance.getCount() + 1,
					oldBalance.getBalance() + newTxn.getTxnAmt());
			newBalance.setTime(Instant.ofEpochMilli(Math.max(Instant.parse(newTxn.getTxnTime()).toEpochMilli(),
					Instant.parse(oldBalance.getTime()).toEpochMilli())).toString());

			return mapper.writeValueAsString(newBalance);
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}

}

class BalanceSheet {

	int count;
	double balance;
	String time;

	public BalanceSheet()
	{
	}

	public BalanceSheet( int count, double balance )
	{
		super();
		this.count = count;
		this.balance = balance;
		this.time = Instant.ofEpochMilli(0L).toString();
	}

	public int getCount()
	{
		return count;
	}

	public void setCount( int count )
	{
		this.count = count;
	}

	public double getBalance()
	{
		return balance;
	}

	public void setBalance( double balance )
	{
		this.balance = balance;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime( String time )
	{
		this.time = time;
	}

}
