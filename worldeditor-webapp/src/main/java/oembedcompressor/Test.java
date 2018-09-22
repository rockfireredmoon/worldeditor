package oembedcompressor;

import java.net.URLEncoder;

public class Test {
	public static void main(String[] args) throws Exception {
		System.out.println(URLEncoder.encode("http://iframe.ly/api/oembed?url=https%3A%2F%2Fwww.facebook.com%2Fastateoftrance%2Fvideos%2F10154408836383653&api_key=224b99359b18f6ad450a2f", "UTF-8"));
	}
}
