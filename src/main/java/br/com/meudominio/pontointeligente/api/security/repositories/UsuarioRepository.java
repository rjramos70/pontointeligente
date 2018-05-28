package br.com.meudominio.pontointeligente.api.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.meudominio.pontointeligente.api.security.entities.Usuario;

// Por ser um repositorio de somente leitura, utiliza a anotacao de@Transactional, assim o acesso e mais rapido,
// uma vez que bao existira a necessidade de nenhum tipo de lock no banco.
@Transactional(readOnly = true)
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Usuario findByEmail(String email);
	
}
