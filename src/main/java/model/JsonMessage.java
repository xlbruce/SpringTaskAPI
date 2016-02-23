package model;

public class JsonMessage implements JsonModel {
	private final short code;
	private final String content;
	
	public JsonMessage (short code, String content) {
		this.code = code;
		this.content = content;
	}
	
	public short getCode() {
		return code;
	}
	
	public String getContent() {
		return content;
	}
}
