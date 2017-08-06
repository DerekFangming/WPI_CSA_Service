package com.fmning.wcservice.test;

import java.io.UnsupportedEncodingException;
import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fmning.service.manager.AboutManager;
import com.fmning.service.manager.HelperManager;

import org.springframework.beans.factory.annotation.Autowired;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/wcservice-servlet.xml")
public class AboutManagerTests extends TestCase
{
	@Autowired private AboutManager helloMgr;
	@Autowired private HelperManager helperManager;
	
	@Test
	public void helloTest()
	{
		assertTrue(helloMgr.getInfo().get(AboutManager.SDK_VERSION_KEY).equals(AboutManager.SDK_VERSION));
		assertTrue(helloMgr.getInfo().get(AboutManager.RELEASE_DATE_KEY).equals(AboutManager.RELEASE_DATE));
	}
	
	@Test
	public void decodeJWT()
	{
		assertNull(helperManager.decodeJWT("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHBpcmUiOiIyMDE3LTA3LTMxVDAzOjAyOjE3LjEyN1oiLCJ1c2VybmFtZSI6InN5bmZtMTIzQGdtYWlsLmNvbSJ9.F-_k-X5YYZLyse7sW7S0YuMWjUMeOhhfLSPwaxTIjOw"));
	}
	
	@Test
	public void signJWT() {
		Instant now = Instant.now();
		String str = helperManager.createAccessToken("synfm@126.com", now);
		System.out.println(str);
		try {
			Algorithm algorithm = Algorithm.HMAC256("PJNing");
		    String token = JWT.create().withClaim("username", "synfm@126.com")
		    		.withClaim("expire", now.toString()).sign(algorithm);
		    System.out.println(token);
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);
	}
}
