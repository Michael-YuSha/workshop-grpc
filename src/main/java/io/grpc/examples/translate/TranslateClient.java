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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.translate.TranslateGrpc.TranslateBlockingStub;
import io.grpc.examples.translate.TranslateGrpc.TranslateStub;
import io.grpc.stub.StreamObserver;

/**
 * Sample client code that makes gRPC calls to the server.
 */
public class TranslateClient {

	public static void main(String[] args) throws InterruptedException {
		TranslateClient client = new TranslateClient("localhost", TranslateServer.PORT);
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

	/** Construct client for accessing Translate server at {@code host:port}. */
	public TranslateClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
	}

	/**
	 * Construct client for accessing Translate server using the existing
	 * channel.
	 */
	public TranslateClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
		blockingStub = TranslateGrpc.newBlockingStub(channel);
		asyncStub = TranslateGrpc.newStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	// ---------- methods to be implemented --------------------

	public void singleLineBlockingTranslateRequest() {
		final TranslateStringMsg request = TranslateStringMsg.newBuilder().setLang(Language.DE).setLine("regel 1")
				.build();

		final TranslateStringMsg response = blockingStub.translate(request);
		System.out.println(response.getLine());
	}

	public void multiLineBlockingTranslateRequest() {
		final TranslateStringListMsg request = TranslateStringListMsg.newBuilder().setLang(Language.DE)
				.addLines("regel 1").addLines("regel 2").addLines("regel 3").build();

		final ResponseKeyMsg response = blockingStub.translateList(request);
		this.responseKey = response.getKey();
		System.out.println("got key : " + this.responseKey);
	}

	public void retrieveMultiLineTranslateRequest() {
		final ResponseKeyMsg request = ResponseKeyMsg.newBuilder().setKey(this.responseKey).build();
		final TranslateStringListMsg response = blockingStub.retrieveTranslateList(request);
		response.getLinesList().forEach(System.out::println);
	}

	public CountDownLatch asyncTranslateChat() {
		final CountDownLatch finishLatch = new CountDownLatch(1);
		StreamObserver<TranslateStringMsg> requestObserver = asyncStub.translateChat(getObserver(finishLatch));

		TranslateStringMsg msg = TranslateStringMsg.newBuilder().setLang(Utils.askLanguage()).build();
		requestObserver.onNext(msg);
		String line = Utils.askLineToTranslate();
		while (!line.isEmpty()) {
			msg = TranslateStringMsg.newBuilder().setLine(line).build();
			requestObserver.onNext(msg);
			line = Utils.askLineToTranslate();
		}
		// Mark the end of requests
		requestObserver.onCompleted();
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
				System.out.println("Finished ...");
				finishLatch.countDown();
			}
		};
	}
}
