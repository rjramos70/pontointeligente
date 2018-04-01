package br.com.meudominio.pontointeligente.api.repositories;

import java.util.List;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.meudominio.pontointeligente.api.entities.Lancamento;

@Transactional( readOnly = true)	// Essa interface inteira e so de leitura ao banco, por ser só uma transação de consulta, o mesmo nao trava o banco.
@NamedQueries({
	@NamedQuery( name = "LancamentoRepository.findByFuncionarioId",	// Esse sera o nome da query quando fomos chamar no Spring.
				query = "SELECT lanc FROM Lancamento lanc WHERE lanc.funcionario.id = :funcionarioId"	// Query montada em JPQl, SQL so JPA. 
			   )})
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

	List<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);	// Metdo retorna todos os funcionarios.
	
	Page<Lancamento> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId, Pageable pagable);	// Metdo retorna so resultados paginados, pode pedir do primeiro ao decimo elemento como exemplo.
}
