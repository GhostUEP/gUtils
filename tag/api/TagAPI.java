package me.ghost.tag.api;

public class TagAPI {

	private final String tagName;
	private final String cor;
	private final String corNick;
	private final String rank;
	private final String tab;
	private final Integer prioridade;

	public TagAPI(final String tagName, final String cor, final String corNick, final String rank, final String tab,
			final Integer prioridade) {
		this.tagName = tagName;
		this.cor = cor;
		this.corNick = corNick;
		this.rank = rank;
		this.tab = tab;
		this.prioridade = prioridade;
	}

	public String getNome() {
		return this.tagName;
	}

	public String getCor() {
		return this.cor;
	}

	public String getCorNick() {
		return this.corNick;
	}

	public String getRank() {
		return this.rank;
	}

	public String getTab() {
		return this.tab;
	}

	public Integer getPrioridade() {
		return this.prioridade;
	}

}
