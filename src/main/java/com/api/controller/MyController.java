package com.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.service.MyService;

import io.swagger.annotations.ApiOperation;

@RestController
public class MyController {
	
	@Autowired
	MyService myService;
	
	@ApiOperation("Hello Post")
	@PostMapping("/helloPost")
	public ResponseEntity<String> helloInput(String input) {
		return ResponseEntity.ok(myService.hello(input));
	}
	
	@ApiOperation("Hello Get")
	@GetMapping("/helloGet")
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("Hello World");
	}

}
