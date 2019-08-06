package com.thedevd.kstreamexamples.bankbalance;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KafkaGenericSerde<T> {

	public static <T> Serde<T> createKafkaSerdesWithGenericSerde( TypeReference<T> typeReference )
	{
		ObjectMapper objectMapper = new ObjectMapper();
		KafkaGenericDeserializer<T> kafkaGenericDeserializer = new KafkaGenericDeserializer<>(objectMapper,
				typeReference);
		KafkaGenericSerializer<T> kafkaGenericSerializer = new KafkaGenericSerializer<>(objectMapper);
		return Serdes.serdeFrom(kafkaGenericSerializer, kafkaGenericDeserializer);
	}
}

class KafkaGenericDeserializer<T> implements Deserializer<T> {

	private final ObjectMapper mapper;
	private final TypeReference<T> typeReference;

	public KafkaGenericDeserializer( ObjectMapper mapper, TypeReference<T> typeReference )
	{
		this.mapper = mapper;
		this.typeReference = typeReference;
	}

	@Override
	public T deserialize( final String topic, final byte[] data )
	{
		if( data == null )
		{
			return null;
		}

		try
		{
			return mapper.readValue(data, typeReference);
		}
		catch( final IOException ex )
		{
			throw new SerializationException(
					"Can't deserialize data [" + Arrays.toString(data) + "] from topic [" + topic + "]", ex);
		}
	}

	@Override
	public void close()
	{
	}

	@Override
	public void configure( final Map<String, ?> settings, final boolean isKey )
	{
	}
}

class KafkaGenericSerializer<T> implements Serializer<T> {

	private final ObjectMapper mapper;

	public KafkaGenericSerializer( ObjectMapper mapper )
	{
		this.mapper = mapper;
	}

	@Override
	public byte[] serialize( String topic, T data )
	{
		if( data == null )
		{
			return null;
		}

		try
		{
			return mapper.writeValueAsBytes(data);
		}
		catch( IOException ex )
		{
			throw new SerializationException("Can't Serialize data [" + data.toString() + "] to topic [" + topic + "]",
					ex);
		}
	}

	@Override
	public void close()
	{
	}

	@Override
	public void configure( final Map<String, ?> settings, final boolean isKey )
	{
	}
}
