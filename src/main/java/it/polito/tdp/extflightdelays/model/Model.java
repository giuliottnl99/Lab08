package it.polito.tdp.extflightdelays.model;
import java.awt.List;
import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.sun.javafx.geom.Edge;

import it.polito.tdp.extflightdelays.*;
import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	
	Graph<Airport, DefaultWeightedEdge> grafo;
	ArrayList<Airline> linee = new ArrayList<Airline>();
	ArrayList<Airport> airports = new ArrayList<Airport>();
	ArrayList<Flight> flights = new ArrayList<Flight>();
	
	
	
	//SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> mioGrafo;
	public Model() {
		
	}
	
	
	public Model(int valore) {
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.interrogaDatabase(valore);
		/*
		 * Il costruttore crea e pulisce il grafo alla chiamata del new, i metodi si usano solo per prendere
		 */
	}
	
	/*
	 * Funzioni getter per trovare:
	 * numero di vertici
	 * numero di archi
	 * elenco archi con distanza
	 * 
	 * serve la stringa descrivit
	 */
	
	public int getnumeroVertici() {
		
		return this.grafo.vertexSet().size();
		
	}
	
	public int getNumeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public String getAllFlights() {
		String risultato="";
		for(Flight f: flights) {
			if(grafo.getAllEdges(this.cercaAereoporto(f.getOriginAirportId()), this.cercaAereoporto(f.getDestinationAirportId())).size()==0) {
				continue;
			}
			risultato+=f.toString()+"\n";
			
		}
		return risultato;
	}
	
	
	
	public Airport cercaAereoporto(int airportId) {
		for(Airport a: airports) {
			if(a.getId()==airportId)
				return a;
		}
		return null;
	}
	
	
	private void pulisciGrafo(int distanzaMinima){
		for(Airport aPart: airports) {
			for(Airport aArr: airports) {
				double somma=0;
				double count=0;
				for(DefaultWeightedEdge thisEdge: grafo.getAllEdges(aPart, aArr)) {
					somma+= grafo.getEdgeWeight(thisEdge);
					count++;
				}
				
				double media=somma/count;
				if (media<distanzaMinima) {
					grafo.removeAllEdges(aPart, aArr);
				}
				
			}
			
		}
		System.out.println("Pulizia finita");
		 
	}
	
	
	
	
	private void interrogaDatabase(int valore) {
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		this.linee = (ArrayList<Airline>) dao.loadAllAirlines();
		this.airports = (ArrayList<Airport>) dao.loadAllAirports();
		this.flights = (ArrayList<Flight>) dao.loadAllFlights();
		
		//Ora che li bho caricati tutti, voglio verificare la distanza minima dell'aereoporto.
		// Elimino le linee che non rispettano la distanza minima
		System.out.println("Database interrogati");
		Graphs.addAllVertices(grafo, airports);
		System.out.println("Vertici aggiunti");
		for(Flight f : flights) {		
			Graphs.addEdgeWithVertices(grafo, this.cercaAereoporto(f.getOriginAirportId()), this.cercaAereoporto(f.getDestinationAirportId()), f.getDistance());
		}
		System.out.println("Percorsi aggiunti");
		this.pulisciGrafo(valore);
		
	}
		
		
		
		
	
	
	
	

}
