package br.com.meudominio.pontointeligente.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SenhaUtils {
	static BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
	
	public static String geraBCrypt(String senha) {
		if(senha == null) {
			return senha;
		}
		return bCryptEncoder.encode(senha);
	}
	
	public static boolean senhaValida(String senha, String senhaEcoded) {
		return bCryptEncoder.matches(senha, senhaEcoded);
	}

}
