package it.unibo.ai.didattica.competition.tablut.IA;

import java.io.IOException;

public class TablutTestBlackClient {

	public static void main(String[] args) throws IOException {
		String[] array = new String[] { "BLACK" };
		if (args.length > 0) {
			array = new String[args.length + 1];
			array[0] = "BLACK";
			System.arraycopy(args, 0, array, 1, args.length);
		}
		TablutTestClient.main(array);
	}

}
