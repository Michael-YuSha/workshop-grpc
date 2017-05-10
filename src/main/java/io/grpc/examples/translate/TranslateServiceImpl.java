package io.grpc.examples.translate;

import com.google.protobuf.InvalidProtocolBufferException;

import io.grpc.stub.StreamObserver;

public class TranslateServiceImpl extends TranslateGrpc.TranslateImplBase {

	//TODO-6 implement de single translate rpc methode. 
	// hint: Utils.translate(..) om daadwerkelijk te 'vertalen' (helaas is google translate api niet meer gratis)
	// zoe ook hints.txt 
	
	//TODO-7 implement de translateList rpc methode. 
	// hint: zie Repo class om een message op te slaan en weer op te halen uit een 'database'

	//TODO-8 implement de retrieveTranslateList rpc methode. 
	// hint: xxxMsg.parseFrom(bytes) 

	//TODO-9 implement de translateChat bidirectional streaming rpc methode. 
	//nb de client is zo gemaakt dat in eerste request de taal wordt gezet, en alle volgende requests een regel om te vertalen.
	// zie verder hints.txt

}
