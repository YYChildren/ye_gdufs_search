package com.ye.gdufs.model;

public final class WordUrlPosPk implements java.io.Serializable{
	private static final long serialVersionUID = 2589445669959914642L;
	private String word;
	private String urlMd5;
	public WordUrlPosPk(){}
	public WordUrlPosPk(String word, String urlMd5) {
		this.word = word;
		this.urlMd5 = urlMd5;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getUrlMd5() {
		return urlMd5;
	}
	public void setUrlMd5(String urlMd5) {
		this.urlMd5 = urlMd5;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((urlMd5 == null) ? 0 : urlMd5.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordUrlPosPk other = (WordUrlPosPk) obj;
		if (urlMd5 == null) {
			if (other.urlMd5 != null)
				return false;
		} else if (!urlMd5.equals(other.urlMd5))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
}
