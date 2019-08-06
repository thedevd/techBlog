package com.thedevd.kstreamexamples.bankbalance;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BankTxnProducer {

	public static void main( String[] args )
	{
		Properties props = new Properties();
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "txnproducer");
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.105.27:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		// producer acks
		props.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // strongest producing guarantee
		props.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
		props.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");
		// leverage idempotent producer from Kafka 0.11 !
		props.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // ensure we don't push duplicates

		String[] custList = { "Dev~123", "Ravi~123", "Atul~123" };
		ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(custList.length);

		ObjectMapper mapper = new ObjectMapper();

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		for( String cust : custList )
		{
			newFixedThreadPool.execute(() -> {
				while( true )
				{
					try
					{
						TransactionDto txnDto = new TransactionDto();
						txnDto.setAccountNo(cust);
						txnDto.setTxnAmt(ThreadLocalRandom.current().nextInt(0, 100));
						txnDto.setTxnTime(Instant.now().toString());

						ProducerRecord<String, String> rec = new ProducerRecord<>("bank-txn-data", cust,
								mapper.writeValueAsString(txnDto));
						producer.send(rec);

						Thread.sleep(100);
					}
					catch( JsonProcessingException | InterruptedException e )
					{
						e.printStackTrace();
						producer.close();
					}
				}
			});
		}

	}

}
