package br.com.meudominio.pontointeligente.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.meudominio.pontointeligente.api.entities.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
	
	@Transactional(readOnly = true)	// Esse método foi anotado como Transactional, pois ao fazer uma consulta ao banco, por ser só uma transação de consulta, o mesmo nao trava o banco.
	Empresa findByCnpj(String cnpj);

}
