package it.unibo.ai.didattica.competition.tablut.IA;

import java.io.IOException;

public class TablutTestBlackClient {
	
	public static void main(String[] args) throws IOException {
		String[] array = new String[]{"BLACK"};
		if (args.length>0){
			array = new String[]{"BLACK", args[0]};
		}
		TablutTestClient.main(array);
	}
	
}
