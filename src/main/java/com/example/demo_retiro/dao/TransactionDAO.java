package com.example.demo_retiro.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.demo_retiro.domain.Transaction;

public interface TransactionDAO extends CrudRepository<Transaction, Integer>{

	
}