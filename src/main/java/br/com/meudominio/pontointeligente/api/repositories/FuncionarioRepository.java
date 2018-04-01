package br.com.meudominio.pontointeligente.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.meudominio.pontointeligente.api.entities.Funcionario;

@Transactional( readOnly = true)	// Essa interface inteira e so de leitura ao banco, por ser só uma transação de consulta, o mesmo nao trava o banco.
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

	Funcionario findByCpf(String cpf);
	Funcionario findByEmail(String email);
	Funcionario findByCpfOrEmail(String cpf, String email);
}
