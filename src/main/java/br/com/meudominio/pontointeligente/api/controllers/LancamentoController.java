package br.com.meudominio.pontointeligente.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.meudominio.pontointeligente.api.dtos.LancamentoDto;
import br.com.meudominio.pontointeligente.api.entities.Funcionario;
import br.com.meudominio.pontointeligente.api.entities.Lancamento;
import br.com.meudominio.pontointeligente.api.enums.TipoEnum;
import br.com.meudominio.pontointeligente.api.response.Response;
import br.com.meudominio.pontointeligente.api.services.FuncionarioService;
import br.com.meudominio.pontointeligente.api.services.LancamentoService;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {
	
	// cria o log
	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	// cria o formatador de datas (formato Americano que o MySQL usa)
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	// Injeta as dependencias
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	// Carrega parametros do arquivo "application.properties"
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;
	
	public LancamentoController() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Retorna a listagem de lancamentos de um funcionario.
	 * 
	 * @param funcionarioId
	 * @param pag
	 * @param ord
	 * @param dir
	 * @return ResponseEntity<Response<Page<LancamentoDto>>>
	 */
	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir){
		
		// Lanca log
		log.info("Buscando lancamentos pot ID do funcionario: {}", funcionarioId, pag);
		
		// Cria resposta
		Response<Page<LancamentoDto>> response = new Response<Page<LancamentoDto>>();
		
		// Cria pagina de resposta
		PageRequest pageRequest = new PageRequest(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		
		// Cria pagina de lancamentos
		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
		
		// Cria lancamentoDto
		Page<LancamentoDto> lancamentosDto = lancamentos.map(lancamento -> this.converteLancamentoDto(lancamento));
		
		response.setData(lancamentosDto);
		return ResponseEntity.ok(response);
		
	}

	/**
	 * Retorna um lancamento por ID
	 * 
	 * @param id
	 * @return ResponseEntity<Response<LancamentoDto>>
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id){
		log.info("Buscando lancamento por ID: {}", id);
		
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		// Se nao for encontrado o lancamento
		if (!lancamento.isPresent()) {
			log.info("Lancamento nao encontrado para o ID: {}", id);
			response.getErrors().add("Lancamento nao encontrado para o ID: {}" + id);
			return ResponseEntity.badRequest().body(response);
		}
		
		// Se lancamento existir na base de dados
		response.setData(this.converteLancamentoDto(lancamento.get()));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Adiciona um nov lancamento
	 * 
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws ParseException
	 */
	// como nao foi definido uma URL, sera usado a API /api/lancamentos
	@PostMapping
	public ResponseEntity<Response<LancamentoDto>> adicionar(@Valid @RequestBody LancamentoDto lancamentoDto,
			BindingResult result) throws ParseException{
		
		log.info("Adicionando lancamento: {}", lancamentoDto.toString());
		
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		
		// Valida se funcionario existe na base de dados
		validarFuncionario(lancamentoDto, result);
		
		// Converte o LancamentoDto para Lancamento
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
		
		// Se houver algum erro de validacao.
		if (result.hasErrors()) {
			log.error("Erro validando lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);	// retorna um erro 400 - BadRequest
		}
		
		// Caso nao tenha erro, salva o lancamento retornando um lancamento
		lancamento = this.lancamentoService.persistir(lancamento);
		// Converte o lancamento para um LancamentoDto e inseri no response
		response.setData(this.converteLancamentoDto(lancamento));
		// Retorna um status 200 - sucess
		return ResponseEntity.ok(response);
	}
	
	
	
	/**
	 * Atualiza os dados de um lancamento
	 * 
	 * @param id
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDto>>
	 * @throws ParseException
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDto>> atualizar(@PathVariable("id") long id,
			@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException{
		
		log.info("Atualizando lancamento: {}", lancamentoDto.toString());
		
		Response<LancamentoDto> response = new Response<LancamentoDto>();
		
		validarFuncionario(lancamentoDto, result);
		
		lancamentoDto.setId(Optional.of(id));
		
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDto, result);
		
		if (result.hasErrors()) {
			log.error("Erro validando lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converteLancamentoDto(lancamento));
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Remove um lancamento por ID.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<String>>
	 */
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id){
		
		log.info("Removendo lancamento id: {}", id);
		Response<String> response = new Response<String>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if (!lancamento.isPresent()) {
			log.info("Erro ao remover devido ao lancamento ID: {} ser invalido.", id);
			response.getErrors().add("Erro ao remover lancamento. Registro nao encontrado para o ID " + id);
			return ResponseEntity.badRequest().body(response);	// Retorna erro 400
		}
		
		this.lancamentoService.remover(id);
		return ResponseEntity.ok(new Response<String>());		// Retorna status 200 - sucess
	}
	
	
	

	/**
	 * Valida um funcionario, verificando se ele e existente e valido no sistema
	 * 
	 * @param lancamentoDto
	 * @param result
	 */
	private void validarFuncionario(LancamentoDto lancamentoDto, BindingResult result) {
		
		if (lancamentoDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "Funcionario nao informado."));
			return;
		}
		
		log.info("Validando funcionario de ID {}:", lancamentoDto.getFuncionarioId());
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
		
		// Se nao for identificado funcionario com o respectivo ID
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario nao encontrado. ID inexistente."));
		}
		
		
	}

	/**
	 * Converte uma entidade lancamento para seu respectivo DTO.
	 * 
	 * @param lancamento
	 * @return LancamentoDto
	 */
	private LancamentoDto converteLancamentoDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		
		lancamentoDto.setId(Optional.of(lancamento.getId()));
		lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
		lancamentoDto.setTipo(lancamento.getTipo().toString());
		lancamentoDto.setDescricao(lancamento.getDescricao());
		lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());
		
		return lancamentoDto;
	}
	
	/**
	 * Converte um LancamentoDto para uma entidade Lancamento.
	 * 
	 * @param  lancamentoDto
	 * @param  result
	 * @return Lancamento
	 * @throws ParseException
	 */
	private Lancamento converterDtoParaLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		Lancamento lancamento = new Lancamento();

		if (lancamentoDto.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoDto.getId().get());
			if (lanc.isPresent()) {
				lancamento = lanc.get();
			} else {
				result.addError(new ObjectError("lancamento", "Lançamento não encontrado."));
			}
		} else {
			lancamento.setFuncionario(new Funcionario());
			lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
		}

		lancamento.setDescricao(lancamentoDto.getDescricao());
		lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
		lancamento.setData(this.dateFormat.parse(lancamentoDto.getData()));

		// EnumUtils e um pacote do Apache, e verifica os tipos de Enum na classe TipoEnum.java 
		// e verifica se o valor recebido lancamentoDto.getTipo() existe dentro da Enum TipoEnum.
		if (EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
		} else {
			result.addError(new ObjectError("tipo", "Tipo inválido."));
		}

		return lancamento;
	}
}
