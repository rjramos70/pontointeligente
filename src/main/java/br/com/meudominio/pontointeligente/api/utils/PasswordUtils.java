package br.com.meudominio.pontointeligente.api.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
	
	private static final Logger log = LoggerFactory.getLogger(PasswordUtils.class);
	private static BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
	
	public PasswordUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static String gerarBCrypt(String senha) {
		if(senha == null) {
			return senha;
		}
		
		log.info("Gerando hash com o BCrypt.");
		return bCryptEncoder.encode(senha); 
	}

}
