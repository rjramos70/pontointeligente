package br.com.meudominio.pontointeligente.api.security.dto;

public class TokenDto {
	
	private String token;
	
	public TokenDto() {
		// TODO Auto-generated constructor stub
	}
	
	public TokenDto(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
}
