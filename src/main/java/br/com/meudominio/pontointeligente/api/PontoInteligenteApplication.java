package br.com.meudominio.pontointeligente.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.meudominio.pontointeligente.security.SenhaUtils;

@SpringBootApplication
public class PontoInteligenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(PontoInteligenteApplication.class, args);
		
		String senha = "123456";
		
		System.out.println("Senha : ".concat(senha));
		
		String senhaEncoded = SenhaUtils.geraBCrypt(senha);
		
		System.out.println("Senha encoded : ".concat(senhaEncoded));
		
		System.out.println("Senha encoded : " + SenhaUtils.senhaValida(senha, senhaEncoded));
		
	}
}
