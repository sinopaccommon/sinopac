package com.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.service.EposService;

@RestController
@RequestMapping("/epos")
public class EposController {
	
	@Autowired
	EposService eposService;
	
	@PostMapping("/auth")
	public ResponseEntity<String> auth(String ocard) {
		return ResponseEntity.ok(eposService.auth(ocard));
	}
	
	@PostMapping("/cancel")
	public ResponseEntity<String> cancel(String ocard) {
		return ResponseEntity.ok(eposService.cancel(ocard));
	}
	
	@PostMapping("/query")
	public ResponseEntity<String> query(String ocard) {
		return ResponseEntity.ok(eposService.query(ocard));
	}
	

}
