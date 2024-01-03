package moses.test.service;

import org.springframework.stereotype.Service;

@Service
public class MyService {
	
	public String hello(String input) {
		return "Hello " + input;
	}

}
