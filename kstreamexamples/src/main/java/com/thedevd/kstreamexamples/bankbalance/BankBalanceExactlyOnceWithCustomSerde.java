package com.thedevd.kstreamexamples.bankbalance;

import java.time.Instant;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

public class BankBalanceExactlyOnceWithCustomSerde {

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

		// Kafka Stream Serdes using Generic Serde
		final Serde<TransactionDto> txnSerde = KafkaGenericSerde
				.createKafkaSerdesWithGenericSerde(new TypeReference<TransactionDto>() {
				});
		final Serde<BankBalanceDto> bankBalanceSerde = KafkaGenericSerde
				.createKafkaSerdesWithGenericSerde(new TypeReference<BankBalanceDto>() {
				});

		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, TransactionDto> txnDetails = builder.stream("bank-txn-data",
				Consumed.with(Serdes.String(), txnSerde));

		// create the initial bankbalance
		BankBalanceDto initialBankBalance = new BankBalanceDto();

		// Updated bankBalance table
		KTable<String, BankBalanceDto> updatedBankBalance = txnDetails
				.groupByKey(Serialized.with(Serdes.String(), txnSerde))
				.aggregate(
						() -> initialBankBalance,
						( k, newV, aggV ) -> updateBalance(newV, aggV),
						Materialized.with(Serdes.String(), bankBalanceSerde)
						);

		// Write to output topic
		updatedBankBalance.toStream()
		.to("bank-balance-data", Produced.with(Serdes.String(), bankBalanceSerde));

		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp(); // dont do this on prod
		streams.start();

		// print the topology
		System.out.println(streams.toString());

		// shutdown hook to correctly close the streams application
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

	private static BankBalanceDto updateBalance( TransactionDto newTxn, BankBalanceDto oldBankBalance )
	{
		BankBalanceDto newBankBalance = new BankBalanceDto();
		newBankBalance.setBalanceAmt(oldBankBalance.getBalanceAmt() + newTxn.getTxnAmt());
		newBankBalance.setTxnCount(oldBankBalance.getTxnCount() + 1);
		newBankBalance
				.setLastUpdatedTime(Instant.ofEpochMilli(Math.max(Instant.parse(newTxn.getTxnTime()).toEpochMilli(),
						Instant.parse(oldBankBalance.getLastUpdatedTime()).toEpochMilli())).toString());

		return newBankBalance;
	}

}
