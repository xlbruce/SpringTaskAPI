package model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Task implements JsonModel {

	private final long id;
	private String nome;
	private String descricao;
	private boolean concluida;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date criacao;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modificada;

	public Task() {
		this("", "");
	}

	public Task(String name) {
		this(name, "");
	}

	public Task(String name, String description) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
		Date now = c.getTime();
		this.id = now.getTime();
		this.nome = name;
		this.descricao = description;
		this.concluida = false;
		this.criacao = now;
		this.modificada = now;
	}

	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String description) {
		this.descricao = description;
	}

	public boolean isConcluida() {
		return concluida;
	}
	
	public void setConcluida() {
		this.concluida = true;
	}

	public Date getCriacao() {
		return this.criacao;
	}

	public Date getModificada() {
		return this.modificada;
	}
	
	public void setModificada(Date date) {
		this.modificada = date;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Task)) {
			return false;
		}
		if (((Task) obj).getId() == this.id
				|| ((Task) obj).getNome().equals(this.nome)) {
			return true;
		}
		return false;
	}
}
