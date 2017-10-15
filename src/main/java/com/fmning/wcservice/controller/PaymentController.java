package com.fmning.wcservice.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;

@Controller
public class PaymentController {
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			  Environment.SANDBOX,
			  "wnbj3bx4nwmtyz77",
			  "2x688s4dpnpxf2dd",
			  "0806df90cf0bd867727c42077e6b41bd"
			);
	
	@RequestMapping("/pay")
	public ResponseEntity<String> test(@RequestBody Map<String, Object> request) {
		
		TransactionRequest paymentRequest = new TransactionRequest()
			    .amount(new BigDecimal("10.00"))
			    .paymentMethodNonce((String)request.get("nonce"))
			    .options()
			      .submitForSettlement(true)
			      .done();

		Result<Transaction> result = gateway.transaction().sale(paymentRequest);
		
		if (result.isSuccess()) {
			System.out.println("scuessfull");
			System.out.println(result.getMessage());
		} else {
			System.out.println("fail");
			System.out.println(result.getMessage());
		}
		
		System.out.println(request.get("nonce"));
		return new ResponseEntity<String>("", HttpStatus.OK);
	}
	
}
