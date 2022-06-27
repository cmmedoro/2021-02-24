package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getPlayerMatch(Match m, Map<Integer, Player> idMap){
		String sql = "SELECT a.PlayerID, p.Name "
				+ "FROM actions a, players p "
				+ "WHERE a.MatchID = ? AND p.PlayerID = a.PlayerID";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Player player = new Player(res.getInt("a.PlayerID"), res.getString("p.Name"));
				result.add(player);
				idMap.put(res.getInt("a.PlayerID"), player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getArchi(Match m, Map<Integer, Player> idMap){
		String sql = "SELECT a1.PlayerID, (a1.TotalSuccessfulPassesAll+a1.Assists)/a1.TimePlayed AS peso1, a2.PlayerID, (a2.TotalSuccessfulPassesAll+a2.Assists)/a2.TimePlayed AS peso2\r\n"
				+ "FROM actions a1, actions a2, matches m, players p1, players p2\r\n"
				+ "WHERE m.MatchID = ? AND a1.MatchID = m.MatchID AND a2.MatchID = m.MatchID AND (a1.TeamID = m.TeamHomeID AND a2.TeamID = m.TeamAwayID) AND p1.PlayerID = a1.PlayerID AND p2.PlayerID = a2.PlayerID";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Player p1 = idMap.get(res.getInt("a1.PlayerID"));
				Player p2 = idMap.get(res.getInt("a2.PlayerID"));
				double efficienzaP1 = res.getDouble("peso1");
				double efficienzaP2 = res.getDouble("peso2");
				if(p1 != null && p2 != null) {
					double diff = efficienzaP1 - efficienzaP2;
					result.add(new Adiacenza(p1, p2, diff));
				}
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getTwoTeams(Match m){
		String sql = "SELECT t1.Name, t2.Name "
				+ "FROM matches m, teams t1, teams t2 "
				+ "WHERE m.MatchID =? AND t1.TeamID = m.TeamHomeID AND t2.TeamID = m.TeamAwayID";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, m.getMatchID());
			ResultSet res = st.executeQuery();
			if(res.first()) {
				result.add(res.getString("t1.Name"));
				result.add(res.getString("t2.Name"));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getSquadraGiocatore(Player p) {
		String sql = "SELECT  DISTINCT t.Name "
				+ "FROM teams t, players p, actions m "
				+ "WHERE p.PlayerID = m.PlayerID AND m.TeamID = t.TeamID AND p.PlayerID = ?";
		String result = null;
		Connection conn = DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, p.getPlayerID());
			ResultSet res = st.executeQuery();
			if(res.first()) {
				result = res.getString("t.Name");
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
