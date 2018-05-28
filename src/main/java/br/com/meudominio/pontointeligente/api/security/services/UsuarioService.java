package br.com.meudominio.pontointeligente.api.security.services;

import java.util.Optional;

import br.com.meudominio.pontointeligente.api.security.entities.Usuario;

public interface UsuarioService {
	
	/**
	 * Busca e retorna um usuario dado um email
	 * 
	 * @param email
	 * @return Optional<Usuario>
	 */
	Optional<Usuario> buscarPorEmail(String email);

}
