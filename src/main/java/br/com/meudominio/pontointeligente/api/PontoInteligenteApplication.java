package br.com.meudominio.pontointeligente.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import br.com.meudominio.pontointeligente.api.security.SenhaUtils;

@SpringBootApplication
@EnableCaching
public class PontoInteligenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(PontoInteligenteApplication.class, args);
		
		/*
		 
		Teste da classe geradora de cryptografia
		
		String senha = "123456";
		
		System.out.println("Senha : ".concat(senha));
		
		String senhaEncoded = SenhaUtils.geraBCrypt(senha);
		
		System.out.println("Senha encoded : ".concat(senhaEncoded));
		
		System.out.println("Senha encoded : " + SenhaUtils.senhaValida(senha, senhaEncoded));
		
		*/
		
	}
}
