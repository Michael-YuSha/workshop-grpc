/*
 * Copyright 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.grpc.examples.translate;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.translate.TranslateGrpc.TranslateBlockingStub;
import io.grpc.examples.translate.TranslateGrpc.TranslateStub;
import io.grpc.stub.StreamObserver;

/**
 * Sample client code that makes gRPC calls to the server.
 */
public class TranslateClient {

	public static void main(String[] args) throws CertificateException, SSLException, IOException, InterruptedException {
		TranslateClient client = new TranslateClient();
		int action = Utils.askAction();
		while (action != 0) {
			if (action == 1) {
				client.singleLineBlockingTranslateRequest();
			} else if (action == 2) {
				client.multiLineBlockingTranslateRequest();
			} else if (action == 3) {
				client.retrieveMultiLineTranslateRequest();
			} else if (action == 4) {
				client.asyncTranslateChat();
			}
			action = Utils.askAction();
		}
		try {
		} finally {
			System.out.println("Shutting down ...");
			client.shutdown();
		}
	}

	private final ManagedChannel channel;
	private final TranslateBlockingStub blockingStub;
	private final TranslateStub asyncStub;

	private String responseKey;

	/** Construct client for accessing Translate server at {@code host:port}. 
	 * @throws IOException 
	 * @throws SSLException 
	 * @throws CertificateException */
	public TranslateClient() throws CertificateException, SSLException, IOException {
		if (TranslateServer.useSimpleServer() == TranslateServer.SIMPLE_SERVER) {
			channel = buildSimpleChannel();
		} else {
			channel = buildSecureChannel();
		}
		// TODO-5 maak de juiste stubs aan (zie hints.txt)
		blockingStub = null; 
		asyncStub = null;
	}

	private ManagedChannel buildSimpleChannel() {
		//TODO-5 maak een niet secure channel aan.
		return null;
	}

	private ManagedChannel buildSecureChannel() throws CertificateException, SSLException, IOException {
		//TODO-10 maak een secure channel aan (zie GrpcUtils)
		return null;
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	// ---------- methods to be implemented --------------------

	public void singleLineBlockingTranslateRequest() {
		//TODO-6 : roep de (blocking) translate methode aan met een request, waarin een taal en een regel om te vertalen zit.
		System.out.println("todo: print de vertaalde regel");
	}

	public void multiLineBlockingTranslateRequest() {
		//TODO-7 : roep de (blocking) translateList aan methode met een request, 
		// waarin een taal en een paar regels zitten om te vertalen.
		this.responseKey = null; // vul deze met de key uit de response.
		System.out.println("Gor responseKey from server: " + responseKey);
	}

	public void retrieveMultiLineTranslateRequest() {
		//TODO-8: roep de (blocking) retrieveTranslateList methode aan, met een request waarin responseKey van hierboven zit.
		System.out.println("todo print alle vertaalde regels uit de response");
	}

	public CountDownLatch asyncTranslateChat() {
		//TODO-9 : implement deze methoe waarbij async translateChat methide wordt aangeroepen.
		// zie hints.txt voor het implementeren van requestObserver en responseObserver
		// nb: maak methode die voor het eerste request, de taal opgeeft
		// en daarna wordt in een loop request verstuurd met een te vertalen regel. Zie:
		
		final CountDownLatch finishLatch = new CountDownLatch(1);
		StreamObserver<TranslateStringMsg> requestObserver = null; //maak de juiste stub

		final Language taal = Utils.askLanguage();
		// TODO: maak message voor het eerste request: XxxMsg msg = ....
		
		//TODO: stuur het request van hierboven naar de server
		
		String line = Utils.askLineToTranslate();
		while (!line.isEmpty()) {
			//TODO maak het volgende request, met daarin this.line om te vertalen
			
			//TOD en verstuur deze naar de server
			line = Utils.askLineToTranslate();
		}
		
		// Breek de chat sessie nu af
		// zie hints.txt
		return finishLatch;
	}

	private StreamObserver<TranslateStringMsg> getObserver(final CountDownLatch finishLatch) {
		return new StreamObserver<TranslateStringMsg>() {

			@Override
			public void onNext(TranslateStringMsg msg) {
				System.out.println(msg.getLine());
			}

			@Override
			public void onError(Throwable t) {
				finishLatch.countDown();
			}

			@Override
			public void onCompleted() {
				System.out.println("Finished chat...");
				finishLatch.countDown();
			}
		};
	}
}
