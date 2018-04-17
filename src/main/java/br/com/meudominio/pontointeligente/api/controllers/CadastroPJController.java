package br.com.meudominio.pontointeligente.api.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.meudominio.pontointeligente.api.dtos.CadastroPJDto;
import br.com.meudominio.pontointeligente.api.entities.Empresa;
import br.com.meudominio.pontointeligente.api.entities.Funcionario;
import br.com.meudominio.pontointeligente.api.enums.PerfilEnum;
import br.com.meudominio.pontointeligente.api.response.Response;
import br.com.meudominio.pontointeligente.api.services.EmpresaService;
import br.com.meudominio.pontointeligente.api.services.FuncionarioService;
import br.com.meudominio.pontointeligente.api.utils.PasswordUtils;

/**
 * Classe controller de cadastro de PJ
 * 
 * @author renatoramos
 *
 */
@RestController							// informa que e um end point
@RequestMapping("/api/cadastrar-pj")		// mapeamento da url (dominio/api/cadastrar-pj
@CrossOrigin(origins = "*")				// permite que as requisicoes venham de diferentes dominios, mas o correto seria aceitar requisicoes somente de URLs suas, de seu dominio, evitando assim que URLs externas acessem sua controller.
public class CadastroPJController {
	
	private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);
	
	// Como essa controller fara cadastro de Empresa e de Funcionario, precisamos injetar as duas respectivas classes.
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private EmpresaService empresaService;
	
	public CadastroPJController() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Cadastra uma Pessoa Juridica no sistema.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 * @return ResponseEntity<Response<CadastroPJDto>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping			// recebera uma requisicao to tipo POST.
	public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto,
			BindingResult result) throws NoSuchAlgorithmException{
		
		log.info("Cadastrando PJ: {}", cadastroPJDto.toString());
		Response<CadastroPJDto> response = new Response<CadastroPJDto>();
		
		validarDadosExistentes(cadastroPJDto, result);
		Empresa empresa = this.converteDtoParaEmpresa(cadastroPJDto);						// Extrai os dados do DTO e transforma em uma Empresa.
		Funcionario funcionario = this.converteDtoParaFuncionario(cadastroPJDto, result);	// Extrai os dados do DTO e transforma em um Funcionario.
		
		// Se possui erros de validacao.
		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
			// Pega todas as mensagens de erro e retorna ao cliente.
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			// Erro badRequest retorna um erro 400 http definindo o corpo 
			return ResponseEntity.badRequest().body(response);
		}
		
		// Primeiro persisti a empresa
		this.empresaService.persistir(empresa);
		// Uma vez persistida a empresa, setar o empresa dentro do funcionario
		funcionario.setEmpresa(empresa);
		// Depois persistir o funcionario.
		this.funcionarioService.persistir(funcionario);
		// Nesse momento ja foi persistido no banco Empresa e Funcionario.
		
		response.setData(this.converteCadastroPJDto(funcionario));
		// Retorna uma ResponseEntity com status 200 http
		return ResponseEntity.ok(response);
		
	}

	/**
	 * Verifica se a empresa ou funcionario ja existem na base de dados.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
			.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa ja existente.")));
		
		this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF ja existente.")));
		
		this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email ja existente.")));
		
	}


	/**
	 * Converte os dados do DTO para Empresa.
	 * 
	 * @param cadastroPJDto
	 * @return Empresa
	 */
	private Empresa converteDtoParaEmpresa(CadastroPJDto cadastroPJDto) {
		
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDto.getCnpj());
		empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());
		
		return empresa;
	}
	
	
	/**
	 * Converte os dados do DTO para Funcionario.
	 * 
	 * @param cadastroPJDto
	 * @param result
	 * @return Funcionario
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario converteDtoParaFuncionario(CadastroPJDto cadastroPJDto, BindingResult result) 
			throws NoSuchAlgorithmException{
		
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPJDto.getNome());
		funcionario.setEmail(cadastroPJDto.getEmail());
		funcionario.setCpf(cadastroPJDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);	// Dono da Empresa
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPJDto.getSenha()));
		
		return funcionario;
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionario e empresa.
	 * 
	 * @param funcionario
	 * @return CadastroPJDto
	 */
	private CadastroPJDto converteCadastroPJDto(Funcionario funcionario) {
		
		CadastroPJDto cadastroPjDto = new CadastroPJDto();
		cadastroPjDto.setId(funcionario.getId());
		cadastroPjDto.setNome(funcionario.getNome());
		cadastroPjDto.setEmail(funcionario.getEmail());
		cadastroPjDto.setCpf(funcionario.getCpf());
		cadastroPjDto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		cadastroPjDto.setCnpj(funcionario.getEmpresa().getCnpj());
		
		return cadastroPjDto;
	}
	
}
