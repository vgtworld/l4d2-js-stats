package pl.vgtworld.l4d2jsstats.match;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class MatchDao {
	
	@PersistenceContext
	private EntityManager em;
	
	public void add(Match match) {
		em.persist(match);
	}
	
	public Match findById(int matchId) {
		return em.find(Match.class, matchId);
	}
	
	@SuppressWarnings("unchecked")
	public Match[] findRecentMatches(int count, int offset) {
		Query query = em.createNamedQuery(Match.QUERY_FIND_RECENT);
		query.setMaxResults(count);
		query.setFirstResult(offset);
		List<Match> result = query.getResultList();
		return result.toArray(new Match[result.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public Match[] findRecentMatchesFromMap(int mapId, int count) {
		Query query = em.createNamedQuery(Match.QUERY_FIND_RECENT_FROM_MAP);
		query.setParameter("mapId", mapId);
		query.setMaxResults(count);
		List<Match> result = query.getResultList();
		return result.toArray(new Match[result.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public Match[] findRecentMatchesForUser(int userId, int count) {
		Query query = em.createNamedQuery(Match.QUERY_FIND_RECENT_FOR_USER);
		query.setParameter("userId", userId);
		query.setMaxResults(count);
		List<Match> result = query.getResultList();
		return result.toArray(new Match[result.size()]);
	}
	
	public long getTotalMatchesPlayedOnMap(int mapId, int matchTypeId) {
		Query query = em.createNamedQuery(Match.QUERY_TOTAL_MATCHES_PLAYED_ON_MAP);
		query.setParameter("mapId", mapId);
		query.setParameter("matchTypeId", matchTypeId);
		return (long) (query.getSingleResult());
	}
	
}
