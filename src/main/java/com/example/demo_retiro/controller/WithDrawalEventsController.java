package com.example.demo_retiro.controller;


import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



import com.example.demo_retiro.domain.Transaction;
import com.example.demo_retiro.producer.WithDrawalEventProducer;
import com.example.demo_retiro.service.ITransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class WithDrawalEventsController {
	
	private Logger log = LoggerFactory.getLogger(WithDrawalEventsController.class);
	
	@Autowired
	WithDrawalEventProducer withDrawalEventProducer;
	
	
	@Autowired
	private ITransactionService transactionService;  
		
	
	@PostMapping("/v1/depositEvent")
	public ResponseEntity<Transaction> postLibraryEvent(@RequestBody  Transaction libraryEvent) throws JsonProcessingException{
		
		Transaction tranSql = transactionService.save(libraryEvent);
		
		log.info("antes sendDepositEvent_Approach3 ");
		
		withDrawalEventProducer.sendWithDrawalEvent_Approach3(libraryEvent);
		
		
		log.info("despues sendDepositEvent_Approach3 ");	
		
		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
	}
	
	

}
