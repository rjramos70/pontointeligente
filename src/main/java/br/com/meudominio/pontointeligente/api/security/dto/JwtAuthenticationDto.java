package br.com.meudominio.pontointeligente.api.security.dto;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class JwtAuthenticationDto {
	
	private String email;
	private String senha;
	
	public JwtAuthenticationDto() {
		// TODO Auto-generated constructor stub
	}

	@NotEmpty( message = "Email nao pode ser vazio." )
	@Email( message = "Email invalido!")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotEmpty( message = "Email nao pode ser vazio." )
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public String toString() {
		return "JwtAuthenticationRequestDto [email=" + email + ", senha=" + senha + "]";
	}
	
	

}
