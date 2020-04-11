package com.example.demo_retiro.service;

import java.util.Optional;

import com.example.demo_retiro.domain.Transaction;

public interface ITransactionService {
	
	public Transaction save ( Transaction transaction);

	public Optional<Transaction> findById(Integer id);

}
