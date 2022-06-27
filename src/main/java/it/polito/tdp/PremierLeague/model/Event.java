package it.polito.tdp.PremierLeague.model;

public class Event implements Comparable<Event>{

	public enum EventType{
		GOAL,
		ESPULSIONE,
		INFORTUNIO
	}
	
	private EventType tipo;
	private int azione;
	public Event(EventType tipo, int azione) {
		super();
		this.tipo = tipo;
		this.azione = azione;
	}
	public EventType getTipo() {
		return tipo;
	}
	public void setTipo(EventType tipo) {
		this.tipo = tipo;
	}
	public int getAzione() {
		return azione;
	}
	public void setAzione(int azione) {
		this.azione = azione;
	}
	@Override
	public int compareTo(Event o) {
		return this.azione-o.azione;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + azione;
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
		Event other = (Event) obj;
		if (azione != other.azione)
			return false;
		return true;
	}
	
	
}
