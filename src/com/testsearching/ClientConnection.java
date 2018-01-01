package com.testsearching;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class ClientConnection {
	public String makeConnectionPost(String url, String params) {
		String response = null;
		// Instantiate an HttpClient
		HttpClient client = new HttpClient();

		// Instantiate a POST HTTP method
		PostMethod method = new PostMethod(url);
		method.addRequestHeader("content-type", "application/json");
		method.addRequestHeader("Accept", "application/json");
		method.setRequestBody(params);
		try {
			int statusCode = client.executeMethod(method);
			System.out.println(statusCode);
			response = method.getResponseBodyAsString();
			System.out.println(response);

			// release connection
			method.releaseConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public String makeConnectionGet(String url) {
		String response = null;
		// Instantiate an HttpClient
		HttpClient client = new HttpClient();
		// Instantiate a GET HTTP method
		GetMethod method = new GetMethod(url);
		method.addRequestHeader("Accept", "application/json");
		try {
			int statusCode = client.executeMethod(method);
			System.out.println(statusCode);
			response = method.getResponseBodyAsString();
			System.out.println(response);
			// release connection
			method.releaseConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}