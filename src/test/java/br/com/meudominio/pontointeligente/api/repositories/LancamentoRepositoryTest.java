package br.com.meudominio.pontointeligente.api.repositories;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.meudominio.pontointeligente.api.entities.Empresa;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	private EmpresaRepository empresaRepository;
	
	@Before
	public void setUp() throws Exception {
		
		// Empresa empresa = this.empresaRepository.save(obterDadosEmpresa());
		
	}

	
	
	private Iterable obterDadosEmpresa() {
		// TODO Auto-generated method stub
		return null;
	}

}
