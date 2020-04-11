package com.example.demo_retiro.producer;

import com.example.demo_retiro.domain.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WithDrawalEventProducer {
	
	String topic = "transaction-events";
	private Logger log = LoggerFactory.getLogger(WithDrawalEventProducer.class);
	
	@Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;
	
	@Autowired
    ObjectMapper objectMapper;
	
	public void sendWithDrawalEvent(Transaction withDrawalEvent) throws JsonProcessingException {

		Integer key = withDrawalEvent.getId();
		String value = objectMapper.writeValueAsString(withDrawalEvent);

	
		
		ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, value, ex);
			}

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key, value, result);
			}
		});

	}
	

	private void handleFailure(Integer key, String value, Throwable ex) {
			log.error("Error Sending the Message and the exception is {}", ex.getMessage());
			try {
				throw ex;
			} catch (Throwable throwable) {
				log.error("Error in OnFailure: {}", throwable.getMessage());
			}
	
	}

	private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
		log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value,
				result.getRecordMetadata().partition());
	}
	
	public SendResult<Integer, String> sendWithDrawalEventSynchronous(Transaction withDrawalEvent)
			throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

		Integer key = withDrawalEvent.getId();
		String value = objectMapper.writeValueAsString(withDrawalEvent);
		SendResult<Integer, String> sendResult = null;
		try {
			sendResult = kafkaTemplate.sendDefault(key, value).get(1, TimeUnit.SECONDS);
		} catch (ExecutionException | InterruptedException e) {
			log.error("ExecutionException/InterruptedException Sending the Message and the exception is {}",
					e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Exception Sending the Message and the exception is {}", e.getMessage());
			throw e;
		}

		return sendResult;

	}
	
	private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {

		// agregando header
		List<Header> recordHeaders = List.of(new RecordHeader("with-drawal-event-source", "scanner".getBytes()));
		//List<Header> recordHeaders = (List<Header>) (new RecordHeader("deposit-event-source", "scanner".getBytes()));

		// return new ProducerRecord<>(topic, null, key, value, null);
		return new ProducerRecord<>(topic, null, key, value, recordHeaders);
	}
	
	public void sendWithDrawalEvent_Approach2(Transaction withDrawalEvent) throws JsonProcessingException {

		Integer key = withDrawalEvent.getId();
		String value = objectMapper.writeValueAsString(withDrawalEvent);

		ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);

		ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);

		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key, value, ex);
			}

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key, value, result);
			}
		});
	}
	
	public ListenableFuture<SendResult<Integer,String>> sendWithDrawalEvent_Approach3(Transaction withDrawalEvent) throws JsonProcessingException {

		Integer key = withDrawalEvent.getId();
        String value = objectMapper.writeValueAsString(withDrawalEvent);

        ProducerRecord<Integer,String> producerRecord = buildProducerRecord(key, value, topic);

        ListenableFuture<SendResult<Integer,String>> listenableFuture =  kafkaTemplate.send(producerRecord);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }






        });

        return listenableFuture;
    }
	
	

	
	

}
