package br.com.meudominio.pontointeligente.api.security.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.meudominio.pontointeligente.api.entities.Funcionario;
import br.com.meudominio.pontointeligente.api.security.JwtUserFactory;
import br.com.meudominio.pontointeligente.api.security.entities.Usuario;
import br.com.meudominio.pontointeligente.api.security.services.UsuarioService;
import br.com.meudominio.pontointeligente.api.services.FuncionarioService;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	
	@Autowired
	private UsuarioService usuarioService;
	
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Optional<Usuario> funcionario2 = this.usuarioService.buscarPorEmail(username);
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorEmail(username);
		
		
		if (funcionario.isPresent()) {
			return JwtUserFactory.create(funcionario.get());
		}
		
		throw new UsernameNotFoundException("Email nao encontrado");
	}

}
