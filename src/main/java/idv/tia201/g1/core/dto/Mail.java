package idv.tia201.g1.core.dto;

import lombok.Data;

@Data
public class Mail {
	private String recipient;
	private String subject;
	private String text;
}
