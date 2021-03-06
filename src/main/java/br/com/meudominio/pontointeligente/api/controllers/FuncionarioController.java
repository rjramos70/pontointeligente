package br.com.meudominio.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.meudominio.pontointeligente.api.dtos.FuncionarioDto;
import br.com.meudominio.pontointeligente.api.entities.Funcionario;
import br.com.meudominio.pontointeligente.api.response.Response;
import br.com.meudominio.pontointeligente.api.services.FuncionarioService;
import br.com.meudominio.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

	private static final Logger log = LoggerFactory.getLogger(FuncionarioController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	public FuncionarioController() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Atualiza os dados de um funcionário.
	 * 
	 * @param id
	 * @param funcionarioDto
	 * @param result
	 * @return ResponseEntity<Response<FuncionarioDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") Long id, 
			@Valid @RequestBody FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException{
		
		// @Valid --> valida os dados de entrada
		
		log.info("Atualizando funcionário : {}", funcionarioDto.toString());
		Response<FuncionarioDto> response = new Response<FuncionarioDto>();
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
		// So devemos atualizar o funcionario existente na base de dados 
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionário não encontrado."));
		}
		
		// Quando Funcionario é opcional, a maneira de se obter o objeto é usando o método .get() (funcionario.get())
		this.atualizarDadosFuncionario(funcionario.get(), funcionarioDto, result);
		
		// Verifico se existe algum erro de validacao, caso haja, imprime na tela a mensagem de erro
		// retornando o status de erro 400 (BadRequest)
		if (result.hasErrors()) {
			log.error("Erro validando funcionário : {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);	// retorna status 400 de badRequest.
		}
		
		// Caso todos os dados sejam validos
		// persisto o funcionario atualizado.
		this.funcionarioService.persistir(funcionario.get());		// Por funcionario ser um Optional, e preciso chamar o metodo .get() da classe.
		response.setData(this.converterFuncionarioDto(funcionario.get()));
		
		return ResponseEntity.ok(response);		// retorna status 200 de sucesso.
		
	}

	/**
	 * Atualiza os dados do funcionário com base nos dados encontrados no DTO.
	 * 
	 * @param funcionario
	 * @param funcionarioDto
	 * @param result
	 * @throws NoSuchAlgorithmException
	 */
	private void atualizarDadosFuncionario(Funcionario funcionario, FuncionarioDto funcionarioDto,
			BindingResult result) throws NoSuchAlgorithmException {

		funcionario.setNome(funcionarioDto.getNome());
		
		if (!funcionario.getEmail().equals(funcionarioDto.getEmail())) {
			// Funcionario pode mudar o email contanto que seja um email nao existente na base de dados.
			this.funcionarioService.buscarPorEmail(funcionarioDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("email", "Email já existente.")));
			funcionario.setEmail(funcionarioDto.getEmail());
		}
		
		funcionario.setQtdHorasAlmoco(null);
		funcionarioDto.getQtdHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		
		funcionario.setQtdHorasTrabalhoDia(null);
		funcionarioDto.getQtdHorasTrabalhoDia()
				.ifPresent(qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
		
		funcionario.setValorHora(null);
		funcionarioDto.getValorHora()
				.ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		if (funcionarioDto.getSenha().isPresent()) {
			funcionario.setSenha(PasswordUtils.gerarBCrypt(funcionarioDto.getSenha().get()));
		}
	}
	
	/**
	 * Retorna um DTO com os dados de um funcionário.
	 * 
	 * @param funcionario
	 * @return FuncionarioDto
	 */
	private FuncionarioDto converterFuncionarioDto(Funcionario funcionario) {
		FuncionarioDto funcionarioDto = new FuncionarioDto();
		
		funcionarioDto.setId(funcionario.getId());
		funcionarioDto.setEmail(funcionario.getEmail());
		funcionarioDto.setNome(funcionario.getNome());
		funcionario.getQtdHorasAlmocoOpt()
				.ifPresent(qtdHorasAlmoco -> funcionarioDto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt()
				.ifPresent(qtdHorasTrabDia -> funcionarioDto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		funcionario.getValorHoraOpt()
				.ifPresent(valorHora -> funcionarioDto.setValorHora(Optional.of(valorHora.toString())));

		return funcionarioDto;
	}

}
