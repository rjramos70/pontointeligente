package br.com.meudominio.pontointeligente.api.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import br.com.meudominio.pontointeligente.api.entities.Funcionario;
import br.com.meudominio.pontointeligente.api.enums.PerfilEnum;


public class JwtUserFactory {
	
	public JwtUserFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Converte e gera um JwtUser com base nos dados de um funcionario.
	 * 
	 * @param usuario
	 * @return JwtUser
	 */
	public static JwtUser create(Funcionario usuario) {
		return new JwtUser(usuario.getId(), usuario.getEmail(), usuario.getSenha(), mapToGrantedAuthorities(usuario.getPerfil()));
	}

	
	/**
	 * Converte o perfil do usuario para o formato utilizado pelo Spring Security
	 * 
	 * @param perfilEnum
	 * @return List<GrantedAuthority>
	 */
	private static Collection<? extends GrantedAuthority> mapToGrantedAuthorities(PerfilEnum perfilEnum) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		authorities.add(new SimpleGrantedAuthority(perfilEnum.toString()));
		
		return authorities;
	}

}
