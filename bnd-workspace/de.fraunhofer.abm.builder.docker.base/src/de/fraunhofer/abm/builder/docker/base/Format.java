package de.fraunhofer.abm.builder.docker.base;

import java.text.MessageFormat;

public class Format {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String format = "Hello {0}";
		String out = MessageFormat.format(format, "ankur");
		System.out.println(out);
	}

}
