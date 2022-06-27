package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private List<Match> allMatches;
	private List<Player> vertex;
	private Map<Integer, Player> idMap;
	private double deltaMax;
	//parametri simulazione
	private Simulator sim;
	private List<Integer> goal;
	private List<Integer> espulsi;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.allMatches = new ArrayList<>();
		this.allMatches = this.dao.listAllMatches();
	}
	
	public List<Match> getMatches(){
		return this.allMatches;
	}
	
	public void creaGrafo(Match m) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		//Aggiungo i vertici
		this.idMap = new HashMap<>();
		this.vertex = new ArrayList<>(this.dao.getPlayerMatch(m, idMap));
		Graphs.addAllVertices(this.grafo, this.vertex);
		//aggiungo gli archi
		List<Adiacenza> edges = new ArrayList<>(this.dao.getArchi(m, idMap));
		for(Adiacenza aa : edges) {
			Player p1 = aa.getP1();
			Player p2 = aa.getP2();
			double peso = aa.getPeso();
			if(peso >= 0.0) {
				//vuol dire che p1 ha maggiore efficienza di p2
				Graphs.addEdgeWithVertices(this.grafo, p1, p2, peso);
			}else if(peso < 0.0) {
				//p2 ha efficienza maggiore di p1
				Graphs.addEdgeWithVertices(this.grafo, p2, p1, (-1)*peso);
			}
		}
	}
	public boolean isGraphCreated() {
		if(this.grafo == null) {
			return false;
		}
		return true;
	}
	public int nVertices() {
		return this.grafo.vertexSet().size();
	}
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	public List<Player> getVertici(){
		return this.vertex;
	}
	public List<String> getSquadreMatch(Match m){
		List<String> squadre = new ArrayList<>(this.dao.getTwoTeams(m));
		return squadre;
	}
	public String getSquadraGiocatore(Player p) {
		return this.dao.getSquadraGiocatore(p);
	}
	
	public Player getGiocatoreMigliore() {
		Player migliore = null;
		double differenza;
		double sommaUscenti;
		double sommaEntranti;
		deltaMax = 0.0;
		for(Player p : this.grafo.vertexSet()) {
			differenza = 0.0;
			sommaUscenti = 0.0;
			sommaEntranti = 0.0;
			for(DefaultWeightedEdge e : this.grafo.outgoingEdgesOf(p)) {
				sommaUscenti += this.grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e : this.grafo.incomingEdgesOf(p)) {
				sommaEntranti += this.grafo.getEdgeWeight(e);
			}
			differenza = sommaUscenti - sommaEntranti;
			if(differenza > deltaMax) {
				deltaMax = differenza;
				migliore = p;
			}
		}
		return migliore;
	}
	public double deltaMax() {
		return this.deltaMax;
	}
	
	public void simulazione(int N, Match m) {
		this.sim = new Simulator(N, this, m);
		this.sim.init();
		this.sim.run();
		this.espulsi = new ArrayList<>(this.sim.getEspulsi());
		this.goal = new ArrayList<>(this.sim.getGoals());
	}

	public List<Integer> getGoal() {
		return goal;
	}

	public List<Integer> getEspulsi() {
		return espulsi;
	}
	
}
