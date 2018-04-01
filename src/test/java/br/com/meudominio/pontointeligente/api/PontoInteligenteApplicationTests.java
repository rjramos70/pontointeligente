package br.com.meudominio.pontointeligente.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")	// Dizemos ao Spring para criar um profile de nome 'test' e carregara os dados do arquivo 'application-test.properties'
public class PontoInteligenteApplicationTests {

	@Test
	public void contextLoads() {
	}

}
