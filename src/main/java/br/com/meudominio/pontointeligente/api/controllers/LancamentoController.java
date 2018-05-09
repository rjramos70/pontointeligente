package br.com.meudominio.pontointeligente.api.controllers;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.meudominio.pontointeligente.api.dtos.LancamentoDto;
import br.com.meudominio.pontointeligente.api.entities.Lancamento;
import br.com.meudominio.pontointeligente.api.response.Response;
import br.com.meudominio.pontointeligente.api.services.FuncionarioService;
import br.com.meudominio.pontointeligente.api.services.LancamentoService;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {
	
	// cria o log
	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	// cria o formatador
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

	
	// FALTA COMPLEMENTAR O RESTPO DO CODIGO !!!
	
	
	
	
	
	
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
	
	
}
