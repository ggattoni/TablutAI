package it.unibo.ai.didattica.competition.tablut.IA;

import java.io.IOException;

public class TablutTestWhiteClient {
	
	public static void main(String[] args) throws IOException {
		String[] array = new String[]{"WHITE"};
		if (args.length>0){
			array = new String[]{"WHITE", args[0]};
		}
		TablutTestClient.main(array);
	}
	
}
