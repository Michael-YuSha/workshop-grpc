package io.grpc.examples.translate;

import com.google.protobuf.InvalidProtocolBufferException;

import io.grpc.stub.StreamObserver;

public class TranslateServiceImpl extends TranslateGrpc.TranslateImplBase {

	@Override
	public void translate(TranslateStringMsg request, StreamObserver<TranslateStringMsg> responseObserver) {
		final TranslateStringMsg response = TranslateStringMsg.newBuilder()
				.setLine(Utils.translate(request.getLang().name(), request.getLine())).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void translateList(TranslateStringListMsg request, StreamObserver<ResponseKeyMsg> responseObserver) {
		final String uuid = Utils.uuid();
		Repo.save(uuid, request.toByteArray());
		ResponseKeyMsg response = ResponseKeyMsg.newBuilder().setKey(uuid).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void retrieveTranslateList(ResponseKeyMsg request, StreamObserver<TranslateStringListMsg> responseObserver) {
		final String uuid = request.getKey();
		final byte[] bytes = Repo.retrieve(uuid);
		try {
			TranslateStringListMsg response = TranslateStringListMsg.parseFrom(bytes);
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	@Override
	public StreamObserver<TranslateStringMsg> translateChat(StreamObserver<TranslateStringMsg> responseObserver) {
		
		return new StreamObserver<TranslateStringMsg>() {
			private Language lang = null;
			
			@Override
			public void onNext(TranslateStringMsg msg) {
				if (this.lang == null) {
					lang = msg.getLang();
				} else {
					String transLine = Utils.translate(lang.name(), msg.getLine());
					TranslateStringMsg responseMsg = TranslateStringMsg.newBuilder(msg).setLine(transLine).build();
					responseObserver.onNext(responseMsg);
				}
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("routeChat cancelled");
			}

			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};
	}

}
